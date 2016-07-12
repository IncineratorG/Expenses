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

import java.util.Arrays;

public class StatisticDetailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_detailed);

        ActionBar actionBar = getSupportActionBar();

        String chosenDateString = "none";

        Bundle chosenDateBundle = getIntent().getExtras();
        if (chosenDateBundle != null) {
            chosenDateString = chosenDateBundle.getString("chosenDate");
        }
        final String chosenDateStringFinal = chosenDateString;
        actionBar.setTitle(chosenDateString);
        actionBar.setDisplayHomeAsUpEnabled(true);

        String overallCostsString = chosenDateBundle.getString("overallCosts");
        final String overallCostsStringFinal = overallCostsString;

        TextView chosenDateTextViewStatisticDetailedActivity = (TextView) findViewById(R.id.chosenDateStatisticDetailedActivity);
        TextView overallCostsTextViewStatisticDetailedActivity = (TextView) findViewById(R.id.overallCostsStatisticDetailedActivity);

        chosenDateTextViewStatisticDetailedActivity.setText(chosenDateString);
        overallCostsTextViewStatisticDetailedActivity.setText(overallCostsString + " руб.");

        final int chosenYear = Integer.parseInt(chosenDateString.substring(chosenDateString.indexOf(" ") + 1));

        String chosenMonthString = chosenDateString.substring(0, chosenDateString.indexOf(" "));
        final int chosenMonth = Arrays.asList(StatisticMainScreenActivity.monthNames).indexOf(chosenMonthString);

        CostsDataBase cdb = new CostsDataBase(this, null, null, 1);

//        List<String> costNamesList = cdb.getCostNames();
//        List<String> costsList = new ArrayList<>();
//        for (String costName : costNamesList)
//            costsList.add(costName + "$" + cdb.getCostValue(-1, chosenMonth, chosenYear, costName));

        String[] costsArray = cdb.getCostValuesOnSpecifiedDate(chosenMonth, chosenYear);

        ListAdapter costsListViewStatisticDetailedActivityAdapter = new CostsListViewAdapter(this, costsArray);

        ListView costsListViewStatisticDetailedActivity = (ListView) findViewById(R.id.costsListViewStatisticDetailedActivity);
        costsListViewStatisticDetailedActivity.setAdapter(costsListViewStatisticDetailedActivityAdapter);
        costsListViewStatisticDetailedActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent statisticCostTypeDetailedIntent = new Intent(StatisticDetailedActivity.this, StatisticCostTypeDetailedActivity.class);

                String textLine = String.valueOf(parent.getItemAtPosition(position));
                String costName = textLine.substring(0, textLine.indexOf("$"));
                String costValue = textLine.substring(textLine.indexOf("$") + 1);

                statisticCostTypeDetailedIntent.putExtra("chosenDate", chosenDateStringFinal);
                statisticCostTypeDetailedIntent.putExtra("overallCosts", overallCostsStringFinal);
                statisticCostTypeDetailedIntent.putExtra("costName", costName);
                statisticCostTypeDetailedIntent.putExtra("costValue", costValue);
                statisticCostTypeDetailedIntent.putExtra("chosenMonth", chosenMonth);
                statisticCostTypeDetailedIntent.putExtra("chosenYear", chosenYear);


                startActivity(statisticCostTypeDetailedIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, StatisticMainScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
