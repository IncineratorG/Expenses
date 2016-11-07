package com.example.newcosts;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class StatisticChosenPeriodActivity extends AppCompatActivity {

    private String initialDateString;
    private String endingDateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_chosen_period);

        Bundle chosenPeriodBundle = getIntent().getExtras();
        if (chosenPeriodBundle == null)
            return;

        final String[] bundleDataArray = chosenPeriodBundle.getStringArray(Constants.DATA_ARRAY_LABEL);
        if (bundleDataArray == null || bundleDataArray.length < 4)
            return;

        final long initialDateInMilliseconds = Long.parseLong(bundleDataArray[Constants.INITIAL_DATE_IN_MILLISECONDS_INDEX]);
        final long endingDateInMilliseconds = Long.parseLong(bundleDataArray[Constants.ENDING_DATE_IN_MILLISECONDS_INDEX]);
        initialDateString = bundleDataArray[Constants.INITIAL_DATE_STRING_INDEX];
        endingDateString = bundleDataArray[Constants.ENDING_DATE_STRING_INDEX];

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(initialDateString + " - " + endingDateString);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Constants.HEADER_SYSTEM_COLOR));

        TextView chosenPeriodTextView = (TextView) findViewById(R.id.chosenPeriodTextView);
        chosenPeriodTextView.setText(initialDateString + " - " + endingDateString);
        TextView overallCostsForChosenPeriodTextView = (TextView) findViewById(R.id.overallCostsForChosenPeriodTextView);
        ListView chosenPeriodListView = (ListView) findViewById(R.id.chosenPeriodListView);

        CostsDB cdb = CostsDB.getInstance(this);
        String[] costNamesAndSumsArray = cdb.getCostsBetweenDates_V2(initialDateInMilliseconds, endingDateInMilliseconds);

        double totalForPeriod = 0.0;
        for (String s : costNamesAndSumsArray) {
            double d = Double.parseDouble(s.substring(s.lastIndexOf(Constants.SEPARATOR_VALUE) + 1));
            totalForPeriod = totalForPeriod + d;
        }
        overallCostsForChosenPeriodTextView.setText(Constants.formatDigit(totalForPeriod) + " руб.");

        ListAdapter listViewAdapter = new ChosenPeriodActivityAdapter(this, costNamesAndSumsArray);
        chosenPeriodListView.setAdapter(listViewAdapter);

        chosenPeriodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String chosenItemString = String.valueOf(parent.getItemAtPosition(position));

                String costName = chosenItemString.substring(0, chosenItemString.lastIndexOf(Constants.SEPARATOR_DATE));
                String costValue = chosenItemString.substring(chosenItemString.lastIndexOf(Constants.SEPARATOR_VALUE) + 1);
                String costDate = chosenItemString.substring(chosenItemString.lastIndexOf(Constants.SEPARATOR_DATE) + 1,
                                                             chosenItemString.lastIndexOf(Constants.SEPARATOR_VALUE));
                int month = Integer.parseInt(costDate.substring(0, costDate.lastIndexOf(".")));
                int year = Integer.parseInt(costDate.substring(costDate.lastIndexOf(".") + 1));

                Intent statisticCostTypeDetailedIntent = new Intent(StatisticChosenPeriodActivity.this, StatisticCostTypeDetailedActivity.class);

                String[] dataArray = new String[8];
                for (int i = 0; i < 4; ++i)
                    dataArray[i] = bundleDataArray[i];

                dataArray[Constants.COST_NAME_INDEX] = costName;
                dataArray[Constants.COST_VALUE_INDEX] = costValue;
                dataArray[Constants.CHOSEN_MONTH_INDEX] = String.valueOf(month);
                dataArray[Constants.CHOSEN_YEAR_INDEX] = String.valueOf(year);

                statisticCostTypeDetailedIntent.putExtra(Constants.DATA_ARRAY_LABEL, dataArray);

                startActivity(statisticCostTypeDetailedIntent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, StatisticChosePeriodActivity.class);
                intent.putExtra("initialDatePrev", initialDateString);
                intent.putExtra("endingDatePrev", endingDateString);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
