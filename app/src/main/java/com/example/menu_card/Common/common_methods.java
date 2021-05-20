package com.example.menu_card.Common;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.menu_card.DB.DBHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class common_methods {
    // These are all the static methods common throughout the app


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

    // Deleting the file
    public static boolean _delete_file_if_exists(Context context, String filename){
        File file = new File(context.getFilesDir(), filename);
        if(file.exists()){
            return file.delete();
        }
        return false;
    }

    // Server response error
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

    //Clear all the order details after payment
    public static boolean _clear_order_details(Context context) throws IOException {

        // Delete data from DB of current order_id
        String order_id = getKey(context, "order_id");
        DBHelper DB = new DBHelper(context);
        DB.deleteOrderInfo(order_id);

        if(!_delete_file_if_exists(context, "order_id")){
            System.err.println("Couldn't delete order_id after payment");
            return false;
        }
        if(!_delete_file_if_exists(context, "tax_percent")){
            System.err.println("Couldn't delete tax_percent after payment");
            return false;
        }
        if(!_delete_file_if_exists(context, "restaurant_id")){
            System.err.println("Couldn't delete restaurant_id after payment");
            return false;
        }
        if(!_delete_file_if_exists(context, "table_no")){
            System.err.println("Couldn't delete table_no after payment");
            return false;
        }

        return true;
    }
}
