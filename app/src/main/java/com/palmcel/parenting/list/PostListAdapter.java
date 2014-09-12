package com.palmcel.parenting.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.palmcel.parenting.model.FeedPost;
import com.palmcel.parenting.model.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * ListView adapter for a list of posts in feed
 */
public class PostListAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_REGULAR = 0;
    private static final int VIEW_TYPE_PICTURE = 1;
    private static final int VIEW_TYPE_PRODUCT = 2;
    private static final int VIEW_TYPE_QUESTION = 3;
    private static final int COUNT_VIEW_TYPES = VIEW_TYPE_QUESTION + 1;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private List<FeedPost> mEntries = new ArrayList<FeedPost>();

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
        Post post = mEntries.get(position);
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
    private View getViewForPost(Post post, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new PostItemView(mContext, post.postType);
        }
        ((PostItemView)convertView).updatePostItemView(post);

        return convertView;
    }

    public void updateEntries(List<FeedPost> entries) {
        mEntries = entries;
        notifyDataSetChanged();
    }
}