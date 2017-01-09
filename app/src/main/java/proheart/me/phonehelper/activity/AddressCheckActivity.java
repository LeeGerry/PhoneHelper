package proheart.me.phonehelper.activity;

import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.db.dao.AddressDao;

/**
 * 黑名单查询
 */

public class AddressCheckActivity extends BaseActivity {
    private EditText etNumber;
    private TextView tvResult;
    private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        etNumber = (EditText) findViewById(R.id.etNumber);
        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String num = s.toString().trim();
                tvResult.setText(AddressDao.getAddress(num));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        tvResult = (TextView) findViewById(R.id.tvAddressResult);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }
    public void checkAddress(View view){
        String number = etNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)){
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            etNumber.startAnimation(shake);
            vibrator.vibrate(new long[]{100, 200, 100, 300, 50, 200}, 2);
            showToast(getString(R.string.tips_number_not_null));
            return;
        }
        String result = AddressDao.getAddress(number);
        tvResult.setText(result);
    }
}
