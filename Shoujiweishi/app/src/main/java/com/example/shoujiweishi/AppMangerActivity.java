package com.example.shoujiweishi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.utils.engine.AppInfo;
import com.example.utils.engine.AppInfoProvider;
import java.util.ArrayList;
import java.util.List;

public class AppMangerActivity extends Activity {

    private MyAdapter myAdaper;
    private List<AppInfo> appInfoList;
    private ListView lv_app_list;
    @SuppressLint("HandlerLeak")
     Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //接收到子线程发来的消息后，开始设置ListView数据适配器
            myAdaper = new MyAdapter();
            lv_app_list.setAdapter(myAdaper);
        }
    };
    private List<AppInfo> systemList;
    private List<AppInfo> customerList;
    private AppInfo appInfo;
    private Button bt_uninstall;
    private View result;
    private ImageView iv_icon;
    private TextView tv_name;
    private TextView tv_path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_app_manger);
        super.onCreate(savedInstanceState);

        initTitle();

        //OnCreate()之后接下来自动调用下面的onResume()方法
    }

    //安卓在onCreate进行完就自动调用onResume()方法，
    // 如果切换到下一个界面，第一个界面不finish 再回到本界面 就直接调onResume ，不调onCreate
    //这里为后面卸载完毕，再次返回到本界面，本界面重新加载，为了刷新LIstView而写的
    protected void onResume(){
        lv_app_list = findViewById(R.id.lv_app_list);

        //写一个线程加载应用
        new Thread(){
            @Override
            public void run() {
                //接收手机安装相关信息的集合，通过AppInfo对象调用属性
                appInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                //遍历集合，将集合分成西系统应用集合和非系统应用集合
                systemList = new ArrayList<>();
                customerList = new ArrayList<>();
                for (AppInfo appInfo:appInfoList) {
                    //判断appinfo对象的属性
                    if(appInfo.isSystem){
                        //添加到系统应用集合
                        systemList.add(appInfo);
                    }else {
                        //添加到用户应用集合
                        customerList.add(appInfo);
                    }
                }
                //发送个消息告诉主线程可以做接下来的事情了
                handler.sendEmptyMessage(0);
            }
        }.start();
        super.onResume();
    }

    private void initTitle() {
        //1.获取磁盘可用大小，
        // 1.1获取磁盘路径(先拿到文件夹，在获取路径)
        String path=Environment.getDataDirectory().getAbsolutePath();
        //1.2getAvailSpace(path)是获取以上两个路径下的文件夹大小,并且通过formatFileSize换算大小为GB单位
        String memoryAvailSpace=Formatter.formatFileSize(this,getAvailSpace(path));

        //初始化控件
        TextView tv_memory=findViewById(R.id.tv_memory);
        tv_memory.setText("存储空间："+memoryAvailSpace);
    }


    /**
     * 获取以上两个路径下的文件夹大小
     * @param path 磁盘路径
     * @return  返回为Byte单位的大小
     * 因为int类型最大代表2G,所以把size类型提升为long类型
     */
    private long getAvailSpace(String path) {
        //获取可用磁盘大小的类
        StatFs statFs=new StatFs(path);
        //获取可用区块的个数
        int count=statFs.getAvailableBlocks();
        //获取区块大小（计算机中一个区块4KB）
        long size=statFs.getBlockSize();
        //可用空间大小==区块大小*区块个数
        return count*size;
    }


    /**
     * ListView数据适配器
     */
     class MyAdapter extends BaseAdapter {

        @Override
        //1.ListView的长度，即两个集合长度和
        public int getCount() {
            return customerList.size()+systemList.size();
        }

        @Override
        //getItem(int position)和getItemId(int position)也必须重写（因为这是是BaseAdapter中的抽象方法），在调用ListView的响应方法的时候才会被调用到，这里不影响布局
        public AppInfo getItem(int position) {
            //根据索引获取当前集合里的对象，先返回用户集合，在返回系统集合，为了后面让用户应用在上面显示，系统应用在下面显示
            if(position < customerList.size()){
                return customerList.get(position);
            }else {
                return systemList.get(position-customerList.size());
            }
        }

        @Override
        //返回索引值
        public long getItemId(int position) {
            return position;
        }

        @Override
        //根据长度绘制item
        public View getView(final int position, View convertView, ViewGroup parent) {
            //加入索引小于customerList集合的长度，就是用户应用，（因为上面已经把用户应用放到了LIstVIew前面）
            if(position < customerList.size()) {
                //把布局文件转换成View对象，加载的是有Buutton的布局，
                //View.inflate加载xml布局，getApplicationContext() 上下文环境
                View view = View.inflate(AppMangerActivity.this, R.layout.listview_app_item, null);
                //初始化
                bt_uninstall = view.findViewById(R.id.bt_uninstall);
                iv_icon = view.findViewById(R.id.iv_icon);
                tv_name = view.findViewById(R.id.tv_name);
                tv_path = view.findViewById(R.id.tv_path);
                //根据索引获取集合里的对象，再获取icon（即获取到集合中的AppInfo对象，再调用AppInfo的成员变量icon）
                //这里getItem就是上面的方法，先返回用户应用，在返回系统应用（这里返回用户应用）
                iv_icon.setBackgroundDrawable(getItem(position).icon);
                tv_name.setText(getItem(position).name);
                tv_path.setText("用户应用");
                result = view;
                //监听卸载按钮进行卸载
                bt_uninstall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //AppInfo appInfo=new AppInfo();
                        //跳转到系统卸载界面
                    Intent intent=new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    //根据索引获取集合里的对象，再获取包名（即获取到集合中的AppInfo对象，再调用AppInfo的成员变量packageName）
                    intent.setData(Uri.parse("package:"+getItem(position).packageName));
                    startActivity(intent);
                    }
                });
            }else{
                View view = View.inflate(AppMangerActivity.this, R.layout.listview_app_item2, null);
                //初始化
                tv_path = view.findViewById(R.id.tv_path);
                iv_icon = view.findViewById(R.id.iv_icon);
                tv_name = view.findViewById(R.id.tv_name);
                //根据索引获取集合里的对象，再获取icon（即获取到集合中的AppInfo对象，再调用AppInfo的成员变量icon）
                //这里getItem就是上面的方法，先返回用户应用，在返回系统应用（这里返回系统应用）
                iv_icon.setBackgroundDrawable(getItem(position).icon);
                tv_name.setText(getItem(position).name);
                tv_path.setText("系统应用");
                result=view;
            }
            return result;
        }
    }

}
