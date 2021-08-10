package net.wimpi.modbustcp.interfaces;

import net.wimpi.modbustcp.bean.OriginalData;

import java.io.Serializable;

public interface IClient extends IDisConnectable, ISender<IClient>, Serializable {

    String getHostIp();

    String getHostName();

    String getUniqueTag();

    void setReaderProtocol(IReaderProtocol protocol);

    void addIOCallback(IClientIOCallback clientIOCallback);

    void removeIOCallback(IClientIOCallback clientIOCallback);

    void removeAllIOCallback();

    void onClientRead(OriginalData originalData);

    void onClientWrite(ISendable sendable);

}
