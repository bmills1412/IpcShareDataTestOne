package com.example.bryan.ipcsharedatatestone.CustomViews;

import android.animation.TimeAnimator;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;
import java.util.Timer;

//TODO add support for bitmap -> drawable size collisions

public class ExtendedThumbDrawable extends Drawable {



    private Bitmap image;

    private Paint thumbPaint;

    private Path thumbPath;

    private int thumbWidth;

    private int defHeight;

    private int rotateAngle = 0;


    int pathStart, pathEnd, pathTop, pathBottom, rectRadius;


    public ExtendedThumbDrawable(Resources resources, int actionIconResource, int color, int thumbWidth, int defHeight){
        this.image = BitmapFactory.decodeResource(resources, actionIconResource);
        this.thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.thumbPaint.setStyle(Paint.Style.FILL);
        this.thumbPaint.setColor(color);

        thumbPath = new Path();

        this.thumbWidth = thumbWidth;

        this.defHeight = defHeight;

    }


    @Override
    public void draw(@NonNull Canvas canvas) {

       thumbPath.reset(); //RESET THE MUTHER FUCKING PATH! FUCK ME!


        Path.Direction circleDirection = (Arrays.equals(getState(), ExtendActionViews.TRACK_HIDDEN))
                                          ? Path.Direction.CW
                                          : Path.Direction.CCW;

         if(Arrays.equals(getState(), ExtendActionViews.TRACK_SHOWN)) {
             pathStart = getBounds().right;
             pathEnd = getBounds().left + rectRadius;
         }

        thumbPath.moveTo(pathStart, pathTop);
        thumbPath.lineTo(pathEnd, pathTop);
        thumbPath.lineTo(pathEnd, pathBottom);
        thumbPath.lineTo(pathStart, pathBottom);

        thumbPath.addCircle(pathEnd, getBounds( ).bottom - rectRadius, rectRadius, circleDirection);

        thumbPath.close( );


        canvas.drawPath(thumbPath, thumbPaint);

        canvas.drawBitmap(image,
                          getBounds().centerX() - (image.getWidth()/2),
                          getBounds().centerY() - (image.getHeight()/2),
                          null);
    }



    @Override
    public void onBoundsChange(Rect rect){

        this.rectRadius = rect.height()/2;

        this.pathStart = rect.left;
        this.pathEnd = rect.right-rectRadius;
        this.pathTop = rect.top;
        this.pathBottom = rect.bottom;

    }


    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        this.thumbPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }


    public boolean isHit(float x, float y){
        return (this.getBounds().contains((int)x, (int)y));
    }


    @Override
    public int getIntrinsicWidth(){
        return thumbWidth;
    }

    @Override
    public int getIntrinsicHeight(){
        final Rect bounds = getBounds();

        if(bounds.isEmpty())
            return getMinimumHeight();

        return getBounds().height();
    }

    @Override
    public int getMinimumHeight(){
        //Should include padding on top/bottom mahn...
        //return Math.max(image.getHeight + (padTop+padBottom), defHeight);
        return defHeight;
    }



}


