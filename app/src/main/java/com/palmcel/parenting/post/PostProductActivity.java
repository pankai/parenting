package com.palmcel.parenting.post;

import android.app.Activity;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.google.common.base.Strings;
import com.palmcel.parenting.R;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.model.PostSetting;

import de.greenrobot.event.EventBus;

public class PostProductActivity extends Activity
        implements PostProductFragment.OnFragmentInteractionListener,
            ChooseProductPictureFragment.OnFragmentInteractionListener,
            ComposeFragment.OnFragmentInteractionListener {

    private static final String TAG = "PostProductActivity";
    private static final String FRAGMENT_STATE_KEY = "FragmentState";

    private PostProductFragment mPostProductFragment;
    private ChooseProductPictureFragment mChooseProductPictureFragment;
    private SearchView mSearchView;
    private FragmentState mFragmentState;
    private ProgressDialog mProgressDialog;
    private InputMethodManager mInputMethodManager;

    enum FragmentState {
        PostProductFragment,
        ChooseProductPictureFragment,
        ComposeFragment
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
            changeFragmentState(null, FragmentState.PostProductFragment);
        } else {
            mPostProductFragment = (PostProductFragment) getFragmentManager().
                    findFragmentByTag(PostProductFragment.class.getName());
            mChooseProductPictureFragment = (ChooseProductPictureFragment) getFragmentManager().
                    findFragmentByTag(ChooseProductPictureFragment.class.getName());
            FragmentState fragmentState =
                    (FragmentState) savedInstanceState.getSerializable(FRAGMENT_STATE_KEY);
            if (fragmentState == null) {
                fragmentState = FragmentState.PostProductFragment;
            }
            changeFragmentState(null, fragmentState);
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        EventBus.getDefault().register(this);
        mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(FRAGMENT_STATE_KEY, mFragmentState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (mFragmentState == FragmentState.ChooseProductPictureFragment) {
            createOptionMenuForChoosePicture(menu);
            setTitle(getResources().getString(R.string.title_activity_choose_picture));
        } else if (mFragmentState == FragmentState.ComposeFragment) {
            createOptionMenuForCompose(menu);
            setTitle(getResources().getString(R.string.title_activity_post_product));
        } else {
            createOptionMenuForPostProduct(menu);
            setTitle(getResources().getString(R.string.title_activity_post_product));
        }

        return true;
    }

    private void createOptionMenuForPostProduct(Menu menu) {
        Log.d(TAG, "In createOptionMenuForPostProduct");

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
                    mInputMethodManager.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
    }

    private void createOptionMenuForChoosePicture(Menu menu) {
        Log.d(TAG, "In createOptionMenuForChoosePicture");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.choose_picture, menu);
    }

    private void createOptionMenuForCompose(Menu menu) {
        Log.d(TAG, "In createOptionMenuForCompose");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.compose_product, menu);
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
            // Go back to PostProductFragment after clicking back key in
            // ChooseProductPictureFragment.
            changeFragmentState(
                    FragmentState.ChooseProductPictureFragment, FragmentState.PostProductFragment);
        } else if (mFragmentState == FragmentState.ComposeFragment) {
            // Go back to ChooseProductPictureFragment after clicking back key in
            // ComposeFragment
            changeFragmentState(
                    FragmentState.ComposeFragment,
                    FragmentState.ChooseProductPictureFragment);
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
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getResources().getString(R.string.progress_loading));

        try {
            mProgressDialog.show();
        } catch (Throwable t) {
            Log.e(TAG, "mProgressDialog.show", t);
        }

        // kick off image url retrieval from WebView. onEventMainThread will be called
        // when there is result.
        mPostProductFragment.kickOffImageUrlsRetrieval();
    }

    /**
     * EventBus event for loading feed.
     * @param event load feed results
     */
    public void onEventMainThread(ImageUrlsRetrievalResultEvent event) {
        Log.d(TAG, "In onEventMainThread for ImageUrlsRetrievalResultEvent");

        // Open ChooseProductPictureFragment fragment
        mChooseProductPictureFragment =
                ChooseProductPictureFragment.newInstance(event.productPageInfo);
        getFragmentManager().beginTransaction()
                .add(R.id.container,
                     mChooseProductPictureFragment,
                     ChooseProductPictureFragment.class.getName())
                .hide(mPostProductFragment)
                .addToBackStack(mPostProductFragment.getClass().getName())
                .commit();
        changeFragmentState(
                FragmentState.PostProductFragment,
                FragmentState.ChooseProductPictureFragment);

        if (mProgressDialog != null) {
            try {
                mProgressDialog.dismiss();
            } catch (Throwable t) {
                Log.e(TAG, "mProgressDialog.dismiss", t);
            }
        }
    }

    private void changeFragmentState(@Nullable FragmentState fromState, FragmentState toState) {
        mFragmentState = toState;

        if (toState == FragmentState.ChooseProductPictureFragment ||
                toState == FragmentState.ComposeFragment ||
                fromState == FragmentState.ChooseProductPictureFragment &&
                toState == FragmentState.PostProductFragment) {
            invalidateOptionsMenu();
        }
    }

    /**
     * User chose an image in ChooseProductPictureFragment
     * @param productImageUrl the url of the product image url
     */
    @Override
    public void onChooseProductPicture(String productImageUrl, ProductPageInfo productPageInfo) {
        Log.d(TAG, "In onChooseProductPicture, productImageUrl=" + productImageUrl
                + ", productPageInfo=" + productPageInfo);

        // Open compose fragment
        getFragmentManager().beginTransaction()
                .add(R.id.container,
                        ComposeFragment.newInstance(null, null),
                        ComposeFragment.class.getName())
                .hide(mChooseProductPictureFragment)
                .addToBackStack(mChooseProductPictureFragment.getClass().getName())
                .commit();
        changeFragmentState(
                FragmentState.ChooseProductPictureFragment,
                FragmentState.ComposeFragment);
    }

    @Override
    public void onSubmitPost(String message, PostSetting postSetting) {

    }

}
