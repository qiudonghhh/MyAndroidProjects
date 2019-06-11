package com.example.utils.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

public class AppInfoProvider {

    public static AppInfo appInfo;

    /**
     * 返回当前手机所有应用的相关信息（名称，包名，图标，内存，（系统应用，用户应用））
     * @param context 获取包管理者的上下文环境（如果不传环境，默认会获取本app的包名）
     * @return 包含手机安装相关信息的对象
     */
    public static List<AppInfo> getAppInfoList(Context context){
        //1.包管理者对象
        PackageManager pm=context.getPackageManager();
        //2.获取安装在手机上每个应用相关信息的集合（0代表基本信息）
        List<PackageInfo> packageInfoList=pm.getInstalledPackages(0);
        List<AppInfo> appInfoList=new ArrayList<>();
        //3.循环遍历应用信息的集合
        for (PackageInfo packageInfo:packageInfoList) {
                    appInfo = new AppInfo();
                    //4.获取应用的包名
                    appInfo.packageName=packageInfo.packageName;
                    //5.获取应用名称
                    //获取一个应用程序的所有信息
                    ApplicationInfo applicationInfo=packageInfo.applicationInfo;
                    appInfo.name=applicationInfo.loadLabel(pm).toString();
                    //6.获取图标
                    appInfo.icon=applicationInfo.loadIcon(pm);
                    //7.判断是否为系统应用(每个手机上应用对应的flag都不一致)
                    if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                        //系统应用
                        appInfo.isSystem=true;
                }else {
                //非系统应用
                appInfo.isSystem=false;
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}
