package proheart.me.phonehelper.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.engine.ProcessProvider;
import proheart.me.phonehelper.receiver.KillProcessWidget;
import proheart.me.phonehelper.utils.StorageUtils;

/**
 * Author: Gary
 * Time: 17/1/3
 */

public class UpdateWidgetService extends Service {
    private ScreenOffReceiver screenOffReceiver;
    private ScreenOnReceiver screenOnReceiver;
    private Timer timer;
    private TimerTask task;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private class ScreenOnReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (timer == null && task == null){
                startTimer();
            }
        }

    }

    private class ScreenOffReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            cancelTimer();//监听锁屏事件，锁屏后停止更新，省电
        }
    }

    @Override
    public void onCreate() {
        screenOnReceiver = new ScreenOnReceiver();
        registerReceiver(screenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));

        screenOffReceiver = new ScreenOffReceiver();
        registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        startTimer();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        cancelTimer();
        unregisterReceiver(screenOnReceiver);
        unregisterReceiver(screenOffReceiver);
        screenOffReceiver = null;
        screenOnReceiver = null;
        super.onDestroy();
    }

    private void startTimer(){
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                //更新桌面widget界面
                AppWidgetManager am = AppWidgetManager.getInstance(getApplicationContext());
                //指定widget的组件
                ComponentName widget = new ComponentName(getApplicationContext(), KillProcessWidget.class);
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);

                views.setTextViewText(R.id.process_count, getString(R.string.runningProcess)+ ProcessProvider.getRunningProcesses(getApplicationContext()).size());
                String size = Formatter.formatFileSize(getApplicationContext(), StorageUtils.getAvailableMemory(getApplicationContext()));
                views.setTextViewText(R.id.process_memory, getString(R.string.availableMem)+size);
                //用延期的意图发送一个自定义广播
                Intent intent = new Intent();
                intent.setAction("com.proheart.me.phonehelper.killall");
                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                views.setOnClickPendingIntent(R.id.btn_clear, pi);
                am.updateAppWidget(widget, views);
                Log.i("UpdateWidgetService", "widget update...");
            }
        };
        timer.schedule(task, 1000, 3000);
    }
    private void cancelTimer(){
        if (timer != null && task != null){
            timer.cancel();
            timer = null;
            task = null;
        }
    }
}
