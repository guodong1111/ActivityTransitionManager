package tw.guodong.activitytransitionmanager.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import tw.guodong.activitytransitionmanager.ActivityTransitionManager;


public class MainActivity extends ActionBarActivity {
    private ListView listView;
    private int[] imageRes = new int[]{R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,R.drawable.h,R.drawable.i,R.drawable.j};
    private String[] text = new String[]{"HelloWord_1","HelloWord_2","HelloWord_3","HelloWord_4","HelloWord_5","HelloWord_6","HelloWord_7","HelloWord_8","HelloWord_9","HelloWord_10"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new MyAdapter(this,imageRes,text));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View imageView = view.findViewById(R.id.imageView);
                View textView = view.findViewById(R.id.textView);
                imageView.setTag(ActivityTransitionManager.getTagKey(), "imageView");
                imageView.animate().setDuration(500);
                textView.setTag(ActivityTransitionManager.getTagKey(), "textView");
                textView.animate().setDuration(1000);
                ActivityTransitionManager.getInstance(MainActivity.this).addFormerView(imageView, textView);
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                Bundle bundle = new Bundle();
                bundle.putInt("imageRes",imageRes[position]);
                bundle.putString("text",text[position]);
                intent.putExtras(bundle);
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
        ActivityTransitionManager.getInstance(this).setAnimationDuration(500);
        ActivityTransitionManager.getInstance(this).animateFormerViewToLatterView();
    }
}
