package com.example.newcosts;

import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class MyDataSets {
    public static boolean dataIsActual = false;
    public static List<DataUnitExpenses> listOfActiveCostNames;
    public static double overallValueForCurrentMonth;
    public static List<DataUnitExpenses> listOfLastEntries;
    public static List<DataUnitExpenses> sumByMonthList;

    public static void setListOfActiveCostNames(List<DataUnitExpenses> activeCostNames, double overallValue) {
        listOfActiveCostNames = activeCostNames;
        overallValueForCurrentMonth = overallValue;
    }
}
