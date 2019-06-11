package com.example.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.db.BlackNumberOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作数据库的类，对数据库进行增删改查
 */
public class BlackNumberDao {
    private BlackNumberOpenHelper blackNumberOpenHelper;
    public BlackNumberDao(Context context){
        blackNumberOpenHelper=new BlackNumberOpenHelper(context);
   }

    /**
     * 添加一个条目
     * @param phone 拦截的电话号
     * @param mode  拦截的类型（短信 电话 所有）
     */
    public void insert(String phone,String mode){
        //开启数据库，准备做写入操作
        SQLiteDatabase db=blackNumberOpenHelper.getWritableDatabase();
        //创建ContentValues对象封装键值对
        ContentValues values=new ContentValues();
        //要插入的字段名和字段值
        values.put("phone",phone);
        values.put("mode",mode);
        //插入数据（表名，字段没有值时把字段维护成null,内容值）
        db.insert("blacknumber",null,values);
        //关闭数据库
        db.close();
    }

    /**
     * 从数据库删除一条数据
     * @param phone  删除的电话号码
     */
    public void delete(String phone){
        //开启数据库，
        SQLiteDatabase db=blackNumberOpenHelper.getWritableDatabase();
        //插入数据（表名，要删除的字段,传入要删除的内容具体值 ）
        db.delete("blacknumber","phone=?",new String[]{phone});
        //关闭数据库
        db.close();
    }

    /**
     * 根据电话号码，修改拦截模式
     * @param phone 更新拦截模式的电话号码
     * @param mode  要更新的模式
     */
    public void  update(String phone,String mode){
        //开启数据库，
        SQLiteDatabase db=blackNumberOpenHelper.getWritableDatabase();
        //创建ContentValues对象封装键值对
        ContentValues values=new ContentValues();
        //要修改的字段名和字段值
        values.put("mode",mode);
        //修改数据（表名，要修改的字段名和字段值，根据字段名更新，根据字段名的具体值来更新）
        db.update("blacknumber",values,"phone=?",new String[]{phone});
        //关闭数据库
        db.close();
    }

    /**
     * 查询所有号码
     */
    public List<BlackNumberInfo> findAll(){
        //开启数据库，
        SQLiteDatabase db=blackNumberOpenHelper.getWritableDatabase();
        //查询所有号码（表名，查询号码和类型，查询条件没有--null,为了后添加的在ListView上面所以倒叙排序id）
        Cursor cursor=db.query("blacknumber",new String[]{"phone","mode"},null,null,null,null,"_id desc");
        List<BlackNumberInfo> blackNumberList=new ArrayList<>();
        //如果游标能往下移动
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
            //游标没向下移动一次，就获取索引为0，索引为1的字段（也就是phone字段和mode字段）赋值给BlackNumberInfo对象
            blackNumberInfo.setPhone(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            //把BlackNumberInfo添加到集合中
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        //关闭数据库
        db.close();
        return blackNumberList;
    }
    //查找数据库中的一条号码，（其返回值用来判断数据库是否存在该号码）
    public boolean find(String phone){
        //默认没有该数据
        boolean result=false;
        //开启数据库，
        SQLiteDatabase db=blackNumberOpenHelper.getWritableDatabase();
        //查询所有号码（表名，查询号码，查询条件没有--null）
        Cursor cursor=db.query("blacknumber",new String[]{"phone"},null,null,null,null,null);
        //如果游标能往下移动
        while (cursor.moveToNext()){
            //遍历Cursor对象，并且跟传入的phone进行比较,如果相同就返回true,说明数据库存在该数据
            if(phone.equals(cursor.getString(0))){
                result=true;
            }
        }
        /*
        一定要关闭游标，回收游标对象
        */
        cursor.close();
        return result;
    }

    /**
     * @param phone  作为查询条件的电话号码
     * @return  返回改号码的拦截模式
     */
    public String getMode(String phone){
        String mode=null;
        //开启数据库，
        SQLiteDatabase db=blackNumberOpenHelper.getWritableDatabase();
        //根据电话号码查询拦截模式（表名，要查询的mode，条件是根据电话号码）
        Cursor cursor=db.query("blacknumber",new String[]{"mode"},"phone=?",new String[]{phone},null,null,null);
        //如果游标能往下移动
        if (cursor.moveToNext()){
            //遍历Cursor对象，获取到索引为0的
            mode=cursor.getString(0);
        }
        /*
        一定要关闭游标，回收游标对象
        */
        cursor.close();
        return mode;
    }
}
