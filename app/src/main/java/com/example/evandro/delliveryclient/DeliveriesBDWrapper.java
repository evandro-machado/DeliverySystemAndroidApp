package com.example.evandro.delliveryclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Evandro on 6/27/2016.
 */
public class DeliveriesBDWrapper extends SQLiteOpenHelper {
    public static final String DELIVERIES = "Deliveries";
    public static final String DELIVERY_ID = "_id";
    public static final String DELIVERY_ADDRESS = "address";
    public static final String DELIVERY_DESCRIPTION = "description";
    public static final String DELIVERY_STATUS = "status";
    public static final String DELIVERY_IMAGE = "image";
    public static final String DELIVERY_EMPLOYEE_ID = "deliverymanId";


    private static final String DATABASE_NAME = "Deliveries.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " +
            DELIVERIES + "(" + DELIVERY_ID + " integer primary key, " +
            DELIVERY_ADDRESS + " text not null, " + DELIVERY_DESCRIPTION + " text not null, " +
            DELIVERY_STATUS + " text not null, " + DELIVERY_IMAGE + " blob, " +
            DELIVERY_EMPLOYEE_ID + " integer);";

    public DeliveriesBDWrapper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DELIVERIES);
        onCreate(db);
    }
}
