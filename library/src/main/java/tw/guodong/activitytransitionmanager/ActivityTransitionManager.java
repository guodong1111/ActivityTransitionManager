package tw.guodong.activitytransitionmanager;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

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
    private int duration, animationEndCount;

    private ActivityTransitionManager(Activity activity) {
        this.activity = activity;
        viewGroup = new RelativeLayout(activity);
        canvasViews = new LinkedList<>();
        addViewGroupToWindow(viewGroup);
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

    public synchronized static ActivityTransitionManager getInstance(Context context) {
        if(context instanceof Activity){
            return getInstance((Activity)context);
        }else{
            throw new RuntimeException("context hava to instanceof Activity");
        }
    }

    public synchronized static ActivityTransitionManager getInstance(Activity activity) {
        if (null == instance) {
            instance = new ActivityTransitionManager(activity);
        }
        if (null != activity && !activity.equals(instance.activity)) {
            instance.activity = activity;
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
        for (View view : views) {
            if (null != view && !canvasViews.contains(view)) {
                canvasViews.add(getCanvasView(view));
            }
        }
    }

    public CanvasView getCanvasView(View view){
        viewGroup.setVisibility(View.VISIBLE);
        CanvasView canvasView = new CanvasView(activity.getApplicationContext(), view);
        addCanvasViewToWindow(canvasView);
        return canvasView;
    }

    public void animateFormerViewToLatterView(View... views) {
//        setActivtiyTransition();
        final View[] examineViews = views;
        if(null == views){
            return;
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
            addCanvasViewToWindow(canvasView);
        }
    }

    private void addCanvasViewToWindow(CanvasView canvasView){
        View view = canvasView.getView();
        int[] xy = getViewLocationOnScreen(view);
        canvasView.setX(xy[0]);
        canvasView.setY(xy[1]);
        try {
            viewGroup.addView(canvasView);
        } catch (IllegalStateException e) {
        }
    }

    public void animateViewToEnd(CanvasView canvasView){
        animateView(canvasView, null);
    }

    public int[] getViewLocationOnScreen(View view) {
        int[] location = {0,0};
        view.getLocationOnScreen(location);
        return location;
    }

    public void removeCanvasViewFromWindow(CanvasView canvasView){
        try {
            viewGroup.removeView(canvasView);
        } catch (IllegalStateException e) {
        }
    }

    public void clearFormerView() {
        stopAllAnimation();
        if(null != viewGroup){
            viewGroup.removeAllViews();
            viewGroup.setVisibility(View.GONE);
        }
        if(null != canvasViews){
            canvasViews.clear();
        }
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
        float x,y,scaleX,scaleY,rotation,rotationX,rotationY,alpha;
        int duration = this.duration;
        if(null != to){
            int toWidth = to.getWidth();
            int toHeight = to.getHeight();
            float canvasViewWidth = canvasView.getWidth();
            float canvasViewHeight = canvasView.getHeight();
            scaleX = toWidth / canvasViewWidth;
            scaleY = toHeight / canvasViewHeight;
            if (duration < 0) {
                duration = (int) to.animate().getDuration();
            }
            int[] xy;
            if(null != to.getParent()){
                xy = getViewLocationOnScreen(to);
            }else{
                xy = new int[]{(int) to.getX(), (int) to.getY()};
            }
            to.animate().setDuration(duration);
            x = xy[0] + canvasView.getWidth() * ((scaleX - 1) / 2);
            y = xy[1] + canvasView.getHeight() * ((scaleY - 1) / 2);
            rotation = to.getRotation();
            rotationX = to.getRotationX();
            rotationY = to.getRotationY();
            alpha = to.getAlpha();
        }else{
            if (duration < 0) {
                duration = (int) canvasView.animate().getDuration();
            }
            int[] xy = canvasView.getTmpLocation();
            x = xy[0];
            y = xy[1];
            x -= canvasView.getView().getWidth() / 2;
            y -= canvasView.getView().getHeight() / 2;
            scaleX = 0;
            scaleY = 0;
            rotation = 0;
            rotationX = 0;
            rotationY = 0;
            alpha = 0;
        }
        canvasView.animate()
                .x(x)
                .y(y)
                .rotation(rotation)
                .rotationX(rotationX)
                .rotationY(rotationY)
                .alpha(alpha)
                .scaleX(scaleX).scaleY(scaleY)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    private float alpha = 1f;

                    @Override
                    public void onAnimationStart(Animator animation) {
                        setViewVisibility(View.INVISIBLE);
                        if (!isAnimationRunning()) {
                            if (null != mOnTransitioAnimationListener) {
                                mOnTransitioAnimationListener.onAnimationStart();
                            }
                        }
                        animationEndCount++;
                        if (null != mOnTransitioAnimationListener) {
                            mOnTransitioAnimationListener.onViewAnimationStart(canvasView, animation);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setViewVisibility(View.VISIBLE);
                        animationEndCount--;
                        if (null != mOnTransitioAnimationListener) {
                            mOnTransitioAnimationListener.onViewAnimationEnd(canvasView, animation);
                        }
                        if (!isAnimationRunning()) {
                            if (null != viewGroup) {
                                viewGroup.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        clearFormerView();
                                    }
                                }, 50);
                            }
                            if (null != mOnTransitioAnimationListener) {
                                mOnTransitioAnimationListener.onAnimationEnd(animation);
                            }
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        setViewVisibility(View.VISIBLE);
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
                        if (null != canvasView.getView()) {
                            canvasView.getView().setVisibility(visibility);
                        }
                        if (null != to) {
                            if (View.VISIBLE == visibility) {
                                to.setAlpha(alpha);
                            } else {
                                alpha = to.getAlpha();
                                to.setAlpha(0f);
                            }
                        }
                    }
                }).start();
    }
}
