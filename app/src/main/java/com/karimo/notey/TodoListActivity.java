package com.karimo.notey;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.os.Build;
import com.karimo.notey.db.TaskContract;
import com.karimo.notey.db.TaskDbHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class TodoListActivity extends Activity
{
	private TaskDbHelper dbHelper;
	private ListView todoListView;
	private ArrayAdapter<String> adapter; 
	private EditText taskEditText;
	private EditText todoListName;
	static final int NEW_TODO_LIST = 4;
	static final int MODIFY_TODO_LIST = 5;
	private int FILE_MODE;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list);
		
		dbHelper = new TaskDbHelper(TodoListActivity.this);
		
		todoListView = (ListView)findViewById(R.id.todoListView);

		todoListName = (EditText)findViewById(R.id.todoListName);
		
		//default edit mode for a new to do list
		FILE_MODE = NEW_TODO_LIST;
		
		
		//get extra from intent, if any
		Intent incoming = getIntent();
		Bundle b = incoming.getExtras();
		if(b != null)
		{
			FILE_MODE = MODIFY_TODO_LIST;
			File f = (File)b.get("openSavedChecklist");
			
			//post the db file contents to the listview UI
			loadExistingDBToView(f);
		}
		else{
		//fetch all data from database
			updateUI();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int id = item.getItemId();
		if (id == R.id.action_add_task)
		{
			//Log.d(TAG, "Add a new task");
			taskEditText = new EditText(TodoListActivity.this);
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("Add a new task");
			dialog.setMessage("What do you want to do next?");
			dialog.setView(taskEditText);
			dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int which) 
			            {
			                String task = String.valueOf(taskEditText.getText());
			                SQLiteDatabase db = dbHelper.getWritableDatabase();
			                ContentValues values = new ContentValues();
			                values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
			                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
			                        null,
			                        values,
			                        SQLiteDatabase.CONFLICT_REPLACE);
			                db.close();
			                //test this piece of code
			                updateUI();
			            }
			        });
			dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
			{
			    public void onClick(DialogInterface dialog, int which)
			    {
			        dialog.cancel();
			    }
			});
			dialog.create();
			try
			{
				dialog.show();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void loadExistingDBToView(File source)
	{
		SQLiteDatabase db = SQLiteDatabase.openDatabase(source.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
		//TaskDbHelper helper = new TaskDbHelper(this, source.getName());
		//set the title of the to do list to be the file name
		todoListName.setText(source.getName().replace(".db", ""));
		//create an adapter for the listview
		ArrayList<String> taskList = new ArrayList<>();
		Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
	            new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
	            null, null, null, null, null);
		while (cursor.moveToNext()) {
	        int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
	        taskList.add(cursor.getString(idx));
	    }
		if (adapter == null) 
	    {
	        adapter = new ArrayAdapter<>(this,
	                R.layout.item_todo,
	                R.id.task_title,
	                taskList);
	        todoListView.setAdapter(adapter);
	    } 
	    else 
	    {
	        adapter.clear();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				adapter.addAll(taskList);
			}
			adapter.notifyDataSetChanged();
	    }

	    cursor.close();
	    db.close();
		
	}
	private void updateUI()
	{
		ArrayList<String> taskList = new ArrayList<>();
		SQLiteDatabase db = null;
		if(dbHelper != null)
		{
			System.out.println("Not null");
		}
		try
		{
			db = dbHelper.getReadableDatabase();
		} 
		catch (Exception e)
		{
			System.out.println(e.getLocalizedMessage());
		}
	    
	    Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
	            new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
	            null, null, null, null, null);
	    while (cursor.moveToNext()) {
	        int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
	        taskList.add(cursor.getString(idx));
	    }

	    if (adapter == null) 
	    {
	        adapter = new ArrayAdapter<>(this,
	                R.layout.item_todo,
	                R.id.task_title,
	                taskList);
	        todoListView.setAdapter(adapter);
	    } 
	    else 
	    {
	        adapter.clear();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				adapter.addAll(taskList);
			}
			adapter.notifyDataSetChanged();
	    }

	    cursor.close();
	    db.close();
	}
	public void deleteTask(View view)
	{
		View parent = (View) view.getParent();
	    TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
	    String task = String.valueOf(taskTextView.getText());
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    db.delete(TaskContract.TaskEntry.TABLE,
	            TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
	            new String[]{task});
	    db.close();
	    updateUI();
	}
	@Override
	public void onBackPressed()
	{
		//collect checklist title, if any
		//rename db file to the checklist title
		//create a copy of the current database
		//and save to Notey folder
		String checklistTitle = todoListName.getText().toString();
		String path = Environment.getExternalStorageDirectory() + File.separator + "Notey";
		
		
		//mkdir if not exists yet...
		createDir();
		
		File dbFilePath = null;
		if(FILE_MODE == NEW_TODO_LIST)
		{
			dbFilePath = new File(path + File.separator + "com.karimo.todolist.db");
		}
		else 
		{
			dbFilePath = new File(path + File.separator + checklistTitle + ".db");
		}
		//create a new db using the checklist title as the filename
		//all db files are stored in the Notey directory
		saveDb(path, checklistTitle, dbFilePath);
		
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
		
	}
	public void saveDb(String path, String name, File sourceDB)
	{
		//make a copy of com.karimo.todolist.db file and rename it, they're both in the same path
		File backupDB = null;
		if(FILE_MODE == NEW_TODO_LIST)
		{
			//write the db file to the Notey folder
			backupDB = new File(path,name + ".db");
			
            FileChannel src = null;
			try
			{
				src = new FileInputStream(sourceDB).getChannel();
			} catch (FileNotFoundException e1)
			{
				e1.printStackTrace();
			}
            FileChannel dst = null;
			try
			{
				dst = new FileOutputStream(backupDB).getChannel();
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
            try
			{
				dst.transferFrom(src, 0, src.size());
			} catch (IOException e)
			{
				e.printStackTrace();
			}
            try
			{
				src.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
            try
			{
				dst.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
            sourceDB.delete();
            
		}
		if(FILE_MODE == MODIFY_TODO_LIST)
		{
			//create a temporary then rename
			backupDB = new File(path,name+"_tmp"+".db");
			FileChannel src = null;
			try
			{
				src = new FileInputStream(sourceDB).getChannel();
			} catch (FileNotFoundException e1)
			{
				e1.printStackTrace();
			}
            FileChannel dst = null;
			try
			{
				dst = new FileOutputStream(backupDB).getChannel();
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
            try
			{
				dst.transferFrom(src, 0, src.size());
			} catch (IOException e)
			{
				e.printStackTrace();
			}
            try
			{
				src.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
            try
			{
				dst.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
            sourceDB.delete();
            
            //rename the temporary file
            File newFile = new File(path,name+"_tmp"+".db");
		    newFile.renameTo(sourceDB);
		}
	}
	public void createDir()
	{
		String path = Environment.getExternalStorageDirectory() + File.separator + "Notey";
		File dir = new File(path);
		//create the directory if the Notey folder isnt there
		try
		{
			  if(!dir.exists())
			  {
				  if(dir.mkdirs()) 
				  {
				     System.out.println("Directory created");
				  } 
				  else 
				  {
					  System.out.println("Directory is not created, already found");
				  }
			  }
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		}
	}
}
