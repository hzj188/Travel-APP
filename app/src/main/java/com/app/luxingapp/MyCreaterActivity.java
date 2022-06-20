package com.app.luxingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.luxingapp.adapter.ProdcutListAdapter;
import com.app.luxingapp.callback.JsonCallback;
import com.app.luxingapp.util.SpsUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

public class MyCreaterActivity extends AppCompatActivity {

    private TextView base_title_tv;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerview;
    private int page = 0;
    private int size = 10;
    private List<JSONObject> dataList = new ArrayList<>();
    private ProdcutListAdapter listAdapter;
    private String nowtype;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycreater);
        base_title_tv = findViewById(R.id.base_title_tv);
        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerview = findViewById(R.id.recyclerview);


        listAdapter = new ProdcutListAdapter(this,dataList, new ProdcutListAdapter.click() {
            @Override
            public void clickshoucang(JSONObject jsonObject, int postion) {
                httpshoucang(jsonObject, postion);
            }

            @Override
            public void details(JSONObject jsonObject) {
                startActivity(new Intent(MyCreaterActivity.this, DetailsActivity.class)
                        .putExtra("id",jsonObject.getString("id"))
                        .putExtra("type",getIntent().getStringExtra("type"))
                );
            }

            @Override
            public void clickdianzan(JSONObject jsonObject, int postion) {
                httpdianzan(jsonObject, postion);
            }
        });
        recyclerview.setAdapter(listAdapter);


        switch (getIntent().getStringExtra("type")) {
            case "creater":
                base_title_tv.setText("我创建的");
                rerfrshall(nowtype="myArticles",page,size);
                break;
            case "shoucang":
                base_title_tv.setText("我收藏的");
                rerfrshall(nowtype="myCollectArticles",page,size);
                break;
            case "dianzan":
                base_title_tv.setText("我点赞的");
                rerfrshall(nowtype="myFavouriteArticles",page,size);
                break;
        }

    }

    private void rerfrshall(String keywords,int page, int size) {
        String user = SpsUtil.getString(getApplicationContext(), "user", "");
        JSONObject userObject = JSONObject.parseObject(user);

        OkHttpUtils
                .post()
                .url("http://121.199.40.253:98/article/"+keywords)
                .addHeader("authorization", userObject.getString("token"))
                .addParams("page", page + "")
                .addParams("size", size + "")
                .build()
                .execute(new JsonCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("Exception", e.getMessage().toString());
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.getIntValue("code")!=0){
                            Toast.makeText(MyCreaterActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadMore();
                        Log.e("onResponse", response.toJSONString());
                        JSONArray content = response.getJSONArray("value");
                        if (page == 0) {
                            dataList.clear();
                        }
                        dataList.addAll(content.toJavaList(JSONObject.class));
                        listAdapter.notifyDataSetChanged();
                    }
                });

    }
    private void httpshoucang(JSONObject jsonObject, int postion) {
        String user = SpsUtil.getString(getApplicationContext(), "user", "");
        JSONObject userObject = JSONObject.parseObject(user);
        OkHttpUtils
                .post()
                .url("http://121.199.40.253:98/article/collect")
                .addHeader("authorization", userObject.getString("token"))
                .addParams("id", jsonObject.getString("id"))
                .build()
                .execute(new JsonCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("Exception", e.getMessage().toString());
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.getIntValue("code")!=0){
                            Toast.makeText(MyCreaterActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.e("onResponse", response.toJSONString());
                        rerfrshall(nowtype,0, 10);
                    }
                });
    }

    private void httpdianzan(JSONObject jsonObject, int postion) {
        String user = SpsUtil.getString(getApplicationContext(), "user", "");
        JSONObject userObject = JSONObject.parseObject(user);
        OkHttpUtils
                .post()
                .url("http://121.199.40.253:98/article/favourite")
                .addHeader("authorization", userObject.getString("token"))
                .addParams("id", jsonObject.getString("id"))
                .build()
                .execute(new JsonCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("Exception", e.getMessage().toString());
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.getIntValue("code")!=0){
                            Toast.makeText(MyCreaterActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.e("onResponse", response.toJSONString());
                        rerfrshall(nowtype,0, 10);
                    }
                });
    }
}
