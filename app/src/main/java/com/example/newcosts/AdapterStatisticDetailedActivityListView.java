package com.example.newcosts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


class AdapterStatisticDetailedActivityListView extends ArrayAdapter<ExpensesDataUnit> {

    public AdapterStatisticDetailedActivityListView(Context context, List<ExpensesDataUnit> dataUnitList) {
        super(context, R.layout.activity_statistic_detailed_single_item, dataUnitList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View singleCostsRow = inflater.inflate(R.layout.activity_statistic_detailed_single_item, parent, false);

        ExpensesDataUnit dataUnit = getItem(position);

        TextView expenseNameTextView = (TextView) singleCostsRow.findViewById(R.id.activity_statistic_detailed_single_item_expense_name_textview);
        TextView expenseValueTextView = (TextView) singleCostsRow.findViewById(R.id.activity_statistic_detailed_single_item_expense_value_textview);

        expenseNameTextView.setText(dataUnit.getExpenseName());
        expenseValueTextView.setText(dataUnit.getExpenseValueString() + " руб.");

        return singleCostsRow;
    }

}
