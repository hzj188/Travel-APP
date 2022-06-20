package com.app.luxingapp.callback;

import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.callback.Callback;


import java.io.IOException;

public abstract class JsonCallback extends Callback<JSONObject>
{
    //非UI线程，支持任何耗时操作
    @Override
    public JSONObject parseNetworkResponse(Response response) throws IOException
    {
        String string = response.body().string();
        JSONObject user = JSONObject.parseObject(string);
        return user;
    }
}