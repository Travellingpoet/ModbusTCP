package net.wimpi.modbustcp.util;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Time:2021/4/9
 * <p>
 * Author:VanDine
 * <p>
 * Description:
 */
public class SerialController {
    //核心线程数
    private final int corePoolSize = 3;
    //最大线程数
    private final int maximumPoolSize = 6;
    //超过 corePoolSize 线程数量的线程最大空闲时间
    private long keepAliveTime = 2;
    //以秒为时间单位
    TimeUnit unit = TimeUnit.SECONDS;
    //创建工作队列，用于存放提交的等待执行任务
    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(2);

    private ExecutorService mThreadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            unit,
            workQueue,
            new ThreadPoolExecutor.AbortPolicy());
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean isOpened = false;
    private OnSerialListener onSerialListener;

    /**
    * 获取所有串口的路径
    * */
    public List<String> getAllSerialPortPath(){
        SerialPortFinder mSerialPortFinder = new SerialPortFinder();
        String[] deviceArr = mSerialPortFinder.getAllDevicesPath();
        return new ArrayList<>(Arrays.asList(deviceArr));
    }

    /**
     * 打开串口
     *
     * @param serialPath 串口地址
     * @param baudRate   波特率
     * @param flags      标志位
     */
    public void openSerialPort(String serialPath,int baudRate,int flags){
        try {
            SerialPort serialPort = new SerialPort(new File(serialPath),baudRate,flags);
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            isOpened = true;
            if (onSerialListener != null){
                onSerialListener.onSerialOpenSuccess();
            }
            mThreadPoolExecutor.execute(new ReceiveDataThread());
        } catch (IOException e) {
            if (onSerialListener != null){
                onSerialListener.onSerialOpenException(e);
            }
        }
    }

    /**
     * 关闭串口
     * */
    public void closeSerialPort(){
        try {
            if (inputStream == null){
                inputStream.close();
            }
            if (outputStream == null){
                outputStream.close();
            }
            isOpened = false;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 发送串口数据
     * @param bytes 发送数据
     *
     * */
    public void sendSerialPort(byte[] bytes){
        if (!isOpened){
            return;
        }
        try {
            if (outputStream != null){
                outputStream.write(bytes);
                outputStream.flush();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean isOpened(){
       return isOpened;
    }

    private class ReceiveDataThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (isOpened){
                if (inputStream != null){
                    byte[] readData = new byte[1024];
                    try {
                        int size = inputStream.read(readData);
                        if (size > 0){
                            if (onSerialListener != null){
                                onSerialListener.onReceivedData(readData,size);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public void setOnSerialListener(OnSerialListener onSerialListener){
        this.onSerialListener = onSerialListener;
    }

    /**
     * 串口监听
     */
    public interface OnSerialListener {

        /**
         * 串口数据返回
         */
        void onReceivedData(byte[] data, int size);

        /**
         * 串口打开成功
         */
        void onSerialOpenSuccess();

        /**
         * 串口打开异常
         */
        void onSerialOpenException(Exception e);
    }
}
