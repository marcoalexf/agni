package com.example.marisco.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity  extends AppCompatActivity implements Serializable {


    public static final String RESPONSE = "com.example.marisco.myapplication.RESPONSE";
    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";
    public static final String USER = "USER";
    public static final String WORKER = "WORKER";
    public static final String CIVIL_PROTECTION = "PROTEÇÃO CIVIL";
    public static final String FIREFIGHTERS = "BOMBEIROS";
    private View mProgressView;
    private View mRegisterFormView;
    Retrofit retrofit;

    @BindView(R.id.name) EditText name_input;
    @BindView(R.id.username) EditText username_input;
    @BindView(R.id.email) EditText email_input;
    @BindView(R.id.password) EditText password_input;
    @BindView(R.id.passwordConf) EditText password_confirmation;
    @BindView(R.id.register_button) Button register_button;
    @BindView(R.id.spinner_user_type) Spinner spinner_user_type;
    @BindView(R.id.spinner_worker_type) Spinner spinner_worker_type;
    @BindView(R.id.county) EditText county_input;
    @BindView(R.id.district) EditText district_input;
    @BindView(R.id.locality) EditText locality_input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        // Set up the register form.
       // populateAutoComplete();
            register_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptRegister();
                }
            });

        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_types, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_user_type.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.worker_types, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_worker_type.setAdapter(adapter2);

        spinner_user_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if(arg2 == 0)
                    spinner_worker_type.setVisibility(View.GONE);
                else spinner_worker_type.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });
    }

    /*private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, );
    }*/

    /* If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        AutoCompleteTextView mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        EditText mNameView = (EditText) findViewById(R.id.name);
        EditText mUsernameView = (EditText) findViewById(R.id.username);
        EditText mPasswordView = (EditText) findViewById(R.id.password);
        EditText mPasswordConfView = (EditText) findViewById(R.id.passwordConf);
        EditText mLocalityView = (EditText) findViewById(R.id.locality);
        EditText mCountyView = (EditText) findViewById(R.id.county);
        EditText mDistrictView = (EditText) findViewById(R.id.district);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfView.setError(null);
        mUsernameView.setError(null);
        mNameView.setError(null);

        String name = name_input.getText().toString();
        String username = username_input.getText().toString();
        String email = email_input.getText().toString();
        String password = password_input.getText().toString();
        String passwordConf = password_confirmation.getText().toString();
        String county = county_input.getText().toString();
        String district = district_input.getText().toString();
        String locality = locality_input.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(name) && !cancel){
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        if(TextUtils.isEmpty(username) && !cancel){
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email) && !cancel) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email) && !cancel) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password) && !cancel){
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if(!password.equals(passwordConf) && !cancel){
            mPasswordView.setError(getString(R.string.error_password_does_not_match));
            focusView = mPasswordView;
            cancel = true;
        }

        if(TextUtils.isEmpty(locality)){
            mLocalityView.setError(getString(R.string.error_field_required));
            focusView = mLocalityView;
            cancel = true;
        }

        if(TextUtils.isEmpty(county)){
            mCountyView.setError(getString(R.string.error_field_required));
            focusView = mCountyView;
            cancel = true;
        }

        if(TextUtils.isEmpty(district)){
            mDistrictView.setError(getString(R.string.error_field_required));
            focusView = mDistrictView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            registerUser();
        }
    }

    /**
     * Shows the progress UI and hides the register form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\Q!@#$%^&*()_+\\=-[]{};:'\"|,.<>/?¨»«£§\\E])(?=\\S+$).{6,}$");
    }

    private void registerUser(){
        String name = name_input.getText().toString();
        String username = username_input.getText().toString();
        String email = email_input.getText().toString();
        String password = password_input.getText().toString();
        String locality = locality_input.getText().toString();
        String district = district_input.getText().toString();
        String county = county_input.getText().toString();
        String type = spinner_user_type.getSelectedItem().toString();
        String entity = null;

        if(spinner_user_type.getSelectedItemPosition() == 1){
            if(spinner_worker_type.getSelectedItemPosition() == 0)
                entity = CIVIL_PROTECTION;
            else if (spinner_worker_type.getSelectedItemPosition() == 1)
                entity = FIREFIGHTERS;
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);
        String [] users = getResources().getStringArray(R.array.user_types);
        UserRegister user;
        if(type.equals(users[0]))
            user = new UserRegister(name, username, password, email, USER, locality, county, district, entity);
        else
            user = new UserRegister(name, username, password, email, WORKER, locality, county, district, entity);

        Call<ResponseBody> call = agniAPI.registerUser(user);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Novo utilizador registado"
                            , Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Registo falhado: " + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }
}