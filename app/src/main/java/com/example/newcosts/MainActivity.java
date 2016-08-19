package com.example.newcosts;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    static final String[] declensionMonthNames = {"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};

    CostsDB cdb;

    int currentYear;
    int currentMonth;                            // Начинается с нуля
    int currentDay;
    int todayMonth;
    int todayYear;
    int todayDay;
    int chosenCostNameId;

    static String nearestEventShown;

    TextView currentDateTextViewMainActivity;
    TextView currentOverallCostsTextViewMainActivity;

    String currentOverallCosts;

    String[] nonActiveCostNames;

    Map<String, Double> costsMap;

    NumberFormat format;




    String[] costsArray;
    ListAdapter costsListViewMainActivityAdapter;
    ListView costsListViewMainActivity;
    Dialog currentDialog;

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
            cdb = new CostsDB(this, null, null, 1);

        // Инициализируем поле, содержащее выбранные день, месяц и год
        if (currentDateTextViewMainActivity == null)
            currentDateTextViewMainActivity = (TextView) findViewById(R.id.currentDateMainActivity);

        // Инициализируем поле, отображающее текущие расходы
        if (currentOverallCostsTextViewMainActivity == null)
            currentOverallCostsTextViewMainActivity = (TextView) findViewById(R.id.currentOverallCostsMainActivity);

        // Устанавливаем текущую дату
        SetCurrentDate();

        // Устанавливаем суммарное значение расходов за текущий месяц
        SetCurrentOverallCosts();

        // Формируем данные для costsListViewMainActivity - названия статей расходов и
        // значения по этим статьям за текущий месяц; устанавливаем слушатели на
        // нажатие элемента списка статей расходов (costsListViewMainActivity)
        CreateListViewContent();
    }


    // Обработчик нажатий кнопок в input_data_popup
    public void OnInputDataPopupClickListener(View view) {
        Button pressedButton = (Button) view;
        String buttonLabel = (String)pressedButton.getText();

        TextView costTypeTextView = (TextView) currentDialog.findViewById(R.id.costTypeTextViewInInputDataPopup);

        EditText inputDataEditText = (EditText) currentDialog.findViewById(R.id.inputTextFieldEditTextInInputDataPopup);
        inputDataEditText.setFilters(new DecimalDigitsInputFilter[] {new DecimalDigitsInputFilter()});
        inputDataEditText.setCursorVisible(false);

        switch (buttonLabel){
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
            case "0":
                inputDataEditText.append(buttonLabel);
                break;

            case ".": {
                String inputText = String.valueOf(inputDataEditText.getText());
                if (!inputText.contains(".")) {
                    inputDataEditText.append(".");
                }
                break;
            }

            case "Del": {
                String inputText = String.valueOf(inputDataEditText.getText());
                if (inputText != null && inputText.length() != 0) {
                    inputText = inputText.substring(0, inputText.length() -1);
                    inputDataEditText.setText(inputText);
                }
                break;
            }

            case "OK": {
                String inputText = String.valueOf(inputDataEditText.getText());
                if (inputText != null && inputText.length() != 0 && !".".equals(inputText)) {
                    Double enteredCostValue = Double.parseDouble(inputText);

                    cdb.addCosts(enteredCostValue, chosenCostNameId);

                    // Обновляем главный экран приложения (MainActivity)
                    SetCurrentOverallCosts();
                    CreateListViewContent();

                    inputDataEditText.setText("");
                }
                break;
            }

            case "Take photo":
                System.out.println("Take photo");
                break;

        }
    }

    // При нажатии на пункт списка статей расходов появляется всплывающее окно,
    // в котором можно ввести значение расходов по выбранной статье. При нажатии
    // на пункт "Добавить новую категорию" появляется всплывающее окно, в котором
    // можно добавить название новой категории расходов
    public void OnCostsListViewItemClick(AdapterView<?> parent, View view, int position, long id) {
        String textLine = String.valueOf(parent.getItemAtPosition(position));

        // Нажатие на пункт "Добавить новую категорию"
        if ("+".equals(String.valueOf(textLine.charAt(textLine.length() - 1)))) {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.add_new_cost_type_popup);

            // Инициализируем поле ввода названия новой статьи расходов
            final AutoCompleteTextView inputTextField = (AutoCompleteTextView) dialog.findViewById(R.id.costTypeTextViewInAddNewCostTypePopup);
            inputTextField.setCursorVisible(false);

            ArrayAdapter<String> autoCompleteTextViewAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, nonActiveCostNames);
            inputTextField.setAdapter(autoCompleteTextViewAdapter);

            // Инициализируем кнопки всплывающего окна
            Button addNewCostTypeButton = (Button) dialog.findViewById(R.id.addNewCostTypeButton);
            Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

            // Устанавливаем слушатели на кнопки
            addNewCostTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newCostTypeName = inputTextField.getText().toString();

                    if (newCostTypeName.length() > 0) {
                        boolean costNameNotExist = cdb.addCostName(newCostTypeName);
                        if (costNameNotExist) {
                            Toast newCostTypeAddedToast = Toast.makeText(MainActivity.this, "Категория '" + newCostTypeName + "' создана.", Toast.LENGTH_LONG);

                            // Обновляем главный экран приложения (MainActivity)
                            SetCurrentOverallCosts();
                            CreateListViewContent();

                            dialog.cancel();
                            newCostTypeAddedToast.show();
                        } else {
                            Toast newCostTypeAddedToast = Toast.makeText(MainActivity.this, "Категория '" + newCostTypeName + "' уже создана.", Toast.LENGTH_LONG);
                            newCostTypeAddedToast.show();
                        }
                    }
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            dialog.show();
        }
        // Нажатие на название категроии расходов
        else {
            // Получаем название выбранной статьи расходов
            String[] textLineData = textLine.split("\\$");
            String costName = textLineData[1];

            // Устанавливаем ID выбранной статьи расходов
            chosenCostNameId = Integer.parseInt(textLineData[0]);

            // Инициализируем диалог ввода значения расходов
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.input_data_popup);
            currentDialog = dialog;

            // Инициализируем поле с названием выбранной статьи расходов
            TextView chosenCostTypeNameTextView = (TextView) dialog.findViewById(R.id.costTypeTextViewInInputDataPopup);
            chosenCostTypeNameTextView.setText(costName);

            dialog.show();
        }
    }


    // При длительном нажатии на пункт списка статей расходов появляется всплывающее окно,
    // позволяющее редактировать название выбранной статьи расходов (удалить, переименовать)
    public boolean OnCostsListViewItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String textLine = String.valueOf(parent.getItemAtPosition(position));
        final String[] textLineData = textLine.split("\\$");

        if (!"+".equals(String.valueOf(textLine.charAt(textLine.length() - 1)))) {
            final String chosenCostTypeName = textLineData[1];

            final Dialog mainEditDialog = new Dialog(MainActivity.this);
            mainEditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mainEditDialog.setContentView(R.layout.edit_cost_type_popup);

            // Устанавливаем поле с названием выбранной статьи расходов
            final EditText chosenCostTypeNameEditText = (EditText) mainEditDialog.findViewById(R.id.costTypeNameEditTextInEditCostTypePopup);
            chosenCostTypeNameEditText.setCursorVisible(false);
            chosenCostTypeNameEditText.setText(chosenCostTypeName);
            chosenCostTypeNameEditText.setEnabled(false);

            // Инициализируем кнопки всплывающего окна
            Button renameButton = (Button) mainEditDialog.findViewById(R.id.renameButton);
            Button deleteButton = (Button) mainEditDialog.findViewById(R.id.deleteButton);
            Button cancelButton = (Button) mainEditDialog.findViewById(R.id.cancelButton);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainEditDialog.cancel();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Диалоговое окно, запрашивающее подтверждение на удаление
                    // выбранного элемента контекстного меню. При нажатии на кнопку "Удалить"
                    // происходит удаление выбранного элемента из базы данных и обновление
                    // текущей суммы расходов по данной категории
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(MainActivity.this);
                    adBuilder.setNegativeButton("Отмена", null);
                    adBuilder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean result = cdb.deleteCostName(Integer.parseInt(textLineData[0]));
                            if (result) {

                                Toast costTypeDeletedToast = Toast.makeText(MainActivity.this, "Категория '" + chosenCostTypeName + "' удалена.", Toast.LENGTH_LONG);
                                costTypeDeletedToast.show();

                                // Обновляем главный экран приложения (MainActivity)
                                SetCurrentOverallCosts();
                                CreateListViewContent();

                                // Закрываем окно редактирования
                                mainEditDialog.cancel();
                            } else {
                                Toast errorDeletingCostTypeToast = Toast.makeText(MainActivity.this, "Не удалось удалить категорию " + chosenCostTypeName + ".", Toast.LENGTH_LONG);
                                errorDeletingCostTypeToast.show();
                            }
                        }
                    });
                    adBuilder.setMessage("Удалить категорию \"" + textLineData[1] + "\" ?");

                    AlertDialog dialog = adBuilder.create();
                    dialog.show();

                    TextView dialogText = (TextView) dialog.findViewById(android.R.id.message);
                    dialogText.setGravity(Gravity.CENTER);
                }
            });

            renameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.add_new_cost_type_popup);

                    // Инициализируем поле ввода названия новой статьи расходов
                    final AutoCompleteTextView inputTextField = (AutoCompleteTextView) dialog.findViewById(R.id.costTypeTextViewInAddNewCostTypePopup);
                    inputTextField.setCursorVisible(true);
                    inputTextField.setText(textLineData[1]);

//                    ArrayAdapter<String> autoCompleteTextViewAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, nonActiveCostNames);
//                    inputTextField.setAdapter(autoCompleteTextViewAdapter);

                    // Инициализируем кнопки всплывающего окна
                    Button renameButton = (Button) dialog.findViewById(R.id.addNewCostTypeButton);
                    renameButton.setText("Переименовать");
                    Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

                    // Устанавливаем слушатели на кнопки
                    renameButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String newCostTypeName = inputTextField.getText().toString();

                            if (newCostTypeName.length() > 0) {
                                int renameResult = cdb.renameCostName(Integer.parseInt(textLineData[0]), newCostTypeName);
                                if (renameResult == 0) {
                                    Toast renameToast = Toast.makeText(MainActivity.this, "Категория '" + newCostTypeName + "' переименована.", Toast.LENGTH_LONG);

                                    // Обновляем главный экран приложения (MainActivity)
                                    SetCurrentOverallCosts();
                                    CreateListViewContent();

                                    dialog.cancel();
                                    renameToast.show();
                                    mainEditDialog.cancel();
                                } else if (renameResult == 1){
                                    Toast newCostTypeErrorToast = Toast.makeText(MainActivity.this, "Категорию '" + newCostTypeName + "' не " +
                                            "возможно создать, так как в программе присутствуют записи по категории с таким названием.", Toast.LENGTH_LONG);
                                    newCostTypeErrorToast.show();
                                } else if (renameResult == 2) {
                                    Toast newCostTypeErrorToast = Toast.makeText(MainActivity.this, "Категория '" + newCostTypeName + "' уже создана.", Toast.LENGTH_LONG);
                                    newCostTypeErrorToast.show();
                                }
                            }
                        }
                    });

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }
            });

            mainEditDialog.show();
        }

        return true;
    }


    // При нажатии на суммарное значение расходов за текущий месяц появляется список,
    // содержащий последние тридцать введённых значений. При нажатии на элемент этого
    // списка появляется всплывающее окно, предлагающее удалить выбранную запись из программы
    public void OnOverallCostsValueClick(View view) {
        final String[] lastEnteredValues = cdb.getLastEntries(30);
//        for (String s : lastEnteredValues)
//            System.out.println(s);

        PopupMenu lastEntriesPopupMenu = new PopupMenu(this, currentOverallCostsTextViewMainActivity);
        for (int i = 0; i < lastEnteredValues.length; ++i)
            lastEntriesPopupMenu.getMenu().add(1, i + 1, i + 1, lastEnteredValues[i].substring(0, lastEnteredValues[i].indexOf("%")));

        lastEntriesPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                System.out.println(item.getTitle().toString());
//                System.out.println(lastEnteredValues[item.getItemId() - 1]);

                final int itemPositionInLastEnteredValuesArray = item.getItemId() - 1;

                // Диалоговое окно, запрашивающее подтверждение на удаление выбранного
                // элемента из списка последних внесённых значений. При нажатии на кнопку "Удалить"
                // происходит удаление выбранного элемента из базы данных и обновление
                // текущей суммы расходов по данной категории
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(MainActivity.this);
                adBuilder.setNegativeButton("Отмена", null);
                adBuilder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dateString = lastEnteredValues[itemPositionInLastEnteredValuesArray].substring(lastEnteredValues[itemPositionInLastEnteredValuesArray].indexOf("%") + 1);
                        Long chosenItemDateInMilliseconds = Long.parseLong(dateString);

                        boolean result = cdb.removeCostValue(chosenItemDateInMilliseconds);

                        // Обновляем главный экран приложения (MainActivity)
                        SetCurrentOverallCosts();
                        CreateListViewContent();
                    }
                });
                adBuilder.setMessage(item.getTitle().toString());

                AlertDialog dialog = adBuilder.create();
                dialog.show();

                TextView dialogText = (TextView) dialog.findViewById(android.R.id.message);
                dialogText.setGravity(Gravity.CENTER);

                return true;
            }
        });


        lastEntriesPopupMenu.show();
    }


    // Инициализирует costsListViewMainActivity и заполняет его
    // данными (названия статей расходов и значения по этим статьям
    // за текущий месяц); устанавливает слушатель на
    // нажатие элемента списка статей расходов (costsListViewMainActivity)
    public void CreateListViewContent() {
        costsArray = CreateCostsArray();

        costsListViewMainActivityAdapter = new NewCostsListViewAdapter(this, costsArray);

        costsListViewMainActivity = (ListView) findViewById(R.id.costsListViewMainActivity);
        costsListViewMainActivity.setAdapter(costsListViewMainActivityAdapter);
        costsListViewMainActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OnCostsListViewItemClick(parent, view, position, id);
            }
        });
        costsListViewMainActivity.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return OnCostsListViewItemLongClick(parent, view, position, id);
            }
        });
    }


    // Устанавливает текущие дату и время
    public void SetCurrentDate() {
        Calendar c = Calendar.getInstance();
        currentYear = todayYear = c.get(Calendar.YEAR);
        currentMonth = todayMonth = c.get(Calendar.MONTH);
        currentDay = todayDay = c.get(Calendar.DAY_OF_MONTH);

        currentDateTextViewMainActivity.setText(currentDay + " " + declensionMonthNames[currentMonth] + " " + currentYear);
    }


    // Устанавливает суммарное значение расходов за текущий месяц - создаёт карту (Map), состоящую из
    // пар: ID и название статьи расходов и значение по этой статье за текущий месяц; все значения суммируются
    // и сумма устанавливается в currentOverallCostsTextViewMainActivity. Созданная карта используеься в
    // дальнейшем в функции CreateCostsArray.
    public void SetCurrentOverallCosts() {
        List<String> tableCostNamesContent = cdb.getActiveCostNames();
        costsMap = new HashMap<>();

        // Получаем массив не активных (удалённых) названий статей расходов
        nonActiveCostNames = cdb.getNonActiveCostNames();

        Double totalCostsValue = 0.0;
        for (String costNameRaw : tableCostNamesContent) {
            int id_n = Integer.parseInt(costNameRaw.substring(0, costNameRaw.indexOf("$")));
            Double costValue = cdb.getCostValue(-1, currentMonth, currentYear, id_n);
            totalCostsValue = totalCostsValue + costValue;
            costsMap.put(costNameRaw, costValue);
        }

        // Устанавливаем суммарное значение расходов за текущий месяц
        currentOverallCosts = format.format(totalCostsValue);
        currentOverallCostsTextViewMainActivity.setText(currentOverallCosts + " руб.");
    }


    // Создаёт массив, состоящий из строк с названиями статей расходов и значениями по этим статьям
    // за текущий месяц, разделённые символом "$". Последним элементом созданного массива всегда
    // идёт строка "Добавить новую категорию$+".
    public String[] CreateCostsArray() {
        List<String> listOfCostNames = new ArrayList<>();

        for (Map.Entry<String, Double> entry : costsMap.entrySet())
            listOfCostNames.add(entry.getKey() + "$" + format.format(entry.getValue()));


        String[] costsArray = new String[listOfCostNames.size() + 1];
        listOfCostNames.toArray(costsArray);

        costsArray[costsArray.length - 1] = Integer.MAX_VALUE + "$Добавить новую категорию$+";

        // Сортируем массив в соответсвие с частотой использования каждого элемента массива
//        OrderCostsArray(costsArray);

        return costsArray;
    }
//
//
//    // Сортировка статей расходов по частоте внесения записей
//    public void OrderCostsArray(String[] costsArray) {
//        // Получаем массив последних введённых значений
//        String[] lastEnteredValues = cdb.getLastThirtyEntries();
//        if (lastEnteredValues.length == 0)
//            return;
//
//        // Выделяем из каждой строки массива только название категории расходов
//        for (int i = 0; i < lastEnteredValues.length; ++i) {
//            lastEnteredValues[i] = lastEnteredValues[i].substring(lastEnteredValues[i].indexOf(":") + 2);
//            lastEnteredValues[i] = lastEnteredValues[i].substring(0, lastEnteredValues[i].indexOf(" "));
//        }
//
//        Arrays.sort(lastEnteredValues);
//
//        // В список будем заносить частоту использование
//        // статьи расходов и её наименование
//        List<String> listOfItemFrequence = new ArrayList<>(10);
//
//        int frequence = 1;
//        for (int i = 1; i < lastEnteredValues.length; ++i) {
//            if (lastEnteredValues[i - 1].equals(lastEnteredValues[i])) {
//                ++frequence;
//            } else {
//                String item = String.valueOf(frequence) + "$" + lastEnteredValues[i - 1];
//                listOfItemFrequence.add(item);
//
//                frequence = 1;
//            }
//
//            if ((i + 1) >= lastEnteredValues.length) {
//                String item = String.valueOf(frequence) + "$" + lastEnteredValues[i];
//                listOfItemFrequence.add(item);
//            }
//        }
//
//        // В массиве содержится частота использования
//        // категории расходов и её наименование (через "$")
//        String[] finalOrder = new String[listOfItemFrequence.size()];
//        listOfItemFrequence.toArray(finalOrder);
//
//        // Сортируем массив "finalOrder" по частоте использования категорий расходов
//        Arrays.sort(finalOrder, new Comparator<String>() {
//            @Override
//            public int compare(String lhs, String rhs) {
//                return Double.compare(Double.valueOf(lhs.substring(0, lhs.indexOf("$"))), Double.valueOf(rhs.substring(0, rhs.indexOf("$"))));
//            }
//        });
//
//        // Изменяем порядок следование элементов в массиве costsArray
//        // в соответствие с массивом finalOrder
//        for (int i = 0; i < finalOrder.length; ++i) {
//            String costName = finalOrder[i].substring(finalOrder[i].indexOf("$") + 1);
//
//            for (int j = 0; j < costsArray.length; ++j) {
//                String costsArrayLine = costsArray[j].substring(0, costsArray[j].indexOf("$"));
//
//                if (costsArrayLine.equals(costName)) {
//                    if (j == 0)
//                        break;
//                    else {
//                        String temp = costsArray[j];
//
//                        for (int k = j - 1; k >= 0; --k) {
//                            costsArray[k + 1] = costsArray[k];
//                        }
//                        costsArray[0] = temp;
//                        break;
//                    }
//                }
//
//            }
//        }
//
//    }
//
//
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

        menu.add(1, 1, 1, "Просмотр статистики");
        menu.add(1, 2, 2, "Последние введённые значения");
//        menu.add(1, 3, 3, "Редактировать название статей");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Переход на экран просмотра статистики расходов
        if (id == 1) {
            Intent statisticMainScreenIntent = new Intent(MainActivity.this, StatisticMainScreenActivity.class);
            startActivity(statisticMainScreenIntent);
        }

        // Просмотр последних введённых значений
        if (id == 2) {
            OnOverallCostsValueClick(currentOverallCostsTextViewMainActivity);
        }

//        // Редактирование названия статей расходов
//        if (id == 3) {
//            Toast notImplementedToast = Toast.makeText(MainActivity.this, "Не реализовано.", Toast.LENGTH_LONG);
//            notImplementedToast.show();
//        }

        return super.onOptionsItemSelected(item);
    }












//    // Ищет записи в базе данных о ближайших (менее двух недель) событиях.
//    // Если находит - выводит сообщение на экран
//    public void SearchForNearestEvents() {
//        String todayDateString = String.valueOf(todayDay) + "." + String.valueOf(todayMonth + 1) + "." + String.valueOf(todayYear);
//        long todayDateInMilliseconds = 0;
//        long nearestEventDateInMilliseconds = 0;
//        String[] eventsArray = cdb.getEvents();
//
//        if (eventsArray.length == 0)
//            return;
//
//        try {
//            todayDateInMilliseconds = new SimpleDateFormat("dd.MM.yyyy").parse(todayDateString).getTime();
//            nearestEventDateInMilliseconds = new SimpleDateFormat("dd.MM.yyyy").
//                    parse(eventsArray[0].substring(0, eventsArray[0].indexOf("$"))).getTime();
//        } catch (Exception e) {
//            Toast errorInDateParsingToast = Toast.makeText(this, "ERROR PARSING DATE ON MAIN SCREEN", Toast.LENGTH_LONG);
//            errorInDateParsingToast.show();
//        }
//
//        if ((nearestEventDateInMilliseconds - todayDateInMilliseconds) < 1209600000) {
//            String nearestEventToastText = "До события '" + eventsArray[0].substring(eventsArray[0].indexOf("$") + 1) +
//                    "' осталось меньше двух недель.";
//
//            Toast nearestEventToast = Toast.makeText(this, nearestEventToastText, Toast.LENGTH_LONG);
//            nearestEventToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER, 0, 0);
//            nearestEventToast.show();
//
//            nearestEventShown = "";
//        }
//    }
}