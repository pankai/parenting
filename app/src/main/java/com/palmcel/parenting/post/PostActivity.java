package com.palmcel.parenting.post;

import android.app.Activity;
import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.R;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.common.UiThreadExecutor;
import com.palmcel.parenting.feed.LoadFeedManager;
import com.palmcel.parenting.model.PostBuilder;
import com.palmcel.parenting.model.PostSetting;

public class PostActivity extends Activity implements
        ComposeFragment.OnFragmentInteractionListener {

    private static final String TAG = "PostActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ComposeFragment())
                    .commit();
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.ic_profile);
        actionBar.setTitle(R.string.menu_post_story);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Submit a regular post
     * @param message the post message
     */
    @Override
    public void onSubmitPost(String message, PostSetting postSetting) {
        Log.d(TAG, "In onSubmitPost, postSetting=" + postSetting);
        PostBuilder builder = PostBuilder.newLocalRegularPostBuilder(message, postSetting);

        PostHandler postHandler = new PostHandler();
        //ListenableFuture savePostFuture = postHandler.savePostToDbOnThread(builder.build());
        ListenableFuture savePostFuture = postHandler.savePostToServerOnThread(builder.build());

        Futures.addCallback(savePostFuture, new FutureCallback() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(PostActivity.this, "Save post successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Saved post successfully");

                // Reload feed in FeedFragment
                LoadFeedManager.getInstance().loadFeed();

                PostActivity.this.finish();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(PostActivity.this, "Failed to save post, " + throwable.toString(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to save post", throwable);
            }
        }, new UiThreadExecutor());
    }
}
