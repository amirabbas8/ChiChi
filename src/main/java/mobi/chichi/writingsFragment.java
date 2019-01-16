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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import static mobi.chichi.R.color;
import static mobi.chichi.R.drawable;
import static mobi.chichi.R.id;
import static mobi.chichi.R.layout;
import static mobi.chichi.R.string;

public class writingsFragment extends Fragment {

    ProgressBar hprog;
    ImageButton refresh,top,mylikes;
    private PullToUpdateListView listView;
    private FeedListAdapter_writings listAdapter;
    private List<FeedItem> feedItems;
    private Button btnLoadMore;
    private String n = "1";
    private String pid = "0";
    private int pidint = 0;
    private DatabaseHandler db1;
    Spinner sptags;
    public writingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db1 = new DatabaseHandler(getActivity());
        final View rootView = inflater.inflate(layout.writings, container, false);
        // Inflate the layout for this fragment

        btnLoadMore = new Button(getActivity());
        hprog = (ProgressBar) rootView.findViewById(id.progressBar1);
        sptags = (Spinner) rootView.findViewById(R.id.tags);
        refresh = (ImageButton) rootView.findViewById(id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                refresh.setVisibility(View.INVISIBLE);
                hprog.setVisibility(View.VISIBLE);
                n = "1";
                new NetCheck().execute();
            }
        });
        top = (ImageButton) rootView.findViewById(id.top);

        top.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent upanel = new Intent(getActivity(), writings_best.class);
                upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upanel);
            }
        });
        mylikes = (ImageButton) rootView.findViewById(id.mylikes);

        mylikes.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent upanel = new Intent(getActivity(), mylike_writings.class);
                upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upanel);
            }
        });
        ImageButton  addwriting = (ImageButton) rootView.findViewById(id.addwriting);

        addwriting.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent upanel = new Intent(getActivity(), neveshtan_writings.class);
                upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upanel);
            }
        });
        listView = (PullToUpdateListView) rootView.findViewById(id.list);
        listView.setPullMode(PullToUpdateListView.MODE.UP_AND_DOWN);
        listView.setAutoLoad(true, 8);
        listView.setPullMessageColor(color.textColorPrimary);
        listView.setLoadingMessage(getResources().getString(string.loading));
        listView.setPullRotateImage(getResources().getDrawable(drawable.refresh_icon));

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
                    Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();

                }

            }

        });
        sptags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                n = "1";

                new NetCheck().execute();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        listView.setVisibility(View.INVISIBLE);
        feedItems = new ArrayList<>();
        listAdapter = new FeedListAdapter_writings(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
        n = "1";
        new NetCheck().execute();


        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

                    new Processgetwritingslist().execute();
                } else if (n.equals("2")) {
                    new Processgetwritingslistloadmore().execute();
                }
            } else {
                if (0 == feedItems.size()) {
                    refresh.setVisibility(View.VISIBLE);
                    hprog.setVisibility(View.INVISIBLE);

                } else {
                    refresh.setVisibility(View.GONE);
                    hprog.setVisibility(View.GONE);
                }
                if (n.equals("1")) {

                    listView.onRefreshUpComplete();
                } else if (n.equals("2")) {
                    btnLoadMore.setVisibility(View.VISIBLE);
                    listView.onRefreshDownComplete(null);
                }


            }
        }
    }

    private class Processgetwritingslist extends AsyncTask<String, String, JSONObject> {

        String idno,tags;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            HashMap<String, String> user;
            user = db1.getUserDetails();
            idno = user.get("idno");
            if (0 == sptags.getSelectedItemPosition()) {
                tags = "0";
            } else if (1 == sptags.getSelectedItemPosition()) {
                tags="1";
            }
            else if (2 == sptags.getSelectedItemPosition()) {
                tags="2";
            }
            else if (3 == sptags.getSelectedItemPosition()) {
                tags="3";
            }      else if (4 == sptags.getSelectedItemPosition()) {
                tags="4";
            }      else if (5 == sptags.getSelectedItemPosition()) {
                tags="5";
            }      else if (6 == sptags.getSelectedItemPosition()) {
                tags="6";
            }      else if (7 == sptags.getSelectedItemPosition()) {
                tags="7";
            }      else if (8 == sptags.getSelectedItemPosition()) {
                tags="8";
            }      else if (9 == sptags.getSelectedItemPosition()) {
                tags="9";
            }      else if (10 == sptags.getSelectedItemPosition()) {
                tags="10";
            }      else if (11== sptags.getSelectedItemPosition()) {
                tags="11";
            }      else if (12== sptags.getSelectedItemPosition()) {
                tags="12";
            }      else if (13== sptags.getSelectedItemPosition()) {
                tags="13";
            }      else if (14== sptags.getSelectedItemPosition()) {
                tags="14";
            }      else if (15== sptags.getSelectedItemPosition()) {
                tags="15";
            }      else if (16== sptags.getSelectedItemPosition()) {
                tags="16";
            }

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.getwritingslist(idno,tags);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.isNull("writingsposts")) {
                    n = "1";


                    new NetCheck().execute();
                } else {
                    JSONArray feedArray = json.getJSONArray("writingsposts");
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
                        item.setfragment("writings");
                        pid = String.valueOf(feedObj.getInt("id"));
                        pidint = pidint + 1;
                        feedItems.add(item);
                    }
                    if (0 == feedItems.size()) {
                        refresh.setVisibility(View.VISIBLE);
                        hprog.setVisibility(View.INVISIBLE);

                    } else {
                        refresh.setVisibility(View.GONE);
                        hprog.setVisibility(View.GONE);
                    }
                    if (n.equals("1")) {

                        listView.onRefreshUpComplete();
                    } else if (n.equals("2")) {
                        btnLoadMore.setVisibility(View.VISIBLE);
                        listView.onRefreshDownComplete(null);
                    }

                    listView.setVisibility(View.VISIBLE);
                    btnLoadMore.setText("Load More");
                    btnLoadMore.setBackgroundResource(drawable.button_white);
                    // Adding Load More button to lisview at bottom
                    listView.addFooterView(btnLoadMore);
                    // notify data changes to list adapater
                    listAdapter.notifyDataSetChanged();
                    // Getting adapter
                    listAdapter = new FeedListAdapter_writings(getActivity(), feedItems);
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
                            listView.setVisibility(View.VISIBLE);
                            btnLoadMore.setVisibility(View.INVISIBLE);
                            n = "2";
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
                        if (0 == feedItems.size()) {
                            refresh.setVisibility(View.VISIBLE);
                            hprog.setVisibility(View.INVISIBLE);

                        } else {
                            refresh.setVisibility(View.GONE);
                            hprog.setVisibility(View.GONE);
                        }
                        if (n.equals("1")) {

                            listView.onRefreshUpComplete();
                        } else if (n.equals("2")) {
                            btnLoadMore.setVisibility(View.VISIBLE);
                            listView.onRefreshDownComplete(null);
                        }
                        Toast.makeText(getActivity(), string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

    private class Processgetwritingslistloadmore extends AsyncTask<String, String, JSONObject> {

        String idno,tags;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            HashMap<String, String> user;
            user = db1.getUserDetails();
            idno = user.get("idno");
            tags = sptags.getSelectedItem().toString();
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.getwritingslistloadmore(idno, pid,tags);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.isNull("writingsposts")) {
                    listView.setVisibility(View.VISIBLE);
                    btnLoadMore.setVisibility(View.INVISIBLE);
                    n = "2";
                    new NetCheck().execute();
                } else {
                    JSONArray feedArray = json.getJSONArray("writingsposts");

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
                        item.setfragment("writings");
                        pid = String.valueOf(feedObj.getInt("id"));
                        pidint = pidint + 1;
                        feedItems.add(item);
                    }
                    listView.setVisibility(View.VISIBLE);
                    if (0 == feedItems.size()) {
                        refresh.setVisibility(View.VISIBLE);
                        hprog.setVisibility(View.INVISIBLE);

                    } else {
                        refresh.setVisibility(View.GONE);
                        hprog.setVisibility(View.GONE);
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {

                    public void run() {
                        if (0 == feedItems.size()) {
                            refresh.setVisibility(View.VISIBLE);
                            hprog.setVisibility(View.INVISIBLE);

                        } else {
                            refresh.setVisibility(View.GONE);
                            hprog.setVisibility(View.GONE);
                        }
                        if (n.equals("1")) {

                            listView.onRefreshUpComplete();
                        } else if (n.equals("2")) {
                            btnLoadMore.setVisibility(View.VISIBLE);
                            listView.onRefreshDownComplete(null);
                        }
                        Toast.makeText(getActivity(), string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }
}
