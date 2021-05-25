package com.example.menu_card.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.menu_card.Common.common_methods;
import com.example.menu_card.R;
import com.example.menu_card.home.Activity_homepage;
import com.example.menu_card.home.Scanner;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.menu_card.Common.common_methods._delete_file_if_exists;
import static com.example.menu_card.Common.common_methods.getKey;
import static com.example.menu_card.Common.common_methods.saveTextToFile;
import static com.example.menu_card.registration.MainActivity.BASE_URL;

public class activitySignIn extends AppCompatActivity {
    MaterialTextView sign_up_btn;
    Button sign_in_btn;
    static boolean isSubmit = false;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        try {
            Toast.makeText(activitySignIn.this, getKey(activitySignIn.this, "jwt_token"), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.progressBar = findViewById(R.id.progressBar_signin);

        sign_in_btn = findViewById(R.id.confirm_button_signin);
        sign_in_btn.setOnClickListener(v -> {
            if (!isSubmit) {
                isSubmit = true;

                TextView email_et = findViewById(R.id.email_sign_in);
                String email = email_et.getText().toString();

                TextView pass_et = findViewById(R.id.password_sign_in);
                String password = pass_et.getText().toString();

                if (email.trim().equals("") || password.trim().equals("")) {
                    Toast.makeText(activitySignIn.this, "Credentials can't be empty", Toast.LENGTH_SHORT).show();
                    isSubmit = false;
                    return;
                }
                if (!common_methods.isEmailValid(email)) {
                    Toast.makeText(activitySignIn.this, "Email is invalid", Toast.LENGTH_SHORT).show();
                    isSubmit = false;
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(activitySignIn.this, "Password length should be at least 5 characters", Toast.LENGTH_SHORT).show();
                    isSubmit = false;
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                // Sign user in through api
                signIn(email, password, result -> {

                    try {
                        _delete_file_if_exists(activitySignIn.this, "jwt_token");
                        _delete_file_if_exists(activitySignIn.this, "order_id");

                        JSONObject response = new JSONObject(result);
                        //Save JWT to system
                        saveTextToFile(activitySignIn.this, "jwt_token", response.getString("jwt_token"));

                        Intent mainIntent = new Intent(activitySignIn.this, Activity_homepage.class);
                        startActivity(mainIntent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

            }
        });

      sign_up_btn = findViewById(R.id.sign_up_redirect);
        sign_up_btn.setOnClickListener(v -> {
            Intent intent = new Intent(activitySignIn.this, activitySignUp.class);
            startActivity(intent);
        });

    }

    public void signIn(String email, String password, Scanner.VolleyCallback callback){
        String url = BASE_URL + "/authenticate";

        RequestQueue requestQueue = Volley.newRequestQueue(activitySignIn.this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                response -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (response.has("error")) {
                        try {
                            Toast.makeText(activitySignIn.this, response.getString("error"), Toast.LENGTH_SHORT).show();
                            isSubmit = false;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            callback.onSuccess(response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, volleyError -> {
                    new AlertDialog.Builder(activitySignIn.this)
                            .setTitle("Error")
                            .setCancelable(false)
                            .setMessage(common_methods._print_server_response_error(volleyError))
                            .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show();
                    isSubmit = false;
                    progressBar.setVisibility(View.INVISIBLE);
                }) {
            //Passing some request headers
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(jsonObjReq);
    }

}
