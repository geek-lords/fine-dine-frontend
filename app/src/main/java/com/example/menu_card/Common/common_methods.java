package com.example.menu_card.Common;

import android.content.Context;

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
