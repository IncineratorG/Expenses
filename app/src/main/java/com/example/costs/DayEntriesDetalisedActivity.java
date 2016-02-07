package com.example.costs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class DayEntriesDetalisedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_entries_detalised);

        CostsDataBase db = new CostsDataBase(this, null, null, 1);

        Bundle dataFromChosenDayEntriesActivity = getIntent().getExtras();
        String costName = (String) dataFromChosenDayEntriesActivity.get("costName");
        String day = (String) dataFromChosenDayEntriesActivity.get("chosenDay");
        String month = (String) dataFromChosenDayEntriesActivity.get("chosenMonth");
        String year = (String) dataFromChosenDayEntriesActivity.get("chosenYear");

        String[] entriesOnSpecifiedDayArray = db.getEntriesOnSpecifiedDateAndCostName(Integer.valueOf(day),
                                                                                        Integer.valueOf(month),
                                                                                        Integer.valueOf(year),
                                                                                        costName);

        ListView entriesOnSpecifiedDayListView = (ListView) findViewById(R.id.entriesOnSpecifiedDayListView);
        ListAdapter entriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, entriesOnSpecifiedDayArray);
        entriesOnSpecifiedDayListView.setAdapter(entriesAdapter);
    }
}
