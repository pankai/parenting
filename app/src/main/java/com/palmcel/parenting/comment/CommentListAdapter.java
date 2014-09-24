package com.palmcel.parenting.comment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.model.PostComment;

/**
 * ListView adapter for a list of comments for a post
 */
public class CommentListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    // mEntries are sorted by timeMsCreated in ASC order
    private ImmutableList<PostComment> mEntries = ImmutableList.of();

    public CommentListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mEntries.size();
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
        PostComment postComment = mEntries.get(position);
        if (convertView == null) {
            convertView = new CommentItemView(mContext);
        }
        ((CommentItemView)convertView).updateCommentItemView(postComment);

        return convertView;
    }

    public void updateEntries(ImmutableList<PostComment> entries) {
        mEntries = entries.reverse();
        notifyDataSetChanged();
    }

    @Nullable
    public PostComment getLastPostComment() {
        if (mEntries.isEmpty()) {
            return null;
        } else {
            return mEntries.get(mEntries.size() - 1);
        }
    }
}