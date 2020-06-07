package com.admin.mymusic.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Field;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/26 18:00
 **/
public class FilePath {

    //data/data/<application package>/files目录
    private static String getFilesDir(Context context) {
        return getFilesDirFile(context).getAbsolutePath();
    }

    //data/data/<application package>/files目录
    private static File getFilesDirFile(Context context) {
        return context.getFilesDir();
    }


    //data/data/<application package>/cache目录
    private static String getCacheDir(Context context) {
        return getCacheDirFile(context).getAbsolutePath();
    }

    //data/data/<application package>/cache目录
    private static File getCacheDirFile(Context context) {
        return context.getCacheDir();
    }


    //取到 SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    private static String getExternalFilesDir(Context context) {
        return getExternalFilesDirFile(context).getAbsolutePath();
    }

    //取到 SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    private static File getExternalFilesDirFile(Context context) {
        return context.getExternalFilesDir("");
    }


    //取到 SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    private static String getExternalCacheDir(Context context) {
        return getExternalCacheDirFile(context).getAbsolutePath();
    }

    //取到 SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    private static File getExternalCacheDirFile(Context context) {
        return context.getExternalCacheDir();
    }

    //storage/sdcard/0这个储存路径需要申请权限
    private static String getSDcard() {
        return getSDcardFile().getAbsolutePath();
    }

    //storage/sdcard/0这个储存路径需要申请权限
    private static File getSDcardFile() {
        return Environment.getExternalStorageDirectory();
    }


    private static String deleteSuffix(String str) {
        int a = str.lastIndexOf(".");
        if (a == -1) return str;
        return str.substring(0, a);
    }

    /**
     * 无后缀找文件
     *
     * @param path     文件所在的文件夹
     * @param fileName 文件名称
     * @return
     */
    private static File getFileNotSuffix(String path, String fileName) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0)
                for (File fi : files) {
                    if (fi.isFile()) {
                        String name = fi.getName();
                        name = deleteSuffix(name);
                        if (fileName.equals(name)) {
                            return fi;
                        }
                    }
                }
            return null;
        } else {
            return null;
        }
    }


    /***************************************************************************************************************************************************************************************************************************************
     *                                                                                              对外API
     * *************************************************************************************************************************************************************************************************************************************
     */


    /**
     * 获取缓存文件夹
     *
     * @param context
     * @param folder 文件夹名称
     * @return
     */
    public static File getCacFiles(Context context, String folder) {
        String path=null;
        try {
            path = getExternalCacheDirFile(context).getAbsolutePath() + File.separator + folder;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;//
        } catch (Exception e) {
            path = getCacheDirFile(context).getAbsolutePath() + File.separator + folder;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            return getCacheDirFile(context);
        }
    }

    /**
     * 获取缓存文件夹
     *
     * @param context
     * @return
     */
    public static String getCacFilesPath(Context context, String folder) {
        return getCacFiles(context,folder).getAbsolutePath() + File.separator;
    }


    /**
     * 获取系统文件夹
     *
     * @param context
     * @param folder  文件夹名称
     * @return
     */
    public static File getFiles(Context context, String folder) {
        String path = null;
        try {
            path = getExternalFilesDirFile(context).getAbsolutePath() + File.separator + folder;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;//
        } catch (Exception e2) {
            path = getFilesDirFile(context).getAbsolutePath() + File.separator + folder;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            return getFilesDirFile(context);
        }
    }

    /**
     * 获取系统文件夹
     *
     * @param context
     * @return
     */
    public static String getFilesPath(Context context, String folder) {
        return getFiles(context, folder).getAbsolutePath()+ File.separator;
    }

    /**
     * 获取歌词文件夹
     *
     * @param context
     * @return
     */
    public static File getLyr(Context context) {
        return getFiles(context,"lyr");//
    }
    /**
     * 获取歌词文件夹
     *
     * @param context
     * @return
     */
    public static String getLyrPath(Context context) {
        File file=getLyr(context);
        return file.getAbsolutePath()+ File.separator;
    }

   /**
     * 获取歌词文件路径
     *
     * @param context
     * @return
     */
   public static File getLyr(Context context,String lyrName){
       String path=getLyrPath(context)+lyrName;
       File file=new File(path);
       if(!file.isFile()){
           return null;
       }
       return file;
   }
    public static String getLyrPath(Context context,String lyrName) {
        File file=getLyr(context,lyrName);
        if(file==null){
            return null;
        }
        return file.getAbsolutePath()+ File.separator;
    }



    /**
     * 获取音乐下载的文件夹
     *
     * @return
     */
    public static File getDowFiles() {
        File file = null;
        String path = getSDcard() + File.separator + "Music";
        file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 获取下载的音乐路径
     * @param musicName
     * @return
     */
    public static File getDowFiles(String musicName) {
       String path=getDowFilesPath()+musicName;
       File file=new File(path);
       if(file.isFile()){
           return file;
       }
       return null;
    }

    /**
     * 获取音乐下载的文件路夹径
     *
     * @return
     */

    public static String getDowFilesPath() {
        return getDowFiles().getAbsolutePath() + File.separator;
    }
    /**
     * 获取音乐下载的文件路夹径
     * @param musicName 音乐名称
     * @return
     */

    public static String getDowFilesPath(String musicName) {
        File file=getDowFiles(musicName);
        if(file==null){
            return null;
        }
        return file.getAbsolutePath() + File.separator;
    }

    /**
     * 获取缓存音乐的路径
     * @param context
     * @return
     */
    public static File getMusicCacFiles(Context context){
        return getCacFiles(context,"musicCache");
    }
    /**
     * 获取缓存音乐的路径
     * @param context
     * @return
     */
    public static File getMusicCacFiles(Context context,String musicName){
        String path=getCacFilesPath(context,"musicCache")+musicName;
        File file=new File(path);
        if(file.isFile()){
            return file;
        }
        return null;
    }

    public static String getMusicCacFilesPath(Context context){
        return getMusicCacFiles(context).getAbsolutePath()+File.separator;
    }

    public static String getMusicCacFilesPath(Context context,String musicName){
        File file=getMusicCacFiles(context,musicName);
        if(file!=null){
            return file.getAbsolutePath();
        }
        return null;
    }


}
