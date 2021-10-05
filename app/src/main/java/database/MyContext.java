package database;


import com.example.fit5120_project.AgeGroup2;
import com.example.fit5120_project.Agegroup;
import com.example.fit5120_project.LocationBean;
import com.example.fit5120_project.Offence;
import com.example.fit5120_project.Weapon;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MyContext {

    public static DBHelper dbHelper;

    public static List<TopicModel> topicModelList = new ArrayList<>();
    public static List<Offence> offences;
    public static List<Agegroup> agegroups;
    public static List<LocationBean> locationBeans;
    public static List<Weapon> weapons;
    public static List<AgeGroup2> ageGroup2s;
    public static LatLng mylocation = new LatLng(-37.9145, 145.1350);

    public static int distance;
    public static LatLng movelocation;


}
