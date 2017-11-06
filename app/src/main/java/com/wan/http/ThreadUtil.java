package com.wan.http;

import android.os.Handler;
import android.os.Looper;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Author wan
 * Created on 2016/7/1.
 */
public class ThreadUtil {
    //用简单的数量线程池
    private static final int MAX_NUM = 10;
    private static ExecutorService mPool = Executors.newFixedThreadPool(MAX_NUM);
    //向future 对列中添加
    private static final Hashtable<Future, CallBack> futures = new Hashtable<>();

    static {
        //每隔0.1秒读取完成的future，回调callBack方法
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep(100);
                callBack();
            }
        }).start();
    }

    //新起一个线程并回调返回值处理方法
    public static <T> void executeAndReturn(CallBack<T> callable) {
        Future<T> future = mPool.submit(callable);
        futures.put(future, callable);
    }

    //call的返回值也就是回调接口的返回值
    public interface CallBack<T> extends Callable<T>, Result<T> {
    }

    private static void callBack() {
        for (Map.Entry<Future, CallBack> entry : futures.entrySet()) {
            if (entry.getKey().isDone())
                try {
                    entry.getValue().back(entry.getKey().get());
                    futures.remove(entry.getKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    public static void newThread(Runnable run) {
        mPool.execute(run);
    }

    private static Handler mDelivery = new Handler(Looper.getMainLooper());

    public static void mainThread(Runnable run) {
        mDelivery.post(run);
    }

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //新起一个线程并回调到主线程
    public static<T> void asyncBack(final CallBack<T> callable){
        newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final T o = callable.call();
                    mainThread(new Runnable() {
                        @Override
                        public void run() {
                            callable.back(o);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
