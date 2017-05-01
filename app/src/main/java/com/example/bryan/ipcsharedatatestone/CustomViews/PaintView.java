package com.example.bryan.ipcsharedatatestone.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.bryan.ipcsharedatatestone.R;

import java.util.ArrayDeque;
import java.util.Iterator;


public class PaintView extends View {

    private Path currPath;
    private Path tempDrawingPath;


    private Paint currPathPaint;
    private Paint tempDrawingPaint;

    private int currPathColor;
    private int tempPathColor;

    private int currPathSize;
    private int tempPathSize;


    private Bitmap drawingBitMap;
    private Canvas drawingCanvas;


    private ArrayDeque<PathData> pathStack = new ArrayDeque<>();


    public PaintView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.paintViewStyle);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PaintView, defStyleAttr, R.style.PaintViewStyle );

        final int pathSizeIndex = array.getIndex(R.styleable.PaintView_thumbStrokeSize);
        final int pathColorIndex = array.getIndex(R.styleable.PaintView_thumbStrokeColor);

        this.currPathSize = array.getDimensionPixelSize(pathSizeIndex, 0);
        this.currPathColor = array.getColor(pathColorIndex, Color.parseColor("#fff000"));

        this.currPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
             currPathPaint.setStyle(Paint.Style.STROKE);
             currPathPaint.setStrokeWidth(currPathSize);
             currPathPaint.setColor(currPathColor);
             currPathPaint.setStrokeCap(Paint.Cap.ROUND);
             currPathPaint.setStrokeJoin(Paint.Join.ROUND);

        this.currPath = new Path();

        this.tempDrawingPaint = new Paint(currPathPaint);
             tempDrawingPaint.setColor(tempPathColor);
             tempDrawingPaint.setStrokeWidth(tempPathSize);

        array.recycle();
    }



    @Override
    public void onDraw(Canvas canvas){
        canvas.drawBitmap(drawingBitMap, 0, 0, null);
    }


    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH){
        super.onSizeChanged(w, h, oldW, oldH);
        this.drawingBitMap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.drawingCanvas = new Canvas(drawingBitMap);
        this.drawingBitMap.eraseColor(Color.WHITE);
    }


    public void setCurrPathColor(int currPathColor) {
        this.currPathColor = currPathColor;
        this.currPathPaint.setColor(currPathColor);
    }

    public void setCurrPathSize(int currPathSize) {
        this.currPathSize = currPathSize;
        this.currPathPaint.setStrokeWidth(currPathSize);
    }

    public Bitmap getDrawingBitmap() {
        if(pathStack.size() == 0){
            return null;
        }
     return drawingBitMap;
    }

    public void undo(){
        if(pathStack.size() > 0) {
            this.pathStack.removeFirst();
            drawingBitMap.eraseColor(Color.WHITE);
            undoRedrawCanvas();
        }
    }

    public void clearPaths(){
        pathStack.clear();
        drawingBitMap.eraseColor(Color.WHITE);
        invalidate();
    }

    public int getPathsSize(){
        return pathStack.size();
    }

    private void undoRedrawCanvas(){
        Iterator<PathData> pathIterator = pathStack.descendingIterator();

        while(pathIterator.hasNext()){
            PathData pathData = pathIterator.next();
            this.tempDrawingPath = pathData.path;
            this.tempDrawingPaint.setColor(pathData.pathColor);
            this.tempDrawingPaint.setStrokeWidth(pathData.pathSize);

            drawingCanvas.drawPath(tempDrawingPath, tempDrawingPaint);
        }
        invalidate();
    }

    private void drawToCanvas(){
        if(!hasMoved){
            currPathPaint.setStyle(Paint.Style.FILL);
            drawingCanvas.drawPath(currPath, currPathPaint);
            currPathPaint.setStyle(Paint.Style.STROKE);
        }else{
            drawingCanvas.drawPath(currPath, currPathPaint);
        }
        invalidate();
    }

    private boolean hasMoved = false;
    private int firstTouchX, firstTouchY;
    private final ViewConfiguration touchConfig = ViewConfiguration.get(getContext());

    @Override
    public boolean onTouchEvent(MotionEvent event){

        final int eventType = event.getAction();

        switch(eventType){

            case MotionEvent.ACTION_DOWN:
                return onTouchDown(event);

            case MotionEvent.ACTION_MOVE:
                onMove(event);
                break;

            case MotionEvent.ACTION_CANCEL: //Treat CANCLE/UP the same, so i bleed through for each.
            case MotionEvent.ACTION_UP:
                onUpOrCancel();
                break;

            default: break;
        }

        return true;
    }

    private boolean onTouchDown(MotionEvent touch){

        firstTouchX = (int)touch.getRawX();
        firstTouchY = (int)touch.getRawY();
        this.currPath.moveTo(firstTouchX, firstTouchY);

        return true;
    }

    private void onMove(MotionEvent touch){

        final int xTouch = (int)touch.getRawX();
        final int yTouch = (int)touch.getRawY();

        final float slop = touchConfig.getScaledTouchSlop();

        if(Math.abs((xTouch - firstTouchX)) > slop ||
                Math.abs(yTouch - firstTouchY) > slop ) {
            hasMoved = true;
            this.currPath.lineTo(xTouch, yTouch);
            drawToCanvas();
        }
    }



    private void onUpOrCancel(){


        if(!hasMoved) {
            currPath.addCircle(firstTouchX, firstTouchY, currPathSize*.5f, Path.Direction.CW);
        }


        final PathData newPathData = new PathData();

        newPathData.path = new Path(currPath);
        newPathData.pathColor = currPathColor;
        newPathData.pathSize = currPathSize;

        drawToCanvas();

        pathStack.push(newPathData);

        hasMoved = false;
        currPath.reset();
    }


    private class PathData{
        Path path;
        int pathColor;
        int pathSize;
    }


}

