package proheart.me.phonehelper.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.domain.AppInfo;
import proheart.me.phonehelper.engine.AppInforProvider;
import proheart.me.phonehelper.utils.StorageUtils;

/**
 * 软件管理
 * Author: Gary
 * Time: 17/1/1
 */

public class AppManageActivity extends BaseActivity implements View.OnClickListener{
    private ListView lvApp;
    private TextView tvMem, tvSdCard, tvAppCount;
    private LinearLayout llLoading;
    private List<AppInfo> list, userList, sysList;//安装的应用，用户应用，系统应用
    private AppAdapter adapter;
    private PopupWindow popupWindow;
    private LinearLayout llShare, llStart, llUninstall;
    private AppInfo info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manage);
        lvApp = (ListView) findViewById(R.id.lvAppList);
        tvMem = (TextView) findViewById(R.id.tvAvailableMem);
        tvSdCard = (TextView) findViewById(R.id.tvAvailableSD);
        tvAppCount = (TextView) findViewById(R.id.tvAppCount);
        llLoading = (LinearLayout) findViewById(R.id.llLoading);
        tvMem.setText(getString(R.string.availableMem)+StorageUtils.getAvailableMemory(this));
        tvSdCard.setText(getString(R.string.availableSD)+StorageUtils.getAvailableExternalMemorySize());
        sysList = new ArrayList<>();
        userList = new ArrayList<>();
        lvApp.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPop();
                if (firstVisibleItem > userList.size()){
                    tvAppCount.setText(getString(R.string.sys_app)+":"+sysList.size());
                }else {
                    tvAppCount.setText(getString(R.string.user_app)+":"+userList.size());
                }
            }
        });
        lvApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = lvApp.getItemAtPosition(position);
                if (obj == null)    return;
                info = (AppInfo)obj;
                dismissPop();
                View v = View.inflate(getApplicationContext(), R.layout.popup_app_item, null);
                llShare = (LinearLayout) v.findViewById(R.id.ll_share);
                llStart = (LinearLayout) v.findViewById(R.id.ll_start);
                llUninstall = (LinearLayout) v.findViewById(R.id.ll_uninstall);
                llStart.setOnClickListener(AppManageActivity.this);
                llShare.setOnClickListener(AppManageActivity.this);
                llUninstall.setOnClickListener(AppManageActivity.this);
                popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int[] location = new int[2];
                view.getLocationInWindow(location);
                popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 60, location[1]);
                AlphaAnimation aa = new AlphaAnimation(0.2f,1.0f);
                aa.setDuration(500);
                TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.0f,
                        Animation.RELATIVE_TO_SELF,0.1f,
                        Animation.RELATIVE_TO_SELF,0.0f,
                        Animation.RELATIVE_TO_SELF,0.0f);
                ta.setDuration(50);
                AnimationSet as = new AnimationSet(true);
                as.addAnimation(ta);
                as.addAnimation(aa);
                v.startAnimation(as);
            }
        });
        loadData();
    }

    /**
     * 隐藏pop
     */
    private void dismissPop(){
        if (popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            llLoading.setVisibility(View.INVISIBLE);
            if (adapter == null){
                adapter = new AppAdapter();
                lvApp.setAdapter(adapter);
            }else {
                adapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * 加载信息
     */
    private void loadData(){
        llLoading.setVisibility(View.VISIBLE);//设置loading可见
        new Thread(){
            @Override
            public void run() {
                list = AppInforProvider.getAppsInfo(AppManageActivity.this);
                sysList.clear();
                userList.clear();
                for (AppInfo info: list){//把用户和系统应用区分开
                    if (info.isUserApp()){
                        userList.add(info);
                    }
                    else{
                        sysList.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_share:
                share();
                break;
            case R.id.ll_start:
                open();
                break;
            case R.id.ll_uninstall:
                uninstall();
                break;
        }
    }

    /**
     * 分享
     */
    private void share(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"recommend apk download: http://aa.com/"+info.getPackname());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadData();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 删除
     */
    private void uninstall(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:"+info.getPackname()));
        startActivityForResult(intent,0);
    }

    /**
     * 打开
     */
    private void open(){
        String packageName = info.getPackname();
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activityInfos = packageInfo.activities;
            if (activityInfos!= null &&activityInfos.length>0){
                String clzName = activityInfos[0].name;
                Intent intent = new Intent();
                intent.setClassName(packageName,clzName);
                startActivity(intent);
            }else {
                showToast(getString(R.string.no_ui));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }
    private class AppAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return list.size()+2;
        }

        @Override
        public Object getItem(int position) {
            AppInfo appInfo;
            if (position == 0 || position == userList.size()+1) return null;
            else if(position <= userList.size())    appInfo = userList.get(position-1);
            else    appInfo = sysList.get(position - userList.size() - 2);
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppInfo info ;
            if (position == 0){//第一个特殊位置
                TextView tv = new TextView(getApplicationContext());
                tv.setText(getString(R.string.user_app)+":"+userList.size());
                return tv;
            } else if (position == userList.size()+1) {//第二个特殊位置
                TextView tv = new TextView(getApplicationContext());
                tv.setText(getString(R.string.sys_app)+":"+sysList.size());
                return tv;
            }else if (position<=userList.size()){//用户程序
                info = userList.get(position-1);//减去最上面的位置
            }else {//系统程序
                info = sysList.get(position-userList.size()-2);//减去上面的两个特殊结构，再减去用户程序的个数
            }

            View view;
            ViewHolder holder;
            if (convertView != null && convertView instanceof RelativeLayout){
                view = convertView;
                holder = (ViewHolder) convertView.getTag();
            }else {
                view = View.inflate(AppManageActivity.this, R.layout.list_app_item, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tvName = (TextView) view.findViewById(R.id.tv_name);
                holder.tvLoc = (TextView) view.findViewById(R.id.tv_location);
                view.setTag(holder);
            }
            holder.ivIcon.setImageDrawable(info.getIcon() == null?getDrawable(R.drawable.ic_launcher):info.getIcon());
            holder.tvLoc.setText(info.isInRom()?getString(R.string.inrom):getString(R.string.outrom));
            holder.tvName.setText(info.getName()==null?"":info.getName());
            return view;
        }

    }
    private static class ViewHolder{
        ImageView ivIcon;
        TextView tvName, tvLoc;
    }

    @Override
    protected void onDestroy() {
        dismissPop();
        super.onDestroy();
    }
}
