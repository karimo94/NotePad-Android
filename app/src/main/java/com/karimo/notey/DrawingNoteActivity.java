package com.karimo.notey;



import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Toast;

public class DrawingNoteActivity extends Activity implements OnClickListener
{

	private DrawingView drawView;
	private ImageButton currPaint;
	private float smallBrush, mediumBrush, largeBrush;
	private ImageButton drawBtn;
	private ImageButton eraseBtn;
	private ImageButton newBtn;
	private ImageButton saveBtn;
	private EditText fileInput;
	private String filePath;
	private String imgName;
	static final int NEW_FILE_DRAW = 2;
	static final int MODIFY_FILE_DRAW = 3;
	private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
	private int FILE_MODE;
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawing_note);
		drawView = (DrawingView) findViewById(R.id.drawingBoard);
		LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
		currPaint = (ImageButton)paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed, null));
		
		ActivityCompat.requestPermissions(this,
	            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
		
		//instantiate the brush sizes
		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);
		
		
		//get the draw button control
		drawBtn = (ImageButton)findViewById(R.id.brush_btn);
		//add a listener for onClick for the draw button
		drawBtn.setOnClickListener(this);
		
		//set the initial brush size
		drawView.setBrushSize(mediumBrush);
		
		//get the eraser button
		eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
		//set the listener
		eraseBtn.setOnClickListener(this);
		//repeat for new painting button
		newBtn = (ImageButton)findViewById(R.id.new_draw_btn);
		newBtn.setOnClickListener(this);
		//finally save button
		saveBtn = (ImageButton)findViewById(R.id.save_btn);
		saveBtn.setOnClickListener(this);
		
		FILE_MODE = NEW_FILE_DRAW;
		
		//get any data passed in from the previous intent
		Intent incoming = getIntent();
		Bundle b = incoming.getExtras();
		if(b != null)
		{
			File f = (File)b.get("openSavedDrawing");
			//open the file, as a png, convert to bitmap, set drawView to the bitmap, set the file mode
			String fileName = f.getPath();
			Bitmap bitmap = BitmapFactory.decodeFile(fileName);
			drawView.setBackground(new BitmapDrawable(getResources(), bitmap));
			FILE_MODE = MODIFY_FILE_DRAW;
			setTitle(f.getName());
			filePath = f.getPath();
			imgName = f.getName();
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.drawing_note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_shareNote)
		{
			try
			{
				Share();
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public void paintClicked(View view)
	{
	    //use chosen color
		if(view!=currPaint)
		{
			//update color
			ImageButton imgView = (ImageButton)view;
			String color = view.getTag().toString();
			drawView.setColor(color);
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed, null));
			currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint, null));
			currPaint=(ImageButton)view;
		}
	}
	@Override
	public void onClick(View view)
	{
		if(view.getId()==R.id.brush_btn)
		{
		    //draw button clicked
			//when a user clicks the button
			//a dialog will be displayed with brush sizes
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Brush size:");
			brushDialog.setContentView(R.layout.brush_chooser);
			ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			    	drawView.setErase(false);
			        drawView.setBrushSize(smallBrush);
			        drawView.setLastBrushSize(smallBrush);
			        brushDialog.dismiss();
			    }
			});
			ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			    	drawView.setErase(false);
			        drawView.setBrushSize(mediumBrush);
			        drawView.setLastBrushSize(mediumBrush);
			        brushDialog.dismiss();
			    }
			});
			 
			ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			    	drawView.setErase(false);
			        drawView.setBrushSize(largeBrush);
			        drawView.setLastBrushSize(largeBrush);
			        brushDialog.dismiss();
			    }
			});
			
			brushDialog.show();
		}
		else if(view.getId()==R.id.erase_btn)
		{
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Eraser size:");
			brushDialog.setContentView(R.layout.brush_chooser);
		    //switch to erase - choose size
			ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setErase(true);
			        drawView.setBrushSize(smallBrush);
			        brushDialog.dismiss();
			    }
			});
			ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setErase(true);
			        drawView.setBrushSize(mediumBrush);
			        brushDialog.dismiss();
			    }
			});
			ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setErase(true);
			        drawView.setBrushSize(largeBrush);
			        brushDialog.dismiss();
			    }
			});
			brushDialog.show();
		}
		else if(view.getId()==R.id.new_draw_btn)
		{
		    //new button
			AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
			newDialog.setTitle("New drawing");
			newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
			newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			    	if(FILE_MODE == MODIFY_FILE_DRAW) {
			    		Intent restartIntent = getIntent();
			    		restartIntent.removeExtra("openSavedDrawing");
			    		finish();
			    		
			    		startActivityForResult(restartIntent, MainPage.NEW_BLANK_DRAWING_REQUEST);
			    	}
			    	else {
			    		drawView.startNew();
			    	}
			        
			        dialog.dismiss();
			    }
			});
			newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        dialog.cancel();
			    }
			});
			newDialog.show();
		}
		else if(view.getId() == R.id.save_btn)
		{
			//create the directory if its not there
			createDir();
			//save the drawing
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Save drawing");
			saveDialog.setMessage("Save this drawing to the folder?");
			final String path = Environment.getExternalStorageDirectory() + File.separator + "Notey";
			fileInput = new EditText(this);
			saveDialog.setView(fileInput);
			
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which)
			    {
			        //save drawing
			    	
			    	drawView.setDrawingCacheEnabled(true);
			    	String fileName = fileInput.getText().toString();
			    	//get the image on screen
			    	Bitmap bitmap = drawView.getDrawingCache();
			    	//determine if we're modifying a file or saving a new file
			    	if(FILE_MODE == MODIFY_FILE_DRAW)
			    	{
			    		//make a temporary, delete the current, and rename the temporary
			    		String temporaryFileName = "temp_" + fileName;
			    		FileOutputStream fs = null;
			    		try
						{
							fs = new FileOutputStream(new File(path + File.separator + temporaryFileName + ".png"));
						} 
			    		catch (Exception e)
						{
							e.printStackTrace();
						}
			    		bitmap.compress(CompressFormat.PNG, 100, fs);
			    		try
			    		{
			    			fs.close();
			    		}
			    		catch(Exception e)
			    		{
			    			e.printStackTrace();
			    		}
			    		//delete the old file
			    		File oldFile = new File(path + File.separator + fileName + ".png");
			    		oldFile.delete();
			    		//rename the temporary
			    		File renamedFile = new File(path + File.separator + temporaryFileName + ".png");
			    		renamedFile.renameTo(oldFile);
			    	}
			    	if(FILE_MODE == NEW_FILE_DRAW)
			    	{
			    		//save it as a new file, because no file has its name
			    		saveDrawing(path, fileName, bitmap);
			    	}
			    	
			    	//destroy any cache, any future drawings wont reuse the current cache
			    	drawView.destroyDrawingCache();
			    }
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        dialog.cancel();
			    }
			});
			saveDialog.show();
		}
	}
	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
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
	@TargetApi(Build.VERSION_CODES.M)
	public void saveDrawing(String path, String fileName, Bitmap bitmap)
	{
		//do a permissions check first
		int hasWriteExtStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (hasWriteExtStoragePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
		if(!fileName.equals(""))
    	{
	    	//write the image to the Notey folder
    		File f = new File(path + File.separator + fileName + ".png");
    		
    		FileOutputStream fs = null;
			try
			{
				fs = new FileOutputStream(f);
			} 
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
    		bitmap.compress(CompressFormat.PNG, 100, fs);
    		try
			{
				fs.close();
			} 
    		catch (IOException e)
			{
				e.printStackTrace();
			}

    	}
    	else
    	{
    		Toast noFileName = Toast.makeText(getApplicationContext(), "Must provide file name.", Toast.LENGTH_SHORT);
    		noFileName.show();
    	}
	}
	public void Share() throws FileNotFoundException
	{
	    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
	    Bitmap imgBitmap = BitmapFactory.decodeFile(filePath);
	    String media = MediaStore.Images.Media.insertImage(getContentResolver(), imgBitmap, imgName,null);
	    
	    Uri uri = Uri.parse(media);
        sharingIntent.setType("image/png");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, imgName);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share image using:"));
	}
}