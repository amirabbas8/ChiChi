package mobi.chichi;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import mobi.chichi.listviewfeed.adapter.FeedListAdapter;
import mobi.chichi.listviewfeed.data.FeedItem;


public class hashtag extends Activity {

    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;

    private Button btnLoadMore;
    private String n = "1";
    private String pid = "0";
    private int pidint = 0;
    private ProgressBar hprog;
    private ImageButton refresh;
    private TextView hashtag;
    private String hashtagtext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hashtag);

        hashtag = (TextView) findViewById(R.id.hashtag);

        btnLoadMore = new Button(this);
        hprog = (ProgressBar) findViewById(R.id.progressBar1);
        //list

        listView = (ListView) findViewById(R.id.list);
        listView.setVisibility(View.INVISIBLE);
        feedItems = new ArrayList<>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);

        //hashtag
        refresh = (ImageButton) findViewById(R.id.refresh);
        // http://twitter.com/status/1234
        Uri data = getIntent().getData();
        if (data != null) {
            //   String scheme = data.getScheme(); // "http"

            //  String host = data.getHost(); // "twitter.com"
            //  String inurl = data.toString();

            //  List<String> params = data.getPathSegments();
            //   String first = params.get(0); // "status"
            //strip off hashtag from the URI
            String tag = data.toString().split("/")[3];
            //   tag = tag.replaceAll("#", "");
            hashtag.setText(tag);
            n = "1";
            hprog.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.INVISIBLE);

            new NetCheck().execute();
        }
        refresh.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (hashtag.getText().toString().length() > 0) {
                    n = "1";
                    hprog.setVisibility(View.VISIBLE);
                    refresh.setVisibility(View.INVISIBLE);

                    new NetCheck().execute();
                }

            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.back);

        back.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                finish();

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    private class NetCheck extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected Boolean doInBackground(String... args) {

            /** * Gets current device state and checks for working internet connection by trying Google. **/

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
                if (n.equals("1")) {

                    new Processgethashtaglist().execute();
                } else if (n.equals("2")) {
                    new Processgethashtaglistloadmore().execute();
                }
            } else {
                hprog.setVisibility(View.INVISIBLE);
                refresh.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Processgethashtaglist extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hashtagtext = hashtag.getText().toString();

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.getsearchlist(hashtagtext);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.isNull("searchposts")) {
                    n = "1";

                    hprog.setVisibility(View.VISIBLE);
                    refresh.setVisibility(View.INVISIBLE);

                    new NetCheck().execute();
                }
                JSONArray feedArray = json.getJSONArray("searchposts");
                listView.setVisibility(View.INVISIBLE);
                listView.removeFooterView(btnLoadMore);
                feedItems.clear();
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);

                    FeedItem item = new FeedItem();
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("name"));
                    item.setUserId(feedObj.getString("userid"));
                    // Image might be null sometimes
                    String image = "http://chichi.mobi/postsimages/" + feedObj.getString("image");
                    if (("http://chichi.mobi/postsimages/").equals(image)) {
                        image = null;

                    }
                    item.setImage(image);
                    item.setStatus(feedObj.getString("status"));
                    item.setProfilePic(feedObj.getString("profilePic"));
                    item.setTimeStamp(feedObj.getString("timeStamp"));

                    // url might be null sometimes
                    String feedUrl = feedObj.isNull("url") ? null : feedObj
                            .getString("url");
                    item.setUrl(feedUrl);
                    item.setLastCommentName(feedObj.getString("lastcommentname"));
                    item.setLastCommentUserId(feedObj.getString("lastcommentuserid"));
                    item.setLastCommentProfilePic(feedObj.getString("lastcommentprofilePic"));
                    item.setLastComment(feedObj.getString("lastcomment"));
                    item.setNLike(feedObj.getString("nlike"));
                    item.setNComment(feedObj.getString("ncomment"));
                    item.setMylike(feedObj.getString("mylike"));
                    pid = String.valueOf(feedObj.getInt("id"));
                    pidint = pidint + 1;
                    feedItems.add(item);
                }
                hprog.setVisibility(View.INVISIBLE);
                refresh.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                btnLoadMore.setText(R.string.loadmore);
                btnLoadMore.setBackgroundResource(R.drawable.button_white);
                // Adding Load More button to lisview at bottom
                listView.addFooterView(btnLoadMore);
                // notify data changes to list adapater
                listAdapter.notifyDataSetChanged();
                // Getting adapter
                listAdapter = new FeedListAdapter(hashtag.this, feedItems);
                listView.setAdapter(listAdapter);

                if (pidint >= 10) {
                    btnLoadMore.setVisibility(View.VISIBLE);
                    pidint = 0;
                } else {
                    btnLoadMore.setVisibility(View.GONE);
                    pidint = 0;
                }
                btnLoadMore.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View arg0) {
                        hprog.setVisibility(View.VISIBLE);
                        refresh.setVisibility(View.INVISIBLE);
                        n = "2";
                        new NetCheck().execute();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        hprog.setVisibility(View.INVISIBLE);
                        refresh.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

    private class Processgethashtaglistloadmore extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            hashtagtext = hashtag.getText().toString();

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.getsearchlistloadmore(hashtagtext, pid);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.isNull("searchposts")) {
                    hprog.setVisibility(View.VISIBLE);
                    refresh.setVisibility(View.INVISIBLE);
                    n = "2";
                    new NetCheck().execute();
                } else {

                    JSONArray feedArray = json.getJSONArray("searchposts");

                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);

                        FeedItem item = new FeedItem();
                        item.setId(feedObj.getInt("id"));
                        item.setName(feedObj.getString("name"));
                        item.setUserId(feedObj.getString("userid"));
                        // Image might be null sometimes
                        String image = "http://chichi.mobi/postsimages/" + feedObj.getString("image");
                        if (("http://chichi.mobi/postsimages/").equals(image)) {
                            image = null;

                        }
                        item.setImage(image);
                        item.setStatus(feedObj.getString("status"));
                        item.setProfilePic(feedObj.getString("profilePic"));
                        item.setTimeStamp(feedObj.getString("timeStamp"));

                        // url might be null sometimes
                        String feedUrl = feedObj.isNull("url") ? null : feedObj
                                .getString("url");
                        item.setUrl(feedUrl);
                        item.setLastCommentName(feedObj.getString("lastcommentname"));
                        item.setLastCommentUserId(feedObj.getString("lastcommentuserid"));
                        item.setLastCommentProfilePic(feedObj.getString("lastcommentprofilePic"));
                        item.setLastComment(feedObj.getString("lastcomment"));
                        item.setNLike(feedObj.getString("nlike"));
                        item.setNComment(feedObj.getString("ncomment"));
                        item.setMylike(feedObj.getString("mylike"));
                        pid = String.valueOf(feedObj.getInt("id"));
                        pidint = pidint + 1;
                        feedItems.add(item);
                    }
                    listView.setVisibility(View.VISIBLE);
                    hprog.setVisibility(View.INVISIBLE);
                    refresh.setVisibility(View.VISIBLE);
                    // notify data changes to list adapater
                    listAdapter.notifyDataSetChanged();
                    if (pidint >= 10) {
                        btnLoadMore.setVisibility(View.VISIBLE);
                        pidint = 0;
                    } else {
                        btnLoadMore.setVisibility(View.GONE);
                        pidint = 0;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        hprog.setVisibility(View.INVISIBLE);
                        refresh.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }
}
