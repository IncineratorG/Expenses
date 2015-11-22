package com.example.costs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    static final String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    static final String[] declensionMonthNames = {"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};

    CostsDB db;

    int currentYear;
    int currentMonth;                            // Начинается с нуля
    int currentDay;
    int chosenYear;
    int chosenMonth;
    int chosenDay;

    boolean notCurrentDate;

    TextView currentDayAndMonthTextView;
    TextView currentYearTextView;
    TextView currentCostsTextView;

    String currentOverallCosts;
    String currentFoodCosts;
    String currentClothesCosts;
    String currentCommunalCosts;
    String currentElectronicsCosts;
    String currentTransportCosts;
    String currentGoodsCosts;
    String currentServicesCosts;
    String currentOthersCosts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Получаем доступ к базе данных
        if (db == null)
            db = new CostsDB(this, null, null, 1);

        // Инициализируем поля, содержащие выбранные день, месяц и год
        if (currentDayAndMonthTextView == null)
            currentDayAndMonthTextView = (TextView) findViewById(R.id.currentDayAndMonth);
        if (currentYearTextView == null)
            currentYearTextView = (TextView) findViewById(R.id.currentYear);

        // Инициализируем пооле, отображающее текущие расходы
        if (currentCostsTextView == null)
            currentCostsTextView = (TextView) findViewById(R.id.currentCosts);

        // Устанавливаем текущую дату
        SetCurrentDate();

        // Устанавливаем расходы за текущий месяц
        SetCurrentCosts();

        String[] costsArray = new String[8];
        costsArray[0] = "Еда$" + currentFoodCosts + "#FOOD";
        costsArray[1] = "Промтовары$" + currentGoodsCosts + "#GOODS";
        costsArray[2] = "Коммунальные платежи$" + currentCommunalCosts + "#COMMUNAL_RENT";
        costsArray[3] = "Одежда$" + currentClothesCosts + "#CLOTHES";
        costsArray[4] = "Услуги$" + currentServicesCosts + "#SERVICES";
        costsArray[5] = "Транспорт$" + currentTransportCosts + "#TRANSPORT";
        costsArray[6] = "Техника$" + currentElectronicsCosts + "#ELECTRONICS";
        costsArray[7] = "Другое$" + currentOthersCosts + "#OTHERS";

        ListAdapter listAdapter = new CostsAdapter(this, costsArray);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent inputDataIntent = new Intent(MainActivity.this, InputDataActivity.class);

                        String textLine = String.valueOf(parent.getItemAtPosition(position));
                        String costsTypeText = textLine.substring(0, textLine.indexOf("$"));
                        inputDataIntent.putExtra("costTypeText", costsTypeText);
                        String costTypeEnum = textLine.substring(textLine.indexOf("#") + 1);
                        inputDataIntent.putExtra("costTypeEnum", costTypeEnum);
                        inputDataIntent.putExtra("currentMonth", currentMonth);
                        inputDataIntent.putExtra("currentYear", currentYear);
                        inputDataIntent.putExtra("currentCosts", textLine.substring(textLine.indexOf("$") + 1, textLine.indexOf("#")));

                        startActivity(inputDataIntent);
                    }
                }
        );




/*

        // Загрузка данных из базы
        List<String> listOfCostsTypes = new ArrayList<>(3);
        listOfCostsTypes.add("Еда");
        listOfCostsTypes.add("Квартплата");
        listOfCostsTypes.add("Одежда");

        List<Double> listOfCostValues = new ArrayList<>(3);
        listOfCostValues.add(1.0);
        listOfCostValues.add(2.0);
        listOfCostValues.add(3.0);
        ////////////////////////////





        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {

            listOfCostValues.toArray(costsValues);
            listOfCostsTypes.toArray(costsTypes);

            for (int i = 0; i < costsTypes.length; ++i) {
                costsTypes[i] = costsTypes[i] + "$" + costsValues[i];
            }
        } else {
            for (int i = 0; i < costsTypes.length; ++i) {
                String bundleString = null;
                try {
                    bundleString = bundle.getString(costsTypes[i].substring(0, costsTypes[i].indexOf("$")));
                } catch (Exception e) {}
                if (bundleString != null) {
                    costsTypes[i] = costsTypes[i].substring(0, costsTypes[i].indexOf("$")) + "$" + bundleString;
                }
            }
        }


        /////////////////////////////////////////////////
        double[] valuesArray = {1.0, 2.0, 3.0};
        String[] records = {"Еда", "Квартплата", "Одежда"};

        for (int i = 0; i < records.length; ++i) {
            records[i] = records[i] + "$" + valuesArray[i];
        }
        ////////////////////////////////////////////////////


        ListAdapter listAdapter = new CostsAdapter(this, costsTypes);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent inputDataIntent = new Intent(MainActivity.this, InputDataActivity.class);

                        String textLine = String.valueOf(parent.getItemAtPosition(position));
                        String costsType = textLine.substring(0, textLine.indexOf("$"));
                        inputDataIntent.putExtra("costsType", costsType);

                        startActivity(inputDataIntent);



                        //String costsType = String.valueOf(parent.getItemAtPosition(position));
                        //Toast.makeText(MainActivity.this, costsType, Toast.LENGTH_SHORT).show();

                    }
                }
        );*/
    }




    // Устанавливает текущие дату и время
    public void SetCurrentDate() {
        Calendar c = Calendar.getInstance();
        currentYear = chosenYear = c.get(Calendar.YEAR);
        currentMonth = chosenMonth = c.get(Calendar.MONTH);
        currentDay = chosenDay = c.get(Calendar.DAY_OF_MONTH);

        currentDayAndMonthTextView.setText(currentDay + "  " + declensionMonthNames[currentMonth]);
        currentYearTextView.setText("  " + String.valueOf(currentYear));

        notCurrentDate = false;
    }

    // Устанавливаем текущие расходы
    public void SetCurrentCosts() {
        double total = 0.0;
        currentFoodCosts = db.getCosts(currentMonth + 1, currentYear, CostsDB.CostType.FOOD);
        if (currentFoodCosts.equals("error")) {
            currentFoodCosts = "0.00";
            db.addCosts(currentMonth + 1, currentYear, CostsDB.CostType.FOOD, currentFoodCosts);
        }
        total = total + Double.valueOf(currentFoodCosts);

        currentClothesCosts = db.getCosts(currentMonth + 1, currentYear, CostsDB.CostType.CLOTHES);
        if (currentClothesCosts.equals("error")) {
            currentClothesCosts = "0.00";
            db.addCosts(currentMonth + 1, currentYear, CostsDB.CostType.CLOTHES, currentClothesCosts);
        }
        total = total + Double.valueOf(currentClothesCosts);

        currentCommunalCosts = db.getCosts(currentMonth + 1, currentYear, CostsDB.CostType.COMMUNAL_RENT);
        if (currentCommunalCosts.equals("error")) {
            currentCommunalCosts = "0.00";
            db.addCosts(currentMonth + 1, currentYear, CostsDB.CostType.COMMUNAL_RENT, currentCommunalCosts);
        }
        total = total + Double.valueOf(currentCommunalCosts);

        currentElectronicsCosts = db.getCosts(currentMonth + 1, currentYear, CostsDB.CostType.ELECTRONICS);
        if (currentElectronicsCosts.equals("error")) {
            currentElectronicsCosts = "0.00";
            db.addCosts(currentMonth + 1, currentYear, CostsDB.CostType.ELECTRONICS, currentElectronicsCosts);
        }
        total = total + Double.valueOf(currentElectronicsCosts);

        currentTransportCosts = db.getCosts(currentMonth + 1, currentYear, CostsDB.CostType.TRANSPORT);
        if (currentTransportCosts.equals("error")) {
            currentTransportCosts = "0.00";
            db.addCosts(currentMonth + 1, currentYear, CostsDB.CostType.TRANSPORT, currentTransportCosts);
        }
        total = total + Double.valueOf(currentTransportCosts);

        currentGoodsCosts = db.getCosts(currentMonth + 1, currentYear, CostsDB.CostType.GOODS);
        if (currentGoodsCosts.equals("error")) {
            currentGoodsCosts = "0.00";
            db.addCosts(currentMonth + 1, currentYear, CostsDB.CostType.GOODS, currentGoodsCosts);
        }
        total = total + Double.valueOf(currentGoodsCosts);

        currentServicesCosts = db.getCosts(currentMonth + 1, currentYear, CostsDB.CostType.SERVICES);
        if (currentServicesCosts.equals("error")) {
            currentServicesCosts = "0.00";
            db.addCosts(currentMonth + 1, currentYear, CostsDB.CostType.SERVICES, currentServicesCosts);
        }
        total = total + Double.valueOf(currentServicesCosts);

        currentOthersCosts = db.getCosts(currentMonth + 1, currentYear, CostsDB.CostType.OTHERS);
        if (currentOthersCosts.equals("error")) {
            currentOthersCosts = "0.00";
            db.addCosts(currentMonth + 1, currentYear, CostsDB.CostType.OTHERS, currentOthersCosts);
        }
        total = total + Double.valueOf(currentOthersCosts);

        currentOverallCosts = String.valueOf(total);
        currentCostsTextView.setText(currentOverallCosts);
    }

















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
