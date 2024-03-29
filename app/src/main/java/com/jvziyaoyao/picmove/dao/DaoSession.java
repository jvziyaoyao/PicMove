package com.jvziyaoyao.picmove.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.jvziyaoyao.picmove.domain.entity.PhotoEntity;
import com.jvziyaoyao.picmove.domain.entity.UserEntity;

import com.jvziyaoyao.picmove.dao.PhotoEntityDao;
import com.jvziyaoyao.picmove.dao.UserEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig photoEntityDaoConfig;
    private final DaoConfig userEntityDaoConfig;

    private final PhotoEntityDao photoEntityDao;
    private final UserEntityDao userEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        photoEntityDaoConfig = daoConfigMap.get(PhotoEntityDao.class).clone();
        photoEntityDaoConfig.initIdentityScope(type);

        userEntityDaoConfig = daoConfigMap.get(UserEntityDao.class).clone();
        userEntityDaoConfig.initIdentityScope(type);

        photoEntityDao = new PhotoEntityDao(photoEntityDaoConfig, this);
        userEntityDao = new UserEntityDao(userEntityDaoConfig, this);

        registerDao(PhotoEntity.class, photoEntityDao);
        registerDao(UserEntity.class, userEntityDao);
    }
    
    public void clear() {
        photoEntityDaoConfig.clearIdentityScope();
        userEntityDaoConfig.clearIdentityScope();
    }

    public PhotoEntityDao getPhotoEntityDao() {
        return photoEntityDao;
    }

    public UserEntityDao getUserEntityDao() {
        return userEntityDao;
    }

}
