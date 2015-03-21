package tw.guodong.activitytransitionmanager;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
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
    private ViewGroup viewGroup;
    private LinkedList<CanvasView> canvasViews;
    private OnTransitioAnimationListener mOnTransitioAnimationListener;
    private HashMap<Activity,View[]> tmpViews;
    private int duration, animationEndCount;
    private boolean transparentBackground;

    private ActivityTransitionManager(Activity activity) {
        this.activity = activity;
        tmpViews = new HashMap();
        canvasViews = new LinkedList<>();
        transparentBackground = false;
        duration = -1;
        addViewGroupToWindow(new RelativeLayout(activity));
    }

    private void addViewGroupToWindow(ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
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

    public void setOnTransitioAnimationListener(OnTransitioAnimationListener onTransitioAnimationListener) {
        mOnTransitioAnimationListener = onTransitioAnimationListener;
    }

    public void stopAllAnimation(){
        Iterator<CanvasView> iterator = canvasViews.iterator();
        while (iterator.hasNext()) {
            try {
                CanvasView canvasView = iterator.next();
                if(isAnimationRunning()){
                    canvasView.animate().cancel();
                }
                viewGroup.removeView(canvasView);
            } catch (Exception e) {
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

    public int[] getViewLocationOnScreen(View view) {
        int[] location = {0,0};
        view.getLocationOnScreen(location);
        Rect rectangle= new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        location[1] -= rectangle.top;
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
        float scale = scaleX;
        if(scaleX > scaleY){
            scale = scaleY;
        }
        int duration = this.duration;
        if (duration < 0) {
            duration = (int) to.animate().getDuration();
        }
        int[] xy = getViewLocationOnScreen(to);
        to.animate().setDuration(duration);
        canvasView.animate()
                .x(xy[0] + canvasView.getWidth() * ((scaleX - 1) / 2))
                .y(xy[1] + canvasView.getHeight() * ((scaleY - 1) / 2))
                .rotation(to.getRotation())
                .rotationX(to.getRotationX())
                .rotationY(to.getRotationY())
                .alpha(to.getAlpha())
                .scaleX(scale).scaleY(scale)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        setViewVisibility(View.INVISIBLE);
                        animationEndCount++;
                        if (null != mOnTransitioAnimationListener) {
                            mOnTransitioAnimationListener.onViewAnimationStart(canvasView,to, animation);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setViewVisibility(View.VISIBLE);
                        animationEndCount--;
                        if (null != mOnTransitioAnimationListener) {
                            mOnTransitioAnimationListener.onViewAnimationEnd(canvasView,to, animation);
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
                        animationEndCount--;
                        if (null != mOnTransitioAnimationListener) {
                            mOnTransitioAnimationListener.onViewAnimationCancel(canvasView, animation);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        if (null != mOnTransitioAnimationListener) {
                            mOnTransitioAnimationListener.onViewAnimationRepeat(canvasView, animation);
                        }
                    }

                    private void setViewVisibility(int visibility) {
                        canvasView.getView().setVisibility(visibility);
                        to.setVisibility(visibility);
                    }
                }).start();
    }

    public boolean isAnimationRunning() {
        if (animationEndCount <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public void destroy(){
        instance = null;
        viewGroup = null;
        canvasViews = null;
        mOnTransitioAnimationListener = null;
        tmpViews = null;
    }
}
