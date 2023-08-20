package com.karimo.notey.db;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class TaskDbHelper extends SQLiteOpenHelper
{

	public TaskDbHelper(Context context)
	{
		super(context, Environment.getExternalStorageDirectory()
	            + File.separator + "Notey"
	            + File.separator + TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
	}
	public TaskDbHelper(Context context, String name)
	{
		super(context, Environment.getExternalStorageDirectory()
	            + File.separator + "Notey"
	            + File.separator + name + ".db", null, TaskContract.DB_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String createTable = "CREATE TABLE " + TaskContract.TaskEntry.TABLE + " ( " 
				+ TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ TaskContract.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL);";
		
		db.execSQL(createTable);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE);
        onCreate(db);
	}


}
