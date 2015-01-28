package tw.guodong.activitytransitionmanager;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by USER on 2015/1/28.
 */
public class CanvasView extends View{
    private View mView;

    public CanvasView(Context context,View view) {
        super(context);
        mView = view;
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setHeight(view.getHeight());
        setWidth(view.getWidth());
        setAlpha(mView.getAlpha());
    }

    public void setHeight(int height) {
        getLayoutParams().height = height;
        requestLayout();
    }

    public void setWidth(int width) {
        getLayoutParams().width = width;
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mView.draw(canvas);
    }

    public View getView() {
        return mView;
    }
}
