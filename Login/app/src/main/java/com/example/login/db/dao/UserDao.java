package com.example.login.db.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.login.db.User;

/**
 * 操作数据库的类，对数据库进行增删改查
 */
public class UserDao {
    private UserDao mDao;
    private final User user;

    public UserDao(Context context){
        user = new User(context);
    }

    public void insert(String username,String pwd){
        //开启数据库，准备做写入操作
        SQLiteDatabase db=user.getWritableDatabase();
        //创建ContentValues对象封装键值对
        ContentValues values=new ContentValues();
        //要插入的字段名和字段值
        values.put("username",username);
        values.put("pwd",pwd);
        //插入数据（表名，字段没有值时把字段维护成null,内容值）
        db.insert("user",null,values);
        //关闭数据库
        db.close();
    }





    //查找数据库中的一条号码，（其返回值用来判断数据库是否存在该号码）
    public boolean find(String username,String pwd){
        //默认没有该数据
        boolean result=false;
        //开启数据库，
        SQLiteDatabase db=user.getWritableDatabase();
        //查询所有号码（表名，查询号码，查询条件没有--null）
        Cursor cursor=db.query("user",new String[]{"username","pwd"},null,
                null,null,null,null);
        //如果游标能往下移动
        while (cursor.moveToNext()){
            //遍历Cursor对象，并且跟传入的phone进行比较,如果相同就返回true,说明数据库存在该数据
            if(username.equals(cursor.getString(0))){
                if(pwd.equals(cursor.getString(1))){
                    result= true;
                }
            }
        }
        /*
        一定要关闭游标，回收游标对象
        */
        cursor.close();
        return result;
    }


}

