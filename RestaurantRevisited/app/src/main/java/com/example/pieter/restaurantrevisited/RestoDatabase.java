package com.example.pieter.restaurantrevisited;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pieter on 28-11-17.
 */

public class RestoDatabase extends SQLiteOpenHelper {
    private static RestoDatabase instance;

    public static RestoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new RestoDatabase(context.getApplicationContext(), "restodb", null, 1);
        }
        return instance;
    }

    private RestoDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private void createTable(SQLiteDatabase db) {
        db.execSQL("create table resto (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price INTEGER, number INTEGER)");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "resto");
        onCreate(sqLiteDatabase);
    }

    public Cursor selectAll() {
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("SELECT _id,* FROM resto", new String[] {});
    }

    public void addItem(String name, int price, int number) {
        SQLiteDatabase dbread = getReadableDatabase();
        Cursor response = dbread.query("resto", null, "name=? AND price=?", new String[] {name, Integer.toString(price)}, null, null, null, "1");
        SQLiteDatabase dbwrite = getWritableDatabase();
        ContentValues values = new ContentValues();
        if (response.getCount() <= 0) {
            values.put("name", name);
            values.put("price", price);
            values.put("number", number);
            dbwrite.insert("resto", null, values);
        } else {
            response.moveToFirst();
            int newnumber = response.getInt(response.getColumnIndex("number")) + number;
            values.put("number", newnumber);
            dbwrite.update("resto", values, "name=? AND price=?", new String[] {name, Integer.toString(price)});
        }
    }

    public void clear() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("resto", null, null);
    }
}
