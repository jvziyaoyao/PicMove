package com.jvziyaoyao.picmove.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hitomi.tilibrary.transfer.TransferConfig;
import com.hitomi.tilibrary.transfer.Transferee;
import com.jvziyaoyao.picmove.PicMoveApplication;
import com.jvziyaoyao.picmove.R;
import com.jvziyaoyao.picmove.adapter.AlbumListAdapter;
import com.jvziyaoyao.picmove.adapter.SelectPhotoListAdapter;
import com.jvziyaoyao.picmove.databinding.ActivitySelectPhotoBinding;
import com.jvziyaoyao.picmove.domain.model.AlbumsEntity;
import com.jvziyaoyao.picmove.domain.model.PhotoQueryEntity;
import com.jvziyaoyao.picmove.extend.PhotoIndicator.CusIndicator;
import com.jvziyaoyao.picmove.extend.PhotoIndicator.PhotoIndicator;
import com.jvziyaoyao.picmove.store.MainPhotoViewModel;
import com.jvziyaoyao.picmove.utils.MUtils;
import com.jvziyaoyao.picmove.utils.PicMoveUtils;
import com.jvziyaoyao.picmove.utils.TransfereeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jvziyaoyao.picmove.store.MainPhotoViewModel.ON_SEL_PHOTO_RESULT_CODE;
import static com.jvziyaoyao.picmove.store.MainPhotoViewModel.SEL_PHOTO_LIST_TAG;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-02-19 21:00
 **/
public class SelectPhotoActivity extends AppCompatActivity {

    public static final String TAG = SelectPhotoActivity.class.getSimpleName();

    // 延后执行初始化的时间
    public static final int INIT_DELAY = 99;
    public static final String TXT_OK_SELECT = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_ASL_OK_SELECTED);

    // 格子间隔大小
    public final int gap = PicMoveUtils.gap;

    // 绑定对象
    private ActivitySelectPhotoBinding mBinding;
    // 图片结果显示列表
    private RecyclerView mSelPhotoRecycler;
    // 相册列表
    private RecyclerView mAlbumsListRecycler;
    // 是否初始化
    private boolean isInit = false;
    // 数据卷
    private MainPhotoViewModel mMainPhotoViewModel;
    // 主视图
    private ConstraintLayout mMainView;
    // 遮罩层
    private TextView masker;
    // 相册名
    private TextView albumName;
    // 完成按钮
    private TextView selOK;
    // 旋转的图标
    private TextView rtIcon;
    // 图片预览相关
    private Transferee transferee;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 去除title
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        MUtils.setTranslucentStatus(SelectPhotoActivity.this);
        MUtils.setAndroidNativeLightStatusBar(SelectPhotoActivity.this,true);
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_photo);
        mMainPhotoViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MainPhotoViewModel.class);
        mBinding.setMainPhotoViewModel(mMainPhotoViewModel);
        mBinding.setLifecycleOwner(SelectPhotoActivity.this);

        // UI初始化
        mMainView = findViewById(R.id.sp_page);
        masker = findViewById(R.id.sp_masker);
        albumName = findViewById(R.id.sp_album_name);
        selOK = findViewById(R.id.sp_ok);
        rtIcon = findViewById(R.id.sp_rt_icon);
        transferee = TransfereeUtils.getTransferee(SelectPhotoActivity.this,null,null);

        MUtils.setAllTypeFace(mMainView, SelectPhotoActivity.this);

        /**
         * 注入页面返回事件
         */
        mMainPhotoViewModel.setOnSelActivityBack(() -> {
            SelectPhotoActivity.this.finish();
        });
        /**
         * 注入结果返回事件
         */
        mMainPhotoViewModel.setOnGetResult(onGetResult);
        /**
         * 注入图片预览事件
         */
        mMainPhotoViewModel.setOnPhotoPreview(() -> {
            List<PhotoQueryEntity> selList = mMainPhotoViewModel.getSEL_PHOTO_LIST().getValue();
            List<String> collect = selList.stream().map(PhotoQueryEntity::getPath).collect(Collectors.toList());
            SelectPhotoActivity.this.showGallery(collect);
        });

        // 绑定监听者
        bindObserver();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (isInit) return;
            MUtils.goTimerDelay(() -> {
                afterInit();
                this.isInit = true;
            }, INIT_DELAY, SelectPhotoActivity.this);
        }
    }

    /**
     * 延后初始化的内容
     */
    private void afterInit() {
        mSelPhotoRecycler = findViewById(R.id.sp_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mSelPhotoRecycler.setLayoutManager(layoutManager);

        mSelectPhotoListAdapter.setCanSelect(true);
        mSelPhotoRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = gap;
                outRect.bottom = gap;
                if (parent.getChildLayoutPosition(view) % 4 == 0) {
                    outRect.left = 0;
                }
            }
        });
        mSelPhotoRecycler.setAdapter(mSelectPhotoListAdapter);
        mAlbumsListRecycler = findViewById(R.id.sp_albums_list_rec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mAlbumsListRecycler.setLayoutManager(linearLayoutManager);
        mAlbumsListRecycler.setAdapter(mAlbumListAdapter);
        if (mMainPhotoViewModel != null) mMainPhotoViewModel.getPhotoList();
    }

    /**
     * 注入结果返回事件
     */
    private Runnable onGetResult = () -> {
        Intent data = new Intent();
        ArrayList<PhotoQueryEntity> post_list = (ArrayList<PhotoQueryEntity>) mMainPhotoViewModel.getSEL_PHOTO_LIST().getValue();
        data.putExtra(SEL_PHOTO_LIST_TAG, (Serializable) post_list);
        SelectPhotoActivity.this.setResult(ON_SEL_PHOTO_RESULT_CODE,data);
        SelectPhotoActivity.this.finish();
    };

    /**
     * 选图recyclerView适配器
     */
    private SelectPhotoListAdapter mSelectPhotoListAdapter = new SelectPhotoListAdapter(new ArrayList<>(), PicMoveUtils.getBlockSize()) {
        @Override
        public void onHolderCustomer(SelectPhotoListAdapter.ViewHolder holder, int position) {
            if (mMainPhotoViewModel != null) {
                List<PhotoQueryEntity> photosList = mMainPhotoViewModel.getSHOW_SEL_ALBUM().getValue().getPhotos();
                PhotoQueryEntity photo = photosList.get(position);
                holder.selHandler.setOnClickListener(v -> {
                    photo.setSelected(!photo.getSelected());
                    holder.onSel(photo.getSelected());
                    if (mMainPhotoViewModel != null) {
                        mMainPhotoViewModel.notifySelList();
                    }
                });
                holder.image.setOnClickListener(v -> {
                    List<String> urls = photosList.stream().map(a -> a.getPath()).collect(Collectors.toList());
                    Log.i(TAG, "onHolderCustomer: ---> " + holder.image + " - " + position);
                    TransferConfig config = TransfereeUtils.getDefaultBuilder(SelectPhotoActivity.this)
                            .setSourceUrlList(urls)
                            .setIndexIndicator(new CusIndicator(getPhotoIndicator(photosList)))
                            .setNowThumbnailIndex(position)
                            .bindRecyclerView(mSelPhotoRecycler,R.id.spi_img);
                    transferee.apply(config).show();
                });
            }
        }
    };

    /**
     * 获取一个图片浏览的指示器
     * @param photosList
     * @return
     */
    private PhotoIndicator getPhotoIndicator(List<PhotoQueryEntity> photosList) {
        return new PhotoIndicator(SelectPhotoActivity.this, photosList, index -> {
            PhotoQueryEntity selItem = photosList.get(index);
            if (selItem != null) selItem.setSelected(!selItem.getSelected());
            mMainPhotoViewModel.notifySelList();
        }, (onClickView) -> {
            onGetResult.run();
        }, (onBackView) -> {
            transferee.dismiss();
        });
    }

    /**
     * 相册适配器
     */
    private AlbumListAdapter mAlbumListAdapter = new AlbumListAdapter(new ArrayList<>()) {
        @Override
        protected void onHolderCustomer(AlbumListAdapter.ViewHolder holder, int position) {
            holder.mainView.setOnClickListener(v -> {
                if (mMainPhotoViewModel != null) {
                    AlbumsEntity albumsEntity = mMainPhotoViewModel.getSHOW_ALBUMS().getValue().get(position);
                    mMainPhotoViewModel.getSHOW_SEL_ALBUM().setValue(albumsEntity);
                    mMainPhotoViewModel.getSHOW_ALBUMS_SEL_LIST().setValue(false);
                }
            });
        }
    };

    /**
     * 相册列表弹出动画
     * @param show
     */
    private void showAlbumsListAction(boolean show) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mMainView);

        Transition transition = new AutoTransition();
        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
        transition.setInterpolator(decelerateInterpolator);
        transition.setDuration(200);

        TransitionManager.beginDelayedTransition(mMainView, transition);

        constraintSet.clear(R.id.sp_albums_list);
        constraintSet.connect(R.id.sp_albums_list, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        constraintSet.connect(R.id.sp_albums_list, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);

        if (show) {
            constraintSet.connect(R.id.sp_albums_list, ConstraintSet.BOTTOM, R.id.sp_bottom_wrap, ConstraintSet.TOP);
        } else {
            constraintSet.connect(R.id.sp_albums_list, ConstraintSet.TOP, R.id.sp_bottom_wrap, ConstraintSet.TOP);
        }

        constraintSet.constrainWidth(R.id.sp_albums_list, mMainView.getWidth());
        constraintSet.constrainHeight(R.id.sp_albums_list, mMainView.getHeight() / 2);
        constraintSet.applyTo(mMainView);

    }

    /**
     * 监听是否显示相册列表
     */
    private Observer<Boolean> showAlbumsListObserver = (val) -> {
        showAlbumsListAction(val);
        masker.setVisibility(val ? View.VISIBLE : View.INVISIBLE);
        // 旋转那个图标
        ObjectAnimator animator3 = null;
        if (val) {
            animator3 = ObjectAnimator.ofFloat(rtIcon, "rotation", 360, 180);
        } else {
            animator3 = ObjectAnimator.ofFloat(rtIcon, "rotation", 180, 360);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator3);
        animatorSet.start();
    };

    /**
     * 监听当前显示的图片列表
     */
    private Observer<AlbumsEntity> showAlbumObserver = (album) -> {
        if (mSelectPhotoListAdapter != null && album != null) {
            mSelectPhotoListAdapter.setList(album.getPhotos());
            albumName.setText(album.getFolderName());
        }
        // recyclerview全部滚动回最顶部
        if (mAlbumsListRecycler != null) mAlbumsListRecycler.scrollToPosition(0);
        if (mSelPhotoRecycler != null) mSelPhotoRecycler.scrollToPosition(0);
    };

    /**
     * 监听相册列表
     */
    private Observer<List<AlbumsEntity>> showAlbumsObserver = (list) -> {
        if (mAlbumListAdapter != null) mAlbumListAdapter.refresh(list);
    };

    /**
     * 监听选中列表
     */
    private Observer<List<PhotoQueryEntity>> selPhotoList = (list) -> {
        String okText = TXT_OK_SELECT.replace("?", list.size()+"");
        selOK.setText(list.size() == 0 ? "" : okText);
        mSelectPhotoListAdapter.refresh();
    };

    /**
     * 显示相册预览的方法
     * @param urls
     */
    public void showGallery(List<String> urls) {
        if (urls == null || urls.size() == 0) return;
        List<PhotoQueryEntity> photosList = mMainPhotoViewModel.getSEL_PHOTO_LIST().getValue();
        View customView = getLayoutInflater().inflate(R.layout.layout_photo_foreground,null);
        TransferConfig config = TransfereeUtils.getDefaultBuilder(SelectPhotoActivity.this)
                .setSourceUrlList(urls)
                .setCustomView(customView)
                .setIndexIndicator(new CusIndicator(getPhotoIndicator(photosList)))
                .create(); // 是否启动列表随着页面的切换而滚动你的列表，默认关闭
        transferee.apply(config).show();
    }

    /**
     * 绑定监听者
     */
    private void bindObserver() {
        if (mMainPhotoViewModel != null) {
            mMainPhotoViewModel.getMAIN_LOADING().setValue(true);
            mMainPhotoViewModel.getSHOW_ALBUMS_SEL_LIST().observeForever(showAlbumsListObserver);
            mMainPhotoViewModel.getSHOW_SEL_ALBUM().observeForever(showAlbumObserver);
            mMainPhotoViewModel.getSHOW_ALBUMS().observeForever(showAlbumsObserver);
            mMainPhotoViewModel.getSEL_PHOTO_LIST().observeForever(selPhotoList);
        }
    }

    /**
     * 解绑监听者
     */
    private void unbindObserver() {
        if (mMainPhotoViewModel != null) {
            mMainPhotoViewModel.getSHOW_ALBUMS_SEL_LIST().removeObserver(showAlbumsListObserver);
            mMainPhotoViewModel.getSHOW_SEL_ALBUM().removeObserver(showAlbumObserver);
            mMainPhotoViewModel.getSHOW_ALBUMS().removeObserver(showAlbumsObserver);
            mMainPhotoViewModel.getSEL_PHOTO_LIST().removeObserver(selPhotoList);
        }
    }

    @Override
    protected void onDestroy() {
        if (transferee != null) transferee.destroy();
        super.onDestroy();
        // 绑定监听者
        unbindObserver();
    }

}
