package com.palmcel.parenting.likes;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.model.PostLike;

/**
 * ListView adapter for a list of likes for a post
 */
public class LikesListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    // mEntries are sorted by timeMsLike in DESC order
    private ImmutableList<PostLike> mEntries = ImmutableList.of();

    public LikesListAdapter(Context context) {
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
        PostLike postLike = mEntries.get(position);
        if (convertView == null) {
            convertView = new LikeItemView(mContext);
        }
        ((LikeItemView)convertView).updateLikeItemView(postLike);

        return convertView;
    }

    public void updateEntries(ImmutableList<PostLike> entries) {
        mEntries = entries;
        notifyDataSetChanged();
    }

    @Nullable
    public PostLike getLastPostLike() {
        if (mEntries.isEmpty()) {
            return null;
        } else {
            return mEntries.get(mEntries.size() - 1);
        }
    }
}