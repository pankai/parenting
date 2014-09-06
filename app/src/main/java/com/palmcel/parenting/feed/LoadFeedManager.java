package com.palmcel.parenting.feed;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.db.DbHelper;
import java.util.concurrent.Callable;
import de.greenrobot.event.EventBus;

import static com.palmcel.parenting.db.DatabaseContract.PostEntry;

/**
 * Singleton class that loads feed
 */
public class LoadFeedManager {
    private static LoadFeedManager INSTANCE = new LoadFeedManager();

    private ListenableFuture<LoadFeedResult> mLoadFeedFuture;

    public void loadFeed(final LoadFeedParams loadFeedParams) {
        if (mLoadFeedFuture != null) {
            return;
        }

        mLoadFeedFuture = ExecutorUtil.execute(new Callable<LoadFeedResult>() {
            @Override
            public LoadFeedResult call() throws Exception {
                return loadFeedFromDb();
            }
        });

        Futures.addCallback(mLoadFeedFuture, new FutureCallback<LoadFeedResult>() {
            @Override
            public void onSuccess(LoadFeedResult result) {
                Log.d("LoadFreeManager", "mLoadFeedFuture succeeded");
                EventBus.getDefault().post(new LoadFeedResultEvent(loadFeedParams, result));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("LoadFreeManager", "mLoadFeedFuture failed", t);
                LoadFeedResult result = LoadFeedResult.errorResult(t);
                EventBus.getDefault().post(new LoadFeedResultEvent(loadFeedParams, result));
            }
        });
    }

    private LoadFeedResult loadFeedFromDb() {
        return null;
    }

    public static LoadFeedManager getInstance() {
        SQLiteDatabase db = DbHelper.getDb();

        String[] projection = {
        };
        return INSTANCE;
    }
}