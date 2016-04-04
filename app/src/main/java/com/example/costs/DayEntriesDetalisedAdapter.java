package com.example.costs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Саня on 04.04.2016.
 */
public class DayEntriesDetalisedAdapter extends ArrayAdapter<String> {

    public DayEntriesDetalisedAdapter(Context context, String[] resource) {
        super(context, R.layout.single_period_row, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View singleCostValueRow = inflater.inflate(R.layout.single_period_row, parent, false);

        TextView singleCostValueTextView = (TextView) singleCostValueRow.findViewById(R.id.periodText);

        String textLine = getItem(position);
        String costName = textLine.substring(0, textLine.indexOf(":"));
        String costValue = textLine.substring(textLine.indexOf(":") + 1, textLine.indexOf("."));

        //System.out.println(textLine);

        singleCostValueTextView.setText(costName + ": " + costValue + ".");

        return singleCostValueRow;
    }
}
