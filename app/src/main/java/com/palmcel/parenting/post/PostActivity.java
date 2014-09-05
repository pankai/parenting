package com.palmcel.parenting.post;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.R;
import com.palmcel.parenting.model.PostBuilder;

import java.util.concurrent.Future;

public class PostActivity extends Activity implements
        ComposeFragment.OnFragmentInteractionListener {

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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * Submit a regular post
     * @param message the post message
     */
    @Override
    public void onSubmitPost(String message) {
        PostBuilder builder = PostBuilder.newLocalRegularPostBuilder(message);

        PostHandler postHandler = new PostHandler();
        ListenableFuture savePostFuture = postHandler.savePostToDbOnThread(builder.build());

        Futures.addCallback(savePostFuture, new FutureCallback() {
            @Override
            public void onSuccess(Object o) {
                //Toast.makeText(PostActivity.this, "Save post successfully", Toast.LENGTH_SHORT).show();
                Log.d("PostActivity", "Saved post successfully");
            }

            @Override
            public void onFailure(Throwable throwable) {
                //Toast.makeText(PostActivity.this, "Failed to save post, " + throwable.toString(), Toast.LENGTH_SHORT).show();
                Log.e("PostActivity", "Failed to save post", throwable);
            }
        });
    }
}
