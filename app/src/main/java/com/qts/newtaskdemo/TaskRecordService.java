package com.qts.newtaskdemo;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCountMap = new HashMap<>();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mCountMap.get("");
            }
        };
        Log.d("BQ", "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BQ", "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId != 0) {
        }
        //        mTimer.schedule();
        // 在API11之后构建Notification的方式
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class);

        builder.setContentIntent(PendingIntent.
                getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("下拉列表中的Title") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("要显示的内容") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(101, notification);
        return super.onStartCommand(intent, flags, startId);
    }
}
