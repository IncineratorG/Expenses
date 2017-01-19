package com.example.newcosts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ActivityStatisticExpenseTypeDetailed extends AppCompatActivity {

//    private int PREVIOUS_ACTIVITY = -1;
    private int STATISTIC_DETAILED_ACTIVITY_MODE = -1;
    private ExpensesDataUnit statisticDetailedActivityDataUnit;
    private ExpensesDataUnit startingDateDataUnit;
    private ExpensesDataUnit endingDateDataUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_expense_type_detailed);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_statistic_cost_type_detailed_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // При нажатии стрелки назад - возвращаемся к предыдущему экрану
        ImageView arrowBackImageView = (ImageView) findViewById(R.id.activity_statistic_cost_type_detailed_arrow_back);
        arrowBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToPreviousActivity();
            }
        });

        TextView toolBarTextView = (TextView) toolbar.findViewById(R.id.activity_statistic_cost_type_detailed_toolbar_textview);
        TextView expenseNameTextView = (TextView) findViewById(R.id.activity_statistic_cost_type_detailed_expense_name_textview);
        TextView expenseValueTextView = (TextView) findViewById(R.id.activity_statistic_cost_type_detailed_overall_value_textview);
        TextView perDayExpensesTextView = (TextView) findViewById(R.id.activity_statistic_cost_type_detailed_per_day_textview);

        CostsDB cdb = CostsDB.getInstance(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_statistic_cost_type_detailed_recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);

        Bundle expenseDataBundle = getIntent().getExtras();
        if (expenseDataBundle == null)
            return;

        STATISTIC_DETAILED_ACTIVITY_MODE = expenseDataBundle.getInt(Constants.STATISTIC_DETAILED_ACTIVITY_MODE);
        ExpensesDataUnit chosenExpenseTypeDataUnit = expenseDataBundle.getParcelable(Constants.DATA_FOR_STATISTIC_COST_TYPE_DETAILED_ACTIVITY);

        // Отображаем название выбранной категории
        expenseNameTextView.setText(chosenExpenseTypeDataUnit.getExpenseName());

        // Отображаем суммарные расходоы по выбранной категории за выбранный период
        expenseValueTextView.setText(chosenExpenseTypeDataUnit.getExpenseValueString() + " руб.");

        switch (STATISTIC_DETAILED_ACTIVITY_MODE) {
            case Constants.STATISTIC_DETAILED_ACTIVITY_MODE_BY_MONTHS: {
                statisticDetailedActivityDataUnit = expenseDataBundle.getParcelable(Constants.DATA_FOR_STATISTIC_DETAILED_ACTIVITY);
                if (chosenExpenseTypeDataUnit == null)
                    return;

                // Определяем количество дней в выбранном месяце. Если выбран
                // текущий месяц - оиспользуем количество дней, прошедших с начала месяца
                Calendar calendar = new GregorianCalendar();
                int daysInMonth = calendar.get(Calendar.DAY_OF_MONTH);
                if (chosenExpenseTypeDataUnit.getMonth() != (int) calendar.get(Calendar.MONTH) ||
                        chosenExpenseTypeDataUnit.getYear() != (int) calendar.get(Calendar.YEAR))
                {
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    calendar.set(Calendar.YEAR, chosenExpenseTypeDataUnit.getYear());
                    calendar.set(Calendar.MONTH, chosenExpenseTypeDataUnit.getMonth());
                    daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                }

                // Устанавливаем средний расход в день
                double averageExpensesPerDay = chosenExpenseTypeDataUnit.getExpenseValueDouble() / daysInMonth;
                perDayExpensesTextView.setText(Constants.formatDigit(averageExpensesPerDay) + " руб./день");

                // Отображаем выбранный период просмотра
                toolBarTextView.setText(Constants.MONTH_NAMES[chosenExpenseTypeDataUnit.getMonth()] + " " +
                        chosenExpenseTypeDataUnit.getYear());

                // Получаем список всех расходов по выбранной катнгории за выбранной период
                List<ExpensesDataUnit> dataUnitList = cdb.getCostValuesArrayOnDateAndCostName_V2(chosenExpenseTypeDataUnit.getMonth(),
                        chosenExpenseTypeDataUnit.getYear(),
                        chosenExpenseTypeDataUnit.getExpenseId_N(),
                        chosenExpenseTypeDataUnit.getExpenseName());

                AdapterStatisticExpenseTypeDetailedRecyclerView expenseTypeDetailedAdapter = new AdapterStatisticExpenseTypeDetailedRecyclerView(dataUnitList, this);
                recyclerView.setAdapter(expenseTypeDetailedAdapter);
            }
            break;

            case Constants.STATISTIC_DETAILED_ACTIVITY_MODE_CUSTOM_DATE: {
                startingDateDataUnit = expenseDataBundle.getParcelable(Constants.STARTING_DATE_LABEL);
                endingDateDataUnit = expenseDataBundle.getParcelable(Constants.ENDING_DATE_LABEL);
                if (startingDateDataUnit == null || endingDateDataUnit == null || chosenExpenseTypeDataUnit == null)
                    return;

                // Подсчитывем количество дней в выбранном периоде
                long chosenAmountOfDays = TimeUnit.DAYS.convert(endingDateDataUnit.getMilliseconds() - startingDateDataUnit.getMilliseconds(), TimeUnit.MILLISECONDS);
                if (chosenAmountOfDays == 0)
                    chosenAmountOfDays = 1;

                // Устанавливаем средний расход в день
                double averageExpensesPerDay = chosenExpenseTypeDataUnit.getExpenseValueDouble() / chosenAmountOfDays;
                perDayExpensesTextView.setText(Constants.formatDigit(averageExpensesPerDay) + " руб./день");

                // Отображаем выбранный период просмотра
                toolBarTextView.setText(new StringBuilder()
                        .append(startingDateDataUnit.getDay())
                        .append(" ")
                        .append(Constants.DECLENSION_MONTH_NAMES[startingDateDataUnit.getMonth()])
                        .append(" ")
                        .append(startingDateDataUnit.getYear())
                        .append(" -\n")
                        .append(endingDateDataUnit.getDay())
                        .append(" ")
                        .append(Constants.DECLENSION_MONTH_NAMES[endingDateDataUnit.getMonth()])
                        .append(" ")
                        .append(endingDateDataUnit.getYear())
                        .toString());

                List<ExpensesDataUnit> dataUnitList = cdb.getCostsBetweenDatesByName_V2(startingDateDataUnit.getMilliseconds(),
                                                    endingDateDataUnit.getMilliseconds(),
                                                    chosenExpenseTypeDataUnit.getExpenseName(),
                                                    chosenExpenseTypeDataUnit.getExpenseId_N());

                AdapterStatisticExpenseTypeDetailedRecyclerView expenseTypeDetailedAdapter = new AdapterStatisticExpenseTypeDetailedRecyclerView(dataUnitList, this);
                recyclerView.setAdapter(expenseTypeDetailedAdapter);
            }
            break;
        }
    }

    // Возвращаемся к предыдущему экрану
    private void returnToPreviousActivity() {
        Intent previousActivity = null;
        switch (STATISTIC_DETAILED_ACTIVITY_MODE) {
            case Constants.STATISTIC_DETAILED_ACTIVITY_MODE_BY_MONTHS:
                previousActivity = new Intent(this, ActivityStatisticDetailed.class);
                previousActivity.putExtra(Constants.STATISTIC_DETAILED_ACTIVITY_MODE, STATISTIC_DETAILED_ACTIVITY_MODE);
                previousActivity.putExtra(Constants.DATA_FOR_STATISTIC_DETAILED_ACTIVITY, statisticDetailedActivityDataUnit);
                previousActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case Constants.STATISTIC_DETAILED_ACTIVITY_MODE_CUSTOM_DATE:
                previousActivity = new Intent(this, ActivityStatisticDetailed.class);
                previousActivity.putExtra(Constants.STATISTIC_DETAILED_ACTIVITY_MODE, STATISTIC_DETAILED_ACTIVITY_MODE);
                previousActivity.putExtra(Constants.STARTING_DATE_LABEL, startingDateDataUnit);
                previousActivity.putExtra(Constants.ENDING_DATE_LABEL, endingDateDataUnit);
                previousActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
        }

        startActivity(previousActivity);
    }
}
