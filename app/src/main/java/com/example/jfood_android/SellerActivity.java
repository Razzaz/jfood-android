package com.example.jfood_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SellerActivity extends AppCompatActivity {

    private static final String TAG = "SellerActivity";

    private RecyclerView mRecyclerView;
    private SellerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<SellerItem> exampleList = new ArrayList<>();
    private ArrayList<Seller> listSeller = new ArrayList<>();
    private ArrayList<Food> foodIdList = new ArrayList<>();

    public static ArrayList<Food> tempFoodList = new ArrayList<>();

    private HashMap<Seller, ArrayList<Food>> foodMapping = new HashMap<>();

    private FloatingActionButton fabHome, fabCart, fabProfile;
    private Animation fabOpen, fabClose, fabClockwise, fabAntiClockwise;
    private boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTransparentStatusBarOnly(SellerActivity.this);
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        refreshList();
        //createExampleList();
        buildExampleList();

        EditText editText = findViewById(R.id.edittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        fabHome = findViewById(R.id.fab_home);
        fabProfile = findViewById(R.id.fab_profile);
        fabCart = findViewById(R.id.fab_cart);

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabAntiClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen){
                    fabHome.startAnimation(fabClockwise);
                    fabProfile.startAnimation(fabClose);
                    fabCart.startAnimation(fabClose);

                    fabCart.setClickable(false);
                    fabProfile.setClickable(false);

                    isOpen = false;
                }
                else{
                    fabHome.startAnimation(fabAntiClockwise);
                    fabProfile.startAnimation(fabOpen);
                    fabCart.startAnimation(fabOpen);

                    fabCart.setClickable(true);
                    fabProfile.setClickable(true);

                    isOpen = true;
                }
            }
        });

        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerActivity.this, ProfileActivity.class));
            }
        });

        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerActivity.this, HistoryActivity.class));
            }
        });
    }

    private void filter(String text){
        ArrayList<SellerItem> filterList = new ArrayList<>();

        for (SellerItem item : exampleList){
            if(item.getText1().toLowerCase().contains(text.toLowerCase()) || item.getText2().toLowerCase().contains((text.toLowerCase()))){
                filterList.add(item);
            }
        }

        mAdapter.filterList(filterList);
    }

    protected void refreshList() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    for (int i=0; i<jsonResponse.length(); i++) {

                        JSONObject food = jsonResponse.getJSONObject(i);
                        JSONObject seller = food.getJSONObject("seller");
                        JSONObject location = seller.getJSONObject("location");

                        Location newLocation = new Location(
                                location.getString("province"),
                                location.getString("description"),
                                location.getString("city")
                        );

                        Seller newSeller = new Seller(
                                seller.getInt("id"),
                                seller.getString("name"),
                                seller.getString("email"),
                                seller.getString("phoneNumber"),
                                newLocation
                        );

                        Food newFood = new Food(
                                food.getInt("id"),
                                food.getString("name"),
                                food.getInt("price"),
                                food.getString("category"),
                                newSeller
                        );

                        foodIdList.add(newFood);

                        boolean tempStatus = true;
                        for(Seller sellerPtr : listSeller) {
                            if(sellerPtr.getId() == newSeller.getId()){
                                tempStatus = false;
                            }
                        }
                        if(tempStatus){
                            listSeller.add(newSeller);
                        }
                    }

                    for(Seller sellerPtr : listSeller){
                        exampleList.add(listSeller.indexOf(sellerPtr), new SellerItem(R.drawable.seller_icon, sellerPtr.getName()+"", sellerPtr.getLocation().getCity()+""));
                        mAdapter.notifyItemInserted(listSeller.indexOf(sellerPtr));

                        tempFoodList = new ArrayList<>();
                        for(Food foodPtr : foodIdList){
                            if(foodPtr.getSeller().getId() == sellerPtr.getId()){
                                tempFoodList.add(foodPtr);
                            }
                        }
                        foodMapping.put(sellerPtr, tempFoodList);
                        Log.d(TAG, String.valueOf(foodMapping));

                    }

                }
                catch (JSONException e) {
                    Toast.makeText(SellerActivity.this, "Load data failed.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        MenuRequest menuRequest = new MenuRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(SellerActivity.this);
        queue.add(menuRequest);

    }

    private void createExampleList(){

        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
        exampleList.add(new SellerItem(R.drawable.seller_icon, "Starbucks", "Harmony"));
    }

    private void buildExampleList(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SellerAdapter(exampleList, listSeller, foodMapping);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new SellerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                ArrayList<Food> newFood;
                newFood = foodMapping.get(listSeller.get(position));
                Intent intent = new Intent(SellerActivity.this, FoodActivity.class);
                intent.putParcelableArrayListExtra("ListFoodData", newFood);
                intent.putExtra("sellerName", listSeller.get(position).getName());
                intent.putExtra("sellerDescription", listSeller.get(position).getLocation());
                startActivity(intent);
                finish();
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
    public void onBackPressed() {
        finish();
    }

}
