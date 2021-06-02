package com.jvziyaoyao.picmove.domain.model;

import com.jvziyaoyao.picmove.domain.entity.PhotoEntity;

import java.io.Serializable;

import lombok.Data;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-02-19 20:44
 **/
@Data
public class PhotoQueryEntity implements Serializable {

    private long id;
    private String name;
    private String path;
    private long size;
    private long time;
    private Boolean selected = false;

    public PhotoQueryEntity() {

    }

    /**
     * 数据库实体转query实体
     * @param photoEntity
     * @return
     */
    public static PhotoQueryEntity photo2QueryEntity(PhotoEntity photoEntity) {
        if (photoEntity == null) return null;
        PhotoQueryEntity photoQueryEntity = new PhotoQueryEntity();
        photoQueryEntity.setId(photoEntity.getId());
        photoQueryEntity.setName(photoEntity.getName());
        photoQueryEntity.setPath(photoEntity.getPath());
        photoQueryEntity.setTime(photoEntity.getTime());
        photoQueryEntity.setSize(photoEntity.getSize());
        return photoQueryEntity;
    }

    public static PhotoEntity query2PhotoEntity(PhotoQueryEntity photoQueryEntity) {
        if (photoQueryEntity == null) return null;
        PhotoEntity photoEntity = new PhotoEntity();
        photoEntity.setId(photoQueryEntity.getId());
        photoEntity.setName(photoQueryEntity.getName());
        photoEntity.setPath(photoQueryEntity.getPath());
        photoEntity.setTime(photoQueryEntity.getTime());
        photoEntity.setSize(photoQueryEntity.getSize());
        return photoEntity;
    }

}
