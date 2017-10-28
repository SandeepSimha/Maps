package com.bbva.my.bbvacompasaplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Sandeep Cherukuri on 10/27/17.
 */

public class LocationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        TextView tvAddress = (TextView) findViewById(R.id.tv_address);
        TextView tvLocationAddress = (TextView) findViewById(R.id.tv_location_address);
        TextView tvLatitude = (TextView) findViewById(R.id.tv_longitude);
        TextView tvLongitude = (TextView) findViewById(R.id.tv_latitude);
        Button directions = (Button) findViewById(R.id.directions);

        Intent intent = getIntent();
        String locationTitle = intent.getStringExtra("title");
        String locationAddress = intent.getStringExtra("location_address");
        final double currentLatitude = intent.getDoubleExtra("current_latitude", 0);
        final double currentLongitude = intent.getDoubleExtra("current_longitude", 0);
        final double destinationLatitude = intent.getDoubleExtra("destination_latitude", 0);
        final double destinationLongitude = intent.getDoubleExtra("destination_longitude", 0);

        tvAddress.setText("Title = " + locationTitle);
        tvLocationAddress.setText("Address = " + locationAddress);
        tvLatitude.setText("Latitude = " + String.valueOf(destinationLatitude));
        tvLongitude.setText("Longitude = " + String.valueOf(destinationLongitude));

        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + currentLatitude + "," + currentLongitude + "&daddr=" + destinationLatitude + "," + destinationLongitude));
                startActivity(intent1);
            }
        });
    }
}