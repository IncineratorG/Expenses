package com.example.costs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewCostTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_cost_type);
    }

    public void addNewCostTypeButtonOnClick(View view) {
        CostsDataBase db = new CostsDataBase(this, null, null, 1);
        EditText newCostTypeInputField = (EditText) findViewById(R.id.newCostType);
        String newCostTypeName = newCostTypeInputField.getText().toString();

        if (!newCostTypeName.equals("")) {
            db.addCosts(0.0, newCostTypeName);

            Toast newCostTypeAddedToast = Toast.makeText(this, "Категория '" + newCostTypeName + "' создана.", Toast.LENGTH_LONG);
            newCostTypeAddedToast.show();

            Intent mainScreenIntent = new Intent(this, MainActivity.class);
            mainScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainScreenIntent);
        } else {
            Toast invalidCostTypeNameToast = Toast.makeText(this, "Введите название категории", Toast.LENGTH_LONG);
            invalidCostTypeNameToast.show();
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
}
