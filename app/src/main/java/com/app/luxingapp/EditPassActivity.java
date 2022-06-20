package com.app.luxingapp;

import android.app.LauncherActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.app.luxingapp.callback.JsonCallback;
import com.app.luxingapp.util.ImgUtil;
import com.app.luxingapp.util.SpsUtil;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

public class EditPassActivity extends AppCompatActivity {

    private EditText oldpass;
    private EditText newpass;
    private EditText newpass1;
    private TextView submit_area;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpass);

        oldpass = findViewById(R.id.oldpass);
        newpass = findViewById(R.id.newpass);
        submit_area = findViewById(R.id.submit_area);
        newpass1 = findViewById(R.id.newpass1);


        submit_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter()) {
                    edituser(oldpass.getText().toString(), newpass.getText().toString());
                }
            }
        });
    }

    private void edituser(String old, String newpass) {
        String user = SpsUtil.getString(EditPassActivity.this, "user", "");
        JSONObject userObject = JSONObject.parseObject(user);
        OkHttpUtils
                .post()
                .url("http://121.199.40.253:98/user/changePassword")
                .addHeader("authorization", userObject.getString("token"))
                .addParams("oldPassword", old)
                .addParams("newPassword", newpass)
                .build()
                .execute(new JsonCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("Exception", e.getMessage().toString());
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.getIntValue("code")!=0){
                            Toast.makeText(EditPassActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.e("onResponse", response.toJSONString());
                        Toast.makeText(EditPassActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(EditPassActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private boolean filter() {
        String password = newpass.getText().toString();
        String password1 = newpass1.getText().toString();
        if (password == null || "".equals(password)) {
            Toast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(password1)) {
            Toast.makeText(this, "两次密码不一致！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
