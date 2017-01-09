package proheart.me.phonehelper.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import java.util.ArrayList;
import java.util.List;

import proheart.me.phonehelper.domain.AppInfo;

/**
 * Author: Gary
 * Time: 17/1/1
 */

public class AppInforProvider {
    /**
     * 手机上的应用列表
     * @param context
     * @return
     */
    public static List<AppInfo> getAppsInfo(Context context){
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        for (PackageInfo info : packageInfoList){
            AppInfo appInfo = new AppInfo();
            String packName = info.packageName;
            appInfo.setPackname(packName == null?"":packName);
            appInfo.setIcon(info.applicationInfo.loadIcon(pm));
            appInfo.setName(info.applicationInfo.loadLabel(pm).toString());
            int uid = info.applicationInfo.uid;
            appInfo.setUid(uid);
            long rx = TrafficStats.getUidRxBytes(uid);//获取当前应用下载流量
            long tx = TrafficStats.getUidTxBytes(uid);//上传流量
            appInfo.setRxByte(rx);
            appInfo.setTxByte(tx);
            int flag = info.applicationInfo.flags;//代表当前应用程序的状态，是很多种状态的任意组合
            if((flag& ApplicationInfo.FLAG_SYSTEM) == 0)//用户应用
                appInfo.setUserApp(true);
            else
                appInfo.setUserApp(false);
            if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0)//手机内存
                appInfo.setInRom(true);
            else
                appInfo.setInRom(false);
            list.add(appInfo);
        }
        return list;
    }
}
