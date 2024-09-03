package com.example.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText editTextDistrict;
    private Spinner spinnerTemperatureUnit;
    private TextView textViewWeatherResult;
    private String selectedUnit = "Celsius"; // Default unit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextDistrict = findViewById(R.id.editTextDistrict);
        spinnerTemperatureUnit = findViewById(R.id.spinnerTemperatureUnit);
        textViewWeatherResult = findViewById(R.id.textViewWeatherResult);
        Button buttonFetchWeather = findViewById(R.id.buttonFetchWeather);

        spinnerTemperatureUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUnit = parent.getItemAtPosition(position).toString();
                // Save the selected unit to SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("TemperatureUnit", selectedUnit);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        buttonFetchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String district = editTextDistrict.getText().toString();
                if (district.isEmpty()) {
                    Toast.makeText(SettingsActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                } else {
                    fetchWeatherData(district, selectedUnit);
                }
            }
        });
    }

    private void fetchWeatherData(String district, String unit) {
        // Start MainActivity and pass the district name
        Intent resultIntent = new Intent();
        resultIntent.putExtra("district", district);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
