package proheart.me.phonehelper.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by liguorui on 16/12/29.
 */

public class AddressDao {
    public static String getAddress(String number){
        String location = number;
        String dbPath = "/data/data/proheart.me.phonehelper/files/address.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        String match = "^1[3458]\\d{9}$";
        if (number.matches(match)){
            //手机号码
            Cursor cursor = db.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)",
                    new String[]{number.substring(0, 7)});
            if (cursor.moveToNext()){
                location = cursor.getString(0);
            }
            cursor.close();
        } else {
            //其他号码
            switch (number.length()){
                case 3://火警匪警等
                    location = "报警电话";
                    break;
                case 4://模拟器
                    location = "模拟器";
                    break;
                case 5://客服
                    location = "客服电话";
                    break;
                case 7://本地
                    location = "本地电话";
                    break;
                case 8://本地
                    location = "本地电话";
                    break;
                default:
                    if (number.length()>=11 && number.startsWith("0")){
                        //试着查前3位
                        Cursor cursor = db.rawQuery("select location from data2 where area = ?",
                                new String[]{number.substring(1, 3)});
                        if (cursor.moveToNext()){
                            String add = cursor.getString(0);
                            location = add.substring(0, add.length()-2);
                            cursor.close();
                        }
                        //试着查前4位
                        cursor = db.rawQuery("select location from data2 where area = ? ",
                                new String[]{number.substring(1, 4)});
                        if (cursor.moveToNext()){
                            String add = cursor.getString(0);
                            location = add.substring(0, add.length()-2);
                            cursor.close();
                        }
                    }

                    break;
            }
        }

        db.close();
        return location;
    }
}
