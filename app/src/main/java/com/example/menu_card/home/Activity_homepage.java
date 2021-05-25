package com.example.menu_card.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.example.menu_card.order.*;

import com.example.menu_card.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class Activity_homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        FloatingActionButton fab =(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Scan Activity Goes Here
                Intent intent = new Intent(Activity_homepage.this, Scanner.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavview);
        NavController navController = Navigation.findNavController(this,R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);

        //For Testing purposes
        ImageView img = (ImageView)findViewById(R.id.profile);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show Profile
                Toast.makeText(Activity_homepage.this, "Profile", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
