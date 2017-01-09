package proheart.me.phonehelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by liguorui on 12/27/16.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    private SharedPreferences sp;
    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean protect = sp.getBoolean("protecting", false);
        if (protect){
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String realSim = tm.getSimSerialNumber()+1;//获取当前手机sim卡串号
            String bindSim = sp.getString("sim", "");//获取绑定的sim卡串号
            if(!realSim.equals(bindSim)){//若不同，在后台向安全号码发送一条报警短信
                String safeNumber = sp.getString("safenumber","");
                if (!TextUtils.isEmpty(safeNumber)){
                    SmsManager sm = SmsManager.getDefault();
                    sm.sendTextMessage(safeNumber, null, "sim card changed", null, null);
                }
            }
        }
    }
}
