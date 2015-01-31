package tw.guodong.activitytransitionmanager;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import tw.guodong.activitytransitionmanager.listener.OnTransitioAnimationListener;

/**
 * Created by Tony on 2015/1/27.
 */
public final class ActivityTransitionManager {
    private static ActivityTransitionManager instance;
    private static int tagKey = 19881111 << 1;
    private Activity activity;
    private RelativeLayout viewGroup;
    private LinkedList<CanvasView> canvasViews;
    private OnTransitioAnimationListener mOnTransitioAnimationListener;
    private HashMap<Activity,View[]> tmpViews;
    private int duration, animationEndCount;
    private boolean transparentBackground;

    private ActivityTransitionManager(Activity activity) {
        this.activity = activity;
        tmpViews = new HashMap();
        viewGroup = new RelativeLayout(activity);
        canvasViews = new LinkedList<>();
        addViewGroupToWindow(viewGroup);
        transparentBackground = false;
        duration = -1;
    }

    private void addViewGroupToWindow(ViewGroup viewGroup) {
        WindowManager windowManager = (WindowManager) activity.getApplication().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        wmParams.format = PixelFormat.TRANSLUCENT;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.x = 0;
        wmParams.y = 0;
        windowManager.addView(viewGroup, wmParams);
    }

    public synchronized static ActivityTransitionManager getInstance(Activity activity) {
        if (null == instance) {
            instance = new ActivityTransitionManager(activity);
        }
        if (null != activity && !activity.equals(instance.activity)) {
            instance.activity = activity;
            instance.transparentBackground = false;
            instance.duration = -1;
        }
        return instance;
    }

    public static int getTagKey() {
        return tagKey;
    }

    public static void setTagKey(int tagKey) {
        if ((tagKey >>> 24) < 2) {
            throw new IllegalArgumentException("The key must be an application-specific "
                    + "resource id.");
        }
        ActivityTransitionManager.tagKey = tagKey;
    }

    public void addFormerView(View... views) {
        clearFormerView();
        if(views.length > 0){
            tmpViews.put(activity,views);
        }else{
            views = tmpViews.get(activity);
        }
        for (View view : views) {
            if (!canvasViews.contains(view)) {
                canvasViews.add(new CanvasView(activity.getApplicationContext(), view));
            }
        }
        floatFormerView();
    }

    public void animateFormerViewToLatterView(View... views) {
//        setActivtiyTransition();
        final View[] examineViews;
        if(views.length > 0){
            examineViews = views;
            tmpViews.put(activity,views);
        }else{
            examineViews = tmpViews.get(activity);
        }

        if(examineViews[0].getHeight() != 0) {
            examineView(examineViews);
        } else {
            examineViews[0].getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    examineViews[0].getViewTreeObserver().removeOnPreDrawListener(this);
                    examineView(examineViews);
                    return true;
                }
            });
        }
    }

    public void setAnimationDuration(int duration) {
        this.duration = duration;
    }

    public boolean isAnimationRunning() {
        if (animationEndCount <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public void setOnTransitioAnimationListener(OnTransitioAnimationListener onTransitioAnimationListener) {
        mOnTransitioAnimationListener = onTransitioAnimationListener;
    }

//    public void setTransparentBackground(boolean transparentBackground) {
//        this.transparentBackground = transparentBackground;
//    }

    public void stopAllAnimation(){
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
    }

    private void floatFormerView() {
        Iterator<CanvasView> iterator = canvasViews.iterator();
        while (iterator.hasNext()) {
            CanvasView canvasView = iterator.next();
            View view = canvasView.getView();
            int[] xy = getViewLocationOnScreen(view);
            canvasView.setX(xy[0]);
            canvasView.setY(xy[1]);
            try {
                viewGroup.addView(canvasView);
            } catch (IllegalStateException e) {
            }
        }
    }

    private int[] getViewLocationOnScreen(View view) {
        int[] location = {0,0};
        view.getLocationOnScreen(location);
        return location;
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
        stopAllAnimation();
        canvasViews.clear();
    }

    private void examineView(View... views) {
        animationEndCount = 0;
        if (null != mOnTransitioAnimationListener) {
            mOnTransitioAnimationListener.onAnimationStart();
        }
        for (View view : views) {
            Iterator<CanvasView> iterator = canvasViews.iterator();
            while (iterator.hasNext()) {
                try {
                    CanvasView canvasView = iterator.next();
                    if (view.getTag(tagKey) == canvasView.getView().getTag(tagKey)) {
                        animateView(canvasView, view);
                    }
                } catch (NullPointerException e) {
                }
            }
        }
    }

    private void animateView(final CanvasView canvasView, final View to) {
        float scaleX = to.getWidth() / (float) canvasView.getWidth();
        float scaleY = to.getHeight() / (float) canvasView.getHeight();
        int duration = this.duration;
        if (duration < 0) {
            duration = (int) canvasView.getView().animate().getDuration();
        }
        int[] xy = getViewLocationOnScreen(to);
        canvasView.animate()
                .x(xy[0] + canvasView.getWidth() * ((scaleX - 1) / 2))
                .y(xy[1]  + canvasView.getHeight() * ((scaleY - 1) / 2))
                .rotation(to.getRotation())
                .rotationX(to.getRotationX())
                .rotationY(to.getRotationY())
                .alpha(to.getAlpha())
                .scaleX(scaleX).scaleY(scaleY)
                .setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setViewVisibility(View.INVISIBLE);
                animationEndCount++;
                if (null != mOnTransitioAnimationListener) {
                    mOnTransitioAnimationListener.onViewAnimationStart(canvasView.getView(), animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setViewVisibility(View.VISIBLE);
                animationEndCount--;
                if (null != mOnTransitioAnimationListener) {
                    mOnTransitioAnimationListener.onViewAnimationEnd(canvasView.getView(), animation);
                }
                if (!isAnimationRunning()) {
                    clearFormerView();
                    if (null != mOnTransitioAnimationListener) {
                        mOnTransitioAnimationListener.onAnimationEnd(animation);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setViewVisibility(View.VISIBLE);
                if (null != mOnTransitioAnimationListener) {
                    mOnTransitioAnimationListener.onViewAnimationCancel(canvasView.getView(), animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if (null != mOnTransitioAnimationListener) {
                    mOnTransitioAnimationListener.onViewAnimationRepeat(canvasView.getView(), animation);
                }
            }

            private void setViewVisibility(int visibility){
                to.setVisibility(visibility);
                canvasView.getView().setVisibility(visibility);
            }
        }).start();
    }
}
