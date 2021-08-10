package net.wimpi.modbustcp.util;

import net.wimpi.modbustcp.bean.OriginalData;
import net.wimpi.modbustcp.interfaces.IAction;
import net.wimpi.modbustcp.interfaces.IClient;
import net.wimpi.modbustcp.interfaces.IClientIOCallback;
import net.wimpi.modbustcp.interfaces.IReaderProtocol;
import net.wimpi.modbustcp.interfaces.ISendable;
import net.wimpi.modbustcp.interfaces.IStateSender;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientImpl implements IClient {
    private volatile boolean isDead;
    private IStateSender mServerStateSender;
    private volatile boolean isReadThreadStarted;
    protected Socket mSocket;
    protected InetAddress mInetAddress;

    private volatile List<IClientIOCallback> mCallbackList = new ArrayList<>();
    public ClientImpl(Socket socket) {
        this.mSocket = socket;
        this.mInetAddress = socket.getInetAddress();
    }

    @Override
    public String getHostIp() {
        return mInetAddress.getHostAddress();
    }

    @Override
    public String getHostName() {
        return mInetAddress.getHostName();
    }

    @Override
    public String getUniqueTag() {
        return null;
    }

    @Override
    public void setReaderProtocol(IReaderProtocol protocol) {

    }

    @Override
    public void addIOCallback(IClientIOCallback clientIOCallback) {
        if (isDead) {
            return;
        }
        synchronized (mCallbackList) {
            mCallbackList.add(clientIOCallback);
        }
    }

    @Override
    public void removeIOCallback(IClientIOCallback clientIOCallback) {
        synchronized (mCallbackList) {
            mCallbackList.remove(clientIOCallback);
        }
    }

    @Override
    public void removeAllIOCallback() {
        synchronized (mCallbackList) {
            mCallbackList.clear();
        }
    }

    @Override
    public void disconnect(Exception e) {
        try {
            synchronized (mSocket) {
                mSocket.close();
            }
        } catch (IOException e1) {
        }
        removeAllIOCallback();
        isReadThreadStarted = false;
    }

    @Override
    public void disconnect() {
        try {
            synchronized (mSocket) {
                mSocket.close();
            }
        } catch (IOException e1) {
        }
        removeAllIOCallback();
        isReadThreadStarted = false;
    }

    @Override
    public IClient send(ISendable sendable) {
        return null;
    }

    @Override
    public void onClientRead(OriginalData originalData) {
        List<IClientIOCallback> list = new ArrayList<>();
        list.addAll(mCallbackList);

        for (IClientIOCallback clientIOCallback : list) {
            try {
                clientIOCallback.onClientRead(originalData, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClientWrite(ISendable sendable) {
        List<IClientIOCallback> list = new ArrayList<>();
        list.addAll(mCallbackList);

        for (IClientIOCallback clientIOCallback : list) {
            try {
                clientIOCallback.onClientWrite(sendable, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
