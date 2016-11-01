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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatisticMainScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_main_screen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Просмотр статистики");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Constants.HEADER_SYSTEM_COLOR));

        CostsDB cdb = CostsDB.getInstance(this);

        // Получаем суммарные значения за месяц и год в формате: "[1]месяц [2]год [3]сумма"
        String[] periodsArrayRaw = cdb.getSumByMonthsEntries_V2();
        String[] periodsArray = new String[periodsArrayRaw.length / 3];
        int periodsArrayIndexCounter = 0;
        StringBuilder sb = new StringBuilder();

        // Приводим полученные значения к виду: "Май 1989$575"
        for (int i = 0; i < periodsArrayRaw.length; i = i + 3) {
            int month = Integer.parseInt(periodsArrayRaw[i]);
            sb.append(periodsArrayRaw[i]);
            sb.append(Constants.SEPARATOR_VALUE);
            sb.append(Constants.MONTH_NAMES[month]);
            sb.append(" ");
            sb.append(periodsArrayRaw[i + 1]);
            sb.append(Constants.SEPARATOR_VALUE);
            sb.append(Constants.formatDigit(Double.parseDouble(periodsArrayRaw[i + 2])));

            periodsArray[periodsArrayIndexCounter++] = sb.toString();
            sb.setLength(0);
        }

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

    // Переход на экран ручного задания периода просмотра статистики расходов
    public void onChoseStatisticPeriodButtonClick(View view) {
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
