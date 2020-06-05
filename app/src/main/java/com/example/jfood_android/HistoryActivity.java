package com.example.jfood_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.example.jfood_android.LoginActivity.SHARED_PREFS;
import static com.example.jfood_android.LoginActivity.userState;

public class HistoryActivity extends AppCompatActivity implements Serializable {

    private static final String TAG = "HistoryActivity";

    private RecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<HistoryItem> exampleList = new ArrayList<>();

    private ArrayList<Seller> listSeller = new ArrayList<>();
    private ArrayList<Food> foodIdList = new ArrayList<>();

    private Set<String> tempFoodUniqueName = new HashSet<>();
    private ArrayList<Integer> invoiceUniqueId = new ArrayList<>();

    private HashMap<Integer, ArrayList<String>> invoiceIdAndFood = new HashMap<>();
    private HashMap<Integer, String> invoiceIdAndDate = new HashMap<>();
    private HashMap<Integer, Boolean> invoiceIdAndActive = new HashMap<>();
    private HashMap<Integer, String> invoiceIdAndStatus = new HashMap<>();
    private HashMap<Integer, String> invoiceIdAndCode = new HashMap<Integer, String>();
    private HashMap<Integer, Integer> invoiceIdAndDiscount = new HashMap<Integer, Integer>();
    private HashMap<Integer, String> invoiceIdAndPay = new HashMap<Integer, String>();
    private HashMap<Integer, String> invoiceIdAndSeller = new HashMap<Integer, String>();
    private HashMap<Integer, Integer> invoiceIdAndTotal = new HashMap<Integer, Integer>();
    private HashMap<String, Integer> invoiceFoodAndPrice = new HashMap<>();

    private int userId;
    private int j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTransparentStatusBarOnly(HistoryActivity.this);
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt(userState, 0);

        exampleList.clear();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    for (int i=0; i < jsonResponse.length(); i++) {
                        ArrayList<String> foodId = new ArrayList<>();
                        JSONObject invoice = jsonResponse.getJSONObject(i);
                        JSONArray foods = invoice.getJSONArray("foods");
                        Log.d(TAG, String.valueOf(foods.length()));
                        for (j = 0; j < foods.length(); j++) {
                            JSONObject food = foods.getJSONObject(j);

                            foodId.add(food.getString("name"));

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
                            tempFoodUniqueName.add(food.getString("name"));
                            invoiceFoodAndPrice.put(food.getString("name"), food.getInt("price"));
                            invoiceIdAndSeller.put(invoice.getInt("id"), seller.getString("name"));

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

                        invoiceIdAndFood.put(invoice.getInt("id"), foodId);

                        invoiceUniqueId.add(invoice.getInt("id"));

                        invoiceIdAndDate.put(invoice.getInt("id"), invoice.getString("date").substring(0, 10));
                        invoiceIdAndStatus.put(invoice.getInt("id"), invoice.getString("invoiceStatus"));

                        invoiceIdAndPay.put(invoice.getInt("id"), invoice.getString("paymentType"));
                        invoiceIdAndTotal.put(invoice.getInt("id"), invoice.getInt("totalPrice"));

                        if(invoice.getString("paymentType").equals("Cashless")){
                            JSONObject promo = invoice.getJSONObject("promo");
                            invoiceIdAndActive.put(invoice.getInt("id"), promo.getBoolean("active"));
                            invoiceIdAndCode.put(invoice.getInt("id"), promo.getString("code"));
                            invoiceIdAndDiscount.put(invoice.getInt("id"), promo.getInt("discount"));
                        }

                    }

                    for(int id = 0; id < invoiceIdAndSeller.size(); id++){
                        exampleList.add(id, new HistoryItem(R.drawable.ic_bill, invoiceIdAndSeller.get(invoiceIdAndSeller.size()-id), invoiceIdAndDate.get(invoiceIdAndSeller.size()-id).substring(0, 10)+""));
                        mAdapter.notifyItemInserted(id);
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        };

        PesananFetchRequest fetchRequest = new PesananFetchRequest(userId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(HistoryActivity.this);
        queue.add(fetchRequest);

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

    private void buildExampleList(){

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new HistoryAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int requestInvoiceId = invoiceUniqueId.size()+1 - invoiceUniqueId.get(position);
                if(invoiceIdAndCode.get(position) == null){
                    invoiceIdAndCode.put(position, "");
                    invoiceIdAndDiscount.put(position, 0);
                }

                ArrayList<String> foodName = new ArrayList<>();
                for(String name : tempFoodUniqueName){
                   foodName.add(name);
                }
                Log.d(TAG, String.valueOf(foodName));

                Intent intent = new Intent(HistoryActivity.this, InvoiceActivity.class);
                intent.putExtra("id", requestInvoiceId);
                intent.putExtra("name", foodName);
                intent.putExtra("food", invoiceIdAndFood.get(requestInvoiceId));
                intent.putExtra("price", invoiceFoodAndPrice);
                intent.putExtra("active", invoiceIdAndActive.get(requestInvoiceId));
                intent.putExtra("total", invoiceIdAndTotal.get(requestInvoiceId));
                intent.putExtra("code", invoiceIdAndCode.get(requestInvoiceId));
                intent.putExtra("date", invoiceIdAndDate.get(requestInvoiceId));
                intent.putExtra("pay", invoiceIdAndPay.get(requestInvoiceId));
                intent.putExtra("status", invoiceIdAndStatus.get(requestInvoiceId));
                intent.putExtra("discount", invoiceIdAndDiscount.get(requestInvoiceId));
                startActivity(intent);
                finish();
            }
        });

    }

    private void filter(String text){
        ArrayList<HistoryItem> filterList = new ArrayList<>();
        for (HistoryItem item : exampleList){
            if(item.getText1().toLowerCase().contains(text.toLowerCase()) || item.getText2().toLowerCase().contains((text.toLowerCase()))){
                filterList.add(item);
            }
        }
        mAdapter.filterList(filterList);
    }

    public void setTransparentStatusBarOnly(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
