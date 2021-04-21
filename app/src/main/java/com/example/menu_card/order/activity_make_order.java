package com.example.menu_card.order;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.menu_card.R;
import com.example.menu_card.home.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class activity_make_order extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);

        LinearLayout linearLayout = findViewById(R.id.linear_layout_menu);

        String menu = getIntent().getStringExtra("menu");
        try {
            JSONObject jsonObject = new JSONObject(menu);
            JSONArray jsonArray = jsonObject.getJSONArray("menu");

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                String id = obj.getString("id");
                String name = obj.getString("name");
                String description = obj.getString("description");
                String photo_url = obj.getString("photo_url");
                String price = obj.getString("price");


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
