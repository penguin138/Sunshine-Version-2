/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */

public void testCreateDb() throws Throwable {
// build a HashSet of all of the table names we wish to look for
// Note that there will be another table in the DB that stores the
// Android metadata (db version information)
final HashSet<String> tableNameHashSet = new HashSet<String>();
tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
SQLiteDatabase db = new WeatherDbHelper(
this.mContext).getWritableDatabase();
assertEquals(true, db.isOpen());

// have we created the tables we want?
Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

assertTrue("Error: This means that the database has not been created correctly",
c.moveToFirst());

// verify that the tables have been created
do {
tableNameHashSet.remove(c.getString(0));
} while( c.moveToNext() );

// if this fails, it means that your database doesn't contain both the location entry
// and weather entry tables
assertTrue("Error: Your database was created without both the location entry and weather entry tables",
tableNameHashSet.isEmpty());

// now, do our tables contain the correct columns?
c = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")",
null);

assertTrue("Error: This means that we were unable to query the database for table information.",
c.moveToFirst());

// Build a HashSet of all of the column names we want to look for
final HashSet<String> locationColumnHashSet = new HashSet<String>();
locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

int columnNameIndex = c.getColumnIndex("name");
do {
String columnName = c.getString(columnNameIndex);
locationColumnHashSet.remove(columnName);
} while(c.moveToNext());

// if this fails, it means that your database doesn't contain all of the required location
// entry columns
assertTrue("Error: The database doesn't contain all of the required location entry columns",
locationColumnHashSet.isEmpty());
db.close();
}


    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testLocationTable() {
        // First step: Get reference to writable database
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        // Create ContentValues of what you want to insert
        ContentValues valuesToInsert = new ContentValues();
        valuesToInsert.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Moscow");
        valuesToInsert.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, "bla");
        valuesToInsert.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, 1);
        valuesToInsert.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, 1);
        // (you can use the createNorthPoleLocationValues if you wish)

        // Insert ContentValues into database and get a row ID back
        long id = db.insert(WeatherContract.LocationEntry.TABLE_NAME,null,valuesToInsert);
        // Query the database and receive a Cursor back
        Cursor cursor = db.query(WeatherContract.LocationEntry.TABLE_NAME,null, WeatherContract.LocationEntry._ID + " = "+ String.valueOf(id),null,null,null,null,null);
        // Move the cursor to a valid database row
        cursor.moveToFirst();
        // Validate data in resulting Cursor with the original ContentValues
        ContentValues returnedValues = new ContentValues();
        returnedValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cursor.getString(cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_CITY_NAME)));
        returnedValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, cursor.getString(cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING)));
        returnedValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, cursor.getInt(cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LAT)));
        returnedValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, cursor.getInt(cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LONG)));
        cursor.close();
        db.close();
        assertEquals(valuesToInsert,returnedValues);
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)

        // Finally, close the cursor and database

    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testWeatherTable() {
        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        // Create ContentValues of what you want to insert
        ContentValues valuesToInsert = new ContentValues();
        valuesToInsert.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Moscow");
        valuesToInsert.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, "bla");
        valuesToInsert.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, 1);
        valuesToInsert.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, 1);
        // (you can use the createNorthPoleLocationValues if you wish)

        // Insert ContentValues into database and get a row ID back
        long id = db.insert(WeatherContract.LocationEntry.TABLE_NAME,null,valuesToInsert);

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY,id);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE,"13081995");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES,25);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,0);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,25);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,20);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE,0);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Clear");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 1);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 10);
        long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,weatherValues);
        Cursor cursor = db.query(WeatherContract.WeatherEntry.TABLE_NAME,null,null,null,null,null,null,null);
        assertTrue(cursor != null);
        assertTrue(cursor.moveToFirst());
        ContentValues returnedValues = new ContentValues();
        returnedValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID)));
        returnedValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED)));
        returnedValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)));
        returnedValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE)));
        returnedValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)));
        returnedValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)));
        returnedValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY)));
        returnedValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES)));
        returnedValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, id);
        returnedValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE)));

        cursor.close();
        db.close();
        assertEquals(weatherValues, returnedValues);
        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.

        // First step: Get reference to writable database

        // Create ContentValues of what you want to insert
        // (you can use the createWeatherValues TestUtilities function if you wish)

        // Insert ContentValues into database and get a row ID back

        // Query the database and receive a Cursor back

        // Move the cursor to a valid database row

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)

        // Finally, close the cursor and database
    }


    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
    public long insertLocation() {
        return -1L;
    }
}
