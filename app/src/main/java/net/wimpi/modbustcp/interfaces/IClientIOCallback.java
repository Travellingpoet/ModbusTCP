package net.wimpi.modbustcp.interfaces;


import net.wimpi.modbustcp.bean.OriginalData;

public interface IClientIOCallback {

    void onClientRead(OriginalData originalData, IClient client);

    void onClientWrite(ISendable sendable, IClient client);

}
