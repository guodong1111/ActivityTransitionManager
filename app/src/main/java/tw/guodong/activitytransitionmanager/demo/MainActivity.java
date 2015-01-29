package tw.guodong.activitytransitionmanager.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import tw.guodong.activitytransitionmanager.ActivityTransitionManager;


public class MainActivity extends ActionBarActivity {
    View view,imageView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        imageView.animate().setDuration(500);
        view.animate().setDuration(1000);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityTransitionManager.getInstance(MainActivity.this).addFormerView(imageView, view);
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        ActivityTransitionManager.getInstance(this).stopAllAnimation();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityTransitionManager.getInstance(this).setAnimationDuration(2000);
        ActivityTransitionManager.getInstance(this).animateFormerViewToLatterView(view,imageView);
    }
}
