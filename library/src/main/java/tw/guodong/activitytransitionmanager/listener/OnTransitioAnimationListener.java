package tw.guodong.activitytransitionmanager.listener;

import android.animation.Animator;
import android.view.View;

import tw.guodong.activitytransitionmanager.CanvasView;

/**
 * Created by Tony on 2015/1/29.
 */
public interface OnTransitioAnimationListener {
    public void onAnimationStart();
    public void onAnimationEnd(Animator animation);
    public void onViewAnimationStart(CanvasView from,View to,Animator animation);
    public void onViewAnimationEnd(CanvasView from,View to,Animator animation);
    public void onViewAnimationCancel(CanvasView view,Animator animation);
    public void onViewAnimationRepeat(CanvasView view,Animator animation);
}
