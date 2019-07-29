package com.qts.newtaskdemo;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

/**
 * android 5.0以上， 单例模式
 * Created by BoQin on 2019-07-26.
 * Modified by BoQin
 *
 * @Version
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
public class TaskRecordManager {

    private static final String LAST_EVENT = "mLastEvent";
    private SortedMap<Long, UsageStats> mSortedMap = new TreeMap<>(
            new Comparator<Long>() {
                @Override
                public int compare(Long aLong, Long t1) {
                    return t1.compareTo(aLong);
                }
            }
    );

    private TaskRecordManager() {
    }

    public static TaskRecordManager getInstance(){
        return TaskRecordManagerHandler.instance;
    }

//性能问题，过多反射
//    public String getCurrentName(Context context){
//        String result = "";
//        long lastTime = 0;
//        if(checkOps(context)){
//            List<UsageStats> list = getUsageStatsByDay(context);
//            if (!list.isEmpty()) {
//                for (UsageStats usageStats : list) {
//                    if (usageStats.getLastTimeUsed() > lastTime) {
//                        try {
//                            Field field = UsageStats.class.getField("mLastEvent");
//                            if (field.getInt(usageStats) == UsageEvents.Event.MOVE_TO_FOREGROUND) {
//                                lastTime = usageStats.getLastTimeUsed();
//                                result = usageStats.getPackageName();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//            }
//        }
//        return result;
//    }

    /**
     * 获取当前前台的app包名
     */
    public String getCurrentName(Context context){
        String result = "";
        mSortedMap.clear();
        if(checkOps(context)){
            List<UsageStats> list = getUsageStatsByDay(context);
            if (!list.isEmpty()) {
                for (UsageStats usageStats : list) {
                    mSortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
            }
        }
        Iterator<Long> iterable = mSortedMap.keySet().iterator();
        while (iterable.hasNext()) {
            try {
                UsageStats usageStats = mSortedMap.get(iterable.next());
                if (usageStats!=null) {
                    Field field = UsageStats.class.getField(LAST_EVENT);
                    if (field.getInt(usageStats) == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        result = usageStats.getPackageName();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private boolean isForegroud(String packageName, Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            List<UsageStats> list = getUsageStatsByDay(context);
            if (!list.isEmpty() && !TextUtils.isEmpty(packageName)) {
                return packageName.equals(list.get(0).getPackageName());
            }
        }
        return false;
    }

    private List<UsageStats> getUsageStatsByWeekly(Context context){
        List<UsageStats> usageStatsList = new ArrayList<>();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if(usageStatsManager!=null){
            Calendar calendar = Calendar.getInstance();
            long endTime = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_WEEK, -1);
            long startTime = calendar.getTimeInMillis();
            usageStatsList.addAll(usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, startTime, endTime));
        }
        return usageStatsList;
    }

    private List<UsageStats> getUsageStatsByDay(Context context){
        List<UsageStats> usageStatsList = new ArrayList<>();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if(usageStatsManager!=null){
            Calendar calendar = Calendar.getInstance();
            long endTime = calendar.getTimeInMillis();
            usageStatsList.addAll(usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, endTime - 30000, endTime));
        }
        return usageStatsList;
    }

    /**
     * 检测权限
     */
    private boolean checkOps(Context context){
        boolean isAllowed = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), context.getPackageName());

            if (mode == AppOpsManager.MODE_ALLOWED) {
                isAllowed = true;
            }else {
                isAllowed = false;
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
        return isAllowed;
    }

    private static class TaskRecordManagerHandler{

        static TaskRecordManager instance = new TaskRecordManager();

    }
}
