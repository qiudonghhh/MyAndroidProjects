package com.example.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    /**
     * @param ctx  上下文环境
     * @param msg  打印的文本内容
     * @param duration  显示时长，传0代表短，传1代表长
     */
    //打印吐司
    public static void show(Context ctx, String msg,int duration){
        Toast.makeText(ctx,msg,duration).show();
    }
}
