package com.cokimutai.med_manager;

import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Handler;

public class AppExecutors {

    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread){
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }
    public static AppExecutors getsInstance(){
        if (sInstance == null){
            synchronized (LOCK){
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return sInstance;
    }
    public Executor diskIO(){
        return diskIO;
    }

    public Executor getMainThread() {
        return mainThread;
    }

    public Executor getNetworkIO() {
        return networkIO;
    }
    private static class MainThreadExecutor implements Executor{
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(@NonNull Runnable command ){
            mainThreadHandler.post(command);
        }
    }
}
