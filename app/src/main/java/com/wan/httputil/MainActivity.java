package com.wan.httputil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.wan.http.HttpUtil;

import java.sql.DriverPropertyInfo;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get请求
        HttpUtil.HttpBuilder builder = HttpUtil.newBuilder(this)//每个请求都绑定一个activity
                .url("https://www.baidu.com/s")
                .addParam("wd","哈哈")
                .get(new HttpUtil.HttpResult<String>() {
                    @Override
                    public void onSuccess(String obj) {//可只处理成功情况
                        TextView textView = (TextView) findViewById(R.id.text);
                        textView.setText(obj);
                    }
                });

//        HttpUtil.remove(builder);//取消单个请求

        //post请求,不能获取数据，只是展现用法
        HttpUtil.newBuilder(this)
                .url("https://www.baidu.com/")
                .addParam(new HashMap<String, String>())//也可以直接添加Map参数
                .post(new HttpUtil.HttpResult<ArrayList<DriverPropertyInfo>>() {//支持两级泛型
                    @Override
                    public void onSuccess(ArrayList<DriverPropertyInfo> infos) {//很多时候我们需要list<>形式，

                    }

                    @Override
                    public void onFailed(String msg) {
                        super.onFailed(msg);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtil.remove(this);//在结束取消activity所有网络请求
    }
}

