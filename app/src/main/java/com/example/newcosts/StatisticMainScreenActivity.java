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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticMainScreenActivity extends AppCompatActivity {

    static final String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_main_screen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Просмотр статистики");
        actionBar.setDisplayHomeAsUpEnabled(true);

        CostsDataBase cdb = new CostsDataBase(this, null, null, 1);

        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH);
        int currentYear = c.get(Calendar.YEAR);

        int oldestMonth = currentMonth;
        int oldestYear = currentYear;

        String oldestDateString = cdb.getOldestDate();
        if (oldestDateString != null && !oldestDateString.equals("") && !oldestDateString.equals("null$null")) {
            String oldestMonthString = oldestDateString.substring(0, oldestDateString.indexOf("$"));
            String oldestYearString = oldestDateString.substring(oldestDateString.indexOf("$") + 1);

            System.out.println(oldestMonthString);
            System.out.println(oldestYearString);

            oldestMonth = Integer.parseInt(oldestMonthString);
            oldestYear = Integer.parseInt(oldestYearString);
        }

        List<String> periodsList = new ArrayList<>();
        while (true) {
            double totalForSpecifiedMonth = cdb.getTotalCostValueOnSpecifiedMonth(oldestMonth, oldestYear);
            periodsList.add(monthNames[oldestMonth] + " " + oldestYear + "$" + totalForSpecifiedMonth);
            ++oldestMonth;

            if (oldestMonth == 12) {
                oldestMonth = 0;
                ++oldestYear;
            }
            if (oldestMonth > currentMonth && oldestYear == currentYear)
                break;
        }
        String[] periodsArray = new String[periodsList.size()];
        for (int i = 0; i < periodsList.size(); ++i)
            periodsArray[periodsArray.length - i - 1] = periodsList.get(i);

        ListView periodsListView = (ListView) findViewById(R.id.monthlyCostsListViewStatisticActivity);
        ListAdapter periodsListViewAdapter = new CostsListViewAdapter(this, periodsArray);
        periodsListView.setAdapter(periodsListViewAdapter);

        periodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent statisticDetailedActivityIntent = new Intent(StatisticMainScreenActivity.this, StatisticDetailedActivity.class);
                String textLine = String.valueOf(parent.getItemAtPosition(position));
                String chosenDate = textLine.substring(0, textLine.indexOf("$"));
                String overallCosts = textLine.substring(textLine.indexOf("$") + 1);

                statisticDetailedActivityIntent.putExtra("chosenDate", chosenDate);
                statisticDetailedActivityIntent.putExtra("overallCosts", overallCosts);

                startActivity(statisticDetailedActivityIntent);
            }
        });
    }

    public void OnChoseStatisticPeriodButtonClick(View view) {
        Intent statisticChosePeriodActivityIntent = new Intent(StatisticMainScreenActivity.this, StatisticChosePeriodActivity.class);
        startActivity(statisticChosePeriodActivityIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
