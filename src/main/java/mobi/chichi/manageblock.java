package mobi.chichi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
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

import mobi.chichi.listviewfeed.adapter.FeedListAdapterb;
import mobi.chichi.listviewfeed.data.FeedItem;


public class manageblock extends Activity {

    ProgressDialog dialog = null;
    ProgressBar prog;
    ImageButton refresh;
    private ListView listViewb;
    private FeedListAdapterb listAdapterb;
    private List<FeedItem> feedItemsb;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manageblock);
        prog = (ProgressBar) findViewById(R.id.progressBar1);
        //list
        listViewb = (ListView) findViewById(R.id.list);
        listViewb.setVisibility(View.INVISIBLE);
        feedItemsb = new ArrayList<>();
        listAdapterb = new FeedListAdapterb(this, feedItemsb);
        listViewb.setAdapter(listAdapterb);
        new NetCheck().execute();

        //list
        //refresh
        refresh = (ImageButton) findViewById(R.id.refresh);

        refresh.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                refresh.setVisibility(View.INVISIBLE);
                prog.setVisibility(View.VISIBLE);

                new NetCheck().execute();
            }
        });
        //back
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

    private class Processgetblocklist extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.getblocklist(idno);
        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                JSONArray feedArray = json.getJSONArray("block");
                listViewb.setVisibility(View.INVISIBLE);
                feedItemsb.clear();
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);

                    FeedItem item = new FeedItem();
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("name"));
                    item.setUserId(feedObj.getString("userid"));
                    item.setProfilePic(feedObj.getString("profilePic"));
                    item.setoprog("2");
                    feedItemsb.add(item);
                }
                listViewb.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.VISIBLE);
                prog.setVisibility(View.INVISIBLE);
                // notify data changes to list adapater
                listAdapterb.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        refresh.setVisibility(View.VISIBLE);
                        prog.setVisibility(View.INVISIBLE);
                        Toast.makeText(manageblock.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
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

                new Processgetblocklist().execute();
            } else {
                refresh.setVisibility(View.VISIBLE);
                prog.setVisibility(View.INVISIBLE);
                Toast.makeText(manageblock.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
