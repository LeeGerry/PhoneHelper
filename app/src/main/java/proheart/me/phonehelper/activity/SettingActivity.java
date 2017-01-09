package proheart.me.phonehelper.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.service.CallSmsSafeService;
import proheart.me.phonehelper.service.ShowAddressService;
import proheart.me.phonehelper.ui.SettingClickView;
import proheart.me.phonehelper.ui.SettingView;
import proheart.me.phonehelper.utils.ServiceStatusUtils;

/**
 * 设置中心界面
 */

public class SettingActivity extends BaseActivity {
    private SettingView svUpdate, svAddress, svBlackIntercept;//自动更新设置，归属地显示设置
    private SharedPreferences sp;
    private Intent showAddressService;//显示归属地的service
    private SettingClickView scvBgSelect, scvAddressPosition;//选择归属地显示背景，设置归属地显示位置
    private static String[] bgStyles = {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        svUpdate = (SettingView)findViewById(R.id.svSetting1);
        svAddress = (SettingView) findViewById(R.id.svSetAddress);
        scvBgSelect = (SettingClickView) findViewById(R.id.svAddressBgSelect);
        scvAddressPosition = (SettingClickView) findViewById(R.id.svAddressPosition);
        svBlackIntercept = (SettingView) findViewById(R.id.svBlackIntercept);
        //根据配置显示自动更新勾选
        sp = getSharedPreferences("config",MODE_PRIVATE);
        boolean update = sp.getBoolean("update", false);
        if (update){
            svUpdate.setChecked(true);
        }else {
            svUpdate.setChecked(false);
        }
        svUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                if (svUpdate.isChecked()){
                    svUpdate.setChecked(false);
                    editor.putBoolean("update", false);
                }else {
                    svUpdate.setChecked(true);
                    editor.putBoolean("update", true);
                }
                editor.commit();
            }
        });
        showAddressService = new Intent(this, ShowAddressService.class);
        svAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (svAddress.isChecked()){
                    svAddress.setChecked(false);
                    stopService(showAddressService);
                }else {
                    startService(showAddressService);
                    svAddress.setChecked(true);
                }
            }
        });
        //根据配置显示所选择的归属地显示样式
        final int styleId = sp.getInt("styleId", 0);
        scvBgSelect.setDesc(bgStyles[styleId]);
        //归属地背景设置
        scvBgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle(getString(R.string.address_style));
                builder.setIcon(android.R.drawable.btn_star_big_on);
                builder.setSingleChoiceItems(bgStyles, styleId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sp.edit().putInt("styleId", i).commit();
                        scvBgSelect.setDesc(bgStyles[i]);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });
        scvAddressPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingActivity.this, SetAddressPosition.class);
                startActivity(i);
            }
        });

        final Intent blackInterceptService = new Intent(SettingActivity.this, CallSmsSafeService.class);
        svBlackIntercept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (svBlackIntercept.isChecked()){
                    svBlackIntercept.setChecked(false);
                    stopService(blackInterceptService);
                }else {
                    svBlackIntercept.setChecked(true);
                    startService(blackInterceptService);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        //判断归属地显示服务是否开启
        if (ServiceStatusUtils.isServiceRunning(this, "proheart.me.phonehelper.service.ShowAddressService")){
            svAddress.setChecked(true);
        }else {
            svAddress.setChecked(false);
        }

        //判断归属地显示服务是否开启
        if (ServiceStatusUtils.isServiceRunning(this, "proheart.me.phonehelper.service.CallSmsSafeService")){
            svBlackIntercept.setChecked(true);
        }else {
            svBlackIntercept.setChecked(false);
        }
        super.onStart();
    }
}
