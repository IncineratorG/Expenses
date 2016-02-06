package com.example.costs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class ChosenDayEntriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_day_entries);

        Bundle dataFromLastEnteredValuesActivity = getIntent().getExtras();
        if (dataFromLastEnteredValuesActivity == null)
            return;

        String chosenDayString = (String) dataFromLastEnteredValuesActivity.get("chosenDay");
        String chosenMonthString = (String) dataFromLastEnteredValuesActivity.get("chosenMonth");
        String chosenYearString = (String) dataFromLastEnteredValuesActivity.get("chosenYear");
        String costValueOnChosenDayString = (String) dataFromLastEnteredValuesActivity.get("costValueOnChosenDay");

        TextView chosenDateTextView = (TextView) findViewById(R.id.chosenDateTextView);
        chosenDateTextView.setText(chosenDayString + " " +
                                    MainActivity.declensionMonthNames[Integer.valueOf(chosenMonthString)] + " " +
                                    chosenYearString);

        TextView overallCostsValueInChosenDateTextView = (TextView) findViewById(R.id.overallCostsInChosenDayTextView);
        overallCostsValueInChosenDateTextView.setText(costValueOnChosenDayString + " руб.");

        CostsDataBase db = new CostsDataBase(this, null, null, 1);
        List<String> listOfEntries = db.getCostValuesOnSpecifiedDate(Integer.valueOf(chosenDayString),
                                                                    Integer.valueOf(chosenMonthString),
                                                                    Integer.valueOf(chosenYearString));

        String[] entriesOnSpecifiedDateArray = new String[listOfEntries.size()];
        listOfEntries.toArray(entriesOnSpecifiedDateArray);

        ListAdapter costsAdapter = new CostsAdapter(this, entriesOnSpecifiedDateArray);

        ListView chosenDateCostsListView = (ListView) findViewById(R.id.chosenDateCostsListView);
        chosenDateCostsListView.setAdapter(costsAdapter);
    }
}
