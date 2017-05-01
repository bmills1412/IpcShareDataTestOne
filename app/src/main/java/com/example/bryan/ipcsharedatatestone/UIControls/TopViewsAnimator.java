package com.example.bryan.ipcsharedatatestone.UIControls;

import android.view.View;

/**
 * Created by bryan on 4/28/2017.
 */

public class TopViewsAnimator {

    public static void animateViewsAlpha(float newAlpha, int dur, View... views){
        for(View view: views){
            view.animate().alpha(newAlpha).setDuration(dur).start();
        }
    }



}
