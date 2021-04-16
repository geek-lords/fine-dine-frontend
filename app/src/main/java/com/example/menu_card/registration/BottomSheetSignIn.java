package com.example.menu_card.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.menu_card.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class BottomSheetSignIn extends BottomSheetDialogFragment {
    TextView sign_up_btn;
    static boolean isVisible = false;

    public BottomSheetSignIn() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.sign_in,container,false);

        sign_up_btn = view.findViewById(R.id.sign_up_redirect);
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isVisible){
                    isVisible = true;
                    BottomSheetSignUp sign_up =  new BottomSheetSignUp();
                    sign_up.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),"TAG");
                }
            }
        });
        return view;


    }
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

}
