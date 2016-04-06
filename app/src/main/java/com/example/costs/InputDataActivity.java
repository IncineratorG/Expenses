package com.example.costs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;

public class InputDataActivity extends AppCompatActivity {

    EditText inputTextField;
    TextView costTypeTextView;
    Button costValueButton;
    TextView dateTextView;

    int currentDay;
    int currentMonth;                                       // Начинается с нуля
    int currentYear;

    String costType;
    String currentCosts;

    String[] lastEnteredValues;

    CostsDataBase db;

    NumberFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);

        db = new CostsDataBase(this, null, null, 1);

        format = NumberFormat.getInstance();
        format.setGroupingUsed(false);

        Bundle bundleData = getIntent().getExtras();
        if (bundleData == null)
            return;

        currentDay = (int) bundleData.get("currentDay");
        currentMonth = (int) bundleData.get("currentMonth");
        currentYear = (int) bundleData.get("currentYear");
        costType = (String) bundleData.get("costType");
        currentCosts = format.format(db.getCostValue(-1, currentMonth, currentYear, costType));

        inputTextField = (EditText) findViewById(R.id.inputTextField);
        /*
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
        */
        inputTextField.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        dateTextView = (TextView) findViewById(R.id.date);
        dateTextView.setText(currentDay + " " + MainActivity.declensionMonthNames[currentMonth] + " " + currentYear);

        costTypeTextView = (TextView) findViewById(R.id.costsType);
        costTypeTextView.setText(costType);

        costValueButton = (Button) findViewById(R.id.costValue);
        costValueButton.setText(currentCosts + " руб.");

        // Создаём контекстное меню для просмотра и удаления последних введённых значений
        registerForContextMenu(costValueButton);
    }


    // Принажатии на кнопку "OK" происходит добавление
    public void OkButtonOnClick(View View) {
        AddCostsToDataBase();
    }

    public void AddCostsToDataBase() {
        String inputTextString = inputTextField.getText().toString();
        if (!inputTextString.isEmpty() && !".".equals(inputTextString)) {
            db.addCosts(Double.valueOf(inputTextString), costType);

            inputTextField.setText("");
            currentCosts = format.format(db.getCostValue(-1, currentMonth, currentYear, costType));

            costValueButton.setText(String.valueOf(currentCosts) + " руб.");
        }
    }

    // Обработка нажатий эранной клавиатуры
    public void OnKeyboardButtonClick(View view) {
        Button b = (Button) view;
        System.out.println(b.getText());
    }




    // ---------------------- Логика работы контекстного меню --------------------------------------
    // Создание контекстного меню, содержащего последние введённые значения
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        lastEnteredValues = db.getLastThirtyEntriesWithMilliseconds();

        for (int i = 0; i < lastEnteredValues.length; ++i) {
            menu.add(1, i + 1, i + 1, lastEnteredValues[i].substring(0, lastEnteredValues[i].indexOf("%")));
        }

    }

    // При нажатии на элементе контекстного меню происходит удаление этого элемента
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String textLine = item.toString();

        final int itemPositionInLastEnteredValuesArray = item.getItemId() - 1;

        // Диалоговое окно, запрашивающее подтверждение на удаление
        // выбранного элемента контекстного меню. При нажатии на кнопку "Удалить"
        // происходит удаление выбранного элемента из базы данных и обновление
        // текущей суммы расходов по данной категории
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(InputDataActivity.this);
        adBuilder.setNegativeButton("Отмена", null);
        adBuilder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long chosenEntryDateInMilliseconds =
                        Long.parseLong(lastEnteredValues[itemPositionInLastEnteredValuesArray].substring(lastEnteredValues[itemPositionInLastEnteredValuesArray].lastIndexOf("%") + 1));

                int result = db.removeValue(chosenEntryDateInMilliseconds);
                currentCosts = format.format(db.getCostValue(-1, currentMonth, currentYear, costType));
                costValueButton.setText(currentCosts + " руб.");
            }
        });
        adBuilder.setMessage(textLine);

        AlertDialog dialog = adBuilder.create();
        dialog.show();

        TextView dialogText = (TextView) dialog.findViewById(android.R.id.message);
        dialogText.setGravity(Gravity.CENTER);


        return super.onContextItemSelected(item);
    }
    // ---------------------------------------------------------------------------------------------







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
