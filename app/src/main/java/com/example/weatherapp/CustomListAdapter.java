package com.example.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] item_date;
    private final String[] item_temp;
    private final Integer[] icons;

    public CustomListAdapter(Activity context, String[] item_date, String[] item_temp, Integer[] icons) {
        super(context, R.layout.my_list, item_date);
        this.context = context;
        this.item_date = item_date;
        this.item_temp = item_temp;
        this.icons = icons;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.my_list, null, true);
        TextView txtDate = (TextView) rowView.findViewById(R.id.txt_date);
        TextView txtTemp = (TextView) rowView.findViewById(R.id.txt_temp);
        ImageView imgIcon = (ImageView) rowView.findViewById(R.id.icon);

        // Retrieve the temperature unit from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherAppPreferences", Context.MODE_PRIVATE);
        String selectedUnit = sharedPreferences.getString("TemperatureUnit", "Celsius");

        txtDate.setText(item_date[position]);

        // Adjust the temperature display based on the selected unit
        if ("Celsius".equals(selectedUnit)) {
            txtTemp.setText(item_temp[position] + "°C");
        } else {
            // Convert Celsius to Fahrenheit if necessary
            double tempCelsius = Double.parseDouble(item_temp[position]);
            double tempFahrenheit = (tempCelsius * 9 / 5) + 32;
            txtTemp.setText(String.format("%.1f", tempFahrenheit) + "°F");
        }

        imgIcon.setImageResource(icons[position]);
        return rowView;
    }
}
