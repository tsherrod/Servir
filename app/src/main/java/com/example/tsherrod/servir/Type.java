package com.example.tsherrod.servir;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public class Type extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    public static String indexText = "NDVI";
    public static String startDateToSend;
    public static String endDateToSend;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        Spinner spinner = findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_list, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationIcon(R.drawable.houseicon);

        Button moreInfoBtn = (Button) findViewById(R.id.moreinfobtn);
        moreInfoBtn.bringToFront();

       /* BEGIN CALENDAR SELECTION*/

        TextView dateTitleTV = (TextView) findViewById(R.id.dateTitleTV);

            // capture our View elements for the start date function
        startDateDisplay = (TextView) findViewById(R.id.startDateDisplay);
        startPickDate = (ImageButton) findViewById(R.id.startPickDate);
        startPickDate.bringToFront();
            // get the current date
        startDate = Calendar.getInstance();

            // add a click listener to the button
        startPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDateDialog(startDateDisplay, startDate);
            }
        });

            // capture our View elements for the end date function
        endDateDisplay = (TextView) findViewById(R.id.endDateDisplay);
        endPickDate = (ImageButton) findViewById(R.id.endPickDate);
        endPickDate.bringToFront();
            // get the current date
        endDate = Calendar.getInstance();

            // add a click listener to the button
        endPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDateDialog(endDateDisplay, endDate);
            }
        });

            // display the current date (this method is below)
        updateDisplay(startDateDisplay, startDate);
        updateDisplay(endDateDisplay, endDate);
        endDateDisplay.setSelected(false);
        startDateDisplay.setSelected(true);

       /* END CALENDAR SELECTION*/
    }


//shows choice
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        indexText= parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), indexText, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Type.this, MapsActivity.class);
        intent.putExtra("mySpinnerValue", indexText);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public TextView startDateDisplay;
    public TextView endDateDisplay;
    private ImageButton startPickDate;
    private ImageButton endPickDate;
    public static Calendar startDate;
    public static Calendar endDate;


    static final int DATE_DIALOG_ID = 0;

    private TextView activeDateDisplay;
    private Calendar activeDate;

    public void updateDisplay(TextView dateDisplay, Calendar date) {
        dateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(date.get(Calendar.MONTH) + 1).append("-")
                        .append(date.get(Calendar.DAY_OF_MONTH)).append("-")
                        .append(date.get(Calendar.YEAR)).append(" "));


    }

    public void showDateDialog(TextView dateDisplay, Calendar date) {
        activeDateDisplay = dateDisplay;
        activeDate = date;
        showDialog(DATE_DIALOG_ID);
    }

    private OnDateSetListener dateSetListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if(startDate.getTime().after(endDate.getTime()) || startDate.getTime().equals(endDate.getTime())){
                Toast.makeText(view.getContext(), "Start Date must be before End Date", Toast.LENGTH_SHORT).show();
            }
           /* else if (startDate.getTime().before()){

            }*/

            else {
                activeDate.set(Calendar.YEAR, year);
                activeDate.set(Calendar.MONTH, monthOfYear);
                activeDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
       /*       startDate.set(Calendar.YEAR, year);
                startDate.set(Calendar.MONTH, monthOfYear);
                startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);*/
                updateDisplay(activeDateDisplay, activeDate);


                unregisterDateDisplay();
            }
        }
    };

    private void unregisterDateDisplay() {
        activeDateDisplay = null;
        activeDate = null;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, dateSetListener, activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void buttonOnClick(View v) {
        // Next button from Type to Location
        if (v.getId() == R.id.typeNext) {
            setStartDateToSend();
            Intent i = new Intent(Type.this, MapsActivity.class);
            startActivity(i);
        }
        else if(v.getId() == R.id.moreinfobtn){
            MoreInfoMessage();
        }

    }

    private void setStartDateToSend(){
        startDateToSend= String.valueOf(startDateDisplay.getText());
    }
    public static String getStartDate(){
        startDateToSend =startDateToSend.replace(" ", "");
        String[] sourceSplit= startDateToSend.split("-");
        int year= Integer.parseInt(sourceSplit[2]);
        int month=Integer.parseInt(sourceSplit[0]);
        int day = Integer.parseInt(sourceSplit[1]);

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year,month-1,day);

        Date date= calendar.getTime();
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

        return myFormat.format(date);
    }
    private void setEndDateToSend(){
        endDateToSend = String.valueOf(startDateDisplay.getText());
    }
    public String getEndDate(){
        return endDateDisplay.toString();
    }
    public void MoreInfoMessage(){

    }
}


