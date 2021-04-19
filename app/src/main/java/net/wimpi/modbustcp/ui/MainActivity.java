package net.wimpi.modbustcp.ui;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.net.ModbusTCPListener;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;
import net.wimpi.modbus.procimg.SimpleInputRegister;
import net.wimpi.modbus.procimg.SimpleProcessImage;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbustcp.R;
import net.wimpi.modbustcp.util.TcpReceiveThread;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    ModbusTCPListener listener = null;
    SimpleProcessImage spi = null;
    private TextView tvModbusText;
    private EditText editAddress;
    private Button modifyButton;
    private Button postButton;
    private TextView tvStatus;

//    public static final String IP_ADDRESS= "192.168.100.52";
    public static final String IP_ADDRESS= "192.168.137.1";
    private String address = "";

    int port = 8090;//Android 1024 以下端口属于系统端口，需要root权限

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvModbusText = findViewById(R.id.tvModbusText);
//        tvModbusText1 = findViewById(R.id.tvModbusText1);
        editAddress = findViewById(R.id.edit_address);
        modifyButton = findViewById(R.id.modify_button);
        postButton = findViewById(R.id.post_button);
        tvStatus = findViewById(R.id.text_status_detail);

        //prepare a process image
        initListener();

//        createModbusTcp();

        createTcp();
    }

    private void initListener(){
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TcpReceiveThread.getInstance().disconnect();
                address = editAddress.getText().toString();
                tvModbusText.setText("");
                tvStatus.setText("");
                createTcp();
            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void createModbusTcp(){
        spi = new SimpleProcessImage();
//        //线圈寄存器
//        spi.addDigitalOut(new SimpleDigitalOut(true));
//        spi.addDigitalOut(new SimpleDigitalOut(true));
//        spi.addDigitalOut(new SimpleDigitalOut(true));
//        spi.addDigitalOut(new SimpleDigitalOut(true));
//        //状态寄存器
//        spi.addDigitalIn(new SimpleDigitalIn(false));
//        spi.addDigitalIn(new SimpleDigitalIn(true));
//        spi.addDigitalIn(new SimpleDigitalIn(false));
//        spi.addDigitalIn(new SimpleDigitalIn(true));
//        spi.addDigitalIn(new SimpleDigitalIn(true));
//        spi.addDigitalIn(new SimpleDigitalIn(true));
//        spi.addDigitalIn(new SimpleDigitalIn(true));
//        spi.addDigitalIn(new SimpleDigitalIn(true));
        //保持寄存器
        for (int i = 0; i < 36; i++) {
            spi.addRegister(new SimpleRegister(0));
//        spi.addRegister(new SimpleRegister(11));
//        spi.addRegister(new SimpleRegister(111));
//        spi.addRegister(new SimpleRegister(2));
//        spi.addRegister(new SimpleRegister(22));
//        spi.addRegister(new SimpleRegister(222));
//        spi.addRegister(new SimpleRegister(3));
//        spi.addRegister(new SimpleRegister(33));
//        spi.addRegister(new SimpleRegister(333));
//        spi.addRegister(new SimpleRegister(3333));
        }
//        //输入寄存器
//        spi.addInputRegister(new SimpleInputRegister(45));


        //create the coupler holding the image
        ModbusCoupler.getReference().setProcessImage(spi);
        ModbusCoupler.getReference().setMaster(false);
        ModbusCoupler.getReference().setUnitID(15);

        new Thread(networkTask).start();
        Log.e(TAG, "本机的IP = " + getHostIP());

        readLocalRegisterAddress();
    }

    private void createTcp(){
//        TcpReceiveThread.getInstance().connect("192.168.100.1",8090);
        TcpReceiveThread.getInstance().connect("".equals(address) ? IP_ADDRESS : address ,port);
        TcpReceiveThread.getInstance().setTcpReceiveListener(new TcpReceiveThread.TcpReceiveListener() {
            @Override
            public void onRealData(final String receicedMessage) {
                Log.d(TAG,receicedMessage);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvModbusText.setText(receicedMessage + "\n"
                                + String.format("服务器连接状态：%s",TcpReceiveThread.getInstance().isConnected()?"已连接":"未连接"));
//                        tvModbusText1.setText(String.format("服务器连接状态：%s",TcpReceiveThread.getInstance().isConnected()?"已连接":"未连接"));
                    }
                });
            }

            @Override
            public void onServerDisconnected(IOException e) {
                Log.d(TAG,"server Disconnected" + e.getMessage());
                tvStatus.setText("连接超时:" + e.toString());
                TcpReceiveThread.getInstance().reconnect();
            }

            @Override
            public void onServerConnected() {
                Log.d(TAG,"server connected");
            }

            @Override
            public void onServerReconnectError() {
                Log.d(TAG,"onServerReconnectError");
                TcpReceiveThread.getInstance().connect(IP_ADDRESS,port);
            }
        });
    }

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            listener = new ModbusTCPListener(9);
            listener.setPort(port);
            listener.start();
        }
    };

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;
    }

    /**
     * 读取本地数据值
     */
    private void readLocalRegisterAddress() {

        Runnable localRunnable = new Runnable() {
            @Override
            public void run() {
                try {
//                    int registerAddress = 0;
                    while (true) {
                        //寄存器地址，从0开始，等于之前add的顺序
                        //保持寄存器（AO）
//                        if (registerAddress > 8) registerAddress = 0;

                        Register[] register = spi.getRegisterRange(0, spi.getRegisterCount());
                        //读值
//                        int readRegisterInt = register.getValue();//int类型
//                        int readRegisterUnsignedShort = register.toUnsignedShort();//无符号整型
//                        short readRegisterShort = register.toShort();//short类型
//                        byte[] readRegisterBytes = register.toBytes();//byte[]类型

                        if (register != null) {
                            final String temp = Arrays.toString(convertRegister2(register));
                            Log.d(TAG,"modbus data--111->"+temp);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    if (!temp.equals(tvModbusText.getText())){
                                    tvModbusText.setText(temp);
//                                        Log.i("data", "readRegister[] :" + temp);
//                                    }
                                }
                            });

//                            Log.i("data", "readRegister[] :" + convertRegister(register));
                        }
//                        registerAddress ++;
                        Thread.sleep(500);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        new Thread(localRunnable).start();
    }

    private String convertRegister(Register[] registers) {
        String result = "";
        List<Byte> data = new ArrayList<>();

        byte[] datas = new byte[registers.length * 2];

        if (registers != null) {
            StringBuffer stringBuffer = new StringBuffer();
            for (Register register : registers) {
                stringBuffer.append(register.getValue() + " ");
                data.add(register.toBytes()[0]);
                data.add(register.toBytes()[1]);
            }
            Log.d(TAG,"modbus data--->"+ Arrays.toString(data.toArray()));

            data.toArray();

            result = stringBuffer.toString();
        }
        return result;
    }

    private byte[] convertRegister2(Register[] registers) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            if (registers != null) {
                for (Register register : registers) {
                    data.write(register.toBytes()[0]);
                    data.write(register.toBytes()[1]);
                }
            }
        }finally {
            try {
                data.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return data.toByteArray();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        TcpReceiveThread.getInstance().disconnect();
        System.exit(0);
    }

    /**
     * 序列化
     * @param obj
     * @return
     */
    public static byte[] serialize2Bytes(Object obj) {
        byte[] result = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            result = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}

