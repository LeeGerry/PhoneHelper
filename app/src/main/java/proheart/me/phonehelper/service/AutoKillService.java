package proheart.me.phonehelper.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;

import proheart.me.phonehelper.domain.ProcessInfo;
import proheart.me.phonehelper.engine.ProcessProvider;

/**
 * Author: Gary
 * Time: 17/1/2
 */

public class AutoKillService extends Service {
    private InnerScreenOffReceiver receiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        receiver = new InnerScreenOffReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
        super.onCreate();
    }

    private class InnerScreenOffReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ProcessInfo> list = ProcessProvider.getRunningProcesses(context);
            for (ProcessInfo info:list){
                am.killBackgroundProcesses(info.getPackname());
            }

        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }
}
