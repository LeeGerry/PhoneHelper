package proheart.me.phonehelper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by liguorui on 16/12/29.
 */

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {
    public BlackNumberDBOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
        Log.i("db","dbcreate");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("db","table");
        db.execSQL("create table blacknumber (_id integer primary key autoincrement, phone varchar(20), mode varchar(2))");
        Log.i("db","table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
