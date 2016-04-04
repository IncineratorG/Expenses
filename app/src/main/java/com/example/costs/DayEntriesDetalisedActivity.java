package com.example.costs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

        String[] entriesOnSpecifiedDayArray = db.getEntriesOnSpecifiedDayAndCostName(Integer.valueOf(day),
                Integer.valueOf(month),
                Integer.valueOf(year),
                costName);

        ListView entriesOnSpecifiedDayListView = (ListView) findViewById(R.id.entriesOnSpecifiedDayListView);
        DayEntriesDetalisedAdapter adapter = new DayEntriesDetalisedAdapter(this, entriesOnSpecifiedDayArray);
        entriesOnSpecifiedDayListView.setAdapter(adapter);

        entriesOnSpecifiedDayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder adBuilder = new AlertDialog.Builder(DayEntriesDetalisedActivity.this);
                adBuilder.setNegativeButton("Отмена", null);
                adBuilder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                adBuilder.setMessage("Удалить?");

                AlertDialog dialog = adBuilder.create();
                dialog.show();

                TextView dialogText = (TextView) dialog.findViewById(android.R.id.message);
                dialogText.setGravity(Gravity.CENTER);

            }
        });
    }
}
