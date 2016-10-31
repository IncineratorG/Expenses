package com.example.newcosts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class MainAdapter extends ArrayAdapter<String> {
    public MainAdapter(Context context, String[] costs) {
        super(context, R.layout.single_costs_row, costs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View singleCostsRow = inflater.inflate(R.layout.single_costs_row, parent, false);

        String dataLine = getItem(position);
        String costName = dataLine.substring(0, dataLine.lastIndexOf(Constants.SEPARATOR_VALUE));
        String costValue = dataLine.substring(dataLine.lastIndexOf(Constants.SEPARATOR_VALUE) + 1,
                dataLine.lastIndexOf(Constants.SEPARATOR_ID));
        String costId = dataLine.substring(dataLine.lastIndexOf(Constants.SEPARATOR_ID) + 1);

        TextView costTypeTextView = (TextView) singleCostsRow.findViewById(R.id.costType);
        TextView costValueTextView = (TextView) singleCostsRow.findViewById(R.id.costValue);


        if (!"+".equals(costId)) {
            costValue = costValue + " руб.";
            costValueTextView.setText(costValue);
        } else {
            costValueTextView.setText("+");
        }
        costTypeTextView.setText(costName);

        return singleCostsRow;
    }
}
