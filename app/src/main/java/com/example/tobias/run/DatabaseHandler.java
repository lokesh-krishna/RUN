package com.example.tobias.run;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.tobias.run.DatabaseContract.RunsContract;


/**
 * Helper class to manage database
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "run.db";

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = new StringBuilder().append("CREATE TABLE IF NOT EXISTS ")
                .append(RunsContract.TABLE_NAME)
                .append("(")
                .append(RunsContract.ID).append(" INTEGER PRIMARY KEY, ")
                .append(RunsContract.DATE).append(" INTEGER, ")
                .append(RunsContract.DISTANCE).append(" INTEGER, ")
                .append(RunsContract.TIME).append(" INTEGER, ")
                .append(RunsContract.RATING).append(" INTEGER, ")
                .append(RunsContract.UNIT).append(" TEXT").append(")")
                .toString();
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + RunsContract.TABLE_NAME;
        db.execSQL(dropTableQuery);
        //Create table again
        onCreate(db);
    }

    public void addRun(TrackedRun run){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(RunsContract.DATE, run.getDate());
        values.put(RunsContract.DISTANCE, run.getDistance());
        values.put(RunsContract.RATING, run.getRating());
        values.put(RunsContract.UNIT, run.getUnit());
        values.put(RunsContract.TIME, run.getTime());

        db.insert(RunsContract.TABLE_NAME, null, values);
        db.close();
    }
}

