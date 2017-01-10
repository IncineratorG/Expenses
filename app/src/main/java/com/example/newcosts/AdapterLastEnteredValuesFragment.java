package com.example.newcosts;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class AdapterLastEnteredValuesFragment extends RecyclerView.Adapter<AdapterLastEnteredValuesFragment.FragmentLastEnteredValuesViewHolder> {

    private OnItemClickListener clickListener;
    private List<ExpensesDataUnit> data;
    private Context context;
    private Calendar calendar;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }


    public AdapterLastEnteredValuesFragment(List<ExpensesDataUnit> data, Context context) {
        this.data = data;
        this.context = context;
        calendar = Calendar.getInstance();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public FragmentLastEnteredValuesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_last_entered_values_single_item, parent, false);
        return new FragmentLastEnteredValuesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FragmentLastEnteredValuesViewHolder holder, int position) {

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
        holder.separatorLineLayout.setVisibility(View.VISIBLE);;

        // Если у элемента нет заметки - убираем соответсвующее поле
        if (!data.get(position).getExpenseNoteString().equals("")) {
            holder.expensesNoteTextView.setVisibility(View.VISIBLE);
            holder.separatorLineLayout.setVisibility(View.VISIBLE);
            holder.expensesNoteTextView.setText(data.get(position).getExpenseNoteString());
        } else {
            holder.expensesNoteTextView.setVisibility(View.GONE);
            holder.separatorLineLayout.setVisibility(View.GONE);
        }

        holder.editLayout.setClickable(false);
        holder.deleteLayout.setClickable(false);

        holder.hiddenDataTextView.setText("0");
        holder.layoutWithText.clearAnimation();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }



    // ===================================== View Holder ===========================================
    public class FragmentLastEnteredValuesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout layoutWithText;
        private LinearLayout editLayout;
        private LinearLayout deleteLayout;
        private LinearLayout separatorLineLayout;
        private LinearLayout topLayout;
        private LinearLayout dateLayout;
        private TextView hiddenDataTextView;
        private TextView expensesTypeTextView;
        private TextView expensesValueTextView;
        private TextView expensesNoteTextView;
        private TextView dateTextView;


        public FragmentLastEnteredValuesViewHolder(View itemView) {
            super(itemView);

            hiddenDataTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_hidden_data_textview);
            hiddenDataTextView.setText("0");
            hiddenDataTextView.setVisibility(View.GONE);
            expensesTypeTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_expenses_type_textview);
            expensesValueTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_expenses_value_textview);
            expensesNoteTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_expenses_note_textview);
            dateTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_date_textview);

            editLayout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_edit_layout);
            deleteLayout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_delete_layout);
            separatorLineLayout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_note_separator_line);
            topLayout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_top_layout);
            dateLayout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_date_layout);
            layoutWithText = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_layout_with_text);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null)
                clickListener.onItemClick(v, getAdapterPosition());
        }

    }
}