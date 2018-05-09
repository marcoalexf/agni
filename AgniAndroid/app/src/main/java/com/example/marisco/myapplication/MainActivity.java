package com.example.marisco.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Serializable{

    public static final String RESPONSE = "com.example.marisco.myapplication.RESPONSE";

    Retrofit retrofit;

    @BindView(R.id.username_input)
    EditText username_input;
    @BindView(R.id.password_input)
    EditText password_input;
    @BindView(R.id.tokenID)
    TextView tokenID;
    @BindView(R.id.login_button)
    Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loginUser(username_input.getText().toString(), password_input.getText().toString());
            }
        });
    }

    public void loginUser(String username, String password) {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://blissful-land-196114.appspot.com/rest/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        Call<LoginResponse> call = agniAPI.loginUser(new User(username_input.getText().toString(), password_input.getText().toString()));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.code() == 200)
                    launchActivity(response.body());
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
}
