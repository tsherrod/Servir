package com.example.tsherrod.servir;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.components.YAxis.AxisDependency;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;


import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.TileOverlay;

import android.widget.SeekBar;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.tsherrod.servir.MapsActivity.imageJson;
import static com.example.tsherrod.servir.MapsActivity.mMap;
import static com.example.tsherrod.servir.MapsActivity.timeJson;

public class Finish extends AppCompatActivity implements OnChartValueSelectedListener, OnSeekBarChangeListener, OnMapReadyCallback {
    public LatLngBounds curScreen =mMap.getProjection().getVisibleRegion().latLngBounds;
    private LineChart mChart;
    private ArrayList <Integer>timeSeriesDates = new ArrayList<Integer>();

    private static final int TRANSPARENCY_MAX = 100;
    private static final String MAP_URL =
           "https://earthengine.googleapis.com/map/";
    private TileOverlay mapTiles;
    private SeekBar mTransparencyBar;
    private double ycoord= mMap.getCameraPosition().target.latitude;
    private double xcoord = mMap.getCameraPosition().target.longitude;


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
//TODO this frikkin graph
        mChart = findViewById(R.id.lineChart);
        mChart.setOnChartValueSelectedListener(this);
        //List of entries containing the coordinates
        List<Entry> timeSeriesCoords = new ArrayList<Entry>();

        mTransparencyBar = (SeekBar) findViewById(R.id.transparencySeekBar);
        mTransparencyBar.setMax(TRANSPARENCY_MAX);
        mTransparencyBar.setProgress(0);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.tilemap);
        mapFragment.getMapAsync(this);


if (timeJson!=null) {
    try {
        JSONArray timeSeries = timeJson.getJSONArray("timeseries");

        for (int i = 0; i < timeSeries.length(); i++) {
            JSONArray coords = timeSeries.getJSONArray(i);


            Entry coordsEntry = new Entry((float) coords.getDouble(0), (float) coords.getDouble(1));
            timeSeriesCoords.add(coordsEntry);
        }


        LineDataSet setLine = new LineDataSet(timeSeriesCoords, "timeseries");
        setLine.setAxisDependency(AxisDependency.LEFT);
        List<ILineDataSet> dataSet = new ArrayList<ILineDataSet>();
        dataSet.add(setLine);
        LineData data = new LineData(dataSet);
        mChart.setData(data);
        mChart.invalidate(); // refresh

        XAxis xAxis = mChart.getXAxis();
        //xAxis.setGranularity(1f);
        xAxis.setValueFormatter(createDateFormatter());
        xAxis.setLabelRotationAngle(-90);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);// minimum axis-step (interval) is 1

    } catch (JSONException e) {
        e.printStackTrace();
    }

  }
}



//***********************************************************/
// Map Methods
//**********************************************************/

@Override
public void onMapReady(GoogleMap tilemap) {
    tilemap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    if( imageJson != null) {
        tilemap.animateCamera(CameraUpdateFactory.newLatLngZoom(curScreen.getCenter(), 16));
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                String s = null;
                try {
                   s = MAP_URL + imageJson.get("mapid") + "/" + zoom + "/" + x + "/" + y +"?token=" + imageJson.get("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }

        };

        mapTiles = tilemap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
        mTransparencyBar.setOnSeekBarChangeListener((OnSeekBarChangeListener) this);
    }
}

    public void setFadeIn(View v) {
        if (mapTiles == null) {
            return;
        }
        mapTiles.setFadeIn(((CheckBox) v).isChecked());
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mapTiles != null) {
            mapTiles.setTransparency((float) progress / (float) TRANSPARENCY_MAX);
        }
    }

/**********************************************************/



    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }


    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }



//Displays date on x-axis as MMM yyyy format
    IAxisValueFormatter createDateFormatter() {
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Date date = new Date((long) value);
                SimpleDateFormat fmt;

                fmt = new SimpleDateFormat("MMM yyyy"); //TODO remove after tests and add switch
                String s = fmt.format(date);
                return s;
            }

            // we don't draw numbers, so no decimal digits needed
            public int getDecimalDigits() {
                return 0;
            }
        };
        return formatter;
    }

}

