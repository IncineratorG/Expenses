package com.example.newcosts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Alexander on 23.07.2016.
 */
public class NewCostsListViewAdapter extends ArrayAdapter<String> {
    public NewCostsListViewAdapter(Context context, String[] costs) {
        super(context, R.layout.single_costs_row, costs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View singleCostsRow = inflater.inflate(R.layout.single_costs_row, parent, false);

        String textLine = getItem(position);
        String[] textLineData = textLine.split("\\$");

        String costType = textLineData[1];
        String costValue = textLineData[2];

        if (!"+".equals(costValue))
            costValue = costValue + " руб.";

        TextView costTypeText = (TextView) singleCostsRow.findViewById(R.id.costType);
        TextView costValueText = (TextView) singleCostsRow.findViewById(R.id.costValue);

        costTypeText.setText(costType);
        costValueText.setText(costValue);

        return singleCostsRow;
    }
}
