package proheart.me.phonehelper.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.ui.CameraPreview;
import proheart.me.phonehelper.utils.Md5Utils;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {
    private GridView gvMain;
    private final static String TAG = "MainActivity";
    private SharedPreferences sp;
    private static final String[] names = {"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "系统优化", "高级工具", "设置中心"};
    private static final int[] icons = {R.drawable.safe,
            R.drawable.callmsgsafe, R.drawable.app, R.drawable.taskmanager,
            R.drawable.netmanager, R.drawable.trojan, R.drawable.sysoptimize,
            R.drawable.atools, R.drawable.settings};
    private GVAdapter adapter;
    private Camera mCamera;
    private CameraPreview mPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        gvMain = (GridView) findViewById(R.id.gvMain);
        adapter = new GVAdapter();
        gvMain.setAdapter(adapter);
        gvMain.setOnItemClickListener(new GVItemClickListener());

        /**模拟器没有摄像头，真机下可开启
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
         */
    }

    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    /**
     * 判断是否设置了密码
     * @return
     */
    private boolean isSetup(){
        String pwd = sp.getString("pwd",null);
        if(!TextUtils.isEmpty(pwd))
            return true;
        else
            return false;
    }

    /**
     * GridView点击事件
     */
    private class GVItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent;
            switch (position) {
                case 0:
                    if(isSetup())
                        showEnterPwdDialog();
                    else
                        showDialog();
                    break;
                case 1:

                    break;
                case 2:
                    intent = new Intent(MainActivity.this, AppManageActivity.class);
                    startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(MainActivity.this, ProcessManageActivity.class);
                    startActivity(intent);
                    break;
                case 4:
                    intent = new Intent(MainActivity.this, DataStatisticActivity.class);
                    startActivity(intent);
                    break;
                case 5:

                    break;
                case 6:

                    break;
                case 7:
                    intent = new Intent(MainActivity.this, AdvanceToolActivity.class);
                    startActivity(intent);
                    break;
                case 8:
                    intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    /**
     * 设置密码对话框
     */
    private void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(MainActivity.this, R.layout.ui_setting_pwd, null);
        final EditText etPwd1 = (EditText) view.findViewById(R.id.etPwd1);
        final EditText etPwd2 = (EditText) view.findViewById(R.id.etPwd2);
        Button btnOk = (Button) view.findViewById(R.id.btnOk);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd1 = etPwd1.getText().toString().trim();
                String pwd2 = etPwd2.getText().toString().trim();
                if (TextUtils.isEmpty(pwd1) || TextUtils.isEmpty(pwd2)) {
                    showToast("密码不能为空");
                    return;
                }
                if (!pwd1.equals(pwd2)) {
                    showToast("输入不一致");
                    return;
                }
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("pwd", Md5Utils.encode(pwd1));
                editor.commit();
                dialog.dismiss();
                showEnterPwdDialog();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 输入密码对话框
     */
    private void showEnterPwdDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.create();
        final String password = sp.getString("pwd","");
        View view = View.inflate(MainActivity.this, R.layout.ui_enter_pwd, null);
        final EditText enterPwd = (EditText) view.findViewById(R.id.etEnterPwd);
        Button btnOk = (Button) view.findViewById(R.id.btnOk);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = enterPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    showToast("密码不能为空");
                    return;
                }
                if (!Md5Utils.encode(pwd).equals(password)) {
                    showToast("密码错误，请重试");
                    /**开启摄像头，拍照，真机下可开启
                    mCamera.autoFocus(null);
                    mCamera.takePicture(null,null,pictureCallback);*/
                    return;
                }
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, LostFoundActivity.class);
                startActivity(intent);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(view, 0, 0, 0, 0);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 主界面GRIDVIEW适配器
     */
    private class GVAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MainActivity.this, R.layout.list_home_item, null);
            ImageView iv = (ImageView) view.findViewById(R.id.ivGVItem);
            TextView tv = (TextView) view.findViewById(R.id.tvGVItem);
            iv.setImageResource(icons[position]);
            tv.setText(names[position]);
            if (position == 0){
                String name = sp.getString("newName","");
                Log.i(TAG,name);
                if (!TextUtils.isEmpty(name))
                    tv.setText(name);
            }
            return view;
        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            // 设置为1，表示前置摄像头
            c = Camera.open(1); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pic = new File(Environment.getExternalStorageDirectory(),"temp.jpg");
            try {
                FileOutputStream fos = new FileOutputStream(pic);
                fos.write(data);
                fos.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onDestroy() {
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
        super.onDestroy();
    }
    final int REQ_CODE = 100;



}
