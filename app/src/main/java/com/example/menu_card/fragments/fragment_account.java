package com.example.menu_card.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.menu_card.Common.common_methods;
import com.example.menu_card.R;
import com.example.menu_card.home.Scanner;
import com.example.menu_card.registration.SplashScreen;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.menu_card.Common.common_methods.getKey;
import static com.example.menu_card.registration.MainActivity.BASE_URL;


public class fragment_account extends Fragment {
    ProgressBar progressBar;
    TextInputEditText email, name;
    MaterialButton update_profile, log_out;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public fragment_account() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getArguments().getString(ARG_PARAM1);
            getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_account, container, false);
        progressBar = view.findViewById(R.id.progressBar_profile);
        progressBar.setVisibility(View.VISIBLE);

        email = view.findViewById(R.id.profile_email);
        name = view.findViewById(R.id.profile_name);
        update_profile = view.findViewById(R.id.btn_update_profile_details);
        log_out = view.findViewById(R.id.log_out);

        getProfileDetails(result -> {
            if (getContext()==null) return;

            JSONObject jsonObject = new JSONObject(result);
            String str_name = jsonObject.getString("name");
            String str_email = jsonObject.getString("email");

            email.setText(str_email);
            name.setText(str_name);

            progressBar.setVisibility(View.INVISIBLE);
            update_profile.setEnabled(true);
        });

        update_profile.setOnClickListener(v -> {
            String str_email = Objects.requireNonNull(email.getText()).toString();
            String str_name = Objects.requireNonNull(name.getText()).toString();

            if(str_email.isEmpty() || str_name.isEmpty()){
                Toast.makeText(requireActivity(), "Details can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!common_methods.isEmailValid(str_email)){
                Toast.makeText(requireActivity(), "Email is invalid", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            update_profile.setEnabled(false);

            try {
                updateProfileDetails(str_email, str_name, result -> {
                    if (getContext()==null) return;

                    progressBar.setVisibility(View.INVISIBLE);
                    update_profile.setEnabled(true);
                    Toast.makeText(requireActivity(), "Profile Details updated", Toast.LENGTH_SHORT).show();
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        log_out.setOnClickListener(v -> {
            common_methods._delete_file_if_exists(requireActivity(), "jwt_token");
            Intent intent = new Intent(requireActivity(), SplashScreen.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void getProfileDetails(final Scanner.VolleyCallback callback) {
        String jwt = null;
        try {
            jwt = common_methods.getKey(requireActivity(), "jwt_token");
        } catch (IOException e) {
            Intent intent = new Intent(getActivity(), com.example.menu_card.registration.MainActivity.class);
            requireActivity().finish();
            startActivity(intent);
        }

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        String url = BASE_URL+"/user";

        String finalJwt = jwt;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("error"))
                            Toast.makeText(requireActivity(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        else callback.onSuccess(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, volleyError -> {
            progressBar.setVisibility(View.INVISIBLE);
            new AlertDialog.Builder(requireActivity())
                    .setTitle("Couldn't fetch details")
                    .setCancelable(true)
                    .setMessage(common_methods._print_server_response_error(volleyError))
                    .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("X-Auth-Token", finalJwt);
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void updateProfileDetails(String email, String name, final Scanner.VolleyCallback callback) throws JSONException {
        String jwt = null;
        try {
            jwt = common_methods.getKey(requireActivity(), "jwt_token");
        } catch (IOException e) {
            Intent intent = new Intent(getActivity(), com.example.menu_card.registration.MainActivity.class);
            requireActivity().finish();
            startActivity(intent);
        }

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        String url = BASE_URL+"/user";

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("name", name);

        String finalJwt = jwt;
        // Request a string response from the provided URL.
                JsonObjectRequest stringRequest = new JsonObjectRequest(
                Request.Method.PUT,url, json,
                response -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        if (jsonObject.has("error"))
                            Toast.makeText(requireActivity(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        else callback.onSuccess(response.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, volleyError -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    update_profile.setEnabled(true);
                    if (volleyError instanceof ServerError) // If 422 error occurs, details are invalid
                        Toast.makeText(requireActivity(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    else
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Couldn't update details")
                            .setCancelable(true)
                            .setMessage(common_methods._print_server_response_error(volleyError))
                            .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).show();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("X-Auth-Token", finalJwt);
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}