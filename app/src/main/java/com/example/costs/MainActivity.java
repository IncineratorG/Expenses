package com.example.costs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    static final String[] declensionMonthNames = {"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};

    CostsDB db;

    int currentYear;
    int currentMonth;                            // Начинается с нуля
    int currentDay;
    int todayMonth;
    int todayYear;
    int todayDay;

    boolean notCurrentDate;
    static String nearestEventShown;

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
                    // Добавлено 19.01.2015
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(currentYear, currentMonth, 1);
                    // --------------------
                    currentDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
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

            if (nearestEventShown == null)
                SearchForNearestEvents();
        }
    }




    // Устанавливает текущие дату и время
    public void SetCurrentDate() {
        Calendar c = Calendar.getInstance();
        currentYear = todayYear = c.get(Calendar.YEAR);
        currentMonth = todayMonth = c.get(Calendar.MONTH);
        currentDay = todayDay = c.get(Calendar.DAY_OF_MONTH);

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

        // Сортируем массив по частоте использования элементов
        OrderCostsArray(costsArray);

        return costsArray;
    }

    // Создание списка последних десяти введённых значений
    public void GenerateCostsPopupMenu() {
        String[] lastEnteredValues = db.getLastEnteredValues();

        costsPopupMenu = new PopupMenu(this, currentCostsTextView);

        for (int i = 0; i < lastEnteredValues.length; ++i) {
            costsPopupMenu.getMenu().add(1, i + 1, i + 1, lastEnteredValues[i]);
        }
    }

    // Ищет записи в базе данных о ближайших (менее двух недель) событиях.
    // Если находит - выводит сообщение на экран
    public void SearchForNearestEvents() {
        String todayDateString = String.valueOf(todayDay) + "." + String.valueOf(todayMonth + 1) + "." + String.valueOf(todayYear);
        long todayDateInMilliseconds = 0;
        long nearestEventDateInMilliseconds = 0;
        String[] eventsArray = db.getEvents();

        if (eventsArray.length == 0)
            return;

        try {
            todayDateInMilliseconds = new SimpleDateFormat("dd.MM.yyyy").parse(todayDateString).getTime();
            nearestEventDateInMilliseconds = new SimpleDateFormat("dd.MM.yyyy").
                    parse(eventsArray[0].substring(0, eventsArray[0].indexOf("$"))).getTime();
        } catch (Exception e) {
            Toast errorInDateParsingToast = Toast.makeText(this, "ERROR PARSING DATE ON MAIN SCREEN", Toast.LENGTH_LONG);
            errorInDateParsingToast.show();
        }

        if ((nearestEventDateInMilliseconds - todayDateInMilliseconds) < 1209600000) {
            String nearestEventToastText = "До события " + eventsArray[0].substring(eventsArray[0].indexOf("$") + 1) +
                    " осталось меньше двух недель!";

            Toast nearestEventToast = Toast.makeText(this, nearestEventToastText, Toast.LENGTH_LONG);
            nearestEventToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER, 0, 0);
            nearestEventToast.show();

            nearestEventShown = "";
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

    // Сортировка статей расходов по частоте внесения записей
    public void OrderCostsArray(String[] costsArray) {
        // Получаем массив последних введённых значений
        String[] lastEnteredValues = db.getLastEnteredValues();
        if (lastEnteredValues.length == 0)
            return;

        // Выделяем из каждой строки массива только название категории расходов
        for (int i = 0; i < lastEnteredValues.length; ++i) {
            lastEnteredValues[i] = lastEnteredValues[i].substring(lastEnteredValues[i].indexOf(" ") + 1);
            lastEnteredValues[i] = lastEnteredValues[i].substring(0, lastEnteredValues[i].indexOf(" "));
        }

        Arrays.sort(lastEnteredValues);

        // В список будем заносить частоту использование
        // статьи расходов и её наименование
        List<String> listOfItemFrequence = new ArrayList<>(10);

        int frequence = 1;
        for (int i = 1; i < lastEnteredValues.length; ++i) {
            if (lastEnteredValues[i - 1].equals(lastEnteredValues[i])) {
                ++frequence;
            } else {
                String item = String.valueOf(frequence) + "$" + lastEnteredValues[i - 1];
                listOfItemFrequence.add(item);

                frequence = 1;
            }

            if ((i + 1) >= lastEnteredValues.length) {
                String item = String.valueOf(frequence) + "$" + lastEnteredValues[i];
                listOfItemFrequence.add(item);
            }
        }

        // В массиве содержится частота использования
        // категории расходов и её наименование (через "$")
        String[] finalOrder = new String[listOfItemFrequence.size()];
        listOfItemFrequence.toArray(finalOrder);

        Arrays.sort(finalOrder);

        // Изменяем порядок следование элементов в массиве costsArray
        // в соответствие с массивом finalOrder
        for (int i = 0; i < finalOrder.length; ++i) {
            String costName = finalOrder[i].substring(finalOrder[i].indexOf("$") + 1);

            for (int j = 0; j < costsArray.length; ++j) {
                String costsArrayLine = costsArray[j].substring(0, costsArray[j].indexOf("$"));

                if (costsArrayLine.equals(costName)) {
                    if (j == 0)
                        break;
                    else {
                        String temp = costsArray[j];

                        for (int k = j - 1; k >= 0; --k) {
                            costsArray[k + 1] = costsArray[k];
                        }
                        costsArray[0] = temp;
                        break;
                    }
                }
            }
        }
    }












    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            nearestEventShown = null;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(1, 1, 1, "Напоминания");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == 1) {
            Intent addNewEventIntent = new Intent(this, AddNewEvent.class);
            startActivity(addNewEventIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
