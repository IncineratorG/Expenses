package com.example.newcosts;


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
    public static final String SEPARATOR_ID = "&";
    public static final String SEPARATOR_MILLISECONDS = "%";
    public static final String SEPARATOR_PRIMARY = " ";
    public static String formatDigit(double d) {
        NumberFormat format = NumberFormat.getInstance(Locale.UK);
        format.setGroupingUsed(false);

        return format.format(d);
    }
}
