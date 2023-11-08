package com.bigkoo.pickerview.listener;

import android.view.View;

import java.util.Date;

/**
 * Created by lmf on 2023/11/7.
 */
public interface OnTimeRangeSelectListener {

    void onTimeRangeSelect(Date dateStart,Date dateEnd, View v);
}
