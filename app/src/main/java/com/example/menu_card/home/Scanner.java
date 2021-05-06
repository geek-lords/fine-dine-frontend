package com.example.menu_card.home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.menu_card.R;
import com.google.zxing.Result;

import org.json.JSONException;

import static com.example.menu_card.registration.MainActivity.BASE_URL;

public class Scanner extends AppCompatActivity {

    public interface VolleyCallback{
        void onSuccess(String result) throws JSONException;
    }

    private CodeScanner mCodeScanner;

    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(Scanner.this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Sample restaurant_id and table no
                        String restaurant_id = "1";
                        String table_no = "1";
                        Toast.makeText(Scanner.this, "Sample data:\nrestaurant_id = 1\ntable_no = 1", Toast.LENGTH_SHORT).show();

                        getMenu(restaurant_id, new VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                Intent intent = new Intent(Scanner.this, com.example.menu_card.order.activity_make_order.class);
                                intent.putExtra("menu", result);
                                intent.putExtra("restaurant_id", restaurant_id);
                                intent.putExtra("table_no", table_no);
                                startActivity(intent);
                            }
                        });

                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }else {
            //mCodeScanner.startPreview();
        }
        String restaurant_id = "1";
        String table_no = "1";
        Toast.makeText(Scanner.this, "Sample data:\nrestaurant_id = 1\ntable_no = 1", Toast.LENGTH_SHORT).show();

        getMenu(restaurant_id, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Intent intent = new Intent(Scanner.this, com.example.menu_card.order.activity_make_order.class);
                intent.putExtra("menu", result);
                intent.putExtra("restaurant_id", restaurant_id);
                intent.putExtra("table_no", table_no);
                startActivity(intent);
            }
        });
    }

    private void getMenu(String restaurant_id, final VolleyCallback callback) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = BASE_URL+"/menu?restaurant_id="+restaurant_id;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
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
                new AlertDialog.Builder(Scanner.this)
                        .setTitle("Couldn't fetch menu")
                        .setCancelable(true)
                        .setMessage(message)
                        .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please allow camera permission.", Toast.LENGTH_LONG).show();
            }else
                mCodeScanner.startPreview();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}