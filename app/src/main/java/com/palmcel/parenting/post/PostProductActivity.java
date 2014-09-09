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

public class PostProductActivity extends Activity {

    private PostProductFragment mPostProductFragment;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_product_activity);
        if (savedInstanceState == null) {
            mPostProductFragment = new PostProductFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mPostProductFragment, "R.layout.post_product_fragment")
                    .commit();
        } else {
            mPostProductFragment = (PostProductFragment) getFragmentManager().
                    findFragmentByTag("R.layout.post_product_fragment");
        }
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mPostProductFragment.webViewCanGoBack()) {
            mPostProductFragment.webViewGoBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}
