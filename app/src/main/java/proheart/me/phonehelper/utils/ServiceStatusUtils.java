package proheart.me.phonehelper.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by liguorui on 16/12/29.
 */

public class ServiceStatusUtils {
    /**
     * 检测服务是否在运行
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info: services){
            if (serviceName.equals(info.service.getClassName()))
                return true;
        }
        return false;
    }
}
