package com.example.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
    /**
     * 给指定字符串按md5加密
     * @param pwd 需要加密的密码
     */
    public static String encoder(String pwd) {
        try {
            //对密码进行“加盐”
            String pwd2=pwd+"dfgfghdfsghrjaodgnmvns";
            //1.指定加密算法类型
            MessageDigest digest=MessageDigest.getInstance("MD5");
            //2.将需要加密的字符串转换成byte类型数组，然后进行随机哈希过程
            byte[] result=digest.digest(pwd2.getBytes());
            //3.循环遍历result，然后让其生成32位的字符串，固定写法
            //拼接字符串过程
            StringBuffer sb=new StringBuffer();
            for (int i=0;i<result.length;i++) {
                int number=result[i]&0xff;
                //将10进制的number转换成十六进制数据
                String str=Integer.toHexString(number);
                //判断加密后的字符的长度，如果长度位1，则在该字符前面补0
                if(str.length()==1){
                    str= "0"+str;
                }else {
                    sb.append(str);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
