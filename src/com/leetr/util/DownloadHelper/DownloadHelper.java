package com.leetr.util.DownloadHelper;

import android.os.AsyncTask;
import com.leetr.util.DownloadHelper.listener.OnDownloadListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created By: Denis Smirnov <denis@deesastudio.com>
 * <p/>
 * Date: 11-10-02
 * Time: 1:39 PM
 */
public class DownloadHelper {
    public static final String ACTION_DOWNLOAD = "com.leetr.util.DownloadHelper.ACTION_DOWNLOAD";
    public static final String EXTRA_URL = "com.leetr.util.DownloadHelper.EXTRA_URL";
    public static final String EXTRA_CACHE_FILENAME = "com.leetr.util.DownloadHelper.EXTRA_CACHE_FILENAME";
    public static final String EXTRA_CACHE_DIR = "com.leetr.util.DownloadHelper.EXTRA_CACHE_DIR";
    public static final String EXTRA_CACHE_FILEPATH = "com.leetr.util.DownloadHelper.EXTRA_CACHE_FILEPATH";
    public static final String EXTRA_ERROR_BITFIElD = "com.leetr.util.DownloadHelper.EXTRA_ERROR_BITFIElD";
    public static final String EXTRA_SUCCESS_DOWNLOAD = "com.leetr.util.DownloadHelper.EXTRA_SUCCESS_DOWNLOAD";
    public static final String EXTRA_SUCCESS_SAVE_CACHE = "com.leetr.util.DownloadHelper.EXTRA_SUCCESS_SAVE_CACHE";

    public static void download(String url, String cacheDir, String filename, OnDownloadListener listener, boolean loadCache) {
        new DownloadHelperAsyncTask(listener, loadCache).execute(url, cacheDir, filename);
    }

    public static void download(String url, String cacheDir, String filename, OnDownloadListener listener) {
        download(url, cacheDir, filename, listener, false);
    }

    public static class DownloadHelperAsyncTask extends AsyncTask<String, Void, Void> {

        private OnDownloadListener mListener;
        private String mUrl;
        private String mSavedCacheFilepath;
        private byte[] mData;
        private int mErrorBitfield = 0;
        private boolean mSuccess = false, mLoadCache = false;

        public DownloadHelperAsyncTask(OnDownloadListener listener, boolean loadCache) {
            super();

            mListener = listener;
            mLoadCache = loadCache;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mListener != null) {
                if (mSuccess) {
                    mListener.onDownload(mUrl, mData, mSavedCacheFilepath);
                } else {
                    mListener.onDownloadFail(mUrl, mErrorBitfield);
                }
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            String url = strings[0];
            String cacheDir = strings[1];
            String filename = strings[2];

            if (mLoadCache) {
                try {
                    String cacheFile = buildCacheFilePath(url, cacheDir, filename);
                    File file = new File(cacheFile);

                    if (file.exists()) {
                        mData = getBytesFromFile(file);
                        mSavedCacheFilepath = cacheFile;
                        mUrl = url;
                        mSuccess = true;
                        return null;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            new DownloadHelperRunnable(url, cacheDir, filename, new OnDownloadListener() {

                @Override
                public void onDownload(String url, byte[] data, String filePath) {
                    mUrl = url;
                    mData = data;
                    mSavedCacheFilepath = filePath;
                    mSuccess = true;
                }

                public void onDownloadFail(String url, int errorBitfield) {
                    mUrl = url;
                    mErrorBitfield = errorBitfield;
                    mSuccess = false;
                }
            }).run();
            return null;
        }
    }

    public static String buildCacheFilePath(String url, String cacheDir, String cacheFile) throws UnsupportedEncodingException {
        return cacheDir + URLEncoder.encode((cacheFile != null) ? cacheFile : url, "UTF-8");
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        byte[] data = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(data);
        fis.close();

        return data;
    }
}
