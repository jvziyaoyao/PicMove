package com.jvziyaoyao.picmove.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hitomi.tilibrary.transfer.TransferConfig;
import com.hitomi.tilibrary.transfer.Transferee;
import com.jvziyaoyao.picmove.BuildConfig;
import com.jvziyaoyao.picmove.PicMoveApplication;
import com.jvziyaoyao.picmove.R;
import com.jvziyaoyao.picmove.adapter.SelectPhotoListAdapter;
import com.jvziyaoyao.picmove.databinding.ActivityMainBinding;
import com.jvziyaoyao.picmove.domain.model.PhotoQueryEntity;
import com.jvziyaoyao.picmove.extend.PhotoIndicator.CusIndicator;
import com.jvziyaoyao.picmove.extend.PhotoIndicator.LeftNumberIndicator;
import com.jvziyaoyao.picmove.store.MainPhotoViewModel;
import com.jvziyaoyao.picmove.store.MainViewModel;
import com.jvziyaoyao.picmove.utils.GalleryFileUtils;
import com.jvziyaoyao.picmove.utils.MUtils;
import com.jvziyaoyao.picmove.utils.PicMoveUtils;
import com.jvziyaoyao.picmove.utils.TransfereeUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    // 权限申请
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public static final String TXT_NOTICE = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_NOTICE);
    public static final String TXT_DO_YOU_NEED_UPDATE = PicMoveApplication.getApplication().getString(R.string.TXT_AM_DO_YOU_NEED_UPDATE);
    public static final String TXT_DOWNLOAD = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_DOWNLOAD);
    public static final String TXT_WE_NEED_PERMISSIONS = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AM_WE_NEED_PERMISSIONS);
    public static final String TXT_CANCEL = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_CANCEL);
    public static final String TXT_GRANT = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_GRANT);
    public static final String TXT_DOING_COPY = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AM_DOING_COPY);
    public static final String TXT_PHOTOS_SELECTED = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AM_PHOTOS_SELECTED);
    public static final String TXT_NONE = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_NONE);
    public static final String TXT_ALL = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_ALL);

    // 延后初始化内容的延后时间
    public static final int AFTER_INIT_DELAY = 99;

    private final int gap = PicMoveUtils.gap;

    // 权限校验
    private boolean permissionGranted = false;

    // 选图返回码
    public static final int GO_SEL_PHOTO_REQUEST = 198;

    // 绑定对象
    private ActivityMainBinding mBinding;

    // 首页数据卷
    private MainViewModel mMainViewModel;
    // 视图浏览器
    private RecyclerView mShowListRecycler;
    // 加载中弹窗
    private LoadingPopupView mLoadingDialog;
    // 选中提示文字
    private TextView mSelCountText;
    // 选择全部或不选
    private TextView mAllOrNone;
    // 主视图
    private ConstraintLayout mMainView;

    // 图片预览相关
    private Transferee transferee;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        MUtils.setTranslucentStatus(MainActivity.this);
        MUtils.setAndroidNativeLightStatusBar(MainActivity.this, true);
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mMainViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);
        mBinding.setMainViewModel(mMainViewModel);
        mBinding.setLifecycleOwner(this);

        // UI初始化
        mSelCountText = findViewById(R.id.main_can_center_info);
        mAllOrNone = findViewById(R.id.main_can_all);
        mMainView = findViewById(R.id.main_page);
        MUtils.setAllTypeFace(mMainView, MainActivity.this);
        transferee = TransfereeUtils.getTransferee(MainActivity.this, null, null);

        // 注入选图跳转事件
        mMainViewModel.setOnSelect(() -> {
            Intent intent = new Intent(MainActivity.this, SelectPhotoActivity.class);
            MainActivity.this.startActivityForResult(intent, MainActivity.GO_SEL_PHOTO_REQUEST);
        });
        mMainViewModel.setOnMore(() -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            MainActivity.this.startActivity(intent);
        });

        bindObserver();

        initDelay();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 检查权限
        requestPermission();
        // 检查更新
        if (PicMoveApplication.getMSystemConfig().getAUTO_UPDATE()) checkVersion();
    }

    /**
     * 检查更新
     */
    private void checkVersion() {
        PicMoveUtils.checkVersion(res -> {
            if (BuildConfig.VERSION_CODE != res.getVersion_code()) {
                new XPopup.Builder(MainActivity.this)
                        .asConfirm(TXT_NOTICE, TXT_DO_YOU_NEED_UPDATE, TXT_CANCEL, TXT_DOWNLOAD, () -> {
                            PicMoveUtils.getDown(MainActivity.this, res.getUrl());
                        }, () -> {
                        }, false)
                        .show();
            }
        }, msg -> {
        });
    }

    /**
     * 延后执行
     */
    private void initDelay() {
        if (mMainViewModel == null) return;
        try {
            MUtils.goTimerDelay(() -> {
                int spanCount = 4;
                mShowListRecycler = findViewById(R.id.main_show_list);
                GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
                mShowListRecycler.setLayoutManager(layoutManager);
                mShowListRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        outRect.left = gap;
                        outRect.bottom = gap;
                        if (parent.getChildLayoutPosition(view) % spanCount == 0) {
                            outRect.left = 0;
                        }
                    }
                });
                mShowListRecycler.setAdapter(mSelectPhotoListAdapter);

//                getPicMovePhotos();
            }, AFTER_INIT_DELAY, MainActivity.this);
        } catch (Exception e) {
            mMainViewModel.getMAIN_LOADING().setValue(false);
        }
    }

    // 视图浏览器适配器
    private SelectPhotoListAdapter mSelectPhotoListAdapter = new SelectPhotoListAdapter(new ArrayList<>(), PicMoveUtils.getBlockSize()) {
        @Override
        public void onHolderCustomer(SelectPhotoListAdapter.ViewHolder holder, int position) {
            PhotoQueryEntity photo = mMainViewModel.getMAIN_PIC_MOVE_LIST().getValue().get(position);
            holder.selHandler.setFocusable(false);
            holder.selHandler.setClickable(false);
            holder.image.setOnClickListener((v) -> {
                if (mMainViewModel.getMAIN_CAN_SELECT().getValue()) {
                    photo.setSelected(!photo.getSelected());
                    holder.onSel(photo.getSelected());
                    mMainViewModel.refreshCurrentSelList();
                } else {
                    List<String> urls = mMainViewModel
                            .getMAIN_PIC_MOVE_LIST()
                            .getValue()
                            .stream()
                            .map(a -> a.getPath())
                            .collect(Collectors.toList());
                    TransferConfig config = TransfereeUtils.getDefaultBuilder(MainActivity.this)
                            .setSourceUrlList(urls)
                            .setIndexIndicator(new CusIndicator(new LeftNumberIndicator(MainActivity.this)))
                            .setNowThumbnailIndex(position)
                            .bindRecyclerView(mShowListRecycler, R.id.spi_img);
                    transferee.apply(config).show();
                }
            });
            holder.image.setOnLongClickListener(v -> {
                Log.i(TAG, "onHolderCustomer: ---> onLong");
                if (mMainViewModel.getMAIN_CAN_SELECT().getValue()) return false;
                photo.setSelected(true);
                mMainViewModel.getMAIN_CAN_SELECT().setValue(true);
                mMainViewModel.refreshCurrentSelList();
                return true;
            });
        }
    };

    /**
     * 获取图片列表
     */
    public void getPicMovePhotos() {
        if (mMainViewModel != null) mMainViewModel.getPicMovePhotos();
    }

    /**
     * 申请权限
     */
    @SuppressLint("CheckResult")
    private void requestPermission() {
        if (permissionGranted) return;
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(PERMISSIONS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(granted -> {
                    Log.i(TAG, "requestPermission: ---> " + granted);
                    permissionGranted = granted;
                    if (granted) {
                        getPicMovePhotos();
                    } else {
                        new XPopup.Builder(MainActivity.this)
                                .asConfirm(
                                        TXT_NOTICE,
                                        TXT_WE_NEED_PERMISSIONS,
                                        TXT_CANCEL, TXT_GRANT,
                                        new OnConfirmListener() {
                                            @Override
                                            public void onConfirm() {
                                                goToAppSetting();
                                            }
                                        }, new OnCancelListener() {
                                            @Override
                                            public void onCancel() {
                                                // 不给权限就退出
                                                MainActivity.this.finish();
                                            }
                                        }, false)
                                .show();
                    }
                });
    }

    /**
     * 跳转到当前应用的设置界面
     */
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    /**
     * 文件转储
     *
     * @param list
     */
    private void saveTest(List<PhotoQueryEntity> list) {
        Log.i(TAG, "saveTest: ---> " + list);
        if (list == null || list.size() == 0) return;
        Observable.just(list)
                .map(new Function<List<PhotoQueryEntity>, List<PhotoQueryEntity>>() {
                    @Override
                    public List<PhotoQueryEntity> apply(List<PhotoQueryEntity> photoQueryEntities) throws Exception {
                        try {
                            GalleryFileUtils.renewByList(photoQueryEntities, MainActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return photoQueryEntities;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<List<PhotoQueryEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mLoadingDialog = new XPopup.Builder(MainActivity.this)
                                .asLoading();
                        mLoadingDialog.setTitle(TXT_DOING_COPY);
                        mLoadingDialog.show();
                    }

                    @Override
                    public void onNext(List<PhotoQueryEntity> value) {
                        mLoadingDialog.dismiss();
                        mSelectPhotoListAdapter.setList(new ArrayList<>());
                        getPicMovePhotos();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(PicMoveApplication.getApplication(),"Error",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        mLoadingDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_SEL_PHOTO_REQUEST && resultCode == MainPhotoViewModel.ON_SEL_PHOTO_RESULT_CODE) {
            if (data != null) {
                List<PhotoQueryEntity> list = (List<PhotoQueryEntity>) data.getSerializableExtra(MainPhotoViewModel.SEL_PHOTO_LIST_TAG);
                if (list != null) {
                    saveTest(list);
                }
            }
        }
    }

    /**
     * 监听显示列表
     */
    private Observer<List<PhotoQueryEntity>> mainShowListObserver = (val) -> {
        if (mSelectPhotoListAdapter != null) mSelectPhotoListAdapter.setList(val);
    };
    /**
     * 监听图片是否可以被选择
     */
    private Observer<Boolean> mainPhotoCanSelect = (val) -> {
        if (mSelectPhotoListAdapter != null) mSelectPhotoListAdapter.setCanSelect(val);
    };
    /**
     * 监听图片选中列表
     */
    private Observer<List<PhotoQueryEntity>> mainPhotoSelList = (val) -> {
        String selText = TXT_PHOTOS_SELECTED.replace("?", val.size()+"");
        if (mSelCountText != null) mSelCountText.setText(selText);
        if (mMainViewModel != null && mAllOrNone != null) {
            boolean isSelectAll = mMainViewModel.getMAIN_PIC_MOVE_LIST().getValue().size() == val.size();
            mAllOrNone.setText(isSelectAll ? TXT_NONE : TXT_ALL);
        }
        if (mSelectPhotoListAdapter != null) mSelectPhotoListAdapter.refresh();
    };

    /**
     * 绑定监听者
     */
    public void bindObserver() {
        if (mMainViewModel != null) {
            mMainViewModel.getMAIN_PIC_MOVE_LIST().observeForever(mainShowListObserver);
            mMainViewModel.getMAIN_CAN_SELECT().observeForever(mainPhotoCanSelect);
            mMainViewModel.getMAIN_SEL_PIC_LIST().observeForever(mainPhotoSelList);
        }
    }

    /**
     * 解绑监听者
     */
    public void unbindServer() {
        if (mMainViewModel != null) {
            mMainViewModel.getMAIN_PIC_MOVE_LIST().removeObserver(mainShowListObserver);
            mMainViewModel.getMAIN_CAN_SELECT().removeObserver(mainPhotoCanSelect);
            mMainViewModel.getMAIN_SEL_PIC_LIST().removeObserver(mainPhotoSelList);
        }
    }

    /**
     * 监听返回按键
     */
    @Override
    public void onBackPressed() {
        // 如果当前正在选择状态，点击返回按钮则退出选择状态
        if (mMainViewModel != null && mMainViewModel.getMAIN_CAN_SELECT().getValue()) {
            mMainViewModel.getMAIN_CAN_SELECT().setValue(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (transferee != null) transferee.destroy();
        unbindServer();
    }
}