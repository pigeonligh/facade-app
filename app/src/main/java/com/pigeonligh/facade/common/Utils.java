package com.pigeonligh.facade.common;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class Utils {
    private static final Gson gsonInstance = new Gson();

    public static String ReadFile(File file, String defVal) {
        StringBuffer buffer = new StringBuffer();
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            int ch = 0;
            while ((ch = reader.read()) != -1) {
                buffer.append((char) ch);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return defVal;
        } catch (IOException e) {
            e.printStackTrace();
            return defVal;
        }
        return buffer.toString();
    }

    public static boolean WriteFile(File file, String data) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(data);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean ValidateURL(String url) {
        return true;
    }

    public static Gson gson() {
        return gsonInstance;
    }
}
