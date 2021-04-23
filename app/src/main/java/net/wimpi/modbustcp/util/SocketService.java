package net.wimpi.modbustcp.util;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import net.wimpi.modbustcp.IBackService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SocketService extends Service {
    private long sendTime = 0L;
    private static final String TAG = "SocketService";
    //    Socket
    private Socket socket;
    /** 主机IP地址  */
    private static final String HOST = "10.14.2.81";
    /** 端口号  */
    public static final int PORT = 60000;
    //    线程
    private Thread thread;
    //    Socket输出流
    private OutputStream outputStream;
    //    Socket输入流
    private InputStream inputStream;

    private ReadThread mReadThread;
    /** 消息广播  */
    public static final String MESSAGE_ACTION = "org.van.message_ACTION";

    /** 心跳广播  */
    public static final String HEART_BEAT_ACTION = "org.van.heart_beat_ACTION";


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
    private static final long HEART_BEAT_RATE = 10 * 1000;
    private static SocketService instance;

    private WeakReference<Socket> mSocket;

    private ExecutorService mThreadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            unit,
            workQueue,
            new ThreadPoolExecutor.DiscardPolicy());

    private int recount = 0;



    private IBackService.Stub iBackService = new IBackService.Stub(){
        @Override
        public boolean sendMessage(String message) throws RemoteException {
            return sendMsg(message);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) iBackService;
    }

    @Override
    public void onCreate() {
        super.onCreate();

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
                    connect();
                }
            }
        }).start();

    }
    private void initSocket() throws UnknownHostException{
        Socket socket = null;
        try {
            socket = new Socket(HOST, PORT);
            mSocket = new WeakReference<Socket>(socket);
            mReadThread = new ReadThread(socket);
            mThreadPoolExecutor.execute(mReadThread);
            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);// 初始化成功后，就准备发送心跳包
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    /**
//     * 通过IP地址(域名)和端口进行连接
//     *
//     * @param ipAddress IP地址(域名)
//     * @param port      端口
//     */
    public void connect() {
//        this.ipAddress = ipAddress;
//        this.port = port;
        mThreadPoolExecutor.execute(new InitSocketThread());
    }

    // 发送心跳包
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                boolean isSuccess = sendMsg("");// 就发送一个\r\n过去, 如果发送失败，就重新初始化一个socket
                if (!isSuccess) {
                    mHandler.removeCallbacks(heartBeatRunnable);
                    mReadThread.release();
                    disconnect(mSocket);
                    connect();
                }
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };


    class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                initSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ReadThread extends Thread{
        private WeakReference<Socket> mWeakSocket;
        private boolean isStart = true;

        public ReadThread(Socket socket){
            mWeakSocket = new WeakReference<Socket>(socket);
        }

        public void release(){
            isStart = false;
            disconnect(mWeakSocket);
        }

        @Override
        public void run() {
            super.run();
            receive();
        }
    }

    public boolean isConnected() {
        return mSocket.get() != null && mSocket.get().isConnected();
    }

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
                if (length >= 0) {
                    byte[] bs = new byte[length];
                    System.arraycopy(bt, 0, bs, 0, length);

                    String str = new String(bs, "UTF-8");
                    if (str.equals("ok")) {
                        Intent intent = new Intent(HEART_BEAT_ACTION);
                        sendBroadcast(intent);
                    }else {
                        // 其他消息回复
                        Intent intent = new Intent(MESSAGE_ACTION);
                        intent.putExtra("message", str);
                        sendBroadcast(intent);
                    }
                }else{
                    if (recount >10){
                        socket.close();
                        socket = null;
                        connect();
                        recount = 0;
                    }
                    recount++;
                }
                Log.i(TAG, "接收成功");
            } catch (IOException e) {
                Log.i(TAG, "接收失败");
            }
        }
    }

    /**
     * 断开连接
     */
    public void disconnect(WeakReference<Socket> mSocket) {
        try {
            if (null != mSocket) {
                Socket sk = mSocket.get();
                if (!sk.isClosed()) {
                    sk.close();
                    // 断开连接回复
                    Intent intent = new Intent(MESSAGE_ACTION);
                    intent.putExtra("message", "断开链接");
                    sendBroadcast(intent);
                }
                sk = null;
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void reconnect(){
//        if (ipAddress != null && !ipAddress.isEmpty() && port >0){
//            connect(ipAddress,port);
//        }else {
//            // 其他消息回复
//            Intent intent = new Intent(MESSAGE_ACTION);
//            intent.putExtra("message", "未知错误");
//            sendBroadcast(intent);
//        }
//    }

    public boolean sendMsg(String msg) {
        if (null == mSocket || null == mSocket.get()) {
            return false;
        }
        Socket soc = mSocket.get();
        try {
            if (!soc.isClosed() && !soc.isOutputShutdown()) {
                OutputStream os = soc.getOutputStream();
                String message = msg + "\r\n";
                os.write(message.getBytes());
                os.flush();
                sendTime = System.currentTimeMillis();// 每次发送成功数据，就改一下最后成功发送的时间，节省心跳间隔时间
                Log.i(TAG, "发送成功的时间：" + sendTime);
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
