package com.palmcel.parenting.list;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.common.base.Strings;
import com.palmcel.parenting.R;
import com.palmcel.parenting.comment.CommentActivity;
import com.palmcel.parenting.common.AppContext;
import com.palmcel.parenting.common.TimeUtil;
import com.palmcel.parenting.common.Utils;
import com.palmcel.parenting.model.Post;
import com.palmcel.parenting.model.PostType;
import com.squareup.picasso.Picasso;

/**
 * A item view of a post in the post list view.
 */
public class PostItemView extends RelativeLayout {

    private static String sAnonymousText;
    private ImageView mProfileImageView;
    private TextView mUserIdTextView;
    private TextView mKidsInfoTextView;
    private TextView mPostTimeTextView;
    private TextView mMessageTextView;
    private ImageView mCommentButton;
    private TextView mCommentCountTextView;
    private ToggleButton mLikeButton;
    private TextView mLikeCountTextView;
    private View mExternalLinkLayout;
    private TextView mExternalLinkCaption;
    private TextView mExternalLinkDescription;
    private TextView mExternalLinkDomain;
    private ImageView mExternalLinkPicture;

    private Context mContext;
    private Post mPost;

    public PostItemView(Context context, PostType postType) {
        this(context, null, postType);
    }

    private static String getAnonymousText() {
        if (sAnonymousText == null) {
            sAnonymousText =
                    AppContext.getAplicationContext().getResources().getString(R.string.anonymous);
        }

        return sAnonymousText;
    }

    public PostItemView(Context context, AttributeSet attrs, PostType postType) {
        super(context, attrs);

        mContext = context;

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

        if (postType == PostType.Product) {
            mExternalLinkLayout = findViewById(R.id.external_page_layout);
            mExternalLinkCaption = (TextView) findViewById(R.id.external_link_caption);
            mExternalLinkDescription = (TextView) findViewById(R.id.external_link_description);
            mExternalLinkDomain = (TextView) findViewById(R.id.external_link_domain);
            mExternalLinkPicture = (ImageView) findViewById(R.id.external_link_image);
        }

        mCommentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, CommentActivity.class));
            }
        });
    }

    /**
     * Update PostItemView using post data
     * @param post user post data
     */
    public PostItemView updatePostItemView(Post post) {
        mPost = post;
        if (post.isAnonymous) {
            mUserIdTextView.setText(getAnonymousText());
        } else {
            mUserIdTextView.setText(post.userId);
        }
        mMessageTextView.setText(post.message);
        mCommentCountTextView.setText(post.comments > 0 ? "" + post.comments : "");
        mLikeCountTextView.setText(post.likes > 0 ? "" + post.likes : "");
        mPostTimeTextView.setText(
                post.timeMsCreated > 0 ? TimeUtil.getTimeAgo(post.timeMsCreated) : "");

        if (post.postType == PostType.Product) {
            // Update views for external link for product post
            mExternalLinkLayout.setVisibility(View.VISIBLE);
            if (!Strings.isNullOrEmpty(post.externalLinkCaption)) {
                mExternalLinkCaption.setVisibility(View.VISIBLE);
                mExternalLinkCaption.setText(post.externalLinkCaption);
            } else {
                mExternalLinkCaption.setVisibility(View.GONE);
            }
            if (!Strings.isNullOrEmpty(post.externalLinkSummary)) {
                mExternalLinkDescription.setVisibility(View.VISIBLE);
                mExternalLinkDescription.setText(post.externalLinkSummary);
            } else {
                mExternalLinkDescription.setVisibility(View.GONE);
            }
            if (!Strings.isNullOrEmpty(post.externalLinkUrl)) {
                // TODO (kpan), put domain into Post.java
                String domainName = Utils.getDomainName(post.externalLinkUrl);
                if (!Strings.isNullOrEmpty(domainName)) {
                    mExternalLinkDomain.setVisibility(View.VISIBLE);
                    mExternalLinkDomain.setText(domainName);
                } else {
                    mExternalLinkDomain.setVisibility(View.GONE);
                }
            } else {
                mExternalLinkDomain.setVisibility(View.GONE);
            }

            if (!Strings.isNullOrEmpty(post.externalLinkImageUrl)) {
                mExternalLinkPicture.setVisibility(View.VISIBLE);
                Picasso.with(mContext)
                       .load(post.externalLinkImageUrl)
                       .placeholder(R.drawable.ic_wait)
                       .into(mExternalLinkPicture);
            } else {
                mExternalLinkPicture.setVisibility(View.GONE);
            }
        }

        return this;
    }
}
