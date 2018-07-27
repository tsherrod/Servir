package com.example.tsherrod.servir;

import android.Manifest;
import android.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.android.gms.maps.model.VisibleRegion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Connection;
import okhttp3.WebSocket;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String MAP_URL =
            "https://earthengine.googleapis.com/map/";
    private static final int MY_REQUEST_INT = 177;
    private Context mcontext;
    public static GoogleMap mMap;
    public static GoogleMap tilemap;
    public static TileOverlay mapTiles;
    public static JSONObject timeJson;
    public static JSONArray timeName;
    public static JSONObject imageJson;
    private static TileProvider tileProvider;
    public static TileProvider tileProviderToSend;
    public static ProgressBar mapProgressBar;
    public static SupportMapFragment mapFragment;
    public static Button mapsNext;
    public static View mapsView;

    public String url = "http://collect.earth:8888/timeSeriesIndex2";
    public String url2 = "http://collect.earth:8888/ImageCollectionbyIndex";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapsView = findViewById(R.id.maplayout);
        mapsNext = (Button) findViewById(R.id.mapsNext);
        mapProgressBar=(ProgressBar) findViewById(R.id.mapProgressBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationIcon(R.drawable.houseicon);
        //ProgressBar progressBar = findViewById(R.id.progressBar2);
        final EditText TFaddress= findViewById(R.id.TFaddress);
        TFaddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    onSearch(v);
                    handled = true;
                    TFaddress.setEnabled(true);
                }
                return handled;
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

 /*       TextView startDateTV = findViewById(R.id.startDateTV);
        startDateTV.setText(String.valueOf(Type.startDate.get(Calendar.DAY_OF_MONTH)));*/

    }

    // SEARCH MAPS
    public void onSearch(View view) {
        EditText location_tf = (EditText) findViewById(R.id.TFaddress);
        String location = location_tf.getText().toString();
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            location_tf.setEnabled(false);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

            //   To add a marker to new location
            /*mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));*/

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_INT);
            }
            return;
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    public void buttonOnClick(View v) {
        if (v.getId() == R.id.mapsNext) {
            LatLngBounds curScreen = mMap.getProjection().getVisibleRegion().latLngBounds;
            doPostRequest(curScreen);
            /*Intent i = new Intent(MapsActivity.this, Finish.class);
            startActivity(i);*/
        }
    }

    public void doPostRequest(LatLngBounds curScreen) {
        if (isNetworkAvailable()) {
            mapsNext.setClickable(false);
            mapsView.setAlpha((float) 0.5);
            mapProgressBar.setVisibility(View.VISIBLE);
            mMap.getUiSettings().setAllGesturesEnabled(false);
            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.readTimeout(5, TimeUnit.MINUTES);
            b.writeTimeout(5, TimeUnit.MINUTES);
            OkHttpClient client = b.build();
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");

            //builds timeSeriesBody
            RequestBody body = RequestBody.create(JSON, timeSeriesBody(curScreen).toString());
            //builds imageCollectionBody
            RequestBody body2 = RequestBody.create(JSON, imageCollectionBody(curScreen).toString());

            Log.d("OKHTTP3", "RequestBody Created.");
            //Time Series Request
            final Request newReq = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            //Time Series Call
            client.newCall(newReq).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //TODO what to do when time call fails
                    mapProgressBar.setVisibility(View.INVISIBLE);
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String response1 = response.body().string();
                    Log.d("OKHTTP3", "time response: " + response1);

                    //if the timeseries response starts with 't', store the response in a JSONObject
                    //  else, display an error dialog
                    if(response1.charAt(2)== 't'){
                        MapsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //After receiving response, builds graph
                                    //received data in format:
                                    //      {"timeseries":[[date, value], [date, value],...]}

                                    timeJson = new JSONObject(response1);
                                    mapsView.setAlpha((float) 1.0);
                                    mapProgressBar.setVisibility(View.INVISIBLE);
                                    mapsNext.setClickable(true);
                                    mMap.getUiSettings().setAllGesturesEnabled(true);
                                    Intent i = new Intent(MapsActivity.this, Finish.class);
                                    startActivity(i);
                                    //horizontalScrollView.setVisibility(View.VISIBLE);
                                    //loadingTV.setVisibility(View.INVISIBLE);
                                    //progressBar.setVisibility(View.INVISIBLE);
                                    //createLineGraph();
                                    //txtstring.setText(json.getJSONArray("timeseries").toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    else{
                        mapProgressBar.setVisibility(View.INVISIBLE);


                        showDialog();
                    }
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
    public void showDialog(){
        Log.d("OKHTTP3", "entered show dialogue");
        FragmentManager manager= getFragmentManager();
        LargeAreaFragment largeArea = new LargeAreaFragment();
        largeArea.show(manager, "my dialog");
    }
    @SuppressLint("ValidFragment")
    public static class LargeAreaFragment extends DialogFragment implements View.OnClickListener {
        Button gotit;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View view=inflater.inflate(R.layout.large_area, null);
            gotit=(Button) view.findViewById(R.id.gotit);
            gotit.setOnClickListener(this);

            setCancelable(false);
            return view;
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.gotit){
                dismiss();
                mapsNext.setClickable(true);
                mapsView.setAlpha((float) 0.5);
                mMap.getUiSettings().setAllGesturesEnabled(true);
            }
        }
    }


}



