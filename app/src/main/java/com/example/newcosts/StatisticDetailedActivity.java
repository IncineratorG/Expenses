package com.example.newcosts;

import android.content.Intent;
import android.graphics.Color;
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

public class StatisticDetailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_detailed);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Constants.HEADER_SYSTEM_COLOR));

        // Получаем строку с информацией о выбранной дате и суммарных затратах за выбранный месяц
        String dataString = "none";
        Bundle dataFromStatisticMainScreenActivity = getIntent().getExtras();
        if (dataFromStatisticMainScreenActivity != null)
            dataString = dataFromStatisticMainScreenActivity.getString("data");

        if (dataString != null && !dataString.equals("none")) {
            // Вытаскиваем нужную нам информацию (месяц, год, суммарные затараты за этот месяц) из переданной строки
            String[] dataStringContent = dataString.split(" ");
            final int chosenMonthNumber = Integer.parseInt(dataStringContent[0].substring(0, dataStringContent[0].indexOf(Constants.SEPARATOR_VALUE)));
            final int chosenYear = Integer.parseInt(dataStringContent[1].substring(0, dataStringContent[1].indexOf(Constants.SEPARATOR_VALUE)));
            String overallCostValueForChosenPeriodString = dataStringContent[1].substring(dataStringContent[1].indexOf(Constants.SEPARATOR_VALUE) + 1);
            String actionBarTitleString = dataStringContent[0].substring(dataStringContent[0].indexOf(Constants.SEPARATOR_VALUE) + 1) +  " " + chosenYear;

            actionBar.setTitle(actionBarTitleString);

            TextView chosenDateTextViewStatisticDetailedActivity = (TextView) findViewById(R.id.chosenDateStatisticDetailedActivity);
            chosenDateTextViewStatisticDetailedActivity.setText(actionBarTitleString);

            TextView overallCostsTextViewStatisticDetailedActivity = (TextView) findViewById(R.id.overallCostsStatisticDetailedActivity);
            overallCostsTextViewStatisticDetailedActivity.setText(overallCostValueForChosenPeriodString + " руб.");

            // Получаем массив статей расходов и суммарные значения по ним за выбранный месяц
            CostsDB cdb = CostsDB.getInstance(this);
            String[] costsArray = cdb.getCostValuesArrayOnDate_V2(chosenMonthNumber, chosenYear);

            // Инициализируем ListView полученным массивом
            ListAdapter costsListViewStatisticDetailedActivityAdapter = new CostsListViewAdapter(this, costsArray);
            ListView costsListViewStatisticDetailedActivity = (ListView) findViewById(R.id.costsListViewStatisticDetailedActivity);
            costsListViewStatisticDetailedActivity.setAdapter(costsListViewStatisticDetailedActivityAdapter);

            // При нажатии на элемент списка статей расходов происходит переход на экран
            // детального просмотра затрат по выбранной статье за выбранный месяц
            final String finalDataString = dataString;
            costsListViewStatisticDetailedActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent statisticCostTypeDetailedIntent = new Intent(StatisticDetailedActivity.this, StatisticCostTypeDetailedActivity.class);

                    String textLine = String.valueOf(parent.getItemAtPosition(position));
                    String costName = textLine.substring(0, textLine.lastIndexOf(Constants.SEPARATOR_VALUE));
                    String costValue = textLine.substring(textLine.lastIndexOf(Constants.SEPARATOR_VALUE) + 1);

                    statisticCostTypeDetailedIntent.putExtra("costName", costName);
                    statisticCostTypeDetailedIntent.putExtra("costValue", costValue);
                    statisticCostTypeDetailedIntent.putExtra("chosenMonth", chosenMonthNumber);
                    statisticCostTypeDetailedIntent.putExtra("chosenYear", chosenYear);
                    statisticCostTypeDetailedIntent.putExtra("dataForStatisticDetailedActivity", finalDataString);

                    startActivity(statisticCostTypeDetailedIntent);
                }
            });
        }
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
