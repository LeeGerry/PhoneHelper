package proheart.me.phonehelper.activity;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.db.dao.CommonNumberDao;

/**
 * 常用号码查询
 * Author: Gary
 * Time: 16/12/30
 */

public class CommomNumberActivity extends BaseActivity {
    private ExpandableListView elv;
    private ExAdapter adapter;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        elv = (ExpandableListView) findViewById(R.id.elvNumber);
        db = SQLiteDatabase.openDatabase(CommonNumberDao.dbPath, null,SQLiteDatabase.OPEN_READONLY);
        adapter = new ExAdapter();
        elv.setAdapter(adapter);
    }
    private class ExAdapter extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            return CommonNumberDao.getGroupCount(db);
        }

        @Override
        public int getChildrenCount(int i) {
            return CommonNumberDao.getChildrenCountByGroupId(db, i);
        }

        @Override
        public Object getGroup(int i) {
            return null;
        }

        @Override
        public Object getChild(int i, int i1) {
            return null;
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText("       "+CommonNumberDao.getGroupNameByGroupId(db, i));
            tv.setTextSize(20);
            tv.setTextColor(Color.RED);
            return tv;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText(CommonNumberDao.getItemNameByGroupIdAndItemId(db, i, i1));
            tv.setTextSize(18);
            return tv;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
