package com.example.question;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.question.Utils.ExamBean;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ExamActivity extends Activity {

    private TextView tv_question;
    
    private Button bt_top;
    private Button bt_next;
    private List<ExamBean> btList;
    private int count;
    private int current;
    private int selectAnswer;
    private RadioGroup rg_group;
    private RadioButton[] radioButtons;
    private List<ExamBean> wrongList;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        initUI();
        initNet();
    }

    private void initUI() {
        tv_title = findViewById(R.id.tv_title);
        tv_question = findViewById(R.id.tv_question);
        rg_group = findViewById(R.id.rg_group);
        radioButtons = new RadioButton[4];
        radioButtons[0] = findViewById(R.id.rb_a);
        radioButtons[1] = findViewById(R.id.rb_b);
        radioButtons[2] = findViewById(R.id.rb_c);
        radioButtons[3] = findViewById(R.id.rb_d);
        bt_top = findViewById(R.id.bt_top);
        bt_next = findViewById(R.id.bt_next);

    }

    private void initNet() {
        //访问网络是耗时操作，必须在子线程中进行，否则会抛异常
        new Thread() {
            @Override
            public void run() {
                //发送请求获取数据,参数为请求json的链接地址
                try {
                    //1封装url地址
                    URL url = new URL("http://172.31.84.140:8080/Server/servlet/ExamServlet");
                    //2开启个链接
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    //3设置常见请求参数
                    //请求超时，超过2000ms就是超时
                    urlConn.setConnectTimeout(5000);
                    //读取超时，读取时中断2000ms就是读取超时
                    urlConn.setReadTimeout(5000);
                    // 设置为GET请求
                    urlConn.setRequestMethod("GET");

                    // 开始连接
                    urlConn.connect();
                    //4获取请求成功响应码
                    if (urlConn.getResponseCode() == 200) {
                        // 获取返回的数据
                        String result = streamToString(urlConn.getInputStream());
                        //Log.i(TAG, result);

                        //json解析,将返回的result字符串反序列化成集合

                        //创建gson对象，用于json处理
                        Gson gson = new Gson();
                        //将从json字符串中读取的数据转换为JsonArray对象
                        JsonArray jsonArray = new JsonParser().parse(result).getAsJsonArray();
                        //将jjsonArray转换成list集合，gson提供fromJson方法进行反序列化
                        btList = gson.fromJson(jsonArray, new TypeToken<List<ExamBean>>() {}.getType());

                        //操作UI，相当于切换到主线程 run()方法在主线程中进行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //获取集合长度，为控件赋值
                                count = btList.size();
                                current = 0;
                                final ExamBean examBean = btList.get(current);
                                tv_title.setText("一共"+btList.size()+"道题，当前第"+(current+1)+"道");
                                tv_question.setText(examBean.getQuestion());
                                radioButtons[0].setText(examBean.getAnswera());
                                radioButtons[1].setText(examBean.getAnswerb());
                                radioButtons[2].setText(examBean.getAnswerc());
                                radioButtons[3].setText(examBean.getAnswerd());
                                //监听GroupRadio
                                rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                                        //循环,如果哪个radiobutton被选中，就给SelectedAnswer赋值
                                        for (int i = 0; i < 4; i++) {
                                            if (radioButtons[i].isChecked()) {
                                                //根据索引获取对象，再对对象的SelectedAnswer属性赋值
                                                btList.get(current).setSelectedAnswer(i);
                                                break;
                                            }
                                        }
                                    }
                                });
                                //监听下一题的操作事件
                                bt_next.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(examBean.getSelectedAnswer() == -1){
                                            Toast.makeText(ExamActivity.this,"sdd",Toast.LENGTH_SHORT).show();
                                        }else {
                                            if (current < count - 1) {//若当前题目不为最后一题，点击next按钮到下一题；
                                                current++;
                                                //更新题目
                                                ExamBean examBean = btList.get(current);
                                                tv_title.setText("一共"+btList.size()+"道题，当前第"+(current+1)+"道");
                                                tv_question.setText(examBean.getQuestion());
                                                radioButtons[0].setText(examBean.getAnswera());
                                                radioButtons[1].setText(examBean.getAnswerb());
                                                radioButtons[2].setText(examBean.getAnswerc());
                                                radioButtons[3].setText(examBean.getAnswerd());
                                                //若之前已经选择过，则应记录选择(选择过答案时，SelectedAnswer存到对象里，
                                                // 点击下一题，就把选择的答案先清除掉，每次获取对象里选择的答案设置哪个被选中)
                                                rg_group.clearCheck();
                                                if (examBean.getSelectedAnswer() != -1) {
                                                    //获取对象里的值，是几对应的radioButtons就是选中状态
                                                radioButtons[examBean.getSelectedAnswer()].setChecked(true);
                                                }

                                            }
                                            //如果是最后一题，出现Dialog,并计算错的题
                                            else{
                                                //接收返回的错题
                                                wrongList = checkAnswer(btList);
                                                ShowDialog();
                                            }
                                        }
                                    }
                                });
                                //监听上一题
                                bt_top.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (current >=1) {//若当前题目不为第一题，点击top按钮到上一题；
                                            current--;
                                            tv_title.setText("一共"+btList.size()+"道题，当前第"+(current+1)+"道");
                                            //更新题目
                                            ExamBean examBean2 = btList.get(current);
                                            tv_question.setText(examBean2.getQuestion());
                                            radioButtons[0].setText(examBean2.getAnswera());
                                            radioButtons[1].setText(examBean2.getAnswerb());
                                            radioButtons[2].setText(examBean2.getAnswerc());
                                            radioButtons[3].setText(examBean2.getAnswerd());

                                            //若之前已经选择过，则应记录选择(选择过答案时，SelectedAnswer存到对象里，
                                            // 点击上一题，就把选择的答案先清除掉，每次获取对象里选择的答案设置哪个被选中)
                                            rg_group.clearCheck();
                                            if (examBean.getSelectedAnswer() != -1) {
                                                //获取对象里的值，是几对应的radioButtons就是选中状态
                                                radioButtons[examBean2.getSelectedAnswer()].setChecked(true);
                                            }
                                        } else {
                                            Toast.makeText(ExamActivity.this, "已经是第一题啦", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void ShowDialog() {
        //对话框,是依赖于activity的，this指定在本activity上
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        // 设置点击屏幕Dialog不消失
        builder.setCancelable(false);
        //设置对话框标题
        builder.setTitle("您已经答完题了");
        //设置描述内容,
        if(wrongList.size()==0){
            builder.setMessage("您太棒了，全部答对了");
            //提示框上的按钮
            builder.setPositiveButton("确定，太棒了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
                }
            });
            builder.show();
        }else{
            builder.setMessage("您一共答了"+btList.size()+"道题,"+"答错了"+wrongList.size()+"道题");
            //提示框上的按钮
            builder.setPositiveButton("查看错题", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent=new Intent(ExamActivity.this,WrongActivity.class);
                    //把对象集合wrongList传递给下一个activity(注意：ExamBean对象要实现Serializable接口)
                    Bundle bundle=new Bundle();
                    //第一个参数是键，用于后面从intent中取值，第二个参数是值，也就是要传递的集合
                    bundle.putSerializable("wrongList",(Serializable)wrongList);//序列化,要注意转化(Serializable)
                    intent.putExtras(bundle);//发送数据
                    startActivity(intent);

                }

            });
            builder.setNegativeButton("不看错题", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //对话框消失
                    dialog.dismiss();
                }
            });
            builder.show();
        }


    }

    /*
    判断用户作答是否正确，并将作答错误题目的下标生成list,返回给调用者
     */
    private List<ExamBean> checkAnswer(List<ExamBean> list) {
        List<ExamBean> wrongList = new ArrayList<>();
        for(int i=0;i<list.size();i++)
        {
            //如果list的正确答案，与原则的答案不同，就把错题添加到wrongList中，
            if(list.get(i).getAnswer()!=list.get(i).getSelectedAnswer()){
                //根据错题的索引获取错题对象，再将将错题对象添加到wrongList中
                wrongList.add(list.get(i));
            }
        }
        return wrongList;
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
            return null;
        }
    }
}
