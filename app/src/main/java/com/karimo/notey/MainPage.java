package com.karimo.notey;



import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
//import java.util.jar.Attributes.Name;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
	private ArrayAdapter<String> adapter;
	private ListView listView;
	private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0;
	static final int NEW_BLANK_NOTE_REQUEST = 1;
	static final int NEW_BLANK_DRAWING_REQUEST = 2;
	//static final int NEW_BLANK_TODOLIST_REQUEST = 3;
	
	@Override
	protected void onStart()
	{
		super.onStart();
		// Ask for permissions
	    int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
	    if (permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
	        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
	        return;
	    }
	    listView = (ListView) findViewById(R.id.notesListView);
		listView.setOnItemClickListener(this);
		
		loadListView();
		listView.setAdapter(adapter);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		
		getOverflowMenu();
		
		listView = (ListView) findViewById(R.id.notesListView);
		listView.setOnItemClickListener(this);
		
		loadListView();
		listView.setAdapter(adapter);
		
		//if we long click on an item, bring up a dialog to confirm deletion of the file
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() 
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				// delete the file selected
				final String path = Environment.getExternalStorageDirectory() + File.separator + "Notey";
				final String fName = (String)parent.getItemAtPosition(position);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
                
		        builder
		        .setMessage("Delete " + fName + "?")
		        .setPositiveButton("Yes",  new DialogInterface.OnClickListener() 
		        {
		            @Override
		            public void onClick(DialogInterface dialog, int id) 
		            {
		                // Yes-code
		            	File file = new File(path + File.separator + fName); 
		            	file.delete();
		            	if(file.getName().endsWith(".txt"))
		            	{
		            		adapter.remove(fName + ".txt");
		            	}
		            	if(file.getName().endsWith(".png"))
		            	{
		            		adapter.remove(fName + ".png");
		            	}
//		            	if(file.getName().endsWith(".db"))
//		            	{
//		            		adapter.remove(fName + ".db");
//		            	}
		            	adapter.notifyDataSetChanged();
		            	loadListView();
		            	listView.setAdapter(adapter);
		            }
		        })
		        .setNegativeButton("No", new DialogInterface.OnClickListener() 
		        {
		            @Override
		            public void onClick(DialogInterface dialog,int id) 
		            {
		                dialog.cancel();
		            }
		        })
		        .show();

				return true;
			}
		});
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
			startActivityForResult(i,NEW_BLANK_NOTE_REQUEST);
			return true;
		case R.id.action_newDrawNote:
			Intent j = new Intent(this, DrawingNoteActivity.class);
			startActivityForResult(j,NEW_BLANK_DRAWING_REQUEST);
			return true;
//		case R.id.action_createNewList:
//			Intent k = new Intent(this, TodoListActivity.class);
//			startActivityForResult(k, NEW_BLANK_TODOLIST_REQUEST);
//			return true;
//		case R.id.action_searchNotes:
//			searchNotes();
//			return true;
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
	//after each new note made, whether its text, picture, or to do list
	//we need to update the listview with new data once we exit the activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		loadListView();
		listView.setAdapter(adapter);
		super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == NEW_BLANK_NOTE_REQUEST) 
	    {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) 
	        {
	            //we've saved a note
	        	//update the listview
	        	loadListView();
	        	listView.setAdapter(adapter);
	        }
	    }
	    if (requestCode == NEW_BLANK_DRAWING_REQUEST)
	    {
	    	if(resultCode == RESULT_OK)
	    	{
	    		loadListView();
	    		listView.setAdapter(adapter);
	    	}
	    }
	    
//	    if (requestCode == NEW_BLANK_TODOLIST_REQUEST)
//	    {
//	    	if(resultCode == RESULT_OK)
//	    	{
//	    		loadListView();
//	    		listView.setAdapter(adapter);
//	    	}
//	    }
	}
	
	@SuppressWarnings("unused")
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
			@SuppressLint("SoonBlockedPrivateApi") Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
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
        final String AboutDialogMessage = " Notey 1.5\n By Karim Oumghar\n\n Website for contact:\n";
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
		File file = new File(Environment.getExternalStorageDirectory() + File.separator +
			    "Notey");
		notesList = getListFiles(file);
		String[] theNamesOfFiles = new String[notesList.size()];
		for (int i = 0; i < theNamesOfFiles.length; i++) {
			   theNamesOfFiles[i] = notesList.get(i).getName();
			}
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, theNamesOfFiles);
	}
	private ArrayList<File> getListFiles(File parentDir) 
	{
	    ArrayList<File> inFiles = new ArrayList<File>();
	    File[] files = parentDir.listFiles();
	    
	    if(files != null)
	    {
		    for (File file : files) 
		    {
		    	if(file.getName().endsWith(".txt") || file.getName().endsWith(".png"))
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
		// here, the intent is opening up a file and displaying on TextNoteActivity
		// intent.putExtra
		// get the file extension
		// if .txt, open up textnoteactivity
		// for now, we only deal with txt
		String path = Environment.getExternalStorageDirectory() + File.separator + "Notey";
		String fName = (String)parent.getItemAtPosition(position);
		File file = new File(path + File.separator + fName); 
		if(fName.endsWith(".txt"))
		{
			//its a text note
			Intent openTextIntent = new Intent(this, TextNoteActivity.class);
			openTextIntent.putExtra("openSavedText", file);
			startActivity(openTextIntent);
		}
		if(fName.endsWith(".png"))
		{
			//its a picture
			Intent openDrawingIntent = new Intent(this, DrawingNoteActivity.class);
			openDrawingIntent.putExtra("openSavedDrawing", file);
			startActivity(openDrawingIntent);
		}
//		else
//		{
//			//its a checklist
//			Intent openChecklistIntent = new Intent(this, TodoListActivity.class);
//			openChecklistIntent.putExtra("openSavedChecklist", file);
//			startActivity(openChecklistIntent);
//		}
	}
	
}
