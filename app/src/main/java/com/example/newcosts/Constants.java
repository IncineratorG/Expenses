package com.example.newcosts;


import android.graphics.Color;

import java.text.NumberFormat;
import java.util.Locale;

public class Constants {
    public static final String[] DECLENSION_MONTH_NAMES = {"Января", "Февраля", "Марта", "Апреля",
            "Мая", "Июня", "Июля", "Августа",
            "Сентября", "Октября", "Ноября", "Декабря"};
    public static final String[] MONTH_NAMES = {"Январь", "Февраль", "Март", "Апрель",
            "Май", "Июнь", "Июль", "Август",
            "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    public static final String[] DAY_NAMES = {"", "Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"};
    public static final String SEPARATOR_VALUE = "$";
    public static final String SEPARATOR_DATE = "#";
    public static final String SEPARATOR_ID = "&";
    public static final String SEPARATOR_MILLISECONDS = "%";
    public static final String SEPARATOR_NOTE = "~";
    public static final int HEADER_SYSTEM_COLOR = Color.parseColor("#FF9800");
    public static final String tag = "globalTag";
    public static final String DATA_ARRAY_LABEL = "dataArray";
    public static final int INITIAL_DATE_IN_MILLISECONDS_INDEX = 0;
    public static final int ENDING_DATE_IN_MILLISECONDS_INDEX = 1;
    public static final int INITIAL_DATE_STRING_INDEX = 2;
    public static final int ENDING_DATE_STRING_INDEX = 3;
    public static final int COST_NAME_INDEX = 4;
    public static final int COST_VALUE_INDEX = 5;
    public static final int CHOSEN_MONTH_INDEX = 6;
    public static final int CHOSEN_YEAR_INDEX = 7;

    public static String formatDigit(double d) {
        NumberFormat format = NumberFormat.getInstance(Locale.UK);
        format.setGroupingUsed(false);

        return format.format(d);
    }
}
