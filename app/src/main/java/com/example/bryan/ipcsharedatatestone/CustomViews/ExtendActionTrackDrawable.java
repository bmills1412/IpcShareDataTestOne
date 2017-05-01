package com.example.bryan.ipcsharedatatestone.CustomViews;

import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The overall Point of this guy is to manage the ActionItems
 * -drawing ActionItems: DONE
 * -resizing?
 * -exposes hit logic DONE
 * -returning a result for the item tapped DONE
 *

 * -animating items when tapped
 *
 * -Managing the bounds of each item
 */

 class ExtendActionTrackDrawable extends Drawable {


    private Map<Integer, ActionItems> actionItemsMap;

    private float itemSpacing;
    private int defHeight;
    private int padLeft, padTop, padRight, padBottom;

    private Paint trackPaint;

    private String itemStateHit = "NO_STATE";

    private ObjectAnimator actionItemScaleAnimator;
    private Matrix scaleMatrix;


     ExtendActionTrackDrawable(int trackColor, float itemSpacing, int defHeight,
                               int padLeft, int padRight, int padTop, int padBottom){


        actionItemsMap = new LinkedHashMap<>();

        this.defHeight = defHeight;
        this.itemSpacing = itemSpacing;
        this.padLeft = padLeft;
        this.padRight = padRight;
        this.padTop = padTop;
        this.padBottom = padBottom;

        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setStyle(Paint.Style.FILL);
        trackPaint.setColor(trackColor);

        this.actionItemScaleAnimator = new ObjectAnimator();
    }

    /**
     *
     * @param statesMap A map containing each icon resource value, as well as the state value that
     *                  the client wants returned when tapped.
     *                  The types are String/Integer, which are States/IconResource
     * @param resources Used to construct a the bitmap icons
     */
      void addActionItems(Map<String, Integer> statesMap,  Resources resources){
        for(String key : statesMap.keySet()){
            int iconResource = statesMap.get(key);
            actionItemsMap.put(iconResource, new ActionItems(iconResource, resources, key));
        }

      }



    @Override
    public void draw(@NonNull Canvas canvas) {

        canvas.drawRect(getBounds(), trackPaint);

        for(int key : actionItemsMap.keySet()){
            actionItemsMap.get(key).draw(canvas);
        }

    }


    @Override
    public void onBoundsChange(Rect rect){

        int newLeft = rect.left+padLeft;

        for(int key: actionItemsMap.keySet()){

            ActionItems actionItems = actionItemsMap.get(key);

             Rect itemRect = actionItems.rect;

             itemRect.left = newLeft;
             itemRect.right = newLeft + actionItems.actionItem.getWidth();
             itemRect.top = rect.top;
             itemRect.bottom = rect.bottom;

             newLeft = itemRect.right;
             newLeft += itemSpacing;
        }

    }


    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        trackPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public int getIntrinsicWidth(){

        int width = 0;
        final int itemMultiplier = actionItemsMap.size();

        if(itemMultiplier==0){
            return width; //no Bitmaps, no Width, return 0.
        }

        for(int key: actionItemsMap.keySet()) {
            int newWidth = actionItemsMap.get(key).actionItem.getWidth();
            width += newWidth;
        }


        width+= itemSpacing*(itemMultiplier-1);
        width+= padLeft+padRight;

    return width;
    }

    @Override
    public int getIntrinsicHeight(){

        int largestItemHeight = 0;

        int height = padBottom+padTop;

        for(int key: actionItemsMap.keySet()){
            int actionItemHeight = actionItemsMap.get(key).actionItem.getHeight();
            if(actionItemHeight > largestItemHeight) largestItemHeight = actionItemHeight;
        }

        height += largestItemHeight;

    return Math.max(height, defHeight);
    }

    public boolean isHit(float x, float y){

        for(int key: actionItemsMap.keySet()){

            final boolean isItemHit = actionItemsMap.get(key).rect.contains((int)x, (int)y);

            if(isItemHit) {
                this.itemStateHit = actionItemsMap.get(key).state;
                return true;
            }
        }
        //May be bad logic, but the principle here is to return a yes/no IF the tap can be consumed by this guys items
        //Then the client is responsible to again, come here and find out who was hit.
     return false;
    }

    public String getItemKeyHit(){
        String returnKey = this.itemStateHit;
        itemStateHit = "NO_STATE";

    return returnKey;
    }

    private static class ActionItems{
        private Bitmap actionItem;
        private Rect rect = new Rect();
        private String state;


        private ActionItems(int itemResource, Resources resources, String state){
            actionItem = BitmapFactory.decodeResource(resources, itemResource);
            this.state = state;

        }

        private void draw(Canvas canvas){
            final int bitmapLeft = rect.centerX() - (actionItem.getWidth()/2);
            final int bitmapTop = rect.centerY() - (actionItem.getHeight()/2);
            canvas.drawBitmap(actionItem, bitmapLeft, bitmapTop, null);
        }

    }


}
