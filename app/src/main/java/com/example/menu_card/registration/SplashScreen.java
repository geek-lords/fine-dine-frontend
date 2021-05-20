package com.example.menu_card.registration;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.menu_card.R;
import com.example.menu_card.home.Activity_homepage;

import java.io.IOException;

import static com.example.menu_card.Common.common_methods.getKey;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        int SPLASH_DISPLAY_LENGTH = 2450;
        new Handler().postDelayed(() -> {
            try {
                String key = getKey(SplashScreen.this, "jwt_token");

                Intent mainIntent = new Intent(SplashScreen.this, Activity_homepage.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            } catch (IOException e) {

                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }


}

