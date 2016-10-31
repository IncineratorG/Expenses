package com.example.newcosts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class CostsDB extends SQLiteOpenHelper {

    /*  В базе данных месяца начинаются с 0  */
    private static final String tag = "CostsDbTag";

    private static CostsDB dbInstance;


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "costs.db";

    // ***************** TABLE_COST_NAMES ***********************
    private static final String TABLE_COST_NAMES = "costnames";
    private static final String ID_N = "id_n";
    private static final String COST_NAME = "costname";
    private static final String IS_ACTIVE = "isactive";
    // **********************************************************

    // ***************** TABLE_COST_VALUES **********************
    private static final String TABLE_COST_VALUES = "costvalues";
    private static final String ID_C = "id_c";
    private static final String ID_N_FK = "id_nfk";
    private static final String DAY = "day";
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private static final String DATE_IN_MILLISECONDS = "dateinmilliseconds";
    private static final String COST_VALUE = "costvalue";
    private static final String IMAGE = "image";
    private static final String TEXT = "text";
    // **********************************************************


    private CostsDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public static CostsDB getInstance(Context context) {
        if (dbInstance != null)
            return dbInstance;
        else {
            dbInstance = new CostsDB(context.getApplicationContext(), null, null, 1);
            return dbInstance;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Таблица, хранящая названия статей расходов
        String createTableCostsNames = "CREATE TABLE " + TABLE_COST_NAMES + " (" +
                ID_N + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COST_NAME + " TEXT, " +
                IS_ACTIVE + " INTEGER)";

        // Таблица, хранящая записи о расходах по соответствующей статье расходов
        String createTableCostsValues = "CREATE TABLE " + TABLE_COST_VALUES + " (" +
                ID_C + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ID_N_FK + " INTEGER, " +
                DAY + " INTEGER, " +
                MONTH + " INTEGER, " +
                YEAR + " INTEGER, " +
                DATE_IN_MILLISECONDS + " INTEGER, " +
                COST_VALUE + " REAL, " +
                IMAGE + " BLOB, " +
                TEXT + " TEXT)";

        db.execSQL(createTableCostsNames);
        db.execSQL(createTableCostsValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COST_NAMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COST_VALUES);
        onCreate(db);
    }



    // Возвращает список из ID и названий всех активных статей расходов
    public List<String> getActiveCostNames() {
        String getCostNamesQuery = "SELECT * FROM " + TABLE_COST_NAMES +
                " WHERE " + IS_ACTIVE + " = " + 1;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<String> costNamesList = new ArrayList<>();

        try {
            c = db.rawQuery(getCostNamesQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                costNamesList.add(c.getString(c.getColumnIndex(ID_N)) + "$" + c.getString(c.getColumnIndex(COST_NAME)));
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return costNamesList;
    }
    public String[] getActiveCostNames_V2() {
        String query = "SELECT COUNT(" + ID_N_FK + ") AS quantity," +
                COST_NAME + ", " +
                ID_N +
                " FROM " + TABLE_COST_NAMES +
                " LEFT OUTER JOIN " + TABLE_COST_VALUES +
                " ON " + ID_N + " = " + ID_N_FK +
                " WHERE " + IS_ACTIVE + " = " + 1 +
                " GROUP BY " + ID_N +
                " ORDER BY quantity DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
//        List<String> list = new ArrayList<>();
        String[] array = null;

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();
            array = new String[c.getCount() * 2];
            int arrayIndexCounter = 0;

            while (!c.isAfterLast()) {
                array[arrayIndexCounter++] = c.getString(c.getColumnIndex(COST_NAME));
                array[arrayIndexCounter++] = c.getString(c.getColumnIndex(ID_N));
//                list.add(c.getString(c.getColumnIndex(COST_NAME)));
//                list.add(c.getString(c.getColumnIndex(ID_N)));
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }
//        array = new String[list.size()];
//        list.toArray(array);

        return array;
    }

    // Добавляет новую статью расходов. В случае успеха возвращает "true".
    // Если запись присутствует в базе - возвращает "false".
    public boolean addCostName(String costName) {
        String checkCostNameQuery = "SELECT " + COST_NAME + ", " + IS_ACTIVE + ", " + ID_N +
                " FROM " + TABLE_COST_NAMES +
                " WHERE " + COST_NAME +
                " LIKE '" + costName + "'";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        boolean result = true;

        try {
            c = db.rawQuery(checkCostNameQuery, null);

            // Если запись присутствует в базе, но не активна - делаем
            // её активной. Иначе добавляем новую запись в базу
            if (c.moveToFirst()) {
                int is_active = c.getInt(c.getColumnIndex(IS_ACTIVE));
                int id_n = c.getInt(c.getColumnIndex(ID_N));

                if (is_active == 1)
                    result = false;
                else {
                    String makeCostNameActiveQuery = "UPDATE " + TABLE_COST_NAMES +
                            " SET " + IS_ACTIVE + " = " + 1 +
                            " WHERE " + ID_N + " = " + id_n;

                    db.execSQL(makeCostNameActiveQuery);
                }
            }
            else {
                ContentValues values = new ContentValues();
                values.put(COST_NAME, costName);
                values.put(IS_ACTIVE, 1);

                db.insert(TABLE_COST_NAMES, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return result;
    }

    // Возвращает суммарное значение расходов для данного "id_n" за
    // заданный период. Если поле 'day' отрицательно - при выборке
    // значений учитывается только указанные месяц, год и тип расходов.
    public double getCostValue(int day, int month, int year, int id_n) {
        String getCostValueQuery = null;

        if (day >= 0) {
            getCostValueQuery = "SELECT " + COST_VALUE +
                    " FROM " + TABLE_COST_VALUES +
                    " WHERE " + DAY + " = " + day +
                    " AND " + MONTH + " = " + month +
                    " AND " + YEAR + " = " + year +
                    " AND " + ID_N_FK + " = " + id_n;
        } else {
            getCostValueQuery = "SELECT " + COST_VALUE +
                    " FROM " + TABLE_COST_VALUES +
                    " WHERE " + MONTH + " = " + month +
                    " AND " + YEAR + " = " + year +
                    " AND " + ID_N_FK + " = " + id_n;
        }

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        double sum = 0.0;

        try {
            c = db.rawQuery(getCostValueQuery, null);

            c.moveToFirst();
            while (!c.isAfterLast()) {
                sum = sum + c.getDouble(c.getColumnIndex(COST_VALUE));
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return sum;
    }

    // Добавляет новую запись по статье расходов с соответсвующим id_n в базу
    public void addCosts(double costValue, int id_n) {
        Calendar calendar = Calendar.getInstance();

        int day =  calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        long currentDateInMilliseconds = calendar.getTimeInMillis();

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DAY, day);
        values.put(MONTH, month);
        values.put(YEAR, year);
        values.put(DATE_IN_MILLISECONDS, currentDateInMilliseconds);
        values.put(ID_N_FK, id_n);
        values.put(COST_VALUE, costValue);
        db.insert(TABLE_COST_VALUES, null, values);

        db.close();
    }

    // Удаляет (делает не активной) запись в таблице TABLE_COST_NAMES
    // с соответствующим ID
    public boolean deleteCostName(int id_n) {
        String deleteCostNameQuery = "UPDATE " + TABLE_COST_NAMES +
                " SET " + IS_ACTIVE + " = " + 0 +
                " WHERE " + ID_N + " = " + id_n;

        SQLiteDatabase db = getWritableDatabase();
        boolean result = true;

        try {
            db.execSQL(deleteCostNameQuery);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (db != null)
                db.close();
        }

        return result;
    }

    // Возвращает массив не активных записей в таблице TABLE_COST_NAMES
    public String[] getNonActiveCostNames() {
        String getNonActiveCostNamesQuery = "SELECT " + COST_NAME +
                " FROM " + TABLE_COST_NAMES +
                " WHERE " + IS_ACTIVE + " = " + 0;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<String> listOfNonActiveCostNames = new ArrayList<>();

        try {
            c = db.rawQuery(getNonActiveCostNamesQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                listOfNonActiveCostNames.add(c.getString(c.getColumnIndex(COST_NAME)));

                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        String[] arrayOfNonActiveCostNames = new String[listOfNonActiveCostNames.size()];
        listOfNonActiveCostNames.toArray(arrayOfNonActiveCostNames);

        return arrayOfNonActiveCostNames;
    }

    // Переименовывает статью расходов с данным id_n на newCostName. В случае
    // успеха возвращает 0. Если запись с таким названием есть в базе, но
    // неактивна - возвращает 1. Если запись есть в базе и активна - возвращает 2
    public int renameCostName(int id_n, String newCostName) {
        String checkNewCostNameQuery = "SELECT " + COST_NAME + ", " + IS_ACTIVE +
                " FROM " + TABLE_COST_NAMES +
                " WHERE " + COST_NAME + " LIKE '" + newCostName + "'";

        String renameQuery = "UPDATE " + TABLE_COST_NAMES +
                " SET " + COST_NAME + " = '" + newCostName +
                "' WHERE " + ID_N + " = " + id_n;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        int result = 0;

        try {
            c = db.rawQuery(checkNewCostNameQuery, null);

            if (c.moveToFirst()) {
                int is_active = c.getInt(c.getColumnIndex(IS_ACTIVE));
                if (is_active == 0)
                    result = 1;
                else if (is_active == 1)
                    result = 2;
            } else
                db.execSQL(renameQuery);
        } catch (Exception e){
            e.printStackTrace();
            result = 2;
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return result;
    }

    // Возвращает заданное число последних введённых сумм расходов
    public String[] getLastEntries(int numberOfEntries) {
        String getLastEntriesQuery = "SELECT * " +
                " FROM " + TABLE_COST_VALUES +
                " INNER JOIN " + TABLE_COST_NAMES +
                " ON " + TABLE_COST_VALUES + "." + ID_N_FK + " = " + TABLE_COST_NAMES + "." + ID_N +
                " ORDER BY " + DATE_IN_MILLISECONDS + " DESC " +
                " LIMIT " + numberOfEntries;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<String> listOfEntries = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
        numberFormat.setGroupingUsed(false);

        try {
            c = db.rawQuery(getLastEntriesQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                sb.append(c.getString(c.getColumnIndex(DAY)));
                sb.append(".");

                sb.append(c.getInt(c.getColumnIndex(MONTH)) + 1);
                sb.append(" ");

                sb.append(c.getString(c.getColumnIndex(COST_NAME)));
                sb.append(": ");

                sb.append(numberFormat.format(c.getDouble(c.getColumnIndex(COST_VALUE))));
                sb.append(" руб.");
                sb.append(Constants.SEPARATOR_MILLISECONDS);

                sb.append(c.getString(c.getColumnIndex(DATE_IN_MILLISECONDS)));

                listOfEntries.add(sb.toString());
                sb.setLength(0);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        String[] array = new String[listOfEntries.size()];
        listOfEntries.toArray(array);

        getLastEntries_V2(numberOfEntries);

        return array;
    }
    public String[] getLastEntries_V2(int numberOfEntries) {
        String getLastEntriesQuery = "SELECT " +
                DAY + ", " +
                MONTH + ", " +
                COST_NAME + ", " +
                COST_VALUE + ", " +
                DATE_IN_MILLISECONDS +
                " FROM " + TABLE_COST_VALUES +
                " INNER JOIN " + TABLE_COST_NAMES +
                " ON " + TABLE_COST_VALUES + "." + ID_N_FK + " = " + TABLE_COST_NAMES + "." + ID_N +
                " ORDER BY " + DATE_IN_MILLISECONDS + " DESC " +
                " LIMIT " + numberOfEntries;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        String[] array = null;

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
        numberFormat.setGroupingUsed(false);

        try {
            c = db.rawQuery(getLastEntriesQuery, null);
            c.moveToFirst();
            array = new String[c.getCount() * 5];
            int arrayIndexCounter = 0;

            while (!c.isAfterLast()) {
                array[arrayIndexCounter++] = c.getString(c.getColumnIndex(DAY));
                array[arrayIndexCounter++] = String.valueOf(c.getInt(c.getColumnIndex(MONTH)) + 1);
                array[arrayIndexCounter++] = c.getString(c.getColumnIndex(COST_NAME));
                array[arrayIndexCounter++] = numberFormat.format(c.getDouble(c.getColumnIndex(COST_VALUE)));
                array[arrayIndexCounter++] = c.getString(c.getColumnIndex(DATE_IN_MILLISECONDS));
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return array;
    }


    // Удаляет запись из таблицы TABLE_COST_VALUES, дата в миллисекундах
    // для которой равна "dateInMilliseconds"
    public boolean removeCostValue(long dateInMillisecond) {
        String deleteQuery = "DELETE FROM " + TABLE_COST_VALUES +
                " WHERE " + DATE_IN_MILLISECONDS + " = " + dateInMillisecond;

        SQLiteDatabase db = getWritableDatabase();
        boolean result = true;

        try {
            db.execSQL(deleteQuery);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (db != null)
                db.close();
        }

        return result;
    }

    // Возвращает массив строк, состоящих из месяца, года и суммарного значения расходов
    // за эти месяц и год, сгруппированные по месяцу и году в формате: 5$1989$575
    public String[] getSumByMonthsEntries() {
        String query = "SELECT " + MONTH + ", " + YEAR + ", SUM(" + COST_VALUE + ") AS SUM, " + DATE_IN_MILLISECONDS +
                " FROM " + TABLE_COST_VALUES +
                " GROUP BY " + MONTH + ", " + YEAR +
                " ORDER BY " + DATE_IN_MILLISECONDS + " DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<String> listOfEntries = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
        numberFormat.setGroupingUsed(false);

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                sb.append(c.getString(c.getColumnIndex(MONTH)));
                sb.append("$");

                sb.append(c.getString(c.getColumnIndex(YEAR)));
                sb.append("$");

                sb.append(numberFormat.format(c.getDouble(c.getColumnIndex("SUM"))));

                listOfEntries.add(sb.toString());
                sb.setLength(0);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        String[] array = new String[listOfEntries.size()];
        listOfEntries.toArray(array);

        return array;
    }

    // Возвращает массив строк, состоящих из названия статьи расходов и суммарного
    // значения расходов по этой статье за выбранный месяц в формате: Продукты$102520
    public String[] getCostValuesArrayOnDate(int month, int year) {
        String query = "SELECT " +
                TABLE_COST_NAMES + "." + COST_NAME + ", SUM(" + TABLE_COST_VALUES + "." + COST_VALUE + ") AS SUM, " +
                TABLE_COST_VALUES + "." + MONTH + ", " + TABLE_COST_VALUES + "." + YEAR + ", " +
                TABLE_COST_VALUES + "." + DATE_IN_MILLISECONDS + ", " + TABLE_COST_VALUES + "." + ID_N_FK + ", " +
                TABLE_COST_NAMES + "." + ID_N +
                " FROM " + TABLE_COST_VALUES +
                " INNER JOIN " + TABLE_COST_NAMES +
                " ON " + TABLE_COST_VALUES + "." + ID_N_FK + " = " + TABLE_COST_NAMES + "." + ID_N +
                " WHERE " + MONTH + " = " + month +
                " AND " + YEAR + " = " + year +
                " GROUP BY " + ID_N_FK +
                " ORDER BY " + "SUM" + " DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        StringBuilder sb = new StringBuilder();
        List<String> listOfEntries = new ArrayList<>();

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
        numberFormat.setGroupingUsed(false);

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                sb.append(c.getString(c.getColumnIndex(COST_NAME)));
                sb.append("$");

                sb.append(numberFormat.format(c.getDouble(c.getColumnIndex("SUM"))));

                listOfEntries.add(sb.toString());
                sb.setLength(0);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        String[] arrayOfEntries = new String[listOfEntries.size()];
        listOfEntries.toArray(arrayOfEntries);

        return arrayOfEntries;
    }

    // Возвращает массив строк, состоящих из дня (даты) и сумм расходов за этот день в
    // формате: 3.5.1989$2500%milliseconds. Группировка по дням не производится
    public String[] getCostValuesArrayOnDateAndCostName(int month, int year, String costName) {
        String query = "SELECT " + COST_VALUE + ", " + DAY + ", " + DATE_IN_MILLISECONDS +
                " FROM " + TABLE_COST_VALUES +
                " WHERE " + MONTH + " = " + month +
                " AND " + YEAR + " = " + year +
                " AND " + ID_N_FK + " IN" +
                    " (SELECT " + ID_N +
                    " FROM " + TABLE_COST_NAMES +
                    " WHERE " + COST_NAME + " LIKE '" + costName + "')" +
                " ORDER BY " + DAY + " ASC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        StringBuilder sb = new StringBuilder();
        List<String> listOfEntries = new ArrayList<>();

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
        numberFormat.setGroupingUsed(false);

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                sb.append(c.getString(c.getColumnIndex(DAY)));
                sb.append(".");
                sb.append(month + 1);
                sb.append(".");
                sb.append(year);
                sb.append("$");

                sb.append(numberFormat.format(c.getDouble(c.getColumnIndex(COST_VALUE))));
                sb.append("%");

                sb.append(c.getString(c.getColumnIndex(DATE_IN_MILLISECONDS)));

                listOfEntries.add(sb.toString());
                sb.setLength(0);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        String[] arrayOfEntries = new String[listOfEntries.size()];
        listOfEntries.toArray(arrayOfEntries);

        return arrayOfEntries;
    }

    public String[] test(long initialDateInMilliseconds, long endingDateInMilliseconds) {
        String query = "SELECT " +
                TABLE_COST_NAMES + "." + COST_NAME + ", SUM(" + TABLE_COST_VALUES + "." + COST_VALUE + ") AS SUM, " +
                TABLE_COST_VALUES + "." + MONTH + ", " + TABLE_COST_VALUES + "." + YEAR + ", " +
                TABLE_COST_VALUES + "." + DATE_IN_MILLISECONDS + ", " + TABLE_COST_VALUES + "." + ID_N_FK + ", " +
                TABLE_COST_NAMES + "." + ID_N +
                " FROM " + TABLE_COST_VALUES +
                " INNER JOIN " + TABLE_COST_NAMES +
                " ON " + TABLE_COST_VALUES + "." + ID_N_FK + " = " + TABLE_COST_NAMES + "." + ID_N +
                " WHERE " + DATE_IN_MILLISECONDS + " BETWEEN " + initialDateInMilliseconds + " AND " + endingDateInMilliseconds +
                " GROUP BY " + ID_N_FK +
                " ORDER BY " + "SUM" + " DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        StringBuilder sb = new StringBuilder();
        List<String> listOfEntries = new ArrayList<>();
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(2);

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                sb.append(c.getString(c.getColumnIndex(COST_NAME)));
                sb.append("$");

                sb.append(numberFormat.format(c.getDouble(c.getColumnIndex("SUM"))));

                listOfEntries.add(sb.toString());
                sb.setLength(0);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        String[] arrayOfEntries = new String[listOfEntries.size()];
        listOfEntries.toArray(arrayOfEntries);

        return arrayOfEntries;
    }



    public String getCostByDateInMillis(long milliseconds) {
        String query = "SELECT " + TABLE_COST_NAMES + "." + COST_NAME + ", " +
                TABLE_COST_VALUES + "." + COST_VALUE + ", " +
                TABLE_COST_VALUES + "." + DAY + ", " +
                TABLE_COST_VALUES + "." + MONTH + ", " +
                TABLE_COST_VALUES + "." + YEAR + ", " +
                TABLE_COST_VALUES + "." + DATE_IN_MILLISECONDS +
                " FROM " + TABLE_COST_VALUES +
                " INNER JOIN " + TABLE_COST_NAMES +
                " ON " + TABLE_COST_VALUES + "." + ID_N_FK + " = " + TABLE_COST_NAMES + "." + ID_N +
                " WHERE " + DATE_IN_MILLISECONDS + " = " + milliseconds;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        StringBuilder sb = new StringBuilder();
        String result = null;
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
        numberFormat.setGroupingUsed(false);

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            if (!c.isAfterLast()) {
                sb.append(c.getString(c.getColumnIndex(COST_NAME)));
                sb.append("%");

                sb.append(numberFormat.format(c.getDouble(c.getColumnIndex(COST_VALUE))));
                sb.append("%");

                sb.append(c.getString(c.getColumnIndex(DAY)));
                sb.append("%");

                sb.append(c.getInt(c.getColumnIndex(MONTH)) + 1);
                sb.append("%");

                sb.append(c.getString(c.getColumnIndex(YEAR)));
                sb.append("%");

                sb.append(c.getString(c.getColumnIndex(DATE_IN_MILLISECONDS)));

                result = sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }


        return result;
    }

    public void addCostInMilliseconds(int id_n, String costValue, long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        SQLiteDatabase db = getWritableDatabase();
        while (true) {
            String query = "SELECT " + DATE_IN_MILLISECONDS +
                    " FROM " + TABLE_COST_VALUES +
                    " WHERE " + DATE_IN_MILLISECONDS + " = " + milliseconds;

            try {
                if (db.rawQuery(query, null).moveToFirst())
                    ++milliseconds;
                else break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ContentValues values = new ContentValues();
        values.put(DAY, day);
        values.put(MONTH, month);
        values.put(YEAR, year);
        values.put(DATE_IN_MILLISECONDS, milliseconds);
        values.put(ID_N_FK, id_n);
        values.put(COST_VALUE, costValue);
        db.insert(TABLE_COST_VALUES, null, values);

        db.close();
    }









    public List<String> getAllTableCostNames() {
        String query = "SELECT * FROM " + TABLE_COST_NAMES;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        StringBuilder sb = new StringBuilder();
        List<String> list = new ArrayList<>();

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                sb.append(c.getString(c.getColumnIndex(ID_N)));
                sb.append("#");

                sb.append(c.getString(c.getColumnIndex(COST_NAME)));
                sb.append("#");

                sb.append(c.getString(c.getColumnIndex(IS_ACTIVE)));

                list.add(sb.toString());
                sb.setLength(0);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }

        return list;
    }
}
