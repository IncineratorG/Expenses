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

    private String dataForPreviousActivity = null;
    private String[] bundleDataArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_cost_type_detailed);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Constants.HEADER_SYSTEM_COLOR));

        Bundle dataFromPreviousActivity = getIntent().getExtras();
        if (dataFromPreviousActivity != null) {
            bundleDataArray = dataFromPreviousActivity.getStringArray(Constants.DATA_ARRAY_LABEL);
            if (bundleDataArray == null || bundleDataArray.length < 8)
                return;

            String costName = bundleDataArray[Constants.COST_NAME_INDEX];
            String costValueTotalString = bundleDataArray[Constants.COST_VALUE_INDEX];
            int chosenMonth = Integer.parseInt(bundleDataArray[Constants.CHOSEN_MONTH_INDEX]);
            int chosenYear = Integer.parseInt(bundleDataArray[Constants.CHOSEN_YEAR_INDEX]);
            dataForPreviousActivity = dataFromPreviousActivity.getString("dataForPreviousActivity");

            String initialDateString;
            String endingDateString;
            long initialDateInMilliseconds;
            long endingDateInMilliseconds;
            String[] dataArray;
            CostsDB cdb = CostsDB.getInstance(this);
            double costValueTotal = 0.0;

            TextView dateTextView = (TextView) findViewById(R.id.dateTextViewInCostTypeDetailed);
            TextView costNameAndCostValueTextView = (TextView) findViewById(R.id.costNameAndCostValueTextViewInCostTypeDetailed);

            // Если пользователь просматривает расходы за какой-либо определённый месяц - отображаем
            // расходы по выбранной категории за этот месяц. Если пользователь вручную задаёт период просмтора -
            // отображаем расходы по выбранной категории за выбранный период
            if (dataForPreviousActivity == null) {
                initialDateString = bundleDataArray[Constants.INITIAL_DATE_STRING_INDEX];
                endingDateString = bundleDataArray[Constants.ENDING_DATE_STRING_INDEX];
                initialDateInMilliseconds = Long.parseLong(bundleDataArray[Constants.INITIAL_DATE_IN_MILLISECONDS_INDEX]);
                endingDateInMilliseconds = Long.parseLong(bundleDataArray[Constants.ENDING_DATE_IN_MILLISECONDS_INDEX]);

                dataArray = cdb.getCostsBetweenDatesByName(initialDateInMilliseconds, endingDateInMilliseconds, costName);

                dateTextView.setText(initialDateString + " - " + endingDateString);
                actionBar.setTitle(initialDateString + " - " + endingDateString);
            } else {
                dataArray = cdb.getCostValuesArrayOnDateAndCostName(chosenMonth, chosenYear, costName);

                dateTextView.setText(Constants.MONTH_NAMES[chosenMonth] + " " + chosenYear);
                actionBar.setTitle(Constants.MONTH_NAMES[chosenMonth] + " " + chosenYear + ": " + costName);
            }

            // Определяем суммарную величину расходов за заданный период по заданной категории
            for (String s : dataArray) {
                double costValue = Double.parseDouble(s.substring(s.lastIndexOf(Constants.SEPARATOR_VALUE) + 1,
                                                                  s.lastIndexOf(Constants.SEPARATOR_MILLISECONDS)));
                costValueTotal = costValueTotal + costValue;
            }
            costValueTotalString = String.valueOf(costValueTotal);
            costNameAndCostValueTextView.setText(costName + ": " + costValueTotalString + " руб.");
            bundleDataArray[Constants.COST_VALUE_INDEX] = costValueTotalString;

            ListAdapter costsListAdapter = new CostTypeDetailedAdapter(this, dataArray);
            ListView detailedCostsListView = (ListView) findViewById(R.id.listViewInCostTypeDetailed);
            detailedCostsListView.setAdapter(costsListAdapter);
            detailedCostsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent editCostsIntent = new Intent(StatisticCostTypeDetailedActivity.this, EditCostsActivity.class);
//                    editCostsIntent.putExtra(Constants.DATA_ARRAY_LABEL, bundleDataArray);
//                    editCostsIntent.putExtra("dataForPreviousActivity", dataForPreviousActivity);
//                    editCostsIntent.putExtra("data", parent.getItemAtPosition(position).toString());
//                    startActivity(editCostsIntent);
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

                    String[] dataArray = new String[4];
                    for (int i = 0; i < dataArray.length; ++i)
                        dataArray[i] = bundleDataArray[i];

                    intent.putExtra(Constants.DATA_ARRAY_LABEL, dataArray);
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
