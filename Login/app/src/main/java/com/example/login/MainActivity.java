package com.example.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.login.db.dao.UserDao;

public class MainActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_pwd;
    private Button regis;
    private Button login;
    private UserDao mDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initData();
    }

    private void initData() {

        //监听注册
        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString().trim();
                String  pwd = et_pwd.getText().toString().trim();
                if(TextUtils.isEmpty(username)||TextUtils.isEmpty(pwd)){
                    Toast.makeText(getApplicationContext(),"账号或密码不能为空",Toast.LENGTH_SHORT).show();
                }else {
                mDao = new UserDao(getApplicationContext());
                mDao.insert(username,pwd);
                Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT).show();
            }}
        });
        //监听登录
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString().trim();
                String pwd = et_pwd.getText().toString().trim();
                if(TextUtils.isEmpty(username)||TextUtils.isEmpty(pwd)){
                    Toast.makeText(getApplicationContext(),"账号或密码不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    mDao=new UserDao(getApplicationContext());
                    if(mDao.find(username,pwd)){
                        Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(getApplicationContext(),"还没有这个账号",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void initUI() {
        et_username = findViewById(R.id.et_username);
        et_pwd = findViewById(R.id.et_pwd);
        regis = findViewById(R.id.regis);
        login = findViewById(R.id.login);
    }
}
