package com.example.menu_card.registration;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.menu_card.Common.common_methods;
import com.example.menu_card.R;
import com.example.menu_card.home.Activity_homepage;
import com.example.menu_card.home.Scanner;

import org.json.JSONException;

import java.io.IOException;

import static com.example.menu_card.Common.common_methods.getKey;
import static com.example.menu_card.registration.MainActivity.BASE_URL;

public class SplashScreen extends AppCompatActivity {
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar = findViewById(R.id.progressBar_splashScreen);

        int SPLASH_DISPLAY_LENGTH = 2450;
        new Handler().postDelayed(() -> {
            try {
                common_methods.getKey(SplashScreen.this, "order_id");
                String restaurant_id = common_methods.getKey(SplashScreen.this, "restaurant_id");
                String table_no = common_methods.getKey(SplashScreen.this, "table_no");

                progressBar.setVisibility(View.VISIBLE);
                getMenu(restaurant_id, result1 -> {
                    Intent intent = new Intent(SplashScreen.this, com.example.menu_card.order.activity_make_order.class);
                    intent.putExtra("menu", result1);
                    intent.putExtra("restaurant_id", restaurant_id);
                    intent.putExtra("table_no", table_no);
                    startActivity(intent);
                });
            }catch (IOException e){

                try {
                    getKey(SplashScreen.this, "jwt_token");

                    Intent mainIntent = new Intent(SplashScreen.this, Activity_homepage.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                } catch (IOException ioException) {

                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void getMenu(String restaurant_id, final Scanner.VolleyCallback callback) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = BASE_URL+"/menu?restaurant_id="+restaurant_id;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    try {
                        callback.onSuccess(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, volleyError -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    new AlertDialog.Builder(SplashScreen.this)
                            .setTitle("Couldn't fetch menu")
                            .setCancelable(true)
                            .setMessage(common_methods._print_server_response_error(volleyError))
                            .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show();
                });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}

