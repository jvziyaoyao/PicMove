package com.jvziyaoyao.picmove.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jvziyaoyao.picmove.BuildConfig;
import com.jvziyaoyao.picmove.PicMoveApplication;
import com.jvziyaoyao.picmove.R;
import com.jvziyaoyao.picmove.adapter.SettingsItemAdapter;
import com.jvziyaoyao.picmove.databinding.ActivitySettingsBinding;
import com.jvziyaoyao.picmove.store.SettingsViewModel;
import com.jvziyaoyao.picmove.utils.MUtils;
import com.jvziyaoyao.picmove.utils.PicMoveUtils;
import com.lxj.xpopup.XPopup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-03-18 22:50
 **/
public class SettingsActivity extends AppCompatActivity {

    public static final String TAG = SettingsActivity.class.getSimpleName();

    public static final String TXT_REINSERT_FILE = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_REINSERT_FILE);
    public static final String TXT_REINSERT_WHEN_TOP_UP = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_REINSERT_WHEN_TOP_UP);
    public static final String TXT_NOTICE = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_NOTICE);
    public static final String TXT_IT_WILL_CAST_TIME = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_IT_WILL_CAST_TIME);
    public static final String TXT_CANCEL = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_CANCEL);
    public static final String TXT_CONFIRM = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_CONFRIM);
    public static final String TXT_AUTO_UPDATE = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_AUTO_UPDATE);
    public static final String TXT_AUTO_UPDATE_DESC = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_AUTO_UPDATE_DESC);
    public static final String TXT_WILL_NOT_AUTO_UPDATE_IF_CANCEL = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_WILL_NOT_AUTO_UPDATE_IF_CANCEL);
    public static final String TXT_CHECK_VERSION = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_CHECK_VERSION);
    public static final String TXT_CURRENT_VERSION = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_CURRENT_VERSION);
    public static final String TXT_CHECKING_VERSION = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_CHECKING_VERSION);
    public static final String TXT_IS_UP_TO_DATE = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_IS_UP_TO_DATE);
    public static final String TXT_NEW_VERSION_RELEASE = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_NEW_VERSION_RELEASE);
    public static final String TXT_DO_YOU_WANT_TO_UPDATE = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_DO_YOU_WANT_TO_UPDATE);
    public static final String TXT_DOWNLOAD = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_DOWNLOAD);
    public static final String TXT_CURRENT_VERSION_IS = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_CURRENT_VERSION_IS);
    public static final String TXT_FEEDBACK = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_FEEDBACK);
    public static final String TXT_FEEDBACK_TO_DEVELOPER = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_AS_FEEDBACK_TO_DEVELOPER);

    // 数据绑定对象
    private ActivitySettingsBinding mBinding;
    // 数据卷
    private SettingsViewModel mSettingsViewModel;
    // 主视图
    private ConstraintLayout mMainView;
    // 滚动视图
    private RecyclerView mSettingListRecycler;
    // 滚动视图适配器
    private SettingsItemAdapter mSettingsItemAdapter;
    // 设置列表
    private List<SettingItem> mSettingList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 去除title
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        MUtils.setTranslucentStatus(SettingsActivity.this);
        MUtils.setAndroidNativeLightStatusBar(SettingsActivity.this,true);
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        mSettingsViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(SettingsViewModel.class);
        mBinding.setSettingsViewModel(mSettingsViewModel);
        mBinding.setLifecycleOwner(this);

        mMainView = findViewById(R.id.setting_page);
        MUtils.setAllTypeFace(mMainView,SettingsActivity.this);

        initSettingsList();

        mSettingsViewModel.setOnBack(this::finish);

    }

    /**
     * 初始化循环列表
     */
    private void initSettingsList() {
        mSettingList = new ArrayList<>();
        // 复制文件
        mSettingList.add(new SettingItem(SettingItem.SETTING_ITEM_TYPE_SWITCH, TXT_REINSERT_FILE, TXT_REINSERT_WHEN_TOP_UP, (it, holder) -> {
            Boolean val = !((Boolean) it.data);
            Consumer<Boolean> onSet = need -> {
                it.data = need;
                PicMoveApplication.getMSystemConfig().setIS_COPY_FILE(need);
                holder.sw.setChecked(need);
            };
            if (val) {
                new XPopup.Builder(SettingsActivity.this)
                        .asConfirm(TXT_NOTICE, TXT_IT_WILL_CAST_TIME, TXT_CANCEL, TXT_CONFIRM,() -> {
                            onSet.accept(true);
                        },() -> {

                        },false)
                        .show();
            } else {
                onSet.accept(false);
            }
        }, it -> {
            it.data = PicMoveApplication.getMSystemConfig().getIS_COPY_FILE();
        }));
        // 自动更新
        mSettingList.add(new SettingItem(SettingItem.SETTING_ITEM_TYPE_SWITCH, TXT_AUTO_UPDATE, TXT_AUTO_UPDATE_DESC, (it, holder) -> {
            Boolean val = !((Boolean) it.data);
            Consumer<Boolean> onSet = need -> {
                it.data = need;
                PicMoveApplication.getMSystemConfig().setAUTO_UPDATE(need);
                holder.sw.setChecked(need);
            };
            if (!val) {
                new XPopup.Builder(SettingsActivity.this)
                        .asConfirm(TXT_NOTICE, TXT_WILL_NOT_AUTO_UPDATE_IF_CANCEL,TXT_CANCEL,TXT_CONFIRM,() -> {
                            onSet.accept(false);
                        },() -> {},false)
                        .show();
            } else {
                onSet.accept(true);
            }
        }, it -> {
            it.data = PicMoveApplication.getMSystemConfig().getAUTO_UPDATE();
        }));
        // 检查更新
        mSettingList.add(new SettingItem(SettingItem.SETTING_ITEM_TYPE_WHITE, TXT_CHECK_VERSION, TXT_CURRENT_VERSION + BuildConfig.VERSION_NAME, (it, holder) -> {
            holder.desc.setText(TXT_CHECKING_VERSION);
            PicMoveUtils.checkVersion(res -> {
                if (BuildConfig.VERSION_CODE == res.getVersion_code()) {
                    holder.desc.setText(TXT_IS_UP_TO_DATE);
                } else {
                    String newVersionRelease = TXT_NEW_VERSION_RELEASE.replace("?", res.getVersion_name());
                    holder.desc.setText(newVersionRelease);
                    new XPopup.Builder(SettingsActivity.this)
                            .asConfirm(TXT_NOTICE, TXT_DO_YOU_WANT_TO_UPDATE,TXT_CANCEL, TXT_DOWNLOAD,() -> {
                                PicMoveUtils.getDown(SettingsActivity.this,res.getUrl());
                            },() -> {

                            }, false)
                            .show();
                }
            }, msg -> {
                String currentVersionIs = TXT_CURRENT_VERSION_IS.replace("?",BuildConfig.VERSION_NAME);
                holder.desc.setText(currentVersionIs);
            });
        },it -> {

        }));
        // 用户反馈
        mSettingList.add(new SettingItem(SettingItem.SETTING_ITEM_TYPE_RIGHT, TXT_FEEDBACK, TXT_FEEDBACK_TO_DEVELOPER,(it, holder) -> {
            Intent intent = new Intent(SettingsActivity.this,FeedbackActivity.class);
            startActivity(intent);
        },it -> {

        }));

        mSettingListRecycler = findViewById(R.id.setting_list);
        mSettingsItemAdapter = new SettingsItemAdapter(mSettingList);
        mSettingListRecycler.setLayoutManager(new LinearLayoutManager(SettingsActivity.this));
        mSettingListRecycler.setAdapter(mSettingsItemAdapter);
        checkSettingsValue();
    }

    /**
     * 更新循环列表数据
     */
    private void checkSettingsValue() {
        mSettingList.forEach(item -> {
            if (item.onCheck != null) item.onCheck.accept(item);
        });
    }

    /**
     * 设置项列表
     */
    public class SettingItem {

        public static final int SETTING_ITEM_TYPE_SWITCH = 0;
        public static final int SETTING_ITEM_TYPE_RIGHT = 1;
        public static final int SETTING_ITEM_TYPE_WHITE = 2;

        // 类型
        @Getter
        private Integer itemType;
        // 标题
        @Getter
        private String title;
        // 描述
        @Getter
        private String desc;
        // 点击事件
        @Getter
        private BiConsumer<SettingItem, SettingsItemAdapter.ViewHolder> onClick;
        // 更新事件
        private Consumer<SettingItem> onCheck;
        // 绑定数据
        @Getter
        @Setter
        private Object data;

        public SettingItem(
                Integer itemType,
                String title,
                String desc,
                @NotNull BiConsumer<SettingItem, SettingsItemAdapter.ViewHolder> onClick,
                @Nullable Consumer<SettingItem> onCheck
        ) {
            this.itemType = itemType;
            this.title = title;
            this.desc = desc;
            this.onClick = onClick;
            this.onCheck = onCheck;
        }

    }

}
