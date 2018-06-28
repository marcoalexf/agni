package com.example.marisco.myapplication;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OccurrenceDetails extends Fragment implements OnMapReadyCallback {

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String VISIBILITY = "visibility";
    private static final String LEVEL = "level";
    private static final String ID = "occurrence_id";
    private static final String TOKEN = "token";
    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";

    private GoogleMap mapG;
    private MarkerOptions mp;
    private double lat, lon;
    private String title;
    private Long occurrence_id ,userID;
    private Retrofit retrofit;
    private LoginResponse token;

    @BindView(R.id.detail_title)
    TextView o_title;
    @BindView(R.id.detail_description)
    TextView o_description;
    @BindView(R.id.detail_level)
    TextView o_level;
    @BindView(R.id.detail_visibility)
    TextView o_visibility;
    @BindView(R.id.occurrence_map)
    MapView map;
    @BindView(R.id.occurrence_image)
    ImageView image;
    @BindView(R.id.comment_btn)
    Button comment_btn;
    @BindView(R.id.new_comment_text)
    EditText comment_text;
    @BindView(R.id.comment_list)
    ListView comment_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.detail_occurrence, container, false);
        ButterKnife.bind(this, v);
        o_description.setText("teste descricao");
        Bundle b = this.getArguments();
        title = "";
        String description = "";
        lat = 0;
        lon = 0;
        boolean visibility = false;
        double level = 1;
        occurrence_id = 0l;
        userID = 0l;
        ArrayList<String> mediaIDs = null;
        if (b != null) {
            title = (String)b.getSerializable(TITLE);
            description = (String)b.getSerializable(DESCRIPTION);
            lat = (double)b.getSerializable(LATITUDE);
            lon = (double)b.getSerializable(LONGITUDE);
            visibility = (boolean)b.getSerializable(VISIBILITY);
            level = (double)b.getSerializable(LEVEL);
            occurrence_id = (Long) b.getSerializable(ID);
            userID = (Long) b.getSerializable("userID");
            mediaIDs = (ArrayList<String>) b.getSerializable("mediaIDs");
            this.token = (LoginResponse) b.getSerializable(TOKEN);
        }

        map.onCreate(savedInstanceState);
        map.onResume(); // needed to get the map to display immediately
        map.getMapAsync(this);
        fillInfo(title, description, visibility, level, occurrence_id, userID, mediaIDs);
        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newComment();
            }
        });
        return v;
    }

    private void fillInfo(String title, String description, boolean visibility, double level
    , Long occurrence_id, Long userID, ArrayList<String> mediaIDs){
        o_title.setText(title);
        o_description.setText(description);

        o_level.setText((int)level + "");

        if(visibility)
            o_visibility.setText(R.string.occurrence_public);
        else o_visibility.setText(R.string.occurrence_private);

        if(mediaIDs != null && !mediaIDs.isEmpty()) {
            Picasso.get().load("https://storage.googleapis.com/custom-tine-204615.appspot.com/user/"
                    + userID + "/occurrence/" + occurrence_id + "/" + mediaIDs.get(0)).rotate(90).into(image);
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.mapG = map;
        putMarker();
    }

    private void putMarker() {
        mapG.clear();
        mp = new MarkerOptions();
        mp.position(new LatLng(lat, lon));
        mp.title(title);
        mapG.addMarker(mp);
        mapG.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat, lon), 11));
    }

    private void newComment(){
        String text = comment_text.getText().toString();
        if(text == null || text.equals("")){
            Toast toast = Toast.makeText(getActivity(), "Texto do comentário inválido ", Toast.LENGTH_SHORT);
             toast.show();
        }else{
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ENDPOINT)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }

            AgniAPI agniAPI = retrofit.create(AgniAPI.class);

            Call<ResponseBody> call = agniAPI.postComment(new OccurrenceCommentData(token, userID, occurrence_id, text));

            call.enqueue(new Callback<ResponseBody>() {
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        Toast toast = Toast.makeText(getActivity(), "Comentário publicado", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        Toast toast = Toast.makeText(getActivity(), "Failed to post comment: " + response.code(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("ERROR", t.toString());
                }
            });
        }
    }
}
