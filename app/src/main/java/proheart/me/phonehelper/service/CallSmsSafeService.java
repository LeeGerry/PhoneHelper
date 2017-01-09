package proheart.me.phonehelper.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import proheart.me.phonehelper.db.dao.BlackNumberDao;

/**
 * Author: Gary
 * Time: 16/12/31
 */

public class CallSmsSafeService extends Service {
    public final static String TAG = "CallSmsSafeService";
    private BlackNumberDao dao;
    private InnerSmsReceiver receiver;
    private TelephonyManager tm;
    private PhoneListener listener;
    @Override
    public void onCreate() {
        dao = new BlackNumberDao(this);
        receiver = new InnerSmsReceiver();
        tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        //注册短信广播接收者
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(1000);
        registerReceiver(receiver,filter);
        //监听用户的电话状态
        listener = new PhoneListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private class InnerSmsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "内部broadcast， 收到了短信广播");
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj: objs){
                SmsMessage smsMessage= SmsMessage.createFromPdu((byte[]) obj);
                String sender = smsMessage.getOriginatingAddress();
                String body = smsMessage.getMessageBody();
                if (body.contains("fapiao")){//举例拦截发票短信的例子，可扩展业务逻辑
                    Log.i(TAG,"发现发票短信，进行拦截");
                    abortBroadcast();
                }
                //查询sender是否存在于黑名单
                String mode = dao.findMode(sender);
                if ("2".equals(mode) || "3".equals(mode)){//模式为短信拦截或者全部拦截
                    Log.i(TAG,"发现黑名单电话发了短信，进行拦截");
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        //destroy时unregister接收短信的广播
        unregisterReceiver(receiver);
        receiver = null;
        //destroy时取消监听电话状态
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;
        super.onDestroy();
    }

    private class PhoneListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING://响铃状态
                    Log.i(TAG, "incoming call:"+incomingNumber);
                    String mode = dao.findMode(incomingNumber);
                    if ("1".equals(mode) || "3".equals(mode)){//模式为电话拦截或者全部拦截
                        endCall();//挂断
                        //注册内容观察者，观察电话记录的数据库内容变化。挂断后可能数据库还没有产生记录，直接删除不会成功
                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri, true, new MyObserver(new Handler(), incomingNumber));
                    }
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 挂断电话
     */
    private void endCall() {
        try {
            Class clz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method=  clz.getDeclaredMethod("getService",String.class);
            IBinder binder = (IBinder)method.invoke(null, TELEPHONY_SERVICE);
            ITelephony.Stub.asInterface(binder).endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除电话记录
     * @param incomingCall
     */
    private void deleteBlackNumber(String incomingCall){
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri, "number=?", new String[]{incomingCall});

    }

    /**
     * 通话记录内容观察者
     */
    private class MyObserver extends ContentObserver {
        private String incomingCall;
        public MyObserver(Handler handler, String number) {
            super(handler);
            this.incomingCall = number;
        }

        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().unregisterContentObserver(this);//当内容变化的时候注销内容观察者
            deleteBlackNumber(this.incomingCall);//此时删除电话记录
            super.onChange(selfChange);
        }
    }
}
