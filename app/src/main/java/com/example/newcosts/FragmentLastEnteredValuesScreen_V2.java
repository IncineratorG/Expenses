package com.example.newcosts;

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

import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class FragmentLastEnteredValuesScreen_V2 extends Fragment {
    private Context context;
    private RecyclerView recyclerView;
    private List<ExpensesDataUnit> listOfLastEntries;
    private AdapterLastEnteredValuesRecyclerView_V2 lastEnteredValuesFragmentAdapter;
    private int selectedItemPosition = -1;
    private CostsDB cdb;

    private Snackbar deleteItemSnackbar;






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
        cdb = CostsDB.getInstance(context);
        listOfLastEntries = cdb.getLastEntries_V3(100);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        // При нажатии на элемент списка появляется диалоговое окно, из которого можно
        // удалить или изменить выбранную запись
        lastEnteredValuesFragmentAdapter = new AdapterLastEnteredValuesRecyclerView_V2(listOfLastEntries, context);
        lastEnteredValuesFragmentAdapter.setClickListener(new AdapterLastEnteredValuesRecyclerView_V2.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, final int position) {
                selectedItemPosition = position;

                DialogFragmentEditExpenses editDialogFragment = DialogFragmentEditExpenses.newInstance(listOfLastEntries.get(position));
                editDialogFragment.setTargetFragment(FragmentLastEnteredValuesScreen_V2.this, Constants.EDIT_EXPENSE_RECORD_DIALOG_REQUEST_CODE);
                editDialogFragment.show(getFragmentManager(), Constants.EDIT_DIALOG_TAG);
            }
        });
        recyclerView.setAdapter(lastEnteredValuesFragmentAdapter);
    }

    // Обработка результата нажатия кнопок в диалоговом окне, отображающемся
    // при нажатии на элемент списка последних введённых значений
    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.EDIT_EXPENSE_RECORD_DIALOG_REQUEST_CODE) {
            switch (resultCode) {
                case Constants.DELETE_ITEM:
                    final ExpensesDataUnit deletedItem = listOfLastEntries.get(selectedItemPosition);

                    // Удаляем выбранный элемент
                    listOfLastEntries.remove(selectedItemPosition);
                    lastEnteredValuesFragmentAdapter.notifyItemRemoved(selectedItemPosition);
                    lastEnteredValuesFragmentAdapter.notifyDataSetChanged();

                    cdb.removeCostValue(deletedItem.getMilliseconds());


                    // Отображаем сообщение об удалении выбранного элемента
                    // с возможностью его восстановления при нажатии кнопки "Отмена"
                    deleteItemSnackbar = Snackbar
                            .make(recyclerView, "Запись удалена", Snackbar.LENGTH_LONG)
                            .setAction("Отмена", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    listOfLastEntries.add(selectedItemPosition, deletedItem);
                                    lastEnteredValuesFragmentAdapter.notifyItemInserted(selectedItemPosition);
                                    lastEnteredValuesFragmentAdapter.notifyDataSetChanged();
                                    recyclerView.scrollToPosition(selectedItemPosition);

                                    cdb.addCostInMilliseconds(deletedItem.getExpenseId_N(),
                                                              deletedItem.getExpenseValueString(),
                                                              deletedItem.getMilliseconds(),
                                                              deletedItem.getExpenseNoteString());

                                    Snackbar restoreItemSnackbar = Snackbar
                                            .make(recyclerView, "Запись восстановлена", Snackbar.LENGTH_LONG);
                                    restoreItemSnackbar.show();
                                }
                            })
                            .setActionTextColor(ContextCompat.getColor(context, R.color.deleteRed));
                    deleteItemSnackbar.show();

                    break;
                case Constants.EDIT_ITEM:
                    ExpensesDataUnit editedItem = listOfLastEntries.get(selectedItemPosition);
                    Intent inputDataActivityIntent = new Intent(context, ActivityInputData.class);
                    inputDataActivityIntent.putExtra(Constants.EXPENSE_DATA_UNIT_LABEL, editedItem);
                    inputDataActivityIntent.putExtra(Constants.ACTIVITY_INPUT_DATA_MODE, Constants.EDIT_MODE);
                    inputDataActivityIntent.putExtra(Constants.PREVIOUS_ACTIVITY_INDEX, Constants.FRAGMENT_LAST_ENTERED_VALUES_SCREEN);
                    startActivity(inputDataActivityIntent);
                    break;
            }
        }
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        if (deleteItemSnackbar != null)
            deleteItemSnackbar.dismiss();
    }




















//    public void alertDialogCreate() {
//                AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
//                aBuilder.setView(R.layout.edit_cost_value_dialog);
//
//                aBuilder.setNegativeButton("Редактировать", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        System.out.println("EDIT");
//                    }
//                });
//                aBuilder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        listOfLastEntries.remove(position);
//                        lastEnteredValuesFragmentAdapter.notifyItemRemoved(position);
//                    }
//                });
//
//                AlertDialog dialog = aBuilder.create();
//                dialog.show();
//
//                TextView dialogExpenseName = (TextView) dialog.findViewById(R.id.edit_cost_value_dialog_costName);
//                dialogExpenseName.setText(listOfLastEntries.get(position).getExpenseName());
//
//                TextView dialogExpenseValue = (TextView) dialog.findViewById(R.id.edit_cost_value_dialog_costValue);
//                dialogExpenseValue.setText(listOfLastEntries.get(position).getExpenseValueString());
//
//                TextView dialogExpenseNote = (TextView) dialog.findViewById(R.id.edit_cost_value_dialog_costNote);
//                if (listOfLastEntries.get(position).HAS_NOTE)
//                    dialogExpenseNote.setText(listOfLastEntries.get(position).getExpenseNoteString());
//                else
//                    dialogExpenseNote.setVisibility(View.GONE);
//    }
}
