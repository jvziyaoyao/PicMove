package com.jvziyaoyao.picmove.store;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.jvziyaoyao.picmove.dao.green.DaoManager;
import com.jvziyaoyao.picmove.dao.UserEntityDao;
import com.jvziyaoyao.picmove.domain.entity.UserEntity;
import com.jvziyaoyao.picmove.utils.MUtils;

import java.util.List;

import lombok.Setter;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-03-18 23:00
 **/
public class SettingsViewModel extends ViewModel {

    public static final String TAG = SettingsViewModel.class.getSimpleName();

    // 返回方法注入
    @Setter
    private Runnable onBack;

    /**
     * 返回按钮
     * @param view
     */
    public void onBack(View view) {
        if (onBack != null) onBack.run();
    }

    /**
     * 插入数据测试
     */
//    public void onDaoInsert(View view) {
//        UserEntityDao mUserEntityDao = DaoManager.getInstance().getMUserEntityDao();
//        UserEntity userEntity = new UserEntity();
//        userEntity.setAge(17);
//        userEntity.setUserName("Jason");
//        userEntity.setUserId(MUtils.getUUID32());
//        mUserEntityDao.insert(userEntity);
//        Toast.makeText(view.getContext(),"数据插入成功！",Toast.LENGTH_LONG).show();
//    }

    /**
     * 查询测试
     * @param view
     */
    public void onDaoQuery(View view) {
        UserEntityDao mUserEntityDao = DaoManager.getInstance().getMUserEntityDao();
        List<UserEntity> list = mUserEntityDao.loadAll();
        Log.i(TAG, "onDaoQuery: ---> xxxxxxxxxxxxxxxxxxxxxxx --->");
        list.forEach(u -> {
            Log.i(TAG, "onDaoQuery: ---> " + u);
        });
    }

}
