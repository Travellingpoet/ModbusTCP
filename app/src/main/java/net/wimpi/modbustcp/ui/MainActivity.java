package net.wimpi.modbustcp.ui;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
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
import net.wimpi.modbustcp.IBackService;
import net.wimpi.modbustcp.R;
import net.wimpi.modbustcp.bean.ConnectionInfo;
import net.wimpi.modbustcp.interfaces.IConnectionManager;
import net.wimpi.modbustcp.util.ManagerHolder;
import net.wimpi.modbustcp.util.SocketService;
import net.wimpi.modbustcp.util.SocketUtil;
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
    private Intent mServiceIntent;
    private IBackService iBackService;
    private ConnectionInfo mConnectionInfo;
    private IConnectionManager mManager;

//    public static final String IP_ADDRESS= "192.168.100.52";
//    public static final String IP_ADDRESS= "192.168.137.1";
    public static final String IP_ADDRESS= "10.14.2.81";//?????????IP??????
    private String address = "";

//    int port = 8090;//Android 1024 ???????????????????????????????????????root??????
    int port = 60000;//??????????????????

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
        //???????????????????????????????????????
//        mServiceIntent = new Intent(this,SocketService.class);

//        createModbusTcp();

        createTcp();

    }

    private void initListener(){
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TcpReceiveThread.getInstance().disconnect();
//                SocketService.getInstance().disconnect();
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
//        //???????????????
//        spi.addDigitalOut(new SimpleDigitalOut(true));
//        spi.addDigitalOut(new SimpleDigitalOut(true));
//        spi.addDigitalOut(new SimpleDigitalOut(true));
//        spi.addDigitalOut(new SimpleDigitalOut(true));
//        //???????????????
//        spi.addDigitalIn(new SimpleDigitalIn(false));
//        spi.addDigitalIn(new SimpleDigitalIn(true));
//        spi.addDigitalIn(new SimpleDigitalIn(false));
//        spi.addDigitalIn(new SimpleDigitalIn(true));
//        spi.addDigitalIn(new SimpleDigitalIn(true));
//        spi.addDigitalIn(new SimpleDigitalIn(true));
//        spi.addDigitalIn(new SimpleDigitalIn(true));
//        spi.addDigitalIn(new SimpleDigitalIn(true));
        //???????????????
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
//        //???????????????
//        spi.addInputRegister(new SimpleInputRegister(45));


        //create the coupler holding the image
        ModbusCoupler.getReference().setProcessImage(spi);
        ModbusCoupler.getReference().setMaster(false);
        ModbusCoupler.getReference().setUnitID(15);

        new Thread(networkTask).start();
        Log.e(TAG, "?????????IP = " + getHostIP());

        readLocalRegisterAddress();
    }

    private void createTcp(){
        mConnectionInfo = new ConnectionInfo(IP_ADDRESS,port);
        mManager = SocketUtil.open(mConnectionInfo);
        connectedStatus();
//        TcpReceiveThread.getInstance().connect("192.168.100.1",8090);
//        TcpReceiveThread.getInstance().connect("".equals(address) ? IP_ADDRESS : address ,port);
//        TcpReceiveThread.getInstance().setTcpReceiveListener(new TcpReceiveThread.TcpReceiveListener() {
//            @Override
//            public void onRealData(final String receicedMessage) {
//                Log.d(TAG,receicedMessage);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d(TAG,receicedMessage);
//                        tvModbusText.setText(receicedMessage + "\n"
//                                + String.format("????????????????????????%s",TcpReceiveThread.getInstance().isConnected()?"?????????":"?????????"));
////                        tvModbusText1.setText(String.format("????????????????????????%s",TcpReceiveThread.getInstance().isConnected()?"?????????":"?????????"));
//                    }
//                });
//            }
//
//            @Override
//            public void onServerDisconnected(IOException e) {
//                Log.d(TAG,"server Disconnected" + e.getMessage());
//                tvStatus.setText("????????????:" + e.toString());
//                TcpReceiveThread.getInstance().setNull();
//            }
//
//            @Override
//            public void onServerConnected() {
//                tvStatus.setText("");
//                Log.d(TAG,"server connected");
//            }
//
//            @Override
//            public void onServerReconnectError() {
//                Log.d(TAG,"onServerReconnectError");
//                TcpReceiveThread.getInstance().connect(IP_ADDRESS,port);
//            }
//        });
    }

    private void connectedStatus(){
        if (mManager == null){
            return;
        }
        if (!mManager.isConnect()) {
            mManager.connect();
        }else {
            mManager.disconnect();
        }
    }

    /**
     * ??????????????????????????????
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            // ??????????????? http request.????????????????????????
            listener = new ModbusTCPListener(9);
            listener.setPort(port);
            listener.start();
        }
    };

    /**
     * ??????ip??????
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
     * ?????????????????????
     */
    private void readLocalRegisterAddress() {

        Runnable localRunnable = new Runnable() {
            @Override
            public void run() {
                try {
//                    int registerAddress = 0;
                    while (true) {
                        //?????????????????????0?????????????????????add?????????
                        //??????????????????AO???
//                        if (registerAddress > 8) registerAddress = 0;

                        Register[] register = spi.getRegisterRange(0, spi.getRegisterCount());
                        //??????
//                        int readRegisterInt = register.getValue();//int??????
//                        int readRegisterUnsignedShort = register.toUnsignedShort();//???????????????
//                        short readRegisterShort = register.toShort();//short??????
//                        byte[] readRegisterBytes = register.toBytes();//byte[]??????

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
     * ?????????
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

    private void registerReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.HEART_BEAT_ACTION);
        intentFilter.addAction(SocketService.MESSAGE_ACTION);
        registerReceiver(mReceiver,intentFilter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(SocketService.MESSAGE_ACTION)){
                String receicedMessage = intent.getStringExtra("message");
                Log.d(TAG,receicedMessage);
                tvModbusText.setText(receicedMessage + "\n"
                        + String.format("????????????????????????%s",TcpReceiveThread.getInstance().isConnected()?"?????????":"?????????"));
            }else if (action.equals(SocketService.HEART_BEAT_ACTION)){
               tvModbusText.setText("????????????");
            }
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //?????????
            iBackService = IBackService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //???????????????
            iBackService = null;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
//        bindService(mServiceIntent,conn,BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(mReceiver);
//        unbindService(conn);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}

