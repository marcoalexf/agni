package com.example.marisco.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.prefs.Preferences;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Serializable{

    public static final String RESPONSE = "com.example.marisco.myapplication.RESPONSE";
    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";

    SharedPreferences sharedPreferences;

    Retrofit retrofit;

    @BindView(R.id.username_input) EditText username_input;
    @BindView(R.id.password_input) EditText password_input;
    @BindView(R.id.login_button) Button login_button;
    @BindView(R.id.register_button) Button register_button;
    @BindView(R.id.save_credentials) CheckBox save_credentials;
    @BindView(R.id.forgot_password) TextView forgot_password;

    RelativeLayout rellay1;
    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String stored_username = this.sharedPreferences.getString("username", "");
        Log.d("STORED_USERNAME", stored_username);
        if(!stored_username.equalsIgnoreCase("")) // username stored, auto-filling
        {
            Log.e("LOGIN", "NO CREDENTIALS FOUND");
            Log.d("PASSWORD STORED", this.sharedPreferences.getString("password", ""));
            loginUser(stored_username, this.sharedPreferences.getString("password", ""));
        }

        handler.postDelayed(runnable, 2000);

        this.login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(save_credentials.isChecked()){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username_input.getText().toString());
                    editor.putString("password", password_input.getText().toString());
                    editor.apply();
                }
                loginUser(username_input.getText().toString(), password_input.getText().toString());
            }
        });

        this.forgot_password.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "Estudasses :|", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        this.register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    public void loginUser(String username, String password) {

        Log.d("LOG_USER_WITH", "USERNAME: " + username + " | PASSWORD: " + password);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        Call<LoginResponse> call = agniAPI.loginUser(new User(username, password));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 200) {
                    launchActivity(response.body());
                }else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }

    public void launchActivity(LoginResponse response){
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(RESPONSE, response);
        startActivity(intent);
    }

    public void registerUser(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
