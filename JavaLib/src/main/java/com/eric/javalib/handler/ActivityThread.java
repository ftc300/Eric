package com.eric.javalib.handler;

import com.eric.javalib.Log;

public class ActivityThread {
    private static final String TAG = "Main";

    public static void main(String[] args) {
        Log.d(TAG, "主线程：" + Thread.currentThread().getName());
        Looper.prepareMainLooper();
        new Application().main();
        Looper.loop();
    }
    private static class Application{
        private static final String TAG = "Application";
        public void main(){
            Log.d(TAG, "Application create.");
            startActivity();
        }

        private void startActivity(){
            Activity activity = new Activity();
            activity.onCreate();
        }
    }

    private static class Activity {
        private static final String TAG = "Activity";
        Handler mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.d(TAG, "当前线程：" + Thread.currentThread().getName());
                Log.d(TAG, "拦截消息：" + msg.obj);
                return false;
            }
        });

        public void onCreate() {
            Log.d(TAG, "onCreate");
            sendTest();
        }

        private void sendTest() {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    //Looper.prepare();

                    for (int i = 0; i < 2; i++) {
                        Log.d(TAG, "-----------");

                        Message msg = new Message();
                        msg.obj = "123";
                        msg.what = 1;
                        Log.d(TAG, "准备发送：" + msg + " 当前线程:" + Thread.currentThread().getName());
                        mHandler.sendMessage(msg);

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
}