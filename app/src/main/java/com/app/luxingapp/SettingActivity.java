package com.app.luxingapp;

import android.app.LauncherActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.app.luxingapp.callback.JsonCallback;
import com.app.luxingapp.fragments.CreateFragment;
import com.app.luxingapp.util.DateDialogUtil;
import com.app.luxingapp.util.ImageUtil;
import com.app.luxingapp.util.ImgUtil;
import com.app.luxingapp.util.SpsUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {


    private ImageView head_iv;
    private TextView nikename;
    private TextView sign;
    private TextView sex;
    private TextView base_right_tv;
    private LinearLayout loguto;
    private LinearLayout editpass;
    private List<String> list=new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        head_iv = findViewById(R.id.head_iv);
        sign = findViewById(R.id.sign);
        nikename = findViewById(R.id.nikename);
        sex = findViewById(R.id.sex);
        editpass = findViewById(R.id.editpass);
        base_right_tv = findViewById(R.id.base_right_tv);
        loguto = findViewById(R.id.loguto);

        list.add("未知");
        list.add("男");
        list.add("女");

        loguto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        head_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goCapture();
            }
        });
        sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdialog(list,sex);
            }
        });

        editpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this,EditPassActivity.class));
            }
        });
        base_right_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(sex.getText().toString())) {
                    Toast.makeText(SettingActivity.this, "请选择性别", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(nikename.getText().toString())) {
                    Toast.makeText(SettingActivity.this, "请输入昵称", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(sign.getText().toString())) {
                    Toast.makeText(SettingActivity.this, "请输入签名", Toast.LENGTH_LONG).show();
                }
                edituser(sex.getText().toString(),nikename.getText().toString(),sign.getText().toString());
            }
        });


        userinfo();


    }

    private void showdialog(List<String> list, TextView view) {
        DateDialogUtil.show(this, new DateDialogUtil.DialogCall() {
            @Override
            public void dateCall(String type, int options1) {
                view.setText(type);
            }
        }, list);
    }


    private void goCapture() {
        ImageUtil.loadActivityLocalPic(SettingActivity.this, 1, false);
    }

    String realPathFromUri;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (PictureSelector.obtainMultipleResult(data).size() > 0) {
                    //上传文件接口
                    //压缩
                    if (Build.VERSION.SDK_INT > 28) {
                        Uri uri = Uri.parse(PictureSelector.obtainMultipleResult(data).get(0).getPath());
                        realPathFromUri = ImageUtil.getRealPathFromUri(SettingActivity.this, uri);
                    } else {
                        realPathFromUri = PictureSelector.obtainMultipleResult(data).get(0).getPath();
                    }
                    uploada(realPathFromUri);
                }
                break;
        }
    }

    private void uploada(String realPathFromUri) {
        File file = new File(realPathFromUri);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        String user = SpsUtil.getString(SettingActivity.this, "user", "");
        JSONObject userObject = JSONObject.parseObject(user);

        OkHttpUtils.post()
                .url("http://121.199.40.253:98/fileUpload")
                .addHeader("authorization", userObject.getString("token"))
                .addParams("type", "photo")
                .addFile("file", file.getName(), file)
                .build()
                .execute(new JsonCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("Exception", e.getMessage().toString());
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.getIntValue("code")!=0){
                            Toast.makeText(SettingActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.e("onResponse", response.toJSONString());
                        edituserheard(response.getString("value"));
                    }
                });

    }

    private void edituser(String se, String nike, String si) {
        String user = SpsUtil.getString(SettingActivity.this, "user", "");
        JSONObject userObject = JSONObject.parseObject(user);
        OkHttpUtils
                .post()
                .url("http://121.199.40.253:98/user/update")
                .addHeader("authorization", userObject.getString("token"))
                .addParams("sex", se.equals("未知")?"0":se.equals("男")?"1":"2")
                .addParams("nikename", nike)
                .addParams("sign", si)
                .build()
                .execute(new JsonCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("Exception", e.getMessage().toString());
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.getIntValue("code")!=0){
                            Toast.makeText(SettingActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(SettingActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                        finish();
//                        Log.e("onResponse", response.toJSONString());
//                        JSONObject jsonObject = response.getJSONObject("value");
//                        ImgUtil.loadImage(head_iv, jsonObject.getString("avatar"));
//                        nikename.setText(jsonObject.getString("nickName"));
//                        sign.setText(jsonObject.getString("sign"));
//                        if (jsonObject.getIntValue("sex") == 0) {
//                            sex.setText("未知");
//                        } else if (jsonObject.getIntValue("sex") == 1) {
//                            sex.setText("男");
//                        } else if (jsonObject.getIntValue("sex") == 2) {
//                            sex.setText("女");
//                        }
                    }
                });
    }

    private void edituserheard(String value) {
        String user = SpsUtil.getString(SettingActivity.this, "user", "");
        JSONObject userObject = JSONObject.parseObject(user);
        OkHttpUtils
                .post()
                .url("http://121.199.40.253:98/user/update")
                .addHeader("authorization", userObject.getString("token"))
                .addParams("avatar", value)
                .build()
                .execute(new JsonCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("Exception", e.getMessage().toString());
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.getIntValue("code")!=0){
                            Toast.makeText(SettingActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.e("onResponse", response.toJSONString());
                        JSONObject jsonObject = response.getJSONObject("value");
                        ImgUtil.loadImage(head_iv, jsonObject.getString("avatar"));
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

    protected void logout() {
        Intent intent = new Intent();
        try {
            intent.setClass(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
        }
    }


    private void userinfo() {
        String user = SpsUtil.getString(SettingActivity.this, "user", "");
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
                            Toast.makeText(SettingActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.e("onResponse", response.toJSONString());
                        JSONObject jsonObject = response.getJSONObject("value");
                        ImgUtil.loadImage(head_iv, jsonObject.getString("avatar"));
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

}
