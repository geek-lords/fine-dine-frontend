package com.example.menu_card.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.menu_card.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_order#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_order extends Fragment {

    public fragment_order() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_order, container, false);
    }
}