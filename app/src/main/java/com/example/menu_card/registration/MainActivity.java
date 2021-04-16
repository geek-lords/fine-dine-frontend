package com.example.menu_card.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.menu_card.R;
import com.example.menu_card.registration.BottomSheetSignIn;
import com.example.menu_card.registration.BottomSheetSignUp;

public class MainActivity extends AppCompatActivity {

    Button button;
    static String BASE_URL = "https://fine-dine-backend.herokuapp.com/api/v1/";

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