package com.jvziyaoyao.picmove.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.jvziyaoyao.picmove.PicMoveApplication;
import com.jvziyaoyao.picmove.dao.PhotoEntityDao;
import com.jvziyaoyao.picmove.dao.green.DaoManager;
import com.jvziyaoyao.picmove.domain.entity.PhotoEntity;
import com.jvziyaoyao.picmove.domain.model.PhotoQueryEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GalleryFileUtils {

    public static final String TAG = GalleryFileUtils.class.getSimpleName();

    public static final String PIC_DIR_NAME = "PicMove"; //在系统的图片文件夹下创建了一个相册文件夹，名为“myPhotos"，所有的图片都保存在该文件夹下。
    private static File mPicDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), PIC_DIR_NAME); //图片统一保存在系统的图片文件夹中

    public static void query2List(Cursor query, List<PhotoQueryEntity> list) {
        if (query == null) return;
        String[] columnNames = query.getColumnNames();
        while (query.moveToNext()) {
            PhotoQueryEntity entity = new PhotoQueryEntity();
            try {
                for (String columnName : columnNames) {
                    Log.i(TAG, "query2List: --> columnName " + columnName + " - " + query.getString(query.getColumnIndex(columnName)));
                    if (columnName.equals(MediaStore.Images.Media.DOCUMENT_ID)) {
                        entity.setName(query.getString(query.getColumnIndex(columnName)));
                    } else if (columnName.equals(MediaStore.Images.Media.DATA)) {
                        entity.setPath(query.getString(query.getColumnIndex(columnName)));
                    } else if (columnName.equals(MediaStore.Images.Media.SIZE)) {
                        entity.setSize(Long.parseLong(query.getString(query.getColumnIndex(columnName))));
                    } else if (columnName.equals(MediaStore.Images.Media.DATE_ADDED)) {
                        entity.setTime(Long.parseLong(query.getString(query.getColumnIndex(columnName))));
                    } else if (columnName.equals(MediaStore.Images.Media._ID)) {
                        entity.setId(Long.parseLong(query.getString(query.getColumnIndex(columnName))));
                        Log.i(TAG, "query2List: ---> _ID " + query.getColumnIndex(columnName));
                    }
                }
            } catch (Exception e) {
                entity = null;
            }
            if (entity != null) {
                list.add(entity);
            }
        }
    }

    /**
     * 获取全部列表
     * @param context
     * @return
     */
    public static List<PhotoQueryEntity> getAllList(final Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor query = null;
        try {
            query = contentResolver.query(uri, null, null, null, MediaStore.MediaColumns.DATE_ADDED + " DESC");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<PhotoQueryEntity> list = new ArrayList<>();
        query2List(query,list);
        if (query != null) query.close();
        return list;
    }


    /**
     * 查询历史更新列表
     * @return
     */
    public static List<PhotoQueryEntity> getMoveListFromDao() {
        List<PhotoEntity> list = DaoManager.getInstance().getMPhotoEntityDao().loadAll();
        List<PhotoQueryEntity> moveList = list.stream()
                .map(p -> PhotoQueryEntity.photo2QueryEntity(p))
                .sorted((a,b) -> (int) (b.getTime() - a.getTime()))
                .collect(Collectors.toList());
        moveList.forEach(e -> {
            Log.i(TAG, "getMoveListFromDao: ---> " + e);
        });
        return moveList;
    }

    /**
     * 通过列表更新
     * @param list
     * @param context
     */
    public static void renewByList(List<PhotoQueryEntity> list, Context context) {
        long current = System.currentTimeMillis();


        PhotoEntityDao photoEntityDao = DaoManager.getInstance().getMPhotoEntityDao();
        list.forEach(photo -> {
            Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, photo.getId());

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATE_ADDED, current);
            values.put(MediaStore.Images.Media.DATE_MODIFIED, current);
//            values.put(MediaStore.Images.Media.DATE_TAKEN, current);

            Log.i(TAG, " renewByList c: ---> sdk " + Build.VERSION.SDK_INT);
            int res = -1;
            if (android.os.Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
                res = updateAboveQ(context,values,uri);
            } else {
                res = updateBelowQ(context,values,uri);
            }
            Log.i(TAG, "renewByList: ---> res " + res);

            Cursor query = context.getContentResolver().query(uri, null, null, null,null);
            List<PhotoQueryEntity> ll = new ArrayList<>();
            query2List(query,ll);

            PhotoEntity photoEntity = PhotoQueryEntity.query2PhotoEntity(photo);
            photoEntity.setTime(current);
            photoEntityDao.insertOrReplace(photoEntity);
            if (PicMoveApplication.getMSystemConfig().getIS_COPY_FILE()) {
                try {
                    renewFile(photoEntity.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static int updateBelowQ(Context context, ContentValues values, Uri uri) {
        return context.getContentResolver().update(uri, values, null, null);
    }

    public static int updateAboveQ(Context context, ContentValues values, Uri uri) {
        ContentValues bv = new ContentValues();
        bv.put(MediaStore.Images.Media.IS_PENDING, 1);
        int res_pending = context.getContentResolver().update(uri, bv, null, null);
        Log.i(TAG, "updateAboveQ: ---> bv " + res_pending);
        values.put(MediaStore.Images.Media.IS_PENDING, 0);
        return context.getContentResolver().update(uri, values, null, null);
    }

    /**
     * 更新文件
     * @param path
     */
    public static void renewFile(String path) {
        File file = new File(path);
        File cp = new File(mPicDir,"cp-" + file.getName());
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(cp);
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = fis.read(buf)) != -1) {
                fos.write(buf,0,len);
                fos.flush();
            }
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cp.exists()) {
            cp.renameTo(file);
        }
    }

    /**
     * 通过列表删除
     * @param list
     * @param context
     */
    public static void deleteByList(List<PhotoQueryEntity> list, Context context) {
        PhotoEntityDao photoEntityDao = DaoManager.getInstance().getMPhotoEntityDao();
        list.stream()
                .map(p -> PhotoQueryEntity.query2PhotoEntity(p))
                .forEach(p -> {
                    photoEntityDao.delete(p);
                });
    }

    /**
     * 根据id查询
     * @param context
     * @param list
     * @return
     */
    public static List<PhotoQueryEntity> queryPhotoQueryEntityByIds(Context context, String[] list) {
        List<PhotoQueryEntity> p_l = new ArrayList<>();
        if (list == null || list.length == 0) return p_l;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor query = context.getContentResolver().query(uri, null, "_data IN ('" + TextUtils.join("\',\'", list) + "') ", null, null);
        GalleryFileUtils.query2List(query, p_l);
        return p_l;
    }

    /**
     * 检查移动列表的图是否都还存在
     * @param context
     */
    public static void checkMoveListExists(Context context) {
        PhotoEntityDao photoEntityDao = DaoManager.getInstance().getMPhotoEntityDao();
        // 查出列表
        List<PhotoEntity> list = photoEntityDao.loadAll();
        // 查出图库列表
        String[] ids = (String[]) list.stream().map(a -> a.getPath()).collect(Collectors.toList()).toArray(new String[list.size()]);
        List<PhotoQueryEntity> existList = queryPhotoQueryEntityByIds(context, ids);
        Map<String, PhotoQueryEntity> existMap = existList.stream().collect(Collectors.toMap(PhotoQueryEntity::getPath, a -> a));
        // 查出到底是谁不存在了
        List<PhotoEntity> notExistList = list.stream().filter(a -> null == existMap.get(a.getPath())).collect(Collectors.toList());
        // 删除不存在的
        notExistList.forEach(e -> {
            photoEntityDao.delete(e);
        });
    }

}
