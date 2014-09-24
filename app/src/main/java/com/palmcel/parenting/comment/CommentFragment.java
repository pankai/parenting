package com.palmcel.parenting.comment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.palmcel.parenting.R;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.model.LoadCommentsParams;
import com.palmcel.parenting.model.LoadCommentsResultEvent;
import com.palmcel.parenting.model.LoadDataResult;
import com.palmcel.parenting.model.PostComment;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CommentFragment extends Fragment {

    private static final String TAG = "CommentFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POST_ID = "postId";

    private ListView mListView;
    private CommentListAdapter mAdapter;
    private View mSubmitButton;
    private EditText mCommentEdit;
    private Context mContext;
    private TextView mHeaderView;

    private String mPostId;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param postId post id
     * @return A new instance of fragment CommentFragment.
     */
    public static CommentFragment newInstance(String postId) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    public CommentFragment() {
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
                R.layout.comment_fragment, container, false);

        mListView = (ListView) rootView.findViewById(R.id.commentListView);
        mSubmitButton = rootView.findViewById(R.id.comment_submit_button);
        mCommentEdit = (EditText) rootView.findViewById(R.id.comment_edit);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mHeaderView = (TextView)((LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.comment_list_header, null, false);
        mListView.addHeaderView(mHeaderView);

        mAdapter = new CommentListAdapter(mContext);
        mListView.setAdapter(mAdapter);

        mHeaderView.setVisibility(View.INVISIBLE);
        mHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHeaderView.setText(getResources().getString(R.string.comments_loading_more));
            }
        });

        // Load post comments. TODO (kpan): don't need to load feed every time in onResume.
        LoadCommentsManager.getInstance().loadComments(mPostId);
        getActivity().setProgressBarIndeterminateVisibility(true);

//        ArrayList<PostComment> comments = Lists.newArrayList();
//
//        long nowTime = System.currentTimeMillis();
//
//        PostCommentBuilder builder = new PostCommentBuilder();
//        builder
//                .setPostUserId(LoggedInUser.getLoggedInUserId())
//                .setCommenterUserId(LoggedInUser.getLoggedInUserId())
//                .setCommentStatus(PostStatus.Normal)
//                .setIsAnonymous(false)
//                .setCommentMessage("comment 2")
//                .setTimeMsCreated(nowTime - 100000);
//
//        comments.add(builder.build());
//
//        builder = new PostCommentBuilder();
//        builder
//                .setPostUserId(LoggedInUser.getLoggedInUserId())
//                .setCommenterUserId(LoggedInUser.getLoggedInUserId())
//                .setCommentStatus(PostStatus.Normal)
//                .setIsAnonymous(false)
//                .setCommentMessage("comment 1")
//                .setTimeMsCreated(nowTime);
//
//        comments.add(builder.build());
//
//        mAdapter.updateEntries(ImmutableList.<PostComment>copyOf(comments));
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * EventBus event for loading comments.
     * @param event load comments results
     */
    public void onEventMainThread(LoadCommentsResultEvent event) {
        LoadCommentsParams loadCommentsParams = event.getLoadCommentsParams();
        Log.d(TAG, "In onEventMainThread for LoadCommentsResultEvent, " + loadCommentsParams);

//        if (loadFeedParams.timeMsInsertedSince > 0) {
//            // It is load more operation
//            mFeedListView.onLoadMoreComplete();
//        }

        LoadDataResult<PostComment> result = event.getLoadCommentsResult();

        if (!result.isSuccess) {
            Toast.makeText(
                    mContext, "Failed comments feed, " + result.error, Toast.LENGTH_SHORT).show();
        } else {
            // Update comments list view
            mAdapter.updateEntries(result.loadedData);
        }

        getActivity().setProgressBarIndeterminateVisibility(false);
    }
}
