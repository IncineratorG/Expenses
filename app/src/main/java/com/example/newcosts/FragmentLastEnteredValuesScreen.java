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

public class FragmentLastEnteredValuesScreen extends Fragment {


    private Context context;
    private RecyclerView recyclerView;
    private List<DataUnitExpenses> lastEntriesList;
    private AdapterLastEnteredValuesRecyclerView lastEnteredValuesFragmentAdapter;
    private int selectedItemPosition = -1;
    private DB_Costs cdb;
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

        if (Constants.stringsAreNull())
            Constants.loadStrings(context);

        // Получаем последние введённые значения
        cdb = DB_Costs.getInstance(context);
//        new AsyncTaskLoadMainActivityFragmentsData(cdb, Constants.FRAGMENT_LAST_ENTERED_VALUES_SCREEN, new CallbackValuesLoaded() {
//            @Override
//            public void valuesLoaded(int callingFragmentCode, List<DataUnitExpenses> data, double overallValue) {
//                if (callingFragmentCode == Constants.FRAGMENT_LAST_ENTERED_VALUES_SCREEN) {
//                    lastEntriesList = data;
//                    init();
//                }
//            }
//        }).execute();
        lastEntriesList = cdb.getLastEntries_V3(10);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        // При нажатии на элемент списка появляется диалоговое окно, из которого можно
        // удалить или изменить выбранную запись
        lastEnteredValuesFragmentAdapter = new AdapterLastEnteredValuesRecyclerView(lastEntriesList, context);
        lastEnteredValuesFragmentAdapter.setClickListener(new AdapterLastEnteredValuesRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, final int position) {
                selectedItemPosition = position;

                DialogFragmentEditLastEnteredValues editDialogFragment = DialogFragmentEditLastEnteredValues.newInstance(context, lastEntriesList.get(position));
                editDialogFragment.setTargetFragment(FragmentLastEnteredValuesScreen.this, Constants.EDIT_EXPENSE_RECORD_DIALOG_REQUEST_CODE);
                editDialogFragment.show(getFragmentManager(), Constants.EDIT_DIALOG_TAG);
            }
        });
        recyclerView.setAdapter(lastEnteredValuesFragmentAdapter);

        recyclerView.addOnScrollListener(new NEW_EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                System.out.println("LOAD_MORE");
                System.out.println(current_page);

                final int lastEntriesListLastElementPosition = lastEntriesList.size() - 1;
                long lastEntriesListLastElementMilliseconds = lastEntriesList.get(lastEntriesListLastElementPosition).getMilliseconds();

                final List<DataUnitExpenses> additionalLastEntriesList = cdb.getEntriesBeforeDateInMilliseconds(lastEntriesListLastElementMilliseconds, 10);
                lastEntriesList.addAll(additionalLastEntriesList);

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
//                        lastEnteredValuesFragmentAdapter.notifyItemInserted(lastEntriesListLastElementPosition);
                        lastEnteredValuesFragmentAdapter.notifyItemRangeInserted(lastEntriesListLastElementPosition, additionalLastEntriesList.size());
                    }
                });
            }
        });

        Constants.lastEnteredValuesFragmentDataIsActual(true);
    }


    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        // При нажатии на элемент списка появляется диалоговое окно, из которого можно
        // удалить или изменить выбранную запись
        lastEnteredValuesFragmentAdapter = new AdapterLastEnteredValuesRecyclerView(lastEntriesList, context);
        lastEnteredValuesFragmentAdapter.setClickListener(new AdapterLastEnteredValuesRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, final int position) {
                selectedItemPosition = position;

                DialogFragmentEditLastEnteredValues editDialogFragment = DialogFragmentEditLastEnteredValues.newInstance(context, lastEntriesList.get(position));
                editDialogFragment.setTargetFragment(FragmentLastEnteredValuesScreen.this, Constants.EDIT_EXPENSE_RECORD_DIALOG_REQUEST_CODE);
                editDialogFragment.show(getFragmentManager(), Constants.EDIT_DIALOG_TAG);
            }
        });
        recyclerView.setAdapter(lastEnteredValuesFragmentAdapter);
        Constants.lastEnteredValuesFragmentDataIsActual(true);
    }

    // Обработка результата нажатия кнопок в диалоговом окне, отображающемся
    // при нажатии на элемент списка последних введённых значений
    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.EDIT_EXPENSE_RECORD_DIALOG_REQUEST_CODE) {
            switch (resultCode) {
                case Constants.DELETE_ITEM:
                    Constants.lastEnteredValuesFragmentDataIsActual(false);
                    final DataUnitExpenses deletedItem = lastEntriesList.get(selectedItemPosition);

                    // Удаляем выбранный элемент
                    lastEntriesList.remove(selectedItemPosition);
                    lastEnteredValuesFragmentAdapter.notifyItemRemoved(selectedItemPosition);
                    lastEnteredValuesFragmentAdapter.notifyDataSetChanged();

                    cdb.removeCostValue(deletedItem.getMilliseconds());


                    // Отображаем сообщение об удалении выбранного элемента
                    // с возможностью его восстановления при нажатии кнопки "Отмена"
                    deleteItemSnackbar = Snackbar
                            .make(recyclerView,
                                    getResources().getString(R.string.flev_deleteItemSnackbar_string),
                                    Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(R.string.flev_deleteItemSnackbar_action_cancel_string), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    lastEntriesList.add(selectedItemPosition, deletedItem);
                                    lastEnteredValuesFragmentAdapter.notifyItemInserted(selectedItemPosition);
                                    lastEnteredValuesFragmentAdapter.notifyDataSetChanged();
                                    recyclerView.scrollToPosition(selectedItemPosition);

                                    cdb.addCostInMilliseconds(deletedItem.getExpenseId_N(),
                                            deletedItem.getExpenseValueString(),
                                            deletedItem.getMilliseconds(),
                                            deletedItem.getExpenseNoteString());

                                    Snackbar restoreItemSnackbar = Snackbar
                                            .make(recyclerView,
                                                    getResources().getString(R.string.flev_restoreItemSnackbar_string),
                                                    Snackbar.LENGTH_LONG);
                                    restoreItemSnackbar.show();
                                }
                            })
                            .setActionTextColor(ContextCompat.getColor(context, R.color.deleteRed));
                    deleteItemSnackbar.show();

                    break;
                case Constants.EDIT_ITEM:
                    Constants.lastEnteredValuesFragmentDataIsActual(false);
                    DataUnitExpenses editedItem = lastEntriesList.get(selectedItemPosition);
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
}
