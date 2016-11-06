package com.example.newcosts;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class StatisticCostTypeDetailedActivity extends AppCompatActivity {

    private static final String tag = "ctDetailedTag";

    private String dataForPreviousActivity = null;
    private String initialDateString;
    private String endingDateString;
    private long initialDateInMilliseconds;
    private long endingDateInMilliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_cost_type_detailed);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Constants.HEADER_SYSTEM_COLOR));

        Bundle dataFromPreviousActivity = getIntent().getExtras();
        if (dataFromPreviousActivity != null) {
            String costName = dataFromPreviousActivity.getString("costName");
            String costValue = dataFromPreviousActivity.getString("costValue");
            int chosenMonth = dataFromPreviousActivity.getInt("chosenMonth");
            int chosenYear = dataFromPreviousActivity.getInt("chosenYear");
            dataForPreviousActivity = dataFromPreviousActivity.getString("dataForPreviousActivity");
            initialDateString = dataFromPreviousActivity.getString("initialDateString");
            endingDateString = dataFromPreviousActivity.getString("endingDateString");
            initialDateInMilliseconds = dataFromPreviousActivity.getLong("initialDateInMilliseconds");
            endingDateInMilliseconds = dataFromPreviousActivity.getLong("endingDateInMilliseconds");

            CostsDB cdb = CostsDB.getInstance(this);
            String[] dataArray = cdb.getCostValuesArrayOnDateAndCostName(chosenMonth, chosenYear, costName);

            TextView dateTextView = (TextView) findViewById(R.id.dateTextViewInCostTypeDetailed);
            TextView costNameAndCostValueTextView = (TextView) findViewById(R.id.costNameAndCostValueTextViewInCostTypeDetailed);

            dateTextView.setText(Constants.MONTH_NAMES[chosenMonth] + " " + chosenYear);
            costNameAndCostValueTextView.setText(costName + ": " + costValue + " руб.");
            actionBar.setTitle(Constants.MONTH_NAMES[chosenMonth] + " " + chosenYear + ": " + costName);

            ListAdapter costsListAdapter = new CostTypeDetailedAdapter(this, dataArray);
            ListView detailedCostsListView = (ListView) findViewById(R.id.listViewInCostTypeDetailed);
            detailedCostsListView.setAdapter(costsListAdapter);
            detailedCostsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent editCostsIntent = new Intent(StatisticCostTypeDetailedActivity.this, EditCostsActivity.class);
                    editCostsIntent.putExtra("data", parent.getItemAtPosition(position).toString());
                    startActivity(editCostsIntent);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = null;
                if (dataForPreviousActivity == null) {
                    intent = new Intent(this, StatisticChosenPeriodActivity.class);
                    intent.putExtra("initialDateInMilliseconds", initialDateInMilliseconds);
                    intent.putExtra("endingDateInMilliseconds", endingDateInMilliseconds);
                    intent.putExtra("initialDateString", initialDateString);
                    intent.putExtra("endingDateString", endingDateString);
                } else {
                    intent = new Intent(this, StatisticDetailedActivity.class);
                    intent.putExtra("data", dataForPreviousActivity);
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
