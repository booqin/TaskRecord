package com.qts.newtaskdemo;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;

/**
 * 小任务记录服务
 * Created by BoQin on 2019-07-26.
 * Modified by BoQin
 *
 * @Version
 */
public class TaskRecordService extends Service {

    Map<String, Integer> mCountMap;

    private Timer mTimer;

    private TimerTask mTimerTask;

    private int mCount = 0;
    private Notification.Builder mBuilder;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCountMap = new HashMap<>();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mCountMap.get("");
                mCount ++ ;
                if (mBuilder!=null) {
                    mBuilder.setContentText("运行"+mCount);
                }else {
                    mBuilder = new Notification.Builder(TaskRecordService.this.getApplicationContext());
                    Intent nfIntent = new Intent(TaskRecordService.this, MainActivity.class);

                    mBuilder.setContentIntent(PendingIntent.
                            getActivity(TaskRecordService.this, 0, nfIntent, 0)) // 设置PendingIntent
                            .setLargeIcon(BitmapFactory.decodeResource(TaskRecordService.this.getResources(),
                                    R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                            .setContentTitle("下拉列表中的Title") // 设置下拉列表里的标题
                            .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                            .setContentText("运行"+mCount) // 设置上下文内容
                            .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
                }
                Notification notification = mBuilder.build();
                notification.defaults = Notification.DEFAULT_SOUND;
                startForeground(101, notification);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer!=null) {
            mTimer.cancel();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mTimer!=null) {
            mTimer.schedule(mTimerTask, 0, 1000);
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
