package com.app.luxingapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.luxingapp.App;
import com.app.luxingapp.R;
import com.app.luxingapp.fragments.HomeFragment;
import com.app.luxingapp.util.ImgUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProdcutListAdapter extends RecyclerView.Adapter<ProdcutListAdapter.ViewHolder> {
    private List<JSONObject> stringList;
    //TextAdapter构造函数
    click click;
    Context context;
    public ProdcutListAdapter(Context context, List<JSONObject> list, click click) {
        stringList = list;
        this.click = click;
        this.context=context;
    }

    //ViewHolder类将子项布局中所有控件绑定为一个对象，该对象包含子项布局的所有控件
    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView userhead_iv;
        ImageView item_comment_iv;
        ImageView item_thumbs_up_iv;
//        TextView title;
        TextView create_time;
        TextView content;
        TextView item_comment_tv;
        TextView item_thumbs_up_tv;
        TextView nickname;
        LinearLayout item_thumbs_up_ll;
        LinearLayout item_comment_ll;
        LinearLayout rootView;
        RecyclerView recyclerview;
        public static int anInt = 0;

        public ViewHolder(View view) {
            //父类构造函数
            super(view);
            view = view;
            userhead_iv = view.findViewById(R.id.userhead_iv);
            item_comment_iv = view.findViewById(R.id.item_comment_iv);
            item_thumbs_up_iv = view.findViewById(R.id.item_thumbs_up_iv);
//            title = view.findViewById(R.id.title);
            create_time = view.findViewById(R.id.create_time);
            content = view.findViewById(R.id.content);
            item_comment_tv = view.findViewById(R.id.item_comment_tv);
            item_thumbs_up_tv = view.findViewById(R.id.item_thumbs_up_tv);
            item_thumbs_up_ll = view.findViewById(R.id.item_thumbs_up_ll);
            item_comment_ll = view.findViewById(R.id.item_comment_ll);
            rootView = view.findViewById(R.id.rootView);
            recyclerview = view.findViewById(R.id.recyclerview);
            nickname = view.findViewById(R.id.nickname);

            anInt++;
            Log.d("TextAdpter:", "ViewHolder:" + anInt);
        }
    }

    //重写构造方法
    //绑定子项布局
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homelist, null, false);
        Log.d("TextAdpter:", "onCreateViewHolder");
        return new ViewHolder(view);
    }

    //为每个子项绑定数据
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int postion) {
        //获取当前位置的子项对象
        JSONObject jsonObject = stringList.get(postion);
        //从当前子项对象中获取数据，绑定在viewHolder对象中
//        viewtextView.setText(str);
        List<String> list = new ArrayList<>();
        ImageAdapter imageAdapter = new ImageAdapter(list);
        viewHolder.recyclerview.setAdapter(imageAdapter);
        viewHolder.recyclerview.setLayoutManager(new GridLayoutManager(context, 3));

        ImgUtil.loadImage(viewHolder.userhead_iv,jsonObject.getJSONObject("user").getJSONObject("userInfo").getString("avatar"));
        viewHolder.nickname.setText(jsonObject.getJSONObject("user").getJSONObject("userInfo").getString("nickName"));
        if (jsonObject.getJSONArray("photos").size() > 0) {
            list.clear();
            JSONArray content = jsonObject.getJSONArray("photos");
            for (int i = 0; i < content.size(); i++) {
                list.add(content.getJSONObject(i).getString("path"));
            }
            imageAdapter.notifyDataSetChanged();
        }
        if (jsonObject.getBoolean("collect")) {
            viewHolder.item_comment_iv.setImageResource(R.mipmap.ic_collection_success);
        } else {
            viewHolder.item_comment_iv.setImageResource(R.mipmap.ic_collection);
        }
        if (jsonObject.getBoolean("favourite")) {
            viewHolder.item_thumbs_up_iv.setImageResource(R.mipmap.ic_thumbs_up_press);
        } else {
            viewHolder.item_thumbs_up_iv.setImageResource(R.mipmap.ic_thumbs_up_normal);
        }
//        viewHolder.title.setText(jsonObject.getString("title"));
        viewHolder.content.setText(jsonObject.getString("content"));
        viewHolder.item_thumbs_up_tv.setText(jsonObject.getJSONArray("favouriteUsers").size() + "");
        viewHolder.item_comment_tv.setText(jsonObject.getJSONArray("collectUsers").size() + "");

        viewHolder.item_thumbs_up_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click.clickdianzan(jsonObject, postion);
            }
        });
        viewHolder.item_comment_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click.clickshoucang(jsonObject, postion);
            }
        });

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        viewHolder.create_time.setText(sdf1.format(jsonObject.getDate("date")));


        viewHolder.rootView.setOnClickListener(view -> click.details(jsonObject));
    }

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


    public interface click {
        void clickshoucang(JSONObject jsonObject, int postion);

        void details(JSONObject jsonObject);

        void clickdianzan(JSONObject jsonObject, int postion);
    }
}
