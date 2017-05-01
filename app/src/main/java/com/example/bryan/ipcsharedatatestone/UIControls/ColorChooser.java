package com.example.bryan.ipcsharedatatestone.UIControls;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.FrameLayout;



import com.example.bryan.ipcsharedatatestone.Adapter.ColorAdapter;
import com.example.bryan.ipcsharedatatestone.BuildConfig;
import com.example.bryan.ipcsharedatatestone.R;

/**
 * TODO blur background
 */

public class ColorChooser extends PopupWindow implements ColorAdapter.OnColorChosen {


    private static final int R_LAYOUT = R.layout.color_chooser_layout;


    private View contentView;

    private FrameLayout colorChooserRoot;

    private GridView colorChooserView;
    private ColorAdapter colorAdapter;
    private FrameLayout colorChosenCheck;

    private Context context;

    private ColorAdapter.OnColorChosen onColorChosenClient;

    private String color;


    public ColorChooser(Context context){
        super(context);

        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);


        if(Build.VERSION.SDK_INT >= 24) {
            this.setEnterTransition(new Fade(Fade.IN));
            this.setExitTransition(new Fade(Fade.OUT));
        }

        this.contentView = LayoutInflater.from(context).inflate(R_LAYOUT, null);


        colorChooserRoot = (FrameLayout) contentView.findViewById(R.id.colorChooserSceneRootId);
        colorChooserRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        colorChooserView = (GridView) contentView.findViewById(R.id.colorChooserGridId);
        colorChosenCheck = (FrameLayout) contentView.findViewById(R.id.colorChooserCheckId);
        colorChosenCheck.setOnClickListener(this.colorChosenCheckListener);


        colorAdapter = new ColorAdapter(context, context.getResources().getStringArray(R.array.available_colors));
        colorAdapter.setOnColorChosenClient(this);
        colorChooserView.setAdapter(colorAdapter);


        this.setContentView(contentView);
    }


    @Override
    public void onColorChosen(String color) {
        this.color = color;

        this.showColorChosenCheck();

        this.setColorChosenBackgroundColor();
    }


    private void showColorChosenCheck(){
        if(Build.VERSION.SDK_INT >= 19) {
            TransitionManager.beginDelayedTransition((ViewGroup) contentView.findViewById(R.id.colorChooserSceneRootId), new Fade());
            this.colorChosenCheck.setVisibility(View.VISIBLE);
        }else{
            this.colorChosenCheck.setAlpha(0f);
            this.colorChosenCheck.setVisibility(View.VISIBLE);
            this.colorChosenCheck.animate().alpha(1f).setDuration(350).start();
        }
    }
    private void setColorChosenBackgroundColor(){
       //if the color selected was white, then set bg of view to black because the check is white.
        //else set to appropriate color
        int parsedColor = Color.parseColor(color);

        if(parsedColor == Color.WHITE)
            parsedColor = Color.BLACK;

        this.colorChosenCheck.setBackgroundColor(parsedColor);
    }

    private final View.OnClickListener colorChosenCheckListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*
            onColorChosenClient.onColorChosen(color);
            dismiss();
             */
        }
    };


    public void setOnColorChosenClient(ColorAdapter.OnColorChosen onColorChosenClient){
        this.onColorChosenClient = onColorChosenClient;
    }





}
