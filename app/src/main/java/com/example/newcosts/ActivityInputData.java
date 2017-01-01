package com.example.newcosts;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;


public class ActivityInputData extends AppCompatActivity implements MyDatePicker.MyDatePickerCallback {

    private TextView signTextView;
    private EditText inputValueEditText, inputNoteEditText;
    private String previousValueString;
    private LinearLayout inputValueEditTextCursor;
    private boolean previousTokenWasPlus;
    private int costID;
    private static final long CURRENT_DAY = -1;
    private static final long PREVIOUS_DAY = -2;
    private boolean hasStoredValue = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);

        // Получаем данные о выбранной статье расходов
        Bundle expenseDataBundle = getIntent().getExtras();
        if (expenseDataBundle == null)
            return;

        String[] expenseDataArray = expenseDataBundle.getStringArray(Constants.DATA_ARRAY_LABEL);
        if (expenseDataArray == null || expenseDataArray.length != Constants.DATA_ARRAY_SIZE)
            return;

        String costNameString = expenseDataArray[Constants.COST_NAME_INDEX];
        String costIdString = expenseDataArray[Constants.COST_ID_INDEX];
        costID = Integer.parseInt(costIdString);
        String costValueString = expenseDataArray[Constants.COST_VALUE_INDEX];
        String costNoteString = expenseDataArray[Constants.COST_NOTE_INDEX];

        // Инициализируем элементы интерфейса
        signTextView = (TextView) findViewById(R.id.activity_input_data_sign_textview);
        signTextView.setText("");

        inputValueEditTextCursor = (LinearLayout) findViewById(R.id.activity_input_data_edit_text_cursor);
        inputValueEditTextCursor.setAnimation(startBlinking());

        inputValueEditText = (EditText) findViewById(R.id.activity_input_data_input_value_edittext);
        inputValueEditText.setFilters(new DecimalDigitsInputFilter[] {new DecimalDigitsInputFilter()});
        inputNoteEditText = (EditText) findViewById(R.id.activity_input_data_input_note_edittext);
        if (costNoteString != null && costNoteString.length() > 0) {
            inputNoteEditText.setText(costNoteString);
            inputNoteEditText.setSelection(inputNoteEditText.getText().length());
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(costNameString + ": " + costValueString + " руб.");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Constants.HEADER_SYSTEM_COLOR));
//        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_input_data_toolbar);
//        setSupportActionBar(toolbar);

        previousValueString = "";
        previousTokenWasPlus = false;
    }

    // Обработчик нажатий кнопок выбора даты внесения расходов
    public void onDateButtonsClick(View view) {
        switch (view.getId()) {
            case R.id.activity_input_data_date_yesterday_button:
                calculateInputValues();
                saveData(PREVIOUS_DAY);
                break;
            case R.id.activity_input_data_date_today_button:
                calculateInputValues();
                saveData(CURRENT_DAY);
                break;
            case R.id.activity_input_data_choose_date_button:
                calculateInputValues();
                MyDatePicker datePicker = new MyDatePicker(ActivityInputData.this);
                datePicker.show();
                break;
        }
    }

    // Обработчик нажатий кнопок цифровой клавиатуры
    public void onKeyboardClick(View view) {
        Button pressedButton = (Button) view;
        String buttonLabel = String.valueOf(pressedButton.getText());
        String inputTextString = "";

        // Если после нажатия на "+" снова нажимаем на "+" (2++++ и т.д.)
        if (view.getId() == R.id.activity_input_data_add && previousTokenWasPlus)
            return;

        // Если после нажатия на "+" вводим другое число
        if (previousTokenWasPlus && !(view.getId() == R.id.activity_input_data_equal)) {
            inputValueEditText.setText("");
            previousTokenWasPlus = false;
        }

        switch (view.getId()) {
            case R.id.activity_input_data_zero:
                inputTextString = String.valueOf(inputValueEditText.getText());
                if (!"0".equals(inputTextString))
                    inputValueEditText.append(buttonLabel);
                break;
            case R.id.activity_input_data_one:
            case R.id.activity_input_data_two:
            case R.id.activity_input_data_three:
            case R.id.activity_input_data_four:
            case R.id.activity_input_data_five:
            case R.id.activity_input_data_six:
            case R.id.activity_input_data_seven:
            case R.id.activity_input_data_eight:
            case R.id.activity_input_data_nine:
                inputTextString = String.valueOf(inputValueEditText.getText());
                if ("0".equals(inputTextString))
                    inputValueEditText.setText(buttonLabel);
                else
                    inputValueEditText.append(buttonLabel);
                break;
            case R.id.activity_input_data_dot:
                inputTextString = String.valueOf(inputValueEditText.getText());
                if (!inputTextString.contains("."))
                    inputValueEditText.append(".");
                break;
            case R.id.activity_input_data_del:
                inputTextString = String.valueOf(inputValueEditText.getText());
                if (inputTextString.length() != 0) {
                    inputTextString = inputTextString.substring(0, inputTextString.length() - 1);
                    inputValueEditText.setText(inputTextString);
                    inputValueEditText.setSelection(inputValueEditText.getText().length());
                }
                break;
            case R.id.activity_input_data_add:
                signTextView.setText("+");
                previousTokenWasPlus = true;
                // Если после ввода следующего значения  был снова нажат "+" (1+2+)
                if (hasStoredValue) {
                    // Складываем введённые значения
                    calculateInputValues();

                    // Устанавливаем полученное значение как предыдущее (так как после символа "+"
                    // ожидается ввод нового значения, с которым нужно будет сложить полученное)
                    previousValueString = inputValueEditText.getText().toString();
                    hasStoredValue = true;
                } else {
                    previousValueString = inputValueEditText.getText().toString();
                    hasStoredValue = true;
                }
                break;
            case R.id.activity_input_data_equal:
                if (hasStoredValue) {
                    signTextView.setText("");
                    previousTokenWasPlus = false;

                    // Складываем введённые значения
                    calculateInputValues();

                    // Предыдущего значения больше нет, так как оно было сложено
                    // с текущим и выведено на экран
                    hasStoredValue = false;
                    previousValueString = "";
                }
                break;
            case R.id.activity_input_data_ok:
                calculateInputValues();
                saveData(CURRENT_DAY);
                break;
        }
    }

    // Складывает текущее введённое значение с предыдущим
    private void calculateInputValues() {
        // Получаем и форматируем текущее и предыдущее введынные значения
        String currentInputEditTextValueString = inputValueEditText.getText().toString();
        if ("".equals(currentInputEditTextValueString) || ".".equals(currentInputEditTextValueString))
            currentInputEditTextValueString = "0";
        if ("".equals(previousValueString) || ".".equals(previousValueString))
            previousValueString = "0";

        double currentValue = 0.0;
        double previousValue = 0.0;

        // Приводим текущее и предыдущее значения к типу "double" и находим их сумму
        try {
            currentValue = Double.parseDouble(currentInputEditTextValueString);
            previousValue = Double.parseDouble(previousValueString);
        } catch (NumberFormatException e) {
            showAlertDialogWithMessage("Что-то пошло не так");
            return;
        }
        currentValue = currentValue + previousValue;

        // Вставляем полученное значение в поле ввода значений
        inputValueEditText.setText(Constants.formatDigit(currentValue));
        inputValueEditText.setSelection(inputValueEditText.getText().length());
    }

    // Сохраняет введённое значение в базу
    private boolean saveData(long milliseconds) {
        String inputValueString = inputValueEditText.getText().toString();
        String inputNoteString = inputNoteEditText.getText().toString();
        CostsDB cdb = CostsDB.getInstance(this);
        double inputValue = 0.0;

        try {
            inputValue = Double.parseDouble(inputValueString);
        } catch (NumberFormatException e) {
            showAlertDialogWithMessage("Что-то пошло не так");
            return false;
        }

        // Если введённое значение = 0 - не сохраняем его
        if (Double.compare(inputValue, 0.0) == 0) {
            showAlertDialogWithMessage("Введите значение");
            return false;
        }

        if (milliseconds == CURRENT_DAY) {
            cdb.addCosts(inputValue, costID, inputNoteString);
            returnToPreviousActivity(inputValueString);
        } else if (milliseconds == PREVIOUS_DAY) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            long millis = calendar.getTimeInMillis();
            cdb.addCostInMilliseconds(costID, inputValueString, millis, inputNoteString);
            returnToPreviousActivity(inputValueString);
        } else {
            cdb.addCostInMilliseconds(costID, inputValueString, milliseconds, inputNoteString);
            returnToPreviousActivity(inputValueString);
        }

        return true;
    }

    // Показ всплывающего окна при некорректном вводе данных
    private void showAlertDialogWithMessage(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(message);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton("Ok", null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    // После сохранения введённого значения возвращаемся к предыдущему экрану
    private void returnToPreviousActivity(String savedValue) {
        // Возвращаемся на главный экран приложения
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("result", true);
        intent.putExtra("value", savedValue);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    // Анимация курсора ввода значения расходов
    private Animation startBlinking(){
        Animation fadeIn = new AlphaAnimation(1, 0);
        fadeIn.setInterpolator(new LinearInterpolator());
        fadeIn.setDuration(1000);
        fadeIn.setRepeatCount(-1);

        return fadeIn;
    }


    @Override
    public void getPickedDate(String pickedDate) {
        String[] pickedDateArray = pickedDate.split("\\.");

        int pickedDay = Integer.valueOf(pickedDateArray[0]);
        int pickedMonth = Integer.valueOf(pickedDateArray[1]);
        int pickedYear = Integer.valueOf(pickedDateArray[2]);

        Calendar calendar = Calendar.getInstance();
        long currentTimeInMilliseconds = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH, pickedDay);
        calendar.set(Calendar.MONTH, pickedMonth - 1);
        calendar.set(Calendar.YEAR, pickedYear);
        final long pickedTimeInMilliseconds = calendar.getTimeInMillis();

        if (pickedTimeInMilliseconds > currentTimeInMilliseconds) {
            String message = "Выбранная дата ещё не наступила";
            showAlertDialogWithMessage(message);
        } else {
            saveData(pickedTimeInMilliseconds);
        }
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
