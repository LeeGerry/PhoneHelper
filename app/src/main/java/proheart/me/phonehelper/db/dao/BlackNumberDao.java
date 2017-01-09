package proheart.me.phonehelper.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import proheart.me.phonehelper.db.BlackNumberDBOpenHelper;
import proheart.me.phonehelper.domain.BlackNumberInfo;

/**
 * Author: Gary
 * Time: 16/12/30
 * 黑名单数据库DAO
 */

public class BlackNumberDao {
    private BlackNumberDBOpenHelper helper;
    public BlackNumberDao(Context context){
        helper = new BlackNumberDBOpenHelper(context);
    }

    /**
     * 增加一个黑名单电话
     * @param number
     * @param mode
     */
    public void add(String number, String mode){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", number);
        values.put("mode", mode);
        db.insert("blacknumber",null, values);
        db.close();
    }

    /**
     * 删除一个黑名单
     * @param number
     */
    public void del(String number){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        db.delete("blacknumber","phone=?", new String[]{number});
        db.close();
    }

    /**
     * 更新一个黑名单电话的拦截模式
     * @param number
     * @param mode
     */
    public void update(String number, String mode){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        db.update("blacknumber",values, "phone=?", new String[]{number});
        db.close();
    }

    /**
     * 查找一个电话是否在黑名单中
     * @param number
     * @return
     */
    public boolean find(String number){
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", null, "phone=?",new String[]{number},null,null,null);
        if (cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 根据电话号码查询拦截模式
     * @param number
     * @return
     */
    public String findMode(String number){
        String mode = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "phone=?",new String[]{number},null,null,null);
        if (cursor.moveToNext()){
            mode = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mode;
    }

    /**
     * 查找全部黑名单号码
     * @return
     */
    public List<BlackNumberInfo> findAll(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackNumberInfo> list = new ArrayList<>();
        Cursor cursor = db.query("blacknumber", new String[]{"phone","mode"},null,null,null,null,null);
        while (cursor.moveToNext()){
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            BlackNumberInfo info = new BlackNumberInfo();
            info.setNumber(number);
            info.setMode(mode);
            list.add(info);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 条件查找：分页
     * @param startIndex
     * @param maxNumber
     * @return
     */
    public List<BlackNumberInfo> findPart(int startIndex, int maxNumber){
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackNumberInfo> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select phone, mode from blacknumber order by _id desc limit ? offset ?",
                new String[]{String.valueOf(maxNumber), String.valueOf(startIndex)});

        while (cursor.moveToNext()){
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            BlackNumberInfo info = new BlackNumberInfo();
            info.setNumber(number);
            info.setMode(mode);
            list.add(info);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 获取黑名单号码总条数
     * @return
     */
    public int getCount(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String []{"phone", "mode"},null,null,null,null,null);
        int total = cursor.getCount();
        cursor.close();
        db.close();
        return total;
    }
}
