package com.example.costs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;

public class InputDataActivity extends AppCompatActivity {
    static final String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    CostsDB db;

    EditText inputTextField;
    TextView costsTypeTextView;
    TextView dateTextView;

    int currentMonth;                                       // Начинается с нуля
    int currentYear;

    String costTypeEnum;

    Double currentCosts;

    CostsDB.CostType costType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);

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

        System.out.println(costType);

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
        db = new CostsDB(this, null, null, 1);

        String currentDate = monthNames[currentMonth] + " " + currentYear;
        dateTextView.setText(currentDate);

        costsTypeTextView = (TextView) findViewById(R.id.costsType);
        costsTypeTextView.setText(bundleData.getString("costTypeText"));
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
            inputTextField.setText("");
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent mainScreenIntent = new Intent(this, MainActivity.class);
            startActivity(mainScreenIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
