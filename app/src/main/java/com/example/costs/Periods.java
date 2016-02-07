package com.example.costs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Calendar;
import java.util.List;

public class Periods extends AppCompatActivity {

    CostsDataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_periods);

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        db = new CostsDataBase(this, null, null, 1);

        List<String> listOfPeriods = db.getAllPeriods();
        String[] periodsArray = null;
        if (!listOfPeriods.contains(currentMonth + " " + currentYear)) {
            periodsArray = new String[listOfPeriods.size() + 1];
            periodsArray[0] = currentMonth + " " + currentYear;
            for (int i = 1; i < periodsArray.length; ++i) {
                periodsArray[i] = listOfPeriods.get(i - 1);
            }

        } else {
            periodsArray = new String[listOfPeriods.size()];
            listOfPeriods.toArray(periodsArray);
        }


        ListAdapter periodsListAdapter = new PeriodsAdapter(this, periodsArray);
        ListView periodsListView = (ListView) findViewById(R.id.periodsList);
        periodsListView.setAdapter(periodsListAdapter);

        periodsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent mainScreenIntent = new Intent(Periods.this, MainActivity.class);

                        String chosenPeriod = String.valueOf(parent.getItemAtPosition(position));
                        mainScreenIntent.putExtra("chosenPeriod", chosenPeriod);
                        mainScreenIntent.putExtra("fromPeriods", true);
                        mainScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(mainScreenIntent);
                    }
                }
        );
    }
}
