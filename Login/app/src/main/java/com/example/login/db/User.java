package com.example.login.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class User extends SQLiteOpenHelper {
    public User(Context context) {
        // 数据库辅助类的构造方法，当数据库存在，则直接返回，没有则创建数据库，并调用onCreate()初始化数据库
        //上下文，数据库名称，游标工厂对象，当前数据库版本号；只传上下文环境参数，其余参数固定写死
        super(context,"User.db",null,1);
    }

    /**
     * 数据库第一次创建的时候调用，在此方法中创建数据表
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库中的表
        db.execSQL("create table user(_id integer primary key autoincrement ,username varchar(20),pwd varchar(5));");
    }

    /**
     * 当前数据库的版本号升级时调用的方法，
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
