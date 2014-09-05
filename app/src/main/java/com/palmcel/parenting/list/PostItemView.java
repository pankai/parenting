package com.palmcel.parenting.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.palmcel.parenting.R;
import com.palmcel.parenting.common.TimeUtil;
import com.palmcel.parenting.model.Post;

/**
 * A item view of a post in the post list view.
 */
public class PostItemView extends RelativeLayout {

    private ImageView mProfileImageView;
    private TextView mUserIdTextView;
    private TextView mKidsInfoTextView;
    private TextView mPostTimeTextView;
    private TextView mMessageTextView;
    private ImageView mCommentButton;
    private TextView mCommentCountTextView;
    private ToggleButton mLikeButton;
    private TextView mLikeCountTextView;

    private Post mPost;

    public PostItemView(Context context) {
        this(context, null);
    }

    public PostItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        int padding = (int) getResources().getDimension(R.dimen.row_item_padding);
        setPadding(padding, padding, padding, 0);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.post_row_item, this);

        mProfileImageView = (ImageView) findViewById(R.id.profile_image);
        mUserIdTextView = (TextView) findViewById(R.id.user_id);
        mKidsInfoTextView = (TextView) findViewById(R.id.kids_info);
        mPostTimeTextView = (TextView) findViewById(R.id.post_time);
        mMessageTextView = (TextView) findViewById(R.id.message);
        mCommentButton = (ImageView) findViewById(R.id.comment_button);
        mCommentCountTextView = (TextView) findViewById(R.id.comment_count);
        mLikeButton = (ToggleButton) findViewById(R.id.like_button);
        mLikeCountTextView = (TextView) findViewById(R.id.like_count);
    }

    /**
     * Update PostItemView using post data
     * @param post user post data
     */
    public PostItemView updatePostItemView(Post post) {
        mPost = post;
        mUserIdTextView.setText(post.userId);
        mMessageTextView.setText(post.message);
        mCommentCountTextView.setText(post.comments > 0 ? "" + post.comments : "");
        mLikeCountTextView.setText(post.likes > 0 ? "" + post.likes : "");
        mPostTimeTextView.setText(
                post.timeMsCreated > 0 ? TimeUtil.getTimeAgo(post.timeMsCreated) : "");
        return this;
    }
}
