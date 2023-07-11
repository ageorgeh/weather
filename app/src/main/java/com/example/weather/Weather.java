package com.example.weather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Weather implements Serializable {
    City city;
    String description;

    public boolean isSuccessfulRequest() {
        return successfulRequest;
    }

    String icon;
    double temp;
    double feelsLikeTemp;
    boolean successfulRequest = true;

    public Weather(City city) {
        this.city = city;
        getData();
    }

    public String getDescription() {
        return description;
    }

    public double getTemp() {
        return temp;
    }

    public double getFeelsLikeTemp() {
        return feelsLikeTemp;
    }

    private void getData(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Background work here
            makeRequest();
            executor.shutdown();
            handler.post(() -> {
                //UI Thread work here

            });
        });
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {

        }


        Log.d("DEBUGGING", "data got");
    }

    private void makeRequest() {
        try {
            String urlString = String.format("https://api.weatherapi.com/v1/current.json?key=958f2e7e8a1443558b6144631230707&q=%s&aqi=no", city.getName());
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Getting the response code
            int responseCode = conn.getResponseCode();


            if (responseCode != 200) {
                successfulRequest = false;
                //throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {

                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                //Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }

                //Close the scanner
                scanner.close();

                //Using the JSON simple library parse the string into a json object
                JSONObject data_obj = new JSONObject(inline);
                Log.d("DEBUGGING", data_obj.getString("current"));
                temp = data_obj.getJSONObject("current").getDouble("temp_c");
                feelsLikeTemp = data_obj.getJSONObject("current").getDouble("feelslike_c");
                description = data_obj.getJSONObject("current").getJSONObject("condition").getString("text");
                icon = String.format("https:%s",data_obj.getJSONObject("current").getJSONObject("condition").getString("icon"));
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

    }
}
