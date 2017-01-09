package proheart.me.phonehelper.utils;

import android.app.ProgressDialog;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by liguorui on 12/20/16.
 */

public class DownLoadUtils {
    public static File downLoad(String fileUrl, String path, ProgressDialog pd){
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            if (code == 200){
                InputStream in = conn.getInputStream();
                File file = new File(path);
                Log.i("download", file.getAbsolutePath());
                pd.setMax(conn.getContentLength());
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len = 0;
                int total = 0;
                while ((len = in.read(buf)) != -1){
                    fos.write(buf, 0, len);
                    total += len;
                    pd.setProgress(total);
                }
                fos.close();
                in.close();
                return file;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return null;
        }
    }
}
