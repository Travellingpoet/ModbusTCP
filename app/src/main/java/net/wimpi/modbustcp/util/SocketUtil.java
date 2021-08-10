package net.wimpi.modbustcp.util;

import net.wimpi.modbustcp.bean.ConnectionInfo;
import net.wimpi.modbustcp.interfaces.IConnectionManager;
import net.wimpi.modbustcp.interfaces.IRegister;
import net.wimpi.modbustcp.interfaces.IServerActionListener;
import net.wimpi.modbustcp.interfaces.IServerManager;

public class SocketUtil {
    private static ManagerHolder holder = ManagerHolder.getInstance();

    /**
     * 获得一个SocketServer服务器.
     *
     * @param serverPort
     * @return
     */
    public static IRegister<IServerActionListener, IServerManager> server(int serverPort) {
        return (IRegister<IServerActionListener, IServerManager>) holder.getServer(serverPort);
    }
    /**
     * 开启一个socket通讯通道,参配为默认参配
     *
     * @param connectInfo 连接信息{@link ConnectionInfo}
     * @return 该参数的连接管理器 {@link IConnectionManager} 连接参数仅作为配置该通道的参配,不影响全局参配
     */
    public static IConnectionManager open(ConnectionInfo connectInfo) {
        return holder.getConnection(connectInfo);
    }
}
