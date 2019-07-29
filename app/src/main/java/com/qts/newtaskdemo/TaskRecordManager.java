package com.qts.newtaskdemo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * android 5.0以上， 单例模式
 * Created by BoQin on 2019-07-26.
 * Modified by BoQin
 *
 * @Version
 */
public class TaskRecordManager {

    private TaskRecordManager() {
    }

    public TaskRecordManager getInstance(){
        return TaskRecordManagerHandler.instance;
    }

    public void setTask(@NonNull Context context){
        UsageStatsManager usageStatsManager = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            if(usageStatsManager!=null){
                Calendar calendar = Calendar.getInstance();
                long endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_WEEK, -1);
                long startTime = calendar.getTimeInMillis();
                List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, startTime, endTime);
            }
        }
    }

    private boolean isForegroud(String packageName, Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            List<UsageStats> list = getUsageStats(context);
            if (!list.isEmpty() && !TextUtils.isEmpty(packageName)) {
                return packageName.equals(list.get(0).getPackageName());
            }
        }
        return false;
    }

    private List<UsageStats> getUsageStats(Context context){
        List<UsageStats> usageStatsList = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            if(usageStatsManager!=null){
                Calendar calendar = Calendar.getInstance();
                long endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_WEEK, -1);
                long startTime = calendar.getTimeInMillis();
                usageStatsList.addAll(usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, startTime, endTime));
            }
        }
        return usageStatsList;
    }

    private static class TaskRecordManagerHandler{

        static TaskRecordManager instance = new TaskRecordManager();

    }
}
