package com.example.shoujiweishi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.db.BlackNumberOpenHelper;
import com.example.db.dao.BlackNumberDao;
import com.example.db.dao.BlackNumberInfo;
import com.example.utils.ToastUtil;

import java.util.List;

public class BlackNumberActivity extends Activity {

    private Button bt_add;
    private ListView lv_blacknumber;
    private EditText et_phone;
    private MyAdaper myAdaper;
    private BlackNumberDao mDao;
    private String mode="短信";
    @SuppressLint("HandlerLeak")
    //1.在主线程中创建一个Handler对象,重写handleMessage方法，用来接收子线程发来的消息
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //4.主线程接收到消息，告知ListView可以设置数据适配器
            myAdaper = new MyAdaper();
            lv_blacknumber.setAdapter(myAdaper);
        }
    };
    private List<BlackNumberInfo> blackNumberList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacknumber);
        //初始化
        initUI();
        //初始化数据
        initData();
        //对点击添加按钮事件的处理
        event();
    }

    private void initUI() {
        bt_add = findViewById(R.id.bt_add);
        lv_blacknumber = findViewById(R.id.lv_blacknumber);
    }

    private void initData() {
        //1.获取操作数据库对象
        mDao=new BlackNumberDao(this);
        //获取数据库中所有的电话号码，由于查询数据可能多，是耗时操作，所以用线程
        new Thread(){
            @Override
            public void run() {
                //2.查询所有数据操作
                blackNumberList = mDao.findAll();
                //3.通过消息机制告诉主线程可以使用包含数据的集合
                handler.sendEmptyMessage(0);
            };
        }.start();
    }

    /**
     * 点击添加按钮事件的处理
     */
    private void event() {
        //对添加按钮点击事件的处理
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        //创建对话框对象
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //设置自定义对话框
        final AlertDialog dialog=builder.create();
        //点击对话框以外不会退出,但返回会退出
        dialog.setCanceledOnTouchOutside(false);
        //加载xml布局
        View view=View.inflate(this,R.layout.dialog_add_blacknumber,null);
        //初始化控件
        et_phone = view.findViewById(R.id.et_phone);
        RadioGroup rg_group= view.findViewById(R.id.rg_group);
        Button bt_submit=view.findViewById(R.id.bt_submit);
        Button bt_cancel=view.findViewById(R.id.bt_cancel);
        //将View对象加载到dialog上
        dialog.setView(view);
        //显示dialog
        dialog.show();
        //监听RadioGroup中选中条目的切换过程
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_sms:
                        //拦截短信,为" 短信"
                        mode = "短信";
                        break;
                    case R.id.rb_phone:
                        //拦截电话 "电话""
                        mode= "电话";
                        break;
                    case R.id.rb_all:
                        //拦截所有 如果选择短信，令mode值"所有"
                        mode= "所有";
                        break;
                }
            }
        });
        //对确认按钮监听处理
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.获取输入框中的电话号码
                String phone=et_phone.getText().toString();
                if(TextUtils.isEmpty(phone)){
                    //如果电话为空，打印吐司
                    ToastUtil.show(BlackNumberActivity.this,"拦截电话号不能为空",0);
                }else {
                    if(mDao.find(phone)){
                        ToastUtil.show(BlackNumberActivity.this,"已经存在该号码，不能插入",0);
                    }else {
                        //2.把电话号插入数据库
                        mDao.insert(phone,mode);
                        //3 插入完之后，数据库中多了条数据，但是并没有再次把数据读到集合中，集合还是原来的集合
                        //所以让数据库和集合保持一致（手动向集合中插入这条数据）,由于集合泛型是BlackNumberInfo，所以把数据转换成BlackNumberInfo对象
                        BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
                        blackNumberInfo.setPhone(phone);
                        blackNumberInfo.setMode(mode);
                        //4.把BlackNumberInfo对象插入集合最顶部(最顶部，索引为0)
                        blackNumberList.add(0,blackNumberInfo);
                        //5.通知数据适配器刷新（集合数据有改变）
                        if(myAdaper!=null){
                            myAdaper.notifyDataSetChanged();
                        }
                        //打印添加成功吐司
                        ToastUtil.show(getApplication(),"添加成功",0);
                        dialog.dismiss();
                    }

                }
            }
        });
        //对取消按钮监听
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    class MyAdaper extends BaseAdapter {
        @Override
        //1.ListView的长度，即集合长度
        public int getCount() {
            return blackNumberList.size();
        }
        //getItem(int position)和getItemId(int position)也必须重写（因为这是是BaseAdapter中的抽象方法），在调用ListView的响应方法的时候才会被调用到，这里不影响布局
        @Override
        public Object getItem(int position) {
            //根据索引获取当前集合里的对象
            return blackNumberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            //返回索引值
            return position;
        }
        //根据长度绘制item
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //把布局文件转换成View对象
            //View.inflate加载xml布局，getApplicationContext() 上下文环境
            View view=View.inflate(getApplicationContext(),R.layout.listview_blacknumber_item,null);
            //初始化
            TextView tv_phone=view.findViewById(R.id.tv_phone);
            TextView tv_mode=view.findViewById(R.id.tv_mode);
            ImageView iv_delete=view.findViewById(R.id.iv_delete);
            //根据索引获取集合里的对象，再获取电话号码（即获取到集合中的BlackNumberInfo对象，再调用BlackNumberInfo的getPhone()）
            tv_phone.setText(blackNumberList.get(position).getPhone());
            //获取到拦截模式并转换成int类型
            mode=(blackNumberList.get(position).getMode());
            switch (mode){
                case "短信":
                    tv_mode.setText("拦截短信");
                    break;
                case "电话":
                    tv_mode.setText("拦截电话");
                    break;
                case "所有":
                    tv_mode.setText("拦截所有");
                    break;
            }
            //对删除事件的处理
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1.数据库中删除,根据索引获取当前集合里的对象,即BlackNumberInfo对象，再调用getPhone()
                    mDao.delete(blackNumberList.get(position).getPhone());
                    //2.集合中删除（因为数据库中的数据都添加在集合中），再通知数据适配器刷新
                    //根据索引删除集合元素
                    blackNumberList.remove(position);
                    //通知数据适配器刷新
                    if(myAdaper!=null){
                        myAdaper.notifyDataSetChanged();
                    }
                    //打印删除成功吐司
                    ToastUtil.show(getApplication(),"删除成功",0);
                }
            });
            return view;
        }
    }
}
