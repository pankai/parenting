package com.palmcel.parenting.post;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.palmcel.parenting.R;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.widget.ObservableWebView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PostProductFragment extends Fragment
        implements ObservableWebView.OnScrollChangedCallback {

    private static final String TAG = "PostProductFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ObservableWebView mWebView;
    private ProgressBar mProgressBar;
    private View mPostButtonPanel;
    private View mPostButton;
    private ListView mSuggestedUrlsListView;
    private MyJavaScriptInterface mJavaScriptInterface;
    private String mProductPageUrl;
    private Context mContext;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostProductFragment newInstance(String param1, String param2) {
        PostProductFragment fragment = new PostProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public PostProductFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.post_product_fragment, container, false);

        mWebView = (ObservableWebView) rootView.findViewById(R.id.webview);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.loading_progress);
        mPostButtonPanel = (View) rootView.findViewById(R.id.bottom_panel);
        mPostButton = rootView.findViewById(R.id.post_button);
        mSuggestedUrlsListView = (ListView) rootView.findViewById(R.id.suggested_url_list);

        mWebView.setOnScrollChangedCallback(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Register a new JavaScript interface called HTMLOUT.
        mJavaScriptInterface = new MyJavaScriptInterface();
        mWebView.addJavascriptInterface(mJavaScriptInterface, "HTMLOUT");

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onPostForPictureClicked();
                }
            }
        });

        final String[] suggestedUrls = new String[] {
                "www.amazon.com",
                "www.walmart.com"
        };
        ArrayAdapter<String> arrayAdapter =
            new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, suggestedUrls);
        mSuggestedUrlsListView.setAdapter(arrayAdapter);
        mSuggestedUrlsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mListener != null) {
                    mListener.onWebsiteUrlSelected(suggestedUrls[i]);
                }

            }
        });
    }

    void loadUrl(String url) {
        mProductPageUrl = url;
        mWebView.loadUrl(url);
    }

    private class MyWebChromeClient extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            mSuggestedUrlsListView.setVisibility(View.INVISIBLE);
            if(progress < 100 && mProgressBar.getVisibility() == ProgressBar.GONE){
                mProgressBar.setVisibility(ProgressBar.VISIBLE);
            }
            mProgressBar.setProgress(progress);
            if(progress == 100) {
                mProgressBar.setVisibility(ProgressBar.GONE);
            }
        }

        @Override
        public void onReceivedTitle (WebView view, String title) {
            Log.d(TAG, "onReceivedTitle, " + title);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mProductPageUrl = url;
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, final String url) {
            Log.d(TAG, "onPageFinished, " + url);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onPostForPictureClicked();
        public void onWebsiteUrlSelected(String websiteUrl);
    }

    boolean webViewCanGoBack() {
        return mWebView.canGoBack();
    }

    void webViewGoBack() {
        mWebView.goBack();
    }

    @Override
    public void onScrollDown() {
        if (mPostButtonPanel.getVisibility() == View.VISIBLE) {
            mPostButtonPanel.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onScrollUp() {
        if (mPostButtonPanel.getVisibility() != View.VISIBLE) {
            mPostButtonPanel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Retrieve urls for the image in the html content of WebView.
     */
    public void kickOffImageUrlsRetrieval() {
        if (Strings.isNullOrEmpty(mProductPageUrl)) {
            Toast.makeText(mContext, R.string.product_url_is_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        mJavaScriptInterface.setProductWebPageUrl(mProductPageUrl);
        /* This call inject JavaScript into the page which just finished loading. */
        mWebView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
    }
}
