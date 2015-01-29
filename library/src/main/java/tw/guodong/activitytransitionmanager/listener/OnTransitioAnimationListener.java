package tw.guodong.activitytransitionmanager.listener;

import android.animation.Animator;
import android.view.View;

/**
 * Created by Tony on 2015/1/29.
 */
public interface OnTransitioAnimationListener {
    public void onAnimationEnd(Animator animation);
    public void onViewAnimationStart(View view,Animator animation);
    public void onViewAnimationEnd(View view,Animator animation);
    public void onViewAnimationCancel(View view,Animator animation);
    public void onViewAnimationRepeat(View view,Animator animation);
}
