package mobi.chichi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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

import mobi.chichi.listviewfeed.adapter.FeedListAdapteradvertise;
import mobi.chichi.listviewfeed.data.FeedItemadvertise;
import mobi.chichi.pulltoupdatelibrary.IonRefreshListener;
import mobi.chichi.pulltoupdatelibrary.PullToUpdateListView;


public class advertise extends Activity {

    private PullToUpdateListView listView;
    private FeedListAdapteradvertise listAdapter;
    private List<FeedItemadvertise> feedItems;
    private ProgressBar prog;
    private ImageButton refresh;
    private DatabaseHandler db1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertise);
        db1 = new DatabaseHandler(getApplicationContext());
        prog = (ProgressBar) findViewById(R.id.progressBar1);

        //list

        listView = (PullToUpdateListView) findViewById(R.id.list);
        listView.setVisibility(View.INVISIBLE);
        feedItems = new ArrayList<>();
        listAdapter = new FeedListAdapteradvertise(this, feedItems);
        listView.setAdapter(listAdapter);
        new NetCheck().execute();
        listView.setPullMode(PullToUpdateListView.MODE.UP_ONLY);
        listView.setAutoLoad(true, 8);
        listView.setPullMessageColor(R.color.textColorPrimary);
        listView.setLoadingMessage(getResources().getString(R.string.loading));
        listView.setPullRotateImage(getResources().getDrawable(R.drawable.refresh_icon));
        prog = (ProgressBar) findViewById(R.id.progressBar1);

        refresh = (ImageButton) findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                refresh.setVisibility(View.INVISIBLE);
                prog.setVisibility(View.VISIBLE);

                new NetCheck().execute();
            }
        });
        listView.setOnRefreshListener(new IonRefreshListener() {
            @Override
            public void onRefreshUp() {


                new NetCheck().execute();


            }

            @Override
            public void onRefeshDown() {

            }

        });
        //neveshtan
        ImageButton plus = (ImageButton) findViewById(R.id.plus);

        plus.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(advertise.this, advertiseplus.class);
                startActivityForResult(intent, 17);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (0 == feedItems.size()) {
                refresh.setVisibility(View.VISIBLE);
                prog.setVisibility(View.INVISIBLE);

            } else {
                refresh.setVisibility(View.GONE);
                prog.setVisibility(View.GONE);
            }
            if (requestCode == 17) {
                //comment
                String userid = data.getStringExtra("userid");
                String link = data.getStringExtra("link");
                String name = data.getStringExtra("name");
                String imagename = data.getStringExtra("imagename");
                String status = data.getStringExtra("status");
                String id = data.getStringExtra("id");
                String startdate = data.getStringExtra("startdate");
                String enddate = data.getStringExtra("enddate");
                FeedItemadvertise item = new FeedItemadvertise();
                item.setId(Integer.parseInt(id));
                item.setName(name);
                item.setUserId(userid);
                item.setStatus(status);
                item.setImage(imagename);
                item.setstartdate(startdate);
                item.setenddate(enddate);
                item.setoprog("2");
                item.setlink(link);
                feedItems.add(0, item);
                listAdapter.notifyDataSetChanged();
            } else if (requestCode == 11) {
                //comment
                String userid = data.getStringExtra("userid");
                String link = data.getStringExtra("link");
                String name = data.getStringExtra("name");
                String imagename = data.getStringExtra("imagename");
                String status = data.getStringExtra("status");
                String id = data.getStringExtra("id");
                String startdate = data.getStringExtra("startdate");
                String enddate = data.getStringExtra("enddate");
                String pos1 = data.getStringExtra("pos");
                int pos = Integer.parseInt(pos1);

                feedItems.get(pos).setId(Integer.parseInt(id));
                feedItems.get(pos).setName(name);
                feedItems.get(pos).setUserId(userid);
                feedItems.get(pos).setStatus(status);
                feedItems.get(pos).setImage(imagename);
                feedItems.get(pos).setstartdate(startdate);
                feedItems.get(pos).setenddate(enddate);
                feedItems.get(pos).setoprog("2");
                feedItems.get(pos).setlink(link);
                listAdapter.notifyDataSetChanged();

            }
        }
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

                new Processgetadvertiselist().execute();

            } else {
                if (0 == feedItems.size()) {
                    refresh.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.INVISIBLE);
                    listView.onRefreshUpComplete();
                } else {
                    refresh.setVisibility(View.GONE);
                    prog.setVisibility(View.GONE);
                }
                Toast.makeText(advertise.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Processgetadvertiselist extends AsyncTask<String, String, JSONObject> {

        String idno;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            HashMap<String, String> user;
            user = db1.getUserDetails();
            idno = user.get("idno");

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.getadvertiselist(idno);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.isNull("advertise")) {


                    new NetCheck().execute();
                } else {
                    JSONArray feedArray = json.getJSONArray("advertise");
                    listView.setVisibility(View.INVISIBLE);
                    feedItems.clear();
                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);

                        FeedItemadvertise item = new FeedItemadvertise();
                        item.setId(feedObj.getInt("id"));
                        item.setName(feedObj.getString("name"));
                        item.setUserId(feedObj.getString("userid"));
                        item.setStatus(feedObj.getString("status"));
                        item.setImage(feedObj.getString("image"));
                        item.setstartdate(feedObj.getString("startdate"));
                        item.setenddate(feedObj.getString("enddate"));
                        item.setlink(feedObj.getString("link"));
                        item.setoprog("2");

                        feedItems.add(item);
                    }
                    listView.onRefreshUpComplete();
                    if (0 == feedItems.size()) {
                        refresh.setVisibility(View.VISIBLE);
                        prog.setVisibility(View.INVISIBLE);

                    } else {
                        refresh.setVisibility(View.GONE);
                        prog.setVisibility(View.GONE);
                    }
                    listView.setVisibility(View.VISIBLE);
                    listAdapter.notifyDataSetChanged();

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
                        Toast.makeText(advertise.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

}
