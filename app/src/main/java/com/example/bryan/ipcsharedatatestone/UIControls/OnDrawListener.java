package com.example.bryan.ipcsharedatatestone.UIControls;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bryan on 5/4/2017.
 */

public class OnDrawListener implements View.OnTouchListener {



    protected View[] view;

    public OnDrawListener(View... view){
        this.view = view;
        Log.i("test", "VIEW CLASS FOR ON DRAW LISTENER: " + view[0].getClass().toString() );
    }


    private boolean shouldHide = true;

    // show/hide all views IF the user is drawing.
    // boolean cond shouldHide is to avoid redundant alpha animation for each move event
    // If more views are added to the content views i can simply place them in the animate... methods

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getActionMasked() == MotionEvent.ACTION_MOVE)
                handleMove();

            else if(event.getActionMasked() == MotionEvent.ACTION_CANCEL||
                    event.getActionMasked() == MotionEvent.ACTION_UP)
               handleCancelOrUp();

            return false;
        }


     private void handleDown(){
         //TODO add logic
     }

     private void handleMove(){
         if(shouldHide){

             for(int i = 0; i < view.length; i++)
                 view[i].animate().alpha(0f).setDuration(100).start();

         shouldHide = false;
         }
     }

     private void handleCancelOrUp(){
         for(int i = 0; i < view.length; i++)
             view[i].animate().alpha(1f).setDuration(100).start();

     shouldHide = true;
     }


}




