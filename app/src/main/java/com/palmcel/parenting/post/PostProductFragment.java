package com.palmcel.parenting.post;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.palmcel.parenting.R;
import com.palmcel.parenting.common.Log;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PostProductFragment extends Fragment {

    private static final String TAG = "PostProductFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private WebView mWebView;
    private ProgressBar mProgressBar;

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

        mWebView = (WebView) rootView.findViewById(R.id.webview);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.loading_progress);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Register a new JavaScript interface called HTMLOUT.
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());

        mWebView.loadUrl("http://www.amazon.com");
    }

    private class MyWebChromeClient extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
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
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "onPageFinished, " + url);
            /* This call inject JavaScript into the page which just finished loading. */
            mWebView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
        }
    }

    /* An instance of this class will be registered as a JavaScript interface
    * See: http://stackoverflow.com/questions/2376471/how-do-i-get-the-web-page-contents-from-a-webview
    * */
    class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {
            Log.d(TAG, "MyJavaScriptInterface, html=" + html);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnFragmentInteractionListener) activity;
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    boolean webViewCanGoBack() {
        return mWebView.canGoBack();
    }

    void webViewGoBack() {
        mWebView.goBack();
    }
}
