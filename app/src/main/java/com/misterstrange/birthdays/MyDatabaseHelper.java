package com.misterstrange.birthdays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_info.db";
    private static final int DATABASE_VERSION = 1;

    // Define the table name and column names

    public static final String KEY_ID = "id";
    public static final String TABLE_NAME = "user_info";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_YEAR = "year";

    // Constructor
    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create the table when the database is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_MONTH + " TEXT, " +
                COLUMN_YEAR + " TEXT)";
        db.execSQL(createTable);
    }

    // Handle database upgrades if needed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // You can implement upgrade logic here if the database schema changes
    }

    public void addContact(Person person){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, person.getName());
        values.put(COLUMN_DATE, person.getDate());
        values.put(COLUMN_MONTH, person.getMonth());
        values.put(COLUMN_YEAR, person.getYear());


        db.insert(TABLE_NAME, null,  values);
        Log.d("dbharry", "Successfully inserted");
        db.close();
    }

    public List<Person> getAllPersons() {
        List<Person> personList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        String select = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(select, null);


        if (cursor != null && cursor.moveToFirst()) {
            do {

                Person person = new Person();

                person.setId(Integer.parseInt(cursor.getString(0)));
                person.setName(cursor.getString(1));
                person.setDate(cursor.getString(2));
                person.setMonth(cursor.getString(3));
                person.setYear(cursor.getString(4));

                personList.add(person);
            } while (cursor.moveToNext());
        }


        if (cursor != null) {
            cursor.close();
        }
        db.close(); // Close the database after use
        return personList;
    }

    public int updateContact (Person person){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, person.getName());
        values.put(COLUMN_DATE, person.getDate());
        values.put(COLUMN_MONTH, person.getMonth());
        values.put(COLUMN_YEAR, person.getYear());
        //Let's update now
        return db.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{String.valueOf(person.getId())});

    }

    public void deleteContactById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + "=?",new String[]{String.valueOf(id)});
        db.close();
    }





}
