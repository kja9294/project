package com.example.user.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.content.SharedPreferences;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MainActivity extends AppCompatActivity {
    static String host="tcp://14.63.171.234:1883";
    static String NAME="950303";
    static String PASS="911111";
    String topic=" ";
    MqttAndroidClient client;
    ImageView image;
    ImageView imageView2;
    int systemcheck=0;
    int fan=0;
    int win=0;
    int one=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        topic=getPreferences();
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), host, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(NAME);
        options.setPassword(PASS.toCharArray());
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, topic+" connected", Toast.LENGTH_LONG).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "connection fail", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button nextButton=(Button) findViewById(R.id.nextButton);
        image=(ImageView) findViewById(R.id.image);
        imageView2=(ImageView) findViewById(R.id.imageView2);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Subacitvity.class);
                startActivityForResult(intent, 0);
            }
        });
        final ToggleButton wb=(ToggleButton)this.findViewById(R.id.toggleButton);
        wb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String message = "windowOn";
                if(wb.isChecked()){
                    win=1;
                    try {
                        client.publish(topic, message.getBytes(),1,false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }else {
                    win=0;
                    message = "windowOff";
                    try {
                        client.publish(topic, message.getBytes(), 1, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        final ToggleButton tb=(ToggleButton)this.findViewById(R.id.button);
        tb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String message = "on";
                if(tb.isChecked()){
                    fan=1;
                    if(win==0){
                        wb.performClick();
                    }
                    wb.setEnabled(false);
                    try {
                        client.publish(topic, message.getBytes(),1,false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }else {
                    fan=0;
                    message = "off";
                    if(win==1){
                        wb.performClick();
                    }
                    wb.setEnabled(true);
                    try {
                        client.publish(topic, message.getBytes(), 1, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        final ToggleButton sb=(ToggleButton)this.findViewById(R.id.자동시스템);
        sb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String message = "SystemOn";
                imageView2.setImageResource(R.drawable.oneon);
                if(fan==1){
                    tb.performClick();
                }
                if(win==1){
                    wb.performClick();
                }
                tb.setEnabled(false);
                wb.setEnabled(false);
                if(sb.isChecked()){
                    one=1;
                    systemcheck=1;
                    try {
                        client.publish(topic, message.getBytes(),1,false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }else{
                    one=0;
                    message="SystemOff";
                    systemcheck=0;
                    imageView2.setImageResource(R.drawable.oneoff);
                   tb.setEnabled(true);
                   wb.setEnabled(true);
                    try {
                        client.publish(topic, message.getBytes(),1,false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
       client.setCallback(new MqttCallback() {
           @Override
           public void connectionLost(Throwable cause) {

           }

           @Override
           public void messageArrived(String topic, MqttMessage message) throws Exception {
               change(new String(message.getPayload(),"UTF-8"));
           }

           @Override
           public void deliveryComplete(IMqttDeliveryToken token) {

           }
       });

    }
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 0:
                topic= data.getStringExtra("주제");
                savePreferences(topic);
                String clientId = MqttClient.generateClientId();
                client = new MqttAndroidClient(this.getApplicationContext(), host, clientId);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setUserName(NAME);
                options.setPassword(PASS.toCharArray());
                try {
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Toast.makeText(MainActivity.this, topic+" connected", Toast.LENGTH_LONG).show();
                            setSubscription();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Toast.makeText(MainActivity.this, "connection fail", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    private void setSubscription(){
        try{
            client.subscribe(topic,0);
        }catch(MqttException e){
            e.printStackTrace();
        }
    }
    public void change(String alarm){
        if(alarm.contains("clean")){
            image.setImageResource(R.drawable.greens);
        }
        else if(alarm.contains("dirty")){
            image.setImageResource(R.drawable.reds);
            }
        else if(alarm.contains("needClean")){
            image.setImageResource(R.drawable.redstop);
            if(one==1){
            final ToggleButton sb=(ToggleButton)this.findViewById(R.id.자동시스템);
            sb.performClick();
            }
        }
    }
    private String getPreferences(){

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        return pref.getString("hi", "bye");

    }
    private void savePreferences(String sub){

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.putString("hi", sub);

        editor.commit();

    }
}