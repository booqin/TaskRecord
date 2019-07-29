package com.qts.newtaskdemo;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.text.TextUtils;

/**
 * 小任务记录服务
 * Created by BoQin on 2019-07-26.
 * Modified by BoQin
 *
 * @Version
 */
public class TaskRecordService extends Service {

    private static final String CHANNEL_ONE_ID = TaskRecordService.class.getSimpleName();
    Map<String, Integer> mCountMap;

    private Timer mTimer;

    private TimerTask mTimerTask;

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
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    String pn = TaskRecordManager.getInstance().getCurrentName(TaskRecordService.this);
                    if (TextUtils.isEmpty(pn)) {
                        return;
                    }else {
                        Integer count = mCountMap.get(pn);
                        if (count == null) {
                            count = 0;
                        }
                        count ++ ;
                        mCountMap.put(pn, count);

                        if (mBuilder==null) {
                            mBuilder = initBuilder();
                        }

                        mBuilder.setContentTitle(pn) // 设置下拉列表里的标题
                                .setContentText("运行"+count);
                        Notification notification = mBuilder.build();
                        notification.defaults = Notification.DEFAULT_SOUND;
                        startForeground(101, notification);
                    }

                }

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

    /**
     * 初始化Notification的Builder，需要处理8.0+的channel配置
     */
    private Notification.Builder initBuilder(){
        Notification.Builder builder = new Notification.Builder(TaskRecordService.this.getApplicationContext());
        Intent nfIntent = new Intent(TaskRecordService.this, MainActivity.class);

        builder.setContentIntent(PendingIntent.
                getActivity(TaskRecordService.this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(TaskRecordService.this.getResources(),
                        R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel= new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_ID, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ONE_ID);
        }

        return builder;
    }
}
