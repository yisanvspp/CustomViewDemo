package com.example.selectfile;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.example.selectfile.bean.FileEntity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 获取手机内部文件数据工具类
 */
public class MediaStoreUtils {

    /**
     * 系统的查询类
     */
    static ContentResolver mContentResolver;


    /**
     * 手机中的文件
     *
     * @param context 上下文
     * @return 文件集合
     */
    public static List<FileEntity> getFilesOfTelephone(Context context) {
        //保存需要文件
        List<FileEntity> files = new ArrayList<>();
        //扫描系统数据库数据
        Cursor cursor = null;
        try {
            mContentResolver = context.getContentResolver();
            cursor = mContentResolver.query(MediaStore.Files.getContentUri("external"), null, null, null, null);
            int id = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            int mimeType = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
            int data = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            int size = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);

            int tempId = 0;
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                String path = cursor.getString(data);
                String fileType = cursor.getString(mimeType);
                if (!TextUtils.isEmpty(path)) {
                    //最后一个逗号
                    int commaIndex = path.lastIndexOf(".");
                    if (commaIndex == -1) {
                        continue;
                    }
                    //最后一个斜杠
                    int separatorIndex = path.lastIndexOf(File.separator);
                    if (separatorIndex == -1) {
                        continue;
                    }
                    //文件名称
                    String displayName = path.substring(separatorIndex + 1, path.length());
                    //文件大小
                    long fileSize = cursor.getLong(size);

                    File file = new File(path);
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()));//线程
                    FileEntity info = new FileEntity();
                    info.setName(displayName);
                    info.setPath(path);
                    info.setSize(size);
                    files.add(info);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return files;
    }


    /**
     * 获取指定字段的数据
     *
     * @param context 上下文
     * @param columns MediaStore.Files.FileColumns._ID,
     *                MediaStore.Files.FileColumns.MIME_TYPE,
     *                MediaStore.Files.FileColumns.SIZE,
     *                MediaStore.Files.FileColumns.DATE_MODIFIED,
     *                MediaStore.Files.FileColumns.DATA
     * @return 文件集合
     */

    public static List<FileEntity> getFilesOfTelephone(Context context, String[] columns) {
        //保存需要文件
        List<FileEntity> files = new ArrayList<>();
        //扫描系统数据库数据
        Cursor cursor = null;
        try {
            mContentResolver = context.getContentResolver();
            cursor = mContentResolver.query(MediaStore.Files.getContentUri("external"), columns, null, null, null);
            int id = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            int mimeType = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
            int data = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            int size = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);

            int tempId = 0;
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                String path = cursor.getString(data);
                String fileType = cursor.getString(mimeType);
                if (!TextUtils.isEmpty(path)) {
                    //最后一个逗号
                    int commaIndex = path.lastIndexOf(".");
                    if (commaIndex == -1) {
                        continue;
                    }
                    //最后一个斜杠
                    int separatorIndex = path.lastIndexOf(File.separator);
                    if (separatorIndex == -1) {
                        continue;
                    }
                    //文件名称
                    String displayName = path.substring(separatorIndex + 1, path.length());
                    //文件大小
                    long fileSize = cursor.getLong(size);

                    File file = new File(path);
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()));//线程
                    FileEntity info = new FileEntity();
                    info.setName(displayName);
                    info.setPath(path);
                    info.setSize(size);
                    files.add(info);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return files;
    }


    /**
     * 根据指定文件夹名称查询文件
     *
     * @param context        上下文
     * @param systemFileName MediaStore.Video.Media.BUCKET_DISPLAY_NAME  ...
     * @param fileName       文件夹名称
     * @return 文件集合
     */
    public static List<FileEntity> getFilesOfTelephone(Context context, String systemFileName, String fileName) {
        //保存需要文件
        List<FileEntity> files = new ArrayList<>();
        //扫描系统数据库数据
        Cursor cursor = null;
        try {
            mContentResolver = context.getContentResolver();
            cursor = mContentResolver.query(MediaStore.Files.getContentUri("external"), null,
                    systemFileName + "=?", new String[]{fileName}, null);
            int id = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            int mimeType = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
            int data = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            int size = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);

            int tempId = 0;
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                String path = cursor.getString(data);
                String fileType = cursor.getString(mimeType);
                if (!TextUtils.isEmpty(path)) {
                    //最后一个逗号
                    int commaIndex = path.lastIndexOf(".");
                    if (commaIndex == -1) {
                        continue;
                    }
                    //最后一个斜杠
                    int separatorIndex = path.lastIndexOf(File.separator);
                    if (separatorIndex == -1) {
                        continue;
                    }
                    //文件名称
                    String displayName = path.substring(separatorIndex + 1, path.length());
                    //文件大小
                    long fileSize = cursor.getLong(size);

                    File file = new File(path);
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()));//线程
                    FileEntity info = new FileEntity();
                    info.setName(displayName);
                    info.setPath(path);
                    info.setSize(fileSize);
                    files.add(info);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return files;
    }


    /**
     * 查询指定类型的文件数据
     *
     * @param context 上下文
     * @param sql     查询语句  String select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.doc'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.docx'" + ")";
     * @return 文件集合
     */
    public static List<FileEntity> getFilesOfTelephone(Context context, String sql) {
        //保存需要文件
        List<FileEntity> files = new ArrayList<>();
        //扫描系统数据库数据
        Cursor cursor = null;
        try {
            mContentResolver = context.getContentResolver();
            cursor = mContentResolver.query(MediaStore.Files.getContentUri("external"), null,
                    sql, null, null);
            int id = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            int mimeType = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
            int data = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            int size = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);

            int tempId = 0;
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                String path = cursor.getString(data);
                String fileType = cursor.getString(mimeType);
                if (!TextUtils.isEmpty(path)) {
                    //最后一个逗号
                    int commaIndex = path.lastIndexOf(".");
                    if (commaIndex == -1) {
                        continue;
                    }
                    //最后一个斜杠
                    int separatorIndex = path.lastIndexOf(File.separator);
                    if (separatorIndex == -1) {
                        continue;
                    }
                    //文件名称
                    String displayName = path.substring(separatorIndex + 1, path.length());
                    //文件大小
                    long fileSize = cursor.getLong(size);

                    File file = new File(path);
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()));//线程
                    FileEntity info = new FileEntity();
                    info.setName(displayName);
                    info.setPath(path);
                    info.setSize(fileSize);
                    files.add(info);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return files;
    }


}
