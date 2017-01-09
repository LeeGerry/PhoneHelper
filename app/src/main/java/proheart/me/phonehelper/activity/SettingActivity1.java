package proheart.me.phonehelper.activity;

import android.content.Intent;
import android.view.View;

import proheart.me.phonehelper.R;

/**
 * 设置引导1
 */

public class SettingActivity1 extends BaseSettingActivity {

    @Override
    protected void init() {
        setContentView(R.layout.activity_setting1);
    }

    @Override
    protected void next(View view) {
        Intent intent = new Intent(this, SettingActivity2.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_next_in,R.anim.tran_next_out);
    }

    @Override
    protected void prev(View view) {
        //overridePendingTransition(R.anim.tran_pre_in,R.anim.tran_pre_out);
    }

}
