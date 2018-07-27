package com.example.tsherrod.servir;


import android.annotation.SuppressLint;
import android.R;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.IMarker;
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
import com.github.mikephil.charting.components.MarkerView;

import com.github.mikephil.charting.utils.MPPointF;
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
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.IOException;
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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import static com.example.tsherrod.servir.MapsActivity.getTileProvider;
import static com.example.tsherrod.servir.MapsActivity.imageJson;
import static com.example.tsherrod.servir.MapsActivity.mMap;
import static com.example.tsherrod.servir.MapsActivity.mapTiles;
import static com.example.tsherrod.servir.MapsActivity.timeJson;

public class Finish extends AppCompatActivity implements OnChartValueSelectedListener, OnSeekBarChangeListener, OnMapReadyCallback {
    public LatLngBounds curScreen =mMap.getProjection().getVisibleRegion().latLngBounds;
    private LineChart mChart;
    public ProgressBar progressBar;
    public HorizontalScrollView horizontalScrollView;
    public TextView loadingTV;
    private static final String MAP_URL =
            "https://earthengine.googleapis.com/map/";
    public String url = "http://collect.earth:8888/timeSeriesIndex2";
    public String url2 = "http://collect.earth:8888/ImageCollectionbyIndex";
    private static final int TRANSPARENCY_MAX = 100;
    public static TileOverlay mapTiles;
    private SeekBar mTransparencyBar;
    public static GoogleMap tilemap;
    public TextView displayTV;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish);

        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        horizontalScrollView =(HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        loadingTV = (TextView) findViewById(R.id.loadingTV);
        progressBar.setVisibility(View.GONE);
        loadingTV.setVisibility(View.GONE);
        //horizontalScrollView.setVisibility(View.GONE);
        mTransparencyBar = (SeekBar) findViewById(R.id.transparencySeekBar);
        mTransparencyBar.setMax(TRANSPARENCY_MAX);
        mTransparencyBar.setProgress(0);

        //final LatLngBounds curScreen = mMap.getProjection().getVisibleRegion().latLngBounds;
        displayTV = findViewById(R.id.displayTV);
        displayTV.setText(Type.getStartDate());

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationIcon(R.drawable.houseicon);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.tilemap);
        mapFragment.getMapAsync(this);

        mTransparencyBar.bringToFront();

        doPostRequest(curScreen);
        //createLineGraph();
        //createMapLayer(tilemap);

}

    public void doPostRequest(LatLngBounds curScreen) {
        if (isNetworkAvailable()) {
            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.readTimeout(5, TimeUnit.MINUTES);
            b.writeTimeout(5, TimeUnit.MINUTES);
            OkHttpClient client = b.build();
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");

            //builds imageCollectionBody
            RequestBody body2 = RequestBody.create(JSON, imageCollectionBody(curScreen).toString());

            Log.d("OKHTTP3", "Image RequestBody Created.");

            //Image Collection Request
            final Request newReq2 = new Request.Builder()
                    .url(url2)
                    .post(body2)
                    .build();

            //Image Collection Call
            client.newCall(newReq2).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //TODO what to do when image call fails
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String response2 = response.body().string();

                    Finish.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                imageJson = new JSONObject(response2);
                                createLineGraph();
                                createMapLayer(tilemap);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }

    // TODO network available function
     private boolean isNetworkAvailable() {

       /* boolean isAvailable = false;
            ConnectivityManager manager = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert manager != null;
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                isAvailable = true;
            }*/
         return true;
     }

    //TODO user input
    public JSONObject imageCollectionBody(LatLngBounds curScreen) {
         JSONObject actualData2 = new JSONObject();

         try {
             //VISPARAMS JSON OBJECT
             JSONObject visParams = new JSONObject();
             try {
                 visParams.put("bands", "EVI");
                 visParams.put("max", "0.3");
                 visParams.put("min", "");
             } catch (JSONException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }

             actualData2.put("collectionNameTimeSeries", "");
             actualData2.put("geometry", GetCoords(curScreen));
             actualData2.put("index", getIndexImg());

             //  actualData2.put("dateFromTimeSeries", Type.GetStartDate());
             //  actualData2.put("dateToTimeSeries", Type.GetEndDate());

             //TODO take out
             actualData2.put("dateFrom", "2010-01-01");
             actualData2.put("dateTo", "2010-12-31");
             actualData2.put("visParams", visParams);

         } catch (JSONException e) {
             Log.d("OKHTTP3", "JSON Exception");
             e.printStackTrace();
         }

         return actualData2;
     }

    //TODO user input
    public JSONObject timeSeriesBody(LatLngBounds curScreen) {

         JSONObject actualData = new JSONObject();

         try {

             actualData.put("collectionNameTimeSeries", "");
             actualData.put("geometry", GetCoords(curScreen));
             actualData.put("indexName", getIndexTime());
             //  actualData.put("dateFromTimeSeries", Type.GetStartDate());
             //  actualData.put("dateToTimeSeries", Type.GetEndDate());

             //For Testing Purposes TODO take out
             actualData.put("dateFromTimeSeries", "2010-01-01");
             actualData.put("dateToTimeSeries", "2010-12-31");

         } catch (JSONException e) {
             Log.d("OKHTTP3", "JSON Exception");
             e.printStackTrace();
         }
         return actualData;
     }

    public String getIndexImg(){
        if(Type.indexText == "EVI2"){
            return "EVI";
        }
        else return Type.indexText;
    }
    public String getIndexTime(){
        if(Type.indexText == "NDMI"){
            return "NDVI";
        }
        else return Type.indexText;
    }

    public JSONArray GetCoords(LatLngBounds curScreen) {
        JSONArray geometry = new JSONArray();
        JSONArray coord0 = new JSONArray();
        JSONArray coord1 = new JSONArray();
        JSONArray coord2 = new JSONArray();
        JSONArray coord3 = new JSONArray();
        JSONArray coord4 = new JSONArray();

        try {
              /*  coord0.put(0, -85.25507948537992);
                coord0.put(1, 40.10371721058706);
                coord1.put(0, -85.25425851083126);
                coord1.put(1, 40.103717207677605);
                coord2.put(0, -85.25425851083126);
                coord2.put(1,40.10308678503157);
                coord3.put(0, -85.25507948537992);
                coord3.put(1, 40.10308678794103);
                coord4.put(0, -85.25507948537992);
                coord4.put(1, 40.10371721058706);*/
            coord0.put(0, curScreen.northeast.longitude);
            coord0.put(1, curScreen.northeast.latitude);
            coord1.put(0, curScreen.northeast.longitude);
            coord1.put(1, curScreen.southwest.latitude);
            coord2.put(0, curScreen.southwest.longitude);
            coord2.put(1, curScreen.southwest.latitude);
            coord3.put(0, curScreen.southwest.longitude);
            coord3.put(1, curScreen.northeast.latitude);
            coord4.put(0, curScreen.northeast.longitude);
            coord4.put(1, curScreen.northeast.latitude);
            geometry.put(0, coord0);
            geometry.put(1, coord1);
            geometry.put(2, coord2);
            geometry.put(3, coord3);
            geometry.put(4, coord4);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return geometry;
    }

    //TODO Start and end date
    public String GetStartDate() {
        /*String date= String.valueOf(Type.startDate.get(Calendar.YEAR)) + "-"
                + String.valueOf(Type.startDate.get(Calendar.MONTH) + 1) + "-"
                + String.valueOf(Type.startDate.get(Calendar.DAY_OF_MONTH));*/
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(Type.startDate.getTime());

        return date;

    }

    public void createLineGraph(){
        mChart = findViewById(R.id.lineChart);
        IMarker marker= new MyMarkerView(this,R.layout.custom_marker);
        mChart.setMarker(marker);
        Description description = new Description();
        description.setText("");
        mChart.setDescription(description);
        mChart.setDrawBorders(true);
        List<Entry> timeSeriesCoords = new ArrayList<Entry>();
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setFormToTextSpace(2);
        //setLine.setHighlightEnabled(false);
        mChart.setHighlightPerTapEnabled(true);
        XAxis xAxis = mChart.getXAxis();
        //xAxis.setGranularity(1f);
        xAxis.setValueFormatter(createDateFormatter());
        xAxis.setLabelRotationAngle(-60);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.setOnChartValueSelectedListener(this);
        mChart.invalidate();
        try {
            //JSONArray that stores received data's array titled "timeseries"
            JSONArray timeSeries = timeJson.getJSONArray("timeseries");

            //iterates through timeSeries Array, adds each array wi
            for (int i = 0; i < timeSeries.length(); i++) {
                JSONArray coords = timeSeries.getJSONArray(i);
                Entry coordsEntry = new Entry((float) coords.getDouble(0), (float) coords.getDouble(1));
                timeSeriesCoords.add(coordsEntry);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //create the line graph after storing all points in timeSeriesCoords
        LineDataSet setLine = new LineDataSet(timeSeriesCoords, Type.indexText +" Time Series");
        setLine.setAxisDependency(AxisDependency.LEFT);
        setLine.setDrawValues(false);
        List<ILineDataSet> dataSet = new ArrayList<ILineDataSet>();
        dataSet.add(setLine);
        LineData data = new LineData(dataSet);
        mChart.setData(data);

        mChart.invalidate(); // refresh

    }


    public interface OnChartValueSelectedListener {
        /**
         * Called when a value has been selected inside the chart.
         *
         * @param e The selected Entry.
         * @param h The corresponding highlight object that contains information
         * about the highlighted position
         */

        public void onValueSelected(Entry e, Highlight h);
        /**
         * Called when nothing has been selected or an "un-select" has been made.
         */
        public void onNothingSelected();
    }

    public void createMapLayer(GoogleMap tilemap){
               Log.d("OKHTTP3", "running TileURL");
               Log.d("OKHTTP3", "imageJSON: " +  imageJson.toString());

                TileProvider tileProvider = new UrlTileProvider(256, 256) {

                    @Override
                    public synchronized URL getTileUrl(int x, int y, int zoom) {
                        String s = null;
                        Log.d("OKHTTP3", "getTileURL method");
                        try {
                            s = MAP_URL + imageJson.get("mapid") + "/" + zoom + "/" + x + "/" + y +"?token=" + imageJson.get("token");
                            Log.d("OKHTTP3", "creating new map url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        URL url = null;
                        try {
                            url = new URL(s);
                            Log.d("OKHTTP3", "new url created");

                        } catch (MalformedURLException e) {
                            throw new AssertionError(e);
                        }
                        return url;
                    }
                };

                //adds tile overlay to map
                mapTiles = tilemap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
                mTransparencyBar.setOnSeekBarChangeListener((OnSeekBarChangeListener) this);
                mTransparencyBar.bringToFront();

    }

    //Displays date on x-axis as MMM yyyy format
    IAxisValueFormatter createDateFormatter() {
        return new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Date date = new Date((long) value);
                SimpleDateFormat fmt;

                fmt = new SimpleDateFormat("MMM yyyy");
                String s = fmt.format(date);
                return s;
            }

            // we don't draw numbers, so no decimal digits needed
            public int getDecimalDigits() {
                return 0;
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        tilemap=googleMap;
        tilemap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        tilemap.animateCamera(CameraUpdateFactory.newLatLngZoom(curScreen.getCenter(),mMap.getCameraPosition().zoom));
    }

  /*  public void setFadeIn(View v) {
        if (mapTiles == null) {
            return;
        }
        mapTiles.setFadeIn(((CheckBox) v).isChecked());
    }
*/
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

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }


    public class MyMarkerView extends MarkerView {

        private TextView tvContent;

        public MyMarkerView(Finish context, int layoutResource) {
            super(context, layoutResource);

            // find your layout components
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            String xVal = mChart.getXAxis().getValueFormatter().getFormattedValue(e.getX(), mChart.getXAxis());
            float yVal = e.getY();
            tvContent.setText(Type.indexText + ": " + yVal + "\nDate: " + xVal);

            // this will perform necessary layouting
            super.refreshContent(e, highlight);
        }

        private MPPointF mOffset;

        @Override
        public MPPointF getOffset() {

            if(mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
            }

            return mOffset;
        }
    }


}

