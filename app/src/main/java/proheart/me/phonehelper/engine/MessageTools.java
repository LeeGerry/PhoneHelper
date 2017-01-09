package proheart.me.phonehelper.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author: Gary
 * Time: 17/1/2
 */

public class MessageTools {
    //备份短信存放的文件名
    public final static String BACKUP_SMS_FILE = "smsbackup.xml";
    public interface MsgBackupCallback{
        void beforeBackup(int max);
        void doInBackgroun(int progress);
    }

    /**
     * 备份短信
     * @param context
     * @param callback
     * @throws IOException
     * @throws InterruptedException
     */
    public static void backupMsg(Context context, MsgBackupCallback callback) throws IOException, InterruptedException {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri =  Uri.parse("content://sms");
        XmlSerializer serializer = Xml.newSerializer();
        File file = new File(Environment.getExternalStorageDirectory(),BACKUP_SMS_FILE);
        FileOutputStream fos = new FileOutputStream(file);
        Log.i("msg",file.getPath());
        serializer.setOutput(fos,"utf-8");
        serializer.startDocument("utf-8", true);
        serializer.startTag(null, "messages");
        Cursor cursor = contentResolver.query(uri, new String[]{"address","date","body","type"},null,null,null);
        callback.beforeBackup(cursor.getCount());//设置回调方法，参数为总条数
        int total = 0;
        while (cursor.moveToNext()){
            //开始一条信息
            serializer.startTag(null, "message");

            //写入信息
            serializer.startTag(null, "address");
            serializer.text(cursor.getString(0));
            serializer.endTag(null, "address");

            serializer.startTag(null, "date");
            serializer.text(cursor.getString(1));
            serializer.endTag(null, "date");

            serializer.startTag(null, "body");
            serializer.text(cursor.getString(2));
            serializer.endTag(null, "body");

            serializer.startTag(null, "type");
            serializer.text(cursor.getString(3));
            serializer.endTag(null, "type");

            //信息结束
            serializer.endTag(null, "message");
            Thread.sleep(50);
            total++;//条数+1
            callback.doInBackgroun(total);//回调函数，参数为当前完成备份的条数
        }

        //结束
        serializer.endTag(null, "messages");
        serializer.endDocument();
        fos.flush();
        fos.close();
    }
}
