package com.example.newcosts;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditCostsActivity extends AppCompatActivity implements MyDatePicker.MyDatePickerCallback {

    private String date;
    private String categoryName;
    private String costSum;
    private String dateInMilliseconds;
    private List<String> availableCostNamesList;
    private NumberFormat numberFormat;
    private CostsDB db;

    Spinner availableCostNamesSpinner;
    Dialog currentDialog;
    EditText inputDataEditText, dateEditText, costSumEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_costs);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Редактирование");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF9800")));

//        date = "3.05.1989";
//        categoryName = "First";
//        costSum = "125800.25 руб.";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.mm.yyyy");
//        try {
//            Date d = simpleDateFormat.parse(date);
//            dateInMilliseconds = String.valueOf(d.getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


        numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(false);

        String dataString = "none";
        Bundle data = getIntent().getExtras();
        if (data != null)
            dataString = data.getString("data");

        db = CostsDB.getInstance(this);
        long milliseconds = Long.parseLong(dataString.substring(dataString.lastIndexOf(Constants.SEPARATOR_MILLISECONDS) + 1));
        String dbData = db.getCostByDateInMillis(milliseconds);
        String[] dataArr = dbData.split("%");

        if (dataArr != null && dataArr.length == 6) {
            categoryName = dataArr[0];
            costSum = dataArr[1] + " руб.";
            date = dataArr[2] + "." + dataArr[3] + "." + dataArr[4];
            dateInMilliseconds = dataArr[5];
        } else finish();

//        System.out.println(dateInMilliseconds);

        // Получаем список всех используемых категорий расходов и их id_n
        availableCostNamesList = db.getActiveCostNames();
        String[] availableCostNames = db.getActiveCostNames_V2();
//        for (String s : availableCostNames)
//            System.out.println(s);

        // Формируем массив, состоящий только из названий используемых
        // категорий расходов
        String[] availableCostNamesArray = new String[availableCostNamesList.size()];
        int k = 0;
        for (String s : availableCostNamesList) {
            availableCostNamesArray[k] = s.substring(s.indexOf("$") + 1);
            ++k;
        }

        // Помещаем выбранную категрию расходов на первое место
        // в масисве названий используемых категорий расходов
        if (!availableCostNamesArray[0].equals(categoryName)) {
            for (int i = 0; i < availableCostNamesArray.length; ++i) {
                if (availableCostNamesArray[i].equals(categoryName)) {
                    availableCostNamesArray[i] = availableCostNamesArray[0];
                    break;
                }
            }
            availableCostNamesArray[0] = categoryName;
        }

        // Заполняем выпадающий список назвниями используемых статей расходов
        availableCostNamesSpinner = (Spinner) findViewById(R.id.editCosts_AvailableCostNames);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, availableCostNamesArray);
        availableCostNamesSpinner.setAdapter(adapter);

        // При нажатии на дату внесения значения по статье расходов,
        // появляется возможность изменения даты внесения значения расходов
        dateEditText = (EditText) findViewById(R.id.editCosts_costDate);
        dateEditText.setText(date);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatePicker datePicker = new MyDatePicker(EditCostsActivity.this);
                datePicker.show();
            }
        });

        // При нажатии на сумму расходов, появляется диалоговое окно,
        // в котором можно изменить сумму
        costSumEditText = (EditText) findViewById(R.id.editCosts_costSum);
        costSumEditText.setText(costSum);
        costSumEditText.setCursorVisible(false);
        costSumEditText.setFocusable(false);
        costSumEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(EditCostsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.edit_cost_sum_popup);
                currentDialog = dialog;

                inputDataEditText = (EditText) dialog.findViewById(R.id.editCostsPopup_inputTextField);
                inputDataEditText.setFilters(new DecimalDigitsInputFilter[] {new DecimalDigitsInputFilter()});
                inputDataEditText.setCursorVisible(false);
                inputDataEditText.setText(costSumEditText.getText().toString().substring(0, costSumEditText.getText().toString().indexOf(" ")));

                dialog.show();
            }
        });
    }


/*================================== Слушатели ==============================================*/

    // Обработчик нажатий кнопок в edit_cost_sum_popup (диалог редактирования значений расходов)
    public void OnEditCostSumPopupClickListener(View view) {
        Button pressedButton = (Button) view;
        String buttonLabel = (String)pressedButton.getText();

        if (inputDataEditText == null) {
            inputDataEditText = (EditText) currentDialog.findViewById(R.id.editCostsPopup_inputTextField);
            inputDataEditText.setFilters(new DecimalDigitsInputFilter[]{new DecimalDigitsInputFilter()});
            inputDataEditText.setCursorVisible(false);
        }

        switch (view.getId()){
            case R.id.editCostsPopup_zero:
            case R.id.editCostsPopup_one:
            case R.id.editCostsPopup_two:
            case R.id.editCostsPopup_three:
            case R.id.editCostsPopup_four:
            case R.id.editCostsPopup_five:
            case R.id.editCostsPopup_six:
            case R.id.editCostsPopup_seven:
            case R.id.editCostsPopup_eight:
            case R.id.editCostsPopup_nine:
                inputDataEditText.append(buttonLabel);
                break;

            case R.id.editCostsPopup_dot: {
                String inputText = String.valueOf(inputDataEditText.getText());
                if (!inputText.contains(".")) {
                    inputDataEditText.append(".");
                }
                break;
            }

            case R.id.editCostsPopup_del: {
                String inputText = String.valueOf(inputDataEditText.getText());
                if (inputText != null && inputText.length() != 0) {
                    inputText = inputText.substring(0, inputText.length() - 1);
                    inputDataEditText.setText(inputText);
                }
                break;
            }

            case R.id.editCostsPopup_ok: {
                String inputText = String.valueOf(inputDataEditText.getText());
                if (inputText != null && inputText.length() != 0 && !".".equals(inputText)) {
                    Double enteredCostValue = Double.parseDouble(inputText);
                    costSumEditText.setText(numberFormat.format(enteredCostValue) + " руб.");
                    currentDialog.cancel();
                }
                break;
            }

            case R.id.editCostsPopup_cancelButton:
                currentDialog.cancel();
                break;

        }
    }

    // Обработчик нажатий кнопок сохранения введённых значений и отмены редактирования
    public void OnSaveCancelButtonsClick(View view) {
        switch (view.getId()) {
            case R.id.editCosts_saveButton:
                String chosenCategoryName = availableCostNamesSpinner.getSelectedItem().toString();

                // Получаем id_n по названию выбранной категории расходов
                int id_n = -1;
                for (String s : availableCostNamesList) {
                    if (s.contains(chosenCategoryName)) {
                        id_n = Integer.parseInt(s.substring(0, s.indexOf("$")));
                        break;
                    }
                }
                if (id_n == -1) {
                    Toast idErrorToast = Toast.makeText(this, "ERROR RETRIEVING ID_N", Toast.LENGTH_LONG);
                    idErrorToast.show();
                    return;
                }

                // Получаем значение введённой суммы расходов
                String enteredCostSum = costSumEditText.getText().toString().substring(0, costSumEditText.getText().toString().indexOf(" "));

                // Получаем выбранную дату
                String enteredDate = dateEditText.getText().toString();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                long enteredDateInMilliseconds = -1;
                try {
                    Date d = simpleDateFormat.parse(enteredDate);
                    enteredDateInMilliseconds = d.getTime();
                } catch (ParseException e) {
                    Toast invalidDataErrorToast = Toast.makeText(this, "Неправильная дата", Toast.LENGTH_LONG);
                    invalidDataErrorToast.show();
                    return;
                }

                db.removeCostValue(Long.valueOf(dateInMilliseconds));
                db.addCostInMilliseconds(id_n, enteredCostSum, enteredDateInMilliseconds);

                Toast recordEditedSuccessfulToast = Toast.makeText(this, "Запись изменена", Toast.LENGTH_LONG);
                recordEditedSuccessfulToast.show();

                break;

            case R.id.editCosts_cancelButton:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

/*================================================================================================*/



    @Override
    public void getPickedDate(String pickedDate) {
        dateEditText.setText(pickedDate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
