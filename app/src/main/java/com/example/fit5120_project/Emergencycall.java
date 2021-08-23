package com.example.fit5120_project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import database.ContactAdapter;
import database.ContactUtils;
import database.DBHelper;
import database.MyContext;
import database.contact;


public class Emergencycall extends AppCompatActivity {
    ListView listView;
    ContactAdapter ContactAdapter;
    List<contact> contacts;
    static int PERMISSION_CODE = 100;
    private AlertDialog contactDialog;
    private EditText et_name, et_phoneNumber;
    private Button btn_submit, btn_back;
    private ListView lv_contacts;
    private CheckBox checkBox;
    private ToggleButton toggleButton;
    private TextView textView;
    private RelativeLayout relativeLayout;
    private Button btn_showDialog;
    public String phoneNumber="000";
    public String phoneNumber1="00055";

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
        setContentView(R.layout.activity_emergencycall);
        MyContext.dbHelper = new DBHelper(this);

        ImageView imageView = findViewById(R.id.back_6);
        ImageView imageView1 = findViewById(R.id.add);
        toggleButton=findViewById(R.id.soschange);
        textView=findViewById(R.id.sosname);
        relativeLayout=findViewById(R.id.sos);
        toggleButton.setBackgroundColor(Color.RED);
        toggleButton.setTextColor(Color.WHITE);
        if (ContextCompat.checkSelfPermission(Emergencycall.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Emergencycall.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);


        }

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    relativeLayout.setBackgroundResource(R.mipmap.black);
                    textView.setText("SOS Silent");
                    Toast.makeText(getApplicationContext(),"You have changed to the silent mode!",Toast.LENGTH_SHORT).show();
                    toggleButton.setBackgroundColor(Color.BLACK);
                    toggleButton.setTextColor(Color.WHITE);




                } else {
                    // The toggle is disabled
                    relativeLayout.setBackgroundResource(R.mipmap.redrectangle);
                    textView.setText("SOS");
                    Toast.makeText(getApplicationContext(),"You have changed to the normal mode!",Toast.LENGTH_SHORT).show();

                    toggleButton.setBackgroundColor(Color.RED);



                }
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_CALL);



                Uri data = Uri.parse(toggleButton.isChecked()?"tel:" +phoneNumber1:"tel:" +phoneNumber);
                intent.setData(data);
                startActivity(intent);

            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent class will help to go to next activity using
                // it's object named intent.
                // SecondActivty is the name of new created EmptyActivity.
                Intent intent = new Intent(Emergencycall.this, MainActivity.class);

                startActivity(intent);

                finish();
            }
        });
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent class will help to go to next activity using
                // it's object named intent.
                // SecondActivty is the name of new created EmptyActivity.
                showContactDialog();



            }
        });
        listView = findViewById(R.id.listview1);
        contacts = new ArrayList<>();
        if (ContactUtils.get(contacts).size()<=0){
            contacts= ContactUtils.getList("contactNo.csv", this);
            for (contact contact : contacts
            ) {
                ContactUtils.insert(contact);
            }
        }else{
            contacts=ContactUtils.get(contacts);
        }







        Log.e("Main", "onCreate: " + contacts.size());

        ContactAdapter = new ContactAdapter(contacts, this, false);

        listView.setAdapter(ContactAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + contacts.get(i).getPhoneNumber());
                intent.setData(data);
                startActivity(intent);

            }
        });


    }

    private void showContactDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Emergencycall.this);
        builder.setCancelable(true);
        View view = LayoutInflater.from(Emergencycall.this).inflate(R.layout.dialog_addcontacts, null);
        et_name = view.findViewById(R.id.et_name);
        et_phoneNumber = view.findViewById(R.id.et_phone);
        btn_submit = view.findViewById(R.id.btn_submit);
        lv_contacts = view.findViewById(R.id.lv_contacts);
        checkBox=view.findViewById(R.id.checkbox);
        ContactAdapter = new ContactAdapter(contacts, view.getContext(), true);
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
                checkBox.setChecked(false);
               Intent intent=new Intent(Emergencycall.this,Emergencycall.class);
               startActivity(intent);

               finish();



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
                    contact.setBool(checkBox.isChecked()?1:0);
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
        Emergencycall.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        contactDialog.getWindow().setLayout((int) (dm.widthPixels * 0.9), (int) (dm.heightPixels * 0.9));

    }


    private void showMsg(String msg) {
        Toast.makeText(Emergencycall.this, msg, Toast.LENGTH_SHORT).show();
    }

}