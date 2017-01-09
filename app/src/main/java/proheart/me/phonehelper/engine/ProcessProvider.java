package proheart.me.phonehelper.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import proheart.me.phonehelper.domain.ProcessInfo;
import proheart.me.phonehelper.utils.ProcessManagerTools;

/**
 * Author: Gary
 * Time: 17/1/2
 */

public class ProcessProvider {
    /**
     * 获取正在运行中的进程
     * @param context
     * @return
     */
    public static List<ProcessInfo> getRunningProcesses(Context context){
        List<ProcessInfo> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();//该API在6.0以上不能得到运行中的进程
        List<ProcessInfo> result = new ArrayList<>();
        List<ProcessManagerTools.Process> processList = ProcessManagerTools.getRunningApps();//在网上找的另外一种方法来获得运行中的进程信息
        for (ProcessManagerTools.Process info: processList){
            ProcessInfo pInfo = new ProcessInfo();
            String pkgName = info.name;//得到包名（上面的具体方法在ProcessManagerTools工具类中，这里保留了之前的逻辑，只从中拿到）
            if (!TextUtils.isEmpty(pkgName)){//包名不空才是应用
                pInfo.setPackname(pkgName);//model设置包名
                long memsize = am.getProcessMemoryInfo(new int[]{info.pid})[0].getTotalPrivateDirty()*1024;
                pInfo.setMemsize(memsize);//Model设置占用存储大小
                try {
                    ApplicationInfo applicationInfo = pm.getApplicationInfo(pkgName, 0);//根据包名得到应用信息
                    Drawable icon = applicationInfo.loadIcon(pm);//获取应用的ICON
                    pInfo.setIcon(icon);
                    String name = applicationInfo.loadLabel(pm).toString();
                    pInfo.setName(name);
                    if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM) ==0){
                        //用户应用
                        pInfo.setUsertask(true);
                    }else{
                        //系统应用
                        pInfo.setUsertask(false);
                    }
                } catch (Exception e) {//如果没有图标就设置默认图标，名字为空则设置为包名
                    //e.printStackTrace();
                    pInfo.setIcon(context.getResources().getDrawable(android.R.drawable.sym_def_app_icon));
                    pInfo.setName(pkgName);
                }
                result.add(pInfo);
            }

        }

        return result;
    }
}
