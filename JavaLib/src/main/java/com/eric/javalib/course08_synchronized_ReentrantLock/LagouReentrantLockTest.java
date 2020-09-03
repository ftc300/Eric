package com.eric.javalib.course08_synchronized_ReentrantLock;

import java.util.concurrent.locks.*;

public class LagouReentrantLockTest {
    
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args){
        final LagouReentrantLockTest l1 = new LagouReentrantLockTest();
        
        Thread t1 = new Thread(new Runnable(){
            @Override
            public void run(){
                l1.printLog();
            }
        });
        Thread t2 = new Thread(new Runnable(){
            @Override
            public void run(){
                l1.printLog();
            }
        });

        t1.start();
        t2.start();
    }

    public void printLog(){
        try{
            lock.lock();
            for(int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + " is printing " + i);
            }
        }catch(Exception e){
        } finally{
            lock.unlock();
        }
    }
}
