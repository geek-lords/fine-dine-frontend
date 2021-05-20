package com.example.menu_card.order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.menu_card.Common.common_methods;
import com.example.menu_card.DB.DBHelper;
import com.example.menu_card.R;
import com.example.menu_card.home.Scanner;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

        assert getArguments() != null;
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

        confirm_order_btn.setOnClickListener(v -> {
            confirm_order_btn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            try {
                String order_id = getKey(requireActivity(), "order_id");

                placeOrder(item_list, result -> {
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

                            if(!DB.updateOrderInfo(order_id, item_id, name, String.valueOf(quant), price))
                                Toast.makeText(requireActivity(), "Error updating in local DB", Toast.LENGTH_SHORT).show();

                        }else{
                            DB.insertOrderInfo(order_id, item_id, name, quantity, price);
                        }

                    }

                    Iterator<Map.Entry<String, String>> it = activity_make_order.hashMap.entrySet().iterator();
                    LinearLayout linearLayout = requireActivity().findViewById(R.id.linear_layout_menu);
                    while (it.hasNext()) {
                        Map.Entry<String, String> pair = it.next();
                        String key = String.valueOf(pair.getKey());
                        MaterialTextView materialTextView = linearLayout.findViewWithTag("quantity_"+key);
                        materialTextView.setText("0");
                        it.remove();
                    }
                    activity_make_order.summary = new JSONArray();
                    confirm_order_btn.setEnabled(true);
                });
            } catch (IOException e) {
                String restaurant_id = requireActivity().getIntent().getStringExtra("restaurant_id");
                String table_no = requireActivity().getIntent().getStringExtra("table_no");

                getOrderId(restaurant_id, table_no, order_id -> {
                    saveTextToFile(requireActivity(), "order_id", order_id);

                    placeOrder(item_list, result -> {
                        for(int i=0; i<activity_make_order.summary.length(); i++){
                            JSONObject json = activity_make_order.summary.getJSONObject(i);
                            String item_id = json.getString("item_id");
                            String name = json.getString("name");
                            String quantity = json.getString("quantity");
                            String price = json.getString("price");

                            Cursor cursor = DB.getOrderItem(order_id, item_id);
                            if(cursor==null || cursor.getCount()<=0){
                                if(!DB.insertOrderInfo(order_id, item_id, name, quantity, price))
                                    Toast.makeText(getActivity(),"Error inserting in local DB", Toast.LENGTH_SHORT).show();
                            }else{
                               if(!DB.updateOrderInfo(order_id, item_id, name, quantity, price))
                                    Toast.makeText(getActivity(), "Error updating in local DB", Toast.LENGTH_SHORT).show();
                            }

                        }
                        Iterator<Map.Entry<String, String>> it = activity_make_order.hashMap.entrySet().iterator();
                        LinearLayout linearLayout = requireActivity().findViewById(R.id.linear_layout_menu);
                        while (it.hasNext()) {
                            Map.Entry<String, String> pair = it.next();
                            String key = String.valueOf(pair.getKey());
                            MaterialTextView materialTextView = linearLayout.findViewWithTag("quantity_"+key);
                            materialTextView.setText("0");
                            it.remove();
                        }
                        activity_make_order.summary = new JSONArray();
                        confirm_order_btn.setEnabled(true);
                    });
                });
            } catch (JSONException e) {e.printStackTrace();}

        });

        return view;
    }


    public int dpToPixel(float dp) {
        float density = this.getResources().getDisplayMetrics().density;
        float pixel = dp * density;
        return (int) pixel;
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
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,url, null,
                response -> {
                    if(response.has("error")) {
                        try {
                            Toast.makeText(getActivity(), response.getString("error"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            common_methods.saveTextToFile(requireActivity(), "restaurant_id", restaurant_id);
                            common_methods.saveTextToFile(requireActivity(), "table_no", table_name);
                            common_methods.saveTextToFile(requireActivity(), "tax_percent", String.valueOf(response.get("tax_percent")));
                            callback.onSuccess(response.getString("order_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, volleyError -> {
                    System.out.println(volleyError.toString());
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Error")
                            .setCancelable(true)
                            .setMessage(common_methods._print_server_response_error(volleyError))
                            .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show();
                }) {
            //Passing some request headers
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
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
                response -> {
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
                            callback.onSuccess("success");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        dismiss();

                    }else{
                        Toast.makeText(requireActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, volleyError -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Error")
                            .setCancelable(false)
                            .setMessage(common_methods._print_server_response_error(volleyError))
                            .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show();
                    confirm_order_btn.setEnabled(true);
                }) {
            //Passing some request headers
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
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