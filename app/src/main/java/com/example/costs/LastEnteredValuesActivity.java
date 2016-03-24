package com.example.costs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

public class LastEnteredValuesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_entered_values);

        CostsDataBase cdb = new CostsDataBase(this, null, null, 1);

        List<String> lastMonthEntriesList = cdb.getLastMonthEntriesGroupedByDays();
        if (lastMonthEntriesList == null) {
            return;
        }

        if (lastMonthEntriesList.size() == 0) {

            String[] lastMonthEntriesByDayArray = new String[1];
            lastMonthEntriesByDayArray[0] = "Нет записей.";

            ListView lastEnteredValuesListView = (ListView) findViewById(R.id.lastEnteredValuesListView);
            ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lastMonthEntriesByDayArray);

            lastEnteredValuesListView.setAdapter(adapter);

        } else {
            String[] lastMonthEntriesByDayArray = new String[lastMonthEntriesList.size()];

            lastMonthEntriesList.toArray(lastMonthEntriesByDayArray);

            ListView lastEnteredValuesListView = (ListView) findViewById(R.id.lastEnteredValuesListView);
            ListAdapter adapter = new LastEntriesAdapter(this, lastMonthEntriesByDayArray);

            lastEnteredValuesListView.setAdapter(adapter);

            lastEnteredValuesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String chosenTextLine = String.valueOf(parent.getItemAtPosition(position));

                    String day = chosenTextLine.substring(0, chosenTextLine.indexOf(" "));
                    String month = chosenTextLine.substring(chosenTextLine.indexOf(" ") + 1, chosenTextLine.indexOf("$") - 5);
                    String year = chosenTextLine.substring(chosenTextLine.indexOf("$") - 4, chosenTextLine.indexOf("$"));
                    String costValue = chosenTextLine.substring(chosenTextLine.indexOf("$") + 1);

                    Intent chosenDayEntriesActivity = new Intent(LastEnteredValuesActivity.this, ChosenDayEntriesActivity.class);

                    chosenDayEntriesActivity.putExtra("chosenDay", day);
                    chosenDayEntriesActivity.putExtra("chosenMonth", month);
                    chosenDayEntriesActivity.putExtra("chosenYear", year);
                    chosenDayEntriesActivity.putExtra("costValueOnChosenDay", costValue);

                    startActivity(chosenDayEntriesActivity);
                }
            });
        }
    }





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent mainScreenIntent = new Intent(this, MainActivity.class);
            mainScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainScreenIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
