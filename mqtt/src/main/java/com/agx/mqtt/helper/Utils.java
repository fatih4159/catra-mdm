package com.agx.mqtt.helper;


import static com.agx.mqtt.ui.MainService.getService;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
//import org.apache.http.conn.util.InetAddressUtils;

public class Utils {
    private static final String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PRECISE_PHONE_STATE

    };
    private static ArrayList<String> permissonsNotGranted = new ArrayList<String>();


    private static final String TAG = "Utils";
    private static String XUUID="";

    /**
     * Convert byte array to hex string
     * @param bytes toConvert
     * @return hexValue
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for(int idx=0; idx < bytes.length; idx++) {
            int intVal = bytes[idx] & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Get utf8 byte array.
     * @param str which to be converted
     * @return  array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try { return str.getBytes("UTF-8"); } catch (Exception ex) { return null; }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     * @param filename which to be converted to string
     * @return String value of File
     * @throws IOException if error occurs
     */
    public static String loadFileAsString(String filename) throws IOException {
        final int BUFLEN=1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8=false;
            int read,count=0;
            while((read=is.read(bytes)) != -1) {
                if (count==0 && bytes[0]==(byte)0xEF && bytes[1]==(byte)0xBB && bytes[2]==(byte)0xBF ) {
                    isUTF8=true;
                    baos.write(bytes, 3, read-3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count+=read;
            }
            return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
        } finally {
            try{ is.close(); } catch(Exception ignored){}
        }
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) buf.append(String.format("%02X:",aMac));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception e) {e.printStackTrace(); } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }

    public static String getUniqueDeviceID() {
        if (XUUID.isEmpty()){
            XUUID= findID();
            return XUUID;
        }else{
            return XUUID;
        }
    }

    @NonNull
    private static String findID() {
        String serialNumber = "";
        Method get = null;
        Class<?> c = null;

        try {

            c = Class.forName("android.os.SystemProperties");
            get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
        }catch (Exception e){e.printStackTrace();}

        if (serialNumber.equals("") ||serialNumber.contains("unknown")) {
            try {
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
                Log.d(TAG, "getSerialNumber: Method1 WORKED!"+serialNumber);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "getSerialNumber: Method1 FAILED!");
                serialNumber = "";

            }
        }
        if (serialNumber.equals("") || serialNumber.contains("unknown")) {
            try {
                serialNumber = (String) get.invoke(c, "ro.serialno");
                Log.d(TAG, "getSerialNumber: Method2 WORKED!"+serialNumber);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "getSerialNumber: Method2 FAILED!");
                serialNumber = "";

            }
        }
        if (serialNumber.equals("") || serialNumber.contains("unknown")) {
            try {
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
                Log.d(TAG, "getSerialNumber: Method3 WORKED!"+serialNumber);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "getSerialNumber: Method3 FAILED!");
                serialNumber = "";
            }
        }
        if (serialNumber.equals("") || serialNumber.contains("unknown")) {
            try {
                serialNumber = Build.SERIAL;
                Log.d(TAG, "getSerialNumber: Method4 WORKED!"+serialNumber);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "getSerialNumber: Method4 FAILED!");
                serialNumber = "";
            }
        }
        if (serialNumber.equals("") || serialNumber.contains("unknown")) {
            try {
                serialNumber = Build.getSerial().toUpperCase();
                Log.d(TAG, "getSerialNumber: Method5 WORKED!"+serialNumber);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "getSerialNumber: Method5 FAILED!");
                serialNumber = "";
            }
        }
        if (serialNumber.equals("") || serialNumber.contains("unknown")) {
            try {
                serialNumber= getMACAddress("wlan0");
                Log.d(TAG, "getSerialNumber: Method6 WORKED!"+serialNumber);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "getSerialNumber: Method6 FAILED!");
                serialNumber = "";
            }
        }
        if (serialNumber.equals("") || serialNumber.contains("unknown")) {
            try {
                serialNumber = UUID.nameUUIDFromBytes(getIPAddress(true).getBytes()).toString().substring(0, 8);
                Log.d(TAG, "getSerialNumber: Method7 WORKED!"+serialNumber);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "getSerialNumber: Method7 FAILED!");
                serialNumber = "";
            }
        }

        serialNumber = "UUID-"+serialNumber.toUpperCase();

        Log.d(TAG, "getSerialNumber: "+serialNumber);
        return serialNumber;
    }

    private static boolean checkPermissionsGranted(String[] checkList, ArrayList<String> notGrantedList){
        Log.d("checkPermissionsGranted","Started");
        boolean resi = true;
        notGrantedList.clear();
        for (int i=0; i<checkList.length ; i++){
            String perm = checkList[i];
            if (ContextCompat.checkSelfPermission( getService().getApplicationContext(), perm) != PackageManager.PERMISSION_GRANTED) {
                notGrantedList.add(perm);
                resi = false;
            }
        }

        Log.d("checkPermissionsGranted","Finished");
        return resi;
    }

    public static class Link{
        public static String getFileName(String URL){
            String[] arr_link = URL.split("/");
            String filename = arr_link[arr_link.length-1];
            return filename;
        }


        public static String getSuffix(String URL){
            String suffix = null;
            String filename = getFileName(URL);
            String[] arr_filename = filename.split("\\.");
            suffix = arr_filename[1];

            return suffix;
        }
        public static String getPrefix(String URL){
            String prefix = null;

            String filename = getFileName(URL);
            String[] arr_filename = filename.split("\\.");
            prefix = arr_filename[0];

            return prefix;
        }
    }

}