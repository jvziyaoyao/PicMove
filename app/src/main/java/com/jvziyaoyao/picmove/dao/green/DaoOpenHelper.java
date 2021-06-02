package com.jvziyaoyao.picmove.dao.green;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.jvziyaoyao.picmove.dao.DaoMaster;
import com.jvziyaoyao.picmove.dao.PhotoEntityDao;
import com.jvziyaoyao.picmove.dao.UserEntityDao;

import org.greenrobot.greendao.database.Database;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-03-20 18:21
 **/
public class DaoOpenHelper extends DaoMaster.DevOpenHelper {

    public DaoOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, UserEntityDao.class, PhotoEntityDao.class);
    }
}
