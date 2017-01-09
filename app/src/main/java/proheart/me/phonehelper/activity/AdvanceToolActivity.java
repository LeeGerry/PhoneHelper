package proheart.me.phonehelper.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.engine.MessageTools;

/**
 * 高级工具
 */
public class AdvanceToolActivity extends BaseActivity implements View.OnClickListener{
    private TextView tvCheckAddress, tvCommonNumber, tvBlackNumber, tvMsgBackup;//归属地查询，常用号码查询，黑名单管理，短信备份
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_tool);
        tvCheckAddress = (TextView) findViewById(R.id.tvCheckNumberAddress);
        tvCommonNumber = (TextView) findViewById(R.id.tvCommonNumber);
        tvBlackNumber = (TextView) findViewById(R.id.tvBlackNumber);
        tvMsgBackup = (TextView) findViewById(R.id.tvMsgBackup);
        tvBlackNumber.setOnClickListener(this);
        tvCommonNumber.setOnClickListener(this);
        tvCheckAddress.setOnClickListener(this);
        tvMsgBackup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent ;
        switch (view.getId()){
            case R.id.tvCheckNumberAddress:
                intent = new Intent(AdvanceToolActivity.this, AddressCheckActivity.class);
                startActivity(intent);
                break;
            case R.id.tvCommonNumber:
                intent = new Intent(AdvanceToolActivity.this, CommomNumberActivity.class);
                startActivity(intent);
                break;
            case R.id.tvBlackNumber:
                intent = new Intent(AdvanceToolActivity.this, BlackListActivity.class);
                startActivity(intent);
                break;
            case R.id.tvMsgBackup:
                backupMsg();
                break;
        }
    }

    /**
     * 备份短信
     */
    private void backupMsg() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage(getString(R.string.backuping));
        pd.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    MessageTools.backupMsg(AdvanceToolActivity.this, new MessageTools.MsgBackupCallback() {
                        @Override
                        public void beforeBackup(int max) {
                            pd.setMax(max);
                        }

                        @Override
                        public void doInBackgroun(int progress) {
                            pd.setProgress(progress);
                        }
                    });
                    Message msg = Message.obtain();
                    msg.obj = getString(R.string.success);
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.obj = getString(R.string.failed);
                    handler.sendMessage(msg);
                }
                pd.dismiss();
            }
        }.start();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(),msg.obj.toString(),Toast.LENGTH_LONG).show();
        }
    };
}
