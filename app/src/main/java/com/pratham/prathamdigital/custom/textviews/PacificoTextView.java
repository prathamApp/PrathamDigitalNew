package com.pratham.prathamdigital.custom.textviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class PacificoTextView extends android.support.v7.widget.AppCompatTextView {
    public PacificoTextView(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/pacifico_regular.ttf");
        this.setTypeface(face);
    }

    public PacificoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/pacifico_regular.ttf");
        this.setTypeface(face);
    }

    public PacificoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/pacifico_regular.ttf");
        this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
