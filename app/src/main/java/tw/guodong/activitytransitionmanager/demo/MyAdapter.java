package tw.guodong.activitytransitionmanager.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by USER on 2015/1/30.
 */
public class MyAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private int[] imageRes;
    private String[] text;

    public MyAdapter(Context context,int[] imageRes,String[] text){
        this.mInflater = LayoutInflater.from(context);
        this.imageRes = imageRes;
        this.text = text;
    }

    @Override
    public int getCount() {
        return imageRes.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.adapter_view, null);
            holder.imageView = (ImageView)convertView.findViewById(R.id.imageView);
            holder.textView = (TextView)convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.imageView.setImageResource(imageRes[position]);
        holder.textView.setText(text[position]);
        return convertView;
    }
    class ViewHolder{
        public ImageView imageView;
        public TextView textView;
    }
}
