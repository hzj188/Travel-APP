package com.app.luxingapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.luxingapp.DetailsActivity;
import com.app.luxingapp.R;
import com.app.luxingapp.adapter.ProdcutListAdapter;
import com.app.luxingapp.callback.JsonCallback;
import com.app.luxingapp.util.SpsUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment  implements OnRefreshLoadMoreListener {

    private int page = 0;
    private int size = 10;
    private List<JSONObject> dataList = new ArrayList<>();
    private RecyclerView recyclerview;
    private ProdcutListAdapter listAdapter;
    private EditText keywords;
    private Button search_button;
    private SmartRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerview = view.findViewById(R.id.recyclerview);
        keywords = view.findViewById(R.id.keywords);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        search_button = view.findViewById(R.id.search_button);


        refreshLayout.setOnRefreshLoadMoreListener(this);
        init();

        return view;
    }

    private void init() {

        listAdapter = new ProdcutListAdapter(getContext(),dataList, new ProdcutListAdapter.click() {
            @Override
            public void clickshoucang(JSONObject jsonObject, int postion) {
                httpshoucang(jsonObject, postion);
            }

            @Override
            public void details(JSONObject jsonObject) {
                startActivity(new Intent(getContext(), DetailsActivity.class).putExtra("id",jsonObject.getString("id")));
            }

            @Override
            public void clickdianzan(JSONObject jsonObject, int postion) {
                httpdianzan(jsonObject, postion);
            }
        });
        recyclerview.setAdapter(listAdapter);

        rerfrshall(keywords.getText().toString(),page, size);


        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page=0;
                size=10;
                rerfrshall(keywords.getText().toString(),page, size);
            }
        });
    }

    private void rerfrshall(String keywords,int page, int size) {
        String user = SpsUtil.getString(getContext(), "user", "");
        JSONObject userObject = JSONObject.parseObject(user);

        OkHttpUtils
                .post()
                .url("http://121.199.40.253:98/article/all")
                .addHeader("authorization", userObject.getString("token"))
                .addParams("keyword", keywords)
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
                            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadMore();
                        Log.e("onResponse", response.toJSONString());
                        JSONArray content = response.getJSONArray("content");
                        if (page == 0) {
                            dataList.clear();
                        }
                        dataList.addAll(content.toJavaList(JSONObject.class));
                        listAdapter.notifyDataSetChanged();
                    }
                });

    }

    private void httpshoucang(JSONObject jsonObject, int postion) {
        String user = SpsUtil.getString(getContext(), "user", "");
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
                            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.e("onResponse", response.toJSONString());
                        rerfrshall(keywords.getText().toString(),0, 10);
                    }
                });
    }

    private void httpdianzan(JSONObject jsonObject, int postion) {
        String user = SpsUtil.getString(getContext(), "user", "");
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
                            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.e("onResponse", response.toJSONString());
                        rerfrshall(keywords.getText().toString(),0, 10);
                    }
                });
    }


    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {

        page++;
        rerfrshall(keywords.getText().toString(),page, size);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {

        page=0;
        size=10;
        rerfrshall(keywords.getText().toString(),page, size);
    }
}
