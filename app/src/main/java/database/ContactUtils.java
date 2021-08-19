package database;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;



public class ContactUtils {


    private static String tableName = "contacts";

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















}
