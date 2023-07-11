package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity
{
    public static ArrayList<City> cityList = new ArrayList<City>();
    private ListView listView;
    private String selectedFilter = "all";
    private String currentSearchText = "";
    private SearchView searchView;
    private DBManager dbManager;

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        dbManager = new DBManager(this);
        dbManager.open();
        setContentView(R.layout.activity_main);
        initSearchWidgets(dbManager);
        setupData();
        setUpList();
        setUpOnclickListener();





    }
    private void initSearchWidgets(DBManager dbManager) {
        searchView = (SearchView) findViewById(R.id.cityListSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                currentSearchText = s;
                ArrayList<City> filteredCities = new ArrayList<City>();
                Cursor c = dbManager.filter(s);
                Log.d("DEBUGGING", "DB count " + Integer.toString(c.getCount()));

                if (c.getCount() > 0) {
                    do {
                        filteredCities.add(new City(c.getString(0), c.getString(1)));
                    }
                    while (c.moveToNext());
                }

                cityList=filteredCities;
                CityAdapter adapter = new CityAdapter(getApplicationContext(), 0, filteredCities);
                listView.setAdapter(adapter);

                return false;
            }
        });
    }

    private void setupData() {

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor c  = dbManager.fetch();
        if (c.getCount() > 0) {
            Log.d("DEBUGGING", "Already populated");
            return;
        }

        try {
            InputStreamReader is = new InputStreamReader(getAssets()
                    .open("worldcities.csv"));

            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            int id = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                //Log.d("DEBUGGING", String.format("This the line: %s", line.split(",")[0].replace("\"", "")));
                //cityList.add(new City(Integer.toString(id), line.split(",")[0].replace("\"", "")));
                dbManager.insert(line.split(",")[0].replace("\"", ""));
                //id+=1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ERROR", "The specified file was not found");
        }
        
    }

    private void setUpList() {
        listView = (ListView) findViewById(R.id.citiesListView);

        CityAdapter adapter = new CityAdapter(getApplicationContext(), 0, cityList);
        listView.setAdapter(adapter);
    }

    private void setUpOnclickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Handler handler = new Handler();

                view.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);


                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        City selectCity = (City) (listView.getItemAtPosition(position));
                        Intent showDetail = new Intent(getApplicationContext(), DetailActivity.class);
                        showDetail.putExtra("id",position);

                        Weather weather = new Weather(selectCity);

                        if (weather.isSuccessfulRequest()) {
                            showDetail.putExtra("weather",weather);
                            view.findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
                            startActivity(showDetail);
                        } else {
                            failedRequestPopup();
                            view.findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
                        }
                    }
                }, 500);

            }
        });
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
        findViewById(R.id.mainLayout).post(new Runnable() {
            public void run() {
                popupWindow.showAtLocation(findViewById(R.id.mainLayout), Gravity.CENTER, 0, 0);
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

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


}










//package com.example.weather;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.widget.EditText;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        EditText editText = (EditText)findViewById(R.id.editTextText);
//        editText.addTextChangedListener(new TextWatcher()
//        {
//            @Override
//            public void afterTextChanged(Editable mEdit)
//            {
//                Log.d("DEBUGGING", "afterTextChanged called");
//                String text = mEdit.toString();
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after){
//                Log.d("DEBUGGING", "beforeTextChanged called");
//            }
//
//            public void onTextChanged(CharSequence s, int start, int before, int count){
//                Log.d("DEBUGGING", String.format("onTextChanged called %s", s));
//            }
//        });
//
//    }
//}