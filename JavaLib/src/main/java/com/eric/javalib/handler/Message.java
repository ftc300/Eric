package com.eric.javalib.handler;

import java.io.Serializable;

public class Message implements Serializable {

    public int what;
    public Object obj;
    public int sendingUid = -1;
    /**
     * 如果正在使用set message。
     * 此标志在消息排队时设置，在消息排队时保持设置状态。
     * 交付后再回收。该标志只被清除
     * 当创建或获取新消息时，因为这是唯一一次
     * 允许应用程序修改消息的内容。
     * 试图排队或回收已在使用的邮件是一个错误。
     */
    static final int FLAG_IN_USE = 1 << 0;
    /**
     * 如果设置消息是异步的
     */
    static final int FLAG_ASYNCHRONOUS = 1 << 1;

    /**
     * 在CopyFrom方法中要清除的标志
     */
    static final int FLAGS_TO_CLEAR_ON_COPY_FROM = FLAG_IN_USE;

    int flags;
    long when;
    Handler target;
    Runnable callback;
    Message next;
    private static final Object sPoolSync = new Object();
    private static Message sPool;
    private static int sPoolSize = 0;
    private static final int MAX_POOL_SIZE = 50;
    private static boolean gCheckRecycle = true;

    /**
     * Return a new Message instance from the global pool. Allows us to
     * avoid allocating new objects in many cases.
     */
    public static Message obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Message m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0; // clear in-use flag
                sPoolSize--;
                return m;
            }
        }
        return new Message();
    }

    public boolean isAsynchronous() {
        return (flags & FLAG_ASYNCHRONOUS) != 0;
    }

    void markInUse() {
        flags |= FLAG_IN_USE;
    }

    public void setAsynchronous(boolean async) {
        if (async) {
            flags |= FLAG_ASYNCHRONOUS;
        } else {
            flags &= ~FLAG_ASYNCHRONOUS;
        }
    }

    /*package*/ boolean isInUse() {
        return ((flags & FLAG_IN_USE) == FLAG_IN_USE);
    }

    public void recycle() {
        if (isInUse()) {
            if (gCheckRecycle) {
                throw new IllegalStateException("This message cannot be recycled because it "
                        + "is still in use.");
            }
            return;
        }
        recycleUnchecked();
    }

    void recycleUnchecked() {
        // Mark the message as in use while it remains in the recycled object pool.
        // Clear out all other details.
        flags = FLAG_IN_USE;
        what = 0;
        obj = null;
        //replyTo = null;
        sendingUid = -1;
        when = 0;
        target = null;
        callback = null;
        //data = null;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "what=" + what +
                ", obj=" + obj +
                '}';
    }
}