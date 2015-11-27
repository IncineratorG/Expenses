package com.example.costs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNewEvent extends AppCompatActivity {

    TextView eventDescription;
    CalendarView calendarView;

    String dateOfEventString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        eventDescription = (TextView) findViewById(R.id.eventDescription);
        calendarView = (CalendarView) findViewById(R.id.calendarView);


        Calendar c = Calendar.getInstance();
        System.out.println(c.getTimeInMillis());


        calendarView.setOnDateChangeListener(
                new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                        dateOfEventString = String.valueOf(dayOfMonth) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year);

/*
                        long l = 0;
                        try {
                            l = new SimpleDateFormat("dd-MM-yyyy").parse(date).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
*/
                    }
                }
        );
    }
}
