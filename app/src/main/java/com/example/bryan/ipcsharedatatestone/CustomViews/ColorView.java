package com.example.bryan.ipcsharedatatestone.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.bryan.ipcsharedatatestone.R;

/**
 * Created by bryan on 4/9/2017.
 */

public class ColorView extends View {

    private int defSize;
    private Paint colorPaint;

    private String color;

    public ColorView(Context context, String color) {
        super(context);
        //Automatically scaled to appropriate size :)
        defSize = (int)context.getResources().getDimension(R.dimen.defaultColorChooserSize);

        //May need to set stroke width?
        this.color = color;
        this.colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.colorPaint.setColor(Color.parseColor(color));
        this.colorPaint.setStyle(Paint.Style.FILL);
    }


    public String getColor(){return this.color;}


    @Override
    public void onMeasure(int width, int height){
        setMeasuredDimension(defSize, defSize);
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.drawCircle(getWidth()*0.5f, getHeight()*0.5f, defSize*0.5f, colorPaint );
    }




}
