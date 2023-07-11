package com.example.weather;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {
    City selectedCity;
    Weather weather;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getselectedCity();
        setValues(weather);
//        weather = new Weather(selectedCity);
//        Intent previousIntent = getIntent();
//        if (weather.isSuccessfulRequest()) {
//            setValues(weather);
//        } else {
//            Intent main = new Intent(getApplicationContext(), MainActivity.class);
//            failedRequestPopup();
//            //startActivity(main);
//        }

    }

    private void failedRequestPopup() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        findViewById(R.id.activity_layout).post(new Runnable() {
            public void run() {
                popupWindow.showAtLocation(findViewById(R.id.activity_layout), Gravity.CENTER, 0, 0);
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
            }
        });


        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("DEBUGGING", "Popup touch");
                popupWindow.dismiss();
                return true;
            }
        });

    }

    private void getselectedCity() {
        Intent previousIntent = getIntent();
        int index = previousIntent.getIntExtra("id", -1);
        selectedCity = MainActivity.cityList.get(index);

        this.weather = (Weather) previousIntent.getSerializableExtra("weather");
    }

    private void setValues(Weather weather) {
        TextView tv = (TextView) findViewById(R.id.cityName);
        ImageView iv = (ImageView) findViewById(R.id.cityFlagImage);
        ImageView ivWeatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        TextView tvTemp = (TextView) findViewById(R.id.textViewTemp);
        TextView tvFeelsLikeTemp = (TextView) findViewById(R.id.textViewFeelsLikeTemp);
        TextView tvTempWeatherDesc = (TextView) findViewById(R.id.textViewWeatherDesc);

        tv.setText(selectedCity.getName());

        String temp = String.format("Temperature: %1$,.2f \u00B0 C",weather.getTemp());
        tvTemp.setText(temp);

        String feelsLikeTemp = String.format("Feels like: %1$,.2f \u00B0 C",weather.getFeelsLikeTemp());
        tvFeelsLikeTemp.setText(feelsLikeTemp);

        tvTempWeatherDesc.setText(weather.getDescription());


        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Background work here
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(weather.icon).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            Bitmap finalMIcon1 = mIcon11;
            handler.post(() -> {
                //UI Thread work here
                ivWeatherIcon.setImageBitmap(finalMIcon1);

            });
        });

        if (selectedCity.getImage() != -1) {
            iv.setImageResource(selectedCity.getImage());
        }
    }




}



