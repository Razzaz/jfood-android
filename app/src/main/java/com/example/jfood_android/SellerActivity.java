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
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

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
        createExampleList();
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

    }

    private void filter(String text){
        ArrayList<SellerItem> filterList = new ArrayList<>();

        for (SellerItem item : exampleList){
            if(item.getText1().toLowerCase().contains(text.toLowerCase())){
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

                    //TODO
                    for(Seller sellerPtr : listSeller){
                        exampleList.add(listSeller.indexOf(sellerPtr), new SellerItem(R.drawable.ic_android, sellerPtr.getName()+"", "Line 2"));
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

        for(Seller sellerPtr : listSeller){
            exampleList.add(new SellerItem(R.drawable.ic_android, sellerPtr.getName(), "Line 2"));
            Log.d("INIIII", sellerPtr.getName());
        }
    }

    private void buildExampleList(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        //todo tambahin sama child mapping di adapter seller
        mAdapter = new SellerAdapter(exampleList, listSeller, foodMapping);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new SellerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                ArrayList<Food> newFood = new ArrayList<>();
                newFood = foodMapping.get(listSeller.get(position));
                Log.d(TAG, String.valueOf(newFood));
                Intent intent = new Intent(SellerActivity.this, FoodActivity.class);
                intent.putParcelableArrayListExtra("ListFoodData", newFood);
                startActivity(intent);

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
