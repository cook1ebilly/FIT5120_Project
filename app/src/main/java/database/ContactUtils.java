package database;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;



public class ContactUtils {

    private static String tableName = "contacts";

    public static List<contact> getList(String fileName, Context context) {
        List<contact> csvList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(fileName), "UTF-8"));//换成你的文件名
            reader.readLine();
            String line = "";

            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");

                contact mode = new contact();
                mode.setPhoneNumber(item[0]);
                mode.setName(item[1]);
                mode.setBool(Integer.parseInt(item[2]));
                csvList.add(mode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TAG", "getList: " + csvList.size());
        return csvList;
    }

    public static int insert(contact contact) {
        try {
            int rec = 0;
            ContentValues contentValues = new ContentValues();
            contentValues.put("phoneNumber", contact.getPhoneNumber());
            contentValues.put("name", contact.getName());
            contentValues.put("bool",contact.getBool());
            rec += MyContext.dbHelper.db.insert(tableName, null, contentValues);
            return rec;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int delete(String phoneNumber) {
        int rec = 0;
        try {
            rec = MyContext.dbHelper.db.delete(tableName, "phoneNumber=?", new String[]{phoneNumber});
        } catch (Exception e) {
        }
        return rec;
    }

    public static boolean update(contact contact) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("phoneNumber", contact.getPhoneNumber());
            contentValues.put("name", contact.getName());
            contentValues.put("bool",contact.getBool());
            return MyContext.dbHelper.db.update(tableName, contentValues, null, null) > 0;
        } catch (Exception e) {
        }
        return false;
    }

    @SuppressLint("Range")
    public static List<contact> get(List<contact> list) {
        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }
        Cursor cursor = MyContext.dbHelper.rawQuery("select * from " + tableName, null);
        while (cursor.moveToNext()) {
            contact bean = new contact();
            bean.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
            bean.setName(cursor.getString(cursor.getColumnIndex("name")));
            bean.setBool(cursor.getInt(cursor.getColumnIndex("bool")));

            list.add(bean);
        }
        cursor.close();
        return list;

    }
    public  static void deleteAll() {
       MyContext.dbHelper. db.execSQL("delete from "+tableName);
    }


}
