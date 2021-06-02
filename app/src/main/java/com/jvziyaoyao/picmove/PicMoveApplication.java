package com.jvziyaoyao.picmove;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.jvziyaoyao.picmove.config.SystemConfig;
import com.jvziyaoyao.picmove.dao.DaoMaster;
import com.jvziyaoyao.picmove.dao.DaoSession;
import com.jvziyaoyao.picmove.dao.green.DaoOpenHelper;

import lombok.Getter;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-02-21 17:42
 **/
public class PicMoveApplication extends Application {

    public static final String TAG = PicMoveApplication.class.getSimpleName();

    @Getter
    private static PicMoveApplication application;

    @Getter
    private static DaoSession mDaoSession;

    @Getter
    private static SystemConfig mSystemConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        mSystemConfig = SystemConfig.getInstance();
        // 数据库初始化
        initDB();
    }

    /**
     * 数据库初始化
     */
    private void initDB() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoOpenHelper(this,BuildConfig.DB_NAME, null);
        SQLiteDatabase db = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

}
