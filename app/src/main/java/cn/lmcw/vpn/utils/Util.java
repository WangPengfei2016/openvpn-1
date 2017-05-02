package cn.lmcw.vpn.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;

/**
 * Created by admin on 2017/4/24.
 */

public class Util {

    // 获取当前SD卡根目录
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }

        return sdDir.getPath();
    }

//    序列化数据

    //保存数据
    public static void save(String path, Serializable obj) {
        ObjectOutputStream fos = null;
        try {

            //如果文件不存在就创建文件
            File file = new File(path);
            //file.createNewFile();
            //获取输出流
            //这里如果文件不存在会创建文件，这是写文件和读文件不同的地方
            fos = new ObjectOutputStream(new FileOutputStream(file));
            //获取输入框内的文件进行写入
            //这里不能再用普通的write的方法了
            //要使用writeObject
            fos.writeObject(obj);
            ;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }

        }

    }

    //读取数据
    public static Object read(String path) {
        ObjectInputStream ois = null;
        Object obj = null;
        try {

            //获取输入流
            ois = new ObjectInputStream(new FileInputStream(new File(path)));
            //获取文件中的数据
            obj = ois.readObject();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }


    public static String humanReadableByteCount(long bytes, boolean mbit) {
        if (mbit)
            bytes = bytes * 8;
        int unit = mbit ? 1000 : 1024;
        if (bytes < unit)
            return bytes + (mbit ? " bit" : " B");

        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (mbit ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (mbit ? "" : "");
        if (mbit)
            return String.format(Locale.getDefault(), "%.1f %sbit", bytes / Math.pow(unit, exp), pre);
        else
            return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
