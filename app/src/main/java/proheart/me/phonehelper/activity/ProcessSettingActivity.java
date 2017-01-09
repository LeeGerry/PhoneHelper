package proheart.me.phonehelper.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.service.AutoKillService;
import proheart.me.phonehelper.utils.ServiceStatusUtils;

/**
 * 进程设置管理
 * Author: Gary
 * Time: 17/1/2
 */

public class ProcessSettingActivity extends BaseActivity {
    private CheckBox cbAutoKill, cbShowSystemProcess;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean showSys = sp.getBoolean("showSys",false);
        setContentView(R.layout.activity_process_setting);
        cbAutoKill = (CheckBox) findViewById(R.id.cb_autokill);
        cbShowSystemProcess = (CheckBox) findViewById(R.id.cb_show_system);
        cbShowSystemProcess.setChecked(showSys);
        cbAutoKill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(ProcessSettingActivity.this, AutoKillService.class);
                if (isChecked){
                    startService(intent);
                }else {
                    stopService(intent);
                }
            }
        });
        cbShowSystemProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean("showSys", isChecked).commit();
            }
        });
    }

    @Override
    protected void onStart() {
        if (ServiceStatusUtils.isServiceRunning(this, "proheart.me.phonehelper.service.AutoKillService")){
            cbAutoKill.setChecked(true);
        }else {
            cbAutoKill.setChecked(false);
        }
        super.onStart();
    }
}
