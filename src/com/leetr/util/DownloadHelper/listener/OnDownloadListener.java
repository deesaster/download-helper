package com.leetr.util.DownloadHelper.listener;

/**
 * Created By: Denis Smirnov <denis@deesastudio.com>
 * <p/>
 * Date: 11-10-02
 * Time: 9:18 PM
 */
public interface OnDownloadListener {
    public void onDownload(String url, byte[] data, String filePath);

    /**
     * Called when download fails
     *
     * @param url Url of the file that failed to download
     * @param errorBitfield Bitfield of error codes
     */
    public void onDownloadFail(String url, int errorBitfield);
}
