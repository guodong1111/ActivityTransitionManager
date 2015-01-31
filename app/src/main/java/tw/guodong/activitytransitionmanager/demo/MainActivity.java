package tw.guodong.activitytransitionmanager.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tw.guodong.activitytransitionmanager.ActivityTransitionManager;

public class MainActivity extends ActionBarActivity {

    private ViewPager awesomePager;
    private AwesomePagerAdapter awesomeAdapter;

    private LayoutInflater mInflater;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInflater = getLayoutInflater();
        awesomeAdapter = new AwesomePagerAdapter();
        awesomePager = (ViewPager) findViewById(R.id.awesomepager);
        awesomePager.setAdapter(awesomeAdapter);

    }

    private class AwesomePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final View view = mInflater.inflate(R.layout.adapter_view, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

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