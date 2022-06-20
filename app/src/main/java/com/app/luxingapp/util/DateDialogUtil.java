package com.app.luxingapp.util;

import android.app.Activity;
import android.view.View;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DateDialogUtil {


    public static void show(Activity context, final DialogCall dialogCall, final List<String> list) {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                dialogCall.dateCall(list.get(options1) + "", options1);
            }
        }).setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .build();
        // 设置数据
        pvOptions.setPicker(list, null, null);//添加数据源
        pvOptions.show();
    }

    private static String getTime(Date date) {//可根据需要自行截取数据显示
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public interface DialogCall {
        void dateCall(String date, int options1);
    }
}
