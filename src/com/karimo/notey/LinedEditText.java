package com.karimo.notey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

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
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xFF000000);
    }


    @Override protected void onDraw(Canvas canvas) 
    {
    	int height = canvas.getHeight();
        int curHeight = 0;
        Rect r = mRect;
        Paint paint = mPaint;
        int baseline = getLineBounds(0, r);
        for (curHeight = baseline + 1; curHeight < height; 
                                                 curHeight += getLineHeight())
        {
            canvas.drawLine(r.left, curHeight, r.right, curHeight, paint);
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