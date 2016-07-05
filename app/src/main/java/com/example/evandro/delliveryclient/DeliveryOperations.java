package com.example.evandro.delliveryclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evandro on 6/27/2016.
 */
public class DeliveryOperations {
    private DeliveriesBDWrapper dbHelper;
    private String [] DELIVERY_TABLE_COLUMNS = {DeliveriesBDWrapper.DELIVERY_ID, DeliveriesBDWrapper.DELIVERY_ADDRESS, DeliveriesBDWrapper.DELIVERY_DESCRIPTION, DeliveriesBDWrapper.DELIVERY_STATUS, DeliveriesBDWrapper.DELIVERY_IMAGE};
    private SQLiteDatabase database;

    public DeliveryOperations(Context context){
        dbHelper = new DeliveriesBDWrapper(context);
    }

    public void open(){
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public Delivery addDelivery(Delivery delivery){
        ContentValues values = new ContentValues();
        values.put(DeliveriesBDWrapper.DELIVERY_ID, delivery.getId());
        values.put(DeliveriesBDWrapper.DELIVERY_ADDRESS, delivery.getAddress());
        values.put(DeliveriesBDWrapper.DELIVERY_DESCRIPTION, delivery.getDescription());
        values.put(DeliveriesBDWrapper.DELIVERY_EMPLOYEE_ID, delivery.getDeliverymanId());
        values.put(DeliveriesBDWrapper.DELIVERY_IMAGE, delivery.getImage());
        values.put(DeliveriesBDWrapper.DELIVERY_STATUS, delivery.getStatus());

        long deliverId = database.insert(DeliveriesBDWrapper.DELIVERIES, null, values);

        return delivery;
    }

    public boolean updateDelivery(long id, String status){
        ContentValues values = new ContentValues();
        values.put(DeliveriesBDWrapper.DELIVERY_STATUS, status);
        return database.update(DeliveriesBDWrapper.DELIVERIES, values, DeliveriesBDWrapper.DELIVERY_ID + " = " +id, null ) > 0;
    }

    public void deleteDelivery(Delivery delivery){
        int id = delivery.getId();
        database.delete(DeliveriesBDWrapper.DELIVERIES, DeliveriesBDWrapper.DELIVERY_ID + " = " + id, null);
    }

    public List getDeliveries(int deliverymanId){
        List<Delivery> deliveriesList = new ArrayList<>();
        Cursor cursor = database.query(DeliveriesBDWrapper.DELIVERIES, DELIVERY_TABLE_COLUMNS, DeliveriesBDWrapper.DELIVERY_EMPLOYEE_ID + " = " + deliverymanId, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Delivery delivery = new Delivery();
            delivery.setId(cursor.getInt(0));
            delivery.setAddress(cursor.getString(1));
            delivery.setDescription(cursor.getString(2));
            delivery.setStatus(cursor.getString(3));
            delivery.setImage(cursor.getBlob(4));
            delivery.setDeliverymanId(deliverymanId);

            deliveriesList.add(delivery);
            cursor.moveToNext();
        }
        cursor.close();
        return deliveriesList;
    }




}
