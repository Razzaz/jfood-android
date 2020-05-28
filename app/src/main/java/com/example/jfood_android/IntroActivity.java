package com.example.jfood_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import java.util.Objects;

import static com.example.jfood_android.LoginActivity.SHARED_PREFS;
import static com.example.jfood_android.LoginActivity.loginState;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTransparentStatusBarOnly(IntroActivity.this);
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                boolean states = sharedPreferences.getBoolean(loginState, false);
                if(states){
                    startActivity(new Intent(IntroActivity.this, SellerActivity.class));
                }
                else{
                    startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, 3000);

    }

    public void setTransparentStatusBarOnly(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
