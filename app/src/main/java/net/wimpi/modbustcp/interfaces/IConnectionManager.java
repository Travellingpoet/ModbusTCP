package net.wimpi.modbustcp.interfaces;

import net.wimpi.modbustcp.bean.ConnectionInfo;
import net.wimpi.modbustcp.util.AbsReconnectionManager;
import net.wimpi.modbustcp.util.PulseManager;

public interface IConnectionManager extends ISender<IConnectionManager>,
        IDisConnectable,
        IConnectable,
        IRegister<ISocketActionListener,IConnectionManager>,
        IStateSender{
    /**
     * 是否链接
     * */
    boolean isConnect();
    /**
     * 是否正在断开连接
     * */
    boolean isDisConnecting();

    /**
     * 是否socket保存这次链接
     * */
    void setIsConnectionHolder(boolean isHold);

    /**
     * 获得连接信息
     * */
    ConnectionInfo getRemoteConnectionInfo();

    /**
     * 获取到心跳管理器,用来配置心跳参数和心跳行为.
     *
     * @return 心跳管理器
     */
    PulseManager getPulseManager();

    /**
     * 获得本地连接信息
     * */
    ConnectionInfo getLocalConnectionInfo();

    /**
     * 设置本地端口信息
     * */
    void setLocalConnectionInfo(ConnectionInfo localConnectionInfo);

    /**
     * 将当前的连接管理器鲜红的连接信息进行切换
     * */
    void switchConnection(ConnectionInfo info);

    /**
     * 获得重连管理器，用来配置重连管理器
     * */
    AbsReconnectionManager getReconnectionManager();
}
