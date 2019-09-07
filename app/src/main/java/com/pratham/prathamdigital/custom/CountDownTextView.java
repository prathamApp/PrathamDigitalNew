package com.pratham.prathamdigital.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.pratham.prathamdigital.R;

public class CountDownTextView extends TextSwitcher
        implements TextSwitcher.ViewFactory, View.OnClickListener {

    private int TOTAL_MILLS;    //默认60s
    private String tipString;   //完成后的提示文字
    private String initString;  //初始文字
    private String gapStringFormat;   //gap间隙提示

    private float TEXT_SIZE;    //默认16sp
    private ColorStateList mColorStateList; //默认黑色

    private int BgEnableResId;  //可用时背景
    private int BgDisableResId; //不可用时背景

    private int animIn;         //文字进入动画
    private int animOut;        //文字退出动画

    private CountDownTimer timer;
    private ISendListener mListener;

    private boolean countDownFinish;

    public CountDownTextView(Context context) {
        this(context, null);
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        checkAttrs(attrs);
        configSwitcher();
        configTimer();
        setOnClickListener(this);

        countDownFinish = true; //允许第一次点击
        setBackgroundResource(BgEnableResId);
        setText(initString);
    }

    /**
     * 检查自定义属性
     */
    private void checkAttrs(AttributeSet attrs) {

        float defaultTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16.0f,
                Resources.getSystem().getDisplayMetrics());

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CountDownTextView);
        TEXT_SIZE = ta.getDimension(R.styleable.CountDownTextView_text_size, defaultTextSize);
        mColorStateList = ta.getColorStateList(R.styleable.CountDownTextView_text_color);
        if (null == mColorStateList) {
            mColorStateList = ColorStateList.valueOf(Color.BLACK);
        }
        BgEnableResId = ta.getResourceId(R.styleable.CountDownTextView_enable_background,
                android.R.color.transparent);
        BgDisableResId = ta.getResourceId(R.styleable.CountDownTextView_disable_background,
                android.R.color.transparent);
        animIn = ta.getResourceId(R.styleable.CountDownTextView_anim_in, android.R.anim.fade_in);
        animOut = ta.getResourceId(R.styleable.CountDownTextView_anim_out, android.R.anim.fade_out);

        TOTAL_MILLS = ta.getInteger(R.styleable.CountDownTextView_total_time, 60);

        tipString = ta.getString(R.styleable.CountDownTextView_tip_text);
        if (TextUtils.isEmpty(tipString)) {
            tipString = "";
        }

        initString = ta.getString(R.styleable.CountDownTextView_init_text);
        if (TextUtils.isEmpty(initString)) {
            initString = "";
        }

        gapStringFormat = ta.getString(R.styleable.CountDownTextView_gap_string_format);
        if (TextUtils.isEmpty(gapStringFormat)) {
            gapStringFormat = getContext().getString(R.string.count_down_gap_string_format);
        }

        ta.recycle();
    }

    public void start() {
        timer.start();
        setBackgroundResource(BgDisableResId);
    }

    public void cancel() {
        timer.cancel();
        timer.onFinish();
    }

    public void reSet() {
        timer.cancel();
        countDownFinish = true;
        setText(initString);
        setBackgroundResource(BgEnableResId);
        textHint(false);
    }

    private void configTimer() {
        if (null == timer) {
            timer = new CountDownTimer(TOTAL_MILLS * 1000, 1000) {
                @Override
                public void onTick(long l) {
                    onGapCallback(l);
                }

                @Override
                public void onFinish() {
                    onCountDownFinish();
                }
            };
        }
    }

    private void onCountDownFinish() {

        countDownFinish = true;
        String show = tipString;
        setText(show);
        setBackgroundResource(BgEnableResId);
        textHint(false);
    }

    @SuppressLint("StringFormatMatches")
    private void onGapCallback(long rest) {

        countDownFinish = false;
        String gap;
        try {
            gap = String.format(gapStringFormat, (rest / 1000));
        } catch (Exception ex) {

            gap = String.format(getContext().getString(R.string.count_down_gap_string_format), (rest / 1000));
            ex.printStackTrace();
        }

        setText(gap);
        textHint(true);
    }

    private void configSwitcher() {
        setFactory(this);
        setInAnimation(getContext(), animIn);
        setOutAnimation(getContext(), animOut);
    }

    public void textHint(boolean hint) {
        setSelected(hint);
    }

    public boolean isProcessing() {
        return !countDownFinish;
    }

    @Override
    public View makeView() {
        TextView textView = new TextView(getContext());
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(mColorStateList);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE);
        int padding = TypedValue.complexToDimensionPixelSize(10,
                Resources.getSystem().getDisplayMetrics());
        textView.setPadding(padding, padding, padding, padding);

        LayoutParams params =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        textView.setLayoutParams(params);
        return textView;
    }

    @Override
    public void onClick(View view) {
        if (countDownFinish && mListener != null) {
            mListener.onSend(view);
        }
    }

    /**
     * 重发事件
     */
    public void setSendListener(ISendListener listener) {
        this.mListener = listener;
    }

    public interface ISendListener {
        void onSend(View view);
    }
}