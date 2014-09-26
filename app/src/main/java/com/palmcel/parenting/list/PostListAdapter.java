package com.palmcel.parenting.list;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.model.FeedPost;
import com.palmcel.parenting.model.FeedPostBuilder;

/**
 * ListView adapter for a list of posts in feed
 */
public class PostListAdapter extends BaseAdapter implements PostItemView.LikeChangeListener {

    private static final String TAG = "PostListAdapter";

    private static final int VIEW_TYPE_REGULAR = 0;
    private static final int VIEW_TYPE_PICTURE = 1;
    private static final int VIEW_TYPE_PRODUCT = 2;
    private static final int VIEW_TYPE_QUESTION = 3;
    private static final int COUNT_VIEW_TYPES = VIEW_TYPE_QUESTION + 1;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    // FeedPosts are sorted by timeMsInserted in DESC order
    private ImmutableList<FeedPost> mEntries = ImmutableList.of();

    public PostListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mEntries.size();
    }

    @Override
    public int getViewTypeCount() {
        return COUNT_VIEW_TYPES;
    }

    @Override
    public int getItemViewType(int position) {
        FeedPost post = mEntries.get(position);

        switch (post.postType) {
            case Picture:
                return VIEW_TYPE_PICTURE;
            case Product:
                return VIEW_TYPE_PRODUCT;
            case Question:
                return VIEW_TYPE_QUESTION;
            default:
                return VIEW_TYPE_REGULAR;
        }
    }

    @Override
    public Object getItem(int position) {
        return mEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedPost post = mEntries.get(position);
        switch (post.postType) {
            case Regular:
                return getViewForPost(post, convertView, parent);
            case Product:
                return getViewForPost(post, convertView, parent);
            default:
                return null;
        }
    }

    /**
     * Get an item view for post
     * @param post
     * @param convertView
     * @param parent
     * @return
     */
    private View getViewForPost(FeedPost post, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new PostItemView(mContext, post.postType);
            ((PostItemView)convertView).setLikeChangeListener(this);
        }
        ((PostItemView)convertView).updatePostItemView(post);

        return convertView;
    }

    public void updateEntries(ImmutableList<FeedPost> entries) {
        mEntries = entries;
        notifyDataSetChanged();
    }

    @Nullable
    public FeedPost getLastFeedPost() {
        if (mEntries.isEmpty()) {
            return null;
        } else {
            return mEntries.get(mEntries.size() - 1);
        }
    }

    @Override
    public void onLikeChanged(String postId, boolean isLiked) {
        // Update like count in mEntries
        Log.d(TAG, "In onLikeChanged for " + postId + ", isLiked=" + isLiked);
        ImmutableList.Builder<FeedPost> builder = ImmutableList.builder();
        for (FeedPost post: mEntries) {
            if (!post.postId.equals(postId)) {
                builder.add(post);
            } else {
                FeedPostBuilder feedPostBuilder = new FeedPostBuilder().from(post);
                feedPostBuilder.setLikes(post.likes + (isLiked ? 1 : -1));
                feedPostBuilder.setIsLiked(isLiked);
                builder.add(feedPostBuilder.build());
            }
        }

        mEntries = builder.build();

        notifyDataSetChanged();
    }
}