package com.example.jfood_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.example.jfood_android.LoginActivity.SHARED_PREFS;
import static com.example.jfood_android.LoginActivity.userState;

public class FoodActivity extends AppCompatActivity implements BottomSheetDialog.BottomSheetListener, Serializable {

    private static final String TAG = "FoodActivity";

    private RecyclerView mRecyclerView;
    private FoodAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Food> listFood = new ArrayList<>();
    private ArrayList<String> invoiceStatus = new ArrayList<>();
    private ArrayList<FoodItem> exampleList = new ArrayList<>();

    private HashMap<Integer, Integer> foodIdAndAmount = new HashMap<>();
    private HashMap<Integer, Integer> foodIdKey = new HashMap<>();
    private HashMap<Integer, String> foodIdAndName = new HashMap<>();
    private HashMap<Integer, String> foodIdAndCategory = new HashMap<>();
    private HashMap<Integer, Integer> foodIdAndPrice = new HashMap<>();

    private HashMap<Integer, Integer> RxFoodIdKey = new HashMap<>();
    private HashMap<Integer, Integer> RxFoodIdAndAmount = new HashMap<>();
    private HashMap<Integer, Integer> RxFoodIdAndPrice = new HashMap<>();
    private HashMap<Integer, String> RxFoodIdAndName = new HashMap<>();

    private int getPosition = 0;
    private TextView ViewBasket;

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

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(userState, 0);

        RxFoodIdKey = (HashMap<Integer, Integer>) getIntent().getSerializableExtra("foodIdKey");
        if(RxFoodIdKey != null){
            foodIdKey = RxFoodIdKey;
        }
        Log.d(TAG, String.valueOf(foodIdAndAmount));

        RxFoodIdAndAmount = (HashMap<Integer, Integer>) getIntent().getSerializableExtra("foodIdAndAmount");
        if(RxFoodIdAndAmount != null){
            foodIdAndAmount = RxFoodIdAndAmount;
        }
        Log.d(TAG, String.valueOf(foodIdAndAmount));

        RxFoodIdAndPrice = (HashMap<Integer, Integer>) getIntent().getSerializableExtra("foodIdAndPrice");
        if(RxFoodIdAndPrice != null){
            foodIdAndPrice = RxFoodIdAndPrice;
        }
        Log.d(TAG, String.valueOf(foodIdAndAmount));

        RxFoodIdAndName = (HashMap<Integer, String>) getIntent().getSerializableExtra("foodIdAndName");
        if(RxFoodIdAndName != null){
            foodIdAndName = RxFoodIdAndName;
        }
        Log.d(TAG, String.valueOf(foodIdAndAmount));

        listFood = getIntent().getParcelableArrayListExtra("ListFoodData");
        if(listFood == null){
            listFood = getIntent().getParcelableArrayListExtra("listFood");
        }
        final String sellerName = getIntent().getStringExtra("sellerName");

        TextView seller = findViewById(R.id.textView4);
        TextView description = findViewById(R.id.textView5);
        seller.setText(sellerName);
        description.setText("Verified Merchant");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    JSONObject invoice = jsonResponse.getJSONObject(jsonResponse.length()-1);
                    invoiceStatus.add(invoice.getString("invoiceStatus"));
                    Log.d(TAG, String.valueOf(invoiceStatus));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        PesananFetchRequest fetchRequest = new PesananFetchRequest(userId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(FoodActivity.this);
        queue.add(fetchRequest);

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

        ViewBasket = findViewById(R.id.view_basket);
        ViewBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodActivity.this, BasketActivity.class);
                intent.putExtra("foodIdAndAmount", foodIdAndAmount);
                intent.putExtra("foodIdKey", foodIdKey);
                intent.putExtra("foodIdAndName", foodIdAndName);
                intent.putExtra("foodIdAndCategory", foodIdAndCategory);
                intent.putExtra("foodIdAndPrice", foodIdAndPrice);
                intent.putExtra("listFood", listFood);
                intent.putExtra("sellerName", sellerName);
                startActivity(intent);
            }
        });
    }

    private void filter(String text){
        ArrayList<FoodItem> filterList = new ArrayList<>();
        for (FoodItem item : exampleList){
            if(item.getText1().toLowerCase().contains(text.toLowerCase())){
                filterList.add(item);
            }
        }
        mAdapter.filterList(filterList);
    }

    private void createExampleList(){
        Log.d(TAG, String.valueOf(listFood));
        for(Food foodPtr : listFood){
            exampleList.add(new FoodItem(R.drawable.food_icon, foodPtr.getName()+"", "Rp. "+foodPtr.getPrice()));
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
                getPosition = position;
                if(invoiceStatus.size() > 0 && invoiceStatus.get(0).equals("Ongoing")){
                    Toast.makeText(FoodActivity.this, "Wait! your order still ongoing", Toast.LENGTH_SHORT).show();
                    ViewBasket.setVisibility(View.GONE);
                }
                else{
                    ViewBasket.setVisibility(View.VISIBLE);
                    BottomSheetDialog bottomSheet = new BottomSheetDialog();
                    bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
                }
            }
        });
    }

    @Override
    public void onButtonClicked(int text) {
        foodIdAndAmount.put(getPosition, text);
        foodIdKey.put(getPosition, listFood.get(getPosition).getId());
        foodIdAndName.put(getPosition, listFood.get(getPosition).getName());
        foodIdAndCategory.put(getPosition, listFood.get(getPosition).getCategory());
        foodIdAndPrice.put(getPosition, listFood.get(getPosition).getPrice());

        Log.d(TAG, String.valueOf(foodIdAndAmount));
        Log.d(TAG, String.valueOf(foodIdAndName));
    }

    public void setTransparentStatusBarOnly(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        Intent intent= new Intent(FoodActivity.this, SellerActivity.class);
        startActivity(intent);
        finish();
    }
}
