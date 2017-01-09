package proheart.me.phonehelper.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import proheart.me.phonehelper.R;

/**
 * 设置来电显示位置
 * Author: Gary
 * Time: 16/12/30
 */
public class SetAddressPosition extends BaseActivity {
    private TextView tvTop, tvBottom;
    private ImageView ivDrag;
    private SharedPreferences sp;
    private WindowManager wm;
    private int screenHeight, screenWidth;//屏幕高度和宽度
    private int startX, startY;//手指的起始位置坐标

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_position_setting);
        tvTop = (TextView) findViewById(R.id.tvLocationTop);
        tvBottom = (TextView) findViewById(R.id.tvLocationBottom);
        ivDrag = (ImageView) findViewById(R.id.ivDrag);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        wm = getWindowManager();
        screenHeight = wm.getDefaultDisplay().getHeight();
        screenWidth = wm.getDefaultDisplay().getWidth();
        int lastX = sp.getInt("lastX", 0);
        int lastY = sp.getInt("lastY", 0);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();
        params.leftMargin = lastX;
        params.topMargin = lastY;
        if (lastY > screenHeight / 2) {
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        } else {
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }
        ivDrag.setLayoutParams(params);
        final long[] hits = new long[2];//双击事件
        //设置点击监听
        ivDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.arraycopy(hits, 1, hits, 0, hits.length - 1);
                hits[hits.length - 1] = SystemClock.uptimeMillis();
                if (SystemClock.uptimeMillis() - hits[0] < 500) {
                    ivDrag.layout(screenWidth / 2 - ivDrag.getWidth() / 2, ivDrag.getTop(), screenWidth / 2 + ivDrag.getWidth() / 2, ivDrag.getBottom());
                    savePosition();
                }
            }
        });
        //设置拖动监听事件
        ivDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN://手指触摸到屏幕
                        //记录起始位置
                        startX = (int) motionEvent.getRawX();
                        startY = (int) motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE://手指在屏幕上移动
                        //新位置
                        int newX = (int) motionEvent.getRawX();
                        int newY = (int) motionEvent.getRawY();
                        //计算偏移量
                        int dX = newX - startX;
                        int dY = newY - startY;
                        //更新imageiew显示位置
                        int newt = ivDrag.getTop() + dY;
                        int newl = ivDrag.getLeft() + dX;
                        int newr = ivDrag.getRight() + dX;
                        int newb = ivDrag.getBottom() + dY;
                        //如果向上 向左 向右 向下 超出屏幕则停止计算
                        if (newt < 0 || newl < 0 || newr > screenWidth || newb >= screenHeight)
                            break;
                        //如果移到屏幕下半部分，上方提示显示，下方隐藏
                        if (newt > screenHeight / 2) {
                            tvTop.setVisibility(View.VISIBLE);
                            tvBottom.setVisibility(View.INVISIBLE);
                        } else {//否则相反
                            tvTop.setVisibility(View.INVISIBLE);
                            tvBottom.setVisibility(View.VISIBLE);
                        }
                        //绘制imageView
                        ivDrag.layout(newl, newt, newr, newb);
                        //更新起始位置
                        startX = (int) motionEvent.getRawX();
                        startY = (int) motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_UP://手指离开屏幕
                        savePosition();
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 保存最后的位置，以供再次进入界面时绘制和归属地显示时绘制
     */
    private void savePosition(){
        int lastX = ivDrag.getLeft();
        int lastY = ivDrag.getTop();
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("lastX", lastX);
        editor.putInt("lastY", lastY);
        editor.commit();
    }
}
