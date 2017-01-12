package com.example.newcosts;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;


public class FragmentStatisticMainScreen extends Fragment {

    private Context context;
//    private ListView periodsListView;
    private RecyclerView recyclerView;
    private Button chooseStatisticPeriodButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View statisticMainScreenView = inflater.inflate(R.layout.fragment_statistic_main_screen, container, false);
        chooseStatisticPeriodButton = (Button) statisticMainScreenView.findViewById(R.id.chosePeriodButton);
        chooseStatisticPeriodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChoseStatisticPeriodButtonClick(v);
            }
        });
        recyclerView = (RecyclerView) statisticMainScreenView.findViewById(R.id.statistic_main_screen_recycler_view);

        return statisticMainScreenView;
    }

    @Override
    public void onResume() {
        super.onResume();

        CostsDB cdb = CostsDB.getInstance(context);

        // Получаем суммарные значения за месяц и год
        final List<ExpensesDataUnit> sumByMonthList = cdb.getSumByMonthsList();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        AdapterStatisticMainScreenRecyclerView statisticMainScreenRecyclerViewAdapter = new AdapterStatisticMainScreenRecyclerView(sumByMonthList, context);
        statisticMainScreenRecyclerViewAdapter.setClickListener(new AdapterLastEnteredValuesRecyclerView_V2.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent statisticDetailedActivityIntent = new Intent(context, StatisticDetailedActivity.class);
                statisticDetailedActivityIntent.putExtra(Constants.STATISTIC_DETAILED_ACTIVITY_MODE, Constants.STATISTIC_DETAILED_ACTIVITY_MODE_BY_MONTHS);
                statisticDetailedActivityIntent.putExtra(Constants.DATA_FOR_STATISTIC_DETAILED_ACTIVITY, sumByMonthList.get(position));
                startActivity(statisticDetailedActivityIntent);
            }
        });
        recyclerView.setAdapter(statisticMainScreenRecyclerViewAdapter);
    }

    // Вызов диалога ручного задания периода просмотра статистики расходов
    public void onChoseStatisticPeriodButtonClick(View view) {
        ChooseStatisticPeriodDialogFragment choosePeriodDialog = ChooseStatisticPeriodDialogFragment.newInstance(context);
        choosePeriodDialog.setTargetFragment(FragmentStatisticMainScreen.this, Constants.CHOOSE_STATISTIC_PERIOD_REQUEST_CODE);;
        choosePeriodDialog.show(getFragmentManager(), Constants.CHOOSE_STATISTIC_PERIOD_DIALOG_TAG);
    }


    // Обработка результата выбора даты периода просмотра статистики
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.CHOOSE_STATISTIC_PERIOD_REQUEST_CODE) {
            if (resultCode == Constants.CHOOSE_STATISTIC_PERIOD_RESULT_CODE) {
                ExpensesDataUnit startingDateDataUnit = null;
                ExpensesDataUnit endingDateDataUnit = null;

                if (data == null)
                    return;

                startingDateDataUnit = data.getExtras().getParcelable(Constants.STARTING_DATE_LABEL);
                endingDateDataUnit = data.getExtras().getParcelable(Constants.ENDING_DATE_LABEL);

                Intent statisticDetailedActivityIntent = new Intent(context, StatisticDetailedActivity.class);
                statisticDetailedActivityIntent.putExtra(Constants.STATISTIC_DETAILED_ACTIVITY_MODE, Constants.STATISTIC_DETAILED_ACTIVITY_MODE_CUSTOM_DATE);
                statisticDetailedActivityIntent.putExtra(Constants.STARTING_DATE_LABEL, startingDateDataUnit);
                statisticDetailedActivityIntent.putExtra(Constants.ENDING_DATE_LABEL, endingDateDataUnit);
                startActivity(statisticDetailedActivityIntent);
            }
        }
    }




    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }
}
