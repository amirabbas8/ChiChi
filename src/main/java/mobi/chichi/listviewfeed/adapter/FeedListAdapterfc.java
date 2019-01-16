package mobi.chichi.listviewfeed.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import mobi.chichi.R;
import mobi.chichi.listviewfeed.app.AppController;
import mobi.chichi.listviewfeed.data.FeedItem;


public class FeedListAdapterfc extends BaseAdapter {

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ImageButton btnlike, btncomment;
    FeedItem item;
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;


    public FeedListAdapterfc(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }


    public int getCount() {
        return feedItems.size();
    }


    public Object getItem(int location) {
        return feedItems.get(location);
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        NetworkImageView profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);
        TextView lastcommentname = (TextView) convertView.findViewById(R.id.lastcommentname);
        NetworkImageView lastcommentprofilePic = (NetworkImageView) convertView.findViewById(R.id.lastcommentprofilePic);
        TextView lastcomment = (TextView) convertView.findViewById(R.id.lastcomment);
        LinearLayout l1 = (LinearLayout) convertView.findViewById(R.id.l1);
        LinearLayout liner = (LinearLayout) convertView.findViewById(R.id.liner);
        RelativeLayout l2 = (RelativeLayout) convertView.findViewById(R.id.l2);
        btnlike = (ImageButton) convertView.findViewById(R.id.btnlike);
        btncomment = (ImageButton) convertView.findViewById(R.id.btncomment);
        ImageButton option = (ImageButton) convertView.findViewById(R.id.option);
        String fontPath = "fonts/BYekan.ttf";
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), fontPath);
        name.setTypeface(tf);
        item = feedItems.get(position);
        name.setText(item.getName());
        lastcommentname.setVisibility(View.GONE);
        lastcomment.setVisibility(View.GONE);
        lastcommentprofilePic.setVisibility(View.GONE);
        l1.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);
        profilePic.setImageUrl("http://chichi.mobi/profileimages/" + item.getProfilePic(), imageLoader);
        option.setVisibility(View.GONE);
        liner.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent();

                String fid = String.valueOf(feedItems.get(position).getUserId());
                String fname = String.valueOf(feedItems.get(position).getName());
                String fprofilePic = String.valueOf(feedItems.get(position).getProfilePic());
                intent.putExtra("fid", fid);
                intent.putExtra("fname", fname);
                intent.putExtra("fprofilePic", fprofilePic);
                activity.setResult(-1, intent);
                activity.finish();

            }
        });
        return convertView;
    }

}
