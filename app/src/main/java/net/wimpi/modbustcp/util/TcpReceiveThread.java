package net.wimpi.modbustcp.util;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TcpReceiveThread {
    private static final String TAG = "TcpReceiveThread";
    //    Socket
    private Socket socket;
    //    IP地址
    private String ipAddress;
    //    端口号
    private int port;
    //    线程
    private Thread thread;
    //    Socket输出流
    private OutputStream outputStream;
    //    Socket输入流
    private InputStream inputStream;

    private static TcpReceiveThread instance;

    private Date lastKeepAliveOkTime;

    //核心线程数
    private final int corePoolSize = 3;
    //最大线程数
    private final int maximumPoolSize = 6;
    //超过 corePoolSize 线程数量的线程最大空闲时间
    private long keepAliveTime = 2;
    //以秒为时间单位
    TimeUnit unit = TimeUnit.SECONDS;
    //创建工作队列，用于存放提交的等待执行任务
    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(2);
    private Handler mHandler = new Handler();
    private static final long HEART_BEAT_RATE = 60 * 1000;


    private ExecutorService mThreadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            unit,
            workQueue,
            new ThreadPoolExecutor.AbortPolicy());

    //    构造函数私有化
    private TcpReceiveThread() {
        super();
    }

    //    提供一个全局的静态方法
    public static TcpReceiveThread getInstance() {
        if (instance == null) {
            synchronized (TcpReceiveThread.class) {
                if (instance == null) {
                    instance = new TcpReceiveThread();
                }
            }
        }
        return instance;
    }

    private class SocketThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                socket = new Socket(ipAddress, port);
//                    socket.setSoTimeout ( 2 * 1000 );//设置超时时间
//                    socket.setKeepAlive(true);//keepAlive保活机制
//                mHandler.postDelayed(heartBeatRunnable,HEART_BEAT_RATE);
                if (isConnected()) {
                    TcpReceiveThread.getInstance().ipAddress = ipAddress;
                    TcpReceiveThread.getInstance().port = port;
                    if (tcpReceiveListener != null) {
                        tcpReceiveListener.onServerConnected();
                    }
                    outputStream = socket.getOutputStream();
                    inputStream = socket.getInputStream();
                    receive();
//                    sendMessage("");
                    Log.i(TAG, "连接成功");
                } else {
                    Log.i(TAG, "连接失败");
                    if (tcpReceiveListener != null) {
                        tcpReceiveListener.onServerDisconnected(new IOException("连接失败"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "连接异常");
                if (tcpReceiveListener != null) {
                    tcpReceiveListener.onServerDisconnected(e);
                }
            }
        }
    }

    /**
     * 通过IP地址(域名)和端口进行连接
     *
     * @param ipAddress IP地址(域名)
     * @param port      端口
     */
    public void connect(final String ipAddress, final int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        mThreadPoolExecutor.execute(new SocketThread());
    }


    private int recount = 0;

    /**
     * 接收数据
     */
    public void receive() {
        while (isConnected()) {
            try {
                /**得到的是16进制数，需要进行解析*/
                byte[] bt = new byte[1024];
//                获取接收到的字节和字节数
                int length = inputStream.read(bt);
//                获取正确的字节
                if (length>=0) {
                    byte[] bs = new byte[length];
                    System.arraycopy(bt, 0, bs, 0, length);

                    String str = new String(bs, "UTF-8");
                    if (str != null) {
                        if (tcpReceiveListener != null) {
                            tcpReceiveListener.onRealData(str);
                        }
                    }
                }else{
                    if (recount >10){
                        socket.close();
                        socket = null;
                        reconnect();
                        recount = 0;
                    }
                    recount++;
                }
                Log.i(TAG, "接收成功");
            } catch (IOException e) {
                Log.i(TAG, "接收失败");
            }
        }
        if (tcpReceiveListener!= null){
            tcpReceiveListener.onServerDisconnected(new IOException("服务断开"));
        }
    }

    /**
     * 发送数据
     *
     * @param data 数据
     */
    public void send(final byte[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (socket != null) {
                    try {
                        outputStream.write(data);
                        outputStream.flush();
                        Log.i(TAG, "发送成功");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(TAG, "发送失败");
                    }
                } else {
                    reconnect();
                }
            }
        }).start();

    }

    public void reconnect(){
        if (ipAddress != null && !ipAddress.isEmpty() && port >0){
            connect(ipAddress,port);
        }else {
            if (tcpReceiveListener != null){
                tcpReceiveListener.onServerDisconnected(new IOException("未知错误"));
            }
        }
    }

    //    TCP回调
    private TcpReceiveListener tcpReceiveListener;

    public interface TcpReceiveListener {
        void onRealData(String receicedMessage);

        void onServerDisconnected(IOException e);

        void onServerConnected();

        void onServerReconnectError();
    }

    public void setTcpReceiveListener(TcpReceiveListener tcpReceiveListener) {
        this.tcpReceiveListener = tcpReceiveListener;
    }

    /**
     * 移除回调
     */
    private void removeCallback() {
        tcpReceiveListener = null;
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (isConnected()) {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                socket.close();
                if (socket.isClosed()) {
                    if (tcpReceiveListener != null) {
                        tcpReceiveListener.onServerDisconnected(new IOException("断开连接"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }


    /**
     * 心跳包
     * */
    public void keepAlive() {
        if (lastKeepAliveOkTime != null) {
            Log.i(TAG, "上次心跳成功时间" + TimeUtils.date2String(lastKeepAliveOkTime));
            Date now = Calendar.getInstance().getTime();
            long between = (now.getTime() - lastKeepAliveOkTime.getTime());
            if (between > 60 * 1000) {
                Log.i(TAG, "心跳异常超过1分钟，重新连接：");
                lastKeepAliveOkTime = null;
                socket = null;
                mHandler.removeCallbacks(heartBeatRunnable);
            }
        } else {
            lastKeepAliveOkTime = Calendar.getInstance().getTime();
        }

        if (!checkIsAlive()) {
            Log.i(TAG, "连接已断开，重新连接");
            reconnect();
        }
    }
    
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            keepAlive();
            mHandler.postDelayed(this,HEART_BEAT_RATE);
        }
    };

    private  boolean checkIsAlive() {
        if (socket == null) {
            return false;
        } else {
            try {
                socket.sendUrgentData(0xFF);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public void sendMessage(String msg){
        if (!checkIsAlive())
            return;
        Log.i(TAG,"准备发送消息:" +msg);
        if (socket != null && socket.isConnected()){
            if (!socket.isOutputShutdown()){
                try {
                    PrintWriter outSteam = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())),true);
                    outSteam.print(msg + (char)13 + (char)10);
                    outSteam.flush();
                    lastKeepAliveOkTime = Calendar.getInstance().getTime();
                    Log.i(TAG,"发送成功");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
