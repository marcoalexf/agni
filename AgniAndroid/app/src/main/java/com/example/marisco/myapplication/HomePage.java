package com.example.marisco.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Serializable {

    String username, name, email, type, mCurrentPhotoPath;
    private LoginResponse token;
    private static final String TOKEN = "token";
    private static final String DOWNLOAD_ENDPOINT = "https://storage.googleapis.com/custom-tine-204615.appspot.com/user/";

    @BindView(R.id.rv) RecyclerView rv;
    @BindView(R.id.fab) FloatingActionButton fab;

    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callCamera();
            }
        });


        rv.hasFixedSize();
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);
        Log.d("LOGGING: ", "before all the retrofit bullshit");
        retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NewsAPI newsAPI = retrofit.create(NewsAPI.class);
        Map<String, String> options = new HashMap<>();
        options.put("country", "pt");
        options.put("q", "fogo");
        options.put("sortBy", "popularity");
        options.put("apiKey", "888b98dd2a90421c950ffd72830ee6f4");
        Log.d("LOGGING: ", "before Call<NewsDataCard>");
        Call<NewsDataCard> call = newsAPI.topHeadlines(options);
        call.enqueue(new Callback<NewsDataCard>() {
            public void onResponse(Call<NewsDataCard> call, Response<NewsDataCard> response) {
                if (response.code() == 200) {
                    Log.d("Got 200 ok", "at least we enter");

                    List<Article> r = response.body().getArticles();
                    Log.d("Num results: ", String.valueOf(r.size()));
                    RVAdapter adapter = new RVAdapter(r);
                    rv.setAdapter(adapter);
                    Log.d("SO I DID THE NEWS THING", "AND IT WAS AWESOME");
                }
                else {
                    Log.d("SO I DID THE NEWS THING", "AND IT FAILED MISERABLY");
                }
            }
            public void onFailure(Call<NewsDataCard> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent i = getIntent();
        LoginResponse response = (LoginResponse) i.getSerializableExtra(MainActivity.RESPONSE);
        NavigationView nv = findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(this);
        View header = nv.getHeaderView(0);
        TextView header_username = header.findViewById(R.id.header_username);
        header_username.setText(response.getUsername());

        this.username = response.getUsername();

        this.token = response;

        CircleImageView header_photo = header.findViewById(R.id.profile_avatar_drawer);
        String path = DOWNLOAD_ENDPOINT + token.getUserid() + "/photo";
        Picasso.get().load(path).into(header_photo);
    }



    public void callCamera(){
        //hasPermissionInManifest(this, "android.permission.CAMERA"); // android doesnt not guarentee that the permission is installed in install time... fuck this
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.marisco.myapplication",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);

                //Redirect to register occurence fragment
                //With file inside the bundle, as well as the token (duh)
                //Screen changes to register occurence fragment and now it has the photo

                OccurrenceFragment of = new OccurrenceFragment();
                FragmentManager fman = getSupportFragmentManager();
                Bundle args = new Bundle();
                args.putSerializable(TOKEN, token);

                args.putSerializable("PHOTO", photoFile);

                of.setArguments(args);
                fman.beginTransaction().replace(R.id.fragment, of).commit();
            }
        }

    }

    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_sign_out){
            Intent intent = new Intent(this, LogoutActivity.class);
            intent.putExtra(TOKEN, token);
            startActivity(intent);


            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            setTitle(R.string.profile);
            ProfileFragment profile = new ProfileFragment();
            FragmentManager fman = getSupportFragmentManager();

            Bundle args = new Bundle();
            args.putSerializable(TOKEN, token);
            profile.setArguments(args);

            fman.beginTransaction().replace(R.id.fragment, profile).commit();
        } else if (id == R.id.nav_occurencies) {
            setTitle(R.string.register_occurrence);

            OccurrenceFragment of = new OccurrenceFragment();
            FragmentManager fman = getSupportFragmentManager();
            Bundle args = new Bundle();
            args.putSerializable(TOKEN, token);

            of.setArguments(args);
            fman.beginTransaction().replace(R.id.fragment, of).commit();
        } else if (id == R.id.nav_map) {
            setTitle(R.string.map);

            MapFragment map = new MapFragment();
            FragmentManager fman = getSupportFragmentManager();
            Bundle args = new Bundle();
            args.putSerializable(TOKEN, token);

            map.setArguments(args);
            fman.beginTransaction().replace(R.id.fragment, map).commit();
        } else if (id == R.id.nav_statistics) {

        } else if (id == R.id.nav_view) {

        }
        else if(id == R.id.occurences_list){
            setTitle(R.string.list_occurrences);

            ListOccurrences lo = new ListOccurrences();
            FragmentManager fman = getSupportFragmentManager();
            Bundle args = new Bundle();
            args.putSerializable(TOKEN, token);

            lo.setArguments(args);
            fman.beginTransaction().replace(R.id.fragment, lo).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}