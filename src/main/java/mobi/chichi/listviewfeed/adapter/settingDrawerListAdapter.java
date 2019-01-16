package mobi.chichi.listviewfeed.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import mobi.chichi.R;
import mobi.chichi.listviewfeed.app.AppController;
import mobi.chichi.listviewfeed.data.settingDrawerItem;


public class settingDrawerListAdapter extends BaseAdapter {

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private Context context;
    private ArrayList<settingDrawerItem> settingDrawerItems;


    public settingDrawerListAdapter(Context context, ArrayList<settingDrawerItem> settingDrawerItems) {
        this.context = context;

        this.settingDrawerItems = settingDrawerItems;

    }


    public int getCount() {
        return settingDrawerItems.size();
    }


    public Object getItem(int position) {
        return settingDrawerItems.get(position);
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }
            ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
            TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

            imgIcon.setVisibility(View.INVISIBLE);
            txtTitle.setText(settingDrawerItems.get(position).getTitle());

            // displaying count
            // check whether it set visible or not
            if (settingDrawerItems.get(position).getCounterVisibility()) {
                txtCount.setText(settingDrawerItems.get(position).getCount());
            } else {
                // hide the counter view
                txtCount.setVisibility(View.GONE);
            }


        return convertView;
    }

}
