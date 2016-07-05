package com.example.evandro.delliveryclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class DeliveryDetails extends AppCompatActivity {

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);
        final Delivery delivery = new Delivery();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                delivery.setId(params.getInt("deliveryId"));
                delivery.setAddress(params.getString("address"));
                delivery.setImage(params.getByteArray("image"));
                delivery.setDescription(params.getString("description"));
                delivery.setStatus(params.getString("status"));
            }
        }

        TextView idTextview = (TextView) findViewById(R.id.idTextView4);
        TextView addressTextView = (TextView) findViewById(R.id.addressTextView);
        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        imageView.setImageBitmap(BitmapFactory.decodeByteArray(delivery.getImage(), 0, delivery.getImage().length));
        descriptionTextView.setText(delivery.getDescription());
        addressTextView.setText(delivery.getAddress());
        idTextview.setText("Delivery #" + delivery.getId());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Position = " + position);
                DeliveryOperations operations = new DeliveryOperations(DeliveryDetails.this);
                operations.open();
                if (position == 0) {
                    operations.updateDelivery(delivery.getId(), "Ready to Deliver");
                } else if (position == 1) {
                    operations.updateDelivery(delivery.getId(), "Attempt Failed");
                } else if (position == 2) {
                    operations.updateDelivery(delivery.getId(), "Delivered");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (delivery.getStatus().equals("Ready to Deliver")) {
            spinner.setSelection(0);
        } else if (delivery.getStatus().equals("Attempt Failed")) {
            spinner.setSelection(1);
        } else if (delivery.getStatus().equals("Delivered")) {
            spinner.setSelection(2);
        }
    }


    @Override
    public void onBackPressed() {
        Bundle paramsReturn = new Bundle();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                System.out.println("STTS: " + spinner.getSelectedItem().toString());
                System.out.println(params.getInt("position"));
                paramsReturn.putString("status", spinner.getSelectedItem().toString());
                paramsReturn.putInt("position", params.getInt("position"));
            }
        }
        Intent intentReturn = new Intent();
        intentReturn.putExtras(paramsReturn);
        setResult(Activity.RESULT_OK, intentReturn);
        finish();
        super.onBackPressed();
    }
}
