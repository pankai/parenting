package com.palmcel.parenting.likes;

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
import com.palmcel.parenting.model.PostLike;

/**
 * A item view of a like in the likes list view.
 */
public class LikeItemView extends RelativeLayout {

    private static final String TAG = "LikeItemView";

    private ImageView mProfileImageView;
    private TextView mUserIdTextView;

    private Context mContext;

    public LikeItemView(Context context) {
        this(context, null);
    }

    public LikeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        int padding = (int) getResources().getDimension(R.dimen.row_item_padding);
        setPadding(padding, padding, padding, 0);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.like_row_item, this);

        mProfileImageView = (ImageView) findViewById(R.id.profile_image);
        mUserIdTextView = (TextView) findViewById(R.id.user_id);
    }

    /**
     * Update LikeItemView using post like data
     * @param postLike post like
     */
    public LikeItemView updateLikeItemView(PostLike postLike) {
        mUserIdTextView.setText(postLike.likerUserId);

        return this;
    }
}
