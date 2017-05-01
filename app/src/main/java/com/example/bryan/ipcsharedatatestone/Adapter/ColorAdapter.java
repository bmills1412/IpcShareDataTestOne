package com.example.bryan.ipcsharedatatestone.Adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.example.bryan.ipcsharedatatestone.CustomViews.ColorView;

/**
 * Created by bryan on 4/9/2017.
 */

public class ColorAdapter implements ListAdapter {

    private String[] colors;

    private Context context;

    private static final int TYPE_COLOR = 1;


    public ColorAdapter(Context context, String[] colors){
        this.colors = colors;
        this.context = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int position) {
        return colors[position];
    }

    @Override
    public long getItemId(int position) {
        return Color.parseColor(colors[position]);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ColorView colorView = new ColorView(context, colors[position]);
            colorView.setOnClickListener(colorChosenListener);
        //Use ConvertView as well

        return colorView;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_COLOR;
    }

    @Override
    public int getViewTypeCount() {
        return colors.length;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }


    public final View.OnClickListener colorChosenListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ColorAdapter.this.colorChosenClient.onColorChosen( ((ColorView)v).getColor());
        }
    };


    private OnColorChosen colorChosenClient;
    public interface OnColorChosen{ void onColorChosen(String color); }
    public void setOnColorChosenClient(OnColorChosen colorChosenClient){this.colorChosenClient = colorChosenClient; }

}
