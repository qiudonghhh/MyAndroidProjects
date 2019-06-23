package com.example.question;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    String TAG = MainActivity.class.getCanonicalName();
    private EditText et_data_uname;
    private EditText et_data_upass;
    private HashMap<String, String> stringHashMap;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_data_uname =  findViewById(R.id.et_data_uname);
        et_data_upass =  findViewById(R.id.et_data_upass);

        stringHashMap = new HashMap<>();
    }
    //注册监听
    public void registerGET(View view) {
        stringHashMap.put("username", et_data_uname.getText().toString());
        stringHashMap.put("password", et_data_upass.getText().toString());
        if(et_data_uname.getText().toString().trim().equals("")||et_data_upass.getText().toString().trim().equals("")){
            Toast.makeText(MainActivity.this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    requestGet(stringHashMap);
                }
            }).start();
        }
    }
    //登录监听
    public void LoginGET(View view){
        stringHashMap.put("username", et_data_uname.getText().toString());
        stringHashMap.put("password", et_data_upass.getText().toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestLoginGet(stringHashMap);
            }
        }).start();

    }

    /**
     * get提交数据
     *
     * @param paramsMap
     */
    private void requestGet(HashMap<String, String> paramsMap) {
        try {
            String baseUrl = "http://172.31.84.140:8080/Server/servlet/RegisterServlet?";
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                Log.i(TAG,"params--get--循环体中>>"+tempParams.toString());
                pos++;
            }

            Log.i(TAG,"params--get-->>"+tempParams.toString());
            String requestUrl = baseUrl + tempParams.toString();
            Log.i(TAG,"params--get--requestUrl>>"+requestUrl);
            // 新建一个URL对象
            URL url = new URL(requestUrl);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接主机超时时间
            urlConn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(5 * 1000);
            // 设置是否使用缓存  默认是true
            urlConn.setUseCaches(true);
            // 设置为GET请求
            urlConn.setRequestMethod("GET");
            //urlConn设置请求头信息
            //设置请求中的媒体类型信息。
            urlConn.setRequestProperty("Content-Type", "application/json");
            //设置客户端与服务连接类型
            urlConn.addRequestProperty("Connection", "Keep-Alive");

            // 开始连接
            urlConn.connect();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                result = streamToString(urlConn.getInputStream());
                //解析json
                JSONObject jsonObject = new JSONObject(result);
                int code=jsonObject.getInt("code");
                Object data=jsonObject.getJSONObject("data");
                final String msg=jsonObject.getString("msg");
                Log.d(TAG,msg);
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Get方式请求成功，result--->" + result);
                            }
                        }
                );
                //判断返回的code，是0就是注册成功
                if(code==0){
                    Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "Get方式请求失败");
                            }
                        }
                );
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
    private void requestLoginGet(HashMap<String, String> paramsMap) {
        try {
            String baseUrl = "http://172.31.84.140:8080/Server/servlet/Loginservlet?";
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                Log.i(TAG,"params--get--循环体中>>"+tempParams.toString());
                pos++;
            }

            Log.i(TAG,"params--get-->>"+tempParams.toString());
            String requestUrl = baseUrl + tempParams.toString();
            Log.i(TAG,"params--get--requestUrl>>"+requestUrl);
            // 新建一个URL对象
            URL url = new URL(requestUrl);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接主机超时时间
            urlConn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(5 * 1000);
            // 设置是否使用缓存  默认是true
            urlConn.setUseCaches(true);
            // 设置为GET请求
            urlConn.setRequestMethod("GET");
            //urlConn设置请求头信息
            //设置请求中的媒体类型信息。
            urlConn.setRequestProperty("Content-Type", "application/json");
            //设置客户端与服务连接类型
            urlConn.addRequestProperty("Connection", "Keep-Alive");

            // 开始连接
            urlConn.connect();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                result = streamToString(urlConn.getInputStream());
                //解析json
                JSONObject jsonObject = new JSONObject(result);
                int code=jsonObject.getInt("code");
                final String msg=jsonObject.getString("msg");
                Log.d(TAG,msg);
                //判断返回的code，是0就是成功
                if(code==0){
                    Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
        /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     * @return
     */
    public String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }
}
