package com.nv.youneverwait.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

    public static final String TAG = "Jaldee";
    private static boolean isDebuging = true;

    public static File getFileLocation() {

        //if there is no SD card, create new directory objects to make directory on device
        if (Environment.getExternalStorageState() == null) {
            //create new file directory object
            return Environment.getDataDirectory();
        } else
            return Environment.getExternalStorageDirectory();

    }

    public static String getFileLocation(String filename) {
        String fileLoc = getFileLocation() + "/Jaldee/Log";

        File logFile = new File(fileLoc);

        if (!logFile.exists()) {
            logFile.mkdirs();
        }
        return fileLoc + filename;
    }

    public static void writeDeviceLog(String text) {

      /* File logFile = new File(getFileLocation("/device_readings.txt"));
          write(logFile, text);*/
    }

    public static void writeServerStatus(String text) {
//
//        File logFile = new File(getFileLocation("/server_connection.txt"));
//        write(logFile, text);
        writeLogTest(text);
    }


    public static void writeLogTest(String text) {

        File logFile = new File(getFileLocation("/app_log.txt"));
        write(logFile, text);
    }

    public static String getShareFile() {
        return getFileLocation("/app_log.txt");
    }

    private static void write(File logFile, String text) {

        if (isDebuging) {

            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            writeToFile(logFile, text);
        } else {

            if (logFile.exists()) {
                logFile.delete();
            }
        }
    }


    private static void writeToFile(File logFile, String text) {

        try {
            Log.e("Jaldee", text);
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));

            SimpleDateFormat dateFormat = new SimpleDateFormat(("dd-MM-yyyy hh:mm:ss a"));
            String localDateTime = dateFormat.format(new Date());

            buf.append(localDateTime + " : " + text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}