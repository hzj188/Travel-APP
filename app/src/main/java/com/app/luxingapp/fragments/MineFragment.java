package com.app.luxingapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.app.luxingapp.MyCreaterActivity;
import com.app.luxingapp.R;
import com.app.luxingapp.SettingActivity;
import com.app.luxingapp.adapter.ImageAdapter;
import com.app.luxingapp.callback.JsonCallback;
import com.app.luxingapp.util.ImgUtil;
import com.app.luxingapp.util.SpsUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import org.jetbrains.annotations.NotNull;

public class MineFragment extends Fragment implements OnRefreshLoadMoreListener {

    private LinearLayout order0;
    private LinearLayout order1;
    private LinearLayout order2;
    private LinearLayout order3;
    private SmartRefreshLayout refreshLayout;
    private ImageView head_iv;
    private TextView nikename;
    private TextView sign;
    private TextView sex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        order0 = view.findViewById(R.id.order0);
        order1 = view.findViewById(R.id.order1);
        order2 = view.findViewById(R.id.order2);
        order3 = view.findViewById(R.id.order3);

        refreshLayout = view.findViewById(R.id.refreshLayout);
        head_iv = view.findViewById(R.id.head_iv);
        nikename = view.findViewById(R.id.nikename);
        sex = view.findViewById(R.id.sex);
        sign = view.findViewById(R.id.sign);

        refreshLayout.setOnRefreshLoadMoreListener(this);
        refreshLayout.setEnableLoadMore(false);

        order0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingActivity.class));
            }
        });
        order1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyCreaterActivity.class).putExtra("type", "creater"));
            }
        });
        order2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyCreaterActivity.class).putExtra("type", "shoucang"));
            }
        });
        order3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyCreaterActivity.class).putExtra("type", "dianzan"));
            }
        });

        userinfo();
        return view;
    }

    private void userinfo() {
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
        String user = SpsUtil.getString(getContext(), "user", "");
        JSONObject userObject = JSONObject.parseObject(user);
        OkHttpUtils
                .get()
                .url("http://121.199.40.253:98/user/member")
                .addHeader("authorization", userObject.getString("token"))
                .build()
                .execute(new JsonCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("Exception", e.getMessage().toString());
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.getIntValue("code")!=0){
                            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.e("onResponse", response.toJSONString());
                        JSONObject jsonObject = response.getJSONObject("value");
                        if (!TextUtils.isEmpty(jsonObject.getString("avatar"))) {
                            ImgUtil.loadImage(head_iv, jsonObject.getString("avatar"));
                        }
                        nikename.setText(jsonObject.getString("nickName"));
                        sign.setText(jsonObject.getString("sign"));
                        if (jsonObject.getIntValue("sex") == 0) {
                            sex.setText("未知");
                        } else if (jsonObject.getIntValue("sex") == 1) {
                            sex.setText("男");
                        } else if (jsonObject.getIntValue("sex") == 2) {
                            sex.setText("女");
                        }
                    }
                });

    }

    @Override
    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        userinfo();
    }
}
