package com.example.menu_card;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.register_button);
        button.setOnClickListener(v -> {
            BottomSheetSignUp sign_up =  new BottomSheetSignUp();
            sign_up.show(getSupportFragmentManager(),"TAG");
        });

        button = findViewById(R.id.sign_in_button);
        button.setOnClickListener(v -> {
            BottomSheetSignIn sign_in =  new BottomSheetSignIn();
            sign_in.show(getSupportFragmentManager(),"TAG");
        });

    }
}