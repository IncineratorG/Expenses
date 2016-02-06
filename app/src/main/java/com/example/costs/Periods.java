package com.example.costs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Periods extends AppCompatActivity {

    CostsDataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_periods);

        db = new CostsDataBase(this, null, null, 1);

        // Создаём массив из периодов (месяца и года), для которых
        // имеются записи в базе данных. Если ни одной записи не найдено -
        // массив будет содержать текущий месяц и год.
        String[] periodsArray = db.getAllPeriods();
        if (periodsArray.length == 0) {
            Bundle bundleDate = getIntent().getExtras();
            if (bundleDate == null)
                return;

            periodsArray = new String[1];
            periodsArray[0] = (String) bundleDate.get("currentDate");
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
