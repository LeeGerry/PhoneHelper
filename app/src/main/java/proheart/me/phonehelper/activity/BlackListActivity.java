package proheart.me.phonehelper.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.db.dao.BlackNumberDao;
import proheart.me.phonehelper.domain.BlackNumberInfo;

/**
 * 黑名单管理
 * Author: Gary
 * Time: 16/12/30
 */

public class BlackListActivity extends BaseActivity {
    private static final int MAX_NUMBER = 20;
    private ListView lvBlack;
    private BlackNumberAdapter adapter;
    private BlackNumberDao dao;
    private List<BlackNumberInfo> list;
    private static int totalNumber;
    private LinearLayout llLoading;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            llLoading.setVisibility(View.INVISIBLE);//收到通知更新界面的消息后把loading界面隐藏
            if (adapter == null){//如果adapter为空，说明是第一次加载，需要实例化适配器并赋给view
                adapter = new BlackNumberAdapter();
                lvBlack.setAdapter(adapter);
            }else {//否则为加载更多，通知适配器更新
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        lvBlack = (ListView) findViewById(R.id.lvBlackList);
        llLoading = (LinearLayout) findViewById(R.id.llLoading);
        dao = new BlackNumberDao(BlackListActivity.this);
        totalNumber = dao.getCount();
        /**初始化一些测试数据
        long baseNumber = 18618196930l;
        Random r = new Random();
        for (int i = 0;i<100;i++)
            dao.add(String.valueOf(baseNumber+i), String.valueOf(r.nextInt(3)+1));
        */
        lvBlack.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        int lastPosition = lvBlack.getLastVisiblePosition();//记录界面上最后显示的item
                        int size = list.size();
                        if (lastPosition == (size-1)){//滚动到最后一条
                            if (size>=totalNumber){//如果等于数据库中的总条数，就返回
                                showToast(getString(R.string.no_more_data));
                                return;
                            }
                            loadData();//否则继续加载
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://手指触摸滚动状态

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING://惯性滚动

                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        loadData();
    }

    /**
     * 加载数据
     */
    private void loadData(){
        llLoading.setVisibility(View.VISIBLE);//设置loading显示
        new Thread() {
            @Override
            public void run() {
                if (list==null){//如果列表为空，说明是第一次查询，则从0开始查，并把结果赋给list
                    list = dao.findPart(0,MAX_NUMBER);
                }else {//不空，说明不是第一次加载数据，需要加载更多；此时从list.size()开始查，结果增加给list
                    list.addAll(dao.findPart(list.size(), MAX_NUMBER));
                }
                handler.sendEmptyMessage(0);//通知更新适配器
            }
        }.start();
    }

    /**
     * 黑名单listView的适配器
     */
    private class BlackNumberAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            ViewHolder holder;
            if(convertView != null){
                v = convertView;
                holder = (ViewHolder) v.getTag();
            }else {
                v = android.view.View.inflate(BlackListActivity.this, R.layout.list_callsms_item, null);
                holder = new ViewHolder();
                holder.tvPhone = (TextView) v.findViewById(R.id.tv_phone);
                holder.tvMode = (TextView) v.findViewById(R.id.tv_mode);
                holder.ivDel = (ImageView) v.findViewById(R.id.iv_delete);
                v.setTag(holder);
            }

            final BlackNumberInfo info = list.get(position);
            holder.tvPhone.setText(info.getNumber());
            holder.tvMode.setText(getModeByIndex(info.getMode()));
            //为删除按钮增加点击监听
            holder.ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BlackListActivity.this);
                    builder.setTitle(getString(R.string.delete_tips));
                    builder.setMessage(getString(R.string.confirm_delete));
                    builder.setPositiveButton(getString(R.string.confirm_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dao.del(info.getNumber());//删除数据库中的item
                            list.remove(info);//删除列表中的item
                            totalNumber = dao.getCount();//更新总条数
                            adapter.notifyDataSetChanged();//通知适配器更新
                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancel),null);
                    builder.show();
                }
            });
            return v;
        }
    }

    /**
     * 标记类，帮助优化ListView
     */
    private static class ViewHolder{
        TextView tvPhone, tvMode;
        ImageView ivDel;
    }

    /**
     * 根据模式号返回模式名
     * @param index
     * @return
     */
    private String getModeByIndex(String index) {
        if (index.equals("1"))
            return getString(R.string.phoneIntercept);
        else if (index.equals("2"))
            return getString(R.string.messageIntercept);
        else
            return getString(R.string.allIntercept);
    }

    private EditText etPhone;
    private RadioGroup rgMode;
    private Button btnOk, btnCancel;
    /**
     * 添加黑名单号码
     * @param view
     */
    public void addBlackNumber(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(BlackListActivity.this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(BlackListActivity.this, R.layout.dialog_add_black_number,null);
        dialog.setView(dialogView,0,0,0,0);
        etPhone = (EditText) dialogView.findViewById(R.id.et_phone);
        rgMode = (RadioGroup) dialogView.findViewById(R.id.rg_mode);
        btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numb = etPhone.getText().toString().trim();
                if(TextUtils.isEmpty(numb)){
                    showToast(getString(R.string.tips_number_not_null));
                    return;
                }
                int id = rgMode.getCheckedRadioButtonId();
                String mode;
                if (id == R.id.rb_all)          mode = "3";//全部拦截
                else if(id == R.id.rb_phone)    mode = "1";//电话拦截
                else                            mode = "2";//短信拦截
                dao.add(numb, mode);//向数据库中增加item
                BlackNumberInfo info = new BlackNumberInfo();
                info.setNumber(numb);
                info.setMode(mode);
                list.add(0,info);//更新列表
                totalNumber = dao.getCount();//更新总条数
                adapter.notifyDataSetChanged();//通知适配器更新
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
