package com.admin.mymusic.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/26 23:07
 **/
public class FileUtil {
    private static final String TAG = "数据监控_FileUtils";
    private boolean isStop = false;

    /**
     * 读取文件
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    private static InputStream read(File file) throws FileNotFoundException {
        if (file != null && file.isFile()) {
            InputStream in = new FileInputStream(file);
            return in;
        } else {
            return null;
        }

    }

    private static InputStream read(String path) throws FileNotFoundException {
        return read(new File(path));
    }


    private static String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }


    /*******************************************************************************************************************************************************************************************************
     *                                                                                             对外API
     *******************************************************************************************************************************************************************************************************
     */

    /**
     * 临时记录保存,保存路径
     * @param context
     * @param filePath 要保存的数据
     */
    public static void saveRecord(Context context,String filePath){
        String recordPath = FilePath.getFilesPath(context,"record")+stringToMD5(filePath);//临时保存路径
        FileWriter writer= null;
        try {
            writer = new FileWriter(recordPath);
            writer.write(filePath);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取保存的记录数据
     * @param context
     * @param filePath
     * @return
     */
    public static String readRecord(Context context,String filePath){
        String recordPath = FilePath.getFilesPath(context,"record")+stringToMD5(filePath);//临时保存路径
        InputStreamReader inr=null;
        BufferedReader reader=null;
        try {
            inr=new InputStreamReader(new FileInputStream(recordPath),"UTF-8");
            reader=new BufferedReader(inr);
            StringBuffer buffer=new StringBuffer();
            String line="";
            while ((line=reader.readLine())!=null){
                buffer.append(line);
            }
            return buffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inr!=null){
                try {
                    inr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 读取全部临时记录文件
     * @param context
     * @return
     */
    public static File[] readAllRecord(Context context){
        File file=FilePath.getFiles(context,"record");
        return file.listFiles();
    }

    /**
     * 删除缓存文件
     * @param context
     * @param filePath
     */
    public static void deleteRecord(Context context,String filePath){
        String recordPath = FilePath.getFilesPath(context,"record")+stringToMD5(filePath);//临时保存路径
        File file=new File(recordPath);
        file.delete();
    }


    /**
     * 保存歌词
     * @param context
     * @param data 要保存的内容
     * @param name 文件名称
     */
    public static void saveLyr(Context context,String data,String name){
        String lyr = FilePath.getLyrPath(context)+name;
        FileWriter writer= null;
        try {
            writer = new FileWriter(lyr);
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取歌词
     * @param context
     * @param name 要读取的文件名称
     * @return
     */
    public static InputStream readLyr(Context context,String name){
        String lyrPath = FilePath.getLyrPath(context, name);
        try {
            FileInputStream inputStream=new FileInputStream(lyrPath);
            return inputStream;
        } catch (FileNotFoundException e) {
            return null;
        }
    }





}
