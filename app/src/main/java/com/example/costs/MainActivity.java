package com.example.costs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    static final String[] declensionMonthNames = {"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};

    CostsDataBase cdb;

    int currentYear;
    int currentMonth;                            // Начинается с нуля
    int currentDay;
    int todayMonth;
    int todayYear;
    int todayDay;

    boolean notCurrentDate;
    static String nearestEventShown;

    TextView currentDateTextView;
    Button currentCostsButton;

    PopupMenu costsPopupMenu;

    String currentOverallCosts;

    Map<String, Double> costsMap = new HashMap<>();

    NumberFormat format;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        format = NumberFormat.getInstance();
        format.setGroupingUsed(false);

        // Получаем доступ к базе данных
        if (cdb == null)
            cdb = new CostsDataBase(this, null, null, 1);

        // Инициализируем поле, содержащее выбранные день, месяц и год
        if (currentDateTextView == null)
            currentDateTextView = (TextView) findViewById(R.id.currentDate);

        // Инициализируем поле, отображающее текущие расходы
        if (currentCostsButton == null)
            currentCostsButton = (Button) findViewById(R.id.currentCosts);

        // Узнаём текущую дату
        SetCurrentDate();

        // Получаем данные о выбранном периоде для просмотра и
        // устанавливаем выбранную дату
        Bundle chosenPeriodData = getIntent().getExtras();
        if (chosenPeriodData != null) {

            String chosenPeriodString = String.valueOf(chosenPeriodData.get("chosenPeriod"));

            if (chosenPeriodData.get("fromPeriods") != null) {
                currentMonth = Integer.parseInt(chosenPeriodString.substring(0, chosenPeriodString.indexOf(" ")));
                currentYear = Integer.parseInt(chosenPeriodString.substring(chosenPeriodString.indexOf(" ") + 1));

                if (currentMonth != todayMonth || currentYear != todayYear) {
                    notCurrentDate = true;
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(currentYear, currentMonth, 1);
                    currentDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                } else
                    notCurrentDate = false;
            }
        }
        currentDateTextView.setText(currentDay + "  " + declensionMonthNames[currentMonth] + " " + currentYear);

        // Если выбрана текущая дата - можно просматривать последние введённые значения
        if (!notCurrentDate) {
            currentCostsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent lastEnteredValues = new Intent(MainActivity.this, LastEnteredValuesActivity.class);
                    startActivity(lastEnteredValues);
                }
            });
        }


        /*
        // При длительном нажатии на общей сумме расходов -
        // переходим на экран, содержащий записи расходов за
        // последний месяц, сгруппированные по дням.
        currentCostsButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent lastEnteredValues = new Intent(MainActivity.this, LastEnteredValuesActivity.class);
                startActivity(lastEnteredValues);

                return true;
            }
        });
        */

        // Устанавливаем расходы за выбранный период
        SetCurrentCosts();


        // Формируем данные для ListView. Если пользователь просматривает затраты за какой-либо
        // предыдущий месяц (выбранная дата не совпадает с текущей) - убираем из списка пункт с
        // возможностью добавления новых статей расходов.
        final String[] costsArray = CreateCostsArray();
        String[] costsArrayWithoutAddNewCostTypeButton = new String[costsArray.length - 1];
        System.arraycopy(costsArray, 0, costsArrayWithoutAddNewCostTypeButton, 0, costsArrayWithoutAddNewCostTypeButton.length);

        ListAdapter listAdapter = null;
        if (!notCurrentDate)
            listAdapter = new CostsAdapter(this, costsArray);
        else
            listAdapter = new CostsAdapter(this, costsArrayWithoutAddNewCostTypeButton);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(listAdapter);

        // Если выбрана текущая дата - можно добавлять данныеё
        // о затратах за текущий период и редактировать список затрат
        if (!notCurrentDate) {
            listView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String textLine = String.valueOf(parent.getItemAtPosition(position));
                            String costValue = textLine.substring(textLine.indexOf("$") + 1);

                            // Если пользователь выбрал пункт "Добавить новую категорию" - переходим на
                            // экран добавления новой категории расходов. Иначе - переходим на экран
                            // ввода затрат по выбранной категории
                            Intent inputDataActivity = null;
                            if (position == costsArray.length - 1) {
                                if ("+".equals(costValue))
                                    inputDataActivity = new Intent(MainActivity.this, AddNewCostTypeActivity.class);
                            } else
                                inputDataActivity = new Intent(MainActivity.this, InputDataActivity.class);

                            inputDataActivity.putExtra("costType", textLine.substring(0, textLine.indexOf("$")));
                            //inputDataActivity.putExtra("costValue", textLine.substring(textLine.indexOf("$") + 1));
                            inputDataActivity.putExtra("currentDay", currentDay);
                            inputDataActivity.putExtra("currentMonth", currentMonth);
                            inputDataActivity.putExtra("currentYear", currentYear);

                            startActivity(inputDataActivity);
                        }
                    }
            );



            // При длительном нажатии на элемент списка переходим
            // на экран удаления выбарнной категории расходов
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    String textLine = String.valueOf(parent.getItemAtPosition(position));
                    String costValue = textLine.substring(textLine.indexOf("$") + 1);

                    // Если значение расходов в данном месяце по выбранной категориии
                    // не равны нулю - её не возможно удалить
                    if (Double.parseDouble(costValue) > 0) {
                        Toast canNotDeleteChosenCategoryToast = Toast.makeText(MainActivity.this,
                                "Нельзя удалить категорию, по которой имеются записи в текущем месяце!",
                                Toast.LENGTH_LONG);
                        canNotDeleteChosenCategoryToast.show();
                        return true;
                    } else {
                        if (!"+".equals(textLine.substring(textLine.indexOf("$") + 1))) {
                            Intent deleteCostTypeActivity = new Intent(MainActivity.this, DeleteCostTypeActivity.class);
                            deleteCostTypeActivity.putExtra("costType", textLine.substring(0, textLine.indexOf("$")));

                            startActivity(deleteCostTypeActivity);
                        }
                        return true;
                    }

                }
            });

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
        List<String> costsTypes = null;
        if (!notCurrentDate)
            costsTypes = cdb.getCostNames();
        else
            costsTypes = cdb.getCostNamesOnSpecifiedMonth(currentMonth, currentYear);

        Double totalCostsValue = 0.0;
        for (String costType : costsTypes) {
            Double costValue = cdb.getCostValue(-1, currentMonth, currentYear, costType);
            totalCostsValue = totalCostsValue + costValue;
            costsMap.put(costType, costValue);
        }

        // Устанавливаем суммарное значение расходов за текущий месяц
        currentOverallCosts = format.format(totalCostsValue);
        currentCostsButton.setText(currentOverallCosts + " руб.");
    }


    public String[] CreateCostsArray() {
        List<String> listOfCosts = new ArrayList<>();

        for (Map.Entry<String, Double> entry : costsMap.entrySet())
            listOfCosts.add(entry.getKey() + "$" + format.format(entry.getValue()));

        String[] costsArray = new String[listOfCosts.size() + 1];
        listOfCosts.toArray(costsArray);

        costsArray[costsArray.length - 1] = "Добавить новую категорию$+";

        // Сортируем массив в соответсвие с частотой использования каждого элемента массива
        OrderCostsArray(costsArray);

        return costsArray;
    }


    // Сортировка статей расходов по частоте внесения записей
    public void OrderCostsArray(String[] costsArray) {
        // Получаем массив последних введённых значений
        String[] lastEnteredValues = cdb.getLastThirtyEntries();
        if (lastEnteredValues.length == 0)
            return;

        // Выделяем из каждой строки массива только название категории расходов
        for (int i = 0; i < lastEnteredValues.length; ++i) {
            lastEnteredValues[i] = lastEnteredValues[i].substring(lastEnteredValues[i].indexOf(":") + 2);
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

        // Сортируем массив "finalOrder" по частоте использования категорий расходов
        Arrays.sort(finalOrder, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return Double.compare(Double.valueOf(lhs.substring(0, lhs.indexOf("$"))), Double.valueOf(rhs.substring(0, rhs.indexOf("$"))));
            }
        });

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


    // Переход к экрану выбора периода
    public void onDateClick(View view) {
        Intent chosePeriodsIntent = new Intent(this, Periods.class);
        startActivity(chosePeriodsIntent);
    }


    /*
    // Просмотр последних тридцати введённых значений
    public void onCostsClick(View view) {

        Intent lastEnteredValues = new Intent(MainActivity.this, LastEnteredValuesActivity.class);
        startActivity(lastEnteredValues);





        String[] lastThirtyEntriesArray = cdb.getLastThirtyEntries();

        costsPopupMenu = new PopupMenu(this, currentCostsButton);
        for (int i = 0; i < lastThirtyEntriesArray.length; ++i) {
            costsPopupMenu.getMenu().add(1, i + 1, i + 1, lastThirtyEntriesArray[i]);
        }
        costsPopupMenu.show();

    }
    */

    // Ищет записи в базе данных о ближайших (менее двух недель) событиях.
    // Если находит - выводит сообщение на экран
    public void SearchForNearestEvents() {
        String todayDateString = String.valueOf(todayDay) + "." + String.valueOf(todayMonth + 1) + "." + String.valueOf(todayYear);
        long todayDateInMilliseconds = 0;
        long nearestEventDateInMilliseconds = 0;
        String[] eventsArray = cdb.getEvents();

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
            String nearestEventToastText = "До события '" + eventsArray[0].substring(eventsArray[0].indexOf("$") + 1) +
                    "' осталось меньше двух недель.";

            Toast nearestEventToast = Toast.makeText(this, nearestEventToastText, Toast.LENGTH_LONG);
            nearestEventToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER, 0, 0);
            nearestEventToast.show();

            nearestEventShown = "";
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
