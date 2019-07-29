package com.qts.newtaskdemo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

/**
 * android 5.0以上
 * Created by BoQin on 2019-07-26.
 * Modified by BoQin
 *
 * @Version
 */
public class TaskRecordManager {

    Context mContext;

    public void setTask(){
        UsageStatsManager usageStatsManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            usageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
            if(usageStatsManager!=null){
                Calendar calendar = Calendar.getInstance();
                long endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_WEEK, -1);
                long startTime = calendar.getTimeInMillis();
                List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, startTime, endTime);
                if (usageStats!=null) {
                    usageStats.toString();
                }
            }
        }
    }

    private boolean isForegroud(String packageName){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<UsageStats> list = getUsageStats();
            if (!list.isEmpty() && !TextUtils.isEmpty(packageName)) {
                return packageName.equals(list.get(0).getPackageName());
            }
        }
        return false;
    }

    private List<UsageStats> getUsageStats(){
        List<UsageStats> usageStatsList = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
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
}
