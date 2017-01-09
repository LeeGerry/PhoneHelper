package proheart.me.phonehelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.service.GPSService;

/**
 * Created by liguorui on 12/27/16.
 */

public class SmsReceiver extends BroadcastReceiver {
    private final static String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        for (Object obj : objs) {
            SmsMessage sm = SmsMessage.createFromPdu((byte[]) obj);
            String body = sm.getMessageBody();
            String from = sm.getOriginatingAddress();
            if ("#*location*#".equals(body)) {
                Intent intent1 = new Intent(context, GPSService.class);
                context.startService(intent1);
                SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                String lastLocation = sp.getString("lastlocation", "");
                String safeNumber = sp.getString("safenumber", "");
//                Log.i(TAG, "返回手机位置," + lastLocation+"， safenumber: "+safeNumber);
                if (!TextUtils.isEmpty(lastLocation) && !TextUtils.isEmpty(safeNumber)) {
                    SmsManager smanager = SmsManager.getDefault();
                    smanager.sendTextMessage(safeNumber, null, lastLocation, null, null);
                }
                abortBroadcast();
            } else if ("#*alarm*#".equals(body)) {
                Log.i(TAG, "报警");
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1.0f, 1.0f);
                player.start();
                abortBroadcast();
            } else if ("#*lockscreen*#".equals(body)) {
                Log.i(TAG, "锁屏");
                abortBroadcast();
            } else if ("#*wipedata*#".equals(body)) {
                Log.i(TAG, "清除信息");
                abortBroadcast();
            }
        }
    }

}
