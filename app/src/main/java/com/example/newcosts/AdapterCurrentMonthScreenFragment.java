package com.example.newcosts;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class AdapterCurrentMonthScreenFragment extends RecyclerView.Adapter<AdapterCurrentMonthScreenFragment.FragmentCurrentMonthScreenViewHolder> {

    private OnItemClickListener clickListener;
    private List<ExpensesDataUnit> data;
    private Context context;
    private Calendar calendar;
    private Fragment targetFragment;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }



    public AdapterCurrentMonthScreenFragment(List<ExpensesDataUnit> data, Context context, Fragment targetFragment) {
        this.data = data;
        this.context = context;
        this.targetFragment = targetFragment;
        calendar = Calendar.getInstance();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public FragmentCurrentMonthScreenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_current_month_screen_single_item, parent, false);
        return new FragmentCurrentMonthScreenViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FragmentCurrentMonthScreenViewHolder holder, int position) {
        holder.categoryNameTextView.setText(data.get(position).getExpenseName());

        final int finalPosition = position;
        // При нажатии на значок редактирования категории расходов
        // показываем соответсвующее дилоговое окно
        holder.editCategoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditExpenseNameDialogFragment editExpenseNameDialogFragment = EditExpenseNameDialogFragment.newInstance(data.get(finalPosition));
                editExpenseNameDialogFragment.setTargetFragment(targetFragment, Constants.EDIT_EXPENSE_NAME_REQUEST_CODE);
                editExpenseNameDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), Constants.EDIT_DIALOG_TAG);
            }
        });

        // При обработке пункта "Добавить новую категорию" необходимо
        // скрыть ненужные элементы
        if (data.get(position).getExpenseId_N() == Integer.MIN_VALUE) {
            holder.editCategoryImageView.setVisibility(View.GONE);
            holder.categoryValueTextView.setVisibility(View.GONE);
            holder.inCurrentMonthTextView.setVisibility(View.GONE);
            holder.arrowRight.setVisibility(View.GONE);
        } else {
            holder.inCurrentMonthTextView.setVisibility(View.VISIBLE);
            holder.editCategoryImageView.setVisibility(View.VISIBLE);
            holder.categoryValueTextView.setVisibility(View.VISIBLE);
            holder.arrowRight.setVisibility(View.VISIBLE);
            holder.categoryValueTextView.setText(data.get(position).getExpenseValueString() + " руб.");
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
    public class FragmentCurrentMonthScreenViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView editCategoryImageView;
        private ImageView arrowRight;
        private TextView categoryNameTextView;
        private TextView categoryValueTextView;
        private TextView inCurrentMonthTextView;


        public FragmentCurrentMonthScreenViewHolder(View itemView) {
            super(itemView);

            editCategoryImageView = (ImageView) itemView.findViewById(R.id.current_month_single_item_edit_category_imageview);

            categoryNameTextView = (TextView) itemView.findViewById(R.id.current_month_single_item_category_textview);
            categoryValueTextView = (TextView) itemView.findViewById(R.id.current_month_single_item_value_textview);
            inCurrentMonthTextView = (TextView) itemView.findViewById(R.id.current_month_single_item_in_current_month_textview);
            arrowRight = (ImageView) itemView.findViewById(R.id.current_month_single_item_arrow_right_imageview);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null)
                clickListener.onItemClick(v, getAdapterPosition());
        }

    }
}
