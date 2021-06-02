package com.jvziyaoyao.picmove.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.jvziyaoyao.picmove.R;
import com.jvziyaoyao.picmove.activity.SettingsActivity;
import com.jvziyaoyao.picmove.utils.MUtils;

import java.util.List;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-03-20 20:29
 **/
public class SettingsItemAdapter extends RecyclerView.Adapter<SettingsItemAdapter.ViewHolder> {

    // 设置项列表
    private List<SettingsActivity.SettingItem> list;

    public SettingsItemAdapter(List<SettingsActivity.SettingItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_settings_item,parent,false);
        MUtils.setAllTypeFace(view,view.getContext());
        SettingsItemAdapter.ViewHolder holder = new SettingsItemAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingsActivity.SettingItem item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.desc.setText(item.getDesc());
        switch (item.getItemType()) {
            case SettingsActivity.SettingItem.SETTING_ITEM_TYPE_SWITCH:
                holder.switch_wrap.setVisibility(View.VISIBLE);
                Boolean check = (Boolean) item.getData();
                holder.sw.setChecked(check);
                break;
            case SettingsActivity.SettingItem.SETTING_ITEM_TYPE_RIGHT:
                holder.right_wrap.setVisibility(View.VISIBLE);
                break;
            case SettingsActivity.SettingItem.SETTING_ITEM_TYPE_WHITE:
                break;
        }
        holder.mainView.setOnClickListener(v -> {
            if (item.getOnClick() != null) item.getOnClick().accept(item,holder);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // 主视图
        public ConstraintLayout mainView;
        // 标题
        public TextView title;
        // 描述
        public TextView desc;
        // 开关
        public SwitchCompat sw;
        // 箭头容器
        public LinearLayout right_wrap;
        // 切换容器
        public LinearLayout switch_wrap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainView = itemView.findViewById(R.id.si_page);
            title = itemView.findViewById(R.id.si_title);
            desc = itemView.findViewById(R.id.si_desc);
            right_wrap = itemView.findViewById(R.id.si_right_wrap);
            switch_wrap = itemView.findViewById(R.id.si_switch_wrap);
            sw = itemView.findViewById(R.id.si_switch);
        }
    }


}
