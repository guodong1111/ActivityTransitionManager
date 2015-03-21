package tw.guodong.activitytransitionmanager.demo;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import tw.guodong.activitytransitionmanager.ActivityTransitionManager;
import tw.guodong.activitytransitionmanager.CanvasView;
import tw.guodong.activitytransitionmanager.listener.OnTransitioAnimationListener;


public class MainActivity2 extends ActionBarActivity {
    ImageView imageView,imageView_bg;
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);
        Bundle bundle =this.getIntent().getExtras();
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView_bg = (ImageView) findViewById(R.id.imageView_bg);
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button2);
        imageView.setImageResource(bundle.getInt("imageRes"));
        textView.setText(bundle.getString("text"));
        imageView_bg.setAlpha(0f);
        button.setAlpha(0f);
        imageView.setTag(ActivityTransitionManager.getTagKey(),"imageView");
        textView.setTag(ActivityTransitionManager.getTagKey(), "textView");
        if(savedInstanceState == null){
            ActivityTransitionManager.getInstance(this).animateFormerViewToLatterView(imageView,textView);
            ActivityTransitionManager.getInstance(this).setOnTransitioAnimationListener(new OnTransitioAnimationListener() {
                @Override
                public void onAnimationStart() {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    button.animate().alpha(1f).setDuration(500).start();
                    imageView_bg.animate().alpha(1f).setDuration(500).start();
                }

                @Override
                public void onViewAnimationStart(CanvasView from, View to, Animator animation) {

                }

                @Override
                public void onViewAnimationEnd(CanvasView from, View to, Animator animation) {

                }

                @Override
                public void onViewAnimationCancel(CanvasView view, Animator animation) {

                }

                @Override
                public void onViewAnimationRepeat(CanvasView view, Animator animation) {

                }
            });
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        ActivityTransitionManager.getInstance(this).addFormerView();
        super.finish();
    }
}
