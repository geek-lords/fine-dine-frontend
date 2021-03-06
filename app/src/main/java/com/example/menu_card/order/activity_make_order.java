package com.example.menu_card.order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.menu_card.Common.common_methods;
import com.example.menu_card.DB.DBHelper;
import com.example.menu_card.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.menu_card.Common.common_methods.getKey;

public class activity_make_order extends AppCompatActivity {

    private final boolean confirmOrder = false;
    MaterialButton confirm;
    MaterialButton checkout;
    //Creating HashMap for keeping record of the items and their quantity
    static HashMap<String,String> hashMap = new HashMap<>();
    static JSONArray summary;
    public static String restaurant_name;

    @Override
    public void onBackPressed() {
        try {
            getKey(activity_make_order.this, "order_id");
            new AlertDialog.Builder(activity_make_order.this)
                    .setTitle("Exit Application?")
                    .setCancelable(true)
                    .setMessage("Do you want to exit the app?")
                    .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                    .setPositiveButton("Yes", (dialog, which) -> finishAffinity()).show();
        } catch (IOException ignored) {
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);

         confirm = findViewById(R.id.confirm);
         checkout = findViewById(R.id.checkout);
         DBHelper DB = new DBHelper(this);

        LinearLayout linearLayout = findViewById(R.id.linear_layout_menu);
        linearLayout.setElevation(dpToPixel(10));

        String menu = getIntent().getStringExtra("menu");

        try {

            JSONObject jsonObject = new JSONObject(menu);
            restaurant_name = jsonObject.getString("restaurant");
            MaterialTextView restaurant = findViewById(R.id.rest_name);
            restaurant.setText(restaurant_name);
            JSONArray jsonArray = jsonObject.getJSONArray("menu");

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                String id = obj.getString("id");
                String name = obj.getString("name");
                String description = obj.getString("description");
                String photo_url = obj.getString("photo_url");
                String price = String.valueOf((int)Double.parseDouble(obj.getString("price")));

                // Initializing this item id as 0 quantity by default
                //hashMap.put(id, "0");

                MaterialCardView cardView = new MaterialCardView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                layoutParams.setMargins(dpToPixel(18),dpToPixel(5),dpToPixel(18),dpToPixel(5));
                cardView.setLayoutParams(layoutParams);

                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(dpToPixel(20));
                cardView.setBackgroundDrawable(shape);

                cardView.setBackgroundColor(Color.WHITE);
                cardView.setUseCompatPadding(true);
                cardView.setForeground(Drawable.createFromPath("@drawable/ripple_effect"));
                cardView.setClickable(true);
                cardView.setFocusable(true);
                cardView.setElevation(dpToPixel(1));

                // First Set the Tag name of the card
                cardView.setTag(id);

                // Display the profile picture
                ShapeableImageView shapeableImageView = new ShapeableImageView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpToPixel(100), dpToPixel(100));
                lp.setMargins(dpToPixel(20),dpToPixel(20),dpToPixel(20),dpToPixel(20));
                shapeableImageView.setLayoutParams(lp);

                shapeableImageView.setTag(photo_url);
                new DownloadImagesTask().execute(shapeableImageView);
                cardView.addView(shapeableImageView);

                //Display Title
                MaterialTextView materialTextView = new MaterialTextView(this);
                materialTextView.setText(name);
                materialTextView.setTag("name_"+id);
                LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(dpToPixel(200), ViewGroup.LayoutParams.WRAP_CONTENT);
                materialTextView.setTextColor(Color.BLACK);
                materialTextView.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
                materialTextView.setTextSize(18);
                params.gravity = Gravity.CENTER;
                params.leftMargin = dpToPixel(137);
                params.topMargin = dpToPixel(20);
                Typeface typeface = ResourcesCompat.getFont(this, R.font.poppins_medium);
                materialTextView.setTypeface(typeface,Typeface.BOLD);
                materialTextView.setMaxLines(2);
                materialTextView.setLayoutParams(params);

                cardView.addView(materialTextView);

                // Display Description
                MaterialTextView materialTextView1 = new MaterialTextView(this);
                materialTextView1.setText(description);
                LinearLayout.LayoutParams params1= new LinearLayout.LayoutParams(dpToPixel(200), ViewGroup.LayoutParams.WRAP_CONTENT);
                materialTextView1.setTextColor(Color.BLACK);
                materialTextView1.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
                materialTextView1.setTextSize(13);
                params1.gravity = Gravity.CENTER;
                params1.leftMargin = dpToPixel(137);
                params1.topMargin = dpToPixel(50);
                Typeface typeface1 = ResourcesCompat.getFont(this, R.font.poppins_medium);
                materialTextView1.setTypeface(typeface1,Typeface.NORMAL);
                materialTextView1.setMaxLines(3);
                materialTextView1.setLayoutParams(params1);

                cardView.addView(materialTextView1);

                // Display the price
                MaterialTextView materialTextView2 = new MaterialTextView(this);
                materialTextView2.setText("Rs."+ price);
                materialTextView2.setTag("price_"+id);
                LinearLayout.LayoutParams params2= new LinearLayout.LayoutParams(dpToPixel(200), ViewGroup.LayoutParams.WRAP_CONTENT);
                materialTextView2.setTextColor(Color.BLACK);
                materialTextView2.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
                materialTextView2.setTextSize(25);
                materialTextView2.setTextColor(Color.rgb(55,0,179));
                params2.gravity = Gravity.CENTER;
                params2.leftMargin = dpToPixel(137);
                params2.topMargin = dpToPixel(100);
                Typeface typeface2 = ResourcesCompat.getFont(this, R.font.poppins_medium);
                materialTextView2.setTypeface(typeface2,Typeface.BOLD);
                materialTextView2.setMaxLines(3);
                materialTextView2.setLayoutParams(params2);

                cardView.addView(materialTextView2);

                //Display the -
                MaterialTextView materialTextView3 = new MaterialTextView(this);
                materialTextView3.setText("-");
                LinearLayout.LayoutParams params3= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                materialTextView3.setTextColor(Color.BLACK);
                materialTextView3.setGravity(View.TEXT_ALIGNMENT_CENTER);
                materialTextView3.setTextSize(25);
                materialTextView3.setTextColor(Color.BLACK);
                params3.gravity = Gravity.END;
                params3.leftMargin = dpToPixel(235);
                params3.topMargin = dpToPixel(105);
                Typeface typeface3 = ResourcesCompat.getFont(this, R.font.poppins_medium);
                materialTextView3.setTypeface(typeface3,Typeface.BOLD);
                materialTextView3.setMaxLines(1);
                materialTextView3.setLayoutParams(params3);

                cardView.addView(materialTextView3);

                //Display the quantity
                MaterialTextView materialTextView4 = new MaterialTextView(this);
                materialTextView4.setText("0");
                materialTextView4.setTag("quantity_"+id);
                LinearLayout.LayoutParams params4= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                materialTextView4.setTextColor(Color.BLACK);
                materialTextView4.setGravity(View.TEXT_ALIGNMENT_CENTER);
                materialTextView4.setTextSize(30);
                materialTextView4.setTextColor(Color.BLACK);
                params4.gravity = Gravity.CENTER;
                params4.leftMargin = dpToPixel(265);
                params4.topMargin = dpToPixel(100);
                Typeface typeface4 = ResourcesCompat.getFont(this, R.font.poppins_medium);
                materialTextView4.setTypeface(typeface4,Typeface.BOLD);
                materialTextView4.setMaxLines(1);
                materialTextView4.setLayoutParams(params4);

                cardView.addView(materialTextView4);

                //Display the +
                MaterialTextView materialTextView5 = new MaterialTextView(this);
                materialTextView5.setText("+");
                LinearLayout.LayoutParams params5= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                materialTextView5.setTextColor(Color.BLACK);
                materialTextView5.setGravity(View.TEXT_ALIGNMENT_CENTER);
                materialTextView5.setTextSize(25);
                materialTextView5.setTextColor(Color.BLACK);
                params5.gravity = Gravity.END;
                params5.leftMargin = dpToPixel(295);
                params5.topMargin = dpToPixel(105);
                Typeface typeface5 = ResourcesCompat.getFont(this, R.font.poppins_medium);
                materialTextView5.setTypeface(typeface5,Typeface.BOLD);
                materialTextView5.setMaxLines(1);
                materialTextView5.setLayoutParams(params5);

                cardView.addView(materialTextView5);

                //Set up on click listeners for quantity
                materialTextView3.setOnClickListener(v -> {
                    String str_quantity = materialTextView4.getText().toString();
                    int quantity = Integer.parseInt(str_quantity);
                    if(quantity > 0) quantity--;
                    String new_quantity = String.valueOf(quantity);
                    materialTextView4.setText(new_quantity);
                    //Update the HashMap
                    if(quantity==0)
                        hashMap.remove(cardView.getTag().toString());
                    else
                        hashMap.put(cardView.getTag().toString(), new_quantity);

                });
                materialTextView5.setOnClickListener(v -> {
                    String str_quantity = materialTextView4.getText().toString();
                    int quantity = Integer.parseInt(str_quantity);
                    if(quantity < 10) quantity++;
                    String new_quantity = String.valueOf(quantity);
                    materialTextView4.setText(new_quantity);
                    //Update the HashMap
                    hashMap.put(cardView.getTag().toString(), new_quantity);
                });
                linearLayout.addView(cardView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set up onclick listener for confirm order
            confirm.setOnClickListener(v -> {
                if(!confirmOrder) {
                    if(hashMap.size()<=0){
                        Toast.makeText(activity_make_order.this, "Please select some items first", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Iterator<Map.Entry<String, String>> it = hashMap.entrySet().iterator();
                    summary = new JSONArray();
                    while (it.hasNext()) {
                        Map.Entry<String, String> pair = it.next();
                        String key = String.valueOf(pair.getKey());

                        // Get the name and price of the item and append to summary
                        String name = ((MaterialTextView)linearLayout.findViewWithTag("name_"+key)).getText().toString();
                        String price = (((MaterialTextView)linearLayout.findViewWithTag("price_"+key)).getText().toString()).replace("Rs.","");
                        price = String.valueOf(Integer.parseInt(price)*Integer.parseInt(String.valueOf(pair.getValue())));

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("item_id", key);
                            jsonObject.put("name",name);
                            jsonObject.put("price",price);
                            jsonObject.put("quantity",pair.getValue());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        summary.put(jsonObject);
                    }


                    //BottomSheet On Confirm Order
                    BottomSheetOrderConfirmation confirm_order = new BottomSheetOrderConfirmation();
                    Bundle args=new Bundle();
                    String userProfileString=summary.toString();
                    args.putString("summary", userProfileString);
                    confirm_order.setArguments(args);
                    confirm_order.show(getSupportFragmentManager(),"TAG");
                    //confirmOrder = true;
                }
            });

        // Set up onclick listener for checkout
            checkout.setOnClickListener(v -> {
                    if(!hashMap.isEmpty()){
                        Toast.makeText(activity_make_order.this, "Please place the the order first.", Toast.LENGTH_LONG).show();
                    }else{
                        try {
                            String order_id = getKey(activity_make_order.this, "order_id");
                            Cursor cursor = DB.getAllOrderItems(order_id);
                            if(cursor.getCount()<=0)
                                Toast.makeText(activity_make_order.this, "Please order some food items first.", Toast.LENGTH_LONG).show();
                            else{
                                Intent intent = new Intent(activity_make_order.this, com.example.menu_card.checkout.checkout.class);
                                startActivity(intent);
                            }
                        } catch (IOException e) {
                            Toast.makeText(activity_make_order.this, "Please order some food items first.", Toast.LENGTH_LONG).show();
                        }

                    }
            });


    }
    public int dpToPixel(float dp) {
        float density = this.getResources().getDisplayMetrics().density;
        float pixel = dp * density;
        return (int) pixel;
    }

    @SuppressWarnings("deprecation")
    public static class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

        @SuppressLint("StaticFieldLeak")
        ImageView imageView = null;

        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            return download_Image((String)imageView.getTag());
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

        private Bitmap download_Image(String url) {

            Bitmap bmp;
            try{
                URL ulrn = new URL(url);
                HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                if (null != bmp)
                    return bmp;

            }catch(Exception ignored){}
            return null;
        }
    }

}
