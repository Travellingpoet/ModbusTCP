package net.wimpi.modbustcp.util;

import net.wimpi.modbustcp.bean.ConnectionInfo;
import net.wimpi.modbustcp.bean.OriginalData;
import net.wimpi.modbustcp.interfaces.IConnectionManager;
import net.wimpi.modbustcp.interfaces.IPulseSendable;
import net.wimpi.modbustcp.interfaces.ISendable;
import net.wimpi.modbustcp.interfaces.ISocketActionListener;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbsReconnectionManager implements ISocketActionListener {
    /**
     * 连接管理器
     */
    protected volatile IConnectionManager mConnectionManager;
    /**
     * 心跳管理器
     */
    protected PulseManager mPulseManager;
    /**
     * 是否销毁
     */
    protected volatile boolean mDetach;
    /**
     * 需要忽略的断开连接集合,当Exception在此集合中,忽略该类型的断开异常,不会自动重连
     */
    protected volatile Set<Class<? extends Exception>> mIgnoreDisconnectExceptionList = new LinkedHashSet<>();

    /**
     * 关联到某一个连接管理器
     *
     * @param manager 当前连接管理器
     */
    public synchronized void attach(IConnectionManager manager) {
        if (mDetach) {
            detach();
        }
        mDetach = false;
        mConnectionManager = manager;
        mPulseManager = manager.getPulseManager();
        mConnectionManager.registerReceiver(this);
    }
    /**
     * 解除连接当前的连接管理器
     */
    public synchronized void detach() {
        mDetach = true;
        if (mConnectionManager != null) {
            mConnectionManager.unRegisterReceiver(this);
        }
    }

    public AbsReconnectionManager(){

    }

    @Override
    public void onSocketIOThreadStart(String action) {

    }

    @Override
    public void onSocketIOThreadShutdown(String action, Exception e) {

    }

    @Override
    public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {

    }

    @Override
    public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {

    }

    @Override
    public void onPulseSend(ConnectionInfo info, IPulseSendable data) {

    }
}
