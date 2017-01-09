package proheart.me.phonehelper.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import proheart.me.phonehelper.R;

/**
 * 手机防盗
 */

public class LostFoundActivity extends BaseActivity {
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config",MODE_APPEND);
        boolean configed = sp.getBoolean("configed",false);
        if (configed){
            setContentView(R.layout.activity_lost_find);
            TextView tvNumber = (TextView) findViewById(R.id.tv_lostfind_number);
            tvNumber.setText(sp.getString("safenumber",""));
            ImageView ivStatus = (ImageView) findViewById(R.id.iv_lostfind_status);
            boolean protect = sp.getBoolean("protecting",false);
            if(protect)
                ivStatus.setImageResource(R.drawable.lock);
            else
                ivStatus.setImageResource(R.drawable.lock);
        }else {
            Intent intent = new Intent(this, SettingActivity1.class);
            startActivity(intent);
            finish();
        }


    }

    public void reEntrySetup(View view){
        Intent intent = new Intent(this, SettingActivity1.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.lost_find_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.change_name){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("更改手机防盗名字");
            final EditText etName=  new EditText(this);
            etName.setHint("输入新名字");
            builder.setView(etName);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = etName.getText().toString().trim();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("newName",name);
                    editor.commit();
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
