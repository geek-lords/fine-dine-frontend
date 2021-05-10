package com.example.menu_card.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.menu_card.R;
import com.example.menu_card.home.Activity_homepage;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
            Toast.makeText(getApplicationContext(), getKey(getApplicationContext(), "jwt_token"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Credentials can't be empty", Toast.LENGTH_SHORT).show();
                    isSubmit = false;
                    return;
                }
                if (!isEmailValid(email)) {
                    Toast.makeText(getApplicationContext(), "Email is invalid", Toast.LENGTH_SHORT).show();
                    isSubmit = false;
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                // Sign user in through api
                String url = BASE_URL + "/authenticate";

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("email", email);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                        Request.Method.POST, url, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressBar.setVisibility(View.INVISIBLE);
                                if (response.has("error")) {
                                    try {
                                        Toast.makeText(getApplicationContext(), response.getString("error"), Toast.LENGTH_SHORT).show();
                                        isSubmit = false;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        saveTextToFile(getApplicationContext(), "jwt_token", response.getString("jwt_token"));

                                        Intent mainIntent = new Intent(getApplicationContext(), Activity_homepage.class);
                                        startActivity(mainIntent);
                                        finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println(volleyError.toString());
                        String message = null;
                        if (volleyError instanceof NetworkError) {
                            message = "Cannot connect to Internet. Please check your connection";
                        } else if (volleyError instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time";
                        } else if (volleyError instanceof AuthFailureError) {
                            message = "Cannot connect to Internet. Please check your connection";
                        } else if (volleyError instanceof ParseError) {
                            message = "Parsing error! Please try again after some time";
                        } else if (volleyError instanceof NoConnectionError) {
                            message = "Cannot connect to Internet. Please check your connection";
                        } else if (volleyError instanceof TimeoutError) {
                            message = "Connection TimeOut. Please check your internet connection.";
                        }
                        new AlertDialog.Builder(getApplicationContext())
                                .setTitle("Error")
                                .setCancelable(false)
                                .setMessage(message)
                                .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show();
                        isSubmit = false;
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }) {
                    //Passing some request headers
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };
                requestQueue.add(jsonObjReq);
            }
        });

      sign_up_btn = findViewById(R.id.sign_up_redirect);
        sign_up_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), activitySignUp.class);
            startActivity(intent);
        });

    }

    public static boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static void saveTextToFile(Context context, String filename, String content) {
        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getKey(Context context, String filename) throws IOException {
        File file = new File(context.getFilesDir(), filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }
}
