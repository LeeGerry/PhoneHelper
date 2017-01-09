package proheart.me.phonehelper.engine;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import proheart.me.phonehelper.domain.UpdateInfo;

/**
 * Created by liguorui on 12/20/16.
 */

public class UpdateInfoParser {
    public static UpdateInfo getUpdateInfo(InputStream stream) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        UpdateInfo info = new UpdateInfo();
        parser.setInput(stream,"utf-8");
        int type = parser.getEventType();
        while (type != XmlPullParser.END_DOCUMENT){
            switch (type){
                case XmlPullParser.START_TAG:
                    String str = parser.getName();
                    if("version".equals(str)){
                        info.setVersion(parser.nextText());
                    }else if("description".equals(str)){
                        info.setDesc(parser.nextText());
                    }else if("apkurl".equals(str)){
                        info.setApkurl(parser.nextText());
                    }
                    break;
            }
            type = parser.next();
        }
        return info;
    }
}
