package com.example.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {
    /**
     * 流转换成字符串
     * @param is  流对象
     * @return      流转换成的字符串，返回null代表异常
     */
    public static String streamToString(InputStream is) throws IOException {

        //在读取的过程中，将读取的内容存储到缓存中，然后一次性转换成字符串返回
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //读流的操作，读到没有为止,每次读取1024字节
        byte[] buffer=new byte[1024];
        int b;
        while((b=is.read(buffer))!=-1){
            bos.write(buffer,0,b);  //写到bos中
        }
        is.close();
        bos.close();
        return bos.toString();
    }
}
