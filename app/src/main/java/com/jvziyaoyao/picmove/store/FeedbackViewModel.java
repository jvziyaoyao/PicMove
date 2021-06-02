package com.jvziyaoyao.picmove.store;

import android.app.Activity;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jvziyaoyao.picmove.utils.MUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;

import java.util.TimerTask;
import java.util.logging.Handler;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-03-21 15:02
 **/
public class FeedbackViewModel extends ViewModel {

    public static final String TAG = FeedbackViewModel.class.getSimpleName();

    // bug描述
    @Getter
    @Setter
    MutableLiveData<String> BUG_INFO = new MutableLiveData<>("");
    // 联系方式
    @Getter
    @Setter
    MutableLiveData<String> CONTACT_INFO = new MutableLiveData<>("");
    // 加载标识
    @Getter
    MutableLiveData<Boolean> LOADING = new MutableLiveData<>(false);

    // 返回事件
    @Setter
    private Runnable onBack;
    // 提交事件
    @Setter
    private Runnable onCommit;

    /**
     * 页面返回
     */
    public void goBack() {
        if (onBack != null) onBack.run();
    }

    /**
     * 提交按钮
     */
    public void goCommit(View view) {
        if (LOADING.getValue()) return;
        LOADING.setValue(true);
    }

}
