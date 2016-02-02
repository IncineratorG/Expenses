package com.example.costs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;

public class InputDataActivity extends AppCompatActivity {
    static final String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    static final String[] declensionMonthNames = {"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};

    EditText inputTextField;
    TextView costsTypeTextView;
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

        dateTextView = (TextView) findViewById(R.id.date);
        dateTextView.setText(currentDay + " " + declensionMonthNames[currentMonth] + " " + currentYear);

        costsTypeTextView = (TextView) findViewById(R.id.costsType);
        costsTypeTextView.setText(costType);


        // Создаём всплывающее меню для отображения
        // последних десяти введённых значений
        //GenerateCostsPopupMenu();
    }


    public void OkButtonOnClick(View View) {
        AddCostsToDataBase();
    }

    public void AddCostsToDataBase() {
        String inputTextString = inputTextField.getText().toString();
        if (!inputTextString.equals("")) {
            BigDecimal bg = new BigDecimal(inputTextString);
            bg = bg.add(new BigDecimal(currentCosts)).setScale(2, BigDecimal.ROUND_HALF_UP);

            db.addCosts(bg.doubleValue(), costType);
            inputTextField.setText("");
            currentCosts = bg.doubleValue();
            //GenerateCostsPopupMenu();
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
        String[] lastEnteredValues = db.getLastEnteredValues();
        costsPopupMenu = new PopupMenu(this, costsTypeTextView);

        for (int i = 0; i < lastEnteredValues.length; ++i) {
            costsPopupMenu.getMenu().add(1, i + 1, i + 1, lastEnteredValues[i]);
        }
    }

    public void onCostTypeClick(View view) {
        costsPopupMenu.show();
    }
    */
}
