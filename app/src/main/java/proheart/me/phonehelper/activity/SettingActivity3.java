package proheart.me.phonehelper.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import proheart.me.phonehelper.R;

/**
 * 设置引导3
 */
public class SettingActivity3 extends BaseSettingActivity{
    private EditText etNumber;
    @Override
    protected void init() {
        setContentView(R.layout.activity_setup3);
        etNumber = (EditText) findViewById(R.id.et_phone);
        etNumber.setText(sp.getString("safenumber", ""));
    }

    @Override
    protected void next(View view) {
        String num = etNumber.getText().toString().trim();
        if (TextUtils.isEmpty(num)){
            showToast("号码不能为空");
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("safenumber",num);
        editor.commit();
        Intent intent = new Intent(this, SettingActivity4.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_next_in,R.anim.tran_next_out);
    }

    @Override
    protected void prev(View view) {
        Intent intent = new Intent(this, SettingActivity2.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_pre_in,R.anim.tran_pre_out);
    }

    /**
     * 选择联系人
     * @param view
     */
    public void selectContact(View view){
        Intent intent = new Intent(this, ChooseContactActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            String number = data.getStringExtra("phone");
            etNumber.setText(number);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
