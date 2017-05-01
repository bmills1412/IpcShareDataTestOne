package com.example.bryan.ipcsharedatatestone.CustomViews;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.bryan.ipcsharedatatestone.Interfaces.OnActionItemClicked;
import com.example.bryan.ipcsharedatatestone.R;
import com.example.bryan.ipcsharedatatestone.UIControls.SimpleAnimatorListener;

import java.util.Map;

/**
 * TODO add animations for touching
 */

public class ExtendActionViews extends View {


    public static final String STATE_CLEAR = "CLEAR";
    public static final String STATE_UNDO = "UNDO";
    public static final String STATE_SAVE = "SAVE";
    public static final String STATE_PALLETE = "PALLETE";
    public static final String STATE_SIZE = "SIZE";


    public static final int[] TRACK_HIDDEN = {1};
    public static final int[] TRACK_SHOWN = {2};


    private int[] currState =  TRACK_HIDDEN;

    private ExtendedThumbDrawable extendThumb;
    private ExtendActionTrackDrawable trackDrawable;


    private OnActionItemClicked onActionItemClickedListener;




    public ExtendActionViews(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * I'm initializing here because i need padding values at init time, and the framework
     * initializes padding values in this constructor.
     */

    public ExtendActionViews(Context context, AttributeSet set, int defStyleAttr){
        super(context, set, defStyleAttr);

        int thumbColor = -1;
        int trackColor = thumbColor;
        float itemSpacing = -1;
        float thumbWidth = -1;

        int extendedActionDrawable = R.drawable.dots;
        final int defViewHeight = context.getResources().getDimensionPixelSize(R.dimen.defaulExtendActionViewHeight);


        TypedArray attrArray = context.obtainStyledAttributes(set, R.styleable.ExtendActionViews);

        final int attrSize = attrArray.getIndexCount();

        for(int index = 0; index < attrSize; index++){
            int attr = attrArray.getIndex(index);
            switch(attr){
                case R.styleable.ExtendActionViews_thumbColor:
                    thumbColor = attrArray.getColor(attr, 0);
                    break;
                case R.styleable.ExtendActionViews_trackColor:
                    trackColor = attrArray.getColor(attr, thumbColor);
                    break;
                case R.styleable.ExtendActionViews_itemSpacing:
                    itemSpacing = attrArray.getDimensionPixelSize(attr, 0);
                    break;
                case R.styleable.ExtendActionViews_thumbWidth:
                    thumbWidth = attrArray.getDimensionPixelSize(attr, 0);
                    break;
                default:
                    break;
            }
        }
        attrArray.recycle();


        this.trackDrawable = new ExtendActionTrackDrawable(trackColor, itemSpacing, defViewHeight,
        getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom());

        this.extendThumb = new ExtendedThumbDrawable(getResources(), extendedActionDrawable,
                thumbColor, (int)thumbWidth, defViewHeight);

    }



    @Override
    public void onMeasure(int widthSpec, int heightSpec){
        int width = MeasureSpec.getSize(widthSpec);
        int widthMode = MeasureSpec.getMode(widthSpec);

        int height = MeasureSpec.getSize(heightSpec);
        int heightMode = MeasureSpec.getMode(heightSpec);

        int measuredWidth, measuredHeight = -1;

        /**
         * Include size contstraints as well, so this guy can re-measure itself to fit all items on screen.
         */
        switch(widthMode){
            case MeasureSpec.UNSPECIFIED:
                measuredWidth = getSuggestedMinimumWidth();
                break;
            case MeasureSpec.AT_MOST:
                measuredWidth = Math.min(width, getSuggestedMinimumWidth());
                break;
            case MeasureSpec.EXACTLY:
                measuredWidth = width;
                break;
            default:
                measuredWidth = getSuggestedMinimumWidth();
                break;
        }

        switch(heightMode){
            case MeasureSpec.UNSPECIFIED:
                measuredHeight = getSuggestedMinimumHeight();
                break;
            case MeasureSpec.AT_MOST:
                measuredHeight = Math.min(height, getSuggestedMinimumHeight());
                break;
            case MeasureSpec.EXACTLY:
                measuredHeight = height;
                break;
            default:
                measuredHeight = getSuggestedMinimumHeight();
                break;
        }
    setMeasuredDimension(measuredWidth, measuredHeight);
    }


    @Override
    public int getSuggestedMinimumWidth(){
        return trackDrawable.getIntrinsicWidth()+extendThumb.getIntrinsicWidth();
    }

    @Override
    public int getSuggestedMinimumHeight(){
        /**
         * Will return one of two values.
         * 1) Either the default height if it's big enough to encompass the icons + padding (in track)
         * 2) The needed track size to encompass padding + icons in track
         *
         * The thumb should always be as big as this guy
         */
    return Math.max(trackDrawable.getIntrinsicHeight(), extendThumb.getIntrinsicHeight());

    }


    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom){
        super.onLayout(changed, left, top, right, bottom);

        extendThumb.setBounds(0, 0, extendThumb.getIntrinsicWidth(), getHeight());
        extendThumb.setState(currState);
        trackDrawable.setBounds(0-trackDrawable.getIntrinsicWidth(), 0, 0, getHeight());
       }



    @Override
    public void onDraw(Canvas canvas){
        extendThumb.draw(canvas);
        trackDrawable.draw(canvas);
    }

    private boolean thumbHit = false;
    private boolean trackHit = false;

    @Override
    public boolean onTouchEvent(MotionEvent event){
        final int xTouch = (int)event.getX();
        final int yTouch = (int)event.getY();



        if(event.getActionMasked() == MotionEvent.ACTION_DOWN){

            thumbHit = (extendThumb.isHit(xTouch, yTouch));
            trackHit = (trackDrawable.isHit(xTouch, yTouch));

            if(thumbHit | trackHit) return true;
            else return false;

        }else if(event.getActionMasked() == MotionEvent.ACTION_UP){

            if(thumbHit)onStartAnimating();

            else if(trackHit)
                onActionItemClickedListener.onActionItemClicked(trackDrawable.getItemKeyHit());

        }

        return false;
    }




    private ValueAnimator thumbDrop = new ValueAnimator();
    private ValueAnimator trackSlide = new ValueAnimator();

    private boolean isThumbDropped = false;
    private int newTrackPos = 0;
    private int newThumbPos = 0;

    public void onStartAnimating(){
        animateThumb();
        animateTrack();
    }

    private void animateThumb(){
        thumbDrop.setFloatValues(0f, 1f);
        thumbDrop.setDuration(150);
        thumbDrop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int newThumbValue = (int)(animation.getAnimatedFraction() * extendThumb.getBounds().height());
                if(isThumbDropped)
                    newThumbPos = (extendThumb.getBounds().height()) - (newThumbValue);
                else
                    newThumbPos = newThumbValue;

                setThumbBounds();
            }
        });

        thumbDrop.addListener(this.thumbListener);
        thumbDrop.start();

    }
    private void animateTrack(){
        trackSlide.setFloatValues(0f, 1f);
        trackSlide.setDuration(350);
        trackSlide.setInterpolator(new FastOutSlowInInterpolator());
        trackSlide.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int newTrackValue = (int)(animation.getAnimatedFraction() * getWidth());
                if(currState == TRACK_SHOWN)
                    newTrackPos = getWidth() - newTrackValue;
                else
                    newTrackPos = newTrackValue;

                setTrackBounds();
            }
        });
        trackSlide.addListener(trackListener);
    trackSlide.start();
    }


//This dude should never be aware of state changes, he should only be called before/after transitions happen
    private void setThumbBounds(){

        final int newThumbBottom = newThumbPos + extendThumb.getBounds().height();

        if(currState == TRACK_HIDDEN){
            extendThumb.setBounds(0, newThumbPos, extendThumb.getIntrinsicWidth(), newThumbBottom);
        }else if(currState == TRACK_SHOWN){
            final int trackleft = trackDrawable.getBounds().left;
            extendThumb.setBounds(trackleft - extendThumb.getIntrinsicWidth(), newThumbPos, trackleft, newThumbBottom);
        }

        invalidate();
    }


    private void setTrackBounds(){
        trackDrawable.setBounds(newTrackPos - trackDrawable.getIntrinsicWidth(), 0, newTrackPos, getHeight());
        invalidate(trackDrawable.getBounds());
    }





    private SimpleAnimatorListener thumbListener = new SimpleAnimatorListener(){
        @Override
        public void onAnimationEnd(Animator animation) {
            if(extendThumb.getBounds().top == getHeight())
                isThumbDropped = true;
            else
                isThumbDropped = false;
        }
    };

    private SimpleAnimatorListener trackListener = new SimpleAnimatorListener(){
        @Override
        public void onAnimationEnd(Animator animator){
            final int trackRight = trackDrawable.getBounds().right;
            final boolean isOpened = (trackRight == getWidth());
            if(isOpened){
                currState = TRACK_SHOWN;
                extendThumb.setState(currState);
            }else{
                currState = TRACK_HIDDEN;
                extendThumb.setState(currState);
            }
            animateThumb();
        }
    };


    public void setActionStates(Map<String, Integer> actionItems){
        /**
         * RequestedLayout, because prior to the addition of icons,
         * the track will have a 0 width until it's loaded with icons
         */
        //Should this guy handle action items, and the track just be responsible for itself only?
        trackDrawable.addActionItems(actionItems, getResources());
        requestLayout();  //BAD IDEA I GUESS. SORRY CHET HAASE. Shouldn't request a layout during the layout pass on a View
    }


    public void setOnActionItemClickedListener(OnActionItemClicked onActionItemClickedListener){
        this.onActionItemClickedListener = onActionItemClickedListener;
    }

}
