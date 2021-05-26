package com.example.menu_card.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.menu_card.R;
import com.example.menu_card.home.Activity_homepage;

public class MainActivity extends AppCompatActivity {

    Button button;
    public static String BASE_URL = "https://admin-fine-dine.herokuapp.com/api/v1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.register_button);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), activitySignUp.class);
            startActivity(intent);
        });

        button = findViewById(R.id.sign_in_button);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), activitySignIn.class);
            startActivity(intent);
        });

    }
}