package com.app.luxingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONObject;
import com.app.luxingapp.fragments.CreateFragment;
import com.app.luxingapp.fragments.FragmentPagerAdapter;
import com.app.luxingapp.fragments.HomeFragment;
import com.app.luxingapp.fragments.MineFragment;
import com.app.luxingapp.util.JsonUtil;
import com.app.luxingapp.util.TipUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * @Description: 主页
 * @Author: LM
 * @CreateDate: 2020/5/19 14:44
 * @Question:
 */
public class MainActivity extends AppCompatActivity  implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private boolean isFromClick;
    private int nowpos=0;
    private ViewPager pager;
    private BottomNavigationView nav;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager=findViewById(R.id.pager);
        nav=findViewById(R.id.nav);


        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), new HomeFragment(),new CreateFragment(),   new MineFragment()));
        pager.addOnPageChangeListener(this);
        pager.setOffscreenPageLimit(4);//有多少fragment就写几个
        nav.setOnNavigationItemSelectedListener(this);

    }




    @Override
    public void onPageSelected(int position) {
        if (!isFromClick) {
            int selectId = position == 0 ? R.id.home
                    : position == 1 ? R.id.news
                    : R.id.mine;
            nav.setSelectedItemId(selectId);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        isFromClick = true;
        switch (item.getItemId()) {
            case R.id.home:
                pager.setCurrentItem(0);
                break;
            case R.id.news:
                pager.setCurrentItem(1);
                break;
            case R.id.mine:
                    pager.setCurrentItem(2);
                    break;
            }
        isFromClick = false;
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    private long clickBackTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - clickBackTime < 2 * 1000) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        } else {
            clickBackTime = System.currentTimeMillis();
            TipUtil.show(getApplicationContext(),"再次点击退出程序");
        }
    }
}
