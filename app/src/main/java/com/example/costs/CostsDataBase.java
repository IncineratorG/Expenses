package com.example.costs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Саня on 29.01.2016.
 */
public class CostsDataBase extends SQLiteOpenHelper {

    /*  В базе данных месяца начинаются с 0  */


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "costs.db";

    private static final String COLUMN_ID = "_id";

    private static final String TABLE_COST_NAMES = "costnames";
    private static final String COLUMN_COST_NAME = "costname";

    private static final String TABLE_COST_VALUES = "costsvalues";
    private static final String COLUMN_DAY = "day";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_DATE_IN_MILLISECONDS = "dateinmilliseconds";
    private static final String COLUMN_COST_VALUE = "costvalue";

    private static final String TABLE_EVENTS = "events";
    private static final String COLUMN_EVENT_DESCRIPTION = "eventdescription";
    private static final String COLUMN_EVENT_DATE = "eventdate";
    private static final String COLUMN_EVENT_DATE_IN_MILLISECONDS = "eventdateinmilliseconds";


    public CostsDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Таблица, хранящая названия статей расходов
        String createTableCostsNames = "CREATE TABLE " + TABLE_COST_NAMES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COST_NAME + " TEXT)";

        // Таблица, хранящая записи о расходах по соответствующей статье расходов
        String createTableCostsValues = "CREATE TABLE " + TABLE_COST_VALUES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY + " INTEGER, " +
                COLUMN_MONTH + " INTEGER, " +
                COLUMN_YEAR + " INTEGER, " +
                COLUMN_DATE_IN_MILLISECONDS + " INTEGER, " +
                COLUMN_COST_NAME + " TEXT, " +
                COLUMN_COST_VALUE + " REAL)";

        // Таблица, хранящая записи о ближайщих событиях
        String createTableEvents = "CREATE TABLE " + TABLE_EVENTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EVENT_DESCRIPTION + " TEXT, " +
                COLUMN_EVENT_DATE + " TEXT, " +
                COLUMN_EVENT_DATE_IN_MILLISECONDS + " INTEGER " +
                ");";

        db.execSQL(createTableCostsNames);
        db.execSQL(createTableCostsValues);
        db.execSQL(createTableEvents);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COST_NAMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COST_VALUES);
        onCreate(db);
    }




    // Добавляет новую запись по статье расходов 'costName' в базу данных
    public void addCosts(double costValue, String costName) {
        Calendar calendar = Calendar.getInstance();

        int day =  calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        long currentDateInMilliseconds = calendar.getTimeInMillis();

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY, day);
        values.put(COLUMN_MONTH, month);
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_DATE_IN_MILLISECONDS, currentDateInMilliseconds);
        values.put(COLUMN_COST_NAME, costName);
        values.put(COLUMN_COST_VALUE, costValue);
        db.insert(TABLE_COST_VALUES, null, values);

        db.close();
    }


    // Возвращает суммарную величину расходов типа 'costName'
    // за заданный период. Если поле 'day' отрицательно -
    // при выборке значений учитывается только указанные месяц, год и тип расходов
    public double getCostValue(int day, int month, int year, String costName) {

        String getCostValueQuery = null;

        if (day >= 0) {
            getCostValueQuery = "SELECT " + COLUMN_COST_VALUE +
                    " FROM " + TABLE_COST_VALUES +
                    " WHERE " + COLUMN_DAY + " = " + day +
                    " AND " + COLUMN_MONTH + " = " + month +
                    " AND " + COLUMN_YEAR + " = " + year +
                    " AND " + COLUMN_COST_NAME + " like '" + costName + "'";
        } else {
            getCostValueQuery = "SELECT " + COLUMN_COST_VALUE +
                    " FROM " + TABLE_COST_VALUES +
                    " WHERE " + COLUMN_MONTH + " = " + month +
                    " AND " + COLUMN_YEAR + " = " + year +
                    " AND " + COLUMN_COST_NAME + " like '" + costName + "'";
        }

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        double sum = 0.0;

        try {
            c = db.rawQuery(getCostValueQuery, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                sum = sum + c.getDouble(c.getColumnIndex(COLUMN_COST_VALUE));
                c.moveToNext();
            }
        } catch (Exception e) {
            System.err.println("EXCEPTION IN 'getCostValue()'.");
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return sum;
    }


    // Возвращает массив строк, состоящих из месяца и года (1 2016),
    // для которых присутствуют записи в базе
    public List<String> getAllPeriods() {
        String getAllPeriodsQuery = "SELECT DISTINCT " +
                COLUMN_MONTH + ", " +
                COLUMN_YEAR + " FROM " +
                TABLE_COST_VALUES +
                " ORDER BY " + COLUMN_DATE_IN_MILLISECONDS +
                " DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<String> listOfPeriods = new ArrayList<>();
        String[] periodsArray = null;

        try {
            c = db.rawQuery(getAllPeriodsQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                listOfPeriods.add(c.getString(c.getColumnIndex(COLUMN_MONTH)) + " " + c.getString(c.getColumnIndex(COLUMN_YEAR)));
                c.moveToNext();
            }
        } catch (Exception e) {
            System.err.println("EXCEPTION IN 'getAllPeriods()'.");
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return listOfPeriods;
    }


    // Добавляет новую статью расходов в базу. Если такая запись
    // уже присутствует в базе - возвращает 0, иначе 1.
    public int addNewCostName(String costName) {
        String checkCostName = "SELECT " + COLUMN_COST_NAME +
                " FROM " + TABLE_COST_NAMES +
                " WHERE " + COLUMN_COST_NAME +
                " LIKE '" + costName + "'";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        int result = 1;

        try {
            c = db.rawQuery(checkCostName, null);
            if (c.moveToFirst()) {
                result = 0;
                return result;
            }
            else {
                ContentValues values = new ContentValues();
                values.put(COLUMN_COST_NAME, costName);
                db.insert(TABLE_COST_NAMES, null, values);
            }
        } catch (Exception e) {
            System.err.println("EXCEPTION IN 'addNewCostName()'.");
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return result;
    }


    // Возвращает названия активных статей расходов, записи для которых
    // присутствуют в базе даных
    public List<String> getCostNames() {
        String getDistinctCostNamesQuery = "SELECT * FROM " + TABLE_COST_NAMES;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<String> listOfCostsNames = new ArrayList<>();

        try {
            c = db.rawQuery(getDistinctCostNamesQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                listOfCostsNames.add(c.getString(c.getColumnIndex(COLUMN_COST_NAME)));
                c.moveToNext();
            }

        } catch (Exception e) {
            System.err.println("EXCEPTION IN 'getCostsNames()'");
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return listOfCostsNames;
    }


    // Возвращает названия статей расходов и их значения на заданную дату
    public List<String> getCostValueOnSpecifiedDate(int day, int month, int year) {
        String getCostValuesOnSpecifiedDayQuery = "SELECT " +
                COLUMN_COST_NAME + ", " +
                "SUM(" + COLUMN_COST_VALUE + ") AS SUM " +
                " FROM " + TABLE_COST_VALUES +
                " WHERE " + COLUMN_DAY + " = " + day +
                " AND " + COLUMN_MONTH + " = " + month +
                " AND " + COLUMN_YEAR + " = " + year +
                " GROUP BY " + COLUMN_COST_NAME;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<String> listOfEntries = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        try {
            c = db.rawQuery(getCostValuesOnSpecifiedDayQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                sb.append(c.getString(c.getColumnIndex(COLUMN_COST_NAME)));
                sb.append("$");

                sb.append(c.getString(c.getColumnIndex("SUM")));

                listOfEntries.add(sb.toString());
                sb.setLength(0);

                c.moveToNext();
            }

        } catch (Exception e) {
            System.err.println("EXCEPTION IN 'getCostValueOnSpecifiedDate()'.");
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return listOfEntries;
    }


    // Возвращает список с названиями статей расходов,
    // присутствующих в базе на заданную дату
    public List<String> getCostNamesOnSpecifiedMonth(int month, int year) {
        String getCostNamesOnSpecifiedDateQuery = "SELECT DISTINCT " +
                COLUMN_COST_NAME +
                " FROM " + TABLE_COST_VALUES +
                " WHERE " + COLUMN_YEAR + " = " + year +
                " AND " + COLUMN_MONTH + " = " + month;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<String> listOfCostNames = new ArrayList<>();

        try {
            c = db.rawQuery(getCostNamesOnSpecifiedDateQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                listOfCostNames.add(c.getString(c.getColumnIndex(COLUMN_COST_NAME)));
                c.moveToNext();
            }

        } catch (Exception e) {
            System.err.println("EXCEPTION IN 'getCostNamesOnSpecifiedMonth()'.");
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return listOfCostNames;
    }


    // Удаляет из таблицы COST_NAMES поле costName
    public int deleteCostName(String costName) {
        String deleteQuery = "DELETE FROM " + TABLE_COST_NAMES +
                " WHERE " + COLUMN_COST_NAME +
                " LIKE '" + costName + "'";

        SQLiteDatabase db = getWritableDatabase();
        int result = 1;

        try {
            db.execSQL(deleteQuery);
        } catch (Exception e) {
            result = 0;
            System.err.println("EXCEPTION IN 'deleteCostName()'.");
            e.printStackTrace();
        }
        finally {
            if (db != null)
                db.close();
        }

        return result;
    }


    // Возвращает список расходов, внесённых за последний месяц, состоящих из даты (1 1 2015)
    // и суммы расходов за эту дату, разделённых знаком '$' (1 1 2015$77.7)
    public List<String> getLastMonthEntriesGroupedByDays() {
        Calendar calendar = Calendar.getInstance();
        long currentDateInMilliseconds = calendar.getTimeInMillis();
        long oneDayInMilliseconds = 86400000;
        long monthAgoDateInMilliseconds = currentDateInMilliseconds - 30 * oneDayInMilliseconds;

        String getLastMonthEntriesQuery = "SELECT " +
                COLUMN_DAY + ", " + COLUMN_MONTH + ", " + COLUMN_YEAR +
                ", SUM(" + COLUMN_COST_VALUE + ") AS SUM " +
                " FROM " + TABLE_COST_VALUES +
                " WHERE " + COLUMN_DATE_IN_MILLISECONDS + " > " + monthAgoDateInMilliseconds +
                " GROUP BY " +
                COLUMN_DAY + ", " + COLUMN_MONTH + ", " + COLUMN_YEAR +
                " ORDER BY " + COLUMN_DATE_IN_MILLISECONDS +
                " DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        StringBuilder sb = new StringBuilder();
        List<String> lastMonthEntriesList = new ArrayList<>();

        try {
            c = db.rawQuery(getLastMonthEntriesQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                sb.append(c.getString(c.getColumnIndex(COLUMN_DAY)));
                sb.append(" ");

                sb.append(c.getString(c.getColumnIndex(COLUMN_MONTH)));
                sb.append(" ");

                sb.append(c.getString(c.getColumnIndex(COLUMN_YEAR)));
                sb.append("$");

                sb.append(c.getString(c.getColumnIndex("SUM")));
                sb.append(" ");

                lastMonthEntriesList.add(sb.toString());
                sb.setLength(0);

                c.moveToNext();
            }


        } catch (Exception e) {
            System.err.println("EXCEPTION IN 'getLastMonthEntriesGroupedByDays()'.");
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return lastMonthEntriesList;
    }


    // Возвращает массив последних тридцати введённых значений
    public String[] getLastThirtyEntries() {
        String getLastThirtyEnteredValuesQuery = "SELECT " +
                COLUMN_DAY + ", " + COLUMN_MONTH + ", " + COLUMN_YEAR + ", " + COLUMN_COST_NAME + ", " + COLUMN_COST_VALUE +
                " FROM " + TABLE_COST_VALUES +
                " ORDER BY " + COLUMN_DATE_IN_MILLISECONDS +
                " DESC " +
                " LIMIT " + 30;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<String> lastThirtyEntriesList = new ArrayList<>(30);
        StringBuilder sb = new StringBuilder();

        try {
            c = db.rawQuery(getLastThirtyEnteredValuesQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                sb.append(c.getString(c.getColumnIndex(COLUMN_DAY)));
                sb.append(" ");

                sb.append(c.getInt(c.getColumnIndex(COLUMN_MONTH)) + 1);
                sb.append(" ");

                sb.append(c.getString(c.getColumnIndex(COLUMN_YEAR)));
                sb.append(": ");

                sb.append(c.getString(c.getColumnIndex(COLUMN_COST_NAME)));
                sb.append(" ");

                sb.append(c.getString(c.getColumnIndex(COLUMN_COST_VALUE)));
                sb.append(" руб.");

                lastThirtyEntriesList.add(sb.toString());
                sb.setLength(0);

                c.moveToNext();
            }

        } catch (Exception e) {
            System.err.println("EXCEPTION IN 'getLastThirtyEntries()'.");
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        String[] lastThirtyEntriesArray = new String[lastThirtyEntriesList.size()];
        lastThirtyEntriesList.toArray(lastThirtyEntriesArray);

        return lastThirtyEntriesArray;
    }


    // Возвращает массив, состоящий из строк с названием и значением расходов
    // по заданной категории за заданный день
    public String[] getEntriesOnSpecifiedDateAndCostName(int day, int month, int year, String costName) {
        String getEntriesOnSpecifiedDateByCostNameQuery = "SELECT " +
                COLUMN_COST_NAME + ", " + COLUMN_COST_VALUE +
                " FROM " + TABLE_COST_VALUES +
                " WHERE " + COLUMN_DAY + " = " + day +
                " AND " + COLUMN_MONTH + " = " + month +
                " AND " + COLUMN_YEAR + " = " + year +
                " AND " + COLUMN_COST_NAME + " LIKE '" + costName + "'";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<String> listOfEntries = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        try {
            c = db.rawQuery(getEntriesOnSpecifiedDateByCostNameQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                sb.append(c.getString(c.getColumnIndex(COLUMN_COST_NAME)));
                sb.append(":    ");

                sb.append(c.getString(c.getColumnIndex(COLUMN_COST_VALUE)));
                sb.append(" руб.");

                listOfEntries.add(sb.toString());
                sb.setLength(0);

                c.moveToNext();
            }

        } catch (Exception e) {
            System.err.println("EXCEPTION IN 'getEntriesOnSpecifiedDateAndCostName()'.");
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        String[] entriesArray = new String[listOfEntries.size()];
        listOfEntries.toArray(entriesArray);

        return entriesArray;
    }









    // Добавляет записи в таблицу предстоящих событий (TABLE_EVENTS)
    public void addNewEvent(String eventDescription, String eventDate, long eventDateInMilliseconds) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_EVENT_DESCRIPTION, eventDescription);
        values.put(COLUMN_EVENT_DATE, eventDate);
        values.put(COLUMN_EVENT_DATE_IN_MILLISECONDS, eventDateInMilliseconds);

        db.insert(TABLE_EVENTS, null, values);
        db.close();
    }

    public String[] getEvents() {
        removeOldEvents();

        String getEventsQuery = "select * from " + TABLE_EVENTS +
                " order by " + COLUMN_EVENT_DATE_IN_MILLISECONDS + " ASC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        String string = "";
        List<String> listStrings = new ArrayList<>();

        try {
            c = db.rawQuery(getEventsQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast() && listStrings.size() < 10) {
                if (c.getString(c.getColumnIndex("_id")) != null) {
                    string += c.getString(c.getColumnIndex(COLUMN_EVENT_DATE)) + "$";
                    string += c.getString(c.getColumnIndex(COLUMN_EVENT_DESCRIPTION));
                }
                listStrings.add(string);
                string = "";
                c.moveToNext();
            }

        } catch (Exception e) {}
        finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();

            String[] events = new String[listStrings.size()];
            listStrings.toArray(events);

            return events;
        }

    }

    // Удаляет записи о событиях, которые уже прошли
    private void removeOldEvents() {
        Calendar calendar = Calendar.getInstance();
        SQLiteDatabase db = getWritableDatabase();
        long currentTimeInMilliseconds = calendar.getTimeInMillis();

        String removeFromEventsQuery = "delete from " + TABLE_EVENTS +
                " where " + COLUMN_EVENT_DATE_IN_MILLISECONDS + " < " + currentTimeInMilliseconds;

        db.execSQL(removeFromEventsQuery);
        db.close();
    }

    // Удаляет из таблицы "TABLE_EVENTS" запись с данным описание
    public void removeEvent(String eventDescription) {
        SQLiteDatabase db = getWritableDatabase();

        String removeQuery = "delete from " + TABLE_EVENTS +
                " where " + COLUMN_EVENT_DESCRIPTION + " = '" + eventDescription + "'";

        db.execSQL(removeQuery);
        db.close();
    }

}
