package com.example.tsherrod.servir;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SelectArea extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectarea);
    }

    public void buttonOnClick(View v) {
        //HOME BUTTON

        //Next button from Area Page to Finish
        if (v.getId() == R.id.areaNext) {
            Intent i = new Intent(SelectArea.this, Finish.class);
            startActivity(i);
        }
    }
}
