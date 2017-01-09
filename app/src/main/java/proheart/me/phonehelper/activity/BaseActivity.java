package proheart.me.phonehelper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * activity基类
 */

public class BaseActivity extends Activity {
    public static String TAG ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getName();
    }
    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    protected void mylog(String message){
        Log.i(TAG,message);
    }
}
