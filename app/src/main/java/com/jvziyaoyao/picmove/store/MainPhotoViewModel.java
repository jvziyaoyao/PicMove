package com.jvziyaoyao.picmove.store;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jvziyaoyao.picmove.PicMoveApplication;
import com.jvziyaoyao.picmove.R;
import com.jvziyaoyao.picmove.domain.model.AlbumsEntity;
import com.jvziyaoyao.picmove.domain.model.PhotoQueryEntity;
import com.jvziyaoyao.picmove.utils.GalleryFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.Setter;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-02-21 23:06
 **/
public class MainPhotoViewModel extends ViewModel {

    public static final String TAG = MainPhotoViewModel.class.getSimpleName();
    public static final String TXT_LATEST_NAME = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_ASL_LATEST_NAME);
    public static final String SEL_PHOTO_LIST_TAG = "SEL_PHOTO_LIST";
    public static final int ON_SEL_PHOTO_RESULT_CODE = 198;
//    public static MainPhotoViewModel mainPhotoViewModel;

    // 是否显示相册列表
    @Getter
    private MutableLiveData<Boolean> SHOW_ALBUMS_SEL_LIST = new MutableLiveData<>(false);
    // 当前显示图片列表
    @Getter
    private MutableLiveData<AlbumsEntity> SHOW_SEL_ALBUM = new MutableLiveData<>(null);
    // 当前显示的相册列表
    @Getter
    private MutableLiveData<List<AlbumsEntity>> SHOW_ALBUMS = new MutableLiveData<>(new ArrayList<>());
    // 当前选中列表
    @Getter
    private MutableLiveData<List<PhotoQueryEntity>> SEL_PHOTO_LIST = new MutableLiveData<>(new ArrayList<>());
    // 主视图加载标识
    @Getter
    private MutableLiveData<Boolean> MAIN_LOADING = new MutableLiveData<>(false);

    // 原始数据集
    private List<PhotoQueryEntity> srcList;
    // 原始相册集
    private List<AlbumsEntity> srcAlbumsList;

    public MainPhotoViewModel() {
//        mainPhotoViewModel = this;
    }

    /**
     * 响应选中列表
     */
    public void notifySelList() {
        List<PhotoQueryEntity> res = srcList.stream().filter(a -> a.getSelected()).collect(Collectors.toList());
        SEL_PHOTO_LIST.setValue(res);
    }

    /**
     * 显示图片相册列表
     * @param view
     */
    public void onAlbumsListShow(View view) {
        SHOW_ALBUMS_SEL_LIST.setValue(!SHOW_ALBUMS_SEL_LIST.getValue());
    }

    /**
     * 退出选择页面
     */
    @Setter
    private Runnable onSelActivityBack;
    public void onSelActivityBack() {
        if (onSelActivityBack != null) onSelActivityBack.run();
    }

    /**
     * 传出结果
     */
    @Setter
    private Runnable onGetResult;
    public void getResult() {
        if (onGetResult != null) onGetResult.run();
    }

    /**
     * 预览图片
     * @param view
     */
    @Setter
    private Runnable onPhotoPreview;
    public void onPhotoPreview(View view) {
        if (onPhotoPreview != null) onPhotoPreview.run();
    }

    /**
     * 查询手机图片列表
     */
    public void getPhotoList() {
        Context context = PicMoveApplication.getApplication();
        if (context == null) return;
        // 需要异步
        Observable.just(this)
                .map(it -> {
                    List<PhotoQueryEntity> list = GalleryFileUtils.getAllList(context);
                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<PhotoQueryEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        MAIN_LOADING.setValue(true);
                    }
                    @Override
                    public void onNext(List<PhotoQueryEntity> list) {
                        formatPhotoList(list);
                    }
                    @Override
                    public void onError(Throwable e) {}
                    @Override
                    public void onComplete() {
                        MAIN_LOADING.setValue(false);
                    }
                });
    }

    /**
     * 得到原始图片列表后整理
     * @param list
     */
    private void formatPhotoList(List<PhotoQueryEntity> list) {
        MainPhotoViewModel.this.srcList = list;

        Map<String, AlbumsEntity> albumsEntityMap = new HashMap<>();
        MainPhotoViewModel.this.srcList.forEach(p -> {
            String folderPath = getFolderPath(p.getPath());
            String folderName = getFolderName(folderPath);
            AlbumsEntity albumsEntity = albumsEntityMap.get(folderPath);
            if (albumsEntity == null) {
                albumsEntity = new AlbumsEntity();
                albumsEntity.setFolderPath(folderPath);
                albumsEntity.setFolderName(folderName);
                albumsEntity.setPhotos(new ArrayList<>());
            }
            albumsEntity.getPhotos().add(p);
            albumsEntityMap.put(folderPath, albumsEntity);
        });
        srcAlbumsList = new ArrayList<>();
        AlbumsEntity latest = new AlbumsEntity();
        latest.setFolderName(TXT_LATEST_NAME);
        if (srcList.size() > 1000) {
            latest.setPhotos(srcList.subList(0, 1000));
        } else {
            latest.setPhotos(srcList);
        }
        srcAlbumsList.add(latest);
        albumsEntityMap.entrySet().forEach(am -> {
            srcAlbumsList.add(am.getValue());
        });

        SHOW_SEL_ALBUM.setValue(latest);
        SHOW_ALBUMS.setValue(srcAlbumsList);
    }

    /**
     * 获取文件路径方法
     * @param filePath
     * @return
     */
    private String getFolderPath(String filePath) {
        int endIndex = filePath.lastIndexOf(File.separator);
        return filePath.substring(0, endIndex);
    }

    /**
     * 获取文件夹名方法
     * @param folderPath
     * @return
     */
    private String getFolderName(String folderPath) {
        int startIndex = folderPath.lastIndexOf(File.separator) + 1;
        return folderPath.substring(startIndex);
    }

}
