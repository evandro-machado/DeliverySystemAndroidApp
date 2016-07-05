package com.example.evandro.delliveryclient;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String deliverymanStr = msg.getData().getString("deliveryman");
            Boolean isLoginFailed = Boolean.parseBoolean(deliverymanStr);
            System.out.println("Login failed? " + isLoginFailed);
            try {
                if (!deliverymanStr.equals("null")) {
                    System.out.println(msg.getData().get("Login Success."));
                    JSONObject deliverymanJson = new JSONObject(deliverymanStr);
                    String login = deliverymanJson.getString("login");
                    int deliverymanId = deliverymanJson.getInt("id");
                    Intent intent = new Intent(MainActivity.this, LoggedActivity.class);
                    Bundle params = new Bundle();
                    params.putInt("deliverymanId", deliverymanId);
                    params.putString("deliverymanLogin", login);
                    intent.putExtras(params);
                    startActivity(intent);
                } else {
                    System.out.println("Wrong login or password.");
                    Toast.makeText(MainActivity.this, "Wrong login or password.", Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    public void login(View view) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                EditText loginEditText = (EditText) findViewById(R.id.loginEditText);
                EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

                String password = passwordEditText.getText().toString();
                String login = loginEditText.getText().toString();

                String url = "http://169.254.254.199:8080/deliveryProject/login";
                WebService ws = new WebService(url);
                Map<String, String> params = new TreeMap<String, String>();
                params.put("action", "deliveryman");
                params.put("login", login);
                params.put("password", password);
                String response = ws.webGet("", params);
                Bundle bundle = new Bundle();
                try {
                    JSONObject json = new JSONObject(response);
                    String deliverymanStr = json.getString("deliveryman");
                    System.out.println(deliverymanStr);
                    bundle.putString("deliveryman", deliverymanStr);

                    Message message = new Message();
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
