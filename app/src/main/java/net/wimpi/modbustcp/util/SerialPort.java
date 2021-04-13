package net.wimpi.modbustcp.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Time:2021/4/9
 * <p>
 * Author:VanDine
 * <p>
 * Description:
 */
public class SerialPort {
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public SerialPort(File device, int baudRate, int flags)throws SecurityException, IOException {
        mFd = open(device.getAbsolutePath(),baudRate,flags);
        if (mFd == null){
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    public InputStream getInputStream(){
        return mFileInputStream;
    }
    public OutputStream getOutputStream(){
        return mFileOutputStream;
    }

    private native static FileDescriptor open(String path,int baudRate,int flags);

    public native void close();

    static {
        System.loadLibrary("serial_port");
    }

}
