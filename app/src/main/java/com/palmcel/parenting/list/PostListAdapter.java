package com.palmcel.parenting.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.palmcel.parenting.model.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * ListView adapter for a list of posts
 */
public class PostListAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_REGULAR = 0;
    private static final int VIEW_TYPE_PICTURE = 1;
    private static final int VIEW_TYPE_PRODUCT = 2;
    private static final int VIEW_TYPE_QUESTION = 3;
    private static final int COUNT_VIEW_TYPES = VIEW_TYPE_QUESTION + 1;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private List<Post> mEntries = new ArrayList<Post>();

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
        Post post = mEntries.get(position);

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
                return getViewForRegularPost(post, convertView, parent);
            default:
                return null;
        }
    }

    /**
     * Get an item view for regular post
     * @param post
     * @param convertView
     * @param parent
     * @return
     */
    private View getViewForRegularPost(Post post, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new PostItemView(mContext);
        }
        ((PostItemView)convertView).updatePostItemView(post);

        return convertView;
    }

    public void updateEntries(List<Post> entries) {
        mEntries = entries;
        notifyDataSetChanged();
    }
}