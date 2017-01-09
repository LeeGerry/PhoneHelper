package proheart.me.phonehelper.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Author: Gary
 * Time: 16/12/30
 */

public class CommonNumberDao {
    public final static String dbPath = "/data/data/proheart.me.phonehelper/files/commonnum.db";

    /**
     * 查询有多少个分组
     * @param db
     * @return
     */
    public static int getGroupCount(SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select count(*) from classlist", null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * 根据groupId查询group中有多少个条目
     * @param db
     * @param groupId
     * @return
     */
    public static int getChildrenCountByGroupId(SQLiteDatabase db, int groupId){
        String tableName = "table" + String.valueOf(groupId+1);
        Cursor cursor = db.rawQuery("select count(*) from "+tableName, null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * 根据groupId获取组名
     * @param db
     * @param groupId
     * @return
     */
    public static String getGroupNameByGroupId(SQLiteDatabase db, int groupId){
        Cursor cursor = db.rawQuery("select name from classlist where idx = ?", new String[]{String.valueOf(groupId+1)});
        cursor.moveToNext();
        String name = cursor.getString(0);
        cursor.close();
        return name;
    }

    /**
     * 根据groupId和itemId获取条目信息
     * @param db
     * @param groupId
     * @return
     */
    public static String getItemNameByGroupIdAndItemId(SQLiteDatabase db, int groupId, int itemId){
        Cursor cursor = db.rawQuery("select name, number from table"+(groupId+1)+" where _id = ?",
                new String[]{String.valueOf(itemId+1)});
        cursor.moveToNext();
        String name = cursor.getString(0);
        String number = cursor.getString(1);
        cursor.close();
        return name+"\n"+number;
    }
}
