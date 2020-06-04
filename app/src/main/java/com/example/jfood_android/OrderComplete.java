package com.example.jfood_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import java.util.Objects;

public class OrderComplete extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTransparentStatusBarOnly(OrderComplete.this);
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(OrderComplete.this, SellerActivity.class));
                finish();
            }
        }, 3500);
    }

    public void setTransparentStatusBarOnly(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
