package com.palmcel.parenting.comment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.palmcel.parenting.R;
import com.palmcel.parenting.common.AppContext;
import com.palmcel.parenting.common.TimeUtil;
import com.palmcel.parenting.model.PostComment;

/**
 * A item view of a comment in the comments list view.
 */
public class CommentItemView extends RelativeLayout {

    private static String sAnonymousText;
    private ImageView mProfileImageView;
    private TextView mUserIdTextView;
    private TextView mPostTimeTextView;
    private TextView mMessageTextView;

    private Context mContext;
    private PostComment mPostComment;

    public CommentItemView(Context context) {
        this(context, null);
    }

    private static String getAnonymousText() {
        if (sAnonymousText == null) {
            sAnonymousText =
                    AppContext.getAplicationContext().getResources().getString(R.string.anonymous);
        }

        return sAnonymousText;
    }

    public CommentItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        int padding = (int) getResources().getDimension(R.dimen.row_item_padding);
        setPadding(padding, padding, padding, 0);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.comment_row_item, this);

        mProfileImageView = (ImageView) findViewById(R.id.profile_image);
        mUserIdTextView = (TextView) findViewById(R.id.user_id);
        mPostTimeTextView = (TextView) findViewById(R.id.post_time);
        mMessageTextView = (TextView) findViewById(R.id.message);
    }

    /**
     * Update CommentItemView using post comment data
     * @param postComment post comment
     */
    public CommentItemView updateCommentItemView(PostComment postComment) {
        mPostComment = postComment;
        if (postComment.isAnonymous) {
            mUserIdTextView.setText(getAnonymousText());
        } else {
            mUserIdTextView.setText(postComment.commenterUserId);
        }
        mMessageTextView.setText(postComment.commentMessage);
        mPostTimeTextView.setText(
                postComment.timeMsCreated > 0 ?
                        TimeUtil.getTimeAgo(postComment.timeMsCreated) : "");

        return this;
    }
}
