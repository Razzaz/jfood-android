package com.example.jfood_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.example.jfood_android.LoginActivity.userState;
import static com.example.jfood_android.LoginActivity.userStateEmail;
import static com.example.jfood_android.LoginActivity.userStateName;

public class ProfileActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String loginState = "loginState";
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTransparentStatusBarOnly(ProfileActivity.this);
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        final int userId = sharedPreferences.getInt(userState, 0);
        String userName = sharedPreferences.getString(userStateName, "John Doe");
        String userEmail = sharedPreferences.getString(userStateEmail, "johndoe@mail.com");
        //String userJoinDate = sharedPreferences.getString(userStateDate, "30031998");

        TextView name = findViewById(R.id.name);
        TextView email = findViewById(R.id.email);

        name.setText(userName);
        email.setText(userEmail);

        findViewById(R.id.textView6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(loginState, false);
                editor.apply();
                startActivity(new Intent(ProfileActivity.this, IntroActivity.class));
                finish();
            }
        });

        findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject != null){
                                Toast.makeText(ProfileActivity.this, "Remove Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProfileActivity.this, IntroActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(ProfileActivity.this, "Remove Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                RemoveRequest removeRequest = new RemoveRequest(userId+"", responseListener);
                RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
                queue.add(removeRequest);

                SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(loginState, false);
                editor.apply();
                startActivity(new Intent(ProfileActivity.this, IntroActivity.class));
            }
        });
    }

    public void setTransparentStatusBarOnly(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        // this lines ensure only the status-bar to become transparent without affecting the nav-bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}
