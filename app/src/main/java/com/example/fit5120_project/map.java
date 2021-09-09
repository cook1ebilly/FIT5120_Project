package com.example.fit5120_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import database.MyContext;
import database.PermissionUtils;
import database.TopicModel;
import database.TopicUtils;

public class map extends FragmentActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation = null;
    private GoogleMap mMap;
    private MapView mMapView;
    ImageView imageView;
    List<Address> addressList;
    String place;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Marker Mymarker;
    TextView tv_topic;
    String Latitude, Longitude;
    List<TopicModel> topicModelList = new ArrayList<>();

    private EditText start, end;


    ListView listView_s, listView_e;


    ArrayList<GooglePlaceModel> googlePlaceModels = new ArrayList<>();

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    boolean isok = false, isoke = false;
    private Button btn_go;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mMapView = findViewById(R.id.mMapView);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        imageView = findViewById(R.id.back_2);
        btn_go = findViewById(R.id.btn_go);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        end = findViewById(R.id.end);
        start = findViewById(R.id.start);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(map.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Intent intent = getIntent();
        place = intent.getStringExtra("report_place");
        tv_topic = findViewById(R.id.tv_topic);

        listView_s = (ListView) findViewById(R.id.listView_s);
        start.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    googlePlaceModels.clear();
                    setAdapters();

                } else {
                    if (!isok)
                        new map.GooglePlaces().execute(String.valueOf(s));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listView_s.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!googlePlaceModels.get(position).getPlaceName().equalsIgnoreCase("Not Found")) {

                    place = googlePlaceModels.get(position).getPlaceName();
                    start.setText(place);
                    moveToplace();
                    googlePlaceModels.clear();
                    setAdapters();
                    isok = true;
                }
            }
        });
        listView_e = (ListView) findViewById(R.id.listView_e);
        end.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    googlePlaceModels.clear();
                    setAdaptere();

                } else {
                    if (!isoke)
                        new map.GooglePlaces_e().execute(String.valueOf(s));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listView_e.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!googlePlaceModels.get(position).getPlaceName().equalsIgnoreCase("Not Found")) {

                    place = googlePlaceModels.get(position).getPlaceName();
                    end.setText(place);

                    moveToplace();
                    googlePlaceModels.clear();
                    setAdaptere();
                    isoke = true;
                }
            }
        });

    }

    public void setAdapters() {
        PlaceAdapter adapter = new PlaceAdapter(map.this, googlePlaceModels);
        listView_s.setAdapter(adapter);
    }

    public void setAdaptere() {
        PlaceAdapter adapter = new PlaceAdapter(map.this, googlePlaceModels);
        listView_e.setAdapter(adapter);
    }

    private class GooglePlaces extends AsyncTask<String, String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                // Your API key
                String key = "?key=AIzaSyDMWzhdPqiZArlLVmG9ZRVs2dJqlle0ruM";
                // components type
                String components = "&components=country:aus";
                // set input type
                String input = "&input=" + URLEncoder.encode(params[0], "utf8");
                // Building the url to the web service
                String strURL = PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON + key + components + input;
                Log.e("TAG", "doInBackground: " + strURL);


                URL url = new URL(strURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String temp;

                    while ((temp = reader.readLine()) != null) {
                        stringBuilder.append(temp);
                    }
                    result = stringBuilder.toString();
                } else {
                    result = "error";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            if (googlePlaceModels != null) {

                googlePlaceModels.clear();
            }
            try {
                JSONObject jsonObj = new JSONObject(s);
                JSONArray jsonArray = jsonObj.getJSONArray("predictions");

                if (jsonObj.getString("status").equalsIgnoreCase("OK")) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        GooglePlaceModel googlePlaceModel = new GooglePlaceModel();
                        googlePlaceModel.setPlaceName(jsonArray.getJSONObject(i).getString("description"));
                        googlePlaceModels.add(googlePlaceModel);
                    }
                } else if (jsonObj.getString("status").equalsIgnoreCase("OVER_QUERY_LIMIT")) {
                    Toast.makeText(getApplicationContext(), "You have exceeded your daily request quota for this API.", Toast.LENGTH_LONG).show();
                    GooglePlaceModel googlePlaceModel = new GooglePlaceModel();
                    googlePlaceModel.setPlaceName("Not Found");
                    googlePlaceModels.add(googlePlaceModel);
                } else {
                    GooglePlaceModel googlePlaceModel = new GooglePlaceModel();
                    googlePlaceModel.setPlaceName("Not Found");
                    googlePlaceModels.add(googlePlaceModel);
                }
                // set adapter
                setAdapters();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private class GooglePlaces_e extends AsyncTask<String, String, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                // Your API key
                String key = "?key=AIzaSyDMWzhdPqiZArlLVmG9ZRVs2dJqlle0ruM";
                // components type
                String components = "&components=country:aus";
                // set input type
                String input = "&input=" + URLEncoder.encode(params[0], "utf8");
                // Building the url to the web service
                String strURL = PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON + key + components + input;
                Log.e("TAG", "doInBackground: " + strURL);


                URL url = new URL(strURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String temp;

                    while ((temp = reader.readLine()) != null) {
                        stringBuilder.append(temp);
                    }
                    result = stringBuilder.toString();
                } else {
                    result = "error";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            if (googlePlaceModels != null) {

                googlePlaceModels.clear();
            }
            try {
                JSONObject jsonObj = new JSONObject(s);
                JSONArray jsonArray = jsonObj.getJSONArray("predictions");

                if (jsonObj.getString("status").equalsIgnoreCase("OK")) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        GooglePlaceModel googlePlaceModel = new GooglePlaceModel();
                        googlePlaceModel.setPlaceName(jsonArray.getJSONObject(i).getString("description"));
                        googlePlaceModels.add(googlePlaceModel);
                    }
                } else if (jsonObj.getString("status").equalsIgnoreCase("OVER_QUERY_LIMIT")) {
                    Toast.makeText(getApplicationContext(), "You have exceeded your daily request quota for this API.", Toast.LENGTH_LONG).show();
                    GooglePlaceModel googlePlaceModel = new GooglePlaceModel();
                    googlePlaceModel.setPlaceName("Not Found");
                    googlePlaceModels.add(googlePlaceModel);
                } else {
                    GooglePlaceModel googlePlaceModel = new GooglePlaceModel();
                    googlePlaceModel.setPlaceName("Not Found");
                    googlePlaceModels.add(googlePlaceModel);
                }
                // set adapter

                setAdaptere();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            PermissionUtils.requestPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION, 1);
        }


        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                MyContext.topicModelList = TopicUtils.get(MyContext.topicModelList);
                topicModelList.clear();
                if (TextUtils.isEmpty(Latitude) || TextUtils.isEmpty(Longitude)) {
                    Longitude = String.valueOf(marker.getPosition().longitude);
                    Latitude = String.valueOf(marker.getPosition().latitude);
                }
                for (int i = 0; i < MyContext.topicModelList.size(); i++) {
                    TopicModel model = MyContext.topicModelList.get(i);
                    if (model.latitude.equals(Latitude) && model.longitude.equals(Longitude)) {
                        topicModelList.add(model);
                    }
                }
                if (topicModelList.size() > 0) {
                    tv_topic.setText("Occur times:" + topicModelList.size() + "\n" + "Year:" + topicModelList.get(0).year + "\n" +
                            "Topic:" + topicModelList.get(0).topic + "\n" + "Description:" + topicModelList.get(0).description +
                            "\n" + "Address:" + getAddress(Double.valueOf(topicModelList.get(0).latitude), Double.valueOf(topicModelList.get(0).longitude)));
                    Longitude = "";
                    Latitude = "";
                } else {
                    tv_topic.setText("no data");
                }
                return false;
            }
        });
        if (MyContext.topicModelList.size() > 0) {
            for (int i = 0; i < MyContext.topicModelList.size(); i++) {
                TopicModel model = MyContext.topicModelList.get(i);
                Marker markerstar = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.valueOf(model.latitude), Double.valueOf(model.longitude)))
                        .title(model.topic));
                markerstar.setTag(0);
            }
        }
        moveToplace();
    }

    private String getAddress(double latitude, double longitude) {

        String address1 = "";
        //Geocoder通过经纬度获取具体信息
        Geocoder gc = new Geocoder(map.this, Locale.getDefault());
        try {
            List<Address> locationList = gc.getFromLocation(latitude, longitude, 1);
            if (locationList != null) {
                Address address = locationList.get(0);
                String countryName = address.getCountryName();//国家
                String countryCode = address.getCountryCode();

                String adminArea = address.getAdminArea();//省
                String adminAre = address.getSubAdminArea();
                String postcode = address.getPostalCode();
                String locality = address.getLocality();//市
                String subLocality = address.getSubLocality();//区
                String featureName = address.getThoroughfare();//街道
                String fnum = address.getFeatureName();

                String state = adminArea.substring(0, 3);

                Log.e("TAG", "getAddress: " + countryName + "——" + countryCode + "——" + adminArea + "——" +
                        locality + "——" + subLocality + "——" + featureName + "——" + fnum + "--" + adminAre);
                address1 = fnum + " " + featureName + ", " + locality + ", " + state + " " + postcode;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return address1;
    }

    private void moveToplace() {
        try {
            if (TextUtils.isEmpty(place)) {
                return;
            }
            Geocoder geocoder = new Geocoder(getApplicationContext());
            addressList = geocoder.getFromLocationName(place, 5);
            Address location = addressList.get(0);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Latitude = String.valueOf(location.getLatitude());
            Longitude = String.valueOf(location.getLongitude());
            Log.e("map", "moveToplace: " + Longitude + "......" + Latitude);
            Mymarker = mMap.addMarker(new MarkerOptions().position(latLng).title(place));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    //获取设备位置信息
    private void getDeviceLocation(final LatLng sydney) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Task locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = (Location) task.getResult();
                        //设置移动到的指定坐标和地图缩放等级(0-21)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), 4));
                    } else {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 4));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            });

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }


    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}