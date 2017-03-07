package com.example.newcosts;

import android.content.Context;
import android.content.res.Resources;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.example.newcosts.R;

public class Constants {
//    public static final String[] DECLENSION_MONTH_NAMES = {"Января", "Февраля", "Марта", "Апреля",
//            "Мая", "Июня", "Июля", "Августа",
//            "Сентября", "Октября", "Ноября", "Декабря"};
//    public static final String[] MONTH_NAMES = {"Январь", "Февраль", "Март", "Апрель",
//            "Май", "Июнь", "Июль", "Август",
//            "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
//    public static final String[] SHORT_MONTH_NAMES = { "Янв.", "Фев.", "Мар.", "Апр.", "Май", "Июн.",
//            "Июл.", "Авг.", "Сен.", "Окт.", "Нояб.", "Дек." };
//    public static final String[] DAY_NAMES = {"", "Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"};


    public static String[] DECLENSION_MONTH_NAMES;
    public static String[] MONTH_NAMES;
    public static String[] SHORT_MONTH_NAMES;
    public static String[] DAY_NAMES;



    public static final String SEPARATOR_VALUE = "$";
    public static final String SEPARATOR_DATE = "#";

    public static final String EXPENSE_DATA_UNIT_LABEL = "expense_data_unit";
    public static final String EDIT_DIALOG_TAG = "edit_dialog_tag";
    public static final String CHOOSE_STATISTIC_PERIOD_DIALOG_TAG = "choose_period_dialog_tag";
    public static final String EXPENSES_LIST_DIALOG_TAG = "expenses_list_dialog_tag";
    public static final String PREVIOUS_ACTIVITY_INDEX = "previous_activity_index";

    public static final String PREVIOUS_ACTIVITY = "previous_activity";
    public static final String TARGET_TAB = "target_tab";

    public static final int FRAGMENT_CURRENT_MONTH_SCREEN = 100;
    public static final int FRAGMENT_LAST_ENTERED_VALUES_SCREEN = 101;
    public static final int STATISTIC_DETAILED_ACTIVITY = 102;
    public static final int FRAGMENT_STATISTIC_MAIN_SCREEN = 103;
    public static final int EDIT_DATA_ACTIVITY = 104;
    public static final int STATISTIC_COST_TYPE_DETAILED_ACTIVITY = 105;
    public static final int STATISTIC_CHOSE_PERIOD_ACTIVITY = 106;
    public static final int STATISTIC_CHOSEN_PERIOD_ACTIVITY = 107;

    public static final String SAVED_VALUE = "savedvalue";

    public static final int EDIT_EXPENSE_RECORD_DIALOG_REQUEST_CODE = 777;
    public static final int DELETE_ITEM = 11;
    public static final int EDIT_ITEM = 12;

    public static final String ACTIVITY_INPUT_DATA_MODE = "ai_mode";
    public static final int EDIT_MODE = 1111;
    public static final int INPUT_MODE = 1112;

    public static final int EDIT_EXPENSE_NAME_REQUEST_CODE = 778;

    public static final int CHOOSE_STATISTIC_PERIOD_REQUEST_CODE = 555;
    public static final int CHOOSE_STATISTIC_PERIOD_RESULT_CODE = 556;
    public static final String STARTING_DATE_LABEL = "starting_date_label";
    public static final String ENDING_DATE_LABEL = "ending_date_label";

    public static final String STATISTIC_DETAILED_ACTIVITY_MODE = "sda_mode";
    public static final int STATISTIC_DETAILED_ACTIVITY_MODE_BY_MONTHS = 2111;
    public static final int STATISTIC_DETAILED_ACTIVITY_MODE_CUSTOM_DATE = 2112;
    public static final String DATA_FOR_STATISTIC_DETAILED_ACTIVITY = "for_statistic_detailed_activity";
    public static final String DATA_FOR_STATISTIC_COST_TYPE_DETAILED_ACTIVITY = "for_cost_type_detailed_activity";

    public static final String BACKUP_FOLDER_NAME_DELIMITER = "@#@";


    public static boolean mainActivityFragmentsDataIsActual = false;
    private static boolean currentMonthFragmentDataIsLoaded = false;
    private static boolean lastEnteredValuesFragmentDataIsLoaded = false;
    private static boolean statisticMainScreenFragmentDataIsLoaded = false;

    public static void currentMonthFragmentDataIsActual(boolean isActual) {
        currentMonthFragmentDataIsLoaded = isActual;
        setMainActivityFragmentsDataIsActual();
    }
    public static void lastEnteredValuesFragmentDataIsActual(boolean isActual) {
        lastEnteredValuesFragmentDataIsLoaded = isActual;
        setMainActivityFragmentsDataIsActual();
    }
    public static void statisticMainScreenFragmentDataIsActual(boolean isActual) {
        statisticMainScreenFragmentDataIsLoaded = isActual;
        setMainActivityFragmentsDataIsActual();
    }

    private static void setMainActivityFragmentsDataIsActual() {
        if (currentMonthFragmentDataIsLoaded && lastEnteredValuesFragmentDataIsLoaded && statisticMainScreenFragmentDataIsLoaded)
            mainActivityFragmentsDataIsActual = true;
        else
            mainActivityFragmentsDataIsActual = false;
    }


    public static String formatDigit(double d) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
        DecimalFormat format = (DecimalFormat) numberFormat;
        format.setGroupingUsed(false);
        format.applyPattern("#.##");
        format.setRoundingMode(RoundingMode.CEILING);

        return format.format(d);
    }


    public static boolean stringsAreNull() {
        return DECLENSION_MONTH_NAMES == null
                || MONTH_NAMES == null
                || SHORT_MONTH_NAMES == null
                || DAY_NAMES == null;
    }

    public static void loadStrings(Context context) {
        DECLENSION_MONTH_NAMES = context.getResources().getStringArray(R.array.DECLENSION_MONTH_NAMES);
        MONTH_NAMES = context.getResources().getStringArray(R.array.MONTH_NAMES);
        SHORT_MONTH_NAMES = context.getResources().getStringArray(R.array.SHORT_MONTH_NAMES);
        DAY_NAMES = context.getResources().getStringArray(R.array.DAY_NAMES);

        System.out.println("STRINGS_LOADED");
    }
}
