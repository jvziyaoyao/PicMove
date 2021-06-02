package com.jvziyaoyao.picmove.domain.model;

import lombok.Data;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-03-21 17:54
 **/
@Data
public class UpgradeEntity {

    // 环境版本
    private Integer env;
    // 标题
    private String title;
    // 描述
    private String description;
    // 版本号
    private Integer version_code;
    // 版本名
    private String version_name;
    // 下载地址
    private String url;

}
