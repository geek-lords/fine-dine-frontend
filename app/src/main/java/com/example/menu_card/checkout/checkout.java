package com.example.menu_card.checkout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.menu_card.Common.common_methods;
import com.example.menu_card.DB.DBHelper;
import com.example.menu_card.R;
import com.example.menu_card.home.Activity_homepage;
import com.example.menu_card.order.activity_make_order;
import com.example.menu_card.payment.PaymentActivity;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;
import java.text.DecimalFormat;

import static com.example.menu_card.Common.common_methods.getKey;

public class checkout extends AppCompatActivity {

    private static final int paymentRequest = 1;

    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Set the restaurant name
        MaterialTextView rest_name = findViewById(R.id.checkout_rest_name);
        rest_name.setText(activity_make_order.restaurant_name);

        // Create the card
        LinearLayout linearLayout = findViewById(R.id.linear_bill_summary);
        Button paytm;

        DBHelper DB = new DBHelper(this);
        String order_id = null;
        int total = 0;

        try {
            order_id = getKey(this, "order_id").trim();
            Cursor cursor = DB.getAllOrderItems(order_id);


            if (cursor.moveToFirst()) {
                do {
                    LinearLayout row = new LinearLayout(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    row.setLayoutParams(params);
                    row.setWeightSum(3);

                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                    String price = cursor.getString(cursor.getColumnIndex("price"));

                    // Name
                    MaterialTextView item_name = new MaterialTextView(this);
                    item_name.setText(name);
                    LinearLayout.LayoutParams params1= new LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT);

                    Typeface typeface = ResourcesCompat.getFont(this, R.font.poppins_medium);
                    item_name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    item_name.setTypeface(typeface,Typeface.BOLD);
                    item_name.setLayoutParams(params1);
                    item_name.setTextColor(Color.BLACK);
                    item_name.setTextSize(18);

                    row.addView(item_name);

                    // Quantity
                    MaterialTextView item_quantity = new MaterialTextView(this);
                    item_quantity.setText(quantity);
                    LinearLayout.LayoutParams params2= new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params2.leftMargin = 10;
                    Typeface typeface2 = ResourcesCompat.getFont(this, R.font.poppins_medium);
                    item_quantity.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    item_quantity.setTypeface(typeface2,Typeface.BOLD);
                    item_quantity.setLayoutParams(params2);
                    item_quantity.setTextColor(Color.BLACK);
                    item_quantity.setTextSize(18);

                    row.addView(item_quantity);

                    //Price
                    MaterialTextView item_price = new MaterialTextView(this);

                    total += Integer.parseInt(price);

                    item_price.setText("Rs."+price);
                    LinearLayout.LayoutParams params3= new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //params3.weight = 1;
                    Typeface typeface3 = ResourcesCompat.getFont(this, R.font.poppins_medium);
                    item_price.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    item_price.setTypeface(typeface3,Typeface.BOLD);
                    item_price.setLayoutParams(params3);
                    item_price.setTextColor(Color.BLACK);
                    item_price.setTextSize(18);

                    row.addView(item_price);

                    linearLayout.addView(row);
                } while (cursor.moveToNext());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        float tax_per = 0;
        try {
            tax_per = Float.parseFloat(getKey(this, "tax_percent"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        float total_tax = (tax_per/100)*total;
        float total_price = total_tax+total;


        MaterialTextView price_before_tax = findViewById(R.id.price_before_tax);
        price_before_tax.setText("Rs."+total);

        MaterialTextView gst_per = findViewById(R.id.gst_per);
        gst_per.setText("GST("+tax_per+"%)");

        MaterialTextView gst_price = findViewById(R.id.gst_price);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        gst_price.setText("Rs."+df.format(total_tax));

        MaterialTextView total_amount = findViewById(R.id.total_bill_amount);
        total_amount.setText("Rs."+df.format(total_price));

        // Checkout using paytm
        paytm = findViewById(R.id.paytm);

        String finalOrder_id = order_id;
        paytm.setOnClickListener(v -> {
            Intent intent = new Intent(checkout.this, PaymentActivity.class);
            intent.putExtra("orderID", finalOrder_id);
            startActivityForResult(intent, paymentRequest);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==paymentRequest){
            if (resultCode==RESULT_OK){
                try {
                    if(common_methods._clear_order_details(checkout.this)){
                        new AlertDialog.Builder(checkout.this)
                                .setTitle("Payment Successful")
                                .setCancelable(true)
                                .setMessage("Payment completed successfully. Please visit again!")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.cancel();
                                    Intent i = new Intent(checkout.this, Activity_homepage.class);
                                    startActivity(i);
                                    finish();
                                }).show();
                    }else
                        new AlertDialog.Builder(checkout.this)
                                .setTitle("Error")
                                .setCancelable(true)
                                .setMessage("Error deleting user details after payment. Check logs.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                new AlertDialog.Builder(checkout.this)
                        .setTitle("Payment failure")
                        .setCancelable(true)
                        .setMessage("Payment could not be completed. Please try again!")
                        .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show();
            }
        }
    }
}