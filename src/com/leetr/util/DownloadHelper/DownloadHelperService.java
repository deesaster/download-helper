package com.leetr.util.DownloadHelper;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

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

        new DownloadHelperRunnable(url, true, cachePath, null) {

            @Override
            public void onDownloadComplete(String url, byte[] data, boolean successDownload, boolean successSaveToCache) {
                Intent intent = new Intent();
                intent.setAction(DownloadHelper.ACTION_DOWNLOAD);
                intent.putExtra(DownloadHelper.EXTRA_URL, url);
                intent.putExtra(DownloadHelper.EXTRA_SUCCESS_SAVE_CACHE, successSaveToCache);
                DownloadHelperService.this.sendBroadcast(intent);
            }
        }.run();
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
