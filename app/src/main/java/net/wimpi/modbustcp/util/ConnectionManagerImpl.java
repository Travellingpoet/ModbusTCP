package net.wimpi.modbustcp.util;

import android.util.Log;

import net.wimpi.modbustcp.bean.ConnectionInfo;
import net.wimpi.modbustcp.interfaces.IAction;
import net.wimpi.modbustcp.interfaces.IConnectionManager;
import net.wimpi.modbustcp.interfaces.ISendable;
import net.wimpi.modbustcp.interfaces.ISocketActionListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ConnectionManagerImpl extends AbsConnectionManager implements IConnectionManager {
    /**
     * 套接字
     */
    private volatile Socket mSocket;
    /**
     * 连接线程
     */
    private Thread mConnectThread;
    /**
     * 连接信息
     */
    protected ConnectionInfo mRemoteConnectionInfo;
    /**
     * 脉搏管理器
     */
    private volatile PulseManager mPulseManager;
    /**
     * 本地绑定信息
     */
    protected ConnectionInfo mLocalConnectionInfo;
    /**
     * 重新连接管理器
     */
    private volatile AbsReconnectionManager mReconnectionManager;
    /**
     * 能否连接
     */
    private volatile boolean isConnectionPermitted = true;
    /**
     * 是否正在断开
     */
    private volatile boolean isDisconnecting = false;

    //    Socket输出流
    private OutputStream outputStream;
    //    Socket输入流
    private InputStream inputStream;

    protected ConnectionManagerImpl(ConnectionInfo info) {
        this(info, null);
    }

    public ConnectionManagerImpl(ConnectionInfo remoteInfo, ConnectionInfo localInfo) {
        super(remoteInfo,localInfo);
        mRemoteConnectionInfo = remoteInfo;
        mLocalConnectionInfo = localInfo;

        String ip = "";
        String port = "";
        if (remoteInfo != null) {
            ip = remoteInfo.getIp();
            port = remoteInfo.getPort() + "";
        }
    }

    @Override
    public boolean isConnect() {
        return false;
    }

    @Override
    public boolean isDisConnecting() {
        return false;
    }

    @Override
    public void setIsConnectionHolder(boolean isHold) {

    }

    @Override
    public ConnectionInfo getRemoteConnectionInfo() {
        if (mRemoteConnectionInfo != null) {
            return mRemoteConnectionInfo.clone();
        }
        return null;
    }

    @Override
    public PulseManager getPulseManager() {
        return null;
    }

    @Override
    public ConnectionInfo getLocalConnectionInfo() {
        return null;
    }

    @Override
    public void setLocalConnectionInfo(ConnectionInfo localConnectionInfo) {

    }

    @Override
    public void switchConnection(ConnectionInfo info) {

    }

    @Override
    public AbsReconnectionManager getReconnectionManager() {
        return null;
    }

    @Override
    public void disconnect(Exception e) {
        synchronized (this) {
            if (isDisconnecting) {
                return;
            }
            isDisconnecting = true;

            if (mPulseManager != null) {
                mPulseManager.dead();
                mPulseManager = null;
            }
        }

        if (e instanceof RuntimeException) {
            if (mReconnectionManager != null) {
                mReconnectionManager.detach();
            }
        }

        String info = mRemoteConnectionInfo.getIp() + ":" + mRemoteConnectionInfo.getPort();
        DisconnectThread thread = new DisconnectThread(e, "Disconnect Thread for " + info);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void disconnect() {

    }

    @Override
    public IConnectionManager send(ISendable sendable) {
        return null;
    }

    @Override
    public void connect() {
        if (!isConnectionPermitted) {
            return;
        }
        isConnectionPermitted = false;
        if (isConnect()) {
            return;
        }
        isDisconnecting = false;
        if (mRemoteConnectionInfo == null) {
            isConnectionPermitted = true;
            throw new RuntimeException("连接参数为空,检查连接参数");
        }

        if (mReconnectionManager != null) {
            mReconnectionManager.detach();
        }
        mReconnectionManager = new ReconnectManager();
        if (mReconnectionManager != null) {
            mReconnectionManager.attach(this);
        }

        String info = mRemoteConnectionInfo.getIp() + ":" + mRemoteConnectionInfo.getPort();
        mConnectThread = new ConnectionThread(" Connect thread for " + info);
        mConnectThread.setDaemon(true);
        mConnectThread.start();
    }

    @Override
    public IConnectionManager registerReceiver(ISocketActionListener socketActionListener) {
        return null;
    }

    @Override
    public IConnectionManager unRegisterReceiver(ISocketActionListener socketActionListener) {
        return null;
    }

    @Override
    public void sendBroadcast(String action, Serializable serializable) {

    }

    @Override
    public void sendBroadcast(String action) {
        sendBroadcast(action,null);
    }

    private class ConnectionThread extends Thread{

        public ConnectionThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                if (mSocket == null) {
                    mSocket = new Socket();
                }
                if (mLocalConnectionInfo != null){
                    mSocket.bind(new InetSocketAddress(mLocalConnectionInfo.getIp(),mLocalConnectionInfo.getPort()));
                }
                mSocket.connect(new InetSocketAddress(mRemoteConnectionInfo.getIp(),mRemoteConnectionInfo.getPort()),3*1000);
                //关闭Nagle算法,无论TCP数据报大小,立即发送
                mSocket.setTcpNoDelay(true);
                if (isConnected()) {
                    outputStream = mSocket.getOutputStream();
                    inputStream = mSocket.getInputStream();
//                    receive();
                }
//                mPulseManager = new PulseManager(this);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private class DisconnectThread extends Thread {
        private Exception mException;

        public DisconnectThread(Exception exception, String name) {
            super(name);
            mException = exception;
        }

        @Override
        public void run() {
            try {
                if (mConnectThread != null && mConnectThread.isAlive()) {
                    mConnectThread.interrupt();
                    try {
                        mConnectThread.join();
                    } catch (InterruptedException e) {
                    }
                    mConnectThread = null;
                }

                if (mSocket != null) {
                    try {
                        mSocket.close();
                    } catch (IOException e) {
                    }
                }

            } finally {
                isDisconnecting = false;
                isConnectionPermitted = true;
                if (!(mException instanceof RuntimeException) && mSocket != null) {
                    mException = mException instanceof RuntimeException ? null : mException;
                    sendBroadcast(IAction.ACTION_DISCONNECTION, mException);
                }
                mSocket = null;
            }
        }
    }

    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected();
    }

    /**
     * 接收数据
     */
//    public void receive() {
//        while (isConnected()) {
//            try {
//                /**得到的是16进制数，需要进行解析*/
//                byte[] bt = new byte[1024];
////                获取接收到的字节和字节数
//                int length = inputStream.read(bt);
////                获取正确的字节
//                if (length >= 0) {
//                    byte[] bs = new byte[length];
//                    System.arraycopy(bt, 0, bs, 0, length);
//
//                    String str = new String(bs, "UTF-8");
//                    if (str != null) {
//                        if (tcpReceiveListener != null) {
//                            tcpReceiveListener.onRealData(str);
//                        }
//                    }
//                }else{
//                    if (recount >10){
//                        mSocket.close();
//                        mSocket = null;
//                        reconnect();
//                        recount = 0;
//                    }
//                    recount++;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if (tcpReceiveListener!= null){
//            tcpReceiveListener.onServerDisconnected(new IOException("服务断开"));
//        }
//    }

}
