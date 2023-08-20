package com.karimo.notey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

@SuppressLint("AppCompatCustomView")
public class LinedEditText extends EditText
{
	private Rect mRect;
    private Paint mPaint;

    public LinedEditText(Context context) 
    {
        super(context);
    }

    public LinedEditText(Context context, AttributeSet attrs) 
    {
    	super(context, attrs);
        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(0xFF000000);
    }


    @Override protected void onDraw(Canvas canvas) 
    {
    	int height = getHeight();
        int line_height = getLineHeight();

        int count = height / line_height;

        if (getLineCount() > count)
            count = getLineCount();//for long text with scrolling

        Rect r = mRect;
        Paint paint = mPaint;
        int baseline = getLineBounds(0, r);//first line

        for (int i = 0; i < count; i++) {

            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
            baseline += getLineHeight();//next line
        }
        super.onDraw(canvas);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
    {
    	super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    	invalidate();
    }
}