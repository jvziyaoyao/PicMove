package com.jvziyaoyao.picmove.domain.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

import lombok.Data;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-03-20 23:48
 **/
@Data
@Entity(nameInDb = "PM_PHOTO")
public class PhotoEntity implements Parcelable {

    @Id
    @Property(nameInDb = "ID")
    private long id;

    @Unique
    @Property(nameInDb = "PATH")
    private String path;

    @Property(nameInDb = "NAME")
    private String name;

    @Property(nameInDb = "SIZE")
    private long size;

    @Property(nameInDb = "TIME")
    private long time;

    public PhotoEntity() {}

    protected PhotoEntity(Parcel in) {
        id = in.readLong();
        path = in.readString();
        name = in.readString();
        size = in.readLong();
        time = in.readLong();
    }

    @Generated(hash = 785765887)
    public PhotoEntity(long id, String path, String name, long size, long time) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.size = size;
        this.time = time;
    }

    public static final Creator<PhotoEntity> CREATOR = new Creator<PhotoEntity>() {
        @Override
        public PhotoEntity createFromParcel(Parcel in) {
            return new PhotoEntity(in);
        }

        @Override
        public PhotoEntity[] newArray(int size) {
            return new PhotoEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(path);
        parcel.writeString(name);
        parcel.writeLong(size);
        parcel.writeLong(time);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
