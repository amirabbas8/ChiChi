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

import mobi.chichi.listviewfeed.adapter.FeedListAdaptern;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.pulltoupdatelibrary.IonRefreshListener;
import mobi.chichi.pulltoupdatelibrary.PullToUpdateListView;


public class notificationFragment extends Fragment {

    private PullToUpdateListView listViewn;
    private FeedListAdaptern listAdaptern;
    private List<FeedItem> feedItemsn;
    private ProgressBar prog;
    private ImageButton refresh;
    private DatabaseHandler db1;
    private String n = "1";

    public notificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification, container, false);
        db1 = new DatabaseHandler(getActivity());

        listViewn = (PullToUpdateListView) rootView.findViewById(R.id.list);
        listViewn.setVisibility(View.INVISIBLE);
        feedItemsn = new ArrayList<>();
        listAdaptern = new FeedListAdaptern(getActivity(), feedItemsn);
        listViewn.setAdapter(listAdaptern);
        n = "1";
        new notificationNetCheck().execute();
        listViewn.setPullMode(PullToUpdateListView.MODE.UP_ONLY);
        listViewn.setAutoLoad(true, 8);
        listViewn.setPullMessageColor(R.color.textColorPrimary);
        listViewn.setLoadingMessage(getResources().getString(R.string.loading));
        listViewn.setPullRotateImage(getResources().getDrawable(R.drawable.refresh_icon));
        prog = (ProgressBar) rootView.findViewById(R.id.progressBar1);

        refresh = (ImageButton) rootView.findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                refresh.setVisibility(View.INVISIBLE);
                prog.setVisibility(View.VISIBLE);
                n = "1";
                new notificationNetCheck().execute();
            }
        });
        listViewn.setOnRefreshListener(new IonRefreshListener() {
            @Override
            public void onRefreshUp() {
                n = "1";

                new notificationNetCheck().execute();
            }

            @Override
            public void onRefeshDown() {

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

    private class Processgetnotificationlist extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.getnotificationlist(idno);
        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.isNull("notificationposts")) {


                    n = "1";
                    new notificationNetCheck().execute();
                } else {
                    JSONArray feedArray = json.getJSONArray("notificationposts");
                    listViewn.setVisibility(View.INVISIBLE);
                    feedItemsn.clear();
                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);

                        FeedItem item = new FeedItem();
                        item.setId(feedObj.getInt("id"));
                        item.setName(feedObj.getString("name"));
                        item.setUserId(feedObj.getString("userid"));
                        item.setPostID(feedObj.getString("postid"));
                        item.setStatus(feedObj.getString("status"));
                        item.setType(feedObj.getString("type"));
                        item.setProfilePic(feedObj.getString("profilePic"));
                        item.setoprog("2");
                        feedItemsn.add(item);
                    }
                    listViewn.setVisibility(View.VISIBLE);
                    // notify data changes to list adapater
                    listAdaptern.notifyDataSetChanged();
                    if (0 == feedItemsn.size()) {
                        refresh.setVisibility(View.VISIBLE);
                        prog.setVisibility(View.INVISIBLE);

                    } else {
                        refresh.setVisibility(View.GONE);
                        prog.setVisibility(View.GONE);
                    }
                    listViewn.onRefreshUpComplete();

                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {

                    public void run() {
                        Toast.makeText(getActivity(), R.string.errorconection, Toast.LENGTH_SHORT).show();
                        if (0 == feedItemsn.size()) {
                            refresh.setVisibility(View.VISIBLE);
                            prog.setVisibility(View.INVISIBLE);

                        } else {
                            refresh.setVisibility(View.GONE);
                            prog.setVisibility(View.GONE);
                        }

                        listViewn.onRefreshUpComplete();
                    }
                });

            }
        }
    }

    private class notificationNetCheck extends AsyncTask<String, String, Boolean> {

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

                    new Processgetnotificationlist().execute();
                }


            } else {


                if (0 == feedItemsn.size()) {
                    refresh.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.INVISIBLE);

                } else {
                    refresh.setVisibility(View.GONE);
                    prog.setVisibility(View.GONE);
                }

                listViewn.onRefreshUpComplete();
                Toast.makeText(getActivity(), R.string.errorconection,
                        Toast.LENGTH_SHORT).show();

            }
        }
    }


}
