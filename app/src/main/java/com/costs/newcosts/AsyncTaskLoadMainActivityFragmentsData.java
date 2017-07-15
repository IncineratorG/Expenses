package com.costs.newcosts;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class AsyncTaskLoadMainActivityFragmentsData extends AsyncTask<Void, Void, Void> {


    private int callingFragmentCode = -1;
    private List<DataUnitExpenses> loadedDataList;
    private Context context;
    private DB_Costs cdb;
    private CallbackValuesLoaded valuesLoadedCallback;
    private double overallValueForCurrentMonth = 0.0;


    public AsyncTaskLoadMainActivityFragmentsData(DB_Costs cdb, int callingFragmentCode, CallbackValuesLoaded callback) {
        this.callingFragmentCode = callingFragmentCode;
        this.cdb = cdb;
        valuesLoadedCallback = callback;
    }


    @Override
    protected Void doInBackground(Void... params) {

        switch (callingFragmentCode) {
            case Constants.FRAGMENT_CURRENT_MONTH_SCREEN:
                // Получаем и устанавливаем текущую дату
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentYear = calendar.get(Calendar.YEAR);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                // Получаем массив активных категорий расходов, получаем сумму расходов по
                // каждой категории в текущем месяце и суммарное значение затрат за текущий месяц
                DataUnitExpenses[] activeExpenseNamesArray = cdb.getActiveCostNames_V3();
                loadedDataList = new ArrayList<>();
                overallValueForCurrentMonth = 0.0;
                for (int i = 0; i < activeExpenseNamesArray.length; ++i) {
                    double singleUnitExpenseValue = cdb.getCostValue(-1, currentMonth, currentYear, activeExpenseNamesArray[i].getExpenseId_N());
                    activeExpenseNamesArray[i].setExpenseValueDouble(singleUnitExpenseValue);
                    activeExpenseNamesArray[i].setExpenseValueString(Constants.formatDigit(singleUnitExpenseValue));
                    loadedDataList.add(activeExpenseNamesArray[i]);
                    overallValueForCurrentMonth = overallValueForCurrentMonth + singleUnitExpenseValue;
                }
                // Последним элементом списка явлеятся пункт "Добавить новую категорию"
                DataUnitExpenses addNewCategoryDataUnit = new DataUnitExpenses();
                addNewCategoryDataUnit.setExpenseId_N(Integer.MIN_VALUE);
                addNewCategoryDataUnit.setExpenseName("Добавить новую категорию");
                addNewCategoryDataUnit.setExpenseValueString("+");
                loadedDataList.add(addNewCategoryDataUnit);
                break;
            case Constants.FRAGMENT_LAST_ENTERED_VALUES_SCREEN:
                loadedDataList = cdb.getLastEntries_V3(100);
                break;
            case Constants.FRAGMENT_STATISTIC_MAIN_SCREEN:
                loadedDataList = cdb.getSumByMonthsList();
                break;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        valuesLoadedCallback.valuesLoaded(callingFragmentCode, loadedDataList, overallValueForCurrentMonth);
    }
}
