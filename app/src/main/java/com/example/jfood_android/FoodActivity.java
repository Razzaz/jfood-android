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

import java.util.ArrayList;
import java.util.Objects;

public class FoodActivity extends AppCompatActivity {

    private static final String TAG = "FoodActivity";

    private RecyclerView mRecyclerView;
    private FoodAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Food> listFood = new ArrayList<>();
    private ArrayList<FoodItem> exampleList = new ArrayList<>();
    private ArrayList<Integer> basketList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTransparentStatusBarOnly(FoodActivity.this);
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        listFood = getIntent().getParcelableArrayListExtra("ListFoodData");

        createExampleList();
        buildExampleList();

    }

    private void createExampleList(){

        for(Food foodPtr : listFood){
            exampleList.add(new FoodItem(R.drawable.ic_android, foodPtr.getName()+"", foodPtr.getPrice()+""));
            Log.d("INIIII", foodPtr.getName());
        }
    }

    private void buildExampleList(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new FoodAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                basketList.add(listFood.get(position).getId());

            }
        });

        //TODO : bikin satu button lagi buat naro intent2, view basket


    }

    public void setTransparentStatusBarOnly(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        // this lines ensure only the status-bar to become transparent without affecting the nav-bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
