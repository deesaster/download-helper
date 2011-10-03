package com.leetr.util.DownloadHelper;

import com.leetr.util.WorkQueue.WorkQueue;

/**
 * Created By: Denis Smirnov <denis@deesastudio.com>
 * <p/>
 * Date: 11-10-02
 * Time: 1:45 PM
 */
public class DownloadWorkQueue extends WorkQueue {
    public static final int NUM_WORKERS = 5;

    public DownloadWorkQueue() {
        super(NUM_WORKERS);
    }
}
