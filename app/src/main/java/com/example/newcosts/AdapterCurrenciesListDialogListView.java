package com.example.newcosts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * TODO: Add a class header comment
 */

public class AdapterCurrenciesListDialogListView extends ArrayAdapter<String> {
    public AdapterCurrenciesListDialogListView(Context context, String[] currenciesArray) {
        super(context, R.layout.currencies_list_dialog_single_item, currenciesArray);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View singleCurrencyRow = inflater.inflate(R.layout.currencies_list_dialog_single_item, parent, false);
        String currencyString = getItem(position);

        TextView currencyTextView = (TextView) singleCurrencyRow.findViewById(R.id.currencies_list_dialog_single_item_textView);
        currencyTextView.setText(currencyString);

        return singleCurrencyRow;
    }
}
