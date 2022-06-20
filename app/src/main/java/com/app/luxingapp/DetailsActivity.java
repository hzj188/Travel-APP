package com.app.luxingapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.luxingapp.adapter.ImageAdapter;
import com.app.luxingapp.callback.JsonCallback;
import com.app.luxingapp.util.ImgUtil;
import com.app.luxingapp.util.SpeechUtils;
import com.app.luxingapp.util.SpsUtil;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    private ImageAdapter imageAdapter;
    private RecyclerView recyclerview;
    private TextView title;
    private TextView content;
    private TextView item_comment_tv;
    private TextView item_thumbs_up_tv;
    private TextView base_right_tv;
    private TextView speak;
    private ImageView item_comment_iv;
    private ImageView item_thumbs_up_iv;
    private LinearLayout item_thumbs_up_ll;
    private LinearLayout item_comment_ll;

    private JSONObject jsonObject;

    List<String> list = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        SpeechUtils.getInstance(DetailsActivity.this);

        recyclerview = findViewById(R.id.recyclerview);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        item_comment_tv = findViewById(R.id.item_comment_tv);
        item_thumbs_up_tv = findViewById(R.id.item_thumbs_up_tv);
        item_comment_iv = findViewById(R.id.item_comment_iv);
        item_thumbs_up_iv = findViewById(R.id.item_thumbs_up_iv);
        item_comment_ll = findViewById(R.id.item_comment_ll);
        item_thumbs_up_ll = findViewById(R.id.item_thumbs_up_ll);
        base_right_tv = findViewById(R.id.base_right_tv);
        speak = findViewById(R.id.speak);

        if (getIntent().getStringExtra("type")!=null && "creater".equals(getIntent().getStringExtra("type"))){
            base_right_tv.setVisibility(View.VISIBLE);
        }
        base_right_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(content.getText().toString())){
                    Toast.makeText(DetailsActivity.this,"没有文字可读",Toast.LENGTH_LONG).show();
                    return;
                }
                SpeechUtils.getInstance(DetailsActivity.this).speakText(content.getText().toString());
            }
        });

        imageAdapter = new ImageAdapter(list);
        recyclerview.setAdapter(imageAdapter);

        item_thumbs_up_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpdianzan();
            }
        });
        item_comment_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpshoucang();
            }
        });

        String id = getIntent().getStringExtra("id");
        detailsinfo(id);

    }

    private void delete() {
        String user = SpsUtil.getString(DetailsActivity.this, "user", "");
        JSONObject userObject = JSONObject.parseObject(user);
        OkHttpUtils
                .post()
                .url("http://121.199.40.253:98/article/delete")
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
                        Log.e("onResponse", response.toJSONString());
                        finish();
                    }
                });
    }

    private void detailsinfo(String id) {
        String user = SpsUtil.getString(DetailsActivity.this, "user", "");
        JSONObject userObject = JSONObject.parseObject(user);
        OkHttpUtils
                .post()
                .url("http://121.199.40.253:98/article/find")
                .addHeader("authorization", userObject.getString("token"))
                .addParams("id", id)
                .build()
                .execute(new JsonCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("Exception", e.getMessage().toString());
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("onResponse", response.toJSONString());
                        if (response.getIntValue("code") != 0) {
                            Toast.makeText(DetailsActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (response.getJSONObject("value") == null) {
                            Toast.makeText(DetailsActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }

                        jsonObject = response.getJSONObject("value");
                        if (jsonObject.getJSONArray("photos").size() > 0) {
                            list.clear();
                            JSONArray content = jsonObject.getJSONArray("photos");
                            for (int i=0;i<content.size();i++){
                                list.add(content.getJSONObject(i).getString("path"));
                            }
                            imageAdapter.notifyDataSetChanged();
                        }
                        if (jsonObject.getBoolean("collect")) {
                            item_comment_iv.setImageResource(R.mipmap.ic_collection_success);
                        } else {
                            item_comment_iv.setImageResource(R.mipmap.ic_collection);
                        }
                        if (jsonObject.getBoolean("favourite")) {
                            item_thumbs_up_iv.setImageResource(R.mipmap.ic_thumbs_up_press);
                        } else {
                            item_thumbs_up_iv.setImageResource(R.mipmap.ic_thumbs_up_normal);
                        }
                        title.setText(jsonObject.getString("title"));
                        content.setText(jsonObject.getString("content"));
                        item_thumbs_up_tv.setText(jsonObject.getJSONArray("favouriteUsers").size() + "");
                        item_comment_tv.setText(jsonObject.getJSONArray("collectUsers").size() + "");
                    }
                });
    }


    private void httpshoucang() {
        if (jsonObject==null){
            return;
        }
        String user = SpsUtil.getString(DetailsActivity.this, "user", "");
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
                        if (response.getIntValue("code") != 0) {
                            Toast.makeText(DetailsActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.e("onResponse", response.toJSONString());
                        detailsinfo(jsonObject.getString("id"));
                    }
                });
    }

    private void httpdianzan() {
        if (jsonObject==null){
            return;
        }
        String user = SpsUtil.getString(DetailsActivity.this, "user", "");
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
                        if (response.getIntValue("code") != 0) {
                            Toast.makeText(DetailsActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.e("onResponse", response.toJSONString());
                        detailsinfo(jsonObject.getString("id"));
                    }
                });
    }

}
