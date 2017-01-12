package com.example.newcosts;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FragmentCurrentMonthScreen extends Fragment {

    private Context context;
    private RecyclerView recyclerView;
    private TextView currentMonthTextView;
    private TextView overallValueTextView;
    private AdapterCurrentMonthScreenRecyclerView currentMonthScreenAdapter;
    private int currentMonth;
    private int currentYear;
    private int currentDay;
    private CostsDB cdb;
    private List<ExpensesDataUnit> listOfActiveCostNames;
    private Snackbar deleteCategorySnackbar;
    private double overallValueForCurrentMonth;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentMonthScreenView = inflater.inflate(R.layout.fragment_current_month_screen, container, false);
        recyclerView = (RecyclerView) currentMonthScreenView.findViewById(R.id.current_month_screen_recycler_view);
        currentMonthTextView = (TextView) currentMonthScreenView.findViewById(R.id.curren_month_screen_month_textview);
        overallValueTextView = (TextView) currentMonthScreenView.findViewById(R.id.current_month_screen_overall_value_textview);

        return currentMonthScreenView;
    }


    @Override
    public void onResume() {
        super.onResume();

        cdb = CostsDB.getInstance(context);

        // Получаем и устанавливаем текущую дату
        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        currentMonthTextView.setText("Всего за " + Constants.MONTH_NAMES[currentMonth]);

        // Получаем массив активных категорий расходов, получаем сумму расходов по
        // каждой категории в текущем месяце и суммарное значение затрат за текущий месяц
        overallValueForCurrentMonth = 0.0;
        ExpensesDataUnit[] activeExpenseNamesArray = cdb.getActiveCostNames_V3();
        listOfActiveCostNames = new ArrayList<>();
        for (int i = 0; i < activeExpenseNamesArray.length; ++i) {
            double singleUnitExpenseValue = cdb.getCostValue(-1, currentMonth, currentYear, activeExpenseNamesArray[i].getExpenseId_N());
            activeExpenseNamesArray[i].setExpenseValueDouble(singleUnitExpenseValue);
            activeExpenseNamesArray[i].setExpenseValueString(Constants.formatDigit(singleUnitExpenseValue));
            listOfActiveCostNames.add(activeExpenseNamesArray[i]);
            overallValueForCurrentMonth = overallValueForCurrentMonth + singleUnitExpenseValue;
        }
        // Последним элементом списка явлеятся пункт "Добавить новую категорию"
        ExpensesDataUnit addNewCategoryDataUnit = new ExpensesDataUnit();
        addNewCategoryDataUnit.setExpenseId_N(Integer.MIN_VALUE);
        addNewCategoryDataUnit.setExpenseName("Добавить новую категорию");
        addNewCategoryDataUnit.setExpenseValueString("+");
        listOfActiveCostNames.add(addNewCategoryDataUnit);
        // Устанавливаем суммарное значение затрат за текущий месяц
        overallValueTextView.setText(Constants.formatDigit(overallValueForCurrentMonth) + " руб.");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        currentMonthScreenAdapter = new AdapterCurrentMonthScreenRecyclerView(listOfActiveCostNames, context, this);
        currentMonthScreenAdapter.setClickListener(new AdapterCurrentMonthScreenRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                ExpensesDataUnit selectedDataUnit = listOfActiveCostNames.get(position);
                // Переходим на экран ввода затрат по выбранной категории
                if (selectedDataUnit.getExpenseId_N() != Integer.MIN_VALUE) {
                    Intent inputDataActivityIntent = new Intent(context, ActivityInputData.class);
                    inputDataActivityIntent.putExtra(Constants.EXPENSE_DATA_UNIT_LABEL, selectedDataUnit);
                    inputDataActivityIntent.putExtra(Constants.ACTIVITY_INPUT_DATA_MODE, Constants.INPUT_MODE);
                    inputDataActivityIntent.putExtra(Constants.PREVIOUS_ACTIVITY_INDEX, Constants.FRAGMENT_CURRENT_MONTH_SCREEN);
                    startActivity(inputDataActivityIntent);
                }
                // Добавляем новую статью расходов
                else if (selectedDataUnit.getExpenseId_N() == Integer.MIN_VALUE) {
                    // Далог добавления новой категории расходов
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.add_new_expense_type_popup);

                    // Инициализируем поле ввода названия новой статьи расходов
                    final AutoCompleteTextView inputTextField = (AutoCompleteTextView) dialog.findViewById(R.id.costTypeTextViewInAddNewCostTypePopup);
                    inputTextField.setFocusable(true);
                    inputTextField.setCursorVisible(true);
                    inputTextField.requestFocus();

                    // Отображаем клавиатуру
                    final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    // Получаем неактивные названия категорий
                    String[] nonActiveCostNames = cdb.getNonActiveCostNames();
                    ArrayAdapter<String> autoCompleteTextViewAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, nonActiveCostNames);
                    inputTextField.setAdapter(autoCompleteTextViewAdapter);

                    // Инициализируем кнопки всплывающего окна
                    Button addNewCostTypeButton = (Button) dialog.findViewById(R.id.addNewCostTypeButton);
                    Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

                    // Устанавливаем слушатели на кнопки
                    addNewCostTypeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String newExpenseName = inputTextField.getText().toString();

                            // Добавляем новую запись в базу
                            cdb.addCostName(newExpenseName);

                            // Создаём элемент расходов с только что созданным названием
                            // и инициализируем его
                            ExpensesDataUnit createdExpenseCategory = new ExpensesDataUnit();
                            createdExpenseCategory.setExpenseName(newExpenseName);
                            createdExpenseCategory.setExpenseId_N(cdb.getExpenseIdByName(newExpenseName));
                            createdExpenseCategory.setExpenseValueDouble(0);
                            createdExpenseCategory.setExpenseValueString("0");

                            // Вставляем созданный элемент на последнюю позицию в списке
                            // статей расходов и обновляем представление
                            int positionInListToInsert = listOfActiveCostNames.size() - 1;
                            listOfActiveCostNames.add(positionInListToInsert, createdExpenseCategory);

                            currentMonthScreenAdapter.notifyItemInserted(positionInListToInsert);
                            currentMonthScreenAdapter.notifyDataSetChanged();

                            // Скрываем клавиатуру
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            dialog.dismiss();

                            // Сообщаем пользователю о добавлении новой категории
                            Snackbar newCategoryCreatedSnackbar = Snackbar
                                    .make(recyclerView, "Категория '" + newExpenseName + "' создана", Snackbar.LENGTH_LONG);
                            newCategoryCreatedSnackbar.show();
                        }
                    });

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }
            }
        });
        recyclerView.setAdapter(currentMonthScreenAdapter);
    }


    // Обработка результата нажатия кнопок в диалоговом окне, отображающемся
    // при нажатии на значок редактирования категории расходов
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.EDIT_EXPENSE_NAME_REQUEST_CODE) {
            // Получаем название и ID выбранной категории
            ExpensesDataUnit selectedCategory = null;
            if (data != null) {
                selectedCategory = data.getExtras().getParcelable(Constants.EXPENSE_DATA_UNIT_LABEL);
            }

            switch (resultCode) {
                case Constants.DELETE_ITEM:
                    if (selectedCategory != null) {
                        // Если по выбранной на удаление категории нет записей в
                        //  текущем месяце - её можно удалить
                        if (selectedCategory.getExpenseValueDouble() > 0) {
                            Snackbar hasRecordsInCurrentMonth = Snackbar
                                    .make(recyclerView, "Нельзя удалить категорию по которой есть записи в текущем месяце", Snackbar.LENGTH_LONG);
                            hasRecordsInCurrentMonth.show();
                        } else {
                            // Удаляем выбранную категорию расходов
                            final int selectedCategoryIndexInList = listOfActiveCostNames.indexOf(selectedCategory);
                            cdb.deleteCostName(selectedCategory.getExpenseId_N());
                            listOfActiveCostNames.remove(selectedCategoryIndexInList);
                            currentMonthScreenAdapter.notifyItemRemoved(selectedCategoryIndexInList);

                            // Отображаем сообщение об удалении выбранного элемента
                            // с возможностью его восстановления при нажатии кнопки "Отмена"
                            final ExpensesDataUnit finalSelectedCategory = selectedCategory;
                            deleteCategorySnackbar = Snackbar
                                    .make(recyclerView, "Запись удалена", Snackbar.LENGTH_LONG)
                                    .setAction("Отмена", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            listOfActiveCostNames.add(selectedCategoryIndexInList, finalSelectedCategory);
                                            currentMonthScreenAdapter.notifyItemInserted(selectedCategoryIndexInList);
                                            currentMonthScreenAdapter.notifyDataSetChanged();

                                            cdb.addCostName(finalSelectedCategory.getExpenseName());

                                            Snackbar restoreItemSnackbar = Snackbar
                                                    .make(recyclerView, "Запись восстановлена", Snackbar.LENGTH_LONG);
                                            restoreItemSnackbar.show();
                                        }
                                    })
                                    .setActionTextColor(ContextCompat.getColor(context, R.color.deleteRed));
                            deleteCategorySnackbar.show();
                        }
                    }
                    break;
                case Constants.EDIT_ITEM:
                    if (selectedCategory != null) {
                        // Далог редактирования категории расходов
                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.add_new_expense_type_popup);

                        // Инициализируем поле переименования статьи расходов
                        final AutoCompleteTextView inputTextField = (AutoCompleteTextView) dialog.findViewById(R.id.costTypeTextViewInAddNewCostTypePopup);
                        inputTextField.setText(selectedCategory.getExpenseName());
                        inputTextField.setSelection(inputTextField.getText().length());
                        inputTextField.setFocusable(true);
                        inputTextField.setCursorVisible(true);
                        inputTextField.requestFocus();

                        // Отображаем клавиатуру
                        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                        // Инициализируем кнопки всплывающего окна
                        Button addNewCostTypeButton = (Button) dialog.findViewById(R.id.addNewCostTypeButton);
                        addNewCostTypeButton.setText("Переименовать");
                        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

                        // Устанавливаем слушатели на кнопки
                        final ExpensesDataUnit finalSelectedCategory1 = selectedCategory;
                        addNewCostTypeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String oldCategoryName = finalSelectedCategory1.getExpenseName();
                                String newCategoryName = inputTextField.getText().toString();
                                int result = cdb.renameCostName(finalSelectedCategory1.getExpenseId_N(), newCategoryName);

                                int selectedCategoryIndexInList = listOfActiveCostNames.indexOf(finalSelectedCategory1);
                                System.out.println(selectedCategoryIndexInList);

                                listOfActiveCostNames.get(selectedCategoryIndexInList).setExpenseName(newCategoryName);
                                currentMonthScreenAdapter.notifyDataSetChanged();

                                String messageToUser = "";
                                switch (result) {
                                    case 0:
                                        messageToUser = new StringBuilder()
                                                .append("'")
                                                .append(oldCategoryName)
                                                .append("' -> '")
                                                .append(newCategoryName)
                                                .append("'")
                                                .toString();
                                        break;
                                    case 2:
                                        messageToUser = new StringBuilder()
                                                .append("Категория '")
                                                .append(newCategoryName)
                                                .append("' уже создана")
                                                .toString();
                                        break;
                                }
                                Snackbar renameResultSnackbar = Snackbar
                                        .make(recyclerView, messageToUser, Snackbar.LENGTH_LONG);
                                renameResultSnackbar.show();

                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                dialog.cancel();
                            }
                        });

                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                dialog.cancel();
                            }
                        });

                        dialog.show();
                    }
                    break;
            }
        }
    }





    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        if (deleteCategorySnackbar != null)
            deleteCategorySnackbar.dismiss();
    }
}
