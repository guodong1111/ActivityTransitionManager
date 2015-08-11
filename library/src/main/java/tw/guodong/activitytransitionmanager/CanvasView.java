package tw.guodong.activitytransitionmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tony on 2015/1/28.
 */
public class CanvasView extends View{
    private View mView;
    private int[] tmpLocation;

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
        Bitmap vBitmap = null;
        try {
            vBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas vBitmapCanvas = new Canvas(vBitmap);
            mView.draw(vBitmapCanvas);
            Rect src = new Rect();
            Rect dst = new Rect();
            src.left = 0;
            src.top = 0;
            src.right = vBitmap.getWidth();
            src.bottom = vBitmap.getHeight();
            dst.left = 0;
            dst.top = 0;
            dst.right = vBitmap.getWidth();
            dst.bottom = vBitmap.getHeight();
            canvas.drawBitmap(vBitmap, src, dst, null);
        }catch(Exception e){
        }
        if(null != vBitmap){
            vBitmap.recycle();
        }
    }

    public View getView() {
        return mView;
    }

    public int[] getTmpLocation() {
        return tmpLocation;
    }

    public void setTmpLocation(int[] tmpLocation) {
        this.tmpLocation = tmpLocation;
    }
}
