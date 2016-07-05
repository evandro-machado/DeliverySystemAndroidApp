package com.example.evandro.delliveryclient;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Evandro on 6/27/2016.
 */
public class ListCell extends ArrayAdapter<Delivery> {
    private final Activity context;
    private final List<Delivery> delivery;

    public ListCell(Activity context, List<Delivery> delivery){
        super(context, R.layout.list_cell, delivery);
        this.context = context;
        this.delivery = delivery;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Delivery deliveryItem = getItem(position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_cell, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        txtTitle.setText(deliveryItem.getAddress());
        return rowView;
    }
}
