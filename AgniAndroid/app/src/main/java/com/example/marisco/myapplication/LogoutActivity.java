package com.example.marisco.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogoutActivity extends AppCompatActivity implements Serializable {

    private static final String TOKEN = "token";
    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";
    private Retrofit retrofit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginResponse token = (LoginResponse)getIntent().getSerializableExtra(TOKEN);
        doLogout(token);

    }

    private void doLogout(LoginResponse token){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        Call<ResponseBody> call = agniAPI.logoutUser(token);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Logout efetuado"
                            , Toast.LENGTH_SHORT);
                    toast.show();
                    deleteCredentials();
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }

    private void deleteCredentials(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEditor = prefs.edit();
        mEditor.remove("username");
        mEditor.remove("password");
        mEditor.apply();
    }
}
