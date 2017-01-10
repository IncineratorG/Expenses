package com.example.newcosts;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


public class FragmentLastEnteredValuesScreen extends Fragment {
    private Context context;
    private RecyclerView recyclerView;
    private List<ExpensesDataUnit> listOfLastEntries;
    private int selectedItemPosition = -1;
    private AdapterLastEnteredValuesFragment lastEnteredValuesFragmentAdapter;
    private View selectedItemView = null;
    private View lastSelectedItemView = null;
    private LinearLayoutManager linearLayoutManager;

    private String value = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View lastEnteredValuesView = inflater.inflate(R.layout.fragment_last_entered_values_screen, container, false);
        recyclerView = (RecyclerView) lastEnteredValuesView.findViewById(R.id.fragment_last_entered_values_recycler_view);

        return lastEnteredValuesView;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Получаем последние введённые значения
        CostsDB cdb = CostsDB.getInstance(context);
        listOfLastEntries = cdb.getLastEntries_V3(30);

        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);


        lastEnteredValuesFragmentAdapter = new AdapterLastEnteredValuesFragment(listOfLastEntries, context);
        // Нажатие на элемент списка последних введённых значений
        lastEnteredValuesFragmentAdapter.setClickListener(new AdapterLastEnteredValuesFragment.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                // Необходимо обеспечить, чтобы только один элемент был доступен для редактирования
                // в данный момент.
                // При нажатии на элемент списка проверяем, открыт ли для редактирования какой-либо
                // другой элемент этого списка. Если открыт и отображается в данный момент на экране -
                // закрываем этот элемент (проигрываем анимацию и объявляем его закрытым)
                if (lastSelectedItemView != null && lastSelectedItemView != itemView) {
                    TextView hiddenDataTextView = (TextView) lastSelectedItemView.findViewById(R.id.fragment_last_entered_values_hidden_data_textview);
                    if (hiddenDataTextView.getText().toString().equals("1")) {

                        // Инициализируем необходимые компоненты
                        LinearLayout lastSelectedLayoutWithText = (LinearLayout) lastSelectedItemView.findViewById(R.id.fragment_last_entered_values_layout_with_text);
                        LinearLayout lastSelectedEditLayout = (LinearLayout) lastSelectedItemView.findViewById(R.id.fragment_last_entered_values_edit_layout);
                        LinearLayout lastSelectedDeleteLayout = (LinearLayout) lastSelectedItemView.findViewById(R.id.fragment_last_entered_values_delete_layout);

                        // Объявляем элемент списка закрытым и проигрываем анимацию
                        hiddenDataTextView.setText("0");
                        Animation slideAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_item_half_right);
                        slideAnimation.setFillAfter(true);
                        lastSelectedLayoutWithText.startAnimation(slideAnimation);

                        // Снимаем слушатели с "кнопок" редактирования и удаления и
                        // объявляем их некликабельными
                        lastSelectedEditLayout.setOnClickListener(null);
                        lastSelectedDeleteLayout.setOnClickListener(null);

                        lastSelectedEditLayout.setClickable(false);
                        lastSelectedDeleteLayout.setClickable(false);
                    }
                }

                // При нажатии на элемент списка инициализируем необходимые компоненты
                LinearLayout layoutWithText = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_layout_with_text);
                LinearLayout editLayout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_edit_layout);
                LinearLayout deleteLayout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_delete_layout);

                // Если элемент списка закрыт для редактирования - открываем его, объявляем открытым,
                // проигрываем анимацию открытия, получаем позициб выбранного элемента и его View,
                // устанавливаем слушатели для редактирования и удаления элемента списка
                // Иначе (нажимаем на уже открытый элемент) - объявляем его закрытым, проигрываем
                // анимацию, снимаем слушатели
                TextView hiddenDataTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_hidden_data_textview);
                if (hiddenDataTextView.getText().toString().equals("0")) {
                    hiddenDataTextView.setText("1");
                    Animation slideAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_item_half_left);
                    slideAnimation.setFillAfter(true);
                    layoutWithText.startAnimation(slideAnimation);

                    selectedItemView = itemView;
                    lastSelectedItemView = itemView;
                    selectedItemPosition = position;
                    editLayout.setOnClickListener(editLayoutClickListener);
                    deleteLayout.setOnClickListener(deleteLayoutClickListener);
                } else {
                    hiddenDataTextView.setText("0");
                    Animation slideAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_item_half_right);
                    slideAnimation.setFillAfter(true);
                    layoutWithText.startAnimation(slideAnimation);

                    editLayout.setOnClickListener(null);
                    deleteLayout.setOnClickListener(null);

                    editLayout.setClickable(false);
                    deleteLayout.setClickable(false);
                }
            }
        });
        recyclerView.setAdapter(lastEnteredValuesFragmentAdapter);
    }




    // Удаление выбранного элемента списка
    private View.OnClickListener deleteLayoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            if (selectedItemView == null)
//                return;
//
//            TextView selectedItemNameTextView = (TextView) selectedItemView.findViewById(R.id.fragment_last_entered_values_expenses_type_textview);
//            TextView selectedItemValueTextView = (TextView) selectedItemView.findViewById(R.id.fragment_last_entered_values_expenses_value_textview);
//            LinearLayout selectedItemDateLayout = (LinearLayout) selectedItemView.findViewById(R.id.fragment_last_entered_values_date_layout);

//            if (selectedItemPosition < listOfLastEntries.size() - 1) {
//                if (selectedItemDateLayout.getVisibility() != View.GONE) {
//                    if (listOfLastEntries.get(selectedItemPosition).getDay() == listOfLastEntries.get(selectedItemPosition + 1).getDay() &&
//                            listOfLastEntries.get(selectedItemPosition).getMonth() == listOfLastEntries.get(selectedItemPosition + 1).getMonth() &&
//                            listOfLastEntries.get(selectedItemPosition).getYear() == listOfLastEntries.get(selectedItemPosition + 1).getYear())
//                    {
//                        TextView selectedItemDateTextView = (TextView) selectedItemView.findViewById(R.id.fragment_last_entered_values_date_textview);
//
//                        View nextItemView = linearLayoutManager.findViewByPosition(selectedItemPosition + 1);
//
//                        LinearLayout nextItemDateLayout = (LinearLayout) nextItemView.findViewById(R.id.fragment_last_entered_values_date_layout);
//                        TextView nextItemDateTextView = (TextView) nextItemView.findViewById(R.id.fragment_last_entered_values_date_textview);
//                        LinearLayout nextItemTopLayout = (LinearLayout) nextItemView.findViewById(R.id.fragment_last_entered_values_top_layout);
//
//                        nextItemDateTextView.setText(selectedItemDateTextView.getText().toString());
//
//                        Animation slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_out);
//                        slideOut.setFillAfter(true);
//                        Animation slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in);
//                        slideIn.setFillAfter(true);
//
//                        nextItemView.setAnimation(slideOut);
//                        nextItemDateLayout.setVisibility(View.INVISIBLE);
//                        nextItemView.setAnimation(slideIn);
//
////                        nextItemDateLayout.setVisibility(View.INVISIBLE);
////                        Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.insert);
////                        fadeOut.setFillAfter(true);
////                        nextItemDateLayout.setAnimation(fadeOut);
////                        nextItemDateLayout.setVisibility(View.VISIBLE);
//
//                        nextItemDateLayout.setVisibility(View.VISIBLE);
//                    }
//                }
//            }



//            View nextItemView = linearLayoutManager.findViewByPosition(selectedItemPosition + 1);
//            TextView name = (TextView) nextItemView.findViewById(R.id.fragment_last_entered_values_expenses_type_textview);
//            TextView value = (TextView) nextItemView.findViewById(R.id.fragment_last_entered_values_expenses_value_textview);
//
//            System.out.println(name.getText().toString());
//            System.out.println(value.getText().toString());

            listOfLastEntries.remove(selectedItemPosition);
            lastEnteredValuesFragmentAdapter.notifyItemRemoved(selectedItemPosition);
        }
    };







//    public void func() {
//        int[] itemViewPosition = new int[2];
//        itemView.getLocationOnScreen(itemViewPosition);
//
//        LinearLayout layout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_layout_with_text);
//        layout.getLocationOnScreen(itemViewPosition);
//
//        LinearLayout dateLayout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_date_layout);
//        TextView dateTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_date_textview);
//
//
//        int width = itemView.getWidth();
//        int height = dateLayout.getHeight();
//
//        layout.setVisibility(View.INVISIBLE);
//
//        EditDialogFragment editDialogFragment = EditDialogFragment.newInstance(listOfLastEntries.get(position), itemViewPosition[0], itemViewPosition[1], height, width);
//        editDialogFragment.setTargetFragment(FragmentLastEnteredValuesScreen_V2.this, Constants.EDIT_EXPENSE_RECORD_DIALOG_REQUEST_CODE);
//        editDialogFragment.show(getFragmentManager(), Constants.EDIT_DIALOG_TAG);
//    }







    // Редактирование выбранного элемента списка
    private View.OnClickListener editLayoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println("EDIT");
        }
    };



    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);


    }
}
