package com.lhht.xiaozhi.activities;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.lhht.xiaozhi.R;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class DroneActivity extends AppCompatActivity
{
    public Bitmap UdpAssemBitmap = null;                                //udp接收完成使用位图定义
    public Boolean BT_OnOffFlag = false;                                //udp接收线程启停位
    public Boolean IP_CheckFlag = true;                                 //IP检查标志位，只开机检查1次
    public volatile Boolean Thread_RunningFlag = false;                 //UDP接收线程运行标志位
    /**
     * 控件定义
     **/
    public Button BT_UdpOnOff;
    public Button BT_Forward;
    public Button BT_Backward;
    public Button BT_TurnLeft;
    public Button BT_TurnRight;
    public Button BT_Break;
    public ImageView IMG_imageView;
    /**
     * UDP服务端IP和端口ESP32CAM->phone
     **/
    public String UDPSeverIP = "192.168.4.2";    //phone的IP
    public int UDPSeverPort = 10000;             //phone接收数据时的端口

    public int UDPphonePort=10010;               //phone发送数据时的端口
    /**
     * UDP客户端IP和端口phone->ESP32CAM
     **/
    public String UDPClientIP = "192.168.4.1";  //ESP32CAM的IP
    public int UDPClientPort = 10001;           //ESP32CAM的发送图像数据的端口

    public int UDPDataPort = 10011;             //ESP32CAM的接收数据的端口
    /**
     * UDP客户端通信定义
     **/
    DatagramSocket UDPSendSocket = null;             //定义UDP 发送socket
    InetAddress UDPClientAddress = null;             //客户端端IP
    /**
     * UDP服务端通信定义
     **/
    DatagramSocket UDPReceiveSocket = null;         //定义UDP 接收socket
    InetAddress UDPSeverAddressIP = null;           //服务端端IP
    /**
     * 显示机IP使用定义
     **/
    private TextView ipTextView = null;
    private TextView netTypeView = null;
    /**
     * 读取本机IP使用定义
     **/
    private ConnectivityManager mConnectivityManager = null;
    private NetworkInfo mActiveNetInfo = null;


    /*******************************************************************************
     * ......................Handler此种形式需在主线程开始前定义......................
     ******************************************************************************/
    private Handler myHandler = new Handler(Looper.myLooper())
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    IMG_imageView.setImageBitmap((Bitmap) msg.obj);   //图像显示
                    break;
                case 1:
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * ......................主线程初始开始处......................
     **/
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone);

        initWindowSettings();                   //初始化窗口设置,常亮

        /*IP显示相关监听事件注册...................*/
        netTypeView = findViewById(R.id.netTypeView);
        ipTextView = findViewById(R.id.ipTextView);
        if (IP_CheckFlag == true) //打开APP后检查一次IP地址
        {
            IP_CheckFlag = false;
            mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);  //获取系统的连接服务
            mActiveNetInfo = mConnectivityManager.getActiveNetworkInfo();                         //获取网络连接的信息
            if (mActiveNetInfo == null)
            {
                myDialog();
            }
            else
            {
                setUpInfo();
            }
        }

        /*ImageView监听事件注册...................*/
        IMG_imageView = findViewById(R.id.imageView);

        /*按钮监听事件注册...................*/
        BT_UdpOnOff = findViewById(R.id.UdpOnOff);
        BT_UdpOnOff.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO Auto-generated method stub
                if (BT_OnOffFlag == false)
                {
                    BT_OnOffFlag = true;
                    BT_UdpOnOff.setBackgroundResource(R.drawable.udp2);                      //背景图片切换ON

                    Thread_RunningFlag = true;                                               //UDP接收标志位修改，线程使能
                    Runnable My_UDP_Receive_Runnable = new UDP_Receive_Runnable();           //创建My_UDP_Receive_Runnable对象
                    Thread UdpRecThread = new Thread(My_UDP_Receive_Runnable);               //UDP数据接收线程创建并启动
                    UdpRecThread.start();
                }
                else
                {
                    BT_OnOffFlag = false;
                    BT_UdpOnOff.setBackgroundResource(R.drawable.udp);       //背景图片切换OFF

                    Thread_RunningFlag = false;                              //UDP接收标志位修改，退出线程使能
                }

                // Toast.makeText(MainActivity.this, "这是第一个UdpOnOff", Toast.LENGTH_SHORT).show();

            }
        });

        BT_Forward = findViewById(R.id.Forward);
        BT_Forward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Toast.makeText(DroneActivity.this, "这是第一个Forward", Toast.LENGTH_SHORT).show();
            }
        });

        BT_Backward = findViewById(R.id.Backward);
        BT_Backward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Toast.makeText(DroneActivity.this, "这是第一个Backward", Toast.LENGTH_SHORT).show();
            }
        });

        BT_TurnLeft = findViewById(R.id.TurnLeft);
        BT_TurnLeft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                UdpSendData();
                Toast.makeText(DroneActivity.this, "这是第一个TurnLeft", Toast.LENGTH_SHORT).show();
            }
        });

        BT_TurnRight = findViewById(R.id.TurnRight);
        BT_TurnRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Toast.makeText(DroneActivity.this, "这是第一个TurnRight", Toast.LENGTH_SHORT).show();
            }
        });

        BT_Break = findViewById(R.id.Break);
        BT_Break.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Toast.makeText(DroneActivity.this, "这是第一个Break", Toast.LENGTH_SHORT).show();

            }
        });


    }

    /**
     * ......................主线程终止处......................
     **/
    protected void onDestroy()
    {
        super.onDestroy();
    }

    /***********************************************************
     * ......................自写方法开始处......................
     **********************************************************/
    /**
     * ......................初始化窗口设置, 包括全屏、常亮、横屏......................
     **/
    private void initWindowSettings()
    {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * ......................本机IP地址查找......................
     **/
    public String getIPAddress()
    {
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected())
        {
            if ((info.getType() == ConnectivityManager.TYPE_MOBILE) || (info.getType() == ConnectivityManager.TYPE_WIFI)) //当前使用2G/3G/4G网络
            {
                try
                {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); )
                    {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); )
                        {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
                            {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                }
                catch (SocketException e)
                {
                    e.printStackTrace();
                }
                finally
                {

                }
            }
        }
        else  //当前无网络连接,请在设置中打开网络
        {
            return null;
        }
        return null;
    }

    /**
     * ......................UI显示IP地址......................
     **/
    public void setUpInfo()
    {
        if (mActiveNetInfo.getType() == ConnectivityManager.TYPE_WIFI)
        {
            netTypeView.setText("网络类型：WIFI");
            ipTextView.setText("IP地址：" + getIPAddress());
        }
        else if (mActiveNetInfo.getType() == ConnectivityManager.TYPE_MOBILE)
        {
            netTypeView.setText("网络类型：3G/4G");
            ipTextView.setText("IP地址：" + getIPAddress());
        }
        else
        {
            netTypeView.setText("网络类型：未知");
            ipTextView.setText("IP地址：");
        }
    }

    /**
     * ......................自定义Dialog对话框......................
     **/
    private void myDialog()
    {
        AlertDialog mDialog = new AlertDialog.Builder(DroneActivity.this).setTitle("注意").setMessage("当前网络不可用，请检查网络！").setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                DroneActivity.this.finish();
            }
        }).create();//创建这个对话框
        mDialog.show();//显示这个对话框
    }

//    /**
//     * 选择变换
//     *
//     * @param origin      原图
//     * @param BitmapAngle 旋转角度，可正可负
//     * @return 旋转后的图片
//     */
//    private Bitmap rotateBitmap(Bitmap origin, float BitmapAngle)
//    {
//        if (origin == null)
//        {
//            return null;
//        }
//        int width = origin.getWidth();
//        int height = origin.getHeight();
//        Matrix matrix = new Matrix();
//        matrix.setRotate(BitmapAngle);
//        // 围绕原地进行旋转
//        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
//        if (newBM.equals(origin))
//        {
//            return newBM;
//        }
//        origin.recycle();
//        return newBM;
//    }

    /**
     * ......................调用UDP发送数据线程，json数据打包发送方法......................
     **/
    public void UdpSendData()
    {
        String sendData;//创建接收缓存区;
        sendData = "{\"DICar\":{\"data\":" + 110 + "}}";
        UDPSend(sendData);
    }

    /**
     * ......................UDP发送数据线程......................
     **/
    public void UDPSend(final String send_data)
    {
        //开启线程发送数据
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    UDPClientAddress = InetAddress.getByName(UDPClientIP);
                    UDPSendSocket = new DatagramSocket(UDPphonePort);
                    int length_data = send_data.length();
                    DatagramPacket sendPacket = new DatagramPacket(send_data.getBytes(), length_data, UDPClientAddress,UDPDataPort);//目标ESP32CAM的IP和端口
                    UDPSendSocket.send(sendPacket);
                    UDPSendSocket.close();
                }
                catch (Exception e)
                {
                    Log.d("TAG", "UDPSend_Exception_ERROR");
                    e.printStackTrace();
                }
                finally
                {

                }
            }
        }).start();
    }

//    /**
//     * ......................数组清空方法......................
//     **/
//    private static byte[] arrayReset(byte[] a)
//    {
//        byte[] b = new byte[a.length];
//        for (int i = 0; i < a.length; i++)
//        {
//            a[i] = b[i];
//        }
//        return a;
//    }


    /**
     * ......................UDP接收数据线程......................
     **/
    class UDP_Receive_Runnable implements Runnable
    {
        @Override
        public void run()        //开启线程接收数据
        {
            try
            {
                boolean dataGetFlag = false;
                int picCopyIndex = 0;
                int buffSize = 0;
                String clientDatalengthStr;

                byte[] picData_buf = new byte[1024 * 35];                                                           //创建一个35kB的图像缓冲区（数据队列）
                byte[] udpReceive_buff = new byte[1024];                                                            //创建接收数据的数据buff

                UDPSeverAddressIP = InetAddress.getByName(UDPSeverIP);                                              //获取IP地址

                UDPReceiveSocket = new DatagramSocket(UDPSeverPort);                                                //创建服务接收端对象

                DatagramPacket receivePacket = new DatagramPacket(udpReceive_buff, udpReceive_buff.length);         //创建缓存区

                Log.d("TAG", "接受数据： " + UDPSeverAddressIP + "   " + UDPSeverPort + "   " + "RECEIVE_THREAD_On: " + Thread_RunningFlag);

                while (Thread_RunningFlag == true)
                {


//                    Thread.sleep(3);                                                                             //线程延时添加，避免卡死

                    UDPReceiveSocket.receive(receivePacket);                                                       //接受数据

//                    String clientData = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
//                    int clientDataLength = receivePacket.getLength();                                            //获取数据长度
//                    int clientPort = receivePacket.getPort();                                                    //通过数据包对象的方法,解析其中的端口
//                    String clientIp = receivePacket.getAddress().getHostAddress();                               //通过数据包对象的方法,解析其中的IP
//
//                    Log.d("TAG", "接受数据=====" + clientData + " 数据长度: " + clientDataLength + " 端口: " + clientPort + " IP: " + clientIp);
                    try
                    {
                        if (receivePacket.getLength() >= 2)        //如果接收数据大于2,则代表接收到数据
                        {
                            if (udpReceive_buff[0] == 'o' && udpReceive_buff[1] == 'k')
                            {
                                dataGetFlag = true;                //当接收到ok信息了,表示马下就会接收下一个包,即图片大小信息包.同时上一次传送的图片信息已接收完毕了
                                if (picCopyIndex >= buffSize)
                                {

                                    UdpAssemBitmap = BitmapFactory.decodeByteArray(picData_buf, 0, picData_buf.length); //字节数据输入流,生成位图

                                    Message message = Message.obtain(); //将数据位图发送到主线程
                                    message.what = 0;
                                    message.obj = UdpAssemBitmap;
                                    myHandler.sendMessage(message);

                                    UdpAssemBitmap = null;

                                    buffSize = 0;
                                    picCopyIndex = 0;

                                    for (int i = 0; i < udpReceive_buff.length; i++) //清空receivePacket的承接对象数组
                                    {
                                        udpReceive_buff[i] = 0;
                                    }
                                    for (int j = 0; j < picData_buf.length; j++) //清空picData_buf数组
                                    {
                                        picData_buf[j] = 0;
                                    }


                                }
                            }
                            else
                            {
                                if (dataGetFlag == true) //接收第二个包，内容为总数据长度
                                {
                                    dataGetFlag = false; //设置为false,表示这以后为第3个包了

                                    buffSize = 0;
                                    picCopyIndex = 0;

                                    clientDatalengthStr = new String((receivePacket.getData()), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
                                    buffSize = Integer.parseInt(clientDatalengthStr); //解析出总数居的长度int

//                                Log.d("TAG", "图片接受clientData2: " + clientDatalengthStr + "图片接受buffSize: " + buffSize);
                                    for (int i = 0; i < udpReceive_buff.length; i++) //清空receivePacket的承接对象数组
                                    {
                                        udpReceive_buff[i] = 0;
                                    }
                                    for (int j = 0; j < picData_buf.length; j++) //清空picData_buf数组
                                    {
                                        picData_buf[j] = 0;
                                    }
                                }
                                else                                                 //开始接收并组合有效的数据包
                                {
                                    System.arraycopy((receivePacket.getData()), 0, picData_buf, picCopyIndex, receivePacket.getLength());
                                    picCopyIndex = picCopyIndex + receivePacket.getLength(); //将第3个包及以后的图片包信息保存于内存缓冲,即将图片包拼装起来组成一张图片信息.

//                                Log.d("TAG", "picCopyIndex: " + picCopyIndex);
                                }
                            }
                        }
                        else     //清空receivePacket的承接对象数组
                        {
                            for (int i = 0; i < udpReceive_buff.length; i++) //清空receivePacket的承接对象数组
                            {
                                udpReceive_buff[i] = 0;
                            }
                            for (int j = 0; j < picData_buf.length; j++) //清空picData_buf数组
                            {
                                picData_buf[j] = 0;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d("TAG", "图片解析错误");
                        e.printStackTrace();

                        buffSize = 0;
                        picCopyIndex = 0;

                        for (int i = 0; i < udpReceive_buff.length; i++) //清空receivePacket的承接对象数组
                        {
                            udpReceive_buff[i] = 0;
                        }
                        for (int j = 0; j < picData_buf.length; j++) //清空picData_buf数组
                        {
                            picData_buf[j] = 0;
                        }
                    }
                    finally
                    {

                    }

                }
            }
            catch (Exception e)
            {
                Log.d("TAG", "UDPRec_Exception_Error: " + e.getMessage());
                e.printStackTrace();
            }
            finally
            {
                UDPReceiveSocket.disconnect();
                UDPReceiveSocket.close();

                Thread_RunningFlag = false;                              //修改程创标志位，退出线程使能
                BT_UdpOnOff.setBackgroundResource(R.drawable.udp);       //背景图片切换OFF
                Log.d("TAG", "=====接受线程终止=====");
            }


        }
    }


}