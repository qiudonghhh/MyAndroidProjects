package com.example.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;

import com.example.db.dao.BlackNumberDao;

public class BlackNumberService extends Service {

    private InnerSmsReceiver innerSmsReceiver;
    private BlackNumberDao mDao;

    /**
     * 服务创建时调用
     */
    public void onCreate(){
        /**
         * 拦截短信原理
         * 短信在接收时候，广播发送，监听广播接收者，拦截短信
         */
        //创建广播接收者
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        innerSmsReceiver = new InnerSmsReceiver();
        registerReceiver(innerSmsReceiver,intentFilter);
        super.onCreate();

    }
    class  InnerSmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信内容，获取发送短信电话号码，如果次电话号码也在黑名单中，并且拦截模式为短信和所有时，拦截短信
            //1.获取短信内容
            Object[] objects= (Object[]) intent.getExtras().get("pdus");
            //2.循环遍历短信
            for (Object object:objects) {
                //3.获取短信对象
                SmsMessage sms=SmsMessage.createFromPdu((byte[])object);
                //4.获取短信基本信息
                String originatingAddress=sms.getOriginatingAddress();
                String messageBody=sms.getMessageBody();

                BlackNumberDao mDao= new BlackNumberDao(getApplicationContext());
                String mode=mDao.getMode(originatingAddress);
                if("短信".equals(mode)||"所有".equals(mode)){
                    //拦截短信,中断广播
                    abortBroadcast();
                }

            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if(innerSmsReceiver!=null){
            unregisterReceiver(innerSmsReceiver);
        }
        super.onDestroy();
    }
}
