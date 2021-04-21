package com.example.menu_card.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context,"Info.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table orders(order_id INTEGER PRIMARY KEY, tax_percent DECIMAL NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists task");
    }

    public boolean insertOrderInfo(String order_id, double tax_percent){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("order_id", order_id);
        contentValues.put("tax_percent", tax_percent);
        long result = DB.insert("orders",null,contentValues);
        return result != -1;
    }
    public boolean updateOrderInfo(String order_id, double tax_percent){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("order_id", order_id);
        contentValues.put("tax_percent", tax_percent);
        long result = DB.update("orders",contentValues,"ID=?",new String[]{String.valueOf(order_id)});
        return result != -1;
    }
    public boolean deleteOrderInfo(String order_id){
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = DB.delete("orders", "order_id=?", new String[]{String.valueOf(order_id)});
        return result != -1;
    }
    public Cursor getOrderId() {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("SELECT order_id, tax_percent FROM orders", null);
    }
}
