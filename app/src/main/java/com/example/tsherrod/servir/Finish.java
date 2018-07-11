package com.example.tsherrod.servir;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.support.v7.widget.Toolbar;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

import static com.example.tsherrod.servir.MapsActivity.mMap;
import static com.example.tsherrod.servir.MapsActivity.response;
import static com.example.tsherrod.servir.MapsActivity.response2;
import static com.example.tsherrod.servir.MapsActivity.timeJson;
import static com.example.tsherrod.servir.R.id.startDateText;
import static com.example.tsherrod.servir.R.id.text;
import static com.example.tsherrod.servir.R.id.type_spinner;

public class Finish extends AppCompatActivity{
    public LatLngBounds curScreen =mMap.getProjection().getVisibleRegion().latLngBounds;;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.houseicon);


        //GET THE VISIBLE REGION'S COORDINATES
        /*LatLngBounds curScreen = mMap.getProjection().getVisibleRegion().latLngBounds;*/


        TextView responseView = findViewById(R.id.reponseText);
        responseView.setText(Arrays.toString(new JSONObject[]{timeJson}));


        //northeast corner
        double northeastLat = curScreen.northeast.latitude;
        String northeastLatString = Double.toString(northeastLat);
        double northeastLng = curScreen.northeast.longitude;
        String northeastLngString = Double.toString(northeastLng);

            //southwest corner
        double southwestLat = curScreen.southwest.latitude;
        String southwestLatString = Double.toString(southwestLat);
        double southwestLng = curScreen.southwest.longitude;
        String southwestLngString= Double.toString(southwestLng);

            //northwest corner = southwestLng & northeastLat
            //southeast corner = northeastLng & southwestLat

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Northeast Latitude: " + northeastLatString + "\nNortheast Longitude: " + northeastLngString +
                            "\nSouthwest Latitude: " + southwestLatString + "\nSouthwest Longitude: " + southwestLngString +
                            "\n Northwest Latitude: " + northeastLatString + "\nNorthwest Longitute: " + southwestLngString +
                            "\n Southeast Latitude: " + southwestLatString + "\n Southeast Longitude: " + northeastLngString +
                            "\n\n\n curScre");
       //END VISIBLE REGION'S COORDINATES


        //OTHER PARAMS
            //start date
        TextView startDateText = findViewById(R.id.startDateText);
        startDateText.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(Type.startDate.get(Calendar.MONTH) + 1).append("-")
                .append(Type.startDate.get(Calendar.DAY_OF_MONTH)).append("-")
                .append(Type.startDate.get(Calendar.YEAR)).append(" "));

            //end date
        TextView endDateText = findViewById(R.id.endDateText);
        endDateText.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(Type.endDate.get(Calendar.MONTH) + 1).append("-")
                .append(Type.endDate.get(Calendar.DAY_OF_MONTH)).append("-")
                .append(Type.endDate.get(Calendar.YEAR)).append(" "));

            //type

        String text = Type.indexText;
        TextView typeText = findViewById(R.id.typeText);
        typeText.setText(text);


        //END OTHER PARAMS
    }
}

