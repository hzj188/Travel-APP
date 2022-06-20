package com.app.luxingapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.app.luxingapp.R;
import com.app.luxingapp.util.ImgUtil;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<String> stringList;

    public ImageAdapter(List<String> list) {
        stringList = list;
    }

    //ViewHolder类将子项布局中所有控件绑定为一个对象，该对象包含子项布局的所有控件
    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView head_iv;

        public static int anInt = 0;

        public ViewHolder(View view) {
            //父类构造函数
            super(view);
            //获取RecyclerView布局的子项布局中的所有控件id,本次实验只有TextView这一种控件
            head_iv = view.findViewById(R.id.head_iv);
            anInt++;
            Log.d("TextAdpter:", "ViewHolder:" + anInt);
        }
    }

    //重写构造方法
    //绑定子项布局
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, null, false);
        Log.d("TextAdpter:", "onCreateViewHolder");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        ImgUtil.loadImage(holder.head_iv, stringList.get(position));

    }

    //为每个子项绑定数据

    //获取集合的大小
    @Override
    public int getItemCount() {
        Log.d("TextAdpter:", "getItemCount: " + stringList.size());
        return stringList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("TextAdpter:", "getItemViewType: " + position);
        return -1;
    }

    @Override
    public long getItemId(int position) {
        Log.d("TextAdpter:", "getItemId");
        return -1;
    }

}

