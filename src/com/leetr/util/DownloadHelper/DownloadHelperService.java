package com.leetr.util.DownloadHelper;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.leetr.util.DownloadHelper.listener.OnDownloadListener;

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
        String cachePath = intent.getStringExtra(DownloadHelper.EXTRA_CACHE_DIR);
        String cacheFilename = intent.getStringExtra(DownloadHelper.EXTRA_CACHE_FILENAME);

        new DownloadHelperRunnable(url, cachePath, cacheFilename, new OnDownloadListener() {
            @Override
            public void onDownload(String url, byte[] data, String filePath) {
                broadcastMessage(true, url, filePath, 0);
            }

            @Override
            public void onDownloadFail(String url, int errorBitfield) {
                broadcastMessage(false, url, null, errorBitfield);
            }
        }).run();
    }

    protected void broadcastMessage(boolean success, String url, String filePath, int errorBitfield) {
        Intent intent = new Intent();
        intent.setAction(DownloadHelper.ACTION_DOWNLOAD);
        intent.putExtra(DownloadHelper.EXTRA_URL, url);
        intent.putExtra(DownloadHelper.EXTRA_SUCCESS_DOWNLOAD, success);
        intent.putExtra(DownloadHelper.EXTRA_CACHE_FILEPATH, filePath);
        intent.putExtra(DownloadHelper.EXTRA_ERROR_BITFIElD, errorBitfield);

        DownloadHelperService.this.sendBroadcast(intent);
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
}
