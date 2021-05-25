package com.example.menu_card.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.menu_card.R;
import com.example.menu_card.order.activity_make_order;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

import static com.example.menu_card.Common.common_methods.getKey;

public class Activity_homepage extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(Activity_homepage.this)
                .setTitle("Exit Application?")
                .setCancelable(true)
                .setMessage("Do you want to exit the app?")
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Yes", (dialog, which) -> finishAffinity()).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            //Scan Activity Goes Here
            Intent intent = new Intent(Activity_homepage.this, Scanner.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavview);
        NavController navController = Navigation.findNavController(this,R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);

        //For Testing purposes
        ImageView img = findViewById(R.id.profile);
        img.setOnClickListener(v -> {
            // Show Profile
            Toast.makeText(Activity_homepage.this, "Profile", Toast.LENGTH_SHORT).show();
        });

    }
}
