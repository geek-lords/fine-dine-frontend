package com.example.menu_card.order;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.menu_card.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class BottomSheetOrderConfirmation extends BottomSheetDialogFragment {

    public BottomSheetOrderConfirmation() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_confirmation,container, false);
        LinearLayoutCompat linearLayoutCompat = view.findViewById(R.id.linearLayoutCompat_summary);
        ScrollView scrollView = view.findViewById(R.id.scrollView2);
        // Create the card
        MaterialCardView materialCardView = new MaterialCardView(requireActivity());
        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(dpToPixel(10),dpToPixel(10),dpToPixel(10),dpToPixel(10));
        materialCardView.setRadius(dpToPixel(10));
        materialCardView.setLayoutParams(params);

        // Name
        MaterialTextView item_name = new MaterialTextView(requireActivity());
        item_name.setText("name");
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
        item_quantity.setText("Quantity");
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
        item_price.setText("Price");
        LinearLayout.LayoutParams params3= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params3.leftMargin = dpToPixel(300);
        Typeface typeface3 = ResourcesCompat.getFont(requireActivity(), R.font.poppins_medium);
        item_price.setTypeface(typeface3,Typeface.BOLD);
        item_price.setLayoutParams(params3);
        item_price.setTextColor(Color.BLACK);
        item_price.setTextSize(15);

        materialCardView.addView(item_price);

        linearLayoutCompat.addView(materialCardView);
        return view;
    }

    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    public int dpToPixel(float dp) {
        float density = this.getResources().getDisplayMetrics().density;
        float pixel = dp * density;
        return (int) pixel;
    }
    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
