package proheart.me.phonehelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import proheart.me.phonehelper.R;
import proheart.me.phonehelper.domain.ContactInfo;
import proheart.me.phonehelper.engine.ContactInfoProvider;

/**
 * 选择联系人界面
 */

public class ChooseContactActivity extends BaseActivity {
    private ListView lvContact;
    private List<ContactInfo> listInfos;
    private ContactAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_contact);
        lvContact = (ListView) findViewById(R.id.lvContact);
        listInfos = ContactInfoProvider.getContactInfos(this);
        adapter = new ContactAdapter();
        lvContact.setAdapter(adapter);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phoneNumber = listInfos.get(position).getPhoneNumber();
                Intent data = new Intent();
                data.putExtra("phone", phoneNumber);
                setResult(0,data);
                finish();
            }
        });
    }
    final private class ContactAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return listInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return listInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ContactInfo info = listInfos.get(position);
            View view = View.inflate(ChooseContactActivity.this, R.layout.list_contact_item,null);
            TextView tvName = (TextView) view.findViewById(R.id.tvContactName);
            TextView tvNumber = (TextView) view.findViewById(R.id.tvContactNumber);
            tvName.setText(info.getName());
            tvNumber.setText(info.getPhoneNumber());
            Log.i(TAG, info.toString());
            return view;
        }
    }
}
