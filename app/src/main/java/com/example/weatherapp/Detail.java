package com.example.weatherapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** @noinspection deprecation*/
public class Detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        TextView txtDate = findViewById(R.id.txtDate2);
        TextView txtTemp = findViewById(R.id.txtTemp2);
        TextView txtDes = findViewById(R.id.txtDes2);
        TextView txtHumidity = findViewById(R.id.txtHumidity2);
        ImageView imgIcon = findViewById(R.id.imgIcon2);

        txtDate.setText(intent.getStringExtra("date"));
        txtTemp.setText(intent.getStringExtra("temperature")+"°C");
        txtDes.setText(intent.getStringExtra("description"));
        txtHumidity.setText("Humidity:"+intent.getStringExtra("humidity")+"%");
        imgIcon.setImageResource(intent.getIntExtra("icon",R.drawable.pic_01d));

    }
}