package tw.guodong.activitytransitionmanager;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by USER on 2015/1/27.
 */
public class ActivityTransitionManager {
    private static ActivityTransitionManager instance;
    private Activity activity;
    private RelativeLayout viewGroup;
    private LinkedList<CanvasView> canvasViews;
    private int duration,animationEndCount;
    private boolean transparentBackground;

    private ActivityTransitionManager(Activity activity) {
        this.activity = activity;
        viewGroup = new RelativeLayout(activity);
        canvasViews = new LinkedList<>();
        addViewGroupToWindow(viewGroup);
    }

    private void addViewGroupToWindow(ViewGroup viewGroup){
        WindowManager windowManager = (WindowManager) activity.getApplication().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams  wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ;
        wmParams.format = PixelFormat.TRANSLUCENT;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.x = 0;
        wmParams.y = 0;
        windowManager.addView(viewGroup,wmParams);
    }

    public synchronized static ActivityTransitionManager getInstance(Activity activity) {
        if (null == instance) {
            instance = new ActivityTransitionManager(activity);
        }
        if (null != instance.activity && !instance.activity.equals(activity)) {
            instance.activity = activity;
            instance.transparentBackground = false;
            instance.duration = 1300;
        }
        return instance;
    }

    public void addFormerView(View... views) {
        clearFormerView();
        for (View view : views) {
            if(!canvasViews.contains(view)) {
                canvasViews.add(new CanvasView(activity.getApplicationContext(), view));
            }
        }
        floatFormerView();
    }

    public void animateFormerViewToLatterView(final View... views) {
        setActivtiyTransition();
        if(views[0].getHeight() != 0){
            examineView(views);
        }else{
            views[0].getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    views[0].getViewTreeObserver().removeOnPreDrawListener(this);
                    examineView(views);
                    return true;
                }
            });
        }
    }

    public void setAnimationDuration(int duration) {
        this.duration = duration;
    }

    public boolean isAnimationRunning(){
        if(animationEndCount <= 0){
            return false;
        }else{
            return true;
        }
    }

    public void setTransparentBackground(boolean transparentBackground) {
        this.transparentBackground = transparentBackground;
    }

    private void floatFormerView() {
        Iterator<CanvasView> iterator = canvasViews.iterator();
        while (iterator.hasNext()) {
            CanvasView canvasView = iterator.next();
            View view = canvasView.getView();
            canvasView.setX(view.getX());
            canvasView.setY(view.getY() + getActionBarHeight());
            try {
                viewGroup.addView(canvasView);
            } catch (IllegalStateException e) {
            }
        }
    }

    private int getActionBarHeight() {
        int actionBarHeight = 0;
        try {
            if (activity.getActionBar().getHeight() > 0) {
                actionBarHeight = activity.getActionBar().getHeight();
            }
        } catch (NullPointerException e) {
        }
        try {
            if (activity instanceof ActionBarActivity) {
                if (((ActionBarActivity) activity).getSupportActionBar().getHeight() > 0) {
                    actionBarHeight = ((ActionBarActivity) activity).getSupportActionBar().getHeight();
                }
            }
        } catch (NullPointerException e) {
        }
        return actionBarHeight;
    }

    private void setActivtiyTransition(){
        ColorDrawable colorDrawable = new ColorDrawable();
        int color;
        if(transparentBackground){
            color = Color.parseColor("#00000000");
        }else{
            color = Color.parseColor("#ffffffff");
        }
        colorDrawable.setColor(color);
        activity.getWindow().setBackgroundDrawable(colorDrawable);
    }

    private void clearFormerView() {
        Iterator<CanvasView> iterator = canvasViews.iterator();
        while (iterator.hasNext()) {
            CanvasView canvasView = iterator.next();
            try {
                if(isAnimationRunning()){
                    canvasView.animate().cancel();
                }
                viewGroup.removeView(canvasView);
            } catch (IllegalArgumentException e) {
            }
        }
        canvasViews.clear();
    }

    private void examineView(View... views){
        for (View view : views) {
            Iterator<CanvasView> iterator = canvasViews.iterator();
            while (iterator.hasNext()) {
                try {
                    CanvasView canvasView = iterator.next();
                    if (view.getId() == canvasView.getView().getId()) {
                        animateView(canvasView, view);
                    }
                } catch (NullPointerException e) {
                }
            }
        }
    }

    private void animateView(final CanvasView canvasView, final View to) {
        float scaleX = to.getWidth()/(float)canvasView.getWidth();
        float scaleY = to.getHeight()/(float)canvasView.getHeight();
        canvasView.animate().x(to.getX()+canvasView.getWidth()*((scaleX - 1)/2)).y(to.getY() + getActionBarHeight()+canvasView.getHeight()*((scaleY - 1)/2))
                .rotation(to.getRotation()).rotationX(to.getRotationX()).rotationY(to.getRotationY())
                .alpha(to.getAlpha())
                .scaleX(scaleX).scaleY(scaleY)
                .setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                to.setVisibility(View.INVISIBLE);
                canvasView.getView().setVisibility(View.INVISIBLE);
                animationEndCount++;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                to.setVisibility(View.VISIBLE);
                canvasView.getView().setVisibility(View.VISIBLE);
                animationEndCount--;
                if(!isAnimationRunning()){
                    clearFormerView();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }
}
