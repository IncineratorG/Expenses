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


    // В базе данных месяца начинаются с 0

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "costs.db";

    //private static final String TABLE_COSTS = "costs";
    //private static final String TABLE_COSTS_OLD = "costsold";
    private static final String COLUMN_ID = "_id";



    private static final String TABLE_COST_NAMES = "costnames";
    private static final String COLUMN_COST_NAME = "costname";

    private static final String TABLE_COST_VALUES = "costsvalues";
    private static final String COLUMN_DAY = "day";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_DATE_IN_MILLISECONDS = "dateinmilliseconds";
    private static final String COLUMN_COST_VALUE = "costvalue";
    private static final String COLUMN_NOTE = "note";


    public CostsDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableCostsNames = "CREATE TABLE " + TABLE_COST_NAMES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COST_NAME + " TEXT)";
        String createTableCostsValues = "CREATE TABLE " + TABLE_COST_VALUES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY + " INTEGER, " +
                COLUMN_MONTH + " INTEGER, " +
                COLUMN_YEAR + " INTEGER, " +
                COLUMN_DATE_IN_MILLISECONDS + " INTEGER, " +
                COLUMN_COST_NAME + " TEXT, " +
                COLUMN_COST_VALUE + " REAL)";

        db.execSQL(createTableCostsNames);
        db.execSQL(createTableCostsValues);
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


    // Возвращает названия статей расходов, записи для которых
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


    // Возвращает список строк, состоящих из даты (1 1 2015) и суммы расходов
    // за эту дату, разделённых знаком '$' (1 1 2015$77.7)
    public List<String> getLastMonthEntriesGroupedByDays() {
        //Calendar calendar = Calendar.getInstance();
        //long currentDateInMilliseconds = calendar.getTimeInMillis();
        //long oneDayInMilliseconds = 86400000;
        // long monthAgoDateInMilliseconds = currentDateInMilliseconds - 30 * oneDayInMilliseconds;

        String getLastMonthEntriesQuery = "SELECT " +
                COLUMN_DAY + ", " + COLUMN_MONTH + ", " + COLUMN_YEAR +
                ", SUM(" + COLUMN_COST_VALUE + ") AS SUM " +
                " FROM " + TABLE_COST_VALUES +
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

/*
    // Возвращает суммарную величину расходов типа 'costType'
    // за заданный период. Если поле 'day' отрицательно -
    // при выборке значений учитывается только указанные месяц, год и тип расходов
    public double getCostValue(int day, int month, int year, String costType) {

        String getCostValueQuery = null;

        if (day >= 0) {
            getCostValueQuery = "select distinct " +
                    COLUMN_COST_VALUE + " from " +
                    TABLE_COSTS + " where " +
                    COLUMN_COST_TYPE + " = '" +
                    costType + "' and " +
                    COLUMN_YEAR + " = " +
                    year + " and " +
                    COLUMN_MONTH + " = " +
                    month + " and " +
                    COLUMN_DAY + " = " +
                    day;
        } else {
            getCostValueQuery = "select distinct " +
                    COLUMN_COST_VALUE + " from " +
                    TABLE_COSTS + " where " +
                    COLUMN_COST_TYPE + " = '" +
                    costType + "' and " +
                    COLUMN_YEAR + " = " +
                    year + " and " +
                    COLUMN_MONTH + " = " +
                    month;
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
            System.err.println("Exception in 'getCostValue()'.");
        }
        finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return sum;
    }


    // Удаляет статью расходов 'costTypeName' из базы данных
    public void deleteCostType(String costTypeName) {
        String deleteCostTypeQuery = "delete from " +
                TABLE_COSTS + " where " +
                COLUMN_COST_TYPE + " = '" +
                costTypeName + "'";

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL(deleteCostTypeQuery);
        } catch (Exception e) {
            System.err.println("Exception in 'deleteCostType()'");
        }
        finally {
            if (db != null)
                db.close();
        }
    }


    // Возвращает записи в базе данных за последний месяц
    public String[] getLastMonthEntriesGroupedByDays() {

        // Получаем текущее время в миллисекундах
        Calendar c = Calendar.getInstance();
        long currentDateInMilliseconds = c.getTimeInMillis();
        long oneDyaInMilliseconds = 86400000;

        // Вычитаем из текущей даты 30 дней
        long monthAgoInMilliseconds = currentDateInMilliseconds - 30 * oneDyaInMilliseconds;
        c.setTimeInMillis(monthAgoInMilliseconds);

        // Переводим получившееся значение в миллисекундах в день, месяц и год
        int monthAgoDay = c.get(Calendar.DAY_OF_MONTH);
        int monthAgoMonth = c.get(Calendar.MONTH);
        int monthAgoYear = c.get(Calendar.YEAR);

        String query = "select * from " + TABLE_COSTS +
                " where " + COLUMN_DAY + " > " + monthAgoDay +
                " and " + COLUMN_MONTH + " >= " + monthAgoMonth +
                " and " + COLUMN_YEAR + " >= " + monthAgoYear;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                String str = cursor.getString(cursor.getColumnIndex(COLUMN_DAY)) +
                        " " + cursor.getString(cursor.getColumnIndex(COLUMN_MONTH)) +
                        " " + cursor.getString(cursor.getColumnIndex(COLUMN_YEAR)) +
                        " " + cursor.getString(cursor.getColumnIndex(COLUMN_COST_TYPE)) +
                        " " + cursor.getString(cursor.getColumnIndex(COLUMN_COST_VALUE));
                System.out.println(str);
                cursor.moveToNext();
            }

        } catch (Exception e) {
            System.out.println("Exception in 'getLastMonthEntriesGroupedByDays()'.");
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (db != null)
                db.close();
        }

        return null;
    }


    public String[] getAllPeriods() {
        String getAllPeriodsQuery = "select distinct " +
                COLUMN_MONTH + ", " +
                COLUMN_YEAR + " from " +
                TABLE_COSTS;

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
            System.err.println("Exception in 'getAllPeriods()'.");
        }
        finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();

            periodsArray = new String[listOfPeriods.size()];
            for (int i = 0; i < periodsArray.length; ++i) {
                periodsArray[i] = listOfPeriods.get((listOfPeriods.size() - 1) - i);
            }
        }

        return periodsArray;
    }
*/
}
