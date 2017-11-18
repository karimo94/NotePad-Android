package com.karimo.notey;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;

public class TextNoteActivity extends Activity
{
	LinedEditText titleEditText;
	LinedEditText notesEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_note);
		
		titleEditText = (LinedEditText) findViewById(R.id.editTitleText1);
		notesEditText = (LinedEditText) findViewById(R.id.editTextMain);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.text_note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void saveFile(String title, String text, String path)
	{
		//save the file to the path, title is the file name
		FileOutputStream fs;
		try
		{
			//check to see if there's duplicates first
			//if not, save
			if(!duplicateCheck(title + ".txt", path))
			{
				fs = this.openFileOutput(path + title + ".txt", Context.MODE_PRIVATE);
				Writer out = new OutputStreamWriter(fs);
		        out.write(text);
		        out.close();
			}
			else
			{
				//search for the recent duplicate
				//increment how many files exactly have fileName & stop
				//add a (#) to the end of the file name
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	private boolean duplicateCheck(String fileName, String path)
	{
		//check the folder for any files with the same fileName and extension
		
		boolean isDup = false;
		//YOU ARE HERE!!!!!!!!!!!!!!!!!!!!!!!!!
		return isDup;
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		//get the text from title and main
		//save it to sd card storage
		String title = titleEditText.getText().toString();
		String textContent = notesEditText.getText().toString();
		String path = Environment.getExternalStorageDirectory() + "/Notey";
		
		//make sure there's actual text to save
		if(textContent.length() != 0 || title.length() != 0)
		{
			File dir = new File(path);
			try
			{
				if(dir.exists() && dir.isDirectory())
				{
					//save the text file using the title as the file name
					saveFile(title, textContent, path);
				}
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			//create the directory if the Notey folder isnt there
			try
			{
			
			  if(!dir.exists())
			  {
				  if(dir.mkdir()) 
				  {
				     System.out.println("Directory created");
				     saveFile(title, textContent, path);
				  } 
				  else 
				  {
				     System.out.println("Directory is not created");
				  }
			  }
			}
			catch(Exception e)
			{
			  e.printStackTrace();
			}
		}
	}
}
