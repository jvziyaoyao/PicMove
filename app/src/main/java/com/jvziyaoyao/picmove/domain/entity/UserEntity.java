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
 * @create: 2021-03-20 16:47
 **/
@Data
@Entity(nameInDb = "PM_USER")
public class UserEntity implements Parcelable {

    @Id(autoincrement = true)
    @Property(nameInDb = "ID")
    private Long id;

    @Unique
    @Property(nameInDb = "USER_ID")
    private String userId;

    @Property(nameInDb = "USER_NAME")
    private String userName;

    @Property(nameInDb = "AGE")
    private Integer age;


    protected UserEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        userId = in.readString();
        userName = in.readString();
        if (in.readByte() == 0) {
            age = null;
        } else {
            age = in.readInt();
        }
    }

    @Generated(hash = 150363056)
    public UserEntity(Long id, String userId, String userName, Integer age) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.age = age;
    }

    @Generated(hash = 1433178141)
    public UserEntity() {
    }

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel in) {
            return new UserEntity(in);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeString(userId);
        parcel.writeString(userName);
        if (age == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(age);
        }
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
