package net.wimpi.modbustcp.util;

import net.wimpi.modbus.util.AtomicCounter;
import net.wimpi.modbustcp.interfaces.IConnectionManager;
import net.wimpi.modbustcp.interfaces.IPulse;
import net.wimpi.modbustcp.interfaces.IPulseSendable;

import java.util.concurrent.atomic.AtomicInteger;

public class PulseManager implements IPulse {
    /**
     * 数据包发送器
     */
    private volatile IConnectionManager iConnectionManager;

    /**
     * 心跳数据包
     */
    private IPulseSendable pulseSendable;

    /**
     * 当前频率
     */
    private volatile long mCurrentFrequency;

    /**
     * 是否死掉
     */
    private volatile boolean isDead = false;

    /**
     * 允许遗漏的次数
     */
    private volatile AtomicInteger mLoseTimes = new AtomicInteger(-1);

    private PulseThread mPulseThread = new PulseThread();

    private int pulseFeedLoseTimes = -5;

    private int pulseFrequency = 5 * 1000;

    PulseManager(IConnectionManager manager) {
        iConnectionManager = manager;
    }

    public synchronized IPulse setPulseSendable(IPulseSendable sendable){
        if (sendable != null){
            pulseSendable = sendable;
        }
        return this;
    }

    public IPulseSendable getPulseSendable(){ return  pulseSendable;}


    @Override
    public void pulse() {
        privateDead();
        updateFrequency();
        if (!mPulseThread.isAlive()) {
            mPulseThread.start();
        }
    }

    @Override
    public void trigger() {
        if (isDead) {
            return;
        }
        if (iConnectionManager != null && pulseSendable != null) {
            iConnectionManager.send(pulseSendable);
        }
    }

    @Override
    public void dead() {
        mLoseTimes.set(0);
        isDead = true;
        privateDead();
    }

    private synchronized void updateFrequency() {
        mCurrentFrequency = pulseFrequency;
        mCurrentFrequency = mCurrentFrequency < 1000 ? 1000 : mCurrentFrequency;//间隔最小为一秒

    }

    @Override
    public void feed() {
        mLoseTimes.set(-1);
    }

    private class PulseThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (isDead){
                this.interrupt();
                return;
            }
            if (iConnectionManager != null && pulseSendable != null) {
                if (mLoseTimes.incrementAndGet() >= pulseFeedLoseTimes) {
                    iConnectionManager.disconnect(new Exception("you need feed dog on time,otherwise he will die"));
                } else {
                    iConnectionManager.send(pulseSendable);
                }
            }
            //not safety sleep.
            try {
                Thread.sleep(mCurrentFrequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    private void privateDead() {
        if (mPulseThread != null) {
            mPulseThread.interrupt();
        }
    }

    public int getLoseTimes() {
        return mLoseTimes.get();
    }

}
