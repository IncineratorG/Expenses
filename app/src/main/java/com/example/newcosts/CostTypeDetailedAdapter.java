package com.example.newcosts;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CostTypeDetailedAdapter extends ArrayAdapter<String> {

    public CostTypeDetailedAdapter(Context context, String[] costs) {
        super(context, R.layout.single_costs_row, costs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View singleCostsRowDetailed = inflater.inflate(R.layout.single_costs_row_detailed, parent, false);

        String textLine = getItem(position);

        String costNote = textLine.substring(0, textLine.lastIndexOf(Constants.SEPARATOR_NOTE));
        String costDate = textLine.substring(textLine.lastIndexOf(Constants.SEPARATOR_NOTE) + 1,
                                             textLine.lastIndexOf(Constants.SEPARATOR_VALUE));
        String costValue = textLine.substring(textLine.lastIndexOf(Constants.SEPARATOR_VALUE) + 1,
                                              textLine.lastIndexOf(Constants.SEPARATOR_MILLISECONDS));

        costValue = costValue + " руб.";

        TextView costDateTextView = (TextView) singleCostsRowDetailed.findViewById(R.id.singleCostRowDetailed_cost_date);
        TextView costValueTextView = (TextView) singleCostsRowDetailed.findViewById(R.id.singleCostRowDetailed_cost_value);
        TextView costNoteTextView = (TextView) singleCostsRowDetailed.findViewById(R.id.singleCostRowDetailed_cost_note);

        costDateTextView.setText(costDate);
        costValueTextView.setText(costValue);
        if (!"null".equals(costNote))
            costNoteTextView.setText(costNote);
        else
            costNoteTextView.setText("");

        return singleCostsRowDetailed;
    }

}
