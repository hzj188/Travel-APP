package com.app.luxingapp.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.luxingapp.App;
import com.app.luxingapp.R;
import com.app.luxingapp.adapter.ImageAdapter;
import com.app.luxingapp.callback.JsonCallback;
import com.app.luxingapp.util.ImageUtil;
import com.app.luxingapp.util.SpsUtil;
import com.app.luxingapp.util.TipUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.compress.OnCompressListener;
import com.luck.picture.lib.config.PictureConfig;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CreateFragment extends Fragment {

    private EditText title;
    private EditText content;
    private ImageView oppenimg;
    private RecyclerView recyclerview;
    private LinearLayout fabull;
    private Button btn_record;
    private Button iv_microphone;
    private TextView tv_recordTime;

    private ImageAdapter imageAdapter;
    List<String> list = new ArrayList<>();

    private String aduiopath;

    private int isRecording = 0;
    private int isPlaying = 0;
    //??????????????????
    private String FilePath = null;
    private Timer mTimer;
    //??????????????????
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;

    final Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1 :
                    String time[] = tv_recordTime.getText().toString().split(":");
                    int hour = Integer.parseInt(time[0]);
                    int minute = Integer.parseInt(time[1]);
                    int second = Integer.parseInt(time[2]);

                    if(second < 59){
                        second++;

                    }
                    else if(second == 59 && minute < 59){
                        minute++;
                        second = 0;

                    }
                    if(second == 59 && minute == 59 && hour < 98){
                        hour++;
                        minute = 0;
                        second = 0;
                    }

                    time[0] = hour + "";
                    time[1] = minute + "";
                    time[2] = second + "";
                    //??????????????????????????????
                    if(second < 10)
                        time[2] = "0" + second;
                    if(minute < 10)
                        time[1] = "0" + minute;
                    if(hour < 10)
                        time[0] = "0" + hour;

                    //?????????TextView???
                    tv_recordTime.setText(time[0]+":"+time[1]+":"+time[2]);

                    break;

            }

        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        title = view.findViewById(R.id.title);
        content = view.findViewById(R.id.content);
        oppenimg = view.findViewById(R.id.oppenimg);
        recyclerview = view.findViewById(R.id.recyclerview);
        fabull = view.findViewById(R.id.fabull);
        btn_record = view.findViewById(R.id.btn_record);
        tv_recordTime = view.findViewById(R.id.tv_recordTime);
        iv_microphone = view.findViewById(R.id.iv_microphone);

        imageAdapter = new ImageAdapter(list);
        recyclerview.setAdapter(imageAdapter);


        oppenimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goCapture();
            }
        });


        fabull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(FilePath)){
                    uploadaradio(FilePath);
                }else {
                    fabuhttp();
                }

            }
        });

        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording == 0){
                    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    if(FilePath != null){
                        File oldFile = new File(FilePath);
                        oldFile.delete();
                    }
                    //?????????????????????????????????????????????????????????
                    SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");
                    Date curDate   =   new Date(System.currentTimeMillis());//??????????????????
                    String   str   =   formatter.format(curDate);

                    str = str + "record.amr";
                    File dir = new File("/sdcard/notes/");
                    File file = new File("/sdcard/notes/",str);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    else{
                        if(file.exists()){
                            file.delete();
                        }
                    }

                    FilePath = dir.getPath() +"/"+ str;
                    //?????????
                    mTimer = new Timer();

                    //???????????????????????????????????????
                    //???????????????????????????00:00:00
                    tv_recordTime.setText("00:00:00");
                    //???????????????????????????
                    isRecording = 1;
                    btn_record.setText("????????????");

                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setOutputFile(FilePath);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                    try {
                        mRecorder.prepare();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mRecorder.start();
                    mTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);

                        }
                    },1000, 1000);
                    //????????????
                }
                //????????????
                else{
                    //???????????????????????????
                    isRecording = 0;
                    btn_record.setText("????????????");
                    mRecorder.stop();
                    mTimer.cancel();
                    mTimer = null;

                    mRecorder.release();
                    mRecorder = null;

                    //????????????????????????????????????
                    //????????????
                    Toast.makeText(getContext(), "???????????????????????????????????????????????????", Toast.LENGTH_LONG).show();
                }
            }
        });
        iv_microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FilePath == null)
                    Toast.makeText(getActivity(), "?????????????????????????????????????????????", Toast.LENGTH_LONG).show();
                else{
                    //??????
                    if(isPlaying == 0){
                        isPlaying = 1;
                        mPlayer = new MediaPlayer();
                        tv_recordTime.setText("00:00:00");
                        mTimer = new Timer();
                        mPlayer.setOnCompletionListener((MediaPlayer.OnCompletionListener) new MediaCompletion());
                        try {
                            mPlayer.setDataSource(FilePath);
                            mPlayer.prepare();
                            mPlayer.start();
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (SecurityException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        mTimer.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);

                            }
                        }, 1000,1000);
                        iv_microphone.setText("????????????");
                        //????????????
                    }
                    //????????????
                    else{
                        isPlaying = 0;
                        mPlayer.stop();
                        mPlayer.release();
                        mPlayer = null;
                        mTimer.cancel();
                        mTimer = null;
                        //????????????
                        iv_microphone.setText("????????????");
                    }
                }
            }
        });
        return view;
    }

    private void fabuhttp() {

        String user = SpsUtil.getString(getContext(), "user", "");
        JSONObject userObject = JSONObject.parseObject(user);
        Log.e("list","list"+list.toString());
        OkHttpUtils
                .post()
                .url("http://121.199.40.253:98/article/create")
                .addHeader("authorization", userObject.getString("token"))
                .addParams("title", title.getText().toString())
                .addParams("content", content.getText().toString())
                .addParams("photos", list.toString().trim().substring(1, list.toString().length() - 1).replace(" ", ""))
                .addParams("audios", aduiopath==null?"":aduiopath)
                .addParams("videos", "")
                .addParams("latitudes", "")
                .addParams("longitudes", "")
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
                        TipUtil.show(getActivity(),response.getString("message"));
                        title.setText("");
                        content.setText("");
                        list.clear();
                        imageAdapter.notifyDataSetChanged();
                    }
                });
    }


    class MediaCompletion implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            mTimer.cancel();
            mTimer = null;
            isPlaying = 0;
            //????????????
            Toast.makeText(getActivity(), "????????????", Toast.LENGTH_LONG).show();
            tv_recordTime.setText("00:00:00");
            iv_microphone.setText("????????????");
        }

    }

    private void goCapture() {
        ImageUtil.loadFragmentLocalPic(CreateFragment.this, 1, false);
    }

    String realPathFromUri;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (PictureSelector.obtainMultipleResult(data).size() > 0) {
                    //??????????????????
                    //??????
                    if (Build.VERSION.SDK_INT > 28) {
                        Uri uri = Uri.parse(PictureSelector.obtainMultipleResult(data).get(0).getPath());
                        realPathFromUri = ImageUtil.getRealPathFromUri(getContext(), uri);
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
            throw new IllegalArgumentException("??????????????????");
        }
        String user = SpsUtil.getString(getContext(), "user", "");
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
                            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.e("onResponse", response.toJSONString());
                        list.add(response.getString("value"));
                        imageAdapter.notifyDataSetChanged();
                    }
                });

    }
    private void uploadaradio(String realPathFromUri) {
        File file = new File(realPathFromUri);
        if (!file.exists()) {
            throw new IllegalArgumentException("??????????????????");
        }
        String user = SpsUtil.getString(getContext(), "user", "");
        JSONObject userObject = JSONObject.parseObject(user);

        OkHttpUtils.post()
                .url("http://121.199.40.253:98/fileUpload")
                .addHeader("authorization", userObject.getString("token"))
                .addParams("type", "audio")
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
                            Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }
                        aduiopath= response.getString("value");
                        fabuhttp();
                        Log.e("onResponse", response.toJSONString());
                    }
                });

    }
}
