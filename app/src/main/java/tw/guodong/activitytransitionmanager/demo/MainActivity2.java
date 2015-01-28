package tw.guodong.activitytransitionmanager.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import tw.guodong.activitytransitionmanager.ActivityTransitionManager;


public class MainActivity2 extends ActionBarActivity {
    View view,imageView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);
        view = findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button2);
        imageView = findViewById(R.id.imageView);
        if(savedInstanceState == null){
            ActivityTransitionManager.getInstance(this).setTransparentBackground(true);
            ActivityTransitionManager.getInstance(this).animateFormerViewToLatterView(view,imageView);
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
        ActivityTransitionManager.getInstance(this).addFormerView(imageView, view);
        super.finish();
    }
}
