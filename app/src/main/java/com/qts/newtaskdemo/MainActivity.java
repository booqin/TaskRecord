package com.qts.newtaskdemo;

import java.util.Calendar;
import java.util.List;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                UsageStatsManager usageStatsManager = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                    int mode = appOpsManager.checkOpNoThrow("android:get_usage_stats",
                            android.os.Process.myUid(), getPackageName());

                    if (mode == AppOpsManager.MODE_ALLOWED) {
                        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
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
                    }else {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);
                    }




                }
            }
        });



    }
}
