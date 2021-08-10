package net.wimpi.modbustcp.util;

import net.wimpi.modbustcp.bean.ConnectionInfo;

import java.util.Iterator;

public class ReconnectManager extends AbsReconnectionManager{
    /**
     * 最大连接失败次数，不包括断开异常
     * */
    private static int MAX_CONNECTION_FAILED_TIMES = 10;
    /**
     * 连接失败次数,不包括断开异常
     */
    private int mConnectionFailedTimes = 0;

    private volatile ReconnectThread mReconnectThread;

    public ReconnectManager(){
        mReconnectThread = new ReconnectThread();
    }


    @Override
    public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
        if (isNeedReconnect(e)){
            reconnectDelay();
        }else {
            resetThread();
        }
    }

    @Override
    public void onSocketConnectionSuccess(ConnectionInfo info, String action) {

    }

    @Override
    public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {

    }

    /**
     * 是否需要重连
     *
     * @param e
     * @return
     */
    private boolean isNeedReconnect(Exception e) {
        synchronized (mIgnoreDisconnectExceptionList) {
            if (e != null && !(e instanceof RuntimeException)) {//break with exception
                Iterator<Class<? extends Exception>> it = mIgnoreDisconnectExceptionList.iterator();
                while (it.hasNext()) {
                    Class<? extends Exception> classException = it.next();
                    if (classException.isAssignableFrom(e.getClass())) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * 重置重连线程,关闭线程
     */
    private synchronized void resetThread() {
        if (mReconnectThread != null) {
            mReconnectThread.interrupt();
        }
    }

    /**
     * 开始延迟重连
     */
    private void reconnectDelay() {
        synchronized (mReconnectThread) {
            if (!mReconnectThread.isAlive()) {
                mReconnectThread.start();
            }
        }
    }

    private class ReconnectThread extends Thread{
        /**
         * 延时连接时间
         * */
        private long mReconnectTimeDelay = 10 * 1000;
        @Override
        public void run() {
            if (mDetach) {
                this.interrupt();
                return;
            }
            //延迟执行
            ThreadUtil.sleep(mReconnectTimeDelay);

            if (mDetach) {
                this.interrupt();
                return;
            }

            if (mConnectionManager.isConnect()) {
                this.interrupt();
                return;
            }


            ConnectionInfo info = mConnectionManager.getRemoteConnectionInfo();
            synchronized (mConnectionManager) {
                if (!mConnectionManager.isConnect()) {
                    mConnectionManager.connect();
                } else {
                    this.interrupt();
                }
            }
        }
    }

}
