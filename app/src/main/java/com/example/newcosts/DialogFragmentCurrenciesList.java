package com.example.newcosts;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * TODO: Add a class header comment
 */

public class DialogFragmentCurrenciesList extends DialogFragment {
    public CurrenciesListDialogCallback callback;


    public interface CurrenciesListDialogCallback {
        void getSelectedCurrency(String currencyString);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (CurrenciesListDialogCallback) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.currencies_list_dialog, container, false);

        if (Constants.stringsAreNull())
            Constants.loadStrings(getActivity());

        AdapterCurrenciesListDialogListView currenciesListViewAdapter = new AdapterCurrenciesListDialogListView(getActivity(), Constants.CURRENCIES);

        ListView currenciesListView = (ListView) v.findViewById(R.id.currencies_list_dialog_currencies_listview);
        currenciesListView.setAdapter(currenciesListViewAdapter);
        currenciesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callback.getSelectedCurrency(Constants.CURRENCIES[position]);
                dismiss();
            }
        });

        return v;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        return dialog;
    }

}
