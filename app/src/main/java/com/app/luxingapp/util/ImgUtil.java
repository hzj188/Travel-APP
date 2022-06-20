package com.app.luxingapp.util;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.app.luxingapp.App;
import com.app.luxingapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;

public class ImgUtil {

    /**
     * 加载图片
     */
    public static void loadImage(ImageView imageView, Object path) {

        if (path == null) {
            imageView.setImageResource(R.mipmap.ic_load_fail);
            return;
        }

        if (path instanceof Integer) {
            imageView.setImageResource((Integer) path);
        } else if (path instanceof String) {
            String url = (String) path;
            url = (url.startsWith("http") || url.contains("storage") || url.contains("sdcard")) ? url : App.RELEASE_IMG_URL + url;
            Glide.with(imageView).load(url).into(new ImageViewTarget<Drawable>(imageView) {
                @Override
                protected void setResource(@Nullable Drawable resource) {
                    imageView.setImageDrawable(resource);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    imageView.setImageResource(R.mipmap.ic_load_fail);
                }
            });
        } else {
//            TipUtil.show("不支持的参数类型");
        }
    }


}
