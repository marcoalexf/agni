package com.example.marisco.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String TOKEN = "token";
    private static final String USERNAME = "username";
    public static final String RESPONSE = "com.example.marisco.myapplication.RESPONSE";
    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";
    private String username, email, name, locality, county, district;
    private Retrofit retrofit;
    private LoginResponse token;

    //@BindView(R.id.profile_edit_avatar) ImageView profile_edit_avatar;

    @BindView(R.id.profile_username) EditText profile_username;
    @BindView(R.id.profile_role) EditText profile_type;
    @BindView(R.id.profile_name) EditText profile_name;
    @BindView(R.id.profile_email) EditText profile_email;
    @BindView(R.id.profile_locality) EditText profile_locality;
    @BindView(R.id.profile_county) EditText profile_county;
    @BindView(R.id.profile_district) EditText profile_district;
    @BindView(R.id.edit_button) Button edit_button;
    @BindView(R.id.btnSave) Button save_button;
    @BindView(R.id.btnCancelSave) Button cancel_button;
    @BindView(R.id.occurrences_img)ImageView occurrences_img;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);
        Bundle b = this.getArguments();
        if(b != null){
            this.token = (LoginResponse) b.getSerializable(TOKEN);
        }

        getProfile();
        fieldsSetup();

        edit_button.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                saveInitialValues();
                editProfile();
            }
        });
        save_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                changeProfile();
                fieldsSetup();
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
                args.putSerializable(USERNAME, token.username);
                od.setArguments(args);
                fman.beginTransaction().replace(R.id.fragment, od).commit();
            }
        });
        return v;
    }

    private void saveInitialValues(){
        username = profile_username.getText().toString();
        name = profile_name.getText().toString();
        email = profile_email.getText().toString();
        locality = profile_locality.getText().toString();
        county = profile_county.getText().toString();
        district = profile_district.getText().toString();
    }

    private void restoreInitialValues(){
        profile_username.setText(username);
        profile_name.setText(name);
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

        EditProfileData request = new EditProfileData(token, profile_username.getText().toString(),
                profile_email.getText().toString(), profile_district.getText().toString(), profile_county.getText().toString(),
                profile_locality.getText().toString(), false);
        Call<ResponseBody> call = agniAPI.changeProfile(request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200){
                    Toast toast = Toast.makeText(getActivity(), "Perfil alterado", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed to change profile" + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
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
}