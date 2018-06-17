package com.dl.messagebar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zwl
 * @describe 消息view
 * @date on 2018/6/17
 */
public class MessageBar extends FrameLayout {

    public static final long DEFAULT_DURATION = 300;//动画时间
    public static final long DEFAULT_TIME = 300;//默认显示时间，时间已过隐藏


    public static final int LENGTH_LONG = 5000;
    public static final int LENGTH_SHORT = 3000;

    private ViewGroup mParentGroup;
    private TextView msgTv;
    private Position position;
    private Interpolator enterInterpolator;
    private Interpolator exitInterpolator;
    private long time;
    private List<MessageBarListener> mMsgListeners = new ArrayList<>();

    public MessageBar(@NonNull Context context) {
        super(context);
    }

    public MessageBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected static MessageBar make(Position position, @NonNull ViewGroup viewGroup, String message, long time) {
        MessageBar toast = (MessageBar) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_message, viewGroup, false);
        toast.setText(message)
                .setPosition(position)
                .setTime(time)
                .setViewGroup(viewGroup)
                .setVisibility(INVISIBLE);
        return toast;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        msgTv = (TextView) findViewById(R.id.message_textview);
    }


    /**
     * 设置消息内容
     *
     * @param msg
     * @return
     */
    public MessageBar setText(String msg) {
        if (msgTv != null && !TextUtils.isEmpty(msg)) {
            msgTv.setText(Html.fromHtml(msg));
        }
        return this;
    }

    /**
     * 设置消息文字颜色
     *
     * @param color
     * @return
     */
    public MessageBar setTextColor(int color) {
        if (color == 0) return this;
        msgTv.setTextColor(color);
        return this;
    }

    /**
     * 设置时间
     *
     * @param time
     * @return
     */
    public MessageBar setTime(long time) {
        if (time != 0) this.time = time;
        return this;
    }

    /**
     * 设置位置显示
     *
     * @param position
     * @return
     */
    public MessageBar setPosition(Position position) {
        this.position = position;
        if (position == Position.BOTTOM) {
            if (getLayoutParams() instanceof FrameLayout.LayoutParams) {
                ((LayoutParams) getLayoutParams()).gravity = Gravity.BOTTOM;
            } else if (getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }
            setAnimationInterpolator(new LinearInterpolator(), null);
        } else {
            if (getLayoutParams() instanceof FrameLayout.LayoutParams) {
                ((LayoutParams) getLayoutParams()).gravity = Gravity.TOP;
            } else if (getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            setAnimationInterpolator(new LinearInterpolator(), null);
        }
        return this;
    }

    private MessageBar setViewGroup(ViewGroup viewGroup) {
        this.mParentGroup = viewGroup;
        return this;
    }


    /**
     * 设置动画插值器
     * BounceInterpolator 带有回弹
     *
     * @param enterInterpolator
     * @param exitInterpolator
     */
    private MessageBar setAnimationInterpolator(Interpolator enterInterpolator, Interpolator exitInterpolator) {
        this.enterInterpolator = enterInterpolator;
        this.exitInterpolator = exitInterpolator;
        return this;
    }

    /**
     * 设置背景颜色
     *
     * @param color
     * @return
     */
    public MessageBar setBackground(int color) {
        if (color == 0) return this;
        setBackgroundColor(color);
        return this;
    }


    MessageBar addMessageBarListener(MessageBarListener toastListener) {
        if (toastListener != null) {
            mMsgListeners.add(toastListener);
        }
        return this;
    }

    MessageBar removeMessageBarListener(MessageBarListener toastListener) {
        if (toastListener != null) {
            mMsgListeners.remove(toastListener);
        }
        return this;
    }


    /**
     * 显示
     */
    public void show() {
        show(0);
    }

    /**
     * 延时显示
     *
     * @param delay 延时时间
     */
    public void show(final long delay) {
        if (this.getParent() != null) {
            return;
        }

        if (mParentGroup != null) {
            mParentGroup.addView(this);
        }

        post(new Runnable() {
            @Override
            public void run() {
                MessageBar.this.setVisibility(VISIBLE);
                if (position == Position.BOTTOM) MessageBar.this.setY((getY() + getHeight()));
                else MessageBar.this.setY((getY() - getHeight()));
            }
        });
        postDelayed(new Runnable() {
            @Override
            public void run() {
                MessageBar.this.animate().translationY(0).setDuration(DEFAULT_DURATION).setInterpolator(enterInterpolator == null ? new BounceInterpolator() : enterInterpolator).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        MessageBar.this.setVisibility(VISIBLE);
                        for (MessageBarListener messageBarListener : mMsgListeners) {
                            messageBarListener.onMsgShow(MessageBar.this);
                        }
                    }
                }).start();
            }
        }, delay);
        if (time != -1) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    MessageBar.this.animate().translationY(position == Position.BOTTOM ? getHeight() : -getHeight()).setDuration(DEFAULT_DURATION).setInterpolator(exitInterpolator == null ? new AccelerateInterpolator() : exitInterpolator).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (MessageBar.this.getParent() instanceof ViewGroup) {
                                ((ViewGroup) MessageBar.this.getParent()).removeView(MessageBar.this);
                            }
                            for (MessageBarListener messageBarListener : mMsgListeners) {
                                messageBarListener.onMsgDismiss(MessageBar.this);
                            }
                        }
                    }).start();
                }
            }, time > 0 ? delay + time + DEFAULT_DURATION : delay + DEFAULT_TIME + DEFAULT_DURATION);
        }
    }

    public enum Position {
        TOP,
        BOTTOM,
    }
}
