package com.example.menu_card.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.menu_card.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetOrderConfirmation extends BottomSheetDialogFragment {

    public BottomSheetOrderConfirmation() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        return inflater.inflate(R.layout.order_confirmation,container, false);
    }

    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }


}
