package com.example.newcosts;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StatisticChosePeriodActivity extends AppCompatActivity {

//    EditText currentlyFocusedEditText;
    EditText initialDateEditText;
    EditText endingDateEditText;
    String selectedDateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_chose_period);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Задать период");
        actionBar.setDisplayHomeAsUpEnabled(true);

        initialDateEditText = (EditText) findViewById(R.id.initialDateTextView);
        //initialDateEditText.setInputType(0);

        endingDateEditText = (EditText) findViewById(R.id.endingDateTextView);
        //endingDateEditText.setInputType(0);

//        final CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);

        Calendar calendar = Calendar.getInstance();

        //System.out.println(calendar.getTimeInMillis());

//        calendar.setTimeInMillis(calendarView.getDate());
        selectedDateString = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);

        initialDateEditText.setText(selectedDateString);
        endingDateEditText.setText(selectedDateString);

//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                month = month + 1;
//                selectedDateString = dayOfMonth + "." + month + "." + year;
//                currentlyFocusedEditText.setText(selectedDateString);
//            }
//        });

//        initialDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    currentlyFocusedEditText = initialDateEditText;
//                }
//            }
//        });
//        endingDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    currentlyFocusedEditText = endingDateEditText;
//                }
//            }
//        });
    }

    public void OnSetPeriodButtonClick(View view) {
        String initialSelectedDateString = initialDateEditText.getText().toString();
        String endingSelectedDateString = endingDateEditText.getText().toString();

//        int initialDay = Integer.valueOf(initialSelectedDateString.substring(0, initialSelectedDateString.indexOf(".")));
//        int initialMonth = Integer.valueOf(initialSelectedDateString.substring(initialSelectedDateString.indexOf(".") + 1, initialSelectedDateString.lastIndexOf(".")));
//        int initialYear = Integer.valueOf(initialSelectedDateString.substring(initialSelectedDateString.lastIndexOf(".") + 1));
//
//        int endingDay = Integer.valueOf(endingSelectedDateString.substring(0, endingSelectedDateString.indexOf(".")));
//        int endingMonth = Integer.valueOf(initialSelectedDateString.substring(initialSelectedDateString.indexOf(".") + 1, initialSelectedDateString.lastIndexOf(".")));
//        int endingYear = Integer.valueOf(initialSelectedDateString.substring(initialSelectedDateString.lastIndexOf(".") + 1));

        long initialDateInMilliseconds = 0;
        long endingDateInMilliseconds = 0;

        try {
            Date initialDate = new SimpleDateFormat("dd.MM.yyyy", Locale.UK).parse(initialSelectedDateString);
            initialDateInMilliseconds = initialDate.getTime();

            Date endingDate = new SimpleDateFormat("dd.MM.yyyy", Locale.UK).parse(endingSelectedDateString);
            endingDateInMilliseconds = endingDate.getTime() + 86400000;
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        System.out.println(initialDateInMilliseconds);
//        System.out.println(selectedDateString);

        if ((initialDateInMilliseconds <= endingDateInMilliseconds) && endingDateInMilliseconds != 0 && initialDateInMilliseconds != 0) {
            Intent chosenPeriodActivityIntent = new Intent(StatisticChosePeriodActivity.this, StatisticChosenPeriodActivity.class);

            chosenPeriodActivityIntent.putExtra("initialDateInMilliseconds", initialDateInMilliseconds);
            chosenPeriodActivityIntent.putExtra("endingDateInMilliseconds", endingDateInMilliseconds);
            chosenPeriodActivityIntent.putExtra("initialDateString", initialSelectedDateString);
            chosenPeriodActivityIntent.putExtra("endingDateString", endingSelectedDateString);

            startActivity(chosenPeriodActivityIntent);
        } else {
            Toast costTypeDeletedToast = Toast.makeText(StatisticChosePeriodActivity.this, "Конечная дата раньше начальной", Toast.LENGTH_LONG);
            costTypeDeletedToast.show();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, StatisticMainScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
