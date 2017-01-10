package com.example.newcosts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class FragmentStatisticMainScreen extends Fragment {

    private Context context;
    private ListView periodsListView;
    private Button chooseStatisticPeriodButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View statisticMainScreenView = inflater.inflate(R.layout.activity_statistic_main_screen, container, false);
        chooseStatisticPeriodButton = (Button) statisticMainScreenView.findViewById(R.id.chosePeriodButton);
        chooseStatisticPeriodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChoseStatisticPeriodButtonClick(v);
            }
        });

        periodsListView = (ListView) statisticMainScreenView.findViewById(R.id.monthlyCostsListViewStatisticActivity);

        return statisticMainScreenView;
    }

    @Override
    public void onResume() {
        super.onResume();

        CostsDB cdb = CostsDB.getInstance(context);

        // Получаем суммарные значения за месяц и год в формате: "[1]месяц [2]год [3]сумма"
        String[] periodsArrayRaw = cdb.getSumByMonthsEntries_V2();
        for (String s : periodsArrayRaw) {
            System.out.println(s);
            System.out.println();
        }
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
        ListAdapter periodsListViewAdapter = new NewCostsListViewAdapter(context, periodsArray);
        periodsListView.setAdapter(periodsListViewAdapter);

        // При нажатии на выбранный период переходим на экран с
        // детальной  информацией о расходоах за выбранный период
        periodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent statisticDetailedActivityIntent = new Intent(context, StatisticDetailedActivity.class);
                String textLine = String.valueOf(parent.getItemAtPosition(position));
                statisticDetailedActivityIntent.putExtra("data", textLine);
                startActivity(statisticDetailedActivityIntent);
            }
        });
    }

    // Переход на экран ручного задания периода просмотра статистики расходов
    public void onChoseStatisticPeriodButtonClick(View view) {
        Intent statisticChosePeriodActivityIntent = new Intent(context, StatisticChosePeriodActivity.class);
        startActivity(statisticChosePeriodActivityIntent);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }
}
