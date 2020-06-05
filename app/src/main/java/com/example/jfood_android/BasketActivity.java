package com.example.jfood_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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

import static com.example.jfood_android.LoginActivity.SHARED_PREFS;
import static com.example.jfood_android.LoginActivity.userState;

public class BasketActivity extends AppCompatActivity implements BasketSheetDialog.BottomSheetListener, BottomSheetDialog.BottomSheetListener {

    private static final String TAG = "BasketActivity";

    private RecyclerView mRecyclerView;
    private BasketAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<BasketItem> exampleList = new ArrayList<>();

    private RadioGroup paymentMethod;
    private TextView placeOrder;
    private ArrayList<Food> listFood = new ArrayList<>();

    private ArrayList<Integer> keySet = new ArrayList<>();
    private HashMap<Integer, Integer> foodIdAndAmount = new HashMap<>();
    private HashMap<Integer, Integer> foodIdKey = new HashMap<>();
    private HashMap<Integer, String> foodIdAndName = new HashMap<>();
    private HashMap<Integer, String> foodIdAndCategory = new HashMap<>();
    private HashMap<Integer, Integer> foodIdAndPrice = new HashMap<>();

    private int totalPrice = 0;
    private TextView total;
    private int getPosition;

    private int priceRequest;
    private int userId;
    private String promoCode;
    private String sellerName;

    private String foodListOrder = "";

    private boolean ready = false;

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

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt(userState, 0);
        listFood = getIntent().getParcelableArrayListExtra("listFood");
        sellerName = getIntent().getStringExtra("sellerName");

        foodIdKey = (HashMap<Integer, Integer>) getIntent().getSerializableExtra("foodIdKey");
        foodIdAndAmount = (HashMap<Integer, Integer>) getIntent().getSerializableExtra("foodIdAndAmount");
        foodIdAndName = (HashMap<Integer, String>) getIntent().getSerializableExtra("foodIdAndName");
        foodIdAndCategory = (HashMap<Integer, String>) getIntent().getSerializableExtra("foodIdAndCategory");
        foodIdAndPrice = (HashMap<Integer, Integer>) getIntent().getSerializableExtra("foodIdAndPrice");

        Log.d(TAG+"Key", String.valueOf(foodIdKey));

        total = findViewById(R.id.total);

        paymentMethod = findViewById(R.id.radioGroup);
        paymentMethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton mRadioButton = findViewById(i);
                String selected = mRadioButton.getText().toString();
                switch (selected){
                    case "Cash":
                        total.setText("Rp. " + totalPrice);
                        ready = true;
                        break;

                    case "Cashless":
                        ready = true;
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
                if(!ready){
                    Toast.makeText(BasketActivity.this, "Cash or cashless?", Toast.LENGTH_SHORT).show();
                }
                else{
                    StringBuilder foodId = null;
                    for(int i : foodIdKey.keySet()){
                        exampleList.add(new BasketItem(foodIdAndAmount.get(i)+"x", foodIdAndName.get(i), "Rp. "+foodIdAndPrice.get(i)));
                        totalPrice = totalPrice + (foodIdAndAmount.get(i) * foodIdAndPrice.get(i));
                        keySet.add(i);

                        int count = foodIdAndAmount.get(i);
                        String val = String.valueOf(foodIdKey.get(i));
                        foodId = new StringBuilder(val.length() * count);
                        while (count -- > 0) {
                            foodId.append(val).append(",");
                        }
                        foodId.toString();
                        foodListOrder = foodListOrder + foodId;
                        Log.d(TAG, foodListOrder);
                    }

                    if(foodListOrder.length() < 1){
                        Toast.makeText(BasketActivity.this, "Item amount can't be zero", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        RadioButton radioButton = findViewById(paymentMethod.getCheckedRadioButtonId());
                        String selected = radioButton.getText().toString();
                        BuatPesananRequest pesananRequest = null;

                        Response.Listener<String> responseListenerOrder = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (response != null){
                                        Toast.makeText(BasketActivity.this, "Order successful", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(BasketActivity.this, "Order failed", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        };

                        if(selected.equals("Cash")){
                            pesananRequest = new BuatPesananRequest(foodListOrder.substring(0, foodListOrder.length()-1), userId+"", responseListenerOrder);
                        }
                        else if(selected.equals("Cashless")){
                            pesananRequest = new BuatPesananRequest(foodListOrder.substring(0, foodListOrder.length()-1), userId+"", promoCode, responseListenerOrder);
                        }

                        RequestQueue queue = Volley.newRequestQueue(BasketActivity.this);
                        queue.add(pesananRequest);

                        startActivity(new Intent(BasketActivity.this, OrderComplete.class));
                        finish();
                    }
                }
            }
        });

        createExampleList();
        total.setText("Rp. "+totalPrice);
        buildExampleList();

    }

    private void createExampleList(){
        for(int i : foodIdKey.keySet()){
            exampleList.add(new BasketItem(foodIdAndAmount.get(i)+"x", foodIdAndName.get(i), "Rp. "+foodIdAndPrice.get(i)));
            totalPrice = totalPrice + (foodIdAndAmount.get(i) * foodIdAndPrice.get(i));
            keySet.add(i);
        }
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
                totalPrice = totalPrice - (foodIdAndPrice.get(keySet.get(position)) * foodIdAndAmount.get(keySet.get(position)));
                total.setText("Rp. "+ totalPrice);
                exampleList.remove(position);
                foodIdKey.remove(position);
                foodIdAndName.remove(position);
                foodIdAndAmount.remove(position);
                foodIdAndPrice.remove(position);
                foodIdAndCategory.remove(position);
                keySet.remove(position);
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onAmountClick(int position) {
                getPosition = position;
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });
    }

    private void clearExampleList(){
        exampleList.clear();
        keySet.clear();
    }

    public void setTransparentStatusBarOnly(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onButtonAddClicked(final String text) {
        promoCode = text;
        if(promoCode.equals("")){
            promoCode = "BM51";
        }
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    for(int i = 0; i <jsonResponse.length(); i++){
                        JSONObject promo = jsonResponse.getJSONObject(i);
                        Log.d(TAG, String.valueOf(promo));
                        if(text.equals(promo.getString("code")) && promo.getBoolean("active")){
                            if(totalPrice > promo.getInt("minPrice")){
                                priceRequest = promo.getInt("discount");
                                totalPrice = totalPrice - priceRequest;
                                if(totalPrice < 0 ){
                                    totalPrice = 0;
                                }
                                total.setText("Rp. "+(totalPrice));
                                Log.d(TAG, total.getText().toString());
                            }
                        }
                        else{
                            total.setText("Rp. "+totalPrice);
                        }
                    }
                }
                catch (JSONException e){
                    Log.d(TAG, "Load data failed.");
                }
            }
        };

        CheckPromoRequest promoRequest = new CheckPromoRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(BasketActivity.this);
        queue.add(promoRequest);
    }

    @Override
    public void onButtonClicked(int text) {

        foodIdAndAmount.put(keySet.get(getPosition), text);
        totalPrice = 0;
        clearExampleList();
        createExampleList();
        total.setText("Rp. "+totalPrice);

        paymentMethod.check(paymentMethod.getChildAt(0).getId());

        buildExampleList();

    }

    @Override
    public void onBackPressed() {
        Intent intent= new Intent(BasketActivity.this, FoodActivity.class);
        intent.putExtra("foodIdAndAmount", foodIdAndAmount);
        intent.putExtra("foodIdKey", foodIdKey);
        intent.putExtra("foodIdAndName", foodIdAndName);
        intent.putExtra("listFood", listFood);
        intent.putExtra("sellerName", sellerName);
        intent.putExtra("foodIdAndCategory", foodIdAndCategory);
        intent.putExtra("foodIdAndPrice", foodIdAndPrice);
        startActivity(intent);
    }
}
