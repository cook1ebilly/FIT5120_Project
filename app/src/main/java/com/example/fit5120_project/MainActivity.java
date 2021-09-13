package com.example.fit5120_project;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.airbnb.lottie.L;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import database.ContactUtils;
import database.DBHelper;
import database.MyContext;
import database.PermissionUtils;
import database.SendAdapter;
import database.TopicModel;
import database.TopicUtils;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener {
    List<Address> addressList;
    RelativeLayout report, contact, data, comment, rle;
    ImageView soscall, about, map, help, helpcenter, cross1, findlocation;
    private MapView mMapView;
    EditText searchbar;
    ListView listView_s;
    LatLng dest;
    Button delete, back;
    TextView content;
    private AlertDialog dialog;
    ToggleButton find;
    ProgressDialog progressDialog;
    PolylineOptions lineOptions = null;
    Polyline polyline;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    List<TopicModel> topicModelList = new ArrayList<>();
    int[] color = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.DKGRAY, Color.BLACK, Color.MAGENTA};

    ArrayList<GooglePlaceModel> googlePlaceModels = new ArrayList<>();
    private GoogleMap mMap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    String place;
    Marker Mymarker;
    String Latitude, Longitude;
    double k1, k2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyContext.dbHelper = new DBHelper(MainActivity.this);
        Intent intent = getIntent();
        place = intent.getStringExtra("report_place");
        report = findViewById(R.id.reportevent);
        contact = findViewById(R.id.contact);
        rle = findViewById(R.id.rle3);
        data = findViewById(R.id.data);
        findlocation = findViewById(R.id.findlocation);
        cross1 = findViewById(R.id.cancel1);
        comment = findViewById(R.id.comment);
        soscall = findViewById(R.id.soscall);
        about = findViewById(R.id.information);
        searchbar = findViewById(R.id.searchbar);
        map = findViewById(R.id.searchmap);
        find = findViewById(R.id.find);
        listView_s = findViewById(R.id.listview1);
        help = findViewById(R.id.help);
        Intent i = getIntent();
        k1 = i.getDoubleExtra("k1", 0);
        k2 = i.getDoubleExtra("k2", 0);

        helpcenter = findViewById(R.id.helpcenter);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (helpcenter.getVisibility() == View.GONE) {
                    helpcenter.setVisibility(View.VISIBLE);


                }

            }
        });

        findlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyContext.mylocation, 20));

            }
        });

        cross1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchbar.setText("");
                googlePlaceModels.clear();
                setAdapters();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                rle.setVisibility(View.GONE);

            }
        });
        helpcenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (helpcenter.getVisibility() == View.VISIBLE) {
                    helpcenter.setVisibility(View.GONE);


                }
            }
        });
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mMapView = findViewById(R.id.mapview);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);


        find.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    find.setBackgroundResource(R.mipmap.incidentlocation);
                    if (MyContext.topicModelList.size() > 0) {
                        for (int i = 0; i < MyContext.topicModelList.size(); i++) {
                            TopicModel model = MyContext.topicModelList.get(i);
                            Marker markerstar = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(model.latitude), Double.valueOf(model.longitude)))
                                    .title(model.topic));
                            markerstar.setTag(0);
                        }

                        if (dest != null) {
                            drawPolylines(MyContext.mylocation, dest);
                        }
                    }


                } else {
                    find.setBackgroundResource(R.mipmap.location);
                    if (dest != null) {
                        drawPolylines(MyContext.mylocation, dest);
                    }
                    mMap.clear();


                }
            }
        });

        listView_s.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!googlePlaceModels.get(i).getPlaceName().equalsIgnoreCase("Not Found")) {

                    place = googlePlaceModels.get(i).getPlaceName();
                    searchbar.setText("");
                    dest = moveToPlaceLatng(place);
                    googlePlaceModels.clear();
                    setAdapters();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                    drawPolylines(MyContext.mylocation, dest);


                }
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rle.setVisibility(View.VISIBLE);


            }
        });

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    googlePlaceModels.clear();
                    setAdapters();

                } else {
                    new MainActivity.GooglePlaces().execute(String.valueOf(s));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent class will help to go to next activity using
                // it's object named intent.
                // SecondActivty is the name of new created EmptyActivity.
                Intent intent = new Intent(MainActivity.this, Reportissues.class);
                startActivity(intent);
                finish();
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent class will help to go to next activity using
                // it's object named intent.
                // SecondActivty is the name of new created EmptyActivity.
                Intent intent = new Intent(MainActivity.this, Aboutus.class);
                startActivity(intent);
                finish();

            }
        });
        soscall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + "000");
                intent.setData(data);
                startActivity(intent);

            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent class will help to go to next activity using
                // it's object named intent.
                // SecondActivty is the name of new created EmptyActivity.
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();

            }
        });

        //导入 topic csv

        if (TopicUtils.get(MyContext.topicModelList).size() == 0) {
            Log.e("TAG", "onCreate:00000");
            MyContext.topicModelList = TopicUtils.getList("csvnew.csv", this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (TopicModel model : MyContext.topicModelList
                    ) {
                        TopicUtils.insert(model);

                    }
                }
            }).start();
        } else {
            Log.e("TAG", "onCreate:else  " + MyContext.topicModelList.size());
        }


    }

    public void setAdapters() {
        PlaceAdapter adapter = new PlaceAdapter(MainActivity.this, googlePlaceModels);
        listView_s.setAdapter(adapter);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(false);
            }
        } else {
            PermissionUtils.requestPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION, 1);
        }


        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyContext.mylocation, 20));
        if (k1 * k2 != 0) {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(k1, k2), 20));


        }
        mMap.addMarker(new MarkerOptions().position(MyContext.mylocation).title("Your are here!").icon(BitmapDescriptorFactory.fromResource(R.mipmap.startpoint)));
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
                if (( dest != null && marker.getPosition().latitude == dest.latitude &&
                        marker.getPosition().longitude == dest.longitude)||(marker.getPosition().latitude == MyContext.mylocation.latitude &&
                        marker.getPosition().longitude == MyContext.mylocation.longitude)) {
                    return true;
                }
                if (topicModelList.size() > 0) {
                    showDialog(topicModelList);

                }
                Longitude="";
                Latitude="";
                return true;
            }
        });
        if (k1 * k2 == 0) {
            moveToplace();
        }
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(@NonNull CameraPosition cameraPosition) {
                Log.e("TAG", "onCameraChange: " + cameraPosition.zoom);
            }
        });

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

    private LatLng moveToPlaceLatng(String place) {
        LatLng latLng = null;
        try {
            if (TextUtils.isEmpty(place)) {
                return null;
            }
            Geocoder geocoder = new Geocoder(getApplicationContext());
            addressList = geocoder.getFromLocationName(place, 5);
            Address location = addressList.get(0);
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Latitude = String.valueOf(location.getLatitude());
            Longitude = String.valueOf(location.getLongitude());
            Log.e("map", "moveToPlaceLatng: " + Longitude + "......" + Latitude);
            Mymarker = mMap.addMarker(new MarkerOptions().position(latLng).title(place));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
            return latLng;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return latLng;

    }

    private void drawPolylines(LatLng origin, LatLng dest) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait, Polyline between two locations is building.");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = getDirectionsUrl(origin, dest) + "&key=AIzaSyDMWzhdPqiZArlLVmG9ZRVs2dJqlle0ruM";
        Log.e("url", url + "");
        MainActivity.DownloadTask downloadTask = new MainActivity.DownloadTask();
        downloadTask.execute(url);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            MainActivity.ParserTask parserTask = new MainActivity.ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);


                MyContext.distance = parser.parseDistance(jObject);
                double latitude = (MyContext.mylocation.latitude + dest.latitude) / 2;
                double longitude = (MyContext.mylocation.longitude + dest.longitude) / 2;
                MyContext.movelocation = new LatLng(latitude, longitude);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            progressDialog.dismiss();
            Log.d("result", result.toString());
            ArrayList points = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(12);
          /*      int [] arr = {0,1,2,3,4,5,6};
                int index=(int)(Math.random()*arr.length);
                int rand = arr[index];*/

                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);

            }

            polyline = mMap.addPolyline(lineOptions);
            mMap.addMarker(new MarkerOptions()
                    .position(dest)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.endpoint)));
            /* if (MyContext.distance)*/
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MyContext.movelocation, transZoom(MyContext.distance)));

        }
    }

    private int transZoom(int distance) {
        int zoom = 14;
        if (distance < 300) {
            zoom = 18;
        } else if (distance < 1000) {
            zoom = 17;
        } else if (distance < 2000) {
            zoom = 15;
        } else if (distance < 5000) {
            zoom = 14;
        } else if (distance < 15000) {
            zoom = 12;
        } else if (distance < 1000000) {
            zoom = 6;
        } else if (distance < 3000000) {
            zoom = 5;
        } else {
            zoom = 4;
        }

       /* 786628 6
       914629 6
        1629789 5
        2029010 5
        1533738 5
        2029875 5
        3691279 4
         891244 6
          10249  12   1663 15   9746 12   3669 14    454 17  18 <300 */
        return zoom;

    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=walking";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void showDialog(List<TopicModel> topicModelList) {
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.popwindow, null);

        delete = view.findViewById(R.id.btn_cancel);
        back = view.findViewById(R.id.btn_ok);
        content = view.findViewById(R.id.topicinfo);
        Log.e("TAG", "showDialog: "+topicModelList.get(0).id);
        content.setText("Occur times:" + " 1" + "\n" + "Year: " + topicModelList.get(0).year + "\n" +
                "Topic: " + topicModelList.get(0).topic + "\n" + "Description: " + topicModelList.get(0).description +
                "\n" + "Address: " + getAddress(Double.valueOf(topicModelList.get(0).latitude), Double.valueOf(topicModelList.get(0).longitude)));

        builder.setView(view);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {


            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Tip")
                        .setMessage("Continue deletion?")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mMap.clear();
                                TopicUtils.delete(topicModelList.get(0).id);
                                dialog.dismiss();
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);

                                intent.putExtra("k1", Double.valueOf(topicModelList.get(0).latitude));
                                intent.putExtra("k2", Double.valueOf(topicModelList.get(0).longitude));

                                startActivity(intent);
                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();



            }
        });
        dialog = builder.show();
        DisplayMetrics dm = new DisplayMetrics();
        MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        dialog.getWindow().setLayout((int) (dm.widthPixels * 0.8), (int) (dm.heightPixels * 0.5));


    }

    private String getAddress(double latitude, double longitude) {

        String address1 = "";
        //Geocoder通过经纬度获取具体信息
        Geocoder gc = new Geocoder(MainActivity.this, Locale.getDefault());
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

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Log.e("location", "onMyLocationClick: ");

        //  map.addMarker(new MarkerOptions().position(currentPosition).title("i am here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyContext.mylocation, 20));

    }


}
