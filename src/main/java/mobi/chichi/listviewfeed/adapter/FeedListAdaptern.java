package mobi.chichi.listviewfeed.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;
import java.util.regex.Pattern;

import mobi.chichi.R;
import mobi.chichi.feed_item;
import mobi.chichi.listviewfeed.app.AppController;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.otherprofile;


public class FeedListAdaptern extends BaseAdapter {

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    FeedItem item;
    int pos;
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;


    public FeedListAdaptern(Activity activity, List<FeedItem> feedItems) {
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

        pos = position;
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_itemn, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        NetworkImageView profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);
        TextView statusMsg = (TextView) convertView.findViewById(R.id.txtStatusMsg);

        String fontPath = "fonts/BYekan.ttf";
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), fontPath);
        name.setTypeface(tf);
        statusMsg.setTypeface(tf);
        item = feedItems.get(position);

        // user profile pic
        profilePic.setImageUrl("http://chichi.mobi/profileimages/" + item.getProfilePic(), imageLoader);
        name.setText(item.getName());
        profilePic.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String userid = String.valueOf(feedItems.get(position).getUserId());
                String username = String.valueOf(feedItems.get(position).getName());
                String userprofilepic = String.valueOf(feedItems.get(position).getProfilePic());
                Intent intent = new Intent(activity, otherprofile.class);
                intent.putExtra("userid", userid);
                intent.putExtra("username", username);
                intent.putExtra("userprofilepic", userprofilepic);
                activity.startActivity(intent);
            }
        });
        name.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String userid = String.valueOf(feedItems.get(position).getUserId());
                String username = String.valueOf(feedItems.get(position).getName());
                String userprofilepic = String.valueOf(feedItems.get(position).getProfilePic());
                Intent intent = new Intent(activity, otherprofile.class);
                intent.putExtra("userid", userid);
                intent.putExtra("username", username);
                intent.putExtra("userprofilepic", userprofilepic);
                activity.startActivity(intent);
            }
        });
        if ("comment".equals(item.getType())) {
            statusMsg.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    String postid = String.valueOf(feedItems.get(position).getPostID());

                    Intent intent = new Intent(activity, feed_item.class);
                    intent.putExtra("postid", postid);
                    activity.startActivity(intent);
                }
            });
            statusMsg.setText(activity.getResources().getString(R.string.newcomment) + item.getStatus());
        } else if ("like".equals(item.getType())) {
            statusMsg.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    String postid = String.valueOf(feedItems.get(position).getPostID());

                    Intent intent = new Intent(activity, feed_item.class);
                    intent.putExtra("postid", postid);
                    activity.startActivity(intent);
                }
            });
            statusMsg.setText(activity.getResources().getString(R.string.newlike));
        } else if ("friend".equals(item.getType())) {
            statusMsg.setText(R.string.newfollower);
            statusMsg.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    String userid = String.valueOf(feedItems.get(position).getUserId());
                    String username = String.valueOf(feedItems.get(position).getName());
                    String userprofilepic = String.valueOf(feedItems.get(position).getProfilePic());
                    Intent intent = new Intent(activity, otherprofile.class);
                    intent.putExtra("userid", userid);
                    intent.putExtra("username", username);
                    intent.putExtra("userprofilepic", userprofilepic);
                    activity.startActivity(intent);
                }
            });
        } else if ("chichi".equals(item.getType())) {
            statusMsg.setText(item.getStatus());
            profilePic.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                }
            });
        } else if ("tag".equals(item.getType())) {
            statusMsg.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    String postid = String.valueOf(feedItems.get(position).getPostID());
                    Intent intent = new Intent(activity, feed_item.class);
                    intent.putExtra("postid", postid);
                    activity.startActivity(intent);
                }
            });
            String username = String.valueOf(feedItems.get(position).getName());
            statusMsg.setText(username + activity.getResources().getString(R.string.newtag));
        }
        Pattern tagMatcher = Pattern.compile("[#]+[A-Za-z0-9-_]+\\b");
        String newActivityURL = "chichihash://chichi.mobi/";
        Linkify.addLinks(statusMsg, tagMatcher, newActivityURL);

        return convertView;
    }
}
