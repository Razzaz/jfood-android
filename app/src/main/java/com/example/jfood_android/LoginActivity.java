package com.example.jfood_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String loginState = "loginState";
    public static final String userState = "userState";
    public static final String userStateName = "userStateName";
    public static final String userStateEmail = "userStateEmail";
    public static final String userStateDate = "userStateDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTransparentStatusBarOnly(LoginActivity.this);
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        final EditText etEmail = findViewById(R.id.login_email);
        final EditText etPassword = findViewById(R.id.login_password);
        final TextView btnLogin = findViewById(R.id.login_button);
        TextView tvRegister = findViewById(R.id.login_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String regexEmail = "^([\\w\\&\\*_~]+\\.{0,1})+@[\\w][\\w\\-]*(\\.[\\w\\-]+)+$";
                String regexPassword = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,}$";

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    etEmail.setError("Email is Required.");
                    btnLogin.setEnabled(true);
                    return;
                }

                if(!Pattern.matches(regexEmail, email)){
                    etEmail.setError("Email not valid.");
                    btnLogin.setEnabled(true);
                    return;
                }

                if(!Pattern.matches(regexPassword, password)){
                    etPassword.setError("Password not valid.");
                    btnLogin.setEnabled(true);
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    etPassword.setError("Password is Required.");
                    btnLogin.setEnabled(true);
                    return;
                }

                if(password.length() < 6){
                    etPassword.setError("Password must be > 6 characters");
                    btnLogin.setEnabled(true);
                    return;
                }

                else{
                    btnLogin.setEnabled(false);
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject != null){
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt(userState, jsonObject.getInt("id"));
                                editor.putString(userStateName, jsonObject.getString("name"));
                                editor.putString(userStateEmail, jsonObject.getString("email"));
                                editor.putString(userStateDate, jsonObject.getString("joinDate"));
                                editor.putBoolean(loginState, true);
                                editor.apply();

                                Intent intent = new Intent(LoginActivity.this, SellerActivity.class);
                                //intent.putExtra("currentUserId", jsonObject.getInt("id"));
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    public void setTransparentStatusBarOnly(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
