package com.palmcel.parenting.likes;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.palmcel.parenting.R;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.model.LikesServiceFinishEvent;
import com.palmcel.parenting.model.LikesServiceStartEvent;
import com.palmcel.parenting.model.LoadDataResult;
import com.palmcel.parenting.model.LoadLikesParams;
import com.palmcel.parenting.model.LoadLikesResultEvent;
import com.palmcel.parenting.model.PostLike;
import com.palmcel.parenting.widget.LoadMoreListView;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.palmcel.parenting.likes.LikesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.palmcel.parenting.likes.LikesFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class LikesFragment extends Fragment implements LoadMoreListView.OnLoadMoreListener {

    private static final String TAG = "LikesFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POST_ID = "postId";

    private LoadMoreListView mListView;
    private LikesListAdapter mAdapter;
    private Context mContext;

    private String mPostId;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param postId post id
     * @return A new instance of fragment LikesFragment.
     */
    public static LikesFragment newInstance(String postId) {
        LikesFragment fragment = new LikesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    public LikesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPostId = getArguments().getString(ARG_POST_ID);
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.likes_fragment, container, false);

        mListView = (LoadMoreListView) rootView.findViewById(R.id.likesListView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new LikesListAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mListView.setOnLoadMoreListener(this);

        // Load post likes. TODO (kpan): don't need to load feed every time in onResume.
        LoadLikesManager.getInstance().loadLikes(mPostId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
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
        EventBus.getDefault().unregister(this);
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
        //public void onSubmitComment(String postId, String message);
    }

    /**
     * EventBus event for loading likes.
     * @param event load likes results
     */
    public void onEventMainThread(LoadLikesResultEvent event) {
        LoadLikesParams loadLikesParams = event.getLoadLikesParams();
        Log.d(TAG, "In onEventMainThread for LoadLikesResultEvent, " + loadLikesParams);

        if (loadLikesParams.timeMsLikeSince > 0) {
            // It is load more operation
            mListView.onLoadMoreComplete();
        }

        LoadDataResult<PostLike> result = event.getLoadLikesResult();

        if (!result.isSuccess) {
            Toast.makeText(
                    mContext, "Failed likes feed, " + result.error, Toast.LENGTH_SHORT).show();
        } else {
            // Update likes list view
            mAdapter.updateEntries(result.loadedData);
        }
    }

    /**
     * EventBus event for starting loading likes from server.
     */
    public void onEventMainThread(LikesServiceStartEvent event) {
        getActivity().setProgressBarIndeterminateVisibility(true);
    }

    /**
     * EventBus event for finishing loading likes from server.
     */
    public void onEventMainThread(LikesServiceFinishEvent event) {
        getActivity().setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "In onLoadMore");
        PostLike lastLike = mAdapter.getLastPostLike();
        if (lastLike == null) {
            Log.e(TAG, "Error: Got null last like from list view!", new RuntimeException());
            mListView.onLoadMoreComplete();
            return;
        }

        LoadLikesManager.getInstance().loadLikesMore(mPostId, lastLike.timeMsLike);
    }

}