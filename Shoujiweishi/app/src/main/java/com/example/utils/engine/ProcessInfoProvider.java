package com.example.utils.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.example.shoujiweishi.R;
import java.util.ArrayList;
import java.util.List;

public class ProcessInfoProvider {
    //获取进程总数
    public static int getProcessCount(Context context) {
        //1.获取activityManager（获取activity管理者对象）
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //3.返回集合的的总数
        return runningAppProcesses.size();
    }

    /**
     * 返回当前进程的相关信息（名称，包名，图标，内存，系统应用）
     *
     * @param context 获取activity管理者的上下文环境
     * @return 包含手机安装相关信息的对象
     */
    public static List<ProcessInfo> getProcessInfo(Context context) {
        //创建集合
        List<ProcessInfo> processInfoList=new ArrayList<>();
        //获取进程相关信息
        //1.获取activityManager（获取activity管理者对象）
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取正在运行的进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //3.循环遍历集合，获取进程的相关信息（名称，包名，图标，内存，系统应用）
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            ProcessInfo processInfo = new ProcessInfo();
            //4.获取进程包名
            processInfo.packageName = info.processName;
            //5.获取应用的名称，图标，是否系统应用
            //5.1获取包管理者对象
            PackageManager pm = context.getPackageManager();
            //5.2获取应用相关信息（指定获取某个应用的信息，也就是指定获取进程的信息），（参数：进程的包名，0）
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                //5.3获取应用名称
                processInfo.name = applicationInfo.loadLabel(pm).toString();
                //5.4获取应用图标
                processInfo.icon = applicationInfo.loadIcon(pm);
                //5.5判断是否系统应用
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    //系统应用
                    processInfo.isSystem = true;
                } else {
                    //非系统应用
                    processInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                //异常原因是找不到对应包名的进程名称，图标
                //找不到名称就把包名赋值给进程名称
                processInfo.name=info.processName;
                //找不到图标就把系统图标赋值给进程图标(在res里)
                processInfo.icon=context.getResources().getDrawable(R.mipmap.ic_launcher);
                //没有名称和包名就是系统进程
                processInfo.isSystem=true;
                e.printStackTrace();
            }
            //把此对象添加到集合中
            processInfoList.add(processInfo);
        }
        //返回集合
        return processInfoList;
    }


    /**
     * @param packageName   要杀死的包名
     * @param context    环境
     */
    public static void killProcess(String packageName,Context context) {
        //1.获取activityManager（获取activity管理者对象）
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2.杀死指定包名的进程
        am.killBackgroundProcesses(packageName);
    }
}
