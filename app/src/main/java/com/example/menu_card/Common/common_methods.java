package com.example.menu_card.Common;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class common_methods {

    // Saving JWT to system
    public static void saveTextToFile(Context context, String filename, String content) {
        File file = new File(context.getFilesDir(), filename);
        if(file.exists()){
            if(!file.delete()){
                System.err.println("Couldn't delete JWT file:"+filename);
                throw new RuntimeException();
            }
        }
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Retrieving JWT from the system
    public static String getKey(Context context, String filename) throws IOException {
        File file = new File(context.getFilesDir(), filename);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        }
    }

    public static boolean _delete_file_if_exists(Context context, String filename){
        File file = new File(context.getFilesDir(), filename);
        if(file.exists()){
            return file.delete();
        }
        return false;
    }

    public static String _print_server_response_error(VolleyError volleyError){
        String message = null;
        if (volleyError instanceof NetworkError) {
            message = "Cannot connect to Internet. Please check your connection";
        } else if (volleyError instanceof ServerError) {
            message = "The server could not be found. Please try again after some time";
        } else if (volleyError instanceof AuthFailureError) {
            message = "Cannot connect to Internet. Please check your connection";
        } else if (volleyError instanceof ParseError) {
            message = "Parsing error! Please try again after some time";
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection TimeOut. Please check your internet connection.";
        }

        if (message==null)
            message = volleyError.toString();

        return message;
    }
}
