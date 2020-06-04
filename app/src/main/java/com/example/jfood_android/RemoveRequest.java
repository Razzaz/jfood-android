package com.example.jfood_android;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RemoveRequest extends StringRequest {
    private static String URL = "http://192.168.43.61:8080/customer/delete/";
    private Map<String, String> params;

    public RemoveRequest(String id, Response.Listener<String> listener) {
        super(Method.DELETE, URL+"?id="+id, listener, null);
        params = new HashMap<>();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
