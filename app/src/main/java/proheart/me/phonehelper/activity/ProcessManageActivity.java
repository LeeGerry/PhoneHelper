package proheart.me.phonehelper.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.domain.ProcessInfo;
import proheart.me.phonehelper.engine.ProcessProvider;
import proheart.me.phonehelper.utils.StorageUtils;

/**
 * 进程管理界面
 * Author: Gary
 * Time: 17/1/2
 */
public class ProcessManageActivity extends BaseActivity{
    private TextView tvProcessCount, tvMemInfo, tvStatus;
    private long availableMem, totalMem;
    private LinearLayout llLoad;
    private ListView lvProcess;
    private List<ProcessInfo> processList, userList, sysList;
    private ProcessAdapter adapter;
    private static int countOfRunningProcess;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        setContentView(R.layout.activity_process_manage);
        lvProcess = (ListView) findViewById(R.id.lv_task_manager);
        tvProcessCount = (TextView) findViewById(R.id.tv_process_count);
        tvMemInfo = (TextView) findViewById(R.id.tv_mem_info);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        llLoad = (LinearLayout) findViewById(R.id.ll_loading);
        lvProcess.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userList != null && sysList != null){
                    if (firstVisibleItem > userList.size())
                        tvStatus.setText(getString(R.string.sys_process)+sysList.size());
                    else tvStatus.setText(getString(R.string.user_process) + userList.size());
                }
            }
        });
        lvProcess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = lvProcess.getItemAtPosition(position);
                if (obj == null)    return;
                ProcessInfo info = (ProcessInfo)obj;
                if (getPackageName().equals(info.getPackname()))    return;
                CheckBox cb = (CheckBox) view.findViewById(R.id.cb_status);
                if (cb.isChecked()){
                    cb.setChecked(false);
                    info.setChecked(false);
                }else {
                    cb.setChecked(true);
                    info.setChecked(true);
                }
            }
        });
        loadProcessData();


    }

    /**
     * 加载数据
     */
    private void loadProcessData() {
        llLoad.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                processList = ProcessProvider.getRunningProcesses(ProcessManageActivity.this);
                userList = new ArrayList<ProcessInfo>();
                sysList = new ArrayList<ProcessInfo>();
                for (ProcessInfo info: processList){
                    if (info.isUsertask())  userList.add(info);
                    else                    sysList.add(info);
                }
                countOfRunningProcess = processList.size();
                totalMem = StorageUtils.getTotalMemorySize(ProcessManageActivity.this);
                availableMem = StorageUtils.getAvailableMemory(ProcessManageActivity.this);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            llLoad.setVisibility(View.INVISIBLE);
            initTitle();
        }
    };

    //更新数据
    private void initTitle(){
        tvProcessCount.setText(getString(R.string.runningProcess) + countOfRunningProcess);
        tvMemInfo.setText(getString(R.string.mem)
                +Formatter.formatFileSize(this,availableMem)
                +"/"+Formatter.formatFileSize(this, totalMem));
        adapter = new ProcessAdapter();
        lvProcess.setAdapter(adapter);
    }

    private class ProcessAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            boolean showSys = sp.getBoolean("showSys",false);
            if (showSys){
                return userList.size()+sysList.size()+2;
            }else {
                return userList.size()+1;
            }

        }

        @Override
        public Object getItem(int position) {
            if (position == 0 || position == userList.size()+1) return null;
            else if (position <= userList.size())               return userList.get(position-1);
            else                                                return sysList.get(position-userList.size()-2);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view;
            ProcessInfo info;
            if (position == 0){
                TextView tv = new TextView(ProcessManageActivity.this);
                tv.setText(getString(R.string.user_process)+userList.size());
                return tv;
            } else if(position == (userList.size()+1)){
                TextView tv = new TextView(ProcessManageActivity.this);
                tv.setText(getString(R.string.sys_process)+sysList.size());
                return tv;
            } else if (position <= userList.size())    {
                info = userList.get(position - 1);
            } else{
                int pos = position - userList.size() -2;
                info = sysList.get(pos);
            }

            if (convertView != null && convertView instanceof RelativeLayout){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else {
                view = View.inflate(ProcessManageActivity.this, R.layout.list_task_item, null);
                holder = new ViewHolder();
                holder.tvName = (TextView) view.findViewById(R.id.tv_name);
                holder.tvSize = (TextView) view.findViewById(R.id.tv_memsize);
                holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.cb = (CheckBox) view.findViewById(R.id.cb_status);
                view.setTag(holder);
            }
            holder.tvName.setText(info.getName());
            holder.tvSize.setText(Formatter.formatFileSize(getApplicationContext(),info.getMemsize()));
            holder.ivIcon.setImageDrawable(info.getIcon());
            holder.cb.setChecked(info.isChecked());
            if (getPackageName().equals(info.getPackname())){
                holder.cb.setVisibility(View.INVISIBLE);
            }else {
                holder.cb.setVisibility(View.VISIBLE);
            }
            return view;
        }
    }

    private static class ViewHolder{
        TextView tvName, tvSize;
        ImageView ivIcon;
        CheckBox cb;
    }

    /**
     * 全选
     * @param view
     */
    public void selectAll(View view){
        for (ProcessInfo info: userList){
            if (getPackageName().equals(info.getPackname()))    continue;
            info.setChecked(true);
        }
        for (ProcessInfo info: sysList){
            info.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选
     * @param view
     */
    public void selectOpp(View view){
        for (ProcessInfo info: userList){
            if (getPackageName().equals(info.getPackname()))    continue;
            info.setChecked(!info.isChecked());
        }
        for (ProcessInfo info: sysList){
            info.setChecked(!info.isChecked());
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * kill all
     * @param view
     */
    public void killAll(View view){
        int count = 0;
        long savedMem = 0;
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ProcessInfo> killedProcessList = new ArrayList<>();
        for (ProcessInfo info: userList){
            if (info.isChecked()){
                am.killBackgroundProcesses(info.getPackname());
                count++;
                savedMem+=info.getMemsize();
                killedProcessList.add(info);
            }
        }
        for (ProcessInfo info: sysList){
            if (info.isChecked()){
                am.killBackgroundProcesses(info.getPackname());
                count++;
                savedMem+=info.getMemsize();
                killedProcessList.add(info);
            }
        }
        for (ProcessInfo info: killedProcessList){
            if (info.isUsertask()){
                userList.remove(info);
            }else {
                sysList.remove(info);
            }
        }
        availableMem+=savedMem;
        countOfRunningProcess -= count;
        loadProcessData();
        showToast("killed "+count+" processes and released "+Formatter.formatFileSize(this, savedMem)+" memory.");
        adapter.notifyDataSetChanged();
    }

    /**
     * 进入设置
     * @param view
     */
    public void enterSetting(View view){
        Intent intent = new Intent(this, ProcessSettingActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadProcessData();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
