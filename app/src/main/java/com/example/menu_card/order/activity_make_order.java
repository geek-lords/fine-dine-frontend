package com.example.menu_card.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.menu_card.R;
import com.example.menu_card.home.Scanner;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class activity_make_order extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);

        LinearLayout linearLayout = findViewById(R.id.linear_layout_menu);
        linearLayout.setElevation(dpToPixel(10));

        String menu = getIntent().getStringExtra("menu");
        try {
            JSONObject jsonObject = new JSONObject(menu);
            JSONArray jsonArray = jsonObject.getJSONArray("menu");

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                String id = obj.getString("id");
                String name = obj.getString("name");
                String description = obj.getString("description");
                String photo_url = obj.getString("photo_url");
                String price = String.valueOf((int)Double.parseDouble(obj.getString("price")));

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
                materialTextView2.setText("Rs. "+ price);
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
                params3.gravity = Gravity.RIGHT;
                params3.leftMargin = dpToPixel(250);
                params3.topMargin = dpToPixel(105);
                Typeface typeface3 = ResourcesCompat.getFont(this, R.font.poppins_medium);
                materialTextView3.setTypeface(typeface3,Typeface.BOLD);
                materialTextView3.setMaxLines(1);
                materialTextView3.setLayoutParams(params3);

                cardView.addView(materialTextView3);

                //Display the quantity
                MaterialTextView materialTextView4 = new MaterialTextView(this);
                materialTextView4.setText("0");
                LinearLayout.LayoutParams params4= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                materialTextView4.setTextColor(Color.BLACK);
                materialTextView4.setGravity(View.TEXT_ALIGNMENT_CENTER);
                materialTextView4.setTextSize(30);
                materialTextView4.setTextColor(Color.BLACK);
                params4.gravity = Gravity.CENTER;
                params4.leftMargin = dpToPixel(285);
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
                params5.gravity = Gravity.RIGHT;
                params5.leftMargin = dpToPixel(320);
                params5.topMargin = dpToPixel(105);
                Typeface typeface5 = ResourcesCompat.getFont(this, R.font.poppins_medium);
                materialTextView5.setTypeface(typeface5,Typeface.BOLD);
                materialTextView5.setMaxLines(1);
                materialTextView5.setLayoutParams(params5);

                cardView.addView(materialTextView5);

                //Set up on click listeners for quantity
                materialTextView3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str_quantity = materialTextView4.getText().toString();
                        int quantity = Integer.parseInt(str_quantity);
                        if(quantity > 0) quantity--;
                        String new_quantity = String.valueOf(quantity);
                        materialTextView4.setText(new_quantity);
                    }
                });
                materialTextView5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str_quantity = materialTextView4.getText().toString();
                        int quantity = Integer.parseInt(str_quantity);
                        if(quantity < 10) quantity++;
                        String new_quantity = String.valueOf(quantity);
                        materialTextView4.setText(new_quantity);
                    }
                });
                linearLayout.addView(cardView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public int dpToPixel(float dp) {
        float density = this.getResources().getDisplayMetrics().density;
        float pixel = dp * density;
        return (int) pixel;
    }

    public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

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

            Bitmap bmp =null;
            try{
                URL ulrn = new URL(url);
                HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                if (null != bmp)
                    return bmp;

            }catch(Exception e){}
            return bmp;
        }
    }

}
