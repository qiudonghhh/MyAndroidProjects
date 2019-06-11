package com.example.shoujiweishi;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class SettingCentreActivity extends Activity {

    private CheckBox cb_box;
    private TextView tv_des;
    private CheckBox cb_blacknumber;
    private TextView tv_blacknumber;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //更新开启与关闭
        intiUpdate();
        //黑名单拦截开启与关闭
        initBlacknumber();
    }


    /**
     * 更新开启与关闭状态
     */
    private void intiUpdate() {
        tv_des = findViewById(R.id.tv_des);
        cb_box = findViewById(R.id.cb_box);
        //获取Sd卡中的config文件，文件类型MODE_PRIVATE，没有该文件就创建
        sp=getSharedPreferences("config",MODE_PRIVATE);
        //初始化UI，获取config文件中的键值对，如果没有该键，默认值为true，
        boolean autoupdate= sp.getBoolean("autoupdate",true);
        if(autoupdate){
            tv_des.setText("自动更新已开启");
            tv_des.setTextColor(Color.BLACK);
            //checkbiox时勾选状态
            cb_box.setChecked(true);
        }else {
            tv_des.setText("自动更新已关闭");
            tv_des.setTextColor(Color.RED);
            //checkbiox时未勾选状态
            cb_box.setChecked(false);
        }
        //checkbox状态改变
        upDateState();
    }

    private void upDateState() {
        cb_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //获取Editor对象，该对象可以向sp中存储数据
                SharedPreferences.Editor editor=sp.edit();
                //将键值对存储到sp文件中
                editor.putBoolean("autoupdate",isChecked);
                //提交数据到sp
                editor.commit();
                if(isChecked){
                    tv_des.setText("自动更新已开启");
                    tv_des.setTextColor(Color.BLACK);
                }else {
                    tv_des.setText("自动更新已关闭");
                    tv_des.setTextColor(Color.RED);
                }
            }
        });
    }

    /**
     * 黑名单开启与关闭状态
     */
    private void initBlacknumber() {
        tv_blacknumber = findViewById(R.id.tv_blacknumber);
        cb_blacknumber = findViewById(R.id.cb_blacknumber);
        //获取Sd卡中的config文件，文件类型MODE_PRIVATE，没有该文件就创建
        sp=getSharedPreferences("config",MODE_PRIVATE);
        //初始化UI，获取config文件中的键值对，如果没有该键，默认值为true，
        boolean autoblacknumber= sp.getBoolean("autoblacknumber",true);
        if(autoblacknumber){
            tv_blacknumber.setText("黑名单拦截已开启");
            tv_blacknumber.setTextColor(Color.BLACK);
            //checkbiox时勾选状态
            cb_blacknumber.setChecked(true);
        }else {
            tv_blacknumber.setText("黑名单拦截已关闭");
            tv_blacknumber.setTextColor(Color.RED);
            //checkbiox时未勾选状态
            cb_blacknumber.setChecked(false);
        }
        //checkbox状态改变
        blacknumberState();
    }
    private void blacknumberState() {
        cb_blacknumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //获取Editor对象，该对象可以向sp中存储数据
                SharedPreferences.Editor editor=sp.edit();
                //将键值对存储到sp文件中
                editor.putBoolean("autoblacknumber",isChecked);
                //提交数据到sp
                editor.commit();
                if(isChecked){
                    tv_blacknumber.setText("黑名单拦截已开启");
                    tv_blacknumber.setTextColor(Color.BLACK);
                }else {
                    tv_blacknumber.setText("黑名单拦截已关闭");
                    tv_blacknumber.setTextColor(Color.RED);
                }
            }
        });
    }
}
