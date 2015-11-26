package com.example.costs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Саня on 21.11.2015.
 */
public class CostsDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "costs.db";
    private static final String TABLE_COSTS = "costs";
    private static final String TABLE_LAST_ENTERED_VALUES = "lastenteredvalues";
    private static final String TABLE_PERIOD = "period";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_COST_TYPE = "coststype";
    private static final String COLUMN_MONTHLYCOSTS = "monthlycosts";
    private static final String COLUMN_COSTS = "costs";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DATE_IN_MILLISECONDS = "milliseconds";
    private static final String COLUMN_NOTE = "note";
    private static final String COLUMN_PERIOD = "period";

    public enum CostType {
        FOOD, CLOTHES, COMMUNAL_RENT,
        ELECTRONICS, TRANSPORT, OTHERS,
        GOODS, SERVICES
    }



    public CostsDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPrimaryTableQuery = "CREATE TABLE " + TABLE_COSTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COST_TYPE + " TEXT, " +
                COLUMN_MONTH + " INTEGER, " +
                COLUMN_YEAR + " INTEGER, " +
                COLUMN_MONTHLYCOSTS + " TEXT, " +
                COLUMN_NOTE + " TEXT " +
                ");";
        String createSecondaryTableQuery = "CREATE TABLE " + TABLE_LAST_ENTERED_VALUES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COST_TYPE + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_DATE_IN_MILLISECONDS + " TEXT, " +
                COLUMN_COSTS + " TEXT, " +
                COLUMN_NOTE + " TEXT " +
                ");";
        String createTablePeriod = "CREATE TABLE " + TABLE_PERIOD + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PERIOD + " TEXT " +
                ");";

        db.execSQL(createPrimaryTableQuery);
        db.execSQL(createSecondaryTableQuery);
        db.execSQL(createTablePeriod);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_COSTS);
        onCreate(db);
    }






    // Добавляет запись в колонку "COLUMN_MONTHLYCOSTS" за указанный период
    // в соответсвие с типом расходов ("costType")
    // Если запись данного типа расходов за соответсвующий месяц и год
    // уже присутствует в базе данных - она заменяется новой ("costsValue")
    // Также производится добавление в таблицу "TABLE_PERIOD"
    public void addCosts(int month, int year, CostType costType, String costsValue) {
        /*
        if (!costsValue.equals("0.00")) {
            addLastValues(costType, costsValue);
        }
        */
        addPeriod(month, year);

        SQLiteDatabase db = getWritableDatabase();

        if (getCosts(month, year, costType).equals("error")) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_MONTH, month);
            values.put(COLUMN_YEAR, year);
            values.put(COLUMN_COST_TYPE, String.valueOf(costType));
            values.put(COLUMN_MONTHLYCOSTS, costsValue);

            db.insert(TABLE_COSTS, null, values);
        }
        else {
            String updateQuery = "update " + TABLE_COSTS + " set " +
                    COLUMN_MONTHLYCOSTS + " = '" + costsValue + "' where " +
                    COLUMN_MONTH + " = " + month + " and " +
                    COLUMN_YEAR + " = " + year + " and " +
                    COLUMN_COST_TYPE + " = '" + String.valueOf(costType) + "'";

            db.execSQL(updateQuery);
        }
    }


    // Возвращает расходы за соответствующий месяц и год исходя из типа расходов.
    // В случае ошибки возвращает строку "error"
    public String getCosts(int month, int year, CostType costType) {

        String getQuery = "select " + COLUMN_MONTHLYCOSTS + " from " + TABLE_COSTS +
                " where " + COLUMN_MONTH + " = " + month +
                " and " + COLUMN_YEAR + " = " + year +
                " and " + COLUMN_COST_TYPE + " = '" + String.valueOf(costType) + "'";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        String dbResult = "error";

        try {
            c = db.rawQuery(getQuery, null);
            c.moveToFirst();

            dbResult = c.getString(c.getColumnIndex(COLUMN_MONTHLYCOSTS));
        } catch (Exception e) {}
        finally {
            if (c != null)
                c.close();

            return dbResult;
        }

    }

    // Возвращает 10 последних записей в базу данных
    public String[] getLastEnteredValues() {
       String getQuery = "select * from " + TABLE_LAST_ENTERED_VALUES +
               " order by " + COLUMN_DATE_IN_MILLISECONDS + " DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        String string = "";
        List<String> listStrings = new ArrayList<>(10);

        try {
            c = db.rawQuery(getQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast() && listStrings.size() < 10) {
                if (c.getString(c.getColumnIndex("_id")) != null) {
                    string += c.getString(c.getColumnIndex(COLUMN_DATE)) + ": ";
                    string += c.getString(c.getColumnIndex(COLUMN_COST_TYPE)) + "  ";
                    string += c.getString(c.getColumnIndex(COLUMN_COSTS)) + " руб.";
                }
                listStrings.add(string);
                string = "";
                c.moveToNext();
            }
        } catch (Exception e) {}
        finally {
            if (c != null)
                c.close();

            String[] lastEnteredValues = new String[listStrings.size()];
            listStrings.toArray(lastEnteredValues);

            return lastEnteredValues;
        }
    }

    // Добавляет записи в таблицу "TABLE_LAST_ENTERED_VALUES"
    public void addLastValues(CostType costType, String costsValue) {
        Calendar c = Calendar.getInstance();

        String currentDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        String currentMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
        String currentYear = String.valueOf(c.get(Calendar.YEAR));

        String currentDate = currentDay + "." + currentMonth + "." + currentYear;
        String currentDateInMilliseconds = String.valueOf(c.getTimeInMillis());

        String costTypeString = "";
        switch (costType) {
            case FOOD:
                costTypeString = "Еда";
                break;
            case CLOTHES:
                costTypeString = "Одежда";
                break;
            case GOODS:
                costTypeString = "Промтовары";
                break;
            case COMMUNAL_RENT:
                costTypeString = "Квартплата";
                break;
            case SERVICES:
                costTypeString = "Услуги";
                break;
            case TRANSPORT:
                costTypeString = "Транспорт";
                break;
            case ELECTRONICS:
                costTypeString = "Техника";
                break;
            case OTHERS:
                costTypeString = "Другое";
                break;
        }

        SQLiteDatabase db =getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, currentDate);
        values.put(COLUMN_DATE_IN_MILLISECONDS, currentDateInMilliseconds);
        values.put(COLUMN_COSTS, costsValue);
        values.put(COLUMN_COST_TYPE, costTypeString);

        db.insert(TABLE_LAST_ENTERED_VALUES, null, values);
    }


    // Добавляет запись в таблицу "TABLE_PERIOD", содержащую
    // строку из месяца и года, для которых имеются записи в
    // таблице "TABLE_COSTS"
    public void addPeriod(int month, int year) {
        String periodString = String.valueOf(month) + " " + String.valueOf(year);
        List<String> listOfExistingPeriods = getAllPeriods();

        if (!listOfExistingPeriods.contains(periodString)) {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_PERIOD, periodString);

            db.insert(TABLE_PERIOD, null, values);
        }
    }

    public List<String> getAllPeriods() {
        String getPeriodsQuery = "select * from " + TABLE_PERIOD;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<String> listOfPeriods = new ArrayList<>();
        String period = "";

        try {
            c = db.rawQuery(getPeriodsQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex("_id")) != null) {
                    listOfPeriods.add(c.getString(c.getColumnIndex(COLUMN_PERIOD)));
                }
                c.moveToNext();
            }
        } catch (Exception e) {}
        finally {
            if (c != null)
                c.close();

            return listOfPeriods;
        }
    }










    /*
    public void addTest() {
        int month = 1;
        int year = 1;
        CostType costType = CostType.FOOD;
        String costsValue = "12000";

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MONTH, month);
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_COST_TYPE, String.valueOf(costType));
        values.put(COLUMN_MONTHLYCOSTS, costsValue);

        db.insert(TABLE_COSTS, null, values);
    }

    public String getTest() {
        int month = 1;
        int year = 1;
        CostType costType = CostType.FOOD;
        String s = String.valueOf(costType);
        String costsValue = "12000";

        SQLiteDatabase db = getWritableDatabase();

        String query = "select * from " + TABLE_COSTS +
                " where " + COLUMN_COST_TYPE + " = '" + String.valueOf(costType) + "'";


        Cursor c = null;
        String dbResult = "error";

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            dbResult = c.getString(c.getColumnIndex(COLUMN_MONTHLYCOSTS));
        } catch (Exception e) {}
        finally {
            if (c != null)
                c.close();

            return dbResult;
        }

    }
*/
    public String getAllDB() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "select * from " + TABLE_COSTS + " where 1";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("_id")) != null) {
                dbString += c.getString(c.getColumnIndex(COLUMN_ID)) + " ";
                dbString += c.getString(c.getColumnIndex(COLUMN_MONTH)) + " ";
                dbString += c.getString(c.getColumnIndex(COLUMN_YEAR)) + " ";
                dbString += c.getString(c.getColumnIndex(COLUMN_COST_TYPE)) + " ";
                dbString += c.getString(c.getColumnIndex(COLUMN_MONTHLYCOSTS));
                dbString += "\n";
            }
            c.moveToNext();
        }

        c.close();
        return dbString;
    }


}
