package com.jvziyaoyao.picmove.activity;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jvziyaoyao.picmove.PicMoveApplication;
import com.jvziyaoyao.picmove.R;
import com.jvziyaoyao.picmove.databinding.ActivityFeedbackBinding;
import com.jvziyaoyao.picmove.store.FeedbackViewModel;
import com.jvziyaoyao.picmove.utils.MUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-03-21 15:02
 **/
public class FeedbackActivity extends AppCompatActivity {

    public static final String TXT_D240 = "/240";
    public static final String TXT_COMMITTING = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AF_COMMITTING);
    public static final String TXT_NOTICE = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_NOTICE);
    public static final String TXT_REQUEST_FAILED = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AF_REQUEST_FAILED);
    public static final String TXT_CANCEL = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_CANCEL);
    public static final String TXT_OK = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AF_OK);

    // 绑定对象
    private ActivityFeedbackBinding mBinding;
    // 反馈页面数据卷
    private FeedbackViewModel mFeedbackViewModel;
    // 主视图
    private ConstraintLayout mMainView;
    // bug_info_count
    private TextView mBugCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        MUtils.setTranslucentStatus(FeedbackActivity.this);
        MUtils.setAndroidNativeLightStatusBar(FeedbackActivity.this, true);
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_feedback);
        mFeedbackViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(FeedbackViewModel.class);
        mBinding.setFeedbackViewModel(mFeedbackViewModel);
        mBinding.setLifecycleOwner(this);

        mMainView = findViewById(R.id.fd_page);
        MUtils.setAllTypeFace(mMainView,FeedbackActivity.this);

        mBugCount = findViewById(R.id.fd_bug_count);

        mFeedbackViewModel.setOnBack(this::finish);
    }

    private Observer<String> bugInfoObserver = (val) -> {
        if (mBugCount != null) mBugCount.setText(val.length() + TXT_D240);
    };

    private Observer<Boolean> loadingObserver = (val) -> {
        if (val) {
            LoadingPopupView loading = new XPopup.Builder(FeedbackActivity.this)
                    .asLoading(TXT_COMMITTING);
            loading.show();
            MUtils.goTimerDelay(() -> {
                loading.dismiss();
                mFeedbackViewModel.getLOADING().setValue(false);
                new XPopup.Builder(FeedbackActivity.this)
                        .asConfirm(TXT_NOTICE, TXT_REQUEST_FAILED, TXT_CANCEL, TXT_OK, () -> {}, () -> {}, true)
                        .show();
            },999, FeedbackActivity.this);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (mFeedbackViewModel != null) {
            mFeedbackViewModel.getBUG_INFO().observeForever(bugInfoObserver);
            mFeedbackViewModel.getLOADING().observeForever(loadingObserver);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFeedbackViewModel != null) {
            mFeedbackViewModel.getBUG_INFO().removeObserver(bugInfoObserver);
            mFeedbackViewModel.getLOADING().removeObserver(loadingObserver);
        }
    }
}
