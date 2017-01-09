package proheart.me.phonehelper;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.List;
import java.util.Random;

import proheart.me.phonehelper.db.BlackNumberDBOpenHelper;
import proheart.me.phonehelper.db.dao.BlackNumberDao;
import proheart.me.phonehelper.domain.BlackNumberInfo;

/**
 * Author: Gary
 * Time: 16/12/30
 */

public class BlackNumberDaoTest extends AndroidTestCase{
    public void testCreateDB() throws Exception {
        BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(
                getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        System.out.println(db.getVersion());
    }

    public void testAdd() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(getContext());
        Random random = new Random();

        long basenumber = 13500000000l;
        for (int i = 0; i < 100; i++) {
            dao.add(String.valueOf(basenumber+i), String.valueOf(random.nextInt(3)+1));
        }
    }

    public void testDelete() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.del("13500000000");
    }

    public void testUpdate() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.update("13500000000", "2");
    }

    public void testFind() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(getContext());
        boolean result = dao.find("13500000000");
        assertEquals(true, result);
    }

    public void testFindAll() throws Exception {
        BlackNumberDao dao = new BlackNumberDao(getContext());
        List<BlackNumberInfo> infos = dao.findAll();
        for(BlackNumberInfo info: infos){
            System.out.println(info.toString());
        }
    }
}
