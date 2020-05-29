package com.example.jfood_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class BasketActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private BasketAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<BasketItem> exampleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTransparentStatusBarOnly(BasketActivity.this);
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        createExampleList();
        buildExampleList();

    }

    private void createExampleList(){

        exampleList.add(new BasketItem(2+"x", "Belanja", "Murah"));
        exampleList.add(new BasketItem(1+"x", "Belanja", "Murah"));
        exampleList.add(new BasketItem(2+"x", "Belanja", "Murah"));
        exampleList.add(new BasketItem(3+"x", "Belanja", "Murah"));
        exampleList.add(new BasketItem(2+"x", "Belanja", "Murah"));
    }

    private void buildExampleList(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new BasketAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BasketAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(BasketActivity.this, position+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position) {
                exampleList.remove(position);
                mAdapter.notifyItemRemoved(position);
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
