package com.lhht.xiaozhi.activities.BtThread;

import android.bluetooth.BluetoothSocket;
import android.util.Log;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//连接了蓝牙设备建立通信之后的数据交互线程类
public class ConnectedThread extends Thread{
    String lastX, lastY;
    BluetoothSocket bluetoothSocket=null;
    InputStream inputStream=null;//获取输入数据
    OutputStream outputStream=null;//获取输出数据
    int[] lastData=new int[]{0,0};
    private boolean isRunning = true;
    public ConnectedThread(BluetoothSocket bluetoothSocket){
        this.bluetoothSocket=bluetoothSocket;
        //先新建暂时的Stream
        InputStream inputTemp=null;
        OutputStream outputTemp=null;
        try {
            inputTemp=this.bluetoothSocket.getInputStream();
            outputTemp=this.bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            try {
                bluetoothSocket.close();//出错就关闭线程吧
            } catch (IOException ex) {}
        }
        inputStream=inputTemp;
        outputStream=outputTemp;
    }

    @Override
    public void run() {
        super.run();
        while(true){
            //发送数据

            //蓝牙对应硬件控制说明
            //硬件为esp32接收蓝牙单个字符
            //1.前进-a
            //2.后退-b
            //3.左转-c
            //4.右转-d
            //5.停止-e
            //以上通过语音控制

//            if (MainActivity.getCenterx>=700){
//                btWriteString("d");
//            }
//            else if (MainActivity.getCenterx<=300) {
//                btWriteString("c");
//            }else{
//
//            }

        }
    }



//软件自身有一个opencv识别人脸功能，通过识别图像中的坐标点，将信号通过蓝牙发送给硬件
//    public void run() {
//        super.run();
//        while (isRunning) {
//            try {
//                if (MainActivity.x != null && MainActivity.y != null) {
//                    lastX = MainActivity.x.getText().toString();
//                    lastY = MainActivity.y.getText().toString();
//                    btWriteString("水平:" + lastX + "\n" + "垂直:" + lastY + "\n");
//                    btWriteString("你好"+"\n");
//                } else {
//                    lastX = "500";
//                    lastY = "300";
//                }
//                Thread.sleep(1000); // 每秒发送一次数据
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    public void btWriteInt(int[] intData){
        for(int sendInt:intData){
            try {
                outputStream.write(sendInt);
                Log.d("Bluetooth", "成功发送整型: " + sendInt);
            } catch (IOException e) {
                Log.e("Bluetooth", "发送失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    //自定义的发送字符串的函数
    public void btWriteString(String string){
        for(byte sendData:string.getBytes()){
            try {
                outputStream.write(sendData);
                Log.d("Bluetooth", "成功发送字节: " + sendData);
            } catch (IOException e) {
                Log.e("Bluetooth", "发送失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    //自定义的关闭Socket线程的函数
    public void cancel(){
        try {
            bluetoothSocket.close();
        } catch (IOException e) {}
    }
}
