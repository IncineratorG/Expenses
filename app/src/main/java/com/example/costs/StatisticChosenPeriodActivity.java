package com.example.costs;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class StatisticChosenPeriodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_chosen_period);

        Bundle chosenPeriodBundle = getIntent().getExtras();

        long initialDateInMilliseconds = chosenPeriodBundle.getLong("initialDateInMilliseconds");
        long endingDateInMilliseconds = chosenPeriodBundle.getLong("endingDateInMilliseconds");

        String initialDateString = chosenPeriodBundle.getString("initialDateString");
        String endingDateString = chosenPeriodBundle.getString("endingDateString");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(initialDateString + " - " + endingDateString);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView chosenPeriodTextView = (TextView) findViewById(R.id.chosenPeriodTextView);
        chosenPeriodTextView.setText(initialDateString + " - " + endingDateString);
        TextView overallCostsForChosenPeriodTextView = (TextView) findViewById(R.id.overallCostsForChosenPeriodTextView);
        ListView chosenPeriodListView = (ListView) findViewById(R.id.chosenPeriodListView);

        CostsDataBase cdb = new CostsDataBase(this, null, null, 1);

        List<String> costNames = cdb.getCostNames();

        String[] costNamesAndValues = new String[costNames.size()];
        double totalCostForChosenPeriod = 0.0;
        for (int i = 0; i < costNamesAndValues.length; ++i) {
            double sum = cdb.getTotalCostsForSpecifiedCostTypeAndSpecifiedPeriodInMilliseconds(initialDateInMilliseconds, endingDateInMilliseconds, costNames.get(i));
            costNamesAndValues[i] = costNames.get(i) + "$" + sum;
            totalCostForChosenPeriod = totalCostForChosenPeriod + sum;
        }

        overallCostsForChosenPeriodTextView.setText(String.valueOf(totalCostForChosenPeriod) + " руб.");

        ListAdapter listViewAdapter = new CostsListViewAdapter(this, costNamesAndValues);
        chosenPeriodListView.setAdapter(listViewAdapter);

        chosenPeriodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent statisticCostTypeDetailedIntent = new Intent(StatisticChosenPeriodActivity.this, StatisticCostTypeDetailedActivity.class);
//
//                String textLine = String.valueOf(parent.getItemAtPosition(position));
//                String costName = textLine.substring(0, textLine.indexOf("$"));
//                String costValue = textLine.substring(textLine.indexOf("$") + 1);
//
//                statisticCostTypeDetailedIntent.putExtra("chosenDate", chosenDateStringFinal);
//                statisticCostTypeDetailedIntent.putExtra("overallCosts", overallCostsStringFinal);
//                statisticCostTypeDetailedIntent.putExtra("costName", costName);
//                statisticCostTypeDetailedIntent.putExtra("costValue", costValue);
//                statisticCostTypeDetailedIntent.putExtra("chosenMonth", chosenMonth);
//                statisticCostTypeDetailedIntent.putExtra("chosenYear", chosenYear);
//
//                startActivity(statisticCostTypeDetailedIntent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, StatisticChosePeriodActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
