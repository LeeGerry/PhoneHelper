package proheart.me.phonehelper.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import proheart.me.phonehelper.utils.Config;

/**
 * 设置中心界面的基类
 */

public abstract class BaseSettingActivity extends BaseActivity{
    public SharedPreferences sp;
    private GestureDetector myGester;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(Config.CONFIG, MODE_PRIVATE);
        myGester = new GestureDetector(BaseSettingActivity.this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) < 100)
                    return true;
                if ((e2.getRawX() - e1.getRawX()) > 200){
                    prev(null);
                    return true;
                }
                if ((e1.getRawX() - e2.getRawX()) > 200){
                    next(null);
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        myGester.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    abstract protected void init();
    abstract protected void next(View view);
    abstract protected void prev(View view);
}
