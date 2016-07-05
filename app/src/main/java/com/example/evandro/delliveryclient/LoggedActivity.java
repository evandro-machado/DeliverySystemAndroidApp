package com.example.evandro.delliveryclient;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LoggedActivity extends AppCompatActivity {

    ListView list;
    List<Delivery> deliveriesList = new ArrayList<>();
    Deliveryman deliveryman = new Deliveryman();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        System.out.println("Im on main!!!!!!!");

        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                deliveryman.setId(params.getInt("deliverymanId"));
                deliveryman.setLogin(params.getString("deliverymanLogin"));
            }
        }

        TextView welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        welcomeTextView.setText("Welcome " + deliveryman.getLogin());

        String[] options = new String[]{"Load deliveries", "Unload Deliveries"};
        loadDeliveriesToDB(deliveryman.getId());

        ListCell adapter = new ListCell(LoggedActivity.this, deliveriesList);
        list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Delivery delivery = deliveriesList.get(position);
                Intent intent = new Intent(LoggedActivity.this, DeliveryDetails.class);
                Bundle params = new Bundle();
                params.putInt("deliveryId", delivery.getId());
                params.putString("address", delivery.getAddress());
                params.putInt("deliverymanId", delivery.getDeliverymanId());
                params.putByteArray("image", delivery.getImage());
                params.putString("description", delivery.getDescription());
                params.putString("status", delivery.getStatus());
                params.putInt("position", position);
                intent.putExtras(params);
                startActivityForResult(intent, 1);
                System.out.println("Item id: " + delivery.getId() + "\n Address: " + delivery.getAddress());
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                deliveriesList.get(params.getInt("position")).setStatus(params.getString("status"));
                System.out.println();
            }
        }
    }

    public Handler handleLoadedDeliveriesToDB = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int entries = msg.getData().getInt("entries");
            ListCell adapter = new ListCell(LoggedActivity.this, deliveriesList);
            list = (ListView) findViewById(R.id.listView);
            list.setAdapter(adapter);
            if (entries > 0) {
                Toast.makeText(LoggedActivity.this, "Loaded " + entries + " new entries.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoggedActivity.this, "No new entries.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public Handler handleUnloadedDeliveriesToDB = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int entries = msg.getData().getInt("entries");
            ListCell adapter = new ListCell(LoggedActivity.this, deliveriesList);
            list = (ListView) findViewById(R.id.listView);
            list.setAdapter(adapter);
            if (entries > 0) {
                Toast.makeText(LoggedActivity.this, "Unloaded " + entries + " entries that have been delivered.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoggedActivity.this, "Nothing to unload.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void loadDeliveriesToDB(final int deliverymanId) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                String url = "http://169.254.254.199:8080/deliveryProject/delivery";
                WebService ws = new WebService(url);
                Map<String, String> params = new TreeMap<String, String>();

                params.put("action", "listById");
                params.put("id", deliverymanId + "");
                String response = ws.webGet("", params);
                Bundle bundle = new Bundle();
                try {
                    int entries = 0;
                    JSONArray deliveryJSONArray = new JSONArray(response);
                    DeliveryOperations deliveryOperations = new DeliveryOperations(LoggedActivity.this);
                    deliveryOperations.open();
                    for (int i = 0; i < deliveryJSONArray.length(); i++) {
                        Delivery delivery = new Delivery();
                        delivery.setAddress(deliveryJSONArray.getJSONObject(i).getString("address"));
                        delivery.setDescription(deliveryJSONArray.getJSONObject(i).getString("description"));
                        delivery.setDeliverymanId(deliverymanId);
                        delivery.setId(Integer.parseInt(deliveryJSONArray.getJSONObject(i).getString("id")));
                        delivery.setStatus(deliveryJSONArray.getJSONObject(i).getString("status"));

                        String imageStr = deliveryJSONArray.getJSONObject(i).getString("image");
                        delivery.setImage(Base64.decode(imageStr, Base64.DEFAULT));

                        deliveryOperations.addDelivery(delivery);
                        entries++;
                    }
                    deliveriesList = deliveryOperations.getDeliveries(deliverymanId);
                    bundle.putInt("entries", entries);
                    Message message = new Message();
                    message.setData(bundle);
                    deliveryOperations.close();
                    handleLoadedDeliveriesToDB.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void unloadDeliveriesToDB() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                String url = "http://169.254.254.199:8080/deliveryProject/delivery";
                WebService ws = new WebService(url);
                Map<String, String> params = new TreeMap<String, String>();
                List<Integer> deliveredIdsArray = new ArrayList<Integer>();
                List<Integer> failedIdsArray = new ArrayList<Integer>();
                List<Integer> readyIdsArray = new ArrayList<Integer>();
                List<Integer> deliveredPositionArray = new ArrayList<Integer>();
                for (int i = 0; i < deliveriesList.size(); i++) {
                    if (deliveriesList.get(i).getStatus().equals("Delivered")) {
                        deliveredIdsArray.add(deliveriesList.get(i).getId());
                        deliveredPositionArray.add(i);
                    } else if (deliveriesList.get(i).getStatus().equals("Attempt Failed")) {
                        failedIdsArray.add(deliveriesList.get(i).getId());
                    } else if (deliveriesList.get(i).getStatus().equals("Ready to Deliver")) {
                        readyIdsArray.add(deliveriesList.get(i).getId());
                    }
                }

                params.put("action", "unload");
                params.put("deliveredIds", deliveredIdsArray.toString());
                params.put("failedIds", failedIdsArray.toString());
                params.put("readyIds", readyIdsArray.toString());

                String response = ws.webGet("", params);
                Bundle bundle = new Bundle();
                try {
                    JSONObject successResponseJSON = new JSONObject(response);
                    boolean successResponse = successResponseJSON.getBoolean("success");
                    if (!successResponse) {
                        throw new JSONException("Error in backend data update.");
                    }

                    DeliveryOperations deliveryOperations = new DeliveryOperations(LoggedActivity.this);
                    deliveryOperations.open();
                    int entries = 0;

                    for (Iterator<Delivery> iterator = deliveriesList.iterator(); iterator.hasNext(); ) {
                        Delivery delivery = iterator.next();
                        if(delivery.getStatus().equals("Delivered")){
                            deliveryOperations.deleteDelivery(delivery);
                            iterator.remove();
                            entries++;
                        }
                    }

                    deliveryOperations.close();
                    bundle.putInt("entries", entries);
                    Message message = new Message();
                    message.setData(bundle);
                    handleUnloadedDeliveriesToDB.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.load:
                System.out.println("Load button clicked");
                loadDeliveriesToDB(deliveryman.getId());
                return true;
            case R.id.unload:
                System.out.println("Unload button clicked");
                unloadDeliveriesToDB();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
