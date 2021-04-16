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

import java.util.Objects;

public class BottomSheetSignUp extends BottomSheetDialogFragment {
    TextView sign_in_btn;
    static boolean isVisible = false;

    public BottomSheetSignUp() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.sign_up,container,false);

        sign_in_btn = view.findViewById(R.id.sign_in_redirect);
        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isVisible){
                    isVisible = true;
                    BottomSheetSignIn sign_in =  new BottomSheetSignIn();
                    sign_in.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),"TAG");
                }
            }
        });

        return view;


    }
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

}
