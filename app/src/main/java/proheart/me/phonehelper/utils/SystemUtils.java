package proheart.me.phonehelper.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;

/**
 * Created by liguorui on 12/20/16.
 */

public class SystemUtils {
    /**
     * 安装APK
     * @param ctx
     * @param apkFile
     */
    public static void installApp(Context ctx, File apkFile) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.INSTALL_PACKAGE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 获取应用版本号
     *
     * @param ctx
     * @return
     */
    public static String getAppVersion(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取可用sd空间
     *
     * @return
     */
    public static String getAvailSD(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long size = blockSize * availableBlocks;
        return Formatter.formatFileSize(context, size);
    }

    /**
     * 获取可用内存
     *
     * @return
     */
    public static String getAvailMem(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long size = blockSize * availableBlocks;
        return Formatter.formatFileSize(context, size);
    }


}
