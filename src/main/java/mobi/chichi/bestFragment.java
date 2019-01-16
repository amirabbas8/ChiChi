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

import mobi.chichi.listviewfeed.adapter.FeedListAdapter;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.pulltoupdatelibrary.IonRefreshListener;
import mobi.chichi.pulltoupdatelibrary.PullToUpdateListView;

import static mobi.chichi.R.color;
import static mobi.chichi.R.drawable;
import static mobi.chichi.R.id;
import static mobi.chichi.R.layout;
import static mobi.chichi.R.string;


public class bestFragment extends Fragment {
    private PullToUpdateListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;

    private DatabaseHandler db1;
    private ProgressBar prog;

    private ImageButton refresh;

    public bestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(layout.best, container, false);
        db1 = new DatabaseHandler(getActivity());
        listView = (PullToUpdateListView) rootView.findViewById(id.list);
        listView.setVisibility(View.INVISIBLE);
        feedItems = new ArrayList<>();
        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
        new bestNetCheck().execute();
        listView.setPullMode(PullToUpdateListView.MODE.UP_ONLY);
        listView.setAutoLoad(true, 8);
        listView.setPullMessageColor(color.textColorPrimary);
        listView.setLoadingMessage(getResources().getString(string.loading));
        listView.setPullRotateImage(getResources().getDrawable(drawable.refresh_icon));
        prog = (ProgressBar) rootView.findViewById(R.id.progressBar1);

        refresh = (ImageButton) rootView.findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                refresh.setVisibility(View.INVISIBLE);
                prog.setVisibility(View.VISIBLE);

                new bestNetCheck().execute();
            }
        });
        listView.setOnRefreshListener(new IonRefreshListener() {
            @Override
            public void onRefreshUp() {

                new bestNetCheck().execute();


            }

            @Override
            public void onRefeshDown() {

            }

        });
        return rootView;
    }

    public void bestfragmentResult(String lastcomment, String pos1, String lastcommentname, String lastcommentuserid, String lastcommentprofilePic, String NComment, String c) {

        if ("".equals(lastcomment)) {

            int pos = Integer.parseInt(pos1);

            feedItems.get(pos).setNComment(NComment);
            listAdapter.notifyDataSetChanged();
        } else if ("1".equals(c)) {

        } else if ("2".equals(c)) {

            int pos = Integer.parseInt(pos1);

            feedItems.get(pos).setLastCommentName(lastcommentname);
            feedItems.get(pos).setLastCommentUserId(lastcommentuserid);
            feedItems.get(pos).setLastCommentProfilePic(lastcommentprofilePic);
            feedItems.get(pos).setLastComment(lastcomment);
            feedItems.get(pos).setNComment(NComment);
            listAdapter.notifyDataSetChanged();

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

    public void bestneveshtanedit(String id, String userid, String text, String url, String profilepic, String name, String imagename, String TimeStamp, String pos1) {
        int pos = Integer.parseInt(pos1);
        feedItems.get(pos).setId(Integer.parseInt(id));
        feedItems.get(pos).setName(name);
        feedItems.get(pos).setUserId(userid);
        // Image might be null sometimes
        String image = "http://chichi.mobi/postsimages/" + imagename;
        if (("http://chichi.mobi/postsimages/").equals(image)) {
            image = null;
        }
        feedItems.get(pos).setImage(image);
        feedItems.get(pos).setStatus(text);
        feedItems.get(pos).setProfilePic(profilepic);
        feedItems.get(pos).setTimeStamp(TimeStamp);
        feedItems.get(pos).setUrl(url);
        feedItems.get(pos).setoprog("2");
        feedItems.get(pos).setlprog("2");
        feedItems.get(pos).setfragment("best");
        listAdapter.notifyDataSetChanged();
    }

    private class Processgetbestactivitylist extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.getbestlist(idno);
        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.isNull("bestposts")) {


                    new bestNetCheck().execute();
                } else {
                    JSONArray feedArray = json.getJSONArray("bestposts");
                    listView.setVisibility(View.INVISIBLE);
                    feedItems.clear();
                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        listView.setVisibility(View.VISIBLE);
                        FeedItem item = new FeedItem();
                        item.setId(feedObj.getInt("id"));
                        item.setoprog("2");
                        item.setlprog("2");
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
                        item.setfragment("best");
                        feedItems.add(item);
                    }
                    if (0 == feedItems.size()) {
                        refresh.setVisibility(View.VISIBLE);
                        prog.setVisibility(View.INVISIBLE);

                    } else {
                        refresh.setVisibility(View.GONE);
                        prog.setVisibility(View.GONE);
                    }

                    listView.onRefreshUpComplete();
                    listView.setVisibility(View.VISIBLE);
                    listAdapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {

                    public void run() {

                        Toast.makeText(getActivity(), string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

    private class bestNetCheck extends AsyncTask<String, String, Boolean> {

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


                new Processgetbestactivitylist().execute();


            } else {

                if (0 == feedItems.size()) {
                    refresh.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.INVISIBLE);
                } else {
                    refresh.setVisibility(View.GONE);
                    prog.setVisibility(View.GONE);
                }
                listView.onRefreshUpComplete();
                Toast.makeText(getActivity(), string.errorconection,
                        Toast.LENGTH_SHORT).show();

            }
        }
    }
}
