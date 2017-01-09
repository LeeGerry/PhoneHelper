package proheart.me.phonehelper.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.db.dao.AddressDao;
import proheart.me.phonehelper.db.dao.BlackNumberDao;

/**
 *
 */

public class ShowAddressService extends Service {
    private TelephonyManager tm;
    private StatusListener listener;
    private InnerReceiver receiver;
    private WindowManager wm;
    private BlackNumberDao dao;
    private View view;
    // {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
    private static final int[] bgs = new int[] { R.drawable.call_locate_white,
            R.drawable.call_locate_orange, R.drawable.call_locate_blue,
            R.drawable.call_locate_gray, R.drawable.call_locate_green };
    @Override
    public void onCreate() {
        receiver = new InnerReceiver();
        dao = new BlackNumberDao(this);
        IntentFilter filter = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(receiver, filter);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        listener = new StatusListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    /**
     * 广播接收者，接受外拨电话，显示归属地信息
     */
    private class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();
            String add = AddressDao.getAddress(number);
            showMyToast(add);

        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class StatusListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE://空闲状态
                    if (view != null) {
                        wm.removeView(view);
                        view = null;
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃状态
                    String address = AddressDao.getAddress(incomingNumber);
                    showMyToast(address);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://通话状态

                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }
    private WindowManager.LayoutParams params;
    /**
     * 自定义显示一个吐司
     * @param address
     */
    private void showMyToast(String address){
        view = View.inflate(this, R.layout.toast_address, null);
        view.setOnTouchListener(new View.OnTouchListener() {
            int startX, startY;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = (int)motionEvent.getRawX();
                        startY = (int)motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int newx = (int)motionEvent.getRawX();
                        int newy = (int)motionEvent.getRawY();
                        int dx = newx - startX;
                        int dy = newy - startY;
                        params.x += dx;
                        params.y += dy;
                        wm.updateViewLayout(view, params);
                        startX = (int)motionEvent.getRawX();
                        startY = (int)motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return true;
            }
        });
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        TextView tv = (TextView) view.findViewById(R.id.tvToast);
        view.setBackgroundResource(bgs[sp.getInt("styleId",0)]);
        tv.setText(address);
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wm.addView(view, params);
    }

}
