package com.eric.javalib.handler;

import com.eric.javalib.Log;

public class Looper {
    private static final String TAG = "Looper";

    private static Looper sMainLooper;  // guarded by Looper.class
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();

    final MessageQueue mQueue;
    final Thread mThread;


    private Looper(boolean quitAllowed) {
        mQueue = new MessageQueue(quitAllowed);
        mThread = Thread.currentThread();
    }

    /**
     * 将当前线程初始化为循环器，并将其标记为
     * 应用程序的主环。应用程序的主循环器
     * 由Android环境创建，因此您不应该需要
     * 自行调用此函数。另请参见：@link prepare（）
     */
    public static void prepareMainLooper() {
        prepare(false);
        synchronized (Looper.class) {
            if (sMainLooper != null) {
                throw new IllegalStateException("The main Looper has already been prepared.");
            }
            sMainLooper = myLooper();
        }
    }
    public static Looper getMainLooper() {
        synchronized (Looper.class) {
            return sMainLooper;
        }
    }

    public static void prepare() {
        prepare(true);
    }

    private static void prepare(boolean quitAllowed) {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        Log.d(TAG,"创建了一个新的Looper");
        sThreadLocal.set(new Looper(quitAllowed));
    }

    public static void loop() {
        final Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        final MessageQueue queue = me.mQueue;

        for (;;) {
            Message msg = queue.next(); // might block
            if (msg == null) {
                // No message indicates that the message queue is quitting.
                return;
            }
            msg.target.dispatchMessage(msg);
            msg.recycleUnchecked();
        }
    }
    public static Looper myLooper() {
        return sThreadLocal.get();
    }
}