package com.jvziyaoyao.picmove.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jvziyaoyao.picmove.R;
import com.jvziyaoyao.picmove.domain.model.PhotoQueryEntity;
import com.jvziyaoyao.picmove.utils.MUtils;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-02-19 22:36
 **/
public abstract class SelectPhotoListAdapter extends RecyclerView.Adapter<SelectPhotoListAdapter.ViewHolder> {

    public static final String TAG = SelectPhotoListAdapter.class.getSimpleName();

    // 数据列表
    private List<ListAdapterItem> list;
    // 单个item的大小
    private int gridSize;
    // 是否支持选择
    public boolean canSelect = false;

    public SelectPhotoListAdapter(List<PhotoQueryEntity> list, int gridSize) {
        setList(list);
        this.gridSize = gridSize;
    }

    /**
     * 更新列表
     * @param list
     */
    public void setList(List<PhotoQueryEntity> list) {
        this.list = list.stream().map(a -> {
            ListAdapterItem item = new ListAdapterItem();
            item.photo = a;
            return item;
        }).collect(Collectors.toList());
        this.list.forEach(e -> {
            Log.i(TAG, "setList: " + e.photo.getPath());
        });
        notifyDataSetChanged();
    }

    public class ListAdapterItem {
        PhotoQueryEntity photo;
        ViewHolder holder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_select_photo_item,parent,false);
        MUtils.setAllTypeFace(view,view.getContext());
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListAdapterItem item = list.get(position);
        PhotoQueryEntity photo = item.photo;
        item.holder = holder;
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = this.gridSize;
        holder.itemView.setLayoutParams(layoutParams);
        Glide.with(holder.itemView.getContext()).load(photo.getPath()).into(holder.image);
        holder.onSel(photo.getSelected());
        onHolderCustomer(holder, position);
    }

    public abstract void onHolderCustomer(ViewHolder holder, int position);

    /**
     * 刷新
     */
    public void refresh() {
        list.forEach(e -> {
            if (e.holder != null && e.photo != null) e.holder.onSel(e.photo.getSelected());
        });
    }

    /**
     * 修改是否支持选择
     * @param val
     */
    public void setCanSelect(boolean val) {
        this.canSelect = val;
        refresh();
    }

    @Override
    public int getItemCount() {
        return this.list == null ? 0 : this.list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mainView;
        public ImageView image;
        public ConstraintLayout selWrap;
        public ConstraintLayout selAbleWrap;
        public TextView selHandler;

        // 切换显示与不显示
        public void onSel(boolean sel) {
            if (canSelect) {
                selWrap.setVisibility(sel ? View.VISIBLE : View.INVISIBLE);
                selAbleWrap.setVisibility(sel ? View.INVISIBLE : View.VISIBLE);
            } else {
                selWrap.setVisibility(View.INVISIBLE);
                selAbleWrap.setVisibility(View.INVISIBLE);
            }
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainView = itemView;
            image = itemView.findViewById(R.id.spi_img);
            selWrap = itemView.findViewById(R.id.spi_sel_wrap);
            selAbleWrap = itemView.findViewById(R.id.spi_sel_able);
            selHandler = itemView.findViewById(R.id.spi_sel_handler);
        }
    }

}
