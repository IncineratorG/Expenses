package com.example.newcosts;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class CostSumInputDialog extends Dialog implements
        android.view.View.OnClickListener {


    Activity activity;
    Button zero, one, two, three, four, five, six, seven, eight, nine,
           dot, takePhoto, addText, ok, delete, cancel;
    EditText inputTextField;
    String dataToCorrect;


    public CostSumInputDialog(Activity activity, String data) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.input_data_popup_test);
//
//        zero = (Button) findViewById(R.id.zero);
//        zero.setOnClickListener(this);
//        one = (Button) findViewById(R.id.one);
//        one.setOnClickListener(this);
//        two = (Button) findViewById(R.id.two);
//        two.setOnClickListener(this);
//        three = (Button) findViewById(R.id.three);
//        three.setOnClickListener(this);
//        four = (Button) findViewById(R.id.four);
//        four.setOnClickListener(this);
//        five = (Button) findViewById(R.id.five);
//        five.setOnClickListener(this);
//        six = (Button) findViewById(R.id.six);
//        six.setOnClickListener(this);
//        seven = (Button) findViewById(R.id.seven);
//        seven.setOnClickListener(this);
//        eight = (Button) findViewById(R.id.eight);
//        eight.setOnClickListener(this);
//        nine = (Button) findViewById(R.id.nine);
//        nine.setOnClickListener(this);
//        dot = (Button) findViewById(R.id.dot);
//        dot.setOnClickListener(this);
//        takePhoto = (Button) findViewById(R.id.takePhoto);
//        takePhoto.setOnClickListener(this);
//        addText = (Button) findViewById(R.id.addTextButton);
//        addText.setOnClickListener(this);
//        ok = (Button) findViewById(R.id.ok);
//        ok.setOnClickListener(this);
//        deleteButton = (Button) findViewById(R.id.del);
//        deleteButton.setOnClickListener(this);
//        cancelButton = (Button) findViewById(R.id.cancelButton);
//        cancelButton.setOnClickListener(this);
//
//        inputTextField = (EditText) findViewById(R.id.inputTextFieldEditTextInInputDataPopup);
//        inputTextField.setFilters(new DecimalDigitsInputFilter[] {new DecimalDigitsInputFilter()});
//        inputTextField.setCursorVisible(false);
    }

    @Override
    public void onClick(View v) {
//        Button pressedButton = (Button) v;
//        String buttonLabel = (String) pressedButton.getText();
//
//        switch (v.getId()) {
//            case R.id.zero:
//            case R.id.one:
//            case R.id.two:
//            case R.id.three:
//            case R.id.four:
//            case R.id.five:
//            case R.id.six:
//            case R.id.seven:
//            case R.id.eight:
//            case R.id.nine:
//                inputTextField.append(buttonLabel);
//                break;
//
//            case R.id.dot: {
//                String inputText = String.valueOf(inputTextField.getText());
//                if (!inputText.contains(".")) {
//                    inputTextField.append(".");
//                }
//                break;
//            }
//
//            case R.id.del: {
//                String inputText = String.valueOf(inputTextField.getText());
//                if (inputText != null && inputText.length() != 0) {
//                    inputText = inputText.substring(0, inputText.length() - 1);
//                    inputTextField.setText(inputText);
//                }
//                break;
//            }
//
//            case R.id.ok: {
////                String inputText = String.valueOf(inputTextField.getText());
////                if (inputText != null && inputText.length() != 0 && !".".equals(inputText)) {
////                    Double enteredCostValue = Double.parseDouble(inputText);
////
////                    cdb.addCosts(enteredCostValue, chosenCostNameId);
////
////                    // Обновляем главный экран приложения (MainActivity)
////                    SetCurrentOverallCosts();
////                    CreateListViewContent();
////
////                    inputDataEditText.setText("");
////                }
//                break;
//            }
//
//            case R.id.takePhotoButton:
//                System.out.println("Take photo");
//                break;
//
//            case R.id.addTextButton:
//                System.out.println("Add text");
//                break;
//
//            case R.id.cancelButton:
//                this.cancelButton();
//                break;
//        }
    }
}
