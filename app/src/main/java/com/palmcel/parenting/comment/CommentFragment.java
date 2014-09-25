package com.palmcel.parenting.comment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.R;
import com.palmcel.parenting.cache.FeedCache;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.common.UiThreadExecutor;
import com.palmcel.parenting.db.PostDbHandler;
import com.palmcel.parenting.feed.LoadFeedManager;
import com.palmcel.parenting.model.CommentsServiceFinishEvent;
import com.palmcel.parenting.model.CommentsServiceStartEvent;
import com.palmcel.parenting.model.LoadCommentsParams;
import com.palmcel.parenting.model.LoadCommentsResultEvent;
import com.palmcel.parenting.model.LoadDataResult;
import com.palmcel.parenting.model.PostComment;
import com.palmcel.parenting.model.PostCommentBuilder;

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
    private static final String ARG_COMMENT_COUNT = "commentCount";

    private ListView mListView;
    private CommentListAdapter mAdapter;
    private View mSubmitButton;
    private EditText mCommentEdit;
    private Context mContext;
    private TextView mHeaderView;

    private String mPostId;
    private int mCommentCount;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param postId post id
     * @return A new instance of fragment CommentFragment.
     */
    public static CommentFragment newInstance(String postId, int commentCount) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        args.putInt(ARG_COMMENT_COUNT, commentCount);
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
            mCommentCount = getArguments().getInt(ARG_COMMENT_COUNT);
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

        mHeaderView.setText("");
        mHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostComment latestComment = mAdapter.getLatestPostComment();
                if (latestComment != null) {
                    mHeaderView.setText(getResources().getString(R.string.comments_loading_more));
                    // Load more comments
                    LoadCommentsManager.getInstance().loadCommentsMore(
                            mPostId, latestComment.timeMsCreated);
                } else {
                    Log.e(TAG, "Empty comment list", new RuntimeException());
                }
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitComment();
            }
        });
        // Load post comments. TODO (kpan): don't need to load feed every time in onResume.
        LoadCommentsManager.getInstance().loadComments(mPostId);
    }

    /**
     * Submit comment to server
     */
    private void submitComment() {
        String commentMessage = mCommentEdit.getText().toString();
        if (Strings.isNullOrEmpty(commentMessage)) {
            return;
        }

        PostCommentBuilder builder = PostCommentBuilder.newLocalRegularCommentBuilder(
                mPostId,
                commentMessage);

        EventBus.getDefault().post(new CommentsServiceStartEvent());

        CommentHandler commentHandler = new CommentHandler();
        ListenableFuture<PostComment> saveCommentFuture =
                commentHandler.saveCommentToServerOnThread(builder.build());

        Futures.addCallback(saveCommentFuture, new FutureCallback<PostComment>() {
            @Override
            public void onSuccess(PostComment postComment) {
                Log.d(TAG, "Saved post comment successfully for " + postComment.postId);

                EventBus.getDefault().post(new CommentsServiceFinishEvent());

                // Increase comment count in cache and db
                incrementPostCommentCount(postComment.postId);

                // Reload feed in FeedFragment
                LoadCommentsManager.getInstance().loadCommentsAfterSubmit(postComment.postId);
                // Clear comment edit
                mCommentEdit.setText("");

                // Update feed listview
                LoadFeedManager.getInstance().loadFeed();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(mContext, "Failed to save comment, " + throwable.toString(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to save post comment", throwable);
                EventBus.getDefault().post(new CommentsServiceFinishEvent());
            }
        }, new UiThreadExecutor());
    }

    private void incrementPostCommentCount(final String postId) {
        mCommentCount++;

        // Increase post comment count in cache for postId
        FeedCache.getInstance().incrementCommentCount(postId);

        // Increase post comment count in feed_post table for postId
        ExecutorUtil.execute(new Runnable() {
            @Override
            public void run() {
                PostDbHandler.getInstance().incrementCommentCount(postId);
            }
        });
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

            // Update 'load more comments' header view of the list view
            updateLoadMoreHeadView();

            if (loadCommentsParams.timeMsCreatedSince == 0) {
                // scroll list view to bottom after submitting a new comment or first open the
                // fragment. Don't scroll to bottom at load-more case.
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        // Select the last row so it will scroll into view...
                        mListView.setSelection(mAdapter.getCount() - 1);
                    }
                });
            }
        }
    }

    private void updateLoadMoreHeadView() {
        if (mAdapter.getCount() < mCommentCount) {
            mHeaderView.setText(getResources().getString(R.string.comments_load_more));
        } else {
            mHeaderView.setText("");
        }
    }

    /**
     * EventBus event for starting loading comments from server or writing comments to server.
     */
    public void onEventMainThread(CommentsServiceStartEvent event) {
        getActivity().setProgressBarIndeterminateVisibility(true);
    }
    /**
     * EventBus event for finishing loading comments from server or writing comments to server.
     */
    public void onEventMainThread(CommentsServiceFinishEvent event) {
        getActivity().setProgressBarIndeterminateVisibility(false);
    }
}