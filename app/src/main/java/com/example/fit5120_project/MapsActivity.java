package com.example.fit5120_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fit5120_project.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import database.CallAdapter;
import database.ContactAdapter;
import database.ContactUtils;
import database.DBHelper;
import database.MyContext;
import database.PermissionUtils;
import database.SendAdapter;
import database.contact;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
            ActivityCompat.OnRequestPermissionsResultCallback {
    ListView listView;
    String myAddress;
    private GoogleMap map;
    private ActivityMapsBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private Button btn_getLocation;
    ContactAdapter ContactAdapter;
    CallAdapter CallAdapter;
    SendAdapter SendAdapter;
    List<contact> sendlist;
    List<contact> contacts;
    List<contact> calllist;
    private GoogleMap mMap;
    private ImageView imageView,phonecall,send;
    private ImageView imageView2,add;
    private AlertDialog contactDialog,callDialog,sendDialog;
    private EditText et_name, et_phoneNumber;
    private Button btn_submit, btn_back,btn_back1,btn_back2;
    private ListView lv_contacts;
    static int PERMISSION_CODE = 100;
    public String phoneNumber="000";
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x01:
                    if ((int) msg.obj > 0) {
                        showMsg("Data added successfully");
                        contacts = ContactUtils.get(contacts);
                        ContactAdapter.notifyDataSetChanged();
                        et_name.setText("");
                        et_phoneNumber.setText("");
                    } else {
                        showMsg("Data addition failed phone number already exists");
                    }
                    break;
                case 0x02:
                    if ((int) msg.obj > 0) {
                        showMsg("Data deleted successfully");
                        contacts = ContactUtils.get(contacts);
                        ContactAdapter.notifyDataSetChanged();
                    } else {
                        showMsg(" Data deletion failed");

                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        imageView=findViewById(R.id.back_6);
        add=findViewById(R.id.addit);
        send=findViewById(R.id.send);
        MyContext.dbHelper=new DBHelper(MapsActivity.this);
        contacts=new ArrayList<>();
        contacts=ContactUtils.get(contacts);
        calllist=new ArrayList<>();
        calllist=ContactUtils.get(calllist);
        sendlist=new ArrayList<>();
        sendlist=ContactUtils.get(sendlist);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent class will help to go to next activity using
                // it's object named intent.
                // SecondActivty is the name of new created EmptyActivity.
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);

                startActivity(intent);

                finish();
            }
        });

        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);


        }


        imageView2=findViewById(R.id.sos);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" +phoneNumber);
                intent.setData(data);
                startActivity(intent);

            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showContactDialog();
            }
        });


         phonecall=findViewById(R.id.call);
        phonecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCallDialog();
            }
        });

        send=findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showSendDialog();
            }
        });




    }

    private void showSendDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder( MapsActivity.this);
        builder.setCancelable(true);
        View view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.send_listview, null);
     sendlist=ContactUtils.get(sendlist);
        ListView listitem1 = view.findViewById(R.id.sendlist);
        SendAdapter = new SendAdapter(sendlist, view.getContext());

        listitem1.setAdapter(SendAdapter);
        listitem1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Location location = map.getMyLocation();
                LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                map.setBuildingsEnabled(false);

                map.getUiSettings().setZoomGesturesEnabled(true);

                map.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                Log.e("TAG", "onClick: "+getLocationInfo(location) );
              myAddress=  getAddress(location.getLatitude(),location.getLongitude());
              checkAndSend(sendlist.get(i));

              sendDialog.dismiss();




            }
        });


        btn_back2 = view.findViewById(R.id.btn_back2);
        builder.setView(view);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {


            }
        });


        btn_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDialog.dismiss();
            }
        });
        sendDialog = builder.show();
        DisplayMetrics dm = new DisplayMetrics();
        MapsActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        sendDialog.getWindow().setLayout((int) (dm.widthPixels * 0.9), (int) (dm.heightPixels * 0.9));










    }


     private void showCallDialog(){
         AlertDialog.Builder builder = new AlertDialog.Builder( MapsActivity.this);
         builder.setCancelable(true);
         View view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.call_listview, null);
         calllist=ContactUtils.get(calllist);

         TextView name =view.findViewById(R.id.cname);
         ListView listitem = view.findViewById(R.id.calllist1);
         Log.e("GGGG", "showCallDialog: "+calllist.size() );
         CallAdapter = new CallAdapter(calllist, view.getContext());

         listitem.setAdapter(CallAdapter);
         listitem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 Intent intent = new Intent(Intent.ACTION_CALL);
                 if (!TextUtils.isEmpty(calllist.get(i).getPhoneNumber())){
                 Uri data = Uri.parse("tel:" +calllist.get(i).getPhoneNumber());
                 intent.setData(data);
                 startActivity(intent);

             }

             else {

                 Toast.makeText(MapsActivity.this,"Wrong contact method.",Toast.LENGTH_SHORT).show();


                 }


             }
         });


         btn_back1 = view.findViewById(R.id.btn_back1);
         builder.setView(view);
         builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
             @Override
             public void onDismiss(DialogInterface dialog) {


             }
         });


         btn_back1.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 callDialog.dismiss();
             }
         });
         callDialog = builder.show();
         DisplayMetrics dm = new DisplayMetrics();
         MapsActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
         callDialog.getWindow().setLayout((int) (dm.widthPixels * 0.9), (int) (dm.heightPixels * 0.9));





     }

    private void showContactDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder( MapsActivity.this);
        builder.setCancelable(true);
        View view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.dialog_addcontacts, null);
        et_name = view.findViewById(R.id.et_name);
        et_phoneNumber = view.findViewById(R.id.et_phone);
        btn_submit = view.findViewById(R.id.btn_submit);
        lv_contacts = view.findViewById(R.id.lv_contacts);
        ContactAdapter = new ContactAdapter(contacts, view.getContext());

        lv_contacts.setAdapter(ContactAdapter);
        lv_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long viewId = view.getId();
                if (viewId == R.id.btn_item_delete) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Tip")
                            .setMessage("Continue deletion?")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int result = ContactUtils.delete(contacts.get(position).getPhoneNumber());
                                    Message message = new Message();
                                    message.what = 0x02;
                                    message.obj = result;
                                    uiHandler.sendMessage(message);

                                }
                            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
        });
        btn_back = view.findViewById(R.id.btn_back);
        builder.setView(view);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                et_name.setText("");
                et_phoneNumber.setText("");




            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(et_name.getText().toString().trim()) &&
                        !TextUtils.isEmpty(et_phoneNumber.getText().toString().trim())) {
                    contact contact = new contact();
                    contact.setName(et_name.getText().toString().trim());
                    contact.setPhoneNumber(et_phoneNumber.getText().toString().trim());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int result = ContactUtils.insert(contact);
                            Message message = new Message();
                            message.what = 0x01;
                            message.obj = result;
                            uiHandler.sendMessage(message);

                        }
                    }).start();



                }


            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactDialog.dismiss();
            }
        });
        contactDialog = builder.show();
        DisplayMetrics dm = new DisplayMetrics();
        MapsActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        contactDialog.getWindow().setLayout((int) (dm.widthPixels * 0.9), (int) (dm.heightPixels * 0.9));

    }

    private void showMsg(String msg) {
        Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void checkAndSend(contact contact) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            sendSMSS("Hi!"+" "+contact.getName()+"I need your help now! My location is"+ " "+myAddress, contact.getPhoneNumber());

        } else {
            PermissionUtils.requestPermission(this,
                    Manifest.permission.SEND_SMS, 1);

        }
    }

    private void sendSMSS(String content, String phone) {

        if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(phone)) {
            SmsManager manager = SmsManager.getDefault();
            ArrayList<String> strings = manager.divideMessage(content);
            for (int i = 0; i < strings.size(); i++) {
                manager.sendTextMessage(phone, null, content, null, null);
            }
            Toast.makeText(MapsActivity.this, "Send Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Number can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Log.e("location", "onMyLocationClick: " + getLocationInfo(location));
        getAddress(location.getLatitude(), location.getLongitude());
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        enableMyLocation();
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            PermissionUtils.requestPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }
    private String getLocationInfo(Location location) {
        return "\n经度：" + location.getLongitude() +
                "\n纬度：" + location.getLatitude() +
                "\n高度：" + location.getAltitude() +
                "\n方向：" + location.getBearing() +
                "\n方式：" + location.getProvider() +
                "\n时间：" + location.getTime();
    }

    private String getAddress(double latitude, double longitude) {

        String address1="";
        //Geocoder通过经纬度获取具体信息
        Geocoder gc = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> locationList = gc.getFromLocation(latitude, longitude, 1);
            if (locationList != null) {
                Address address = locationList.get(0);
                String countryName = address.getCountryName();//国家
                String countryCode = address.getCountryCode();

                String adminArea = address.getAdminArea();//省
                String adminAre=address.getSubAdminArea();
                String postcode=address.getPostalCode();
                String locality = address.getLocality();//市
                String subLocality = address.getSubLocality();//区
                String featureName = address.getThoroughfare();//街道
                String fnum = address.getFeatureName();

                String state=adminArea.substring(0,3);

                Log.e("TAG", "getAddress: " + countryName + "——" + countryCode + "——" + adminArea + "——" +
                        locality + "——" + subLocality + "——" + featureName + "——" + fnum+"--"+adminAre);
                address1=fnum+" "+ featureName+", " + locality+", " +state+" "+postcode ;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return address1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.checkPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        } else {
            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            permissionDenied = false;
        }
    }
}