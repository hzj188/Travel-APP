package com.app.luxingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.app.luxingapp.callback.JsonCallback;
import com.app.luxingapp.util.SpsUtil;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText phoneEt, passwordEt;
    private static final String SHARE_PHONE = "phone";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneEt = findViewById(R.id.phone_et);
        passwordEt = findViewById(R.id.password_et);
        phoneEt.setSelection(phoneEt.getText().length());
    }


    public void login(View view) {
        if (filter()) {
            apiLogin();
        }
    }

    public void register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void apiLogin() {
        OkHttpUtils
                .post()
                .url("http://121.199.40.253:98/user/login")
                .addParams("username", phone)
                .addParams("password", password)
                .build()
                .execute(new JsonCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("Exception", e.getMessage().toString());
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.getIntValue("code")!=0){
                            Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                        SpsUtil.put("user",response.getJSONObject("value").toJSONString(),getApplicationContext());
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                });
    }

    private String phone, password;

    private boolean filter() {
        phone = phoneEt.getText().toString();
        password = passwordEt.getText().toString();

        if ((phone == null || "".equals(phone))) {
            Toast.makeText(this, "请输入正确的账号", Toast.LENGTH_LONG).show();
            return false;
        }

        if (password == null || "".equals(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
