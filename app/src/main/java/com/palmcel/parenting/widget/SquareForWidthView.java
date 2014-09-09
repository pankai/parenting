package com.palmcel.parenting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Square view. The height is always the same as width.
 */
public class SquareForWidthView extends View {
    public SquareForWidthView(Context context) {
        super(context);
    }

    public SquareForWidthView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareForWidthView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = getMeasuredWidth();
        setMeasuredDimension(size, size);
    }
}
