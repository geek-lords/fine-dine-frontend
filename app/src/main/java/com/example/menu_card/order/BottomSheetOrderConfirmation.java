package com.example.menu_card.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.menu_card.DB.DBHelper;
import com.example.menu_card.R;
import com.example.menu_card.checkout.checkout;
import com.example.menu_card.home.Activity_homepage;
import com.example.menu_card.home.Scanner;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static com.example.menu_card.Common.common_methods.getKey;
import static com.example.menu_card.Common.common_methods.saveTextToFile;
import static com.example.menu_card.registration.MainActivity.BASE_URL;

public class BottomSheetOrderConfirmation extends BottomSheetDialogFragment {
    MaterialButton confirm_order_btn;
    DBHelper DB;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_confirmation,container, false);

        // Create a List consisting of item_id and quantity to send to server
        JSONArray item_list = new JSONArray();

        DB = new DBHelper(getActivity());
        confirm_order_btn = view.findViewById(R.id.final_confirm_order_btn);
        progressBar = view.findViewById(R.id.progressBar_place_order);

        ScrollView scrollView = view.findViewById(R.id.scrollView_summary);
        LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(requireActivity());
        LinearLayout.LayoutParams param= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayoutCompat.setLayoutParams(param);

        String userProfileString=getArguments().getString("summary");
        int total=0;
        try {
            JSONArray summary=new JSONArray(userProfileString);
            for(int i=0; i<summary.length(); i++){
                JSONObject json = summary.getJSONObject(i);
                String item_id = json.getString("item_id");
                String name = json.getString("name");
                String quantity = json.getString("quantity");
                String price = json.getString("price");
                total += Integer.parseInt(price);

                // Fill the item list
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("menu_id", item_id);
                jsonObject.put("quantity", quantity);
                item_list.put(jsonObject);

                // Create the card
                MaterialCardView materialCardView = new MaterialCardView(requireActivity());
                LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(dpToPixel(10),dpToPixel(10),dpToPixel(10),dpToPixel(10));
                materialCardView.setRadius(dpToPixel(10));
                materialCardView.setLayoutParams(params);

                // Name
                MaterialTextView item_name = new MaterialTextView(requireActivity());
                item_name.setText(name);
                LinearLayout.LayoutParams params1= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.leftMargin = dpToPixel(50);
                params1.rightMargin = dpToPixel(230);
                params1.gravity = Gravity.START;
                Typeface typeface = ResourcesCompat.getFont(requireActivity(), R.font.poppins_medium);
                item_name.setTypeface(typeface,Typeface.BOLD);
                item_name.setLayoutParams(params1);
                item_name.setTextColor(Color.BLACK);
                item_name.setTextSize(15);

                materialCardView.addView(item_name);

                // Quantity
                MaterialTextView item_quantity = new MaterialTextView(requireActivity());
                item_quantity.setText(quantity);
                LinearLayout.LayoutParams params2= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.gravity = Gravity.CENTER_HORIZONTAL;
                params2.leftMargin = dpToPixel(170);
                Typeface typeface2 = ResourcesCompat.getFont(requireActivity(), R.font.poppins_medium);
                item_quantity.setTypeface(typeface2,Typeface.BOLD);
                item_quantity.setLayoutParams(params2);
                item_quantity.setTextColor(Color.BLACK);
                item_quantity.setTextSize(15);

                materialCardView.addView(item_quantity);

                //Price
                MaterialTextView item_price = new MaterialTextView(requireActivity());
                item_price.setText("Rs."+price);
                LinearLayout.LayoutParams params3= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params3.leftMargin = dpToPixel(300);
                Typeface typeface3 = ResourcesCompat.getFont(requireActivity(), R.font.poppins_medium);
                item_price.setTypeface(typeface3,Typeface.BOLD);
                item_price.setLayoutParams(params3);
                item_price.setTextColor(Color.BLACK);
                item_price.setTextSize(15);

                materialCardView.addView(item_price);

                linearLayoutCompat.addView(materialCardView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            scrollView.addView(linearLayoutCompat);
            MaterialTextView total_price = view.findViewById(R.id.total_price);
            total_price.setText("Rs."+total);
        }

        confirm_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_order_btn.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                try {
                    String order_id = getKey(requireActivity(), "order_id");

                    placeOrder(item_list, new Scanner.VolleyCallback() {
                        @Override
                        public void onSuccess(String result) throws JSONException {
                            progressBar.setVisibility(View.INVISIBLE);

                            for(int i=0; i<activity_make_order.summary.length(); i++){
                                JSONObject json = activity_make_order.summary.getJSONObject(i);
                                String item_id = json.getString("item_id");
                                String name = json.getString("name");
                                String quantity = json.getString("quantity");
                                String price = json.getString("price");

                                Cursor cursor = DB.getOrderItem(order_id, item_id);

                                int count = cursor.getCount();
                                if(count>0){
                                    cursor.moveToFirst();
                                    int quant = Integer.parseInt(cursor.getString(cursor.getColumnIndex("quantity")));
                                    quant += Integer.parseInt(quantity);

                                    boolean bool = DB.updateOrderInfo(order_id, item_id, name, String.valueOf(quant), price);

                                }else{
                                    DB.insertOrderInfo(order_id, item_id, name, quantity, price);
                                }

                            }

                            Iterator it = activity_make_order.hashMap.entrySet().iterator();
                            LinearLayout linearLayout = requireActivity().findViewById(R.id.linear_layout_menu);
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                String key = String.valueOf(pair.getKey());
                                MaterialTextView materialTextView = linearLayout.findViewWithTag("quantity_"+key);
                                materialTextView.setText("0");
                                it.remove();
                            }
                            activity_make_order.summary = new JSONArray();
                            confirm_order_btn.setEnabled(true);
                        }
                    });
                } catch (IOException e) {
                    String restaurant_id = requireActivity().getIntent().getStringExtra("restaurant_id");
                    String table_no = requireActivity().getIntent().getStringExtra("table_no");

                    getOrderId(restaurant_id, table_no, new Scanner.VolleyCallback() {
                        @Override
                        public void onSuccess(String order_id) throws JSONException {
                            progressBar.setVisibility(View.INVISIBLE);
                            saveTextToFile(getActivity(), "order_id", order_id);

                            placeOrder(item_list, new Scanner.VolleyCallback() {
                                @Override
                                public void onSuccess(String result) throws JSONException {
                                    for(int i=0; i<activity_make_order.summary.length(); i++){
                                        JSONObject json = activity_make_order.summary.getJSONObject(i);
                                        String item_id = json.getString("item_id");
                                        String name = json.getString("name");
                                        String quantity = json.getString("quantity");
                                        String price = json.getString("price");

                                        Cursor cursor = DB.getOrderItem(order_id, item_id);
                                        if(cursor==null || cursor.getCount()<=0){
                                            boolean bool = DB.insertOrderInfo(order_id, item_id, name, quantity, price);
                                            //Toast.makeText(getActivity(),"count less:" + bool, Toast.LENGTH_SHORT).show();
                                        }else{
                                            boolean bool = DB.updateOrderInfo(order_id, item_id, name, quantity, price);
                                            //Toast.makeText(getActivity(), "count more:" + bool, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    Iterator it = activity_make_order.hashMap.entrySet().iterator();
                                    LinearLayout linearLayout = requireActivity().findViewById(R.id.linear_layout_menu);
                                    while (it.hasNext()) {
                                        Map.Entry pair = (Map.Entry)it.next();
                                        String key = String.valueOf(pair.getKey());
                                        MaterialTextView materialTextView = linearLayout.findViewWithTag("quantity_"+key);
                                        materialTextView.setText("0");
                                        it.remove();
                                    }
                                    activity_make_order.summary = new JSONArray();
                                    confirm_order_btn.setEnabled(true);
                                }
                            });
                        }
                    });
                } catch (JSONException e) {e.printStackTrace();}

            }
        });

        return view;
    }


    public int dpToPixel(float dp) {
        float density = this.getResources().getDisplayMetrics().density;
        float pixel = dp * density;
        return (int) pixel;
    }
    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    private void getOrderId(String restaurant_id, String table_name, final Scanner.VolleyCallback callback) {
        String jwt = null;
        try {
            jwt = getKey(requireActivity(), "jwt_token");
        } catch (IOException e) {
            Intent intent = new Intent(getActivity(), com.example.menu_card.registration.MainActivity.class);
            requireActivity().finish();
            startActivity(intent);
        }

        String finalJwt = jwt;
        String url = BASE_URL+"/order?table="+table_name+"&restaurant_id="+restaurant_id;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.has("error")) {
                            try {
                                Toast.makeText(getActivity(), response.getString("error"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                saveTextToFile(getActivity(), "tax_percent", String.valueOf(response.get("tax_percent")));
                                callback.onSuccess(response.getString("order_id"));
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
                new AlertDialog.Builder(getActivity())
                        .setTitle("Error")
                        .setCancelable(true)
                        .setMessage(message)
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

    // Place order for items
    public void placeOrder(JSONArray list, final Scanner.VolleyCallback callback) throws JSONException {
        String jwt = null, order_id = null;
        try {
            jwt = getKey(requireActivity(), "jwt_token");
        } catch (IOException e) {
            Intent intent = new Intent(getActivity(), com.example.menu_card.registration.MainActivity.class);
            requireActivity().finish();
            startActivity(intent);
        }

        try {
            order_id = getKey(requireActivity(), "order_id");
        }catch (Exception e){
            e.printStackTrace();
        }

        JSONObject order_list = new JSONObject();
        order_list.put("order_list", list);

        String finalJwt = jwt;
        String url = BASE_URL+"/order_items?order_id="+order_id;

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,url, order_list,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.has("error")) {
                            try {
                                Toast.makeText(getActivity(), response.getString("error"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if(response.has("success")){
                            new AlertDialog.Builder(requireActivity())
                                    .setTitle("Order Placed")
                                    .setCancelable(false)
                                    .setMessage("Your order has been successfully placed.")
                                    .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show();
                            try {
                                //Toast.makeText(getActivity(), String.valueOf(activity_make_order.summary.length()), Toast.LENGTH_SHORT).show();
                                callback.onSuccess("success");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            dismiss();

                        }else{
                            Toast.makeText(requireActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressBar.setVisibility(View.INVISIBLE);
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
                new AlertDialog.Builder(getActivity())
                        .setTitle("Error")
                        .setCancelable(false)
                        .setMessage(message)
                        .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show();
                confirm_order_btn.setEnabled(true);
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

    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

}
