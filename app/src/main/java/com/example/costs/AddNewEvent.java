package com.example.costs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNewEvent extends AppCompatActivity {

    TextView eventDescriptionTextView;
    TextView eventDayTextView;
    TextView eventMonthTextView;
    TextView eventYearTextView;

    ListView eventsListView;

    PopupMenu eventsPopupMenu;

    CostsDB db;

    String dateOfEventString;
    String eventDateError = "Некорректная дата";

    boolean isInputIncorrect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        // Инициализируем поля ввода
        eventDescriptionTextView = (TextView) findViewById(R.id.eventDesc);
        eventDayTextView = (TextView) findViewById(R.id.eventDay);
        eventMonthTextView = (TextView) findViewById(R.id.eventMonth);
        eventYearTextView = (TextView) findViewById(R.id.eventYear);

        eventsListView = (ListView) findViewById(R.id.eventsList);

        // Получаем доступ к базе данных
        db = new CostsDB(this, null, null, 1);

        // Инициализируем список предстоящих событий
        SetEventsListView();
    }

    public void SetEventsListView() {
        String[] eventsArray = db.getEvents();

        ListAdapter eventsListAdapter = new EventsAdapter(this, eventsArray);
        eventsListView.setAdapter(eventsListAdapter);

        // При нажатии на событии появляется меню,
        // предлагающее удалить данное событие из списка
        eventsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        final String textLine = (String) parent.getItemAtPosition(position);
                        final String descriptionTextToRemove = textLine.substring(textLine.indexOf("$") + 1);

                        eventsPopupMenu = new PopupMenu(AddNewEvent.this, view);
                        eventsPopupMenu.getMenu().add(1, 1, 1, "Удалить");

                        eventsPopupMenu.setOnMenuItemClickListener(
                                new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        db.removeEvent(descriptionTextToRemove);
                                        Toast deleteEventToast = Toast.makeText(AddNewEvent.this, "Событие удалено", Toast.LENGTH_LONG);
                                        deleteEventToast.show();
                                        SetEventsListView();

                                        return true;
                                    }
                                }
                        );

                        eventsPopupMenu.show();
                    }
                }
        );
    }

    public void addEventButtonOnClick(View view) {
        Calendar calendar = Calendar.getInstance();

        // Получаем и проверяем входные данные
        int eventDay = 3;
        int eventMonth = 5;
        int eventYear = 1989;
        try {
            isInputIncorrect = false;
            eventDay = Integer.parseInt(eventDayTextView.getText().toString());
            eventMonth = Integer.parseInt(eventMonthTextView.getText().toString());
            eventYear = Integer.parseInt(eventYearTextView.getText().toString());

            if ((eventDay < 1 && eventDay > 31) || (eventMonth < 1 && eventMonth > 12) || (eventYear < calendar.get(Calendar.YEAR))) {
                isInputIncorrect = true;
                Toast errorDayToast = Toast.makeText(this, eventDateError, Toast.LENGTH_LONG);
                errorDayToast.show();
            }

            // Обязательно должно присутствовать описание события
            if (eventDescriptionTextView.getText().toString().equals("")) {
                isInputIncorrect = true;
                Toast noDescriptionToast = Toast.makeText(this, "Опишите событие", Toast.LENGTH_LONG);
                noDescriptionToast.show();
            }
        } catch (Exception e) {
            isInputIncorrect = true;
            Toast errorDayToast = Toast.makeText(this, eventDateError, Toast.LENGTH_LONG);
            errorDayToast.show();
        }

        // Если всё в порядке - записываем данные о предстоящем событии в базу данных
        if (!isInputIncorrect) {

            dateOfEventString = String.valueOf(eventDay) + "." + String.valueOf(eventMonth) + "." + String.valueOf(eventYear);
            long dateOfEventInMilliseconds = 0;
            boolean allDataIsFine = true;

            try {
                dateOfEventInMilliseconds = new SimpleDateFormat("dd.MM.yyyy").parse(dateOfEventString).getTime();
            } catch (Exception e) {
                Toast errorInDateParsingToast = Toast.makeText(this, "ERROR PARSING DATE", Toast.LENGTH_LONG);
                errorInDateParsingToast.show();
                allDataIsFine = false;
            }

            if (allDataIsFine) {
                db.addNewEvent(eventDescriptionTextView.getText().toString(), dateOfEventString, dateOfEventInMilliseconds);

                Toast addEventToast = Toast.makeText(this, "Событие добавлено", Toast.LENGTH_LONG);
                addEventToast.show();

                eventDescriptionTextView.setText("");
                eventDayTextView.setText("");
                eventMonthTextView.setText("");
                eventYearTextView.setText("");

                SetEventsListView();
            }
        }

    }
}
