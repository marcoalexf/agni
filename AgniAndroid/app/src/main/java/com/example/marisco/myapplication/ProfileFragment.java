package com.example.marisco.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marisco.myapplication.constructors.CursorList;
import com.example.marisco.myapplication.constructors.EditProfileData;
import com.example.marisco.myapplication.constructors.ListOccurrenceData;
import com.example.marisco.myapplication.constructors.ListOccurrenceLikeData;
import com.example.marisco.myapplication.constructors.ProfileRequest;
import com.example.marisco.myapplication.constructors.ProfileResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String TOKEN = "token";
    private static final String MODE = "mode";
    private static final String LIKED_OCCURRENCES = "liked_occurrences";
    private static final String REGISTERED_OCCURRENCES = "registered_occurrences";
    public static final String RESPONSE = "com.example.marisco.myapplication.RESPONSE";
    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";
    private static final String DOWNLOAD_ENDPOINT = "https://storage.googleapis.com/custom-tine-204615.appspot.com/user/";
    private static final int PICK_IMAGE = 1;
    private String username, email, fullName, locality, county, district, cursor;
    private Retrofit retrofit;
    private LoginResponse token;
    private List<Map<String, Object>> list;
    private Bitmap photoImg;

    private static final int QUERY_LIMIT = 100;

    //@BindView(R.id.profile_edit_avatar) ImageView profile_edit_avatar;

    @BindView(R.id.profile_avatar)ImageView profile_img;
    @BindView(R.id.profile_username) EditText profile_username;
    @BindView(R.id.profile_role) EditText profile_type;
    @BindView(R.id.profile_name) EditText profile_name;
    @BindView(R.id.profile_email) EditText profile_email;
    @BindView(R.id.profile_locality) EditText profile_locality;
    @BindView(R.id.profile_county) EditText profile_county;
    @BindView(R.id.profile_district) EditText profile_district;
    @BindView(R.id.edit_button) FloatingActionButton edit_button;
    @BindView(R.id.edit_photo) ImageButton edit_photo;
    @BindView(R.id.btnSave) Button save_button;
    @BindView(R.id.btnCancelSave) Button cancel_button;
    @BindView(R.id.occurrences_img)ImageView occurrences_img;
    @BindView(R.id.liked_occurrences_img)ImageView liked_occurrences_img;
    @BindView(R.id.liked_occurrences_number)TextView liked_occurrences_number;
    @BindView(R.id.registered_occurrences_number)TextView registered_occurrences_number;
    @BindView(R.id.new_password) EditText new_password;
    @BindView(R.id.new_password_retype) EditText new_password_retype;
    @BindView(R.id.old_password) EditText old_password;

    public ProfileFragment() {
        list = new LinkedList<Map<String, Object>>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.modern_profile_fragment, container, false);
        ButterKnife.bind(this, v);
        Bundle b = this.getArguments();
        if(b != null){
            this.token = (LoginResponse) b.getSerializable(TOKEN);
        }

        getProfile();
        getLikedOccurrences();
        getRegisteredOccurrences();
        fieldsSetup();
        getProfileImage();

        edit_button.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                saveInitialValues();
                editProfile();
            }
        });
        save_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                fieldsSetup();
                changeProfile();
            }
        });

        cancel_button.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                fieldsSetup();
                restoreInitialValues();
            }
        });

        occurrences_img.setClickable(true);
        occurrences_img.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {

                ListOccurrences od = new ListOccurrences();
                FragmentManager fman = getFragmentManager();
                Bundle args = new Bundle();
                args.putSerializable(TOKEN, token);
                args.putSerializable(MODE, REGISTERED_OCCURRENCES);
                od.setArguments(args);
                fman.beginTransaction().replace(R.id.fragment, od).commit();
            }
        });

        liked_occurrences_img.setClickable(true);
        liked_occurrences_img.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                ListOccurrences od = new ListOccurrences();
                FragmentManager fman = getFragmentManager();
                Bundle args = new Bundle();
                args.putSerializable(TOKEN, token);
                args.putSerializable(MODE, LIKED_OCCURRENCES);
                od.setArguments(args);
                fman.beginTransaction().replace(R.id.fragment, od).commit();
            }
        });
        edit_photo.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Escolha a foto de perfil");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                Matrix matrix = new Matrix();

                //matrix.postRotate(90);

                photoImg = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                profile_img.setImageBitmap(photoImg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveInitialValues(){
        username = profile_username.getText().toString();
        fullName = profile_name.getText().toString();
        email = profile_email.getText().toString();
        locality = profile_locality.getText().toString();
        county = profile_county.getText().toString();
        district = profile_district.getText().toString();
    }

    private void restoreInitialValues(){
        profile_username.setText(username);
        profile_name.setText(fullName);
        profile_email.setText(email);
        profile_locality.setText(locality);
        profile_county.setText(county);
        profile_district.setText(district);
    }

    //Sets up all the EditText fields in this fragment to be un-editable.
    public void fieldsSetup(){
        profile_username.setInputType(InputType.TYPE_NULL);
        profile_username.setEnabled(false);
        profile_username.setTextIsSelectable(false);

        profile_type.setInputType(InputType.TYPE_NULL);
        profile_type.setEnabled(false);
        profile_type.setTextIsSelectable(false);

        profile_name.setInputType(InputType.TYPE_NULL);
        profile_name.setEnabled(false);
        profile_name.setTextIsSelectable(false);

        profile_county.setInputType(InputType.TYPE_NULL);
        profile_county.setEnabled(false);
        profile_county.setTextIsSelectable(false);

        profile_district.setInputType(InputType.TYPE_NULL);
        profile_district.setEnabled(false);
        profile_district.setTextIsSelectable(false);

        profile_email.setInputType(InputType.TYPE_NULL);
        profile_email.setEnabled(false);
        profile_email.setTextIsSelectable(false);

        profile_locality.setInputType(InputType.TYPE_NULL);
        profile_locality.setEnabled(false);
        profile_locality.setTextIsSelectable(false);

        save_button.setVisibility(View.GONE);
        cancel_button.setVisibility(View.GONE);
        edit_photo.setVisibility(View.GONE);
        new_password.setVisibility(View.GONE);
        new_password_retype.setVisibility(View.GONE);
        old_password.setVisibility(View.GONE);
    }

    public void editProfile(){
        profile_username.setInputType(InputType.TYPE_CLASS_TEXT);
        profile_username.setEnabled(true);
        profile_username.setTextIsSelectable(true);

        profile_name.setInputType(InputType.TYPE_CLASS_TEXT);
        profile_name.setEnabled(true);
        profile_name.setTextIsSelectable(true);

        profile_county.setInputType(InputType.TYPE_CLASS_TEXT);
        profile_county.setEnabled(true);
        profile_county.setTextIsSelectable(true);

        profile_district.setInputType(InputType.TYPE_CLASS_TEXT);
        profile_district.setEnabled(true);
        profile_district.setTextIsSelectable(true);

        profile_email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        profile_email.setEnabled(true);
        profile_email.setTextIsSelectable(true);

        profile_locality.setInputType(InputType.TYPE_CLASS_TEXT);
        profile_locality.setEnabled(true);
        profile_locality.setTextIsSelectable(true);

        save_button.setVisibility(View.VISIBLE);
        cancel_button.setVisibility(View.VISIBLE);
        edit_photo.setVisibility(View.VISIBLE);
        new_password.setVisibility(View.VISIBLE);
        new_password_retype.setVisibility(View.VISIBLE);
        old_password.setVisibility(View.VISIBLE);
    }

    public void getProfile() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        ProfileRequest request = new ProfileRequest(token.username, token);
        Call<ProfileResponse> call = agniAPI.getProfile(request);

        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.code() == 200)
                    fillInfo(response.body());
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed to get profile" + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }

    private void changeProfile(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);
        String newUsername = profile_username.getText().toString().equals(username) ? null : profile_username.getText().toString();
        String newFullname = profile_name.getText().toString().equals(fullName) ? null : profile_name.getText().toString();
        String newEmail = profile_email.getText().toString().equals(email) ? null : profile_email.getText().toString();
        String newDistrict = profile_district.getText().toString().equals(district) ? null : profile_district.getText().toString();
        String newCounty = profile_county.getText().toString().equals(county) ? null : profile_county.getText().toString();
        String newLocality = profile_locality.getText().toString().equals(locality) ? null : profile_locality.getText().toString();
        String oldPassword = old_password.getText().toString();
        String newPassword = new_password.getText().toString();
        if(newPassword.equals(new_password_retype.getText().toString())){
            if(newPassword.equals(""))
                newPassword = null;
        }else newPassword = null;

        EditProfileData request = new EditProfileData(token, Long.parseLong(token.getUserid()), newUsername, oldPassword,
                newPassword, newEmail,newFullname, newDistrict, newCounty, newLocality, photoImg != null);
        Call<Long> call = agniAPI.changeProfile(request);

        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.code() == 200){
                    Toast toast = Toast.makeText(getActivity(), "Perfil alterado", Toast.LENGTH_SHORT);
                    toast.show();
                    if(photoImg.getByteCount() > 0){
                        List<Long> list  = new LinkedList<>();
                        list.add(response.body());
                        uploadPhoto(list);
                    }

                }
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed to change profile" + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }

    private void getProfileImage(){
        String path = DOWNLOAD_ENDPOINT  + token.getUserid() + "/photo";

        RequestCreator f = Picasso.get().load(path);
        f.into(profile_img);
        try{
            //Log.d("PHOTO SIZE -> ", "" + f.get().getByteCount());
            if(f.get().getByteCount() > 0)
                f.into(profile_img);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void uploadPhoto( List<Long> list_of_ids_to_upload_to){
        AgniAPI agniAPI = retrofit.create(AgniAPI.class);
        Long id = list_of_ids_to_upload_to.get(0);

        try {

            int factor = calculateResizeFactor(photoImg);
            Bitmap resized = Bitmap.createScaledBitmap(photoImg, photoImg.getWidth()/factor,
                    photoImg.getHeight()/factor, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] data = stream.toByteArray();
            resized.recycle();
            Log.d("SIZE OF DATA: ", String.valueOf(data.length));
            String contentType = "image/jpeg";
            RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), data);
            Call <ResponseBody> call = agniAPI.uploadPhoto(id, contentType, body);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        Toast toast = Toast.makeText(getActivity(), "Photo successfully uploaded", Toast.LENGTH_SHORT);
                        toast.show();
                        photoImg = null;
                    }else{
                        Toast toast = Toast.makeText(getActivity(), "Failed to upload photo: " + response.code(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast toast = Toast.makeText(getActivity(), "Failed to upload photo", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        } catch (Exception e) {
            Toast toast = Toast.makeText(getActivity(), "Excecao no uploadPhoto ", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        }
    }

    private int calculateResizeFactor(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if(width + height > 2000)
            return width + height / 2000;
        else
            return 1;
    }


    private void fillInfo(ProfileResponse response){
        profile_type.setText(response.getRole());

        profile_username.setText(response.getUsername());
        profile_name.setText(response.getName());
        profile_email.setText(response.getEmail());
        profile_locality.setText(response.getLocality());
        profile_county.setText(response.getCounty());
        profile_district.setText(response.getDistrict());
    }

    private void getLikedOccurrences(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        AgniAPI agniAPI = retrofit.create(AgniAPI.class);
        Call<CursorList> call;

        call = agniAPI.getLikedOccurrences(new ListOccurrenceLikeData(token, Long.parseLong(token.userID), null));

        call.enqueue(new Callback<CursorList>() {
            public void onResponse(Call<CursorList> call, Response<CursorList> response) {
                if (response.code() == 200) {
                    CursorList c = response.body();
                    cursor = c.getCursor();
                    if(!c.getMapList().isEmpty()){
                        list.addAll( c.getMapList());
                        if(c.getMapList().size() == QUERY_LIMIT)
                            liked_occurrences_number.setText("99+");
                        else
                            liked_occurrences_number.setText(c.getMapList().size()+"");

                    }
                    else
                        liked_occurrences_number.setText("0");
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed to get occurrences" + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            public void onFailure(Call<CursorList> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }

    private void getRegisteredOccurrences(){

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        AgniAPI agniAPI = retrofit.create(AgniAPI.class);
        Call<CursorList> call = agniAPI.getMoreOccurrences(new ListOccurrenceData(token, true,
                token.username, cursor, null, null, null));

        call.enqueue(new Callback<CursorList>() {
            public void onResponse(Call<CursorList> call, Response<CursorList> response) {
                if (response.code() == 200) {
                    CursorList c = response.body();
                    cursor = c.getCursor();
                    if(!c.getMapList().isEmpty()){
                        list.addAll( c.getMapList());
                        if(c.getMapList().size() == QUERY_LIMIT)
                            registered_occurrences_number.setText("99+");
                        else
                            registered_occurrences_number.setText(c.getMapList().size()+"");
                    }
                    else
                        registered_occurrences_number.setText("0");
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed to get occurrences" + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            public void onFailure(Call<CursorList> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }
}