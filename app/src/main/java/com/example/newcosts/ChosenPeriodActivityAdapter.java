package com.example.newcosts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


class ChosenPeriodActivityAdapter extends ArrayAdapter<String> {

    public ChosenPeriodActivityAdapter(Context context, String[] costs) {
        super(context, R.layout.single_costs_row, costs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View singleCostsRow = inflater.inflate(R.layout.single_costs_row, parent, false);

        String textLine = getItem(position);

        String costType = textLine.substring(0, textLine.lastIndexOf(Constants.SEPARATOR_DATE));
        String costValue = textLine.substring(textLine.lastIndexOf(Constants.SEPARATOR_VALUE) + 1);
        costValue = costValue + " руб.";

        TextView costTypeText = (TextView) singleCostsRow.findViewById(R.id.costType);
        TextView costValueText = (TextView) singleCostsRow.findViewById(R.id.costValue);

        costTypeText.setText(costType);
        costValueText.setText(costValue);

        return singleCostsRow;
    }

}
