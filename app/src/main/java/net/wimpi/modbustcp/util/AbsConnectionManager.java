package net.wimpi.modbustcp.util;




import net.wimpi.modbustcp.bean.ConnectionInfo;
import net.wimpi.modbustcp.interfaces.IConnectionManager;
import net.wimpi.modbustcp.interfaces.IConnectionSwitchListener;
import net.wimpi.modbustcp.interfaces.ISocketActionListener;

import java.io.Serializable;


/**
 * Created by xuhao on 2017/5/17.
 */

public abstract class AbsConnectionManager implements IConnectionManager {
    /**
     * 连接信息
     */
    protected ConnectionInfo mRemoteConnectionInfo;
    /**
     * 本地绑定信息
     */
    protected ConnectionInfo mLocalConnectionInfo;
    /**
     * 连接信息switch监听器
     */
    private IConnectionSwitchListener mConnectionSwitchListener;

    public AbsConnectionManager(ConnectionInfo info) {
        this(info, null);
    }

    public AbsConnectionManager(ConnectionInfo remoteInfo, ConnectionInfo localInfo) {
        mRemoteConnectionInfo = remoteInfo;
        mLocalConnectionInfo = localInfo;
    }
//
//    public IConnectionManager registerReceiver(final ISocketActionListener socketResponseHandler) {
//        mActionDispatcher.registerReceiver(socketResponseHandler);
//        return this;
//    }
//
//    public IConnectionManager unRegisterReceiver(ISocketActionListener socketResponseHandler) {
//        mActionDispatcher.unRegisterReceiver(socketResponseHandler);
//        return this;
//    }
//
//    protected void sendBroadcast(String action, Serializable serializable) {
//        mActionDispatcher.sendBroadcast(action, serializable);
//    }
//
//    protected void sendBroadcast(String action) {
//        mActionDispatcher.sendBroadcast(action);
//    }

    @Override
    public ConnectionInfo getRemoteConnectionInfo() {
        if (mRemoteConnectionInfo != null) {
            return mRemoteConnectionInfo.clone();
        }
        return null;
    }

    @Override
    public ConnectionInfo getLocalConnectionInfo() {
        if (mLocalConnectionInfo != null) {
            return mLocalConnectionInfo;
        }
        return null;
    }

//    @Override
//    public synchronized void switchConnectionInfo(ConnectionInfo info) {
//        if (info != null) {
//            ConnectionInfo tempOldInfo = mRemoteConnectionInfo;
//            mRemoteConnectionInfo = info.clone();
//            if (mActionDispatcher != null) {
//                mActionDispatcher.setConnectionInfo(mRemoteConnectionInfo);
//            }
//            if (mConnectionSwitchListener != null) {
//                mConnectionSwitchListener.onSwitchConnectionInfo(this, tempOldInfo, mRemoteConnectionInfo);
//            }
//        }
//    }

    protected void setOnConnectionSwitchListener(IConnectionSwitchListener listener) {
        mConnectionSwitchListener = listener;
    }

}
