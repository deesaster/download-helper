package com.leetr.util.DownloadHelper;

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
 * Time: 8:17 PM
 */
public abstract class DownloadHelperRunnable implements Runnable {
    private static final String TAG = "DownloadHelperRunnable";
    private static final int BYTE_ARRAY_BUFFER_SIZE = 1024;

    private String mUrl, mCacheDir, mFilename;
    private boolean mSaveToCache;

    public DownloadHelperRunnable(String url, boolean saveToCache, String cacheDir, String filename) {
        mUrl = url;
        mCacheDir = cacheDir;
        mSaveToCache = saveToCache;
        mFilename = filename;

        if (mCacheDir.charAt(mCacheDir.length() - 1) != File.pathSeparatorChar) {
            mCacheDir += File.pathSeparator;
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
        boolean didSaveToCache = false;
        URLConnection conn = null;
        ByteArrayBuffer buffer = null;

        try {
            conn = url.openConnection();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            buffer = readBytes(in);
            didDownloadFile = true;

            if (mSaveToCache && mCacheDir != null) {

                String cacheFilename = mCacheDir + URLEncoder.encode((mFilename != null) ? mFilename : mUrl, "UTF-8");
                writeToFile(buffer.toByteArray(), cacheFilename);
                didSaveToCache = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        onDownloadComplete(mUrl, (buffer == null) ? null : buffer.toByteArray(), didDownloadFile, didSaveToCache);
    }

    public abstract void onDownloadComplete(String url, byte[] data, boolean successDownload, boolean successSaveCache);

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
