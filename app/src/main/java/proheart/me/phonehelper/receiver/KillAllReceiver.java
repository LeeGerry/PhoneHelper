package proheart.me.phonehelper.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import proheart.me.phonehelper.domain.ProcessInfo;
import proheart.me.phonehelper.engine.ProcessProvider;

/**
 * 杀死后台进程
 * Author: Gary
 * Time: 17/1/3
 */

public class KillAllReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ProcessInfo> list = ProcessProvider.getRunningProcesses(context);
        for (ProcessInfo info: list){
            am.killBackgroundProcesses(info.getPackname());
        }
    }
}
