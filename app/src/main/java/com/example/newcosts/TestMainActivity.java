package com.example.newcosts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class TestMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);

        CostsDB db = new CostsDB(this, null, null, 1);
//        db.addNewCostName("First");
//        db.addNewCostName("Second");
//        db.addCosts(100.0, "First");
//        db.addCosts(125.0, "First");
//        db.addCosts(200.52, "Second");

//        List<String> listOfCostDB = db.getAllCostsValuesTable();
//        System.out.println(listOfCostDB.size());
//        for (String s : listOfCostDB)
//            System.out.println(s);
    }
}
