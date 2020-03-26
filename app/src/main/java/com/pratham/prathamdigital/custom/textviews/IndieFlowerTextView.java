package com.pratham.prathamdigital.custom.textviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.util.PD_Constant;

public class IndieFlowerTextView extends androidx.appcompat.widget.AppCompatTextView {
    public IndieFlowerTextView(Context context) {
        super(context);
        Typeface face = null;
        if (FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI).equalsIgnoreCase(PD_Constant.ORIYA))
            face = Typeface.createFromAsset(context.getAssets(), "fonts/oriya.ttf");
        else face = Typeface.createFromAsset(context.getAssets(), "fonts/Fred.ttf");
        this.setTypeface(face);
    }

    public IndieFlowerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = null;
        if (FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI).equalsIgnoreCase(PD_Constant.ORIYA))
            face = Typeface.createFromAsset(context.getAssets(), "fonts/oriya.ttf");
        else face = Typeface.createFromAsset(context.getAssets(), "fonts/Fred.ttf");
        this.setTypeface(face);
    }

    public IndieFlowerTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = null;
        if (FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI).equalsIgnoreCase(PD_Constant.ORIYA))
            face = Typeface.createFromAsset(context.getAssets(), "fonts/oriya.ttf");
        else face = Typeface.createFromAsset(context.getAssets(), "fonts/Fred.ttf");
        this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
