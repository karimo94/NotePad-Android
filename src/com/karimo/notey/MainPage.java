package com.karimo.notey;



import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainPage extends Activity implements OnItemClickListener
{
	private ArrayList<File> notesList;
	private Builder aboutWindow;
	//listview adapter
	@SuppressWarnings("unused")
	private ArrayAdapter<File> adapter;
	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		
		getOverflowMenu();
		
		listView = (ListView) findViewById(R.id.notesListView);
		loadListView();
		
		listView.setOnItemClickListener(this);
		
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
			//here, the intent is a new note, make it known to TextNoteActivity class
			//intent.putExtra
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
		case R.id.about_help:
			AboutWindow();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	private void searchNotes()
	{
		// TODO Auto-generated method stub
		// filter the list view by searching using the term
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
	private void AboutWindow()//done!
    {
        //the code below is for the about window
        aboutWindow = new AlertDialog.Builder(this);
        final String website = " simpledevcode.wordpress.com";
        final String AboutDialogMessage = " Notey 1.0\n By Karim Oumghar\n\n Website for contact:\n";
        final TextView tx = new TextView(this);
        tx.setText(AboutDialogMessage + website);
        tx.setAutoLinkMask(RESULT_OK);
        tx.setTextColor(Color.WHITE);
        tx.setTextSize(15);
        tx.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(tx, Linkify.WEB_URLS);
        
        aboutWindow.setIcon(R.drawable.ic_launcher);
        aboutWindow.setTitle("About");
    	aboutWindow.setView(tx);
    	
    	aboutWindow.setPositiveButton("OK", new DialogInterface.OnClickListener() 
    	{
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.dismiss();
			}
    	});
    	aboutWindow.show();
    }
	private void loadListView()
	{
		//populate the list view
		File file = new File(Environment.getExternalStorageDirectory(),
			    "Notey");
		notesList = getListFiles(file);
		adapter = new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, notesList);
	}
	private ArrayList<File> getListFiles(File parentDir) 
	{
	    ArrayList<File> inFiles = new ArrayList<File>();
	    File[] files = parentDir.listFiles();
	    for (File file : files) 
	    {
	        if (file.isDirectory()) 
	        {
	            inFiles.addAll(getListFiles(file));
	        } 
	        else 
	        {
	            if(file.getName().endsWith(".txt"))
	            {
	                inFiles.add(file);
	            }
	        }
	    }
	    return inFiles;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		// TODO Auto-generated method stub
		// here, the intent is opening up a file and displaying on TextNoteActivity
		// intent.putExtra
	}
}
