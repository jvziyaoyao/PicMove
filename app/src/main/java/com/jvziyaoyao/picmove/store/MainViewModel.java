package com.jvziyaoyao.picmove.store;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jvziyaoyao.picmove.PicMoveApplication;
import com.jvziyaoyao.picmove.R;
import com.jvziyaoyao.picmove.domain.model.PhotoQueryEntity;
import com.jvziyaoyao.picmove.utils.GalleryFileUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;

import java.util.ArrayList;
import java.util.List;
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
 * @create: 2021-02-19 19:59
 **/
public class MainViewModel extends ViewModel {

    public static final String TAG = MainViewModel.class.getSimpleName();
    public static final String TXT_RENEWING = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AM_RENEWING);
    public static final String TXT_RENEW_SUCCEEDED = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AM_RENEW_SUCCEEDED);
    public static final String TXT_NOTICE = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_NOTICE);
    public static final String TXT_ARE_YOU_SURE_YOU_WANT_TO_DELETE = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AM_R_U_SURE_DELETE);
    public static final String TXT_CANCEL = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_CANCEL);
    public static final String TXT_CONFIRM = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_CONFRIM);
    public static final String TXT_DELETING = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AM_DELETING);
    public static final String TXT_DELETE_SUCCEEDED = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AM_DELETE_SUCCEEDED);

    // 主视图图片列表
    @Getter
    private MutableLiveData<List<PhotoQueryEntity>> MAIN_PIC_MOVE_LIST = new MutableLiveData<>(new ArrayList<>());
    // 主视图加载标识
    @Getter
    private MutableLiveData<Boolean> MAIN_LOADING = new MutableLiveData<>(false);
    // 标识当前页面状态
    @Getter
    private MutableLiveData<Boolean> MAIN_CAN_SELECT = new MutableLiveData<>(false);
    // 当前选中列表
    @Getter
    private MutableLiveData<List<PhotoQueryEntity>> MAIN_SEL_PIC_LIST = new MutableLiveData<>(new ArrayList<>());

    @Setter
    private Runnable onSelect;

    @Setter
    private Runnable onMore;

    /**
     * 获取图片列表
     */
    public void getPicMovePhotos() {
        Observable.just(MAIN_LOADING.getValue())
                .map(val -> {
                    List<PhotoQueryEntity> moveList = new ArrayList<>();
                    try {
                        // 检查图片是否存在，不存在就删掉
                        GalleryFileUtils.checkMoveListExists(PicMoveApplication.getApplication());
                        // 加载图片列表
                        moveList = GalleryFileUtils.getMoveListFromDao();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return moveList;
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
                        MAIN_PIC_MOVE_LIST.setValue(list);
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
     * 响应当前选中列表
     */
    public void refreshCurrentSelList() {
        List<PhotoQueryEntity> list = MAIN_PIC_MOVE_LIST.getValue();
        List<PhotoQueryEntity> resList = list.stream().filter(a -> a.getSelected()).collect(Collectors.toList());
        MAIN_SEL_PIC_LIST.setValue(resList);
    }

    /**
     * 清空当前列表
     */
    public void clearCurrentSelList() {
        List<PhotoQueryEntity> list = MAIN_PIC_MOVE_LIST.getValue();
        list.forEach(a -> a.setSelected(false));
        refreshCurrentSelList();
    }

    /**
     * 选择全部
     */
    public void selectAllOrNone(View view) {
        List<PhotoQueryEntity> list = MAIN_PIC_MOVE_LIST.getValue();
        int currentSize = list.stream().filter(a -> a.getSelected()).collect(Collectors.toList()).size();
        boolean isSelectAll = currentSize == list.size();
        list.forEach(a -> a.setSelected(!isSelectAll));
        refreshCurrentSelList();
    }

    /**
     * 跳转选择图片页面
     */
    public void goSelect() {
        if (onSelect != null) onSelect.run();
    }

    /**
     * 刷新图片
     */
    public void renewPhoto(View view) {
        loadingProxy(view.getContext(), TXT_RENEWING, () -> {
//            GalleryFileUtils.renewAllList(view.getContext());
            List<PhotoQueryEntity> allList = MAIN_PIC_MOVE_LIST.getValue();
            GalleryFileUtils.renewByList(allList,view.getContext());
            getPicMovePhotos();
            Toast.makeText(view.getContext(), TXT_RENEW_SUCCEEDED,Toast.LENGTH_LONG).show();
        });
    }

    /**
     * 两端增加加载中样式
     * @param context
     * @param title
     * @param run
     */
    public void loadingProxy(Context context, String title, Runnable run) {
        LoadingPopupView loadingPopupView = new XPopup.Builder(context)
                .asLoading();
        loadingPopupView.setTitle(title);
        loadingPopupView.show();
        loadingPopupView.show();
        run.run();
        loadingPopupView.dismiss();
    }

    /**
     * 取消选择状态
     * @param view
     */
    public void cancelSelect(View view) {
        MAIN_CAN_SELECT.setValue(false);
        clearCurrentSelList();
    }

    /**
     * 更新选中列表
     * @param view
     */
    public void renewSelected(View view) {
        loadingProxy(view.getContext(),TXT_RENEWING,() -> {
            List<PhotoQueryEntity> list = MAIN_SEL_PIC_LIST.getValue();
            GalleryFileUtils.renewByList(list,view.getContext());
            getPicMovePhotos();
            MAIN_CAN_SELECT.setValue(false);
            Toast.makeText(view.getContext(),TXT_RENEW_SUCCEEDED,Toast.LENGTH_LONG).show();
        });
    }

    /**
     * 删除选中
     * @param view
     */
    public void deleteFromSelected(View view) {
        new XPopup.Builder(view.getContext()).asConfirm(
                TXT_NOTICE,
                TXT_ARE_YOU_SURE_YOU_WANT_TO_DELETE,
                TXT_CANCEL,
                TXT_CONFIRM,
                () -> {
                    loadingProxy(view.getContext(), TXT_DELETING, () -> {
                        List<PhotoQueryEntity> list = MAIN_SEL_PIC_LIST.getValue();
                        GalleryFileUtils.deleteByList(list,view.getContext());
                        getPicMovePhotos();
                        MAIN_CAN_SELECT.setValue(false);
                        Toast.makeText(view.getContext(), TXT_DELETE_SUCCEEDED,Toast.LENGTH_LONG).show();
                    });
                },
                () -> {

                },
                false
        ).show();
    }

    /**
     * 更多按钮点击事件
     * @param view
     */
    public void onMore(View view) {
        if (onMore != null) onMore.run();
    }

}
