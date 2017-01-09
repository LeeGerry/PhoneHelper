package proheart.me.phonehelper.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import proheart.me.phonehelper.service.UpdateWidgetService;

/**
 * Author: Gary
 * Time: 17/1/3
 */

public class KillProcessWidget extends AppWidgetProvider {
    //第一个widget被创建
    @Override
    public void onEnabled(Context context) {
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
        super.onEnabled(context);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    //最后一个被消除
    @Override
    public void onDisabled(Context context) {
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.stopService(intent);
        super.onDisabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
