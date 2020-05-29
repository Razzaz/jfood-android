package com.example.jfood_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class BasketActivity extends AppCompatActivity implements BasketSheetDialog.BottomSheetListener {

    private RecyclerView mRecyclerView;
    private BasketAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<BasketItem> exampleList = new ArrayList<>();

    private RadioGroup paymentMethod;
    private TextView placeOrder;

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

        paymentMethod = findViewById(R.id.radioGroup);
        paymentMethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton mRadioButton = findViewById(i);
                String selected = mRadioButton.getText().toString();
                switch (selected){
                    case "Cash":

                        break;

                    case "Cashless":
                        BasketSheetDialog basketSheet = new BasketSheetDialog();
                        basketSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
                        break;
                }
            }
        });

        placeOrder = findViewById(R.id.place_order);
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BasketActivity.this, OrderComplete.class));
                finish();
            }
        });

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

    @Override
    public void onButtonClicked(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
