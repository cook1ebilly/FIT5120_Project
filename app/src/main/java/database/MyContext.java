package database;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MyContext {

    public static DBHelper dbHelper;

    public static List<TopicModel> topicModelList=new ArrayList<>();

    public static LatLng mylocation =new LatLng(-37.9145, 145.1350);

    public static double k1=0;
    public static double k2=0;



}
