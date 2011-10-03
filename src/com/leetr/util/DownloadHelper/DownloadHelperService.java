package com.leetr.util.DownloadHelper;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.apache.http.util.ByteArrayBuffer;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created By: Denis Smirnov <denis@deesastudio.com>
 * <p/>
 * Date: 11-10-02
 * Time: 1:40 PM
 */
public class DownloadHelperService extends IntentService {
    public static final String TAG = "DownloadHelperService";

    public DownloadHelperService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra(DownloadHelper.EXTRA_URL);
        String cachePath = intent.getStringExtra(DownloadHelper.EXTRA_CACHE_PATH);

        new DownloadHelperRunnable(this, url, cachePath).run();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w(TAG, "service created");
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "service destroyed");
        super.onDestroy();
    }

    private static class DownloadHelperRunnable implements Runnable {
        private static final String TAG = "DownloadHelperRunnable";
        private static final int BYTE_ARRAY_BUFFER_SIZE = 1024;

        private String mUrl;
        private String mFileDir;
        private Context mContext;

        public DownloadHelperRunnable(Context context, String url, String fileDir) {
            mUrl = url;
            mFileDir = fileDir;
            mContext = context;

            if (mFileDir.charAt(mFileDir.length() - 1) != File.pathSeparatorChar) {
                mFileDir += File.pathSeparator;
            }
        }

        public void run() {
            URL url = null;
            try {
                url = new URL(mUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (url == null) {
                return;
            }

            boolean didDownloadFile = false;

            Log.w("THREAD", "willStartDownload");

            URLConnection conn = null;
            try {
                conn = url.openConnection();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                ByteArrayBuffer buffer = readBytes(in);
                String cacheFilename = mFileDir + URLEncoder.encode(mUrl, "UTF-8");
                Log.w("THREAD", "willStartWriteToDisc");
                writeToFile(buffer.toByteArray(), cacheFilename);
                didDownloadFile = true;

            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.w("THREAD", "willSendMessageToHandler");

            onDownloadComplete(mUrl, didDownloadFile);
        }

        protected void onDownloadComplete(String url, boolean success) {
            Intent intent = new Intent();
            intent.setAction(DownloadHelper.ACTION_DOWNLOAD);
            intent.putExtra(DownloadHelper.EXTRA_URL, url);
            intent.putExtra(DownloadHelper.EXTRA_SUCCESS, success);
            mContext.sendBroadcast(intent);
        }

        protected ByteArrayBuffer readBytes(InputStream inputStream) throws IOException {
            ByteArrayBuffer buffer = new ByteArrayBuffer(BYTE_ARRAY_BUFFER_SIZE);

            int current = 0;
            while ((current = inputStream.read()) != -1) {
                buffer.append((byte) current);
            }

            return buffer;
        }

        protected boolean writeToFile(byte[] data, String filename) throws IOException {
            File file = new File(filename);

            boolean fileExists = file.exists();

            if (!fileExists) {
                fileExists = file.mkdirs();
            }

            if (fileExists && file.canWrite()) {
                FileOutputStream fos = null;

                fos = new FileOutputStream(file);


                fos.write(data);
                fos.close();

                return true;
            }
            return false;
        }
    }
}
