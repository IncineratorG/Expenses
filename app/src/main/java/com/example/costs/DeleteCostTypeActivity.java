package com.example.costs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DeleteCostTypeActivity extends AppCompatActivity {

    TextView deleteCostTypeTextView;
    String costTypeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_cost_type);

        Bundle dataFromMainActivity = getIntent().getExtras();
        if (dataFromMainActivity == null)
            return;

        costTypeName = dataFromMainActivity.getString("costType");

        deleteCostTypeTextView = (TextView) findViewById(R.id.deleteCostTypeTextView);
        deleteCostTypeTextView.setText("Удалить категорию '" + costTypeName + "' из программы?");
    }


    public void deleteButtonOnClick(View view) {
        CostsDataBase db = new CostsDataBase(this, null, null, 1);
        db.deleteCostType(costTypeName);

        Toast costTypeDeletedToast = Toast.makeText(this, "Категория '" + costTypeName + "' удалена.", Toast.LENGTH_LONG);
        costTypeDeletedToast.show();

        Intent mainScreenIntent = new Intent(this, MainActivity.class);
        mainScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainScreenIntent);
    }

    public void cancelDeleteButtonOnClick(View view) {
        Intent mainScreenIntent = new Intent(this, MainActivity.class);
        mainScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainScreenIntent);
    }

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
