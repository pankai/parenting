package com.palmcel.parenting.post;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.SearchView;

import com.google.common.base.Strings;
import com.palmcel.parenting.R;

public class PostProductActivity extends Activity
        implements PostProductFragment.OnFragmentInteractionListener {

    private static final String FRAGMENT_STATE_KEY = "FragmentState";

    private PostProductFragment mPostProductFragment;
    private SearchView mSearchView;
    private FragmentState mFragmentState;

    enum FragmentState {
        PostProductFragment,
        ChooseProductPictureFragment
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_product_activity);
        if (savedInstanceState == null) {
            mPostProductFragment = new PostProductFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mPostProductFragment,
                            PostProductFragment.class.getName())
                    .commit();
            mFragmentState = FragmentState.PostProductFragment;
        } else {
            mPostProductFragment = (PostProductFragment) getFragmentManager().
                    findFragmentByTag(PostProductFragment.class.getName());
            mFragmentState = (FragmentState) savedInstanceState.getSerializable(FRAGMENT_STATE_KEY);
            if (mFragmentState == null) {
                mFragmentState = FragmentState.PostProductFragment;
            }
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(FRAGMENT_STATE_KEY, mFragmentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_product, menu);

        MenuItem searchViewItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) searchViewItem.getActionView();
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryHint(getResources().getString(R.string.hint_enter_store_website));
        mSearchView.setQuery("www.amazon.com", false);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!Strings.isNullOrEmpty(query)) {
                    if (!query.startsWith("http://")) {
                        query = "http://" + query;
                    }
                    mPostProductFragment.loadUrl(query);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
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

    @Override
    public void onBackPressed() {
        // Check if the key event was the Back button and if there's history
        if (mFragmentState == FragmentState.PostProductFragment &&
                mPostProductFragment != null &&
                mPostProductFragment.webViewCanGoBack()) {
            mPostProductFragment.webViewGoBack();
            return;
        }

        if (mFragmentState == FragmentState.ChooseProductPictureFragment) {
            // Go back to PostProductFragment after clicking back key.
            mFragmentState = FragmentState.PostProductFragment;
        }

        // There's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        super.onBackPressed();
    }

    /**
     * User clicked Post button in PostProductFragment. We should open ChooseProductPictureFragment.
     */
    @Override
    public void onPostForPictureClicked() {
        getFragmentManager().beginTransaction()
            .add(R.id.container, new ChooseProductPictureFragment(),
                    "R.layout.ChooseProductPictureFragment")
            .hide(mPostProductFragment)
            .addToBackStack(mPostProductFragment.getClass().getName())
            .commit();
        mFragmentState = FragmentState.ChooseProductPictureFragment;
    }
}
