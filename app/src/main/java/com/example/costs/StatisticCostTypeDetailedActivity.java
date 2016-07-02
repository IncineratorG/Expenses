package com.example.costs;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class StatisticCostTypeDetailedActivity extends AppCompatActivity {

    String chosenDateString;
    String overallCostsString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_cost_type_detailed);

        Bundle statisticDetailedActivityBundle = getIntent().getExtras();
        chosenDateString = statisticDetailedActivityBundle.getString("chosenDate");
        overallCostsString = statisticDetailedActivityBundle.getString("overallCosts");

        int chosenMonth = statisticDetailedActivityBundle.getInt("chosenMonth");
        int chosenYear = statisticDetailedActivityBundle.getInt("chosenYear");
        String costName = statisticDetailedActivityBundle.getString("costName");
        String costValue = statisticDetailedActivityBundle.getString("costValue");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(costName + ": " + chosenDateString);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView dateTextView = (TextView) findViewById(R.id.dateTextViewInCostTypeDetailed);
        TextView costNameAndCostValueTextView = (TextView) findViewById(R.id.costNameAndCostValueTextViewInCostTypeDetailed);

        dateTextView.setText(chosenDateString);
        costNameAndCostValueTextView.setText(costName + ": " + costValue + " руб.");

        CostsDataBase cdb = new CostsDataBase(this, null, null, 1);

        String[] costsArray = cdb.getCostValuesOnSpecifiedDateAndCostName(chosenMonth, chosenYear, costName);

        ListAdapter costsListAdapter = new CostsListViewAdapter(this, costsArray);

        ListView detailedCostsListView = (ListView) findViewById(R.id.listViewInCostTypeDetailed);
        detailedCostsListView.setAdapter(costsListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, StatisticDetailedActivity.class);

                intent.putExtra("chosenDate", chosenDateString);
                intent.putExtra("overallCosts", overallCostsString);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
