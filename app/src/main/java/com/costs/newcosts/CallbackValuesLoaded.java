package com.costs.newcosts;

import java.util.List;

/**
 * TODO: Add a class header comment
 */

public interface CallbackValuesLoaded {
    void valuesLoaded(int callingFragmentCode, List<DataUnitExpenses> data, double overallValue);
}
