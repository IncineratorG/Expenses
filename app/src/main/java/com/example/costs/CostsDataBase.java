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
 * Created by Саня on 29.01.2016.
 */
public class CostsDataBase extends SQLiteOpenHelper {


    // В базе данных месяца начинаются с 0

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "costs.db";

    private static final String TABLE_COSTS = "costs";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DAY = "day";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_COST_TYPE = "coststype";
    private static final String COLUMN_COST_VALUE = "costvalue";
    private static final String COLUMN_NOTE = "note";



    public CostsDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableCosts = "CREATE TABLE " + TABLE_COSTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COST_TYPE + " TEXT, " +
                COLUMN_DAY + " INTEGER, " +
                COLUMN_MONTH + " INTEGER, " +
                COLUMN_YEAR + " INTEGER, " +
                COLUMN_COST_VALUE + " REAL, " +
                COLUMN_NOTE + " TEXT " +
                ");";

        db.execSQL(createTableCosts);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_COSTS);
        onCreate(db);
    }




    // Добавляет новую запись по статье расходов (costType) в базу данных
    public void addCosts(double costValue, String costType) {
        Calendar calendar = Calendar.getInstance();

        int day =  calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY, day);
        values.put(COLUMN_MONTH, month);
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_COST_TYPE, costType);
        values.put(COLUMN_COST_VALUE, costValue);
        db.insert(TABLE_COSTS, null, values);
    }


    // Возвращает названия статей расходов, записи для которых
    // присутствуют в базе даных
    public List<String> getCostsTypes() {
        String getDistinctCostNamesQuery = "select distinct " +
                COLUMN_COST_TYPE + " from " +
                TABLE_COSTS;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<String> listOfCostsNames = new ArrayList<>();

        try {
            c = db.rawQuery(getDistinctCostNamesQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                listOfCostsNames.add(c.getString(c.getColumnIndex(COLUMN_COST_TYPE)));
                c.moveToNext();
            }

        } catch (Exception e) {
            System.err.println("Exception in 'getCostsNames()'");
        }
        finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return listOfCostsNames;
    }


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

}
