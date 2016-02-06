package com.example.costs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;

public class InputDataActivity extends AppCompatActivity {
    //static final String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    //static final String[] declensionMonthNames = {"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};

    EditText inputTextField;
    TextView costTypeTextView;
    TextView costValueTextView;
    TextView dateTextView;

    int currentDay;
    int currentMonth;                                       // Начинается с нуля
    int currentYear;

    String costType;

    Double currentCosts;

    CostsDataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);

        db = new CostsDataBase(this, null, null, 1);

        Bundle bundleData = getIntent().getExtras();
        if (bundleData == null)
            return;

        currentDay = (int) bundleData.get("currentDay");
        currentMonth = (int) bundleData.get("currentMonth");
        currentYear = (int) bundleData.get("currentYear");
        costType = (String) bundleData.get("costType");
        currentCosts = Double.parseDouble(String.valueOf(bundleData.get("costValue")));

        inputTextField = (EditText) findViewById(R.id.inputTextField);
        inputTextField.setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    AddCostsToDataBase();
                    return true;
                }
                return false;
            }

        });
        inputTextField.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        dateTextView = (TextView) findViewById(R.id.date);
        dateTextView.setText(currentDay + " " + MainActivity.declensionMonthNames[currentMonth] + " " + currentYear);

        costTypeTextView = (TextView) findViewById(R.id.costsType);
        costTypeTextView.setText(costType);

        costValueTextView = (TextView) findViewById(R.id.costValue);
        costValueTextView.setText(String.valueOf(currentCosts) + " руб.");
    }


    public void OkButtonOnClick(View View) {
        AddCostsToDataBase();
    }

    public void AddCostsToDataBase() {
        String inputTextString = inputTextField.getText().toString();
        if (!inputTextString.isEmpty() && !".".equals(inputTextString)) {
            db.addCosts(Double.valueOf(inputTextString), costType);

            inputTextField.setText("");
            currentCosts = db.getCostValue(-1, currentMonth, currentYear, costType);

            costValueTextView.setText(String.valueOf(currentCosts));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent mainScreenIntent = new Intent(this, MainActivity.class);
            mainScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainScreenIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }




    /*
    public void GenerateCostsPopupMenu() {
        String[] lastEnteredValues = db.getLastMonthEntriesGroupedByDays();
        costsPopupMenu = new PopupMenu(this, costTypeTextView);

        for (int i = 0; i < lastEnteredValues.length; ++i) {
            costsPopupMenu.getMenu().add(1, i + 1, i + 1, lastEnteredValues[i]);
        }
    }

    public void onCostTypeClick(View view) {
        costsPopupMenu.show();
    }
    */
}
