package com.example.newcosts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class AdapterLastEnteredValuesRecyclerView_V2 extends RecyclerView.Adapter<AdapterLastEnteredValuesRecyclerView_V2.FragmentLastEnteredValuesViewHolder_V2> {

    private OnItemClickListener clickListener;
    private List<ExpensesDataUnit> data;
    private Context context;
    private Calendar calendar;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }


    public AdapterLastEnteredValuesRecyclerView_V2(List<ExpensesDataUnit> data, Context context) {
        this.data = data;
        this.context = context;
        calendar = Calendar.getInstance();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public FragmentLastEnteredValuesViewHolder_V2 onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_last_entered_values_single_item_v2, parent, false);
        return new FragmentLastEnteredValuesViewHolder_V2(v);
    }

    @Override
    public void onBindViewHolder(FragmentLastEnteredValuesViewHolder_V2 holder, int position) {
        // Группируем список последних введённых значений по дате занесения элементов в базу
        if (position > 0 && (data.get(position - 1).getDay() == data.get(position).getDay() &&
                data.get(position - 1).getMonth() == data.get(position).getMonth() &&
                data.get(position - 1).getYear() == data.get(position).getYear()))
        {
            holder.dateLayout.setVisibility(View.GONE);
        }
        else
        {
            calendar.setTimeInMillis(data.get(position).getMilliseconds());
            holder.dateLayout.setVisibility(View.VISIBLE);
            holder.dateTextView.setText(new StringBuilder().append(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)])
                    .append(", ")
                    .append(data.get(position).getDay())
                    .append(" ")
                    .append(Constants.DECLENSION_MONTH_NAMES[data.get(position).getMonth() - 1]));
        }

        holder.expensesTypeTextView.setText(data.get(position).getExpenseName());
        holder.expensesValueTextView.setText(data.get(position).getExpenseValueString() + " руб.");
        holder.expensesNoteTextView.setVisibility(View.VISIBLE);
        holder.noteSeparatorLayout.setVisibility(View.VISIBLE);;

        // Если у элемента нет заметки - убираем соответсвующее поле
        if (!data.get(position).getExpenseNoteString().equals("")) {
            holder.expensesNoteTextView.setVisibility(View.VISIBLE);
            holder.noteSeparatorLayout.setVisibility(View.VISIBLE);
            holder.expensesNoteTextView.setText(data.get(position).getExpenseNoteString());
        } else {
            holder.expensesNoteTextView.setVisibility(View.GONE);
            holder.noteSeparatorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }



    // ===================================== View Holder ===========================================
    public class FragmentLastEnteredValuesViewHolder_V2 extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout noteSeparatorLayout;
        private LinearLayout dateLayout;
        private TextView expensesTypeTextView;
        private TextView expensesValueTextView;
        private TextView expensesNoteTextView;
        private TextView dateTextView;


        public FragmentLastEnteredValuesViewHolder_V2(View itemView) {
            super(itemView);

            expensesTypeTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_expenses_type_textview);
            expensesValueTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_expenses_value_textview);
            expensesNoteTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_expenses_note_textview);
            dateTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_date_textview);

            noteSeparatorLayout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_layout_horizontal_note_separator);
            dateLayout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_date_layout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null)
                clickListener.onItemClick(v, getAdapterPosition());
        }

    }
}
