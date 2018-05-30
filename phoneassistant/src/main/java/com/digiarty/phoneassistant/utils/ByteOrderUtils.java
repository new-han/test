package com.digiarty.phoneassistant.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteOrder;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

/***
 *
 * Created on：03/05/2018
 *
 * Created by：henmory
 *
 * Description:
 *
 *
 **/
public class ByteOrderUtils {

    public static boolean isLITTLE_ENDIAN;

    static {
        isLITTLE_ENDIAN = (LITTLE_ENDIAN == ByteOrder.nativeOrder());
    }
    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];
            targets[3] = (byte) (res & 0xff);// 最低位
            targets[2] = (byte) ((res >> 8) & 0xff);// 次低位
            targets[1] = (byte) ((res >> 16) & 0xff);// 次高位
            targets[0] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    public static int byteArrayToInt(byte[] b){
        byte[] a = new byte[4];
        int i = a.length - 1,j = b.length - 1;
        for (; i >= 0 ; i--,j--) {//从b的尾部(即int值的低位)开始copy数据
            if(j >= 0)
                a[i] = b[j];
            else
                a[i] = 0;//如果b.length不足4,则将高位补0
        }
        int v0 = (a[0] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
        int v1 = (a[1] & 0xff) << 16;
        int v2 = (a[2] & 0xff) << 8;
        int v3 = (a[3] & 0xff) ;
        return v0 + v1 + v2 + v3;
    }

    public static byte[] long2byte(long res) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((res >> offset) & 0xff);
        }
        return buffer;
    }

    public static long byteArrayToLong(byte[] b){
        long values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8;
            values|= (b[i] & 0xff);
        }
        return values;
    }


    /**
     * 切换大小端续
     */
    public static byte[] changeBytes(byte[] a){
        byte[] b = new byte[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = a[b.length - i - 1];
        }
        return b;
    }

    public static byte[] changeLongBytestoBigEndian(Long data){
        byte[] bytes;

        bytes = long2byte(data);

        if (isLITTLE_ENDIAN){
            return changeBytes(bytes);
        }else{
            return bytes;
        }
    }

    public static long getLongFromBigEndian(byte[] data){
        byte[] bytes;

        if (isLITTLE_ENDIAN){
            bytes = changeBytes(data);
        }else{
            bytes =  data;
        }
        return byteArrayToLong(bytes);
    }


    //字节转换成对象
    public static Object ByteToObject(byte[] bytes) {
        Object obj = null;
        try {
            // bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);

            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }

    //将对象转换成字节
    public static byte[] ObjectToByte(java.lang.Object obj) {
        byte[] bytes = null;
        try {
            // object to bytearray
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return bytes;
    }


    public static void main(String[] arg){
        long data = 8;
        byte[] by = changeLongBytestoBigEndian(data);
        for (int i  =0; i < 8; i++){
            System.out.print(by[i] + " ");
        }
    }


}
