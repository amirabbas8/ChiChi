package mobi.chichi;

import android.content.Context;
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
import android.widget.EditText;
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
import java.util.List;

import mobi.chichi.listviewfeed.adapter.FeedListAdapter;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.pulltoupdatelibrary.IonRefreshListener;
import mobi.chichi.pulltoupdatelibrary.PullToUpdateListView;


public class searchFragment extends Fragment {
    private PullToUpdateListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;

    private Button btnLoadMore;
    private String n = "1";
    private String pid = "0";
    private int pidint = 0;
    private ProgressBar hprog;
    private ImageButton search;
    private EditText inputsearch;
    private String searchtext;

    public searchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search, container, false);

        inputsearch = (EditText) rootView.findViewById(R.id.inputsearch);

        btnLoadMore = new Button(getActivity());
        hprog = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        //list
        listView = (PullToUpdateListView) rootView.findViewById(R.id.list);
        listView.setVisibility(View.INVISIBLE);
        feedItems = new ArrayList<>();
        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
        //search
        search = (ImageButton) rootView.findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (inputsearch.getText().toString().length() > 0) {
                    n = "1";

                    new NetCheck().execute();
                }

            }
        });
        listView.setPullMode(PullToUpdateListView.MODE.UP_AND_DOWN);
        listView.setAutoLoad(true, 8);
        listView.setPullMessageColor(R.color.textColorPrimary);
        listView.setLoadingMessage(getResources().getString(R.string.loading));
        listView.setPullRotateImage(getResources().getDrawable(R.drawable.refresh_icon));

        listView.setOnRefreshListener(new IonRefreshListener() {
            @Override
            public void onRefreshUp() {
                n = "1";

                new NetCheck().execute();
            }

            @Override
            public void onRefeshDown() {
                if (9 < feedItems.size()) {
                    listView.setVisibility(View.VISIBLE);
                    btnLoadMore.setVisibility(View.INVISIBLE);
                    n = "2";
                    new NetCheck().execute();

                }

            }

        });
        return rootView;
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
                if (n.equals("1")) {

                    new Processgetsearchlist().execute();
                } else if (n.equals("2")) {
                    new Processgetsearchlistloadmore().execute();
                }
            } else {
                hprog.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Processgetsearchlist extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchtext = inputsearch.getText().toString();
            searchtext = searchtext.replaceAll(" ", "|");

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.getsearchlist(searchtext);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.isNull("searchposts")) {
                    n = "1";

                    hprog.setVisibility(View.VISIBLE);
                    search.setVisibility(View.INVISIBLE);

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
                if (n.equals("1")) {

                    listView.onRefreshUpComplete();
                } else if (n.equals("2")) {
                    btnLoadMore.setVisibility(View.VISIBLE);
                    listView.onRefreshDownComplete(null);
                }
                hprog.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                btnLoadMore.setText(R.string.loadmore);
                btnLoadMore.setBackgroundResource(R.drawable.button_white);
                // Adding Load More button to lisview at bottom
                listView.addFooterView(btnLoadMore);
                // notify data changes to list adapater
                listAdapter.notifyDataSetChanged();
                // Getting adapter
                listAdapter = new FeedListAdapter(getActivity(), feedItems);
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
                        search.setVisibility(View.INVISIBLE);
                        n = "2";
                        new NetCheck().execute();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {

                    public void run() {
                        hprog.setVisibility(View.INVISIBLE);
                        search.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

    private class Processgetsearchlistloadmore extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            searchtext = inputsearch.getText().toString();
            searchtext = searchtext.replaceAll(" ", "|");

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.getsearchlistloadmore(searchtext, pid);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.isNull("searchposts")) {
                    hprog.setVisibility(View.VISIBLE);
                    search.setVisibility(View.INVISIBLE);
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
                    if (n.equals("1")) {

                        listView.onRefreshUpComplete();
                    } else if (n.equals("2")) {
                        btnLoadMore.setVisibility(View.VISIBLE);
                        listView.onRefreshDownComplete(null);
                    }
                    listView.setVisibility(View.VISIBLE);
                    hprog.setVisibility(View.INVISIBLE);
                    search.setVisibility(View.VISIBLE);
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

                getActivity().runOnUiThread(new Runnable() {

                    public void run() {
                        hprog.setVisibility(View.INVISIBLE);
                        search.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }
}
