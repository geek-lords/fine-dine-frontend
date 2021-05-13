package com.example.menu_card.home;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.menu_card.Common.common_methods;
import com.example.menu_card.R;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static com.example.menu_card.Common.common_methods.getKey;
import static com.example.menu_card.registration.MainActivity.BASE_URL;

public class detailed_recent_order extends AppCompatActivity {
    ProgressBar progressBar;
    LinearLayout linearLayout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_recent_order);

        JSONObject order_info = null;
        linearLayout = findViewById(R.id.detailed_linearLayout_bill);
        progressBar = findViewById(R.id.progressBar_detailed);
        progressBar.setVisibility(View.VISIBLE);


        try {
            order_info = new JSONObject(getIntent().getStringExtra("order_info"));
            JSONObject finalOrder_info = order_info;

            MaterialTextView hotel_name = findViewById(R.id.hotel_name_detailed_order);
            hotel_name.setText(order_info.getString("name"));

            getDetailedRecentOrder(order_info.getString("id"), new Scanner.VolleyCallback() {
                @Override
                public void onSuccess(String result) throws JSONException {

                    JSONArray bill = new JSONObject(result).getJSONArray("bill");

                    float total = 0;

                    for(int i=0; i<bill.length(); i++){
                        JSONObject order  = bill.getJSONObject(i);


                        LinearLayout row = new LinearLayout(detailed_recent_order.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        row.setLayoutParams(params);
                        row.setWeightSum(3);

                        String name = order.getString(("name"));
                        String quantity = order.getString("quantity");
                        String price = order.getString("price");
                        price = String.valueOf(Float.parseFloat(price) * Integer.parseInt(quantity));

                        // Name
                        MaterialTextView item_name = new MaterialTextView(detailed_recent_order.this);
                        item_name.setText(name);
                        LinearLayout.LayoutParams params1= new LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT);

                        Typeface typeface = ResourcesCompat.getFont(detailed_recent_order.this, R.font.poppins_medium);
                        item_name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        item_name.setTypeface(typeface,Typeface.BOLD);
                        item_name.setLayoutParams(params1);
                        item_name.setTextColor(Color.BLACK);
                        item_name.setTextSize(18);

                        row.addView(item_name);

                        // Quantity
                        MaterialTextView item_quantity = new MaterialTextView(detailed_recent_order.this);
                        item_quantity.setText(quantity);
                        LinearLayout.LayoutParams params2= new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params2.leftMargin = 10;
                        Typeface typeface2 = ResourcesCompat.getFont(detailed_recent_order.this, R.font.poppins_medium);
                        item_quantity.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        item_quantity.setTypeface(typeface2,Typeface.BOLD);
                        item_quantity.setLayoutParams(params2);
                        item_quantity.setTextColor(Color.BLACK);
                        item_quantity.setTextSize(18);

                        row.addView(item_quantity);

                        //Price
                        MaterialTextView item_price = new MaterialTextView(detailed_recent_order.this);

                        total += Float.parseFloat(price);

                        item_price.setText("Rs."+price);
                        LinearLayout.LayoutParams params3= new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT);
                        //params3.weight = 1;
                        Typeface typeface3 = ResourcesCompat.getFont(detailed_recent_order.this, R.font.poppins_medium);
                        item_price.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        item_price.setTypeface(typeface3,Typeface.BOLD);
                        item_price.setLayoutParams(params3);
                        item_price.setTextColor(Color.BLACK);
                        item_price.setTextSize(18);

                        row.addView(item_price);

                        linearLayout.addView(row);

                    }

                    float tax_per = Float.parseFloat(finalOrder_info.getString("tax_percent"));
                    float total_tax = (tax_per/100)*total;
                    float total_price = total_tax+total;

                    //Toast.makeText(this, ""+tax_per+"\n"+total_tax+"\n"+total_price, Toast.LENGTH_SHORT).show();

                    MaterialTextView price_before_tax = findViewById(R.id.detailed_price_before_tax);
                    price_before_tax.setText("Rs."+total);

                    MaterialTextView gst_per = findViewById(R.id.detailed_gst_per);
                    gst_per.setText("GST("+tax_per+"%)");

                    MaterialTextView gst_price = findViewById(R.id.detailed_gst_price);
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    gst_price.setText("Rs."+df.format(total_tax));

                    MaterialTextView total_amount = findViewById(R.id.detailed_total_bill_amount);
                    total_amount.setText("Rs."+df.format(total_price));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDetailedRecentOrder(String order_id, Scanner.VolleyCallback callback) throws JSONException {
        String jwt = null;
        try {
            jwt = getKey(detailed_recent_order.this, "jwt_token");
        } catch (IOException e) {
            Intent intent = new Intent(detailed_recent_order.this, com.example.menu_card.registration.MainActivity.class);
            finish();
            startActivity(intent);
        }

        String finalJwt = jwt;
        String url = BASE_URL+"/order_history/"+order_id;
        RequestQueue requestQueue = Volley.newRequestQueue(detailed_recent_order.this);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if(response.has("error")) {
                            try {
                                Toast.makeText(detailed_recent_order.this, response.getString("error"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                callback.onSuccess(response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressBar.setVisibility(View.INVISIBLE);
                new AlertDialog.Builder(detailed_recent_order.this)
                        .setTitle("Error")
                        .setCancelable(true)
                        .setMessage(common_methods._print_server_response_error(volleyError))
                        .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show();
            }
        }) {
            //Passing some request headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("X-Auth-Token", finalJwt);
                System.out.println(finalJwt);
                return headers;
            }
        };
        requestQueue.add(jsonObjReq);
    }

}