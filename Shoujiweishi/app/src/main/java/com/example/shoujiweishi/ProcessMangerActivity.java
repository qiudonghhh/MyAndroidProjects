package com.example.shoujiweishi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.utils.ToastUtil;
import com.example.utils.engine.ProcessInfo;
import com.example.utils.engine.ProcessInfoProvider;
import java.util.ArrayList;
import java.util.List;

public class ProcessMangerActivity extends Activity {
    private TextView tv_process_count;
    private ListView lv_process_list;
    private int count;
    private List<ProcessInfo> processInfoList;
    private MyAdapter myAdaper;


    private List<ProcessInfo> systemList;
    private List<ProcessInfo> customerList;
    private Button bt_clear;
    private ImageView iv_icon;
    private TextView tv_name;
    private TextView tv_path;
    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //接收到子线程发来的消息后，开始设置ListView数据适配器
            myAdaper = new MyAdapter();
            lv_process_list.setAdapter(myAdaper);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_processmanger);
        super.onCreate(savedInstanceState);

       initUI();
       //初始化表头数据
       initTitleData();
       //初始化ListView数据
       initListData();
    }


    //初始化
    private void initUI() {
        tv_process_count = findViewById(R.id.tv_process_count);
        lv_process_list = findViewById(R.id.lv_process_list);
    }

    private void initTitleData() {
        //获取进程总数
        count = ProcessInfoProvider.getProcessCount(getApplicationContext());
        tv_process_count.setText("进程总数："+count);
    }

    private void initListData() {
        //写一个线程加载进程
        new Thread(){
            @Override
            public void run() {
                //接收手机进程相关信息的集合，通过ProcessInfo对象调用属性
                processInfoList = ProcessInfoProvider.getProcessInfo(getApplicationContext());
                //遍历集合，将集合分成西系统应用集合和非系统应用集合
                systemList = new ArrayList<>();
                customerList = new ArrayList<>();
                for (ProcessInfo pocessInfo : processInfoList) {
                    //判断appinfo对象的属性
                    if(pocessInfo.isSystem){
                        //添加到系统应用集合
                        systemList.add(pocessInfo);
                    }else {
                        //添加到用户应用集合
                        customerList.add(pocessInfo);
                    }
                }
                //发送个消息告诉主线程可以做接下来的事情了
                handler.sendEmptyMessage(0);
            }
        }.start();
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
        public ProcessInfo getItem(int position) {
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
                //View.inflate加载xml布局，getApplicationContext() 上下文环境
                View view = View.inflate(ProcessMangerActivity.this, R.layout.listview_process_item, null);
                //初始化
                bt_clear = view.findViewById(R.id.bt_clear);
                iv_icon = view.findViewById(R.id.iv_icon);
                tv_name = view.findViewById(R.id.tv_name);
                tv_path = view.findViewById(R.id.tv_path);
                //根据索引获取集合里的对象，再获取icon（即获取到集合中的ProcessInfo对象，再调用ProcessInfo的成员变量icon）
                //这里getItem就是上面的方法，先返回用户应用，在返回系统应用
                iv_icon.setBackgroundDrawable(getItem(position).icon);
                tv_name.setText(getItem(position).name);
                //根据索引获取包名
                final String packageName=getItem(position).packageName;
                if(position<customerList.size()){
                    tv_path.setText("用户进程");
                }else {
                    tv_path.setText("系统进程");
                }


                //监听清理按钮
                bt_clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //1.清理进程,先根据索引从两个集合中移除进程
                        if(position<customerList.size()){
                            customerList.remove(position);
                            //2.杀死进程,传入包名（因为要根据包名杀死进程）
                            ProcessInfoProvider.killProcess(packageName,getApplicationContext());
                        }else {
                            systemList.remove(position-customerList.size());
                            //2.杀死进程
                            ProcessInfoProvider.killProcess(packageName,getApplicationContext());
                        }

                        //在集合改变后，通知数据适配器刷新
                        if(myAdaper!=null){
                            myAdaper.notifyDataSetChanged();
                        }
                        ToastUtil.show(getApplication(),"进程已清理",0);

                        //更新进程总数
                        count=count-1;
                        tv_process_count.setText("进程总数："+count);
                    }
                });
                return view;
            }

        }

}

