package com.example.menu_card.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.example.menu_card.Common.common_methods;
import com.example.menu_card.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            // Get the JSON data from the result
            JSONObject rest_info;
            String restaurant_id, table_no;

            try {
                rest_info = new JSONObject(result.getText());
                restaurant_id = rest_info.getString("restaurant_id");
                table_no = URLEncoder.encode(rest_info.getString("table"), StandardCharsets.UTF_8.toString());

                getMenu(restaurant_id, result1 -> {
                    Intent intent = new Intent(Scanner.this, com.example.menu_card.order.activity_make_order.class);
                    intent.putExtra("menu", result1);
                    intent.putExtra("restaurant_id", restaurant_id);
                    intent.putExtra("table_no", table_no);
                    startActivity(intent);
                });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }));
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }else {
            mCodeScanner.startPreview();
        }
        String restaurant_id = "1";
        String table_no = "1";
        Toast.makeText(Scanner.this, "Sample data:\nrestaurant_id = 1\ntable_no = 1", Toast.LENGTH_SHORT).show();

        getMenu(restaurant_id, result -> {
            Intent intent = new Intent(Scanner.this, com.example.menu_card.order.activity_make_order.class);
            intent.putExtra("menu", result);
            intent.putExtra("restaurant_id", restaurant_id);
            intent.putExtra("table_no", table_no);
            startActivity(intent);
        });
    }

    private void getMenu(String restaurant_id, final VolleyCallback callback) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = BASE_URL+"/menu?restaurant_id="+restaurant_id;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        callback.onSuccess(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, volleyError -> new AlertDialog.Builder(Scanner.this)
                        .setTitle("Couldn't fetch menu")
                        .setCancelable(true)
                        .setMessage(common_methods._print_server_response_error(volleyError))
                        .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show());
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