package proheart.me.phonehelper.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.domain.UpdateInfo;
import proheart.me.phonehelper.engine.UpdateInfoParser;
import proheart.me.phonehelper.utils.DownLoadUtils;
import proheart.me.phonehelper.utils.SystemUtils;

/**
 * 划屏界面
 */

public class SplashActivity extends BaseActivity {
    private TextView tvVersion;
    private SharedPreferences sp;
    private UpdateInfo info;
    private Handler handler;
    private final static String TAG = "SplashActivity";
    private final static int NO_UPDATE = 1, UPDATE = 2, XMLERROR = 3, CONN_PROTOCOL = 4, CONN_URL = 5;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        String version = SystemUtils.getAppVersion(this);
        if (TextUtils.isEmpty(version)){
            tvVersion.setText("version:"+version);
        }
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case NO_UPDATE:XMLERROR:CONN_PROTOCOL:CONN_URL:
                        showMain();
                        break;
                    case UPDATE:
                        showDialog();
                        break;
                }
            }
        };
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
             // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        boolean autoUpdate = sp.getBoolean("update",false);
        if (autoUpdate){
            checkAppVersion();
        }else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMain();
                }
            },500);
        }

        AlphaAnimation animation = new AlphaAnimation(0.2f, 1f);
        animation.setDuration(1000);
        findViewById(R.id.rlRoot).setAnimation(animation);
        copyAssetsDB("address.db");
        copyAssetsDB("commonnum.db");
    }

    private void copyAssetsDB(final String dbName){
        File file = new File(getFilesDir(), dbName);
        if (file.exists()){
            mylog("db 已存在");
            return;
        }

        new Thread(){
            @Override
            public void run() {
                mylog("db copy");
                try {
                    InputStream is = getAssets().open(dbName);
                    File file = new File(getFilesDir(), dbName);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buf = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buf))!=-1)
                        fos.write(buf, 0, len);
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 进入主界面
     */
    private void showMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 检查版本号
     */
    private void checkAppVersion() {
        new Thread(){
            @Override
            public void run() {
                try {
                    String serverUrl = getString(R.string.appurl);
                    URL url = new URL(serverUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    int code = conn.getResponseCode();
                    if(code == 200){
                        InputStream is = conn.getInputStream();
                        info = UpdateInfoParser.getUpdateInfo(is);
                        if(info.getVersion().equals(SystemUtils.getAppVersion(SplashActivity.this))){
                            Thread.sleep(1000);
                            handler.sendEmptyMessage(NO_UPDATE);
                        }else {
                            handler.sendEmptyMessage(UPDATE);
                        }
                    }else{

                    }
                } catch (XmlPullParserException e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(XMLERROR);
                } catch (ProtocolException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(CONN_PROTOCOL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(CONN_URL);
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(NO_UPDATE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(NO_UPDATE);
                }
            }
        }.start();
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("升级提示");
        String des = info.getDesc();
        if (TextUtils.isEmpty(des))
            des = "";
        builder.setMessage(des);
        builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //showToast("download : "+ info.getApkurl());
                final ProgressDialog pd = new ProgressDialog(SplashActivity.this);
                pd.setTitle("更新提示");
                pd.setMessage("正在下载...");
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.show();
                pd.setCancelable(false);
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    new Thread(){
                        @Override
                        public void run() {
                            File f = new File(Environment.getExternalStorageDirectory(),"temp.apk");
                            DownLoadUtils.downLoad(info.getApkurl(), f.getAbsolutePath(), pd);
                            if (f == null){
                                showMain();
                            }else {
                                SystemUtils.installApp(SplashActivity.this, f);
                            }
                            pd.dismiss();
                        }
                    }.start();
                }else{
                    showToast("sd卡不可用");
                    showMain();
                }
            }
        });
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showMain();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}
