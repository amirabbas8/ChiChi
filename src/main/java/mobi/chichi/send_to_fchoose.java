package mobi.chichi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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

import mobi.chichi.listviewfeed.adapter.FeedListAdapterfc;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.pulltoupdatelibrary.IonRefreshListener;
import mobi.chichi.pulltoupdatelibrary.PullToUpdateListView;


public class send_to_fchoose extends Activity {

    ProgressBar prog;
    ImageButton refresh;
    private PullToUpdateListView listViewfc;
    private FeedListAdapterfc listAdapterfc;
    private List<FeedItem> feedItemsfc;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.managefriend);
        prog = (ProgressBar) findViewById(R.id.progressBar1);
        //list
        listViewfc = (PullToUpdateListView) findViewById(R.id.list);
        listViewfc.setVisibility(View.INVISIBLE);
        feedItemsfc = new ArrayList<>();
        listAdapterfc = new FeedListAdapterfc(this, feedItemsfc);
        listViewfc.setAdapter(listAdapterfc);
        new NetCheck().execute();
        listViewfc.setPullMode(PullToUpdateListView.MODE.UP_ONLY);
        listViewfc.setAutoLoad(true, 8);
        listViewfc.setPullMessageColor(R.color.textColorPrimary);
        listViewfc.setLoadingMessage(getResources().getString(R.string.loading));
        listViewfc.setPullRotateImage(getResources().getDrawable(R.drawable.refresh_icon));
        prog = (ProgressBar) findViewById(R.id.progressBar1);

        refresh = (ImageButton) findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                refresh.setVisibility(View.INVISIBLE);
                prog.setVisibility(View.VISIBLE);

                new NetCheck().execute();
            }
        });
        listViewfc.setOnRefreshListener(new IonRefreshListener() {
            @Override
            public void onRefreshUp() {


                new NetCheck().execute();

                listViewfc.onRefreshUpComplete();
            }

            @Override
            public void onRefeshDown() {
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

    private class Processgetfriendlist extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.getfriendlist(idno);
        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                JSONArray feedArray = json.getJSONArray("friend");

                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);

                    FeedItem item = new FeedItem();
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("name"));
                    item.setUserId(feedObj.getString("userid"));
                    item.setProfilePic(feedObj.getString("profilePic"));

                    feedItemsfc.add(item);
                }
                listViewfc.setVisibility(View.VISIBLE);
                // notify data changes to list adapater
                listAdapterfc.notifyDataSetChanged();
                if (0 == feedItemsfc.size()) {
                    refresh.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.INVISIBLE);

                } else {
                    refresh.setVisibility(View.GONE);
                    prog.setVisibility(View.GONE);
                }

                listViewfc.onRefreshUpComplete();
                listViewfc.onRefreshDownComplete(null);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        if (0 == feedItemsfc.size()) {
                            refresh.setVisibility(View.VISIBLE);
                            prog.setVisibility(View.INVISIBLE);

                        } else {
                            refresh.setVisibility(View.GONE);
                            prog.setVisibility(View.GONE);
                        }

                        listViewfc.onRefreshUpComplete();
                        listViewfc.onRefreshDownComplete(null);
                        Toast.makeText(send_to_fchoose.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
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

                new Processgetfriendlist().execute();
            } else {
                Toast.makeText(send_to_fchoose.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                if (0 == feedItemsfc.size()) {
                    refresh.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.INVISIBLE);

                } else {
                    refresh.setVisibility(View.GONE);
                    prog.setVisibility(View.GONE);
                }

                listViewfc.onRefreshUpComplete();
                listViewfc.onRefreshDownComplete(null);
            }
        }
    }
}
