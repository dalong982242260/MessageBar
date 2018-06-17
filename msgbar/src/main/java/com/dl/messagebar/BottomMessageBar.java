package com.dl.messagebar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

/**
 * @author zwl
 * @describe 从下面显示的消息view
 * @date on 2018/6/17
 */
public class BottomMessageBar extends MessageBar {
    public BottomMessageBar(@NonNull Context context) {
        super(context);
    }

    public static MessageBar make(@NonNull ViewGroup viewGroup, String message, long time) {
        return make(Position.BOTTOM, viewGroup, message, time);
    }
}
