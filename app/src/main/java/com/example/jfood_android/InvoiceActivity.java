package com.example.jfood_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class InvoiceActivity extends AppCompatActivity {

    private static final String TAG = "InvoiceActivity";

    private RecyclerView mRecyclerView;
    private InvoiceAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<InvoiceItem> exampleList = new ArrayList<>();

    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> food = new ArrayList<>();

    private HashMap<String, Integer> amount = new HashMap<>();
    private HashMap<String, Integer> price = new HashMap<>();

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTransparentStatusBarOnly(InvoiceActivity.this);
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        id = getIntent().getIntExtra("id", 0);

        name = (ArrayList<String>) getIntent().getSerializableExtra("name");
        food = (ArrayList<String>) getIntent().getSerializableExtra("food");
        price = (HashMap<String, Integer>) getIntent().getSerializableExtra("price");

        String code = getIntent().getStringExtra("code");
        String date = getIntent().getStringExtra("date");
        String pay = getIntent().getStringExtra("pay");
        String status = getIntent().getStringExtra("status");
        Boolean active = getIntent().getBooleanExtra("active", false);
        int discount = getIntent().getIntExtra("discount", 0);
        int total = getIntent().getIntExtra("total", 0);

        TextView invoiceDate = findViewById(R.id.date);
        invoiceDate.setText(date);

        final TextView invoiceStatus = findViewById(R.id.status);
        invoiceStatus.setText(status);

        TextView invoiceId = findViewById(R.id.id);
        invoiceId.setText("#"+id);

        TextView invoicePay = findViewById(R.id.pay);
        if(code == null || !active){
            invoicePay.setText(pay);
        }
        else{
            invoicePay.setText(pay+" ("+code+")");
        }

        TextView invoiceDiscount = findViewById(R.id.discount);
        if(!active){
            invoiceDiscount.setText("- Rp. 0");
        }
        else{
            invoiceDiscount.setText("- Rp. "+(discount));
        }

        TextView invoiceTotal = findViewById(R.id.total);
        invoiceTotal.setText("Rp. "+total);

        int count = 0;
        for(String namePtr : name){
            for(String foodPtr : food){
                if(namePtr.equals(foodPtr)){
                    count++;
                    amount.put(namePtr, count);
                }
            }
            count = 0;
        }

        createExampleList();
        buildExampleList();

        final TextView finish = findViewById(R.id.finish);
        final TextView cancel = findViewById(R.id.cancel);
        final TextView done = findViewById(R.id.done);

        done.setVisibility(View.GONE);

        if(status.equals("Finished") || status.equals("Cancelled")){
            done.setVisibility(View.VISIBLE);
            finish.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
        }

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                done.setVisibility(View.VISIBLE);
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            invoiceStatus.setText("Finished");
                            Toast.makeText(InvoiceActivity.this, "Invoice change successful", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(InvoiceActivity.this, "Invoice change failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                PesananSelesaiRequest selesaiRequest = new PesananSelesaiRequest(id+"", responseListener);
                RequestQueue queue = Volley.newRequestQueue(InvoiceActivity.this);
                queue.add(selesaiRequest);
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                done.setVisibility(View.VISIBLE);
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            invoiceStatus.setText("Cancelled");
                            Toast.makeText(InvoiceActivity.this, "Invoice change successful", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(InvoiceActivity.this, "Invoice change failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                PesananBatalRequest batalRequest = new PesananBatalRequest(id+"", responseListener);
                RequestQueue queue = Volley.newRequestQueue(InvoiceActivity.this);
                queue.add(batalRequest);
            }
        });
    }

    private void createExampleList(){
        for(String eat : amount.keySet()){
            exampleList.add(new InvoiceItem(amount.get(eat)+"x", eat, "Rp. "+price.get(eat)));
        }
    }

    private void buildExampleList(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new InvoiceAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new InvoiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(InvoiceActivity.this, position+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(InvoiceActivity.this, HistoryActivity.class));
        finish();
    }

    public void setTransparentStatusBarOnly(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}
