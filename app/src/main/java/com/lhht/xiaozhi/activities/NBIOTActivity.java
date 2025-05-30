package com.lhht.xiaozhi.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lhht.xiaozhi.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class NBIOTActivity extends AppCompatActivity {
    private String ip="ssl://d4a0113a.ala.dedicated.aliyun.emqxcloud.cn:8883";
    private String username="xiaozhi";
    private String password="HLKhlk97";
    private String id="app"+System.currentTimeMillis();
    private String mqtt_sub_topic="xiaozhi";
    private String mqtt_pub_topic="nbiot";
    private MqttClient mqtt_client;
    private MqttConnectOptions options;
    private TextView textView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nbiotactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textView = findViewById(R.id.test);
        mqtt_init_Connect();
        mqtt_client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                System.out.println(msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(msg);
                    }
                });
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
    public void mqtt_init_Connect(){
        //初始化mqtt连接
        try {
            mqtt_client = new MqttClient(ip,id,new MemoryPersistence());
            options =new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(50);
            options.setAutomaticReconnect(true);
            options.setCleanSession(false);

            Connect();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(NBIOTActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();

        }
    }
    public void Connect(){
        try {
            Toast.makeText(NBIOTActivity.this, "开始连接", Toast.LENGTH_SHORT).show();

            mqtt_client.connect(options);
            mqtt_client.subscribe(mqtt_sub_topic);
            Toast.makeText(NBIOTActivity.this, "连接成功啦~", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(NBIOTActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
            Log.d("MQTTCON","连接失败");
        }
    }







}