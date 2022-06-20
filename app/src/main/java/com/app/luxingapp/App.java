 package com.app.luxingapp;

 import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

 public class App extends Application implements Application.ActivityLifecycleCallbacks {

     public static final String RELEASE_IMG_URL = "http://121.199.40.253:98/photo/";


     @Override
     public void onCreate() {
         super.onCreate();
     }



     @Override
     protected void attachBaseContext(Context base) {
         super.attachBaseContext(base);

     }

     @Override
     public void onActivityCreated(Activity activity, Bundle bundle) {

     }

     @Override
     public void onActivityStarted(Activity activity) {
     }

     @Override
     public void onActivityResumed(Activity activity) {
     }

     @Override
     public void onActivityPaused(Activity activity) {
     }

     @Override
     public void onActivityStopped(Activity activity) {
     }

     @Override
     public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
     }

     @Override
     public void onActivityDestroyed(Activity activity) {
     }
 }
