package proheart.me.phonehelper.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import proheart.me.phonehelper.domain.ContactInfo;

/**
 * Created by liguorui on 12/23/16.
 */

public class ContactInfoProvider {
    /**
     * 获取联系人姓名和电话
     * @param ctx
     * @return
     */
    public static List<ContactInfo> getContactInfos(Context ctx){
        ContentResolver resolver = ctx.getContentResolver();
        List<ContactInfo> list = new ArrayList<>();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = resolver.query(uri, new String[]{"contact_id"}, null, null, null);
        while (cursor.moveToNext()){
            String id = cursor.getString(0);
            if(!TextUtils.isEmpty(id)){
                ContactInfo info = new ContactInfo();
                Cursor dataCursor = resolver.query(dataUri, new String[]{"data1","mimetype"}, "raw_contact_id=?", new String[]{id},null);
                while (dataCursor.moveToNext()){
                    String data1 = dataCursor.getString(0);
                    String type = dataCursor.getString(1);
                    if("vnd.android.cursor.item/phone_v2".equals(type)){
                        info.setPhoneNumber(data1);
                    }else if("vnd.android.cursor.item/name".equals(type)){
                        info.setName(data1);
                    }
                }
                list.add(info);
                dataCursor.close();
            }
        }
        cursor.close();
        return list;
    }
}
