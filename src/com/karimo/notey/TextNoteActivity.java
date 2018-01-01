package com.karimo.notey;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class TextNoteActivity extends Activity
{
	LinedEditText titleEditText;
	LinedEditText notesEditText;
	static final int NEW_FILE_NOTE = 2;
	static final int MODIFY_FILE_NOTE = 3;
	private int FILE_MODE;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_note);
		
		titleEditText = (LinedEditText) findViewById(R.id.editTitleText1);
		notesEditText = (LinedEditText) findViewById(R.id.editTextMain);
		
		FILE_MODE = NEW_FILE_NOTE;
		
		//get extra from intent, if any
		Intent incoming = getIntent();
		Bundle b = incoming.getExtras();

		if(b != null)
		{
			File f = (File)b.get("openSavedText");
			Scanner scanner = null;
			//open the file
			try
			{
				scanner = new Scanner(f);
			} 
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			//set the title first
			titleEditText.setText(f.getName().replace(".txt", ""));
			//read all lines & set text 
			String allText = "";
			while(scanner.hasNextLine())
			{
				allText += scanner.nextLine();
			}
			notesEditText.setText(allText);
			FILE_MODE = MODIFY_FILE_NOTE;
		}
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
		FileWriter fw;
		try
		{
			//check to see if there's duplicates first
			//if not, save
			if(!duplicateCheck(title + ".txt", path) && FILE_MODE == NEW_FILE_NOTE)
			{
				fw = new FileWriter(path + File.separator + title + ".txt", false);
				PrintWriter pw = new PrintWriter(fw);
				pw.printf("%s" + "%n", text);
				pw.close();
				fw.close();
			}
			else if(duplicateCheck(title +".txt", path) && FILE_MODE == NEW_FILE_NOTE)
			{
				//find the most recent file
				//get the amount of copies
				//add an incrementing number to each duplicate file
				//Toast - let the user know its duplicately named...
				File f = new File(path);
				ArrayList<File> records = getListFiles(f, title); 
				fw = new FileWriter(path + File.separator + title + "(" + (records.size() + 1) + ")" + ".txt", false);
				PrintWriter pw = new PrintWriter(fw);
				pw.printf("%s" + "%n", text);
				pw.close();
				fw.close();
				Toast.makeText(this, "Saved as " + title + "(" + (records.size() + 1) + ")" + ".txt", Toast.LENGTH_SHORT).show();
			}
			else
			{
				//replace by creating a temporary, write all the text, delete the original file
				//and rename the temporary
				String oldFileName = title;
			    String tmpFileName = "tmp_" + title;
				fw = new FileWriter(path + File.separator + tmpFileName +  ".txt", false);
				PrintWriter pw = new PrintWriter(fw);
				pw.printf("%s" + "%n", text);
				pw.close();
				fw.close();
				File oldFile = new File(path + File.separator + oldFileName + ".txt");
			    oldFile.delete();
			    File newFile = new File(path + File.separator + tmpFileName + ".txt");
			    newFile.renameTo(oldFile);
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
		File file = new File(path + File.separator + fileName);
		return file.exists();
	}
	@Override
	public void onBackPressed() 
	{
		//get the text from title and main
		//save it to sd card storage
		String title = titleEditText.getText().toString();
		String textContent = notesEditText.getText().toString();
		String path = Environment.getExternalStorageDirectory() + File.separator + "Notey";
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		
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
					
					/*File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "/test" + String.valueOf(fCount++) +".text" );*/
					
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
				  if(dir.mkdirs()) 
				  {
				     System.out.println("Directory created");
				     saveFile(title, textContent, path);
				  } 
				  else 
				  {
				     System.out.println("Directory is not created, already found");
				     saveFile(title, textContent, path);
				  }
			  }
			}
			catch(Exception e)
			{
			  e.printStackTrace();
			}
		}
		finish();
	}
	@Override
	protected void onStop()
	{
		super.onStop();
	}
	private ArrayList<File> getListFiles(File parentDir, String fileName) 
	{
	    ArrayList<File> inFiles = new ArrayList<File>();
	    File[] files = parentDir.listFiles();
	    
	    if(files != null)
	    {
		    for (File file : files) 
		    {
		    	if(file.getName().contains(fileName))
	            {
	                inFiles.add(file);
	            }
		    }
	    }
	    return inFiles;
	}
}
