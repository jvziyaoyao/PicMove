package com.jvziyaoyao.picmove.domain.model;

import java.util.List;

import lombok.Data;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-02-21 16:57
 **/
@Data
public class AlbumsEntity {

    private String folderPath;
    private String folderName;

    private List<PhotoQueryEntity> photos;

}
