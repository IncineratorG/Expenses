package com.example.costs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

public class Periods extends AppCompatActivity {

    CostsDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_periods);

        db = new CostsDB(this, null, null, 1);

        // Создаём массив из периодов (месяца и года), для которых
        // имеются записи в базе данных
        List<String> periodsList = db.getAllPeriods();
        String[] periodsArray = new String[periodsList.size()];
        periodsList.toArray(periodsArray);

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
