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
        db.execSQL("create table orders(order_id TEXT, item_id TEXT , name TEXT NOT NULL, quantity TEXT NOT NULL, price TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertOrderInfo(String order_id, String item_id, String name, String quantity, String price){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("order_id", order_id.trim());
        contentValues.put("item_id", item_id);
        contentValues.put("name", name);
        contentValues.put("quantity", quantity);
        contentValues.put("price", price);
        long result = db.insert("orders",null,contentValues);
        return result != -1;
    }
    public boolean updateOrderInfo(String order_id, String item_id, String name, String quantity, String price){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("quantity", quantity);
        contentValues.put("price", price);
        long result = db.update("orders",contentValues,"item_id='"+item_id+"' AND order_id='"+order_id.trim()+"'",null);
        return result != -1;
    }
    public void deleteOrderInfo(String order_id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from orders where order_id='"+order_id.trim()+"'");
    }
    public Cursor getAllOrderItems(String order_id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("SELECT item_id, name,quantity,price FROM orders where order_id='"+order_id.trim()+"'", null);
    }

    public Cursor getOrderItem(String order_id, String item_id) {
        SQLiteDatabase db = getWritableDatabase();
        //System.out.println("SELECT name, quantity, price FROM orders where order_id='"+order_id.trim()+"' AND item_id='"+item_id+"'");
        return db.rawQuery("SELECT name, quantity, price FROM orders where order_id='"+order_id.trim()+"' AND item_id='"+item_id+"'", null);
    }

}
