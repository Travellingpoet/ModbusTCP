package net.wimpi.modbustcp.util;

import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

/**
 * Time:2021/4/8
 * <p>
 * Author:VanDine
 * <p>
 * Description:
 */
public class SerialPortFinder {
    private static final String TAG = "SerialPort";

    public class Driver{
        private final String mDriverName;
        private final String mDeviceRoot;
        Vector<File> mDevice = null;

        public Driver(String name, String root) {
            this.mDriverName = name;
            this.mDeviceRoot = root;
        }

        Vector<File> getDevice(){
            if (mDevice == null){
                mDevice = new Vector<>();
                File dev = new File("/dev");
                File[] files = dev.listFiles();
                if (files == null){
                    return mDevice;
                }
                int i;
                for (i = 0; i<files.length;i++){
                    if (files[i].getAbsolutePath().startsWith(mDeviceRoot)){
                        Log.d(TAG,"Found new device: " + files[i]);
                        mDevice.add(files[i]);
                    }
                }
            }
            return mDevice;
        }

        public String getName(){
            return mDriverName;
        }
    }
    private Vector<Driver> mDrivers = null;

    Vector<Driver> getDrivers() throws IOException{
        if (mDrivers == null){
            mDrivers = new Vector<Driver>();
            LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
            String l;
            while ((l = r.readLine()) != null){
                String driverName = l.substring(0,0x15).trim();
                String[] w = l.split(" +");
                if ((w.length>5) && (w[w.length - 1].equals("serial"))){
                    Log.d(TAG,"Found new driver: " + driverName + " on " + w[w.length-4]);
                    mDrivers.add(new Driver(driverName,w[w.length-4]));
                }
            }
            r.close();
        }
        return mDrivers;
    }

    public String[] getAllDevices(){
        Vector<String> devices = new Vector<String>();
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()){
                Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevice().iterator();
                while (itdev.hasNext()){
                    String device = itdev.next().getName();
                    String value = String.format("%s (%s)",device,driver.getName());
                    devices.add(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }

    public String[] getAllDevicesPath(){
        Vector<String> devices = new Vector<String>();
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()){
                Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevice().iterator();
                while (itdev.hasNext()){
                    String device = itdev.next().getAbsolutePath();
                    devices.add(device);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }
}
