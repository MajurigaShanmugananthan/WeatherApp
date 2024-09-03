package com.example.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ListView list;
    private String selectedUnit;
    private String district = "Colombo"; // Default district

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the default night mode to follow the system theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve the saved temperature unit from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE);
        selectedUnit = sharedPreferences.getString("TemperatureUnit", "Celsius");

        list = findViewById(R.id.list_view);

        new FetchData().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 1);
            return true;

        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            district = data.getStringExtra("district");
            // Refresh data when returning from settings
            new FetchData().execute();
        }
    }

    public class FetchData extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        String forecastJsonStr = null;

        private BufferedReader reader;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if (forecastJsonStr != null) {
                String[] date_list = new String[20];
                String[] temp_list = new String[20];
                String[] humidity_list = new String[20];
                String[] description_list = new String[20];
                Integer[] icon_list = new Integer[20];

                TextView txtTemp = findViewById(R.id.txtTemp);
                txtTemp.setText(R.string.temperature_text);
                TextView txtDescription = findViewById(R.id.txtDescription);
                TextView txtFeelsLike = findViewById(R.id.txtFeelsLike);

                try {
                    JSONObject weather_object = new JSONObject(forecastJsonStr);
                    JSONArray data_list = weather_object.getJSONArray("list");
                    for (int i = 0; i < data_list.length(); i++) {
                        JSONObject value_object = data_list.getJSONObject(i);
                        date_list[i] = value_object.getString("dt_txt");
                        JSONObject main_object = value_object.getJSONObject("main");
                        temp_list[i] = main_object.getString("temp");
                        humidity_list[i] = main_object.getString("humidity");
                        JSONArray weather_array = value_object.getJSONArray("weather");
                        JSONObject weather_array_object = weather_array.getJSONObject(0);
                        description_list[i] = weather_array_object.getString("description");
                        icon_list[i] = getApplicationContext().getResources().getIdentifier("@drawable/pic_" +
                                weather_array_object.getString("icon"), "drawable", getApplicationContext().getPackageName());
                    }
                    CustomListAdapter adapter = new CustomListAdapter(MainActivity.this, date_list, temp_list, icon_list);
                    list.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent detailActivity = new Intent(MainActivity.this, Detail.class);
                        detailActivity.putExtra("date", date_list[i]);
                        detailActivity.putExtra("temperature", temp_list[i]);
                        detailActivity.putExtra("description", description_list[i]);
                        detailActivity.putExtra("humidity", humidity_list[i]);
                        detailActivity.putExtra("icon", icon_list[i]);
                        startActivity(detailActivity);
                    }
                });
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                final String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast";
                final String API_KEY = "bb1242ed514dba1f10cabaaa78d88a4e";
                final String UNIT_PARAM = selectedUnit.equals("Celsius") ? "metric" : "imperial";
                URL url = new URL(BASE_URL + "?q=" + district + "&cnt=20&units=" + UNIT_PARAM + "&appid=" + API_KEY);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("MainActivity", "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }
            return forecastJsonStr;
        }
    }
}
