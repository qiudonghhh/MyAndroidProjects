package com.example.question;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.question.Utils.ExamBean;
import java.util.List;

public class WrongActivity extends Activity {

    private TextView tv_title;
    private TextView tv_question;
    private TextView tv_a;
    private TextView tv_b;
    private TextView tv_c;
    private TextView tv_d;
    private TextView tv_select_answer;
    private TextView tv_answer;
    private TextView tv_explaination;
    private Button bt_next;
    private Button bt_top;
    private List<ExamBean> wrongList;
    private int count;
    private int current;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong);
        //初始化Ui
        initUI();
        //处理UI
        initData();
    }

    private void initUI() {
        tv_title = findViewById(R.id.tv_title);
        tv_question = findViewById(R.id.tv_question);
        tv_a = findViewById(R.id.tv_a);
        tv_b = findViewById(R.id.tv_b);
        tv_c = findViewById(R.id.tv_c);
        tv_d = findViewById(R.id.tv_d);
        tv_select_answer = findViewById(R.id.tv_select_answer);
        tv_answer = findViewById(R.id.tv_answer);
        tv_explaination = findViewById(R.id.tv_explaination);
        bt_next = findViewById(R.id.bt_next);
        bt_top = findViewById(R.id.bt_top);
    }

    /**
     * 处理UI
     */
    private void initData() {
        Intent intent=this.getIntent();
        //接收发送过来的list
        wrongList = (List<ExamBean>) intent.getSerializableExtra("wrongList");

        //获取集合长度，为控件赋值
        count = wrongList.size();
        current = 0;
        final ExamBean examBean = wrongList.get(current);
        tv_title.setText("您一共错了"+wrongList.size()+"道题，当前第"+(current+1)+"道");
        tv_question.setText(examBean.getQuestion());
        tv_a.setText(examBean.getAnswera());
        tv_b.setText(examBean.getAnswerb());
        tv_c.setText(examBean.getAnswerc());
        tv_d.setText(examBean.getAnswerd());
        tv_select_answer.setText("您的答案："+ change(examBean.getSelectedAnswer()));
        tv_answer.setText("正确答案："+ change(examBean.getAnswer()));
        tv_explaination.setText("解析:"+examBean.getExplaination());

        //监听下一题的操作事件
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current < count - 1) {//若当前题目不为最后一题，点击next按钮到下一题；
                    current++;
                    //更新题目
                    ExamBean examBean = wrongList.get(current);
                    tv_title.setText("您一共错了"+wrongList.size()+"道题，当前第"+(current+1)+"道");
                    tv_question.setText(examBean.getQuestion());
                    tv_a.setText(examBean.getAnswera());
                    tv_b.setText(examBean.getAnswerb());
                    tv_c.setText(examBean.getAnswerc());
                    tv_d.setText(examBean.getAnswerd());
                    tv_select_answer.setText("您的答案："+ change(examBean.getSelectedAnswer()));
                    tv_answer.setText("正确答案："+ change(examBean.getAnswer()));
                    tv_explaination.setText("解析:"+examBean.getExplaination());
                }
                //如果是最后一题，打印吐司
                else{
                    Toast.makeText(WrongActivity.this,"您已经看完了所有的错题了",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //监听上一题
        bt_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current >=1) {//若当前题目不为第一题，点击top按钮到上一题；
                    current--;
                    tv_title.setText("您一共错了"+wrongList.size()+"道题，当前第"+(current+1)+"道");
                    //更新题目
                    ExamBean examBean2 = wrongList.get(current);
                    tv_question.setText(examBean.getQuestion());
                    tv_a.setText(examBean.getAnswera());
                    tv_b.setText(examBean.getAnswerb());
                    tv_c.setText(examBean.getAnswerc());
                    tv_d.setText(examBean.getAnswerd());
                    tv_select_answer.setText("您的答案："+ change(examBean.getSelectedAnswer()));
                    tv_answer.setText("正确答案："+ change(examBean.getAnswer()));
                    tv_explaination.setText("解析:"+examBean.getExplaination());

                } else {
                    Toast.makeText(WrongActivity.this, "已经是第一题啦", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public String change(int selectAnswer){
        switch (selectAnswer){
            case 0:
                 result="A";
                break;
            case 1:
                result="B";
                break;
            case 2:
                 result="C";
                break;
            case 3:
                 result="D";
                break;
        }
        return result;
    }

}
