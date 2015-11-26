package com.example.costs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.text.DecimalFormat;
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
    int todayMonth;
    int todayYear;
    int todayDay;

    boolean notCurrentDate;

    TextView currentDateTextView;
    TextView currentCostsTextView;

    PopupMenu costsPopupMenu;

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

        // Инициализируем поле, содержащее выбранные день, месяц и год
        if (currentDateTextView == null)
            currentDateTextView = (TextView) findViewById(R.id.currentDate);

        // Инициализируем поле, отображающее текущие расходы
        if (currentCostsTextView == null)
            currentCostsTextView = (TextView) findViewById(R.id.currentCosts);

        // Узнаём текущую дату
        SetCurrentDate();

        // Получаем данные о выбранном периоде для просмотра и
        // устанавливаем выбранную дату
        Bundle chosenPeriodData = getIntent().getExtras();
        if (chosenPeriodData != null) {
            String chosenPeriodString = String.valueOf(chosenPeriodData.get("chosenPeriod"));

            if (chosenPeriodData.get("fromPeriods") != null) {
                currentMonth = Integer.parseInt(chosenPeriodString.substring(0, chosenPeriodString.indexOf(" "))) - 1;
                currentYear = Integer.parseInt(chosenPeriodString.substring(chosenPeriodString.indexOf(" ") + 1));

                if (currentMonth != todayMonth || currentYear != todayYear) {
                    notCurrentDate = true;
                    currentDay = 28;
                } else
                    notCurrentDate = false;
            }
        }
        currentDateTextView.setText(currentDay + "  " + declensionMonthNames[currentMonth] + " " + String.valueOf(currentYear));

        // Устанавливаем расходы за выбранный период
        SetCurrentCosts();

        // Создаём всплывающее меню для отображения
        // последних десяти введённых значений
        GenerateCostsPopupMenu();

        // Формируем данные для ListView
        String[] costsArray = CreateCostsArray();

        ListAdapter listAdapter = new CostsAdapter(this, costsArray);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(listAdapter);

        // Если выбрана текущая дата - можно добавлять данныеё
        // о затратах за текущий период
        if (!notCurrentDate) {

            listView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent inputDataIntent = new Intent(MainActivity.this, InputDataActivity.class);

                            String textLine = String.valueOf(parent.getItemAtPosition(position));
                            String costsTypeText = textLine.substring(0, textLine.indexOf("$"));
                            String costTypeEnum = textLine.substring(textLine.indexOf("#") + 1);

                            inputDataIntent.putExtra("costTypeText", costsTypeText);
                            inputDataIntent.putExtra("costTypeEnum", costTypeEnum);
                            inputDataIntent.putExtra("currentMonth", currentMonth);
                            inputDataIntent.putExtra("currentYear", currentYear);
                            inputDataIntent.putExtra("currentCosts", textLine.substring(textLine.indexOf("$") + 1, textLine.indexOf("#")));

                            startActivity(inputDataIntent);
                        }
                    }
            );

        }
    }




    // Устанавливает текущие дату и время
    public void SetCurrentDate() {
        Calendar c = Calendar.getInstance();
        currentYear = todayYear = c.get(Calendar.YEAR);
        currentMonth = todayMonth = c.get(Calendar.MONTH);
        currentDay = todayDay = c.get(Calendar.DAY_OF_MONTH);

        //currentDateTextView.setText(currentDay + "  " + declensionMonthNames[currentMonth] + " " + String.valueOf(currentYear));
        //currentYearTextView.setText("  " + String.valueOf(currentYear));

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

        DecimalFormat format = new DecimalFormat("0.00");
        currentOverallCosts = String.valueOf(format.format(total));
        currentCostsTextView.setText(currentOverallCosts + " руб.");
    }

    public String[] CreateCostsArray() {
        String[] costsArray = new String[8];
        costsArray[0] = "Еда$" + currentFoodCosts + "#FOOD";
        costsArray[1] = "Промтовары$" + currentGoodsCosts + "#GOODS";
        costsArray[2] = "Квартплата$" + currentCommunalCosts + "#COMMUNAL_RENT";
        costsArray[3] = "Одежда$" + currentClothesCosts + "#CLOTHES";
        costsArray[4] = "Услуги$" + currentServicesCosts + "#SERVICES";
        costsArray[5] = "Транспорт$" + currentTransportCosts + "#TRANSPORT";
        costsArray[6] = "Техника$" + currentElectronicsCosts + "#ELECTRONICS";
        costsArray[7] = "Другое$" + currentOthersCosts + "#OTHERS";

        return costsArray;
    }

    public void GenerateCostsPopupMenu() {
        String[] lastEnteredValues = db.getLastEnteredValues();
        costsPopupMenu = new PopupMenu(this, currentCostsTextView);

        for (int i = 0; i < lastEnteredValues.length; ++i) {
            costsPopupMenu.getMenu().add(1, i + 1, i + 1, lastEnteredValues[i]);
        }
    }


    // Переход к экрану выбора периода
    public void onDateClick(View view) {
        Intent chosePeriodsIntent = new Intent(this, Periods.class);
        startActivity(chosePeriodsIntent);
    }

    // Просмотр последних десяти введённых значений
    public void onCostsClick(View view) {
        costsPopupMenu.show();
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
