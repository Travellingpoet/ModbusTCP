package net.wimpi.modbustcp.interfaces;


import net.wimpi.modbustcp.bean.ConnectionInfo;

/**
 * Created by xuhao on 2017/6/30.
 */

public interface IConnectionSwitchListener {
    void onSwitchConnectionInfo(IConnectionManager manager, ConnectionInfo oldInfo, ConnectionInfo newInfo);
}
