package mobi.chichi;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobi.chichi.listviewfeed.adapter.FeedListAdapter_writings;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.pulltoupdatelibrary.IonRefreshListener;
import mobi.chichi.pulltoupdatelibrary.PullToUpdateListView;


public class mylike_writings extends Activity {

    Button btnLoadMore;
    String n = "1";
    String pid = "0";
    int pidint = 0;
    ProgressBar prog;
    ImageButton refresh,back;
    private PullToUpdateListView listView;
    private FeedListAdapter_writings listAdapter;
    private List<FeedItem> feedItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylike_writings);
        btnLoadMore = new Button(mylike_writings.this);

        //list

        listView = (PullToUpdateListView) findViewById(R.id.list);
        listView.setVisibility(View.INVISIBLE);
        feedItems = new ArrayList<>();

        listAdapter = new FeedListAdapter_writings(mylike_writings.this, feedItems);
        listView.setAdapter(listAdapter);
        n = "1";
        new NetCheck().execute();
        listView.setPullMode(PullToUpdateListView.MODE.UP_AND_DOWN);
        listView.setAutoLoad(true, 8);
        listView.setPullMessageColor(R.color.textColorPrimary);
        listView.setLoadingMessage(getResources().getString(R.string.loading));
        listView.setPullRotateImage(getResources().getDrawable(R.drawable.refresh_icon));
        prog = (ProgressBar) findViewById(R.id.progressBar1);

        refresh = (ImageButton) findViewById(R.id.refresh);

        refresh.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                n = "1";
                new NetCheck().execute();
            }
        });
        back = (ImageButton) findViewById(R.id.back);

        back.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
            finish();
            }
        });
        listView.setOnRefreshListener(new IonRefreshListener() {
            @Override
            public void onRefreshUp() {
                n = "1";

                new NetCheck().execute();
            }

            @Override
            public void onRefeshDown() {
                n = "2";

                new NetCheck().execute();
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

                    new Processgetmylike_writingslist().execute();

                } else if (n.equals("2")) {

                    new Processgetmylike_writingslistloadmore().execute();
                }
            } else {
                {
                    if (0 == feedItems.size()) {
                        refresh.setVisibility(View.VISIBLE);
                        prog.setVisibility(View.INVISIBLE);

                    } else {
                        refresh.setVisibility(View.GONE);
                        prog.setVisibility(View.GONE);
                    }
                    if (n.equals("1")) {

                        listView.onRefreshUpComplete();
                    } else if (n.equals("2")) {
                        btnLoadMore.setVisibility(View.VISIBLE);
                        listView.onRefreshDownComplete(null);
                    }
                    Toast.makeText(mylike_writings.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class Processgetmylike_writingslist extends AsyncTask<String, String, JSONObject> {

        String idno;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
            HashMap<String, String> user;
            user = db1.getUserDetails();
            idno = user.get("idno");

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.getmylike_writingslist(idno);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                JSONArray feedArray = json.getJSONArray("posts");
                listView.setVisibility(View.INVISIBLE);
                listView.removeFooterView(btnLoadMore);
                feedItems.clear();
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);

                    FeedItem item = new FeedItem();
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("name"));
                    item.setUserId(feedObj.getString("userid"));
                    item.setStatus(feedObj.getString("status"));
                    item.setProfilePic(feedObj.getString("profilePic"));
                    item.setNLike(feedObj.getString("nlike"));
                    item.setMylike(feedObj.getString("mylike"));
                    item.setoprog("2");
                    item.setlprog("2");
                    item.setfragment("mylikeswritings");
                    pid = String.valueOf(feedObj.getInt("id"));
                    pidint = pidint + 1;
                    feedItems.add(item);
                }
                if (0 == feedItems.size()) {
                    refresh.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.INVISIBLE);

                } else {
                    refresh.setVisibility(View.GONE);
                    prog.setVisibility(View.GONE);
                }
                if (n.equals("1")) {

                    listView.onRefreshUpComplete();
                } else if (n.equals("2")) {
                    btnLoadMore.setVisibility(View.VISIBLE);
                    listView.onRefreshDownComplete(null);
                }
                listView.setVisibility(View.VISIBLE);
                btnLoadMore.setText(R.string.loadmore);
                btnLoadMore.setBackgroundResource(R.drawable.button_white);
                // Adding Load More button to lisview at bottom
                listView.addFooterView(btnLoadMore);
                // notify data changes to list adapater
                listAdapter.notifyDataSetChanged();
                // Getting adapter
                listAdapter = new FeedListAdapter_writings(mylike_writings.this, feedItems);
                listView.setAdapter(listAdapter);

                if (pidint >= 10) {
                    btnLoadMore.setVisibility(View.VISIBLE);
                    pidint = 0;
                } else {
                    btnLoadMore.setVisibility(View.GONE);
                    pidint = 0;
                }
                btnLoadMore.setOnClickListener(new OnClickListener() {

                    public void onClick(View arg0) {

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
                        if (0 == feedItems.size()) {
                            refresh.setVisibility(View.VISIBLE);
                            prog.setVisibility(View.INVISIBLE);

                        } else {
                            refresh.setVisibility(View.GONE);
                            prog.setVisibility(View.GONE);
                        }

                        listView.onRefreshUpComplete();
                        listView.onRefreshDownComplete(null);
                        Toast.makeText(mylike_writings.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

    private class Processgetmylike_writingslistloadmore extends AsyncTask<String, String, JSONObject> {

        String idno;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
            HashMap<String, String> user;
            user = db1.getUserDetails();
            idno = user.get("idno");

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.getmylike_writingslistloadmore(idno, pid);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                JSONArray feedArray = json.getJSONArray("posts");

                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);

                    FeedItem item = new FeedItem();
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("name"));
                    item.setUserId(feedObj.getString("userid"));
                    item.setStatus(feedObj.getString("status"));
                    item.setProfilePic(feedObj.getString("profilePic"));
                    item.setNLike(feedObj.getString("nlike"));
                    item.setMylike(feedObj.getString("mylike"));
                    item.setoprog("2");
                    item.setlprog("2");
                    item.setfragment("mylikeswritings");
                    pid = String.valueOf(feedObj.getInt("id"));
                    pidint = pidint + 1;
                    feedItems.add(item);
                }
                listView.setVisibility(View.VISIBLE);
                if (0 == feedItems.size()) {
                    refresh.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.INVISIBLE);

                } else {
                    refresh.setVisibility(View.GONE);
                    prog.setVisibility(View.GONE);
                }
                if (n.equals("1")) {

                    listView.onRefreshUpComplete();
                } else if (n.equals("2")) {
                    btnLoadMore.setVisibility(View.VISIBLE);
                    listView.onRefreshDownComplete(null);
                }
                // notify data changes to list adapater
                listAdapter.notifyDataSetChanged();
                if (pidint >= 10) {
                    btnLoadMore.setVisibility(View.VISIBLE);
                    pidint = 0;
                } else {
                    btnLoadMore.setVisibility(View.GONE);
                    pidint = 0;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        if (0 == feedItems.size()) {
                            refresh.setVisibility(View.VISIBLE);
                            prog.setVisibility(View.INVISIBLE);

                        } else {
                            refresh.setVisibility(View.GONE);
                            prog.setVisibility(View.GONE);
                        }

                        listView.onRefreshUpComplete();
                        listView.onRefreshDownComplete(null);
                        Toast.makeText(mylike_writings.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }
}
