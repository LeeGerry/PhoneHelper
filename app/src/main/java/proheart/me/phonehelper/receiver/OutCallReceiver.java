package proheart.me.phonehelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import proheart.me.phonehelper.activity.LostFoundActivity;
import proheart.me.phonehelper.db.dao.AddressDao;

/**
 * Created by liguorui on 12/22/16.
 */

public class OutCallReceiver extends BroadcastReceiver {
    private final static String TAG = "OutCallReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String number = getResultData();

        if ("1234".equals(number)){
            setResultData(null);
            Intent intent1 = new Intent(context, LostFoundActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
