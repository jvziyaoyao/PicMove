package com.jvziyaoyao.picmove.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jvziyaoyao.picmove.R;
import com.jvziyaoyao.picmove.databinding.FragmentMainPhotoBinding;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-02-21 22:59
 **/
public class MainPhotosSelFragment extends Fragment {

    // 绑定数据卷
    private FragmentMainPhotoBinding mBinding;
    // 主视图
    private View mMainView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_photo, container, false);
        mBinding.setLifecycleOwner(getActivity());
        mMainView = (ConstraintLayout) mBinding.getRoot();

        return mMainView;
    }

}
