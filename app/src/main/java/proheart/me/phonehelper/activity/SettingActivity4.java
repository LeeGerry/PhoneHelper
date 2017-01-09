package proheart.me.phonehelper.activity;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import proheart.me.phonehelper.R;

/**
 * 设置引导4
 */
public class SettingActivity4 extends BaseSettingActivity{
    private CheckBox cbProtect;
    @Override
    protected void init() {
        setContentView(R.layout.activity_setup4);
        boolean protect = sp.getBoolean("protecting", false);
        cbProtect = (CheckBox) findViewById(R.id.cb_setup4_status);
        cbProtect.setChecked(protect);
        if (protect)
            cbProtect.setText(getString(R.string.protect));
        else
            cbProtect.setText(getString(R.string.unprotect));
        cbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    cbProtect.setText(getString(R.string.protect));
                else
                    cbProtect.setText(getString(R.string.unprotect));
                sp.edit().putBoolean("protecting",isChecked).commit();
            }
        });
    }

    @Override
    protected void next(View view) {
        sp.edit().putBoolean("configed", true).commit();
        Intent intent = new Intent(this, LostFoundActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    @Override
    protected void prev(View view) {
        Intent intent = new Intent(this, SettingActivity3.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_pre_in,R.anim.tran_pre_out);
    }
}
