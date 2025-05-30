package com.lhht.xiaozhi.activities.BtThread;


import static com.lhht.xiaozhi.activities.BluetoothActivity.MY_UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

//连接蓝牙设备的操作线程类
public class ConnectThread extends Thread{

    BluetoothDevice bluetoothDevice=null;
    //蓝牙套接字
    public static BluetoothSocket bluetoothSocket=null;
    public ConnectThread(BluetoothDevice bluetoothDevice){
        this.bluetoothDevice=bluetoothDevice;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        super.run();
        try {
            bluetoothSocket=this.bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            //连接
            bluetoothSocket.connect();
        } catch (IOException e) {
            try {
                bluetoothSocket.close();//出错就断开连接
            } catch (IOException ex) {}
        }
    }
    //自定义的断开连接
    public void cancel(){
        if(bluetoothSocket!=null){
            try {
                bluetoothSocket.close();
            } catch (IOException e) {}
            bluetoothSocket=null;
        }
    }
}
