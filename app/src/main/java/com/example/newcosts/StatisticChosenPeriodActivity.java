package com.example.newcosts;

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

        CostsDB cdb = new CostsDB(this, null, null, 1);
        String[] dataArray = cdb.test(initialDateInMilliseconds, endingDateInMilliseconds);
        double totalForPeriod = 0.0;
        for (String s : dataArray) {
            double d = Double.parseDouble(s.substring(s.indexOf("$") + 1));
            totalForPeriod = totalForPeriod + d;
        }

        overallCostsForChosenPeriodTextView.setText(String.valueOf(totalForPeriod) + " руб.");

        ListAdapter listViewAdapter = new CostsListViewAdapter(this, dataArray);
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
