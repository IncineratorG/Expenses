package com.example.newcosts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;


/**
 * TODO: Add a class header comment
 */

public class AdapterExpensesListDialog extends ArrayAdapter<ExpensesDataUnit> {
    public AdapterExpensesListDialog(Context context, ExpensesDataUnit[] dataUnits) {
        super(context, R.layout.expenses_names_list_single_item, dataUnits);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View singleExpenseRow = inflater.inflate(R.layout.expenses_names_list_single_item, parent, false);

        ExpensesDataUnit dataUnit = getItem(position);

        TextView checkedTextView = (TextView) singleExpenseRow.findViewById(R.id.expenses_names_list_textview);
        checkedTextView.setText(dataUnit.getExpenseName());

        return singleExpenseRow;
    }
}
