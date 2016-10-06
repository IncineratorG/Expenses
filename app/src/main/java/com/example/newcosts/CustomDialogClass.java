package com.example.newcosts;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity activity;
    public Dialog d;
    public Button edit, delete, cancel;
    public String dataString;

    public CustomDialogClass(Activity activity, String dataString) {
        super(activity);

        this.activity = activity;
        this.dataString = dataString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        edit = (Button) findViewById(R.id.edit_btn);
        delete = (Button) findViewById(R.id.delete_btn);
        cancel = (Button) findViewById(R.id.cancel_btn);

        TextView textView = (TextView) findViewById(R.id.dialog_txtView);
        textView.setText(dataString.substring(0, dataString.indexOf("%")));

        edit.setOnClickListener(this);
        delete.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_btn:
                Intent editCostsIntent = new Intent(activity, EditCostsActivity.class);
                editCostsIntent.putExtra("data", dataString);
                activity.startActivity(editCostsIntent);

                break;
            case R.id.delete_btn:
                CostsDB db = new CostsDB(activity, null, null, 1);
                db.removeCostValue(Long.parseLong(dataString.substring(dataString.indexOf("%") + 1)));

                dismiss();
                break;
            case R.id.cancel_btn:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
