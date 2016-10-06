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

import java.text.NumberFormat;
import java.util.ArrayList;
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

        CostsDB cdb = new CostsDB(this, null, null, 1);

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(false);

        // Получаем суммарные значения за месяц и год в формате: "5$1989$575"
        String[] periodsArrayRaw = cdb.getSumByMonthsEntries();
        List<String> listOfPeriods = new ArrayList<>();

        // Приводим полученные значения к виду: "Май 1989$575"
        for (String entry : periodsArrayRaw) {
            String[] arrayOfEntries = entry.split("\\$");
            int month = Integer.parseInt(arrayOfEntries[0]);
            String periodString = month + "$" + monthNames[month] + " " + arrayOfEntries[1] + "$" + numberFormat.format(
                                                                                                           Double.parseDouble(arrayOfEntries[2])
                                                                                                                                                );
            listOfPeriods.add(periodString);
        }
        String[] periodsArray = new String[listOfPeriods.size()];
        listOfPeriods.toArray(periodsArray);

        // Инициализируем listView
        ListView periodsListView = (ListView) findViewById(R.id.monthlyCostsListViewStatisticActivity);
        ListAdapter periodsListViewAdapter = new NewCostsListViewAdapter(this, periodsArray);
        periodsListView.setAdapter(periodsListViewAdapter);

        // При нажатии на выбранный период переходим на экран с
        // детальной  информацией о расходоах за выбранный период
        periodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent statisticDetailedActivityIntent = new Intent(StatisticMainScreenActivity.this, StatisticDetailedActivity.class);
                String textLine = String.valueOf(parent.getItemAtPosition(position));

                statisticDetailedActivityIntent.putExtra("data", textLine);

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
