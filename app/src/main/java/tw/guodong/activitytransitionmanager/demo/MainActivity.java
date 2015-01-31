package tw.guodong.activitytransitionmanager.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tw.guodong.activitytransitionmanager.ActivityTransitionManager;

public class MainActivity extends ActionBarActivity {
    private int[] imageRes = new int[]{R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,R.drawable.h,R.drawable.i,R.drawable.j};
    private String[] text = new String[]{"HelloWord_1","HelloWord_2","HelloWord_3","HelloWord_4","HelloWord_5","HelloWord_6","HelloWord_7","HelloWord_8","HelloWord_9","HelloWord_10"};
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
            return imageRes.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = mInflater.inflate(R.layout.adapter_view, null);
            final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            final TextView textView = (TextView) view.findViewById(R.id.textView);
            imageView.setImageResource(imageRes[position]);
            textView.setText(text[position]);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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