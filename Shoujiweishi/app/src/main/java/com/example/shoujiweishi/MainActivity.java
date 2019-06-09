package com.example.shoujiweishi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.example.utils.StreamUtil;
import com.example.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import org.json.JSONObject;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化控件
        initUI();
        initData();
    }
     /**
     * 更新新版本的状态码
     */
    protected static final int UPDATE_VERSION=100;
    /**
     * 今日应用程序主界面的状态码
     */
    protected static final int ENTER_HOME=101;
    /**
     * 异常的状态码
     */
    private String versionDescrible;
    protected static final int ERROR=102;
    private TextView tv_version_name;
    private int mLocalVersionCode;
    private String mdownloadUrl;


    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_VERSION:
                    //弹出对话框提示更新
                    showUpdateDialog();

                    break;
                case ENTER_HOME:
                    //进入主程序,activity跳转
                    enterHome();
                    break;
                case ERROR:
                    //打印吐司
                    //ctx上下文环境，就是显示在那个activity中,异常可能包括URL,Json，IO异常，
                    ToastUtil.show(MainActivity.this,"异常了，不能检测更新",0);
                    //检测更新异常并不影响进入主界面
                    enterHome();
                    break;
            }
        };
    };




    /**
     * 弹出对话框，提示用户更新
     */
    private void showUpdateDialog() {
        //对话框,是依赖于activity的，this指定在本activity上
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //设置对话框左上角图标
        builder.setIcon(R.drawable.home_apps);
        // 设置点击屏幕Dialog不消失*/
        builder.setCancelable(false);
        //设置对话框标题
        builder.setTitle("更新提醒");
        //设置描述内容,即解析json获取的描述
        builder.setMessage(versionDescrible);
        //提示框上的按钮
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //立即更新，apk链接地址downloadUrl
                downloadApk();
            }
        });
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消对话框，进入主界面
                enterHome();

            }
        });
        //手机点击返回（取消）的事件监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //即使用户点击取消，也会进入主程序
                enterHome();
                //让dialog消失
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void downloadApk() {
        //apk下载链接地址，放置apk的路径

        //创建下载进度条
        final ProgressDialog pd=new ProgressDialog(MainActivity.this);
        //设置进度条提示消息
        pd.setMessage("正在下载");
        //设置进度条为水平/
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //1.判断sd卡是否可用（外部存储设备跟已经挂载上的比较）
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

            //2.获取sd卡的路径（sd卡的文件名/apk名字）
            String path=Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"mobilesafe.apk";
            //3.发送请求获取apk并且放置到指定的路径（用xutils的http服务）
            HttpUtils httpUtils = new HttpUtils();
            //4.发送请求，传递参数（下载地址，目标地址，）
            httpUtils.download(mdownloadUrl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {

                    Log.d("MainActivity", "下载成功");
                    //让下载进度条消失
                    pd.dismiss();
                    //下载成功(下载过后的放在sd卡中的apk)
                    File file=responseInfo.result;
                    //提示用户安装
                    installApk(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    //下载失败
                    Log.d("MainActivity", "下载失败");
                }
                //刚刚开始的下载方法
                public void onStart(){

                    Log.d("MainActivity", "刚刚下载");
                    //设置进度条提示消息
                    pd.setMessage("正在下载");
                    //设置进度条为水平
                    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pd.show();
                    super.onStart();
                }
                //下载过程中的方法(下载apk的总大小，当前的下载位置，是否正在下载)
                public void onLoading(long total,long current,boolean isUploading){
                    Log.d("MainActivity", "下载中..................");
                    //进度条显示公式
                    pd.setProgress((int) (((int) current / (float) total) * 100));
                    super.onLoading(total, current,isUploading);
                }
            });
        }
    }

    /**
     * 安装apk
     * @param file 安装sd卡中的文件
     */
    public void installApk(File file) {
        //跳转到的是系统界面，匹配Category，Type
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        startActivityForResult(intent,0);// 如果用户取消安装的话,
        // 会返回结果,回调方法onActivityResult
    }
    //开启一个activity返回结果调用的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 进入应用程序的主界面
     */
    private void enterHome() {
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        //在开启一个新的界面后，将导航界面关闭（导航界面只可见一次）
    finish();
}

    /**
     * 初始化UI方法
     */
    private void initUI(){
        tv_version_name=findViewById(R.id.tv_version_name);       //找到这个控件
    }

    /**
     * 初始化数据方法
     */
    private void initData(){
        //1.应用版本名称
        String versionName=getVersionName();
        tv_version_name.setText("版本名称："+versionName);     //设置控件的文本（文本就是版本名称）
        //2.检测是否有更新（本地版本号跟服务器版本号做比对），如果有更新，提示更新
        //2.1拿到本地版本号方法
        mLocalVersionCode=getVersionCode();
        //获取sd卡config文件
        SharedPreferences sp=getSharedPreferences("config",MODE_PRIVATE);
        if(sp.getBoolean("autoupdate",true)){
            //2.2获取服务器的版本号（客户端发请求，服务端给响应（用json数据传递））
            //json中包含的数据信息：更新版本的版本名称，新版本的描述信息，服务器版本号，新版本apk更新地址
            //检测版本号方法
            checkVersion();
        }else{
            //mHandler.sendMessageDelayed(msg,1000);
            //使用Handler对象将状态码发送到主线程，延时1s去做处理（也就是在Main界面停留1s）
            mHandler.sendEmptyMessageDelayed(ENTER_HOME,1000);

        }

    }

    /**
     * 检测版本号方法
     */
    private void checkVersion() {

        //访问网络是耗时操作，必须在子线程中进行，否则会抛异常
        new Thread(){
            @Override
            public void run() {
                //创建一个消息
                Message msg=Message.obtain();
                //发送请求获取数据,参数为请求json的链接地址
                try {
                    //1封装url地址
                    URL url=new URL("http://www.qiudong.xyz:8080/update.json");
                    //2开启个链接
                    HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                    //3设置常见请求参数
                    //请求超时，超过2000ms就是超时
                    connection.setConnectTimeout(2000);
                    //读取超时，读取时中断2000ms就是读取超时
                    connection.setReadTimeout(2000);

                    //4获取请求成功响应码
                    if(connection.getResponseCode()==200){
                        //5.以流的形式将数据获取
                       InputStream is= connection.getInputStream();
                       //6.将流转换成字符串（用工具类封装）,并返回
                        String json=StreamUtil.streamToString(is);

                        //日志打印，检测请求是否成功
//                        Log.d("MainActivity",json);

                        //7.json解析
                        JSONObject jsonObject= new JSONObject(json);
                        String versionName=jsonObject.getString("versionName");
                        versionDescrible=jsonObject.getString("versionDescrible");
                        String versionCode=jsonObject.getString("VersionCode");
                        mdownloadUrl = jsonObject.getString("downloadUrl");
                        //日志打印，检测是否解析成功
//                        Log.d("MainActivity",versionName);
//                        Log.d("MainActivity",versionDescrible);
//                        Log.d("MainActivity",versionCode);
//                        Log.d("MainActivity",downloadUrl);


//                        msg.obj=VersionCode;
                        //8.比对版本号，（服务器版本号>本地版本号，提示用户更新）
                        if(mLocalVersionCode < Integer.parseInt(versionCode)){
                            //提示更新（弹出对话框UI,因为在子线程中，无法操作UI,所以需要用消息机制handler）
                            msg.what= UPDATE_VERSION;
                        }else{
                            //让线程睡1秒，为了显示出主界面
                            Thread.sleep(1000);
                            //进入应用程序主界面
                            msg.what=ENTER_HOME;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what=ERROR;
                }finally {
                    //将消息发出
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 返回版本号的方法
     * @return 非0代表获取成功，0代表异常
     */
    private int getVersionCode() {
        //1.获取包管理对象PackageManager
        PackageManager pm=getPackageManager();
        //2.从包的管理对象中获取指定包的基本信息（版本名称，版本号）
        //this.getPackageName()获取本类包名，因为手机app包名唯一，所以获取本类报名就可以了，传0代表获取基本信息
        try {
            PackageInfo packageInfo=pm.getPackageInfo(this.getPackageName(),0);
            //3.获取版本号并返回
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 获取版本名称，清单文件中
     * @return 应用版本名称，返回null代表异常
     */
    private String  getVersionName(){
        //1.获取包管理对象PackageManager
        PackageManager pm=getPackageManager();
        //2.从包的管理对象中获取指定包的基本信息（版本名称，版本号）
        //this.getPackageName()获取本类包名，因为手机app包名唯一，所以获取本类报名就可以了，传0代表获取基本信息
        try {
            PackageInfo packageInfo=pm.getPackageInfo(this.getPackageName(),0);
            //3.获取版本名称并返回
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;     //异常返回null
    }
}
