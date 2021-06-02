package com.jvziyaoyao.picmove.dao.green;

import com.jvziyaoyao.picmove.PicMoveApplication;
import com.jvziyaoyao.picmove.dao.PhotoEntityDao;
import com.jvziyaoyao.picmove.dao.UserEntityDao;

import lombok.Getter;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-03-20 17:12
 **/
public class DaoManager {

    private static DaoManager mDaoManager;

    @Getter
    private UserEntityDao mUserEntityDao;

    @Getter
    private PhotoEntityDao mPhotoEntityDao;

    private DaoManager() {
        mUserEntityDao = PicMoveApplication.getMDaoSession().getUserEntityDao();
        mPhotoEntityDao = PicMoveApplication.getMDaoSession().getPhotoEntityDao();
    }

    public static DaoManager getInstance() {
        if (mDaoManager == null) {
            mDaoManager = new DaoManager();
        }
        return mDaoManager;
    }

}
