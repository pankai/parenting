package com.palmcel.parenting.likes;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.palmcel.parenting.R;

public class LikesActivity extends Activity {

    private static final String TAG = "LikesActivity";
    private LikesFragment mLikesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        String postId = getIntent().getStringExtra("postId");
        setContentView(R.layout.likes_activity);
        if (savedInstanceState == null) {
            mLikesFragment = LikesFragment.newInstance(postId);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mLikesFragment,
                            LikesFragment.class.getName())
                    .commit();
        } else {
            mLikesFragment = (LikesFragment) getFragmentManager().
                    findFragmentByTag(LikesFragment.class.getName());
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.likes, menu);
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
