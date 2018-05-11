package com.example.marisco.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    public static final String RESPONSE = "com.example.marisco.myapplication.RESPONSE";
    private static final String ENDPOINT = "https://liquid-layout-196103.appspot.com/rest/";
    private String username, email, type, name;
    private Retrofit retrofit;
    private LoginResponse token;

    //@BindView(R.id.profile_edit_avatar) ImageView profile_edit_avatar;

    @BindView(R.id.profile_username) TextView profile_username;
    @BindView(R.id.profile_type) TextView profile_type;
    @BindView(R.id.profile_name) TextView profile_name;
    @BindView(R.id.profile_email) TextView profile_email;
    @BindView(R.id.profile_locality) TextView profile_locality;
    @BindView(R.id.profile_county) TextView profile_county;
    @BindView(R.id.profile_district) TextView profile_district;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);
        Bundle b = this.getArguments();
        if(b != null){
            this.token = (LoginResponse) getActivity().getIntent().getSerializableExtra(TOKEN);
        }

        //this.username = args.getString("username");
       //profile_username.setText(this.username);
        getProfile();
        return v;
    }

    public void getProfile() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        if(token == null){
            Toast toast2 = Toast.makeText(getActivity(), "token é nulo", Toast.LENGTH_SHORT);
            toast2.show();
        }


        Call<ProfileResponse> call = agniAPI.getProfile(token);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.code() == 200)
                    fillInfo(response.body());
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed to get profile", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }

    /*public void launchActivity(ProfileResponse response){
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(RESPONSE, response);
        startActivity(intent);
    }*/
    private void fillInfo(ProfileResponse response){
        Toast toast2 = Toast.makeText(getActivity(), "tou no fillInfo", Toast.LENGTH_SHORT);
        toast2.show();
        profile_username.setText(response.getUsername());
        profile_type.setText(response.getRole());

        profile_name.append(response.getName());
        profile_email.append(response.getEmail());
        profile_locality.append(response.getLocality());
        profile_county.append(response.getCounty());
        profile_district.append(response.getDistrict());
    }
}
