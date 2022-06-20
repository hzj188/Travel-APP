package com.app.luxingapp;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.app.luxingapp.callback.JsonCallback;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

public class RegisterActivity  extends AppCompatActivity {
    private static final String TAG = "";
    private EditText phoneEt, passwordEt, password_et11;
    private Button btn;
    private Cursor cursor;
    private TextView base_back_tv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuce);
        phoneEt = findViewById(R.id.phone_et);
        passwordEt = findViewById(R.id.password_et);
        btn = findViewById(R.id.btn);
        password_et11 = findViewById(R.id.password_et1);
    }

    public void login(View view) {
        apires();
    }

    private void apires() {
        if (filter()) {

            OkHttpUtils
                    .post()
                    .url("http://121.199.40.253:98/user/register")
                    .addParams("username", phone)
                    .addParams("password", password)
                    .build()
                    .execute(new JsonCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            Log.e("Exception",e.getMessage().toString());
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(RegisterActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
        }
    }

    private String phone, password, password1;

    private boolean filter() {
        phone = phoneEt.getText().toString();
        password = passwordEt.getText().toString();
        password1 = password_et11.getText().toString();
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
