package com.example.weather;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CityAdapter extends ArrayAdapter<City>
{

    public CityAdapter(Context context, int resource, List<City> cityList)
    {
        super(context,resource,cityList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        City city = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.city_cell, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.cityName);
        ImageView iv = (ImageView) convertView.findViewById(R.id.cityFlagImage);

        tv.setText(city.getName());
        if (city.getImage() != -1) {
            iv.setImageResource(city.getImage());
        }

        return convertView;
    }
}