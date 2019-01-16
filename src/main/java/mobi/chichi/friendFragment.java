package mobi.chichi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import mobi.chichi.listviewfeed.adapter.FeedListAdapterm;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.pulltoupdatelibrary.IonRefreshListener;
import mobi.chichi.pulltoupdatelibrary.PullToUpdateListView;


public class friendFragment extends Fragment {

    private PullToUpdateListView listViewm;
    private FeedListAdapterm listAdapterm;
    private List<FeedItem> feedItemsm;
    private ProgressBar prog;
    private ImageButton refresh;
    private DatabaseHandler db1;
    private int n;
    private String pid = "0";
    private int pidint = 0;
    private Button btnLoadMore;

    public friendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.managefriend, container, false);
        db1 = new DatabaseHandler(getActivity());

        btnLoadMore = new Button(getActivity());
        //list
        listViewm = (PullToUpdateListView) rootView.findViewById(R.id.list);
        listViewm.setVisibility(View.INVISIBLE);
        feedItemsm = new ArrayList<>();
        listAdapterm = new FeedListAdapterm(getActivity(), feedItemsm);
        listViewm.setAdapter(listAdapterm);
        n = 1;
        new NetCheck().execute();
        listViewm.setPullMode(PullToUpdateListView.MODE.UP_AND_DOWN);
        listViewm.setAutoLoad(true, 8);
        listViewm.setPullMessageColor(R.color.textColorPrimary);
        listViewm.setLoadingMessage(getResources().getString(R.string.loading));
        listViewm.setPullRotateImage(getResources().getDrawable(R.drawable.refresh_icon));
        prog = (ProgressBar) rootView.findViewById(R.id.progressBar1);

        refresh = (ImageButton) rootView.findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                refresh.setVisibility(View.INVISIBLE);
                prog.setVisibility(View.VISIBLE);
                n = 1;
                new NetCheck().execute();
            }
        });
        listViewm.setOnRefreshListener(new IonRefreshListener() {
            @Override
            public void onRefreshUp() {
                n = 1;
                new NetCheck().execute();
            }

            @Override
            public void onRefeshDown() {
                n = 2;
                new NetCheck().execute();
            }

        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 12) {

            getActivity();
            if (resultCode == Activity.RESULT_OK) {

                String isfriend = data.getStringExtra("isfriend");
                String pos1 = data.getStringExtra("pos");
                int pos = Integer.parseInt(pos1);
                if ("1".equals(isfriend)) {
                    feedItemsm.remove(pos);
                    listAdapterm.notifyDataSetChanged();
                }

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

    private class Processgetfriendlist extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.getfriendlist(idno);
        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.isNull("friend")) {
                    n = 1;
                    new NetCheck().execute();
                } else {
                    JSONArray feedArray = json.getJSONArray("friend");
                    listViewm.setVisibility(View.INVISIBLE);
                    feedItemsm.clear();
                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        listViewm.setVisibility(View.VISIBLE);
                        FeedItem item = new FeedItem();
                        item.setId(feedObj.getInt("id"));
                        item.setName(feedObj.getString("name"));
                        item.setUserId(feedObj.getString("userid"));
                        item.setProfilePic(feedObj.getString("profilePic"));
                        item.setoprog("2");
                        pid = String.valueOf(feedObj.getInt("id"));
                        pidint = pidint + 1;
                        feedItemsm.add(item);
                    }
                    if (0 == feedItemsm.size()) {
                        refresh.setVisibility(View.VISIBLE);
                        prog.setVisibility(View.INVISIBLE);

                    } else {
                        refresh.setVisibility(View.GONE);
                        prog.setVisibility(View.GONE);
                    }
                    if (n == 1) {

                        listViewm.onRefreshUpComplete();
                    } else if (n == 2) {
                        btnLoadMore.setVisibility(View.VISIBLE);
                        listViewm.onRefreshDownComplete(null);
                    }

                    listViewm.setVisibility(View.VISIBLE);
                    btnLoadMore.setText("Load More");
                    btnLoadMore.setBackgroundResource(R.drawable.button_white);
                    // Adding Load More button to lisview at bottom
                    listViewm.addFooterView(btnLoadMore);
                    // notify data changes to list adapater
                    listAdapterm.notifyDataSetChanged();
                    // Getting adapter
                    listAdapterm = new FeedListAdapterm(getActivity(), feedItemsm);
                    listViewm.setAdapter(listAdapterm);

                    if (pidint >= 10) {
                        btnLoadMore.setVisibility(View.VISIBLE);
                        pidint = 0;
                    } else {
                        btnLoadMore.setVisibility(View.GONE);
                        pidint = 0;
                    }
                    btnLoadMore.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View arg0) {
                            listViewm.setVisibility(View.VISIBLE);
                            btnLoadMore.setVisibility(View.INVISIBLE);
                            n = 2;
                            new NetCheck().execute();
                        }
                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {

                    public void run() {
                        if (0 == feedItemsm.size()) {
                            refresh.setVisibility(View.VISIBLE);
                            prog.setVisibility(View.INVISIBLE);

                        } else {
                            refresh.setVisibility(View.GONE);
                            prog.setVisibility(View.GONE);
                        }
                        if (n == 1) {

                            listViewm.onRefreshUpComplete();
                        } else if (n == 2) {
                            btnLoadMore.setVisibility(View.VISIBLE);
                            listViewm.onRefreshDownComplete(null);
                        }
                        Toast.makeText(getActivity(), R.string.errorconection, Toast.LENGTH_SHORT).show();
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

            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
                if (n == 1) {

                    new Processgetfriendlist().execute();
                } else if (n == 2) {
                    new Processgetfriendlistloadmore().execute();
                }
            } else {
                if (0 == feedItemsm.size()) {
                    refresh.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.INVISIBLE);

                } else {
                    refresh.setVisibility(View.GONE);
                    prog.setVisibility(View.GONE);
                }
                if (n == 1) {

                    listViewm.onRefreshUpComplete();
                } else if (n == 2) {
                    btnLoadMore.setVisibility(View.VISIBLE);
                    listViewm.onRefreshDownComplete(null);
                }


                Toast.makeText(getActivity(), R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Processgetfriendlistloadmore extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.getfriendlistloadmore(idno, pid);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.isNull("friend")) {
                    listViewm.setVisibility(View.VISIBLE);
                    btnLoadMore.setVisibility(View.INVISIBLE);
                    n = 2;
                    new NetCheck().execute();
                } else {
                    JSONArray feedArray = json.getJSONArray("friend");

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
                        item.setoprog("2");
                        item.setlprog("2");
                        item.setfragment("home");
                        pid = String.valueOf(feedObj.getInt("id"));
                        pidint = pidint + 1;
                        feedItemsm.add(item);
                    }
                    listViewm.setVisibility(View.VISIBLE);
                    if (0 == feedItemsm.size()) {
                        refresh.setVisibility(View.VISIBLE);
                        prog.setVisibility(View.INVISIBLE);

                    } else {
                        refresh.setVisibility(View.GONE);
                        prog.setVisibility(View.GONE);
                    }
                    if (n == 1) {

                        listViewm.onRefreshUpComplete();
                    } else if (n == 2) {
                        btnLoadMore.setVisibility(View.VISIBLE);
                        listViewm.onRefreshDownComplete(null);
                    }
                    // notify data changes to list adapater
                    listAdapterm.notifyDataSetChanged();
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

                getActivity().runOnUiThread(new Runnable() {

                    public void run() {
                        if (0 == feedItemsm.size()) {
                            refresh.setVisibility(View.VISIBLE);
                            prog.setVisibility(View.INVISIBLE);

                        } else {
                            refresh.setVisibility(View.GONE);
                            prog.setVisibility(View.GONE);
                        }
                        if (n == 1) {

                            listViewm.onRefreshUpComplete();
                        } else if (n == 2) {
                            btnLoadMore.setVisibility(View.VISIBLE);
                            listViewm.onRefreshDownComplete(null);
                        }
                        Toast.makeText(getActivity(), R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }
}
