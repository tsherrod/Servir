package com.example.tsherrod.servir;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Location extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
    }

    public void buttonOnClick(View v) {

        /*Next button from Main page to Type*/
        if (v.getId() == R.id.locationNext) {
            Intent i = new Intent(Location.this, SelectArea.class);
            startActivity(i);
        }
    }
}
