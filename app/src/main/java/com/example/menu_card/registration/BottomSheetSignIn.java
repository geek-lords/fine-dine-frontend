package com.example.menu_card.registration;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.menu_card.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.example.menu_card.registration.MainActivity.BASE_URL;

public class BottomSheetSignIn extends BottomSheetDialogFragment {
    TextView sign_up_btn, sign_in_btn;
    static boolean isVisible = false;
    static boolean isSubmit = false;

    public BottomSheetSignIn() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.sign_in,container,false);

        sign_in_btn = view.findViewById(R.id.logo_sign_in);
        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSubmit){
                    isSubmit = true;

                    EditText email_et = view.findViewById(R.id.email_sign_in);
                    String  email = email_et.getText().toString();

                    EditText pass_et = view.findViewById(R.id.password_sign_in);
                    String  password = pass_et.getText().toString();

                    if(email.trim().equals("") || password.trim().equals("")){
                        Toast.makeText(getActivity(), "Credentials can't be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!isEmailValid(email)){
                        Toast.makeText(getActivity(), "Email is invalid", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Sign user in through api

                    String url = BASE_URL+"/authenticate";

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            response -> {
                                try {
                                    Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                                    JSONObject jsonObject = new JSONObject(response);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            error -> {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Error")
                                        .setMessage(error.getMessage())
                                        .setCancelable(false)
                                        .setPositiveButton("OK", (dialog, which) -> {}).show();
                            }){

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map <String, String> params = new HashMap<>();
                            params.put("password",password);
                            params.put("email",email);
                            return  params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
                    requestQueue.add(stringRequest);
                }
            }
        });

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

    public static boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static void saveTextToFile(Context context, String filename, String content) {
        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getKey(Context context, String filename) throws IOException {
        File file = new File(context.getFilesDir(), filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }
}
