package com.example.newcosts;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    private Activity activity;
    private Button editButton, deleteButton, cancelButton;
    private String dataString;

    public CustomDialogClass(Activity activity, String dataString) {
        super(activity);

        this.activity = activity;
        this.dataString = dataString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_cost_value_dialog);

        Long milliseconds = Long.parseLong(dataString.substring(dataString.lastIndexOf(Constants.SEPARATOR_MILLISECONDS) + 1));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
//        String dateString = dateFormat.format(calendar.getTime());

        editButton = (Button) findViewById(R.id.edit_cost_value_dialog_editButton);
        editButton.setOnClickListener(this);
        deleteButton = (Button) findViewById(R.id.edit_cost_value_dialog_deleteButton);
        deleteButton.setOnClickListener(this);
        cancelButton = (Button) findViewById(R.id.edit_cost_value_dialog_cancelButton);
        cancelButton.setOnClickListener(this);

        String costName = dataString.substring(dataString.indexOf(" ") + 1, dataString.lastIndexOf(":"));
        String costValue = dataString.substring(dataString.indexOf(":") + 2, dataString.lastIndexOf(Constants.SEPARATOR_MILLISECONDS));

        TextView costValueTextView = (TextView) findViewById(R.id.edit_cost_value_dialog_costValue);
        costValueTextView.setText(costValue);
        TextView costNameTextView = (TextView) findViewById(R.id.edit_cost_value_dialog_costName);
        costNameTextView.setText(costName);
        TextView costDateTextView = (TextView) findViewById(R.id.edit_cost_value_dialog_costDate);
        costDateTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                 String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                 Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)] + ", " +
                                 String.valueOf(calendar.get(Calendar.YEAR)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_cost_value_dialog_editButton:
                Intent editCostsIntent = new Intent(activity, EditCostsActivity.class);
                editCostsIntent.putExtra("data", dataString);
                activity.startActivity(editCostsIntent);
                dismiss();

                break;
            case R.id.edit_cost_value_dialog_deleteButton:
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(activity);
                aBuilder.setNegativeButton("Отмена", null);
                aBuilder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CostsDB db = CostsDB.getInstance(activity);
                        db.removeCostValue(Long.parseLong(dataString.substring(dataString.lastIndexOf(Constants.SEPARATOR_MILLISECONDS) + 1)));
                        dismiss();
                    }
                });
                aBuilder.setMessage("Удалить запись?");

                AlertDialog dialog = aBuilder.create();
                dialog.show();

                TextView dialogText = (TextView) dialog.findViewById(android.R.id.message);
                dialogText.setGravity(Gravity.CENTER);

                break;
            case R.id.edit_cost_value_dialog_cancelButton:
                dismiss();
                break;
            default:
                break;
        }
    }
}
