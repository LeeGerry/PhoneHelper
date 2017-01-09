package proheart.me.phonehelper.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.domain.AppInfo;
import proheart.me.phonehelper.engine.AppInforProvider;

/**
 * 流量统计,借用AppManageActivity界面
 * Author: Gary
 * Time: 17/1/1
 */

public class DataStatisticActivity extends BaseActivity {
    private ListView lvApp;
    private LinearLayout llLoading;
    private List<AppInfo> list;//安装的应用
    private AppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_statistic);
        lvApp = (ListView) findViewById(R.id.lvAppList);
        llLoading = (LinearLayout) findViewById(R.id.llLoading);
        loadData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            llLoading.setVisibility(View.INVISIBLE);
            if (adapter == null) {
                adapter = new AppAdapter();
                lvApp.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * 加载信息
     */
    private void loadData() {
        llLoading.setVisibility(View.VISIBLE);//设置loading可见
        new Thread() {
            @Override
            public void run() {
                list = AppInforProvider.getAppsInfo(DataStatisticActivity.this);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private class AppAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppInfo info;
            info = list.get(position);

            View view;
            ViewHolder holder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) convertView.getTag();
            } else {
                view = View.inflate(DataStatisticActivity.this, R.layout.list_app_item, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tvName = (TextView) view.findViewById(R.id.tv_name);
                holder.tvLoc = (TextView) view.findViewById(R.id.tv_location);
                view.setTag(holder);
            }
            holder.ivIcon.setImageDrawable(info.getIcon() == null ? getDrawable(R.drawable.ic_launcher) : info.getIcon());
            StringBuilder sb = new StringBuilder();
            sb.append(getString(R.string.upload))
                    .append(Formatter.formatFileSize(getApplicationContext(), info.getTxByte()))
                    .append(getString(R.string.download)).append(Formatter.formatFileSize(getApplicationContext(), info.getRxByte()));
            holder.tvLoc.setText(sb.toString());
            holder.tvName.setText(info.getName() == null ? "" : info.getName());
            return view;
        }

    }

    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvName, tvLoc;
    }

}
