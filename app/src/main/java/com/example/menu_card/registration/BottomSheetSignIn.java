package com.example.menu_card.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.menu_card.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetSignIn extends BottomSheetDialogFragment {


    public BottomSheetSignIn() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.sign_in,container,false);
        return view;


    }
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

}
