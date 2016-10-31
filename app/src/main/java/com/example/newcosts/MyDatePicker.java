package com.example.newcosts;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.Calendar;

public class MyDatePicker extends Dialog implements
        android.view.View.OnClickListener, ViewSwitcher.ViewFactory {

    private int PickedDay, PickedMonth, PickedYear;
    private int MaxTextViewWidth;
    private Calendar calendar;

    private Activity activity;

    private Button OkButton, CancelButton;
    private Button DayUpButton, DayDownButton,
                MonthUpButton, MonthDownButton,
                YearUpButton, YearDownButton;
    private TextSwitcher DayTextSwitcher,
                 MonthTextSwitcher,
                 YearTextSwitcher;
    private TextView ChosenDayMonthTextView,
                     ChosenYearTextView;


    MyDatePickerCallback Callback;
    public interface MyDatePickerCallback {
        public void getPickedDate(String pickedDate);
    }


    public MyDatePicker(Activity activity) {
        super(activity);
        this.activity = activity;

        Callback = (MyDatePickerCallback) activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.datepicker);

//        ViewGroup.LayoutParams params = getWindow().getAttributes();
//        params.width = ViewPager.LayoutParams.FILL_PARENT;
//        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

//        findMaxTextViewWidth();


        calendar = Calendar.getInstance();
        PickedDay = calendar.get(Calendar.DAY_OF_MONTH);
        PickedMonth = calendar.get(Calendar.MONTH);
        PickedYear = calendar.get(Calendar.YEAR);


//        Animation in = AnimationUtils.loadAnimation(activity,
//                android.R.anim.fade_in);
//        Animation out = AnimationUtils.loadAnimation(activity,
//                android.R.anim.fade_out);


        OkButton = (Button) findViewById(R.id.calendar_ok);
        OkButton.setOnClickListener(this);
        CancelButton = (Button) findViewById(R.id.calendar_cancel);
        CancelButton.setOnClickListener(this);

        DayUpButton = (Button) findViewById(R.id.calendar_dayUp);
        DayUpButton.setOnClickListener(this);
        DayDownButton = (Button) findViewById(R.id.calendar_dayDown);
        DayDownButton.setOnClickListener(this);
        MonthUpButton = (Button) findViewById(R.id.calendar_monthUp);
        MonthUpButton.setOnClickListener(this);
        MonthDownButton = (Button) findViewById(R.id.calendar_monthDown);
        MonthDownButton.setOnClickListener(this);
        YearUpButton = (Button) findViewById(R.id.calendar_yearUp);
        YearUpButton.setOnClickListener(this);
        YearDownButton = (Button) findViewById(R.id.calendar_yearDown);
        YearDownButton.setOnClickListener(this);

        DayTextSwitcher = (TextSwitcher) findViewById(R.id.calendar_day);
//        DayTextSwitcher.setInAnimation(in);
//        DayTextSwitcher.setOutAnimation(out);
        DayTextSwitcher.setFactory(this);
        DayTextSwitcher.setText(String.valueOf(PickedDay));
        MonthTextSwitcher = (TextSwitcher) findViewById(R.id.calendar_month);
//        MonthTextSwitcher.setInAnimation(in);
//        MonthTextSwitcher.setOutAnimation(out);
        MonthTextSwitcher.setFactory(this);
        MonthTextSwitcher.setText(Constants.MONTH_NAMES[PickedMonth]);
        YearTextSwitcher = (TextSwitcher) findViewById(R.id.calendar_year);
//        YearTextSwitcher.setInAnimation(in);
//        YearTextSwitcher.setOutAnimation(out);
        YearTextSwitcher.setFactory(this);
        YearTextSwitcher.setText(String.valueOf(PickedYear));


        ChosenDayMonthTextView = (TextView) findViewById(R.id.calendar_chosenDayMonth);
        ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                       String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                       Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);

        ChosenYearTextView = (TextView) findViewById(R.id.calendar_chosenYear);
        ChosenYearTextView.setText(String.valueOf(PickedYear));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.calendar_dayDown:
                --PickedDay;
                if (PickedDay <= 0)
                    PickedDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                DayTextSwitcher.setText(String.valueOf(PickedDay));
                calendar.set(Calendar.DAY_OF_MONTH, PickedDay);

                ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                               String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                               Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);
                break;
            case R.id.calendar_dayUp:
                ++PickedDay;
                if (PickedDay > calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                    PickedDay = 1;

                DayTextSwitcher.setText(String.valueOf(PickedDay));
                calendar.set(Calendar.DAY_OF_MONTH, PickedDay);

                ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                               String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                               Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);
                break;


            case R.id.calendar_monthDown:
                --PickedMonth;
                if (PickedMonth < 0)
                    PickedMonth = 11;

                MonthTextSwitcher.setText(Constants.MONTH_NAMES[PickedMonth]);
                calendar.set(Calendar.MONTH, PickedMonth);

                if (PickedDay > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    PickedDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    DayTextSwitcher.setText(String.valueOf(PickedDay));
                }

                ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                               String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                               Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);
                break;
            case R.id.calendar_monthUp:
                ++PickedMonth;
                if (PickedMonth > 11)
                    PickedMonth = 0;

                MonthTextSwitcher.setText(Constants.MONTH_NAMES[PickedMonth]);
                calendar.set(Calendar.MONTH, PickedMonth);

                if (PickedDay > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    PickedDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    DayTextSwitcher.setText(String.valueOf(PickedDay));
                };

                ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                               String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                               Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);
                break;


            case R.id.calendar_yearDown:
                --PickedYear;

                YearTextSwitcher.setText(String.valueOf(PickedYear));
                calendar.set(Calendar.YEAR, PickedYear);

                if (PickedDay > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    PickedDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    DayTextSwitcher.setText(String.valueOf(PickedDay));
                }

                ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                               String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                               Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);
                ChosenYearTextView.setText(String.valueOf(PickedYear));
                break;
            case R.id.calendar_yearUp:
                ++PickedYear;

                YearTextSwitcher.setText(String.valueOf(PickedYear));
                calendar.set(Calendar.YEAR, PickedYear);

                if (PickedDay > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    PickedDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    DayTextSwitcher.setText(String.valueOf(PickedDay));
                }

                ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                               String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                               Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);
                ChosenYearTextView.setText(String.valueOf(PickedYear));
                break;


            case R.id.calendar_ok:
                String pickedDate = String.valueOf(PickedDay) + "." +
                                    String.valueOf(PickedMonth + 1) + "." +
                                    String.valueOf(PickedYear);
                Callback.getPickedDate(pickedDate);
                dismiss();
                break;
            case R.id.calendar_cancel:
                dismiss();
                break;


            default:
                break;
        }
    }

    @Override
    public View makeView() {
        TextView t = new TextView(activity);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        t.setTextSize(24);
        t.setTextColor(Color.parseColor("#000000"));
        t.setMaxLines(1);

        return t;
    }







//    private void findMaxTextViewWidth() {
//        TextView tv = new TextView(activity);
//        tv.setTextSize(18);
//        tv.setText("Апрель");
//
//        tv.measure(0, 0);
//        MaxTextViewWidth = tv.getMeasuredWidth();
//        System.out.println(MaxTextViewWidth);
//    }
}
