package proheart.me.phonehelper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import proheart.me.phonehelper.R;

/**
 * 设置引导2
 */
public class SettingActivity2 extends BaseSettingActivity{
    private SharedPreferences sp;
    private ImageView ivBand;
    @Override
    protected void init() {
        setContentView(R.layout.activity_setup2);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        String sim = sp.getString("sim",null);
        ivBand = (ImageView)findViewById(R.id.iv_setup2_status);
        if (!TextUtils.isEmpty(sim)){
            ivBand.setImageResource(R.drawable.lock);
        }else {
            ivBand.setImageResource(R.drawable.unlock);
        }
    }

    @Override
    protected void next(View view) {
        Intent intent = new Intent(this, SettingActivity3.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_next_in,R.anim.tran_next_out);
    }

    @Override
    protected void prev(View view) {
        Intent intent = new Intent(this, SettingActivity1.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_pre_in,R.anim.tran_pre_out);
    }
    public void bindSim(View view){
        if(!TextUtils.isEmpty(sp.getString("sim", null))){
            sp.edit().putString("sim", "").commit();
            ivBand.setImageResource(R.drawable.unlock);
        }else {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String sim = tm.getSimSerialNumber();
            if(!TextUtils.isEmpty(sim)){
                sp.edit().putString("sim", sim).commit();
                ivBand.setImageResource(R.drawable.lock);
            }else {
                showToast("未检测到sim卡");
            }
        }
    }
}
