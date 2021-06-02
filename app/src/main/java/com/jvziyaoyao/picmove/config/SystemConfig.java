package com.jvziyaoyao.picmove.config;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jvziyaoyao.picmove.utils.ShareUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-03-20 20:52
 **/
public class SystemConfig {

    public static final String TAG = SystemConfig.class.getSimpleName();

    // 单例
    public static SystemConfig systemConfig;

    // 是否复制文件
    @Getter
    private Boolean IS_COPY_FILE = false;
    public void setIS_COPY_FILE(Boolean IS_COPY_FILE) {
        this.IS_COPY_FILE = IS_COPY_FILE;
        save();
    }

    // 是否自动更新
    @Getter
    private Boolean AUTO_UPDATE = true;
    public void setAUTO_UPDATE(Boolean AUTO_UPDATE) {
        this.AUTO_UPDATE = AUTO_UPDATE;
        save();
    }

    /**
     * 构造方法私有化
     */
    private SystemConfig() {}

    /**
     * 获取实例
     * @return
     */
    public static SystemConfig getInstance() {
        String json = ShareUtils.get(ShareUtils.SHARE_SYSTEM_CONFIG_KEY);
        Gson gson = new Gson();
        try {
            systemConfig = gson.fromJson(json, (Type) new TypeToken<SystemConfig>(){}.getClass());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        if (systemConfig == null) {
            systemConfig = new SystemConfig();
            ShareUtils.set(ShareUtils.SHARE_SYSTEM_CONFIG_KEY, gson.toJson(systemConfig));
        }
        return systemConfig;
    }

    /**
     * 持久化
     */
    public void save() {
        Gson gson = new Gson();
        ShareUtils.set(ShareUtils.SHARE_SYSTEM_CONFIG_KEY, gson.toJson(systemConfig));
        notifyListener();
    }

    /**
     * 保存通知模块
     */
    public interface OnSystemConfigSaveListener {
        void onSave();
    }
    private List<OnSystemConfigSaveListener> mSaveListenerList = new ArrayList<>();
    public void registerSaveListener(OnSystemConfigSaveListener listener) {
        if (listener != null) mSaveListenerList.add(listener);
    }
    public void notifyListener() {
        mSaveListenerList.forEach(l -> {
            if (l != null) l.onSave();
        });
    }

}
