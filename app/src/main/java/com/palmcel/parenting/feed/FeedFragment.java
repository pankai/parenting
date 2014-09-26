package com.palmcel.parenting.feed;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.palmcel.parenting.R;

import com.palmcel.parenting.common.DataLoadCause;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.list.PostListAdapter;
import com.palmcel.parenting.model.FeedPost;
import com.palmcel.parenting.model.LoadDataParams;
import com.palmcel.parenting.model.LoadDataResult;
import com.palmcel.parenting.model.LoadFeedResultEvent;
import com.palmcel.parenting.widget.LoadMoreListView;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class FeedFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
            LoadMoreListView.OnLoadMoreListener {

    private static final String TAG = "FeedFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout mListViewContainer;
    private SwipeRefreshLayout mEmptyViewContainer;
    private PostListAdapter mAdapter;
    private LoadMoreListView mFeedListView;

    private Context mContext;
    String[] arr = new String[] {"FEED", "EXPLORE", "PRODUCTS", "2222", "3333"};

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public FeedFragment() {
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

        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.feed_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // SwipeRefreshLayout
        mListViewContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefreshLayout_listView);
        mEmptyViewContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefreshLayout_emptyView);

        // Configure SwipeRefreshLayout
        onCreateSwipeToRefresh(mListViewContainer);
        onCreateSwipeToRefresh(mEmptyViewContainer);

        // Adapter
        mAdapter = new PostListAdapter(mContext);
        //mAdapter.addAll(new Vector(Arrays.asList(arr)));

        // ListView
        mFeedListView = (LoadMoreListView) getActivity().findViewById(R.id.feedListView);
        mFeedListView.setEmptyView(mEmptyViewContainer);
        mFeedListView.setAdapter(mAdapter);
        mFeedListView.setOnLoadMoreListener(this);

        // Load feed from db. TODO (kpan): don't need to load feed every time in onResume.
        LoadFeedManager.getInstance().loadFeed();
    }

    private void onCreateSwipeToRefresh(SwipeRefreshLayout refreshLayout) {

        refreshLayout.setOnRefreshListener(this);

        refreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light);

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
        mContext = activity;
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * EventBus event for loading feed.
     * @param event load feed results
     */
    public void onEventMainThread(LoadFeedResultEvent event) {
        LoadDataParams loadDataParams = event.getLoadDataParams();
        Log.d(TAG, "In onEventMainThread for LoadFeedResultEvent, " + loadDataParams);

        if (loadDataParams.dataLoadCause == DataLoadCause.USER_REQUEST) {
            mListViewContainer.setRefreshing(false);
            mEmptyViewContainer.setRefreshing(false);
        }

        if (loadDataParams.timeSince > 0) {
            // It is load more operation
            mFeedListView.onLoadMoreComplete();
        }

        LoadDataResult<FeedPost> result = event.getLoadFeedResult();

        if (!result.isSuccess) {
            Toast.makeText(
                    mContext, "Failed load feed, " + result.error, Toast.LENGTH_SHORT).show();
        } else {
            // Update feed list view
            mAdapter.updateEntries(result.loadedData);
        }
    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "In onLoadMore");
        FeedPost lastPost = mAdapter.getLastFeedPost();
        if (lastPost == null) {
            Log.e(TAG, "Error: Got null last post from list view!", new RuntimeException());
            mFeedListView.onLoadMoreComplete();
            return;
        }

        LoadFeedManager.getInstance().loadFeedMore(lastPost.timeMsInserted);
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

    public static Fragment newInstance() {
        return new FeedFragment();
    }


    @Override
    public void onRefresh() {
        Log.d(TAG, "In onRefresh");

        LoadFeedManager.getInstance().loadFeedForced();
    }
}
