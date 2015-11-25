package com.example.costs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Саня on 25.11.2015.
 */
public class PeriodsAdapter extends ArrayAdapter<String> {

    static final String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    public PeriodsAdapter(Context context, String[] periods) {
        super(context, R.layout.single_period_row, periods);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View singlePeriodRow = inflater.inflate(R.layout.single_period_row, parent, false);

        String textLine = getItem(position);
        String monthText = monthNames[Integer.parseInt(textLine.substring(0, textLine.indexOf(" "))) - 1];
        String yearText = textLine.substring(textLine.indexOf(" ") + 1);
        String dateText = monthText + " " + yearText;

        TextView dateTextView = (TextView) singlePeriodRow.findViewById(R.id.periodText);
        dateTextView.setText(dateText);

        return singlePeriodRow;
    }
}
