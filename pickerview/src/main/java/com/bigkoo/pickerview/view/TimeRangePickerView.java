package com.bigkoo.pickerview.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.ISelectTimeCallback;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间选择器
 * Created by Sai on 15/11/22.
 * Updated by XiaoSong on 2017-2-22.
 */
public class TimeRangePickerView extends BasePickerView implements View.OnClickListener {

    private WheelTime wheelTime_start; //自定义控件
    private WheelTime wheelTime_end; //自定义控件
    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";

    public TimeRangePickerView(PickerOptions pickerOptions) {
        super(pickerOptions.context);
        mPickerOptions = pickerOptions;
        initView(pickerOptions.context);
    }

    private void initView(Context context) {
        setDialogOutSideCancelable();
        initViews();
        initAnim();

        if (mPickerOptions.customListener == null) {
            LayoutInflater.from(context).inflate(R.layout.pickerview_timerange, contentContainer);

            //顶部标题
            TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
            RelativeLayout rv_top_bar = (RelativeLayout) findViewById(R.id.rv_topbar);

            //确定和取消按钮
            Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
            Button btnCancel = (Button) findViewById(R.id.btnCancel);

            btnSubmit.setTag(TAG_SUBMIT);
            btnCancel.setTag(TAG_CANCEL);

            btnSubmit.setOnClickListener(this);
            btnCancel.setOnClickListener(this);

            //设置文字
            btnSubmit.setText(TextUtils.isEmpty(mPickerOptions.textContentConfirm) ? context.getResources().getString(R.string.pickerview_submit) : mPickerOptions.textContentConfirm);
            btnCancel.setText(TextUtils.isEmpty(mPickerOptions.textContentCancel) ? context.getResources().getString(R.string.pickerview_cancel) : mPickerOptions.textContentCancel);
            tvTitle.setText(TextUtils.isEmpty(mPickerOptions.textContentTitle) ? "" : mPickerOptions.textContentTitle);//默认为空

            //设置color
            btnSubmit.setTextColor(mPickerOptions.textColorConfirm);
            btnCancel.setTextColor(mPickerOptions.textColorCancel);
            tvTitle.setTextColor(mPickerOptions.textColorTitle);
            rv_top_bar.setBackgroundColor(mPickerOptions.bgColorTitle);

            //设置文字大小
            btnSubmit.setTextSize(mPickerOptions.textSizeSubmitCancel);
            btnCancel.setTextSize(mPickerOptions.textSizeSubmitCancel);
            tvTitle.setTextSize(mPickerOptions.textSizeTitle);

        } else {
            mPickerOptions.customListener.customLayout(LayoutInflater.from(context).inflate(mPickerOptions.layoutRes, contentContainer));
        }
        // 时间转轮 自定义控件
        LinearLayout timePickerView = (LinearLayout) findViewById(R.id.timepicker);
        timePickerView.setBackgroundColor(mPickerOptions.bgColorWheel);
        // 时间转轮 自定义控件
        LinearLayout timePickerView1 = (LinearLayout) findViewById(R.id.timepicker1);
        timePickerView1.setBackgroundColor(mPickerOptions.bgColorWheel);
        initWheelTime(timePickerView,timePickerView1);
    }

    private void initWheelTime(LinearLayout timePickerView,LinearLayout timePickerView1) {
        wheelTime_start = new WheelTime(timePickerView, mPickerOptions.type, mPickerOptions.textGravity, mPickerOptions.textSizeContent);
        wheelTime_start.setSelectChangeCallback(new ISelectTimeCallback() {
            @Override
            public void onTimeSelectChanged() {
                try {
                    Date date = WheelTime.dateFormat.parse(wheelTime_start.getTime());
                    Date date1 = WheelTime.dateFormat.parse(wheelTime_end.getTime());
//                    Log.e("lmf",wheelTime_start.getTime()+"  ,  "+wheelTime_end.getTime());
                    if(date.getTime()>date1.getTime()){
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTime(date);
                        setDate(calendar,calendar);
                    }
                    if (mPickerOptions.timeRangeSelectChangeListener != null) {
                        date = WheelTime.dateFormat.parse(wheelTime_start.getTime());
                        date1 = WheelTime.dateFormat.parse(wheelTime_end.getTime());
                        mPickerOptions.timeRangeSelectChangeListener.onTimeRangeSelectChanged(date,date1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        wheelTime_start.setLunarMode(mPickerOptions.isLunarCalendar);

        wheelTime_end = new WheelTime(timePickerView1, mPickerOptions.type, mPickerOptions.textGravity, mPickerOptions.textSizeContent);
        wheelTime_end.setSelectChangeCallback(new ISelectTimeCallback() {
            @Override
            public void onTimeSelectChanged() {
                try {
                    Date date = WheelTime.dateFormat.parse(wheelTime_start.getTime());
                    Date date1 = WheelTime.dateFormat.parse(wheelTime_end.getTime());
//                    Log.e("lmf",wheelTime_start.getTime()+"  ,  "+wheelTime_end.getTime());
                    if(date.getTime()>date1.getTime()){
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTime(date1);
                        setDate(calendar,calendar);
                    }
                    if (mPickerOptions.timeRangeSelectChangeListener != null) {
                        date = WheelTime.dateFormat.parse(wheelTime_start.getTime());
                        date1 = WheelTime.dateFormat.parse(wheelTime_end.getTime());
                        mPickerOptions.timeRangeSelectChangeListener.onTimeRangeSelectChanged(date,date1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        wheelTime_end.setLunarMode(mPickerOptions.isLunarCalendar);

        if (mPickerOptions.startYear != 0 && mPickerOptions.endYear != 0
                && mPickerOptions.startYear <= mPickerOptions.endYear) {
            setRange();
        }

        //若手动设置了时间范围限制
        if (mPickerOptions.startDate != null && mPickerOptions.endDate != null) {
            if (mPickerOptions.startDate.getTimeInMillis() > mPickerOptions.endDate.getTimeInMillis()) {
                throw new IllegalArgumentException("startDate can't be later than endDate");
            } else {
                setRangDate();
            }
        } else if (mPickerOptions.startDate != null) {
            if (mPickerOptions.startDate.get(Calendar.YEAR) < 1900) {
                throw new IllegalArgumentException("The startDate can not as early as 1900");
            } else {
                setRangDate();
            }
        } else if (mPickerOptions.endDate != null) {
            if (mPickerOptions.endDate.get(Calendar.YEAR) > 2100) {
                throw new IllegalArgumentException("The endDate should not be later than 2100");
            } else {
                setRangDate();
            }
        } else {//没有设置时间范围限制，则会使用默认范围。
            setRangDate();
        }

        setTime();
        wheelTime_start.setLabels(mPickerOptions.label_year, mPickerOptions.label_month, mPickerOptions.label_day
                , mPickerOptions.label_hours, mPickerOptions.label_minutes, mPickerOptions.label_seconds);
        wheelTime_start.setTextXOffset(mPickerOptions.x_offset_year, mPickerOptions.x_offset_month, mPickerOptions.x_offset_day,
                mPickerOptions.x_offset_hours, mPickerOptions.x_offset_minutes, mPickerOptions.x_offset_seconds);
        wheelTime_start.setItemsVisible(mPickerOptions.itemsVisibleCount);
        wheelTime_start.setAlphaGradient(mPickerOptions.isAlphaGradient);
        setOutSideCancelable(mPickerOptions.cancelable);
        wheelTime_start.setCyclic(mPickerOptions.cyclic);
        wheelTime_start.setDividerColor(mPickerOptions.dividerColor);
        wheelTime_start.setDividerType(mPickerOptions.dividerType);
        wheelTime_start.setLineSpacingMultiplier(mPickerOptions.lineSpacingMultiplier);
        wheelTime_start.setTextColorOut(mPickerOptions.textColorOut);
        wheelTime_start.setTextColorCenter(mPickerOptions.textColorCenter);
        wheelTime_start.isCenterLabel(mPickerOptions.isCenterLabel);

        wheelTime_end.setLabels(mPickerOptions.label_year, mPickerOptions.label_month, mPickerOptions.label_day
                , mPickerOptions.label_hours, mPickerOptions.label_minutes, mPickerOptions.label_seconds);
        wheelTime_end.setTextXOffset(mPickerOptions.x_offset_year, mPickerOptions.x_offset_month, mPickerOptions.x_offset_day,
                mPickerOptions.x_offset_hours, mPickerOptions.x_offset_minutes, mPickerOptions.x_offset_seconds);
        wheelTime_end.setItemsVisible(mPickerOptions.itemsVisibleCount);
        wheelTime_end.setAlphaGradient(mPickerOptions.isAlphaGradient);
        wheelTime_end.setCyclic(mPickerOptions.cyclic);
        wheelTime_end.setDividerColor(mPickerOptions.dividerColor);
        wheelTime_end.setDividerType(mPickerOptions.dividerType);
        wheelTime_end.setLineSpacingMultiplier(mPickerOptions.lineSpacingMultiplier);
        wheelTime_end.setTextColorOut(mPickerOptions.textColorOut);
        wheelTime_end.setTextColorCenter(mPickerOptions.textColorCenter);
        wheelTime_end.isCenterLabel(mPickerOptions.isCenterLabel);
    }


    /**
     * 设置默认时间
     */
    public void setDate(Calendar startSelectDate,Calendar endSelectDate) {
        mPickerOptions.startSelectDate = startSelectDate;
        mPickerOptions.endSelectDate = endSelectDate;
        setTime();
    }

    /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
    private void setRange() {
        wheelTime_start.setStartYear(mPickerOptions.startYear);
        wheelTime_start.setEndYear(mPickerOptions.endYear);
        wheelTime_end.setStartYear(mPickerOptions.startYear);
        wheelTime_end.setEndYear(mPickerOptions.endYear);
    }

    /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
    private void setRangDate() {
        wheelTime_start.setRangDate(mPickerOptions.startDate, mPickerOptions.endDate);
        wheelTime_end.setRangDate(mPickerOptions.startDate, mPickerOptions.endDate);
        initDefaultSelectedDate();
    }

    private void initDefaultSelectedDate() {
        //如果手动设置了时间范围
        if (mPickerOptions.startDate != null && mPickerOptions.endDate != null) {
            //若默认时间未设置，或者设置的默认时间越界了，则设置默认选中时间为开始时间。
            if (mPickerOptions.date == null || mPickerOptions.date.getTimeInMillis() < mPickerOptions.startDate.getTimeInMillis()
                    || mPickerOptions.date.getTimeInMillis() > mPickerOptions.endDate.getTimeInMillis()) {
                mPickerOptions.date = mPickerOptions.startDate;
            }
        } else if (mPickerOptions.startDate != null) {
            //没有设置默认选中时间,那就拿开始时间当默认时间
            mPickerOptions.date = mPickerOptions.startDate;
        } else if (mPickerOptions.endDate != null) {
            mPickerOptions.date = mPickerOptions.endDate;
        }
    }

    /**
     * 设置选中时间,默认选中当前时间
     */
    private void setTime() {
        int year, month, day, hours, minute, seconds;
        Calendar calendar = Calendar.getInstance();

        if (mPickerOptions.startSelectDate == null) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);
        } else {
            year = mPickerOptions.startSelectDate.get(Calendar.YEAR);
            month = mPickerOptions.startSelectDate.get(Calendar.MONTH);
            day = mPickerOptions.startSelectDate.get(Calendar.DAY_OF_MONTH);
            hours = mPickerOptions.startSelectDate.get(Calendar.HOUR_OF_DAY);
            minute = mPickerOptions.startSelectDate.get(Calendar.MINUTE);
            seconds = mPickerOptions.startSelectDate.get(Calendar.SECOND);
        }

        wheelTime_start.setPicker(year, month, day, hours, minute, seconds);

        if (mPickerOptions.endSelectDate == null) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);
        } else {
            year = mPickerOptions.endSelectDate.get(Calendar.YEAR);
            month = mPickerOptions.endSelectDate.get(Calendar.MONTH);
            day = mPickerOptions.endSelectDate.get(Calendar.DAY_OF_MONTH);
            hours = mPickerOptions.endSelectDate.get(Calendar.HOUR_OF_DAY);
            minute = mPickerOptions.endSelectDate.get(Calendar.MINUTE);
            seconds = mPickerOptions.endSelectDate.get(Calendar.SECOND);
        }

        wheelTime_end.setPicker(year, month, day, hours, minute, seconds);
    }


    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_SUBMIT)) {
            returnData();
        } else if (tag.equals(TAG_CANCEL)) {
            if (mPickerOptions.cancelListener != null) {
                mPickerOptions.cancelListener.onClick(v);
            }
        }
        dismiss();
    }

    public void returnData() {
        if (mPickerOptions.timeRangeSelectListener != null) {
            try {
                Date date = WheelTime.dateFormat.parse(wheelTime_start.getTime());
                Date date1 = WheelTime.dateFormat.parse(wheelTime_end.getTime());
                mPickerOptions.timeRangeSelectListener.onTimeRangeSelect(date, date1,clickView);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 动态设置标题
     *
     * @param text 标题文本内容
     */
    public void setTitleText(String text) {
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText(text);
        }
    }

    /**
     * 目前暂时只支持设置1900 - 2100年
     *
     * @param lunar 农历的开关
     */
    public void setLunarCalendar(boolean lunar) {
        try {
            int year, month, day, hours, minute, seconds;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(WheelTime.dateFormat.parse(wheelTime_start.getTime()));
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);

            wheelTime_start.setLunarMode(lunar);
            wheelTime_start.setLabels(mPickerOptions.label_year, mPickerOptions.label_month, mPickerOptions.label_day,
                    mPickerOptions.label_hours, mPickerOptions.label_minutes, mPickerOptions.label_seconds);
            wheelTime_start.setPicker(year, month, day, hours, minute, seconds);

            calendar.setTime(WheelTime.dateFormat.parse(wheelTime_end.getTime()));
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);

            wheelTime_end.setLunarMode(lunar);
            wheelTime_end.setLabels(mPickerOptions.label_year, mPickerOptions.label_month, mPickerOptions.label_day,
                    mPickerOptions.label_hours, mPickerOptions.label_minutes, mPickerOptions.label_seconds);
            wheelTime_end.setPicker(year, month, day, hours, minute, seconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isLunarCalendar() {
        return wheelTime_start.isLunarMode();
    }


    @Override
    public boolean isDialog() {
        return mPickerOptions.isDialog;
    }
}
