package mobi.chichi.listviewfeed.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import mobi.chichi.DatabaseHandler;
import mobi.chichi.R;
import mobi.chichi.UserFunctions;
import mobi.chichi.listviewfeed.app.AppController;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.otherprofile;


public class FeedListAdapterpbff extends BaseAdapter {

    private static String KEY_SUCCESS = "success";
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    FeedItem item;
    String fid;
    int pos;
    String fname;
    String fprofilepic;
    int n = 0;
    ProgressBar oprog;
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;


    public FeedListAdapterpbff(Activity activity, List<FeedItem> feedItems) {
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
            convertView = inflater.inflate(R.layout.feed_itempbff, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView phonebookname = (TextView) convertView.findViewById(R.id.phonebookname);
        NetworkImageView profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);
        LinearLayout liner = (LinearLayout) convertView.findViewById(R.id.liner);
        ImageButton option = (ImageButton) convertView.findViewById(R.id.option);
        oprog = (ProgressBar) convertView.findViewById(R.id.progressBar1);
        String fontPath = "fonts/BYekan.ttf";
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), fontPath);
        name.setTypeface(tf);
        phonebookname.setTypeface(tf);
        item = feedItems.get(position);
        name.setText(item.getName());
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(item.getUserId()));
        String contactName;

        // querying contact data store
        Cursor cursor;
        cursor = activity.getContentResolver().query(uri, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            phonebookname.setText(contactName);
        }

        cursor.close();

        // user profile pic
        profilePic.setImageUrl("http://chichi.mobi/profileimages/" + item.getProfilePic(), imageLoader);

        profilePic.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String userid = String.valueOf(feedItems.get(position).getUserId());
                Intent intent = new Intent(activity, otherprofile.class);
                intent.putExtra("userid", userid);
                String d = String.valueOf(pos);
                intent.putExtra("pos", d);
                activity.startActivityForResult(intent, 12);
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
        liner.setOnClickListener(new OnClickListener() {

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
        if ("1".equals(item.getoprog())) {

            oprog.setVisibility(View.VISIBLE);
            option.setVisibility(View.INVISIBLE);

        } else if ("2".equals(item.getoprog())) {

            oprog.setVisibility(View.INVISIBLE);
            option.setVisibility(View.VISIBLE);

        }
        if ("1".equals(item.getMylike())) {

            liner.setBackgroundColor(0xFF00FF00);
            option.setBackgroundColor(0xFF00FF00);
            option.setImageResource(R.drawable.delete_icon);
            option.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    pos = position;
                    fid = feedItems.get(position).getUserId();
                    fname = feedItems.get(position).getName();
                    fprofilepic = feedItems.get(position).getProfilePic();
                    feedItems.get(position).setoprog("1");
                    notifyDataSetChanged();
                    n = 5;
                    new NetCheck().execute();
                }
            });
        } else if ("2".equals(item.getMylike())) {

            liner.setBackgroundColor(0xFFFFFFFF);
            option.setBackgroundColor(R.attr.colorPrimary);
            option.setImageResource(R.drawable.add_icon);
            option.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    pos = position;
                    fid = feedItems.get(position).getUserId();
                    fname = feedItems.get(position).getName();
                    fprofilepic = feedItems.get(position).getProfilePic();
                    feedItems.get(position).setoprog("1");
                    notifyDataSetChanged();
                    n = 4;
                    new NetCheck().execute();
                }
            });
        }

        return convertView;
    }




    private class NetCheck extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected Boolean doInBackground(String... args) {

            /** * Gets current device state and checks for working internet connection by trying Google. **/

            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://chichi.mobi/addordel.php");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;

        }


        @Override
        protected void onPostExecute(Boolean th) {
            if (th) {
                if (n == 4) {
                    new Processadd().execute();
                } else if (n == 5) {
                    new Processdelete().execute();
                }
            } else {
                feedItems.get(pos).setoprog("2");
                notifyDataSetChanged();
                Toast.makeText(activity, R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class Processadd extends AsyncTask<String, String, JSONObject> {

        String idno, myname, myprofilepic;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseHandler db1 = new DatabaseHandler(activity);

            HashMap<String, String> user;
            user = db1.getUserDetails();
            idno = user.get("idno");
            myname = user.get("realname");
            myprofilepic = user.get("profileimage");
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            return userFunction.addffusername(idno, fid, fname, fprofilepic, myname, myprofilepic);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 51) {
                        feedItems.get(pos).setoprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorfunblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 52) {
                        feedItems.get(pos).setoprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 2) {
                        feedItems.get(pos).setoprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.wasfriend, Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(res) == 1) {
                        feedItems.get(pos).setoprog("2");
                        feedItems.get(pos).setMylike("1");
                        notifyDataSetChanged();
                    } else {
                        feedItems.get(pos).setoprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorproblem, Toast.LENGTH_SHORT).show();

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class Processdelete extends AsyncTask<String, String, JSONObject> {

        String idno;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseHandler db1 = new DatabaseHandler(activity);

            HashMap<String, String> user;
            user = db1.getUserDetails();
            idno = user.get("idno");
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            return userFunction.delete(idno, fid);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        feedItems.get(pos).setoprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(res) == 2) {
                        feedItems.get(pos).setoprog("2");

                        feedItems.get(pos).setMylike("2");
                        notifyDataSetChanged();
                    } else {
                        feedItems.get(pos).setoprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorproblem, Toast.LENGTH_SHORT).show();

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
