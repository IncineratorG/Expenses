package com.example.costs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class EventsAdapter extends ArrayAdapter<String> {

    public EventsAdapter(Context context, String[] events) {
        super(context, R.layout.single_event_row, events);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View singleEventRow = inflater.inflate(R.layout.single_event_row, parent, false);

        TextView dateOfEventTextView = (TextView) singleEventRow.findViewById(R.id.dateOfEvent);
        TextView eventDescriptionTextView = (TextView) singleEventRow.findViewById(R.id.eventDescription);

        // Строка из базы данных приходит в формате "ДатаСобытия$ОписаниеСобытия"
        String textLine = getItem(position);
        String eventDate = textLine.substring(0, textLine.indexOf("$"));
        String eventDescription = textLine.substring(textLine.indexOf("$") + 1);

        dateOfEventTextView.setText(eventDate);
        eventDescriptionTextView.setText(eventDescription);

        return singleEventRow;
    }
}
