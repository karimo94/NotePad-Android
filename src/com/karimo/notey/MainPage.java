package com.karimo.notey;



import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

public class MainPage extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		getOverflowMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_page, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		/*int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}*/
		switch(item.getItemId())
		{
		case R.id.action_newTextNote:
			Intent i = new Intent(this, TextNoteActivity.class);
			startActivity(i);
			return true;
		case R.id.action_newDrawNote:
			Intent j = new Intent(this, DrawingNoteActivity.class);
			startActivity(j);
			return true;
		case R.id.action_createNewList:
			Intent k = new Intent(this, TodoListActivity.class);
			startActivity(k);
			return true;
		case R.id.action_searchNotes:
			searchNotes();
			return true;
		case R.id.action_settings:
			Intent settings = new Intent(this, SettingsActivity.class);
			startActivity(settings);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	private void searchNotes()
	{
		// TODO Auto-generated method stub
		
	}

	private void getOverflowMenu()
	{
		try
		{
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) 
			{
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		}
		catch (Exception e) 
		{
			 e.printStackTrace();
		}
	}
}
