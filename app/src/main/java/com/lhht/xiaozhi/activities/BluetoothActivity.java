package com.lhht.xiaozhi.activities;
import static com.lhht.xiaozhi.activities.BtThread.ConnectThread.bluetoothSocket;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.lhht.xiaozhi.R;
import com.lhht.xiaozhi.activities.BtThread.ConnectThread;
import com.lhht.xiaozhi.activities.BtThread.ConnectedThread ;



import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {
    //UUID 是 Java 中的一个类，代表通用唯一标识符（Universally Unique Identifier）。
    // UUID 是一个 128 位的值，通常用于在分布式系统中标识信息。
    public static UUID MY_UUID= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//符合UUID格式就行。
    Button back=null;
    ListView btList=null;
    Intent intent=null;
    //蓝牙操作
    BluetoothAdapter bluetoothAdapter=null;
    List<String> devicesNames=new ArrayList<>();
    //提供了快速的随机访问性能，因为它是基于数组的。
    //ArrayList 在添加或删除元素时可能需要更多的时间，特别是当数组需要扩容时
    ArrayList<BluetoothDevice> readyDevices=null;
    //可以将数组或列表中的数据展示在 ListView、Spinner 等控件中。
    ArrayAdapter<String> btNames=null;

    //自定义线程类的初始化
    static ConnectThread connectThread=null;
    static ConnectedThread connectedThread=null;

    @SuppressLint({"MissingPermission", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth);

        back=(Button) findViewById(R.id.back);
        btList=(ListView) findViewById(R.id.btList);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在返回主界面的操作中开启发送数据的线程
                if(bluetoothSocket!=null&&bluetoothSocket.isConnected()){//先判断连接上了
                    connectedThread=new ConnectedThread(bluetoothSocket);
                    connectedThread.start();
                    Toast.makeText(BluetoothActivity.this,"已开启数据线程，蓝牙真正连接",Toast.LENGTH_SHORT).show();
                    intent=new Intent(BluetoothActivity.this,VoiceCallActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(BluetoothActivity.this,"蓝牙未连接",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //获取蓝牙适配器的信息
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        //先打开蓝牙
        if(!bluetoothAdapter.isEnabled()){
            intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,1);//不用管，填1就好，表示打开蓝牙的
        }
        //set这是一个集合类型，表示一个不包含重复元素的集合。在这里，它用于存储这是BluetoothAdapter类的一个方法，
        // 用于获取当前已配对的蓝牙设备列表。它返回一个Set<BluetoothDevice>对象，其中包含所有已配对的设备。
        @SuppressLint("MissingPermission")
        Set<BluetoothDevice> pairedDevices=bluetoothAdapter.getBondedDevices();
        readyDevices=new ArrayList();
        if(pairedDevices!=null&&pairedDevices.size()>0){
            for(BluetoothDevice device:pairedDevices){
                readyDevices.add(device);
                devicesNames.add(device.getName());
                btNames=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,devicesNames);
            }
            btList.setAdapter(btNames);
        }else{
            Toast.makeText(this, "没有设备已经配对！", Toast.LENGTH_SHORT).show();
        }

        //列表项目点击事件，点击蓝牙设备名称，然后连接
        btList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //看看点击蓝牙设备是否可行
                //Toast.makeText(BluetoothActivity.this, "点击了"+readyDevices.get(position).getName(), Toast.LENGTH_SHORT).show();

                //做连接操作
                //先判断是否有连接，我们只要一个连接，在这个软件内只允许有一个连接
                if(connectThread!=null){//如果不为空，就断开这个连接
                    connectThread.cancel();
                    connectThread=null;
                }
                //开始连接新的设备对象
                connectThread=new ConnectThread(readyDevices.get(position));
                connectThread.start();//start（）函数开启线程，执行操作
                Toast.makeText(BluetoothActivity.this, "已尝试连接"+readyDevices.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}