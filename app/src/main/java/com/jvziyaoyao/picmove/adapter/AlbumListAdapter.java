package com.jvziyaoyao.picmove.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jvziyaoyao.picmove.R;
import com.jvziyaoyao.picmove.domain.model.AlbumsEntity;
import com.jvziyaoyao.picmove.domain.model.PhotoQueryEntity;
import com.jvziyaoyao.picmove.store.MainPhotoViewModel;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.Setter;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-02-22 20:15
 **/
public abstract class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.ViewHolder> {

    // 数据列表
    private List<AlbumsEntity> list;

    public AlbumListAdapter(List<AlbumsEntity> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_albums_list_item, parent,false);
        AlbumListAdapter.ViewHolder holder = new AlbumListAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pL = holder.mainView.getContext().getResources().getDimensionPixelOffset(R.dimen.pL);
        int ps = holder.mainView.getContext().getResources().getDimensionPixelOffset(R.dimen.ps);
        AlbumsEntity albumsEntity = list.get(position);
        int count = albumsEntity.getPhotos().size();
        String name = albumsEntity.getFolderName();
        String path = albumsEntity.getPhotos().get(0).getPath();
        if (position == 0) {
            holder.mainView.setPadding(pL,pL,pL,ps);
        } else if (position == list.size()-1) {
            holder.mainView.setPadding(pL,ps,pL,pL);
        }
        holder.name.setText(name);
        holder.count.setText(count + "");
        Glide.with(holder.itemView.getContext()).load(path).into(holder.img);

        onHolderCustomer(holder, position);
    }

    protected abstract void onHolderCustomer(ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    /**
     * 刷新
     * @param list
     */
    public void refresh(List<AlbumsEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mainView;
        private ImageView img;
        private TextView name;
        private TextView count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainView = itemView;
            img = mainView.findViewById(R.id.ali_img);
            name = mainView.findViewById(R.id.ali_name);
            count = mainView.findViewById(R.id.ali_count);
        }
    }

}
