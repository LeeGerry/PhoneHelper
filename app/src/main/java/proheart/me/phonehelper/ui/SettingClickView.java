package proheart.me.phonehelper.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import proheart.me.phonehelper.R;

/**
 * Created by liguorui on 12/21/16.
 */

public class SettingClickView extends RelativeLayout {
    private TextView tvTitle, tvDesc;
//    private CheckBox cb;
    private String desc_on, desc_off;

    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/proheart.me.phonehelper","setting_title");
        desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/proheart.me.phonehelper","desc_on");
        desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/proheart.me.phonehelper","desc_off");
        setTitle(title);
        setDesc(desc_off);
        //this.setBackgroundResource(R.drawable.checkbox_bg);
    }


    private void initView(Context ctx){
        View v = View.inflate(ctx, R.layout.ui_setting_click,this);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvDesc = (TextView) v.findViewById(R.id.tvDesc);

    }
    public void setTitle(String title){
        tvTitle.setText(title);
    }
    public void setDesc(String desc){
        tvDesc.setText(desc);
    }


}
