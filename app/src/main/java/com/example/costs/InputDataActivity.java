package com.example.costs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.math.BigDecimal;

public class InputDataActivity extends AppCompatActivity {
    static final String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    CostsDB db;

    EditText inputTextField;
    TextView costsTypeTextView;
    TextView dateTextView;

    PopupMenu costsPopupMenu;

    int currentMonth;                                       // Начинается с нуля
    int currentYear;

    String costTypeEnum;

    Double currentCosts;

    CostsDB.CostType costType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);

        db = new CostsDB(this, null, null, 1);

        Bundle bundleData = getIntent().getExtras();
        if (bundleData == null)
            return;

        currentMonth = (int) bundleData.get("currentMonth");        // Начинается с нуля
        currentYear = (int) bundleData.get("currentYear");
        costTypeEnum = (String) bundleData.get("costTypeEnum");
        currentCosts = Double.parseDouble(String.valueOf(bundleData.get("currentCosts")));

        switch (costTypeEnum) {
            case "FOOD":
                costType = CostsDB.CostType.FOOD;
                break;
            case "CLOTHES":
                costType = CostsDB.CostType.CLOTHES;
                break;
            case "GOODS":
                costType = CostsDB.CostType.GOODS;
                break;
            case "COMMUNAL_RENT":
                costType = CostsDB.CostType.COMMUNAL_RENT;
                break;
            case "SERVICES":
                costType = CostsDB.CostType.SERVICES;
                break;
            case "TRANSPORT":
                costType = CostsDB.CostType.TRANSPORT;
                break;
            case "ELECTRONICS":
                costType = CostsDB.CostType.ELECTRONICS;
                break;
            case "OTHERS":
                costType = CostsDB.CostType.OTHERS;
                break;
        }

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

        String currentDate = monthNames[currentMonth] + " " + currentYear;
        dateTextView.setText(currentDate);

        costsTypeTextView = (TextView) findViewById(R.id.costsType);
        costsTypeTextView.setText(bundleData.getString("costTypeText"));

        // Создаём всплывающее меню для отображения
        // последних десяти введённых значений
        GenerateCostsPopupMenu();
    }

    public void OkButtonOnClick(View View) {
        AddCostsToDataBase();
    }

    public void AddCostsToDataBase() {
        String inputTextString = inputTextField.getText().toString();
        if (!inputTextString.equals("")) {
            BigDecimal bg = new BigDecimal(inputTextString);
            bg = bg.add(new BigDecimal(currentCosts)).setScale(2, BigDecimal.ROUND_HALF_UP);

            db.addCosts(currentMonth + 1, currentYear, costType, bg.toString());
            db.addLastValues(costType, inputTextString);
            inputTextField.setText("");

            GenerateCostsPopupMenu();
        }
    }

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
}
