package com.example.menu_card.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.menu_card.R;
import com.example.menu_card.home.Scanner;
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
import java.util.Map;

import static com.example.menu_card.Common.common_methods.getKey;
import static com.example.menu_card.registration.MainActivity.BASE_URL;


public class fragment_order extends Fragment {
    LinearLayout linearLayout;
    ProgressBar progressBar;

    public fragment_order() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_order, container, false);

        linearLayout = view.findViewById(R.id.linearLayout_recent_order);
        progressBar = view.findViewById(R.id.progressBar_recent_orders);
        progressBar.setVisibility(View.VISIBLE);

        getRecentOrders(result -> {
            if(getContext()==null) return;

            progressBar.setVisibility(View.INVISIBLE);
            JSONArray history = new JSONObject(result).getJSONArray("history");

            for(int i=0; i<history.length(); i++){
                JSONObject order = history.getJSONObject(i);

                //Create the card
                MaterialCardView cardView = new MaterialCardView(requireActivity());
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
                cardView.setTag(order.toString());

                // Display the profile picture
                ShapeableImageView shapeableImageView = new ShapeableImageView(requireActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpToPixel(100), dpToPixel(100));
                lp.setMargins(dpToPixel(5),dpToPixel(0),dpToPixel(20),dpToPixel(0));
                shapeableImageView.setLayoutParams(lp);

                if(order.has("photo_url")){
                    shapeableImageView.setTag(order.getString("photo_url"));
                    System.out.println(order.getString("photo_url"));
                    new DownloadImagesTask().execute(shapeableImageView);
                }
                cardView.addView(shapeableImageView);

                //Display Name of restaurant
                MaterialTextView materialTextView = new MaterialTextView(requireActivity());
                materialTextView.setText(order.getString("name"));
                LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(dpToPixel(200), ViewGroup.LayoutParams.WRAP_CONTENT);
                materialTextView.setTextColor(Color.BLACK);
                materialTextView.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
                materialTextView.setTextSize(18);
                params.gravity = Gravity.CENTER;
                params.leftMargin = dpToPixel(137);
                params.topMargin = dpToPixel(5);
                Typeface typeface = ResourcesCompat.getFont(requireActivity(), R.font.poppins_medium);
                materialTextView.setTypeface(typeface,Typeface.BOLD);
                materialTextView.setMaxLines(2);
                materialTextView.setLayoutParams(params);

                cardView.addView(materialTextView);

                // Display Amount
                MaterialTextView materialTextView1 = new MaterialTextView(requireActivity());
                materialTextView1.setText("Rs. "+order.getString("price"));
                LinearLayout.LayoutParams params1= new LinearLayout.LayoutParams(dpToPixel(200), ViewGroup.LayoutParams.WRAP_CONTENT);
                materialTextView1.setTextColor(Color.BLACK);
                materialTextView1.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
                materialTextView1.setTextSize(15);
                params1.gravity = Gravity.CENTER;
                params1.leftMargin = dpToPixel(137);
                params1.topMargin = dpToPixel(35);
                Typeface typeface1 = ResourcesCompat.getFont(requireActivity(), R.font.poppins_medium);
                materialTextView1.setTypeface(typeface1,Typeface.NORMAL);
                materialTextView1.setMaxLines(3);
                materialTextView1.setLayoutParams(params1);

                cardView.addView(materialTextView1);

                // Display Date
                MaterialTextView materialTextView2 = new MaterialTextView(requireActivity());
                materialTextView2.setText(order.getString("time_and_date"));
                LinearLayout.LayoutParams params2= new LinearLayout.LayoutParams(dpToPixel(200), ViewGroup.LayoutParams.WRAP_CONTENT);
                materialTextView2.setTextColor(Color.BLACK);
                materialTextView2.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
                materialTextView2.setTextSize(13);
                params2.gravity = Gravity.CENTER;
                params2.leftMargin = dpToPixel(137);
                params2.topMargin = dpToPixel(70);
                Typeface typeface2 = ResourcesCompat.getFont(requireActivity(), R.font.poppins_medium);
                materialTextView2.setTypeface(typeface2,Typeface.NORMAL);
                materialTextView2.setMaxLines(3);
                materialTextView2.setLayoutParams(params2);

                cardView.addView(materialTextView2);

                // Add the event on card click
                cardView.setOnClickListener(v -> {
                    Intent intent = new Intent(requireActivity(), com.example.menu_card.home.detailed_recent_order.class);
                    intent.putExtra("order_info", order.toString());
                    startActivity(intent);
                });

                linearLayout.addView(cardView);

            }
        });

        return view;
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

    public void getRecentOrders(Scanner.VolleyCallback callback){
        String jwt = null;
        try {
            jwt = getKey(requireActivity(), "jwt_token");
        } catch (IOException e) {
            Intent intent = new Intent(getActivity(), com.example.menu_card.registration.MainActivity.class);
            requireActivity().finish();
            startActivity(intent);
        }

        String finalJwt = jwt;
        String url = BASE_URL+"/order_history";
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,url, null,
                response -> {
                    if(response.has("error")) {
                        try {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), response.getString("error"), Toast.LENGTH_LONG).show();
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
                }, volleyError -> {
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
                    } else if (volleyError instanceof TimeoutError) {
                        message = "Connection TimeOut. Please check your internet connection.";
                    }
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Error")
                            .setCancelable(true)
                            .setMessage(message)
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
}