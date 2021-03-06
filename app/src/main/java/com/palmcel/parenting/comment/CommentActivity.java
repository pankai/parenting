package com.palmcel.parenting.comment;

import android.app.Activity;
import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.palmcel.parenting.R;

public class CommentActivity extends Activity {

    private static final String TAG = "CommentActivity";
    private CommentFragment mCommentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        String postId = getIntent().getStringExtra("postId");
        int commentCount = getIntent().getIntExtra("commentCount", 0);
        setContentView(R.layout.comment_activity);
        if (savedInstanceState == null) {
            mCommentFragment = CommentFragment.newInstance(postId, commentCount);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mCommentFragment,
                            CommentFragment.class.getName())
                    .commit();
        } else {
            mCommentFragment = (CommentFragment) getFragmentManager().
                    findFragmentByTag(CommentFragment.class.getName());
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comment, menu);
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
}
