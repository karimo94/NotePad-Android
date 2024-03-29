package com.karimo.notey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.util.AttributeSet;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;
public class DrawingView extends View
{
	//drawing path
	private Path drawPath;
	//drawing and canvas paint
	private Paint drawPaint, canvasPaint;
	//initial color
	private int paintColor = 0xFF660000;
	//canvas
	private Canvas drawCanvas;
	//canvas bitmap
	private Bitmap canvasBitmap;
	
	private float brushSize, lastBrushSize;
	
	private boolean erase = false;
	
	int width, height;
	public DrawingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setupDrawing();
	}

	private void setupDrawing()
	{
		//get drawing area setup for interaction
		drawPath = new Path();
		drawPaint = new Paint();
		drawPaint.setColor(paintColor);
		//initial path properties
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(brushSize);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		//instantiate canvasPaint object
		canvasPaint = new Paint(Paint.DITHER_FLAG);
		
		brushSize = getResources().getInteger(R.integer.medium_size);
		lastBrushSize = brushSize;
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}
	@Override
	protected void onDraw(Canvas canvas) 
	{
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		//detect user touch     
		float touchX = event.getX();
		float touchY = event.getY();
		switch (event.getAction()) 
		{
		case MotionEvent.ACTION_DOWN:
		    drawPath.moveTo(touchX, touchY);
		    break;
		case MotionEvent.ACTION_MOVE:
		    drawPath.lineTo(touchX, touchY);
		    break;
		case MotionEvent.ACTION_UP:
		    drawCanvas.drawPath(drawPath, drawPaint);
		    drawPath.reset();
		    break;
		default:
		    return false;
		}
		invalidate();
		return true;
	}
	public void setColor(String newColor)
	{
		invalidate();
		paintColor = Color.parseColor(newColor);
		drawPaint.setColor(paintColor);
	}
	public void setBrushSize(float newSize)
	{
		//update size
		float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
			    newSize, getResources().getDisplayMetrics());
		brushSize=pixelAmount;
		drawPaint.setStrokeWidth(brushSize);
	}
	public void setLastBrushSize(float lastSize)
	{
	    lastBrushSize=lastSize;
	}
	public float getLastBrushSize()
	{
	    return lastBrushSize;
	}
	public void setErase(boolean isErase)
	{
		erase=isErase;
		if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		else drawPaint.setXfermode(null);
	}
	public void startNew()
	{
	    drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
	    canvasBitmap.eraseColor(Color.WHITE);
	    invalidate();
	}

}
