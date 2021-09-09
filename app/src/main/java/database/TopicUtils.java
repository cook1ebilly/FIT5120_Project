package database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TopicUtils {

    private static String tableName = "topic";


    public static List<TopicModel> getList(String fileName, Context context) {
        List<TopicModel> csvList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(fileName), "UTF-8"));//换成你的文件名
            reader.readLine();
            String line = "";

            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");

                TopicModel mode = new TopicModel();
                mode.year = item[0];
                mode.longitude = item[1];
                mode.latitude = item[2];
                mode.topic = item[3];
                mode.description = item[4];

                csvList.add(mode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TAG", "getList: " + csvList.size());
        return csvList;
    }

    public static int insert(TopicModel model) {
        try {
            int rec = 0;
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", getTime());
            contentValues.put("year", model.year);
            contentValues.put("longitude", model.longitude);
            contentValues.put("latitude", model.latitude);
            contentValues.put("topic", model.topic);
            contentValues.put("description", model.description);
            rec += MyContext.dbHelper.db.insert(tableName, null, contentValues);
            return rec;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int delete(String id) {
        int rec = 0;
        try {
            rec = MyContext.dbHelper.db.delete(tableName, "id=?", new String[]{id});
        } catch (Exception e) {

        }
        Log.e("TAG", "delete: "+rec);

        return rec;
    }

    @SuppressLint("Range")
    public static List<TopicModel> get(List<TopicModel> list) {
        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }
        Cursor cursor = MyContext.dbHelper.rawQuery("select * from " + tableName, null);
        while (cursor.moveToNext()) {
           TopicModel model = new TopicModel();
            model.id = cursor.getString(cursor.getColumnIndex("id"));
            model.year = cursor.getString(cursor.getColumnIndex("year"));
            model.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
            model.latitude = cursor.getString(cursor.getColumnIndex("latitude"));
            model.topic = cursor.getString(cursor.getColumnIndex("topic"));
            model.description = cursor.getString(cursor.getColumnIndex("description"));

            list.add(model);
        }
        cursor.close();
        return list;

    }

    public static void deleteAll() {
        MyContext.dbHelper.db.execSQL("delete from " + tableName);
    }

    public static String getTime() {
        Date date = new Date();
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String nowTime = sd.format(date);
        return nowTime;
    }

    public static String getYear() {
        return getTime().substring(0, 4);
    }

}
