package com.example.costs;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LastEntriesAdapter extends ArrayAdapter<String> {

    public LastEntriesAdapter(Context context, String[] costs) {
        super(context, R.layout.single_costs_row, costs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View singleCostsRow = inflater.inflate(R.layout.single_day_entry_row, parent, false);

        String textLine = getItem(position);
        String costDate = textLine.substring(0, textLine.indexOf("$"));
        String costValue = textLine.substring(textLine.indexOf("$") + 1);

        String day = costDate.substring(0, costDate.indexOf(" "));
        String monthName = MainActivity.declensionMonthNames[Integer.valueOf(costDate.substring(costDate.indexOf(" ") + 1, costDate.lastIndexOf(" ")))];
        String year = costDate.substring(costDate.lastIndexOf(" ") + 1);


        if (!costValue.equals("+"))
            costValue = costValue + " руб.";

        TextView costDateText = (TextView) singleCostsRow.findViewById(R.id.date);
        TextView costValueText = (TextView) singleCostsRow.findViewById(R.id.value);

        costDateText.setText(day + " " + monthName + "\n" + year);
        costValueText.setText(costValue);

        return singleCostsRow;
    }

}
