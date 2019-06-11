package com.example.shoujiweishi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.utils.Md5Util;
import com.example.utils.ToastUtil;

public class HomeActivity extends Activity implements View.OnClickListener {
    private ListView gv_home;
    private String[] mTitleStr;
    private int[] mDrawId;
    private SharedPreferences sp;
    private Button bt_cancel;
    private Button bt_submit;
    private AlertDialog dialog;
    private EditText et_set_pwd;
    private EditText et_confirm_pwd;
    private String setPwd;
    private String confirmPwd;
    private Button bt_exist_cancel;
    private Button bt_exist_submit;
    private EditText et_exist_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //初始化UI
        initUI();
        //初始化数据
        initData();
    }

    private void initUI() {
        gv_home = findViewById(R.id.gv_home);
    }

    private void initData() {
        //准备数据放在数组中（图片数组和名字数组）
        mTitleStr = new String[]{"手机黑名单","手机软件","手机进程","手机设置"};
        mDrawId = new int[]{R.drawable.home_b,R.drawable.home_c,R.drawable.home_c,R.drawable.home_d};
        //设置适配器，传入自定义适配器
        gv_home.setAdapter(new MyAdaper());
        //注册item单个条目点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //position点击item的索引
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //弹出弹框
                       showDialog();
                       break;
                    case 1:
                         Intent intent1=new Intent(HomeActivity.this, AppMangerActivity.class);
                         startActivity(intent1);

                        break;
                    case 3:
                        Intent intent2=new Intent(HomeActivity.this, SettingCentreActivity.class);
                        startActivity(intent2);
                        break;
                }
            }
        });
    }

    private void showDialog() {
        //获取Sd卡中的config文件，文件类型MODE_PRIVATE，没有该文件就创建
        sp = getSharedPreferences("config",MODE_PRIVATE);
        if(isSetupPwd()){
            //设置过密码，弹出输入密码的对话框
            showExistPwd();
        }else {
            //没设置过密码，弹出初始设置密码对话框
            showSetPwd();
        }

    }


    /**
     * 没设置过密码，弹出初始设置密码对话框
     */
    private void showSetPwd() {
        //创建对话框
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setCancelable(false);这个方法返回也不会退出，所以不用，注释
        //自定义dialog
        dialog = builder.create();
        //点击对话框以外不会退出,但返回会退出
        dialog.setCanceledOnTouchOutside(false);
        //通过infalte加载xml布局
        View view=View.inflate(this,R.layout.dialog_set_pwd,null);
        //初始化控件
        bt_cancel = view.findViewById(R.id.bt_cancel);
        bt_submit = view.findViewById(R.id.bt_submit);
        et_set_pwd = view.findViewById(R.id.et_set_pwd);
        et_confirm_pwd = view.findViewById(R.id.et_confirm_pwd);
        //设置监听器
        bt_cancel.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
        //将View对象加载到dialog上
        dialog.setView(view);
        //显示dialog
        dialog.show();
    }

    /**
     * 设置过密码，弹出输入密码的对话框
     */
    private void showExistPwd() {
        //创建对话框
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //自定义dialog
        dialog = builder.create();
        //点击对话框以外不会退出,但返回会退出
        dialog.setCanceledOnTouchOutside(false);
        //通过infalte加载xml布局
        View view=View.inflate(this,R.layout.dialog_confirm_pwd,null);
        //初始化控件
        bt_exist_cancel = view.findViewById(R.id.bt_exist_cancel);
        bt_exist_submit = view.findViewById(R.id.bt_exist_submit);
        et_exist_pwd = view.findViewById(R.id.et_exist_pwd);
        //设置监听器
        bt_exist_cancel.setOnClickListener(this);
        bt_exist_submit.setOnClickListener(this);
        //将View对象加载到dialog上
        dialog.setView(view);
        //显示dialog
        dialog.show();
    }

    private boolean isSetupPwd() {
        //获取config文件中的键值对，如果没有该键，默认值为空，
        String savePwd=sp.getString("password","");
        if(TextUtils.isEmpty(savePwd)){     //判断值是否为空
            return false;
        }else {
            return true;
        }
    }
    public void onClick(View v){
        switch (v.getId()){
            //没设置过密码布局的确认按钮,对按钮事件的处理
            case R.id.bt_submit:
                //获取设置密码字符串,并去掉前后空格
                setPwd = et_set_pwd.getText().toString().trim();
                //获取确认密码字符串，并去掉前后空格
                confirmPwd = et_confirm_pwd.getText().toString().trim();
                if(!TextUtils.isEmpty(setPwd)&&!TextUtils.isEmpty(confirmPwd)){
                    //设置密码和确认密码都不为空
                    if(setPwd.equals(confirmPwd)){
                        //获取Editor对象，该对象可以向sp中存储数据
                        SharedPreferences.Editor editor= sp.edit();
                        //将密码加密后存储到sp中
                        editor.putString("password", Md5Util.encoder(setPwd));
                        //提交数据到sp
                        editor.commit();
                        //两次输入密码一样，进入手机安全主界面
                        Intent intent=new Intent(HomeActivity.this, BlackNumberActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }else {
                        ToastUtil.show(HomeActivity.this,"两次密码输入不一致，请重新输入",0);
                    }
                }else {
                    //提示用户密码不能为空
                   ToastUtil.show(HomeActivity.this,"密码为空，请输入密码",0);
                }
                break;
            //没设置过密码布局的取消按钮,对按钮事件的处理
            case R.id.bt_cancel:
                dialog.dismiss();
                break;
            //设置过密码布局的确认按钮,对按钮事件的处理
            case R.id.bt_exist_submit:
                //获取设置密码字符串,并去掉前后空格
                String exist_pwd=et_exist_pwd.getText().toString().trim();
                if(TextUtils.isEmpty(exist_pwd)){
                    ToastUtil.show(HomeActivity.this,"密码不能为空",0);
                }else{
                    //获取sp中加密保存后的密码
                    String savedpwd=sp.getString("password","");
                    //由于Md5不可逆，所以只能把sp中加密后的密码与输入的密码进行加密做比较
                    if(savedpwd.equals(Md5Util.encoder(exist_pwd))){
                        ToastUtil.show(HomeActivity.this,"密码正确",0);
                        //对话框消失
                        dialog.dismiss();
                        //跳转到安全界面
                        Intent intent=new Intent(HomeActivity.this, BlackNumberActivity.class);
                        startActivity(intent);
                    }else {
                        ToastUtil.show(HomeActivity.this,"密码错误",0);
                    }
                }
                break;
            //设置过密码布局的取消按钮,对按钮事件的处理
            case R.id.bt_exist_cancel:
                dialog.dismiss();
                break;
        }
    }

    class MyAdaper extends BaseAdapter{

        @Override
        //1.ListView的长度，即文字组数==图片张树
        public int getCount() {
            return mTitleStr.length;
        }

        @Override
        //getItem(int position)和getItemId(int position)也必须重写（因为这是是BaseAdapter中的抽象方法），在调用ListView的响应方法的时候才会被调用到，这里不影响布局
        public Object getItem(int position) {
            //根据索引获取字符串
            return mTitleStr[position];
        }

        @Override
        public long getItemId(int position) {
            //返回索引
            return position;
        }

        /**
         * @param position 索引值
         * @param convertView 是从布局文件中inflate来布局。
         * @param parent
         * @return
         */
        @Override
        //2.根据长度绘制item
        public View getView(int position, View convertView, ViewGroup parent) {
            //把布局文件转换成View对象
            //View.inflate加载xml布局，getApplicationContext() 上下文环境
            View view=View.inflate(getApplicationContext(),R.layout.listview_item,null);
            TextView tv_title=view.findViewById(R.id.tv_title);
            ImageView iv_icon=view.findViewById(R.id.iv_icon);
            tv_title.setText(mTitleStr[position]);
            iv_icon.setBackgroundResource(mDrawId[position]);
            return view;
        }
    }

}