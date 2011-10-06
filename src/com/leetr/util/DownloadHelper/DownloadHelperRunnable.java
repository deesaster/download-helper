package com.leetr.util.DownloadHelper;

import com.leetr.util.DownloadHelper.listener.OnDownloadListener;
import org.apache.http.util.ByteArrayBuffer;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created By: Denis Smirnov <denis@deesastudio.com>
 * <p/>
 * Date: 11-10-02
 * Time: 8:17 PM
 */
public class DownloadHelperRunnable implements Runnable {
    private static final String TAG = "DownloadHelperRunnable";
    private static final int BYTE_ARRAY_BUFFER_SIZE = 1024;

    private String mUrl, mCacheDir, mFilename;
    private OnDownloadListener mListener;

    public DownloadHelperRunnable(String url, String cacheDir, String filename, OnDownloadListener listener) {
        mUrl = url;
        mCacheDir = cacheDir;
        mFilename = filename;
        mListener = listener;

        if (mCacheDir.charAt(mCacheDir.length() - 1) != File.pathSeparatorChar) {
            mCacheDir += File.separatorChar;
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

        URLConnection conn = null;
        ByteArrayBuffer buffer = null;
        String savedFile = null;

        try {
            conn = url.openConnection();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            buffer = readBytes(in);

            if (mCacheDir != null) {
                savedFile = writeToFile(buffer.toByteArray(), DownloadHelper.buildCacheFilePath(mUrl, mCacheDir, mFilename));
            }

        } catch (IOException e) {

            e.printStackTrace();
        } catch (OutOfMemoryError e) {

            e.printStackTrace();
        }

        if (mListener != null) {
            if (buffer == null) {
                mListener.onDownloadFail(mUrl, 0); //TODO: add error codes
            } else {
                mListener.onDownload(mUrl, buffer.toByteArray(), savedFile);
            }
        }
    }

    protected ByteArrayBuffer readBytes(InputStream inputStream) throws IOException {
        ByteArrayBuffer buffer = new ByteArrayBuffer(BYTE_ARRAY_BUFFER_SIZE);

        int current = 0;
        while ((current = inputStream.read()) != -1) {
            buffer.append((byte) current);
        }

        return buffer;
    }

    protected String writeToFile(byte[] data, String filename) throws IOException {
        File file = new File(filename);

        boolean fileExists = file.exists();


        if (!fileExists) {
            fileExists = file.mkdirs();
        }

        if (file.canWrite()) {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();

            return filename;
        }
        return null;
    }
}
