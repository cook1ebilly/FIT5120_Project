package com.example.fit5120_project;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


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

import database.MyContext;
import database.TopicModel;
import database.TopicUtils;

public class Reportissues extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mMapView;
    private GoogleMap googleMap;
    Address reportLocation;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    ListView listView;
    EditText search;
    ImageView cancel;
    String place;
    ArrayList<GooglePlaceModel> googlePlaceModels = new ArrayList<>();
    List<Address> addressList;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private Spinner sp;
    String sp_info = "";
    private EditText desc;
    private Button submit,see;
    private String[] topicArray = {"Please select problem type","Poor lighting","Unwanted attention","Drug activity","Loiterers","Solicitation",
            "Pickpockets","Vandalism","Gang activity ","Street violence","Deserted areas","Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportissues);
        ImageView imageView = findViewById(R.id.back_3);
        cancel=findViewById(R.id.cancel);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent class will help to go to next activity using
                // it's object named intent.
                // SecondActivty is the name of new created EmptyActivity.
                Intent intent = new Intent(Reportissues.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.mMapView);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = desc.getText().toString().trim();
                String s2 = sp_info;

                if (!TextUtils.isEmpty(s1) && !TextUtils.isEmpty(s2) && reportLocation != null) {
                    TopicModel model = new TopicModel();
                    model.id = TopicUtils.getTime();
                    model.year = TopicUtils.getYear();
                    model.topic = s2;
                    model.description = s1;
                    model.longitude = String.valueOf(reportLocation.getLongitude());
                    model.latitude = String.valueOf(reportLocation.getLatitude());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            TopicUtils.insert(model);
                        }
                    }).start();
                    Toast.makeText(Reportissues.this, "insert success" , Toast.LENGTH_SHORT).show();


                    reportLocation = null;
                    see.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.GONE);


                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setText("");
                googlePlaceModels.clear();
                setAdapter();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);


            }
        });

        see = findViewById(R.id.see);
        see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Reportissues.this,MainActivity.class);
                intent.putExtra("report_place",place);
                startActivity(intent);
                finish();

            }
        });
        search = (EditText) findViewById(R.id.search);
        initSpinner();
        desc = findViewById(R.id.desc);
        listView = (ListView) findViewById(R.id.listView);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    googlePlaceModels.clear();
                    setAdapter();

                } else {
                    new Reportissues.GooglePlaces().execute(String.valueOf(s));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!googlePlaceModels.get(position).getPlaceName().equalsIgnoreCase("Not Found")) {

                    place = googlePlaceModels.get(position).getPlaceName();
                    search.setText("");

                    moveToplace();

                    googlePlaceModels.clear();
                    setAdapter();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });


    }

    public void setAdapter() {
        PlaceAdapter adapter = new PlaceAdapter(Reportissues.this, googlePlaceModels);
        listView.setAdapter(adapter);
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
                setAdapter();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void moveToplace() {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            addressList = geocoder.getFromLocationName(place, 5);
            Address location = addressList.get(0);
            reportLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng).title(place));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    private void initSpinner() {
        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> starAdapter = new ArrayAdapter<String>(this, R.layout.item_select, topicArray);
        //设置数组适配器的布局样式
        starAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //从布局文件中获取名叫sp_dialog的下拉框
        sp = findViewById(R.id.sp);
        //设置下拉框的数组适配器
        sp.setAdapter(starAdapter);
        //设置下拉框默认的显示第一项
        sp.setSelection(0);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp.setOnItemSelectedListener(new MySelectedListener());
    }

    class MySelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            sp_info = topicArray[i];
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            addressList = geocoder.getFromLocationName(place, 5);
            Address location = addressList.get(0);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            googleMap.addMarker(new MarkerOptions().position(latLng).title(place));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap.addMarker(new MarkerOptions().position(MyContext.mylocation).title("You are here now!"));
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyContext.mylocation, 15));


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