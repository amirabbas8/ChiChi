package mobi.chichi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobi.chichi.listviewfeed.adapter.FeedListAdaptero;
import mobi.chichi.listviewfeed.app.AppController;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.pulltoupdatelibrary.IonRefreshListener;
import mobi.chichi.pulltoupdatelibrary.PullToUpdateListView;


public class otherprofile extends Activity {

    private static String KEY_SUCCESS = "success";
    String userid;
    String username;
    String userprofilepic;
    TextView myname;
    TextView mybio;
    ImageButton addordel;
    String isfriend = "0";
    String pos;
    ProgressBar prog;
    ImageButton refresh;
    Button btnLoadMore;
    String n = "1";
    String pid = "0";
    int pidint = 0;
    com.android.volley.toolbox.ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    Dialog report_inappropriate_dialog;
    String reportcomment;
    EditText commenttext;
    private PullToUpdateListView listView;
    private FeedListAdaptero listAdapter;
    private List<FeedItem> feedItems;
    private NetworkImageView profilePic;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otprofile);
        btnLoadMore = new Button(otherprofile.this);
        Bundle bundle = getIntent().getExtras();
        userid = bundle.getString("userid");
        username = bundle.getString("username");
        pos = bundle.getString("pos");
        userprofilepic = "http://chichi.mobi/profileimages/" + bundle.getString("userprofilepic");
        // creating connection detector class instance
        prog = (ProgressBar) findViewById(R.id.progressBar1);

        myname = (TextView) findViewById(R.id.myname);
        mybio = (TextView) findViewById(R.id.mybio);

        profilePic = (NetworkImageView) findViewById(R.id.profileimage1);

        addordel = (ImageButton) findViewById(R.id.addordel);
        String fontPath = "fonts/BYekan.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        myname.setTypeface(tf);
        mybio.setTypeface(tf);

        myname.setText(username);
        profilePic.setImageUrl("http://chichi.mobi/profileimages/" + userprofilepic, imageLoader);
        //list
        listView = (PullToUpdateListView) findViewById(R.id.list);
        listView.setVisibility(View.INVISIBLE);
        feedItems = new ArrayList<>();

        listAdapter = new FeedListAdaptero(this, feedItems);
        listView.setAdapter(listAdapter);
        n = "1";
        new NetCheck().execute();
        listView.setPullMode(PullToUpdateListView.MODE.UP_AND_DOWN);
        listView.setAutoLoad(true, 8);
        listView.setPullMessageColor(R.color.textColorPrimary);
        listView.setLoadingMessage(getResources().getString(R.string.loading));
        listView.setPullRotateImage(getResources().getDrawable(R.drawable.refresh_icon));

        listView.setOnRefreshListener(new IonRefreshListener() {
            @Override
            public void onRefreshUp() {
                n = "5";

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

        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("pos", pos);
                intent.putExtra("isfriend", isfriend);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        //refresh
        refresh = (ImageButton) findViewById(R.id.refresh);

        refresh.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                prog.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.INVISIBLE);

                n = "1";
                new NetCheck().execute();
            }
        });

        addordel.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                addordel.setVisibility(View.INVISIBLE);
                new NetCheck1().execute();

            }
        });
        report_inappropriate_dialog = new Dialog(this);
        report_inappropriate_dialog.setContentView(R.layout.report_inappropriate_dialog);
        report_inappropriate_dialog.setTitle(R.string.report_inappropriate);

        commenttext = (EditText) report_inappropriate_dialog.findViewById(R.id.commenttext);

        ImageButton dialog_ok = (ImageButton) report_inappropriate_dialog.findViewById(R.id.dialog_ok);
        Button cancell = (Button) report_inappropriate_dialog.findViewById(R.id.cancell);
        dialog_ok.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String text = commenttext.getText().toString();
                prog.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.INVISIBLE);
                n = "4";
                new NetCheck().execute();
                reportcomment = text;

            }
        });
        cancell.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                report_inappropriate_dialog.dismiss();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {

            case 11:
                String lastcomment = data.getStringExtra("lastcomment");
                if ("".equals(lastcomment)) {
                    String NComment = data.getStringExtra("NComment");

                    String pos1 = data.getStringExtra("pos");
                    int pos = Integer.parseInt(pos1);

                    feedItems.get(pos).setNComment(NComment);
                    listAdapter.notifyDataSetChanged();
                } else if ("1".equals(data.getStringExtra("c"))) {

                } else if ("2".equals(data.getStringExtra("c"))) {

                    String pos1 = data.getStringExtra("pos");
                    int pos = Integer.parseInt(pos1);
                    String lastcommentname = data.getStringExtra("lastcommentname");
                    String lastcommentuserid = data.getStringExtra("lastcommentuserid");
                    String lastcommentprofilePic = data.getStringExtra("lastcommentprofilePic");
                    String NComment = data.getStringExtra("NComment");
                    feedItems.get(pos).setLastCommentName(lastcommentname);
                    feedItems.get(pos).setLastCommentUserId(lastcommentuserid);
                    feedItems.get(pos).setLastCommentProfilePic(lastcommentprofilePic);
                    feedItems.get(pos).setLastComment(lastcomment);
                    feedItems.get(pos).setNComment(NComment);
                    listAdapter.notifyDataSetChanged();

                }

                break;
            case 17:

                //editneveshtan
                String userid = data.getStringExtra("userid");
                String pos1 = data.getStringExtra("pos");
                String text = data.getStringExtra("text");
                String profilepic = data.getStringExtra("profilepic");
                String name = data.getStringExtra("name");
                String imagename = data.getStringExtra("imagename");
                String Url = data.getStringExtra("Url");
                String TimeStamp = data.getStringExtra("TimeStamp");
                String id = data.getStringExtra("id");
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
                feedItems.get(pos).setUrl(Url);
                feedItems.get(pos).setoprog("2");
                feedItems.get(pos).setlprog("2");
                listAdapter.notifyDataSetChanged();
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
        HashMap<String, String> user;
        user = db1.getUserDetails();
        String idno = user.get("idno");
        if (userid.equals(idno)) {

        } else {
            getMenuInflater().inflate(R.menu.oteprofmenu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_block:

                n = "3";
                new NetCheck().execute();
                prog.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.INVISIBLE);

                break;
            case R.id.action_report_inappropriate:
                report_inappropriate_dialog.show();

                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isfriend", isfriend);
        intent.putExtra("pos", pos);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    private class Processgetotherprofilelist extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.getprofilelist(idno, userid);
        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                JSONArray feedArray = json.getJSONArray("profileposts");
                listView.setVisibility(View.INVISIBLE);
                listView.removeFooterView(btnLoadMore);
                feedItems.clear();
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);

                    FeedItem item = new FeedItem();
                    item.setId(feedObj.getInt("id"));
                    item.setUserId(feedObj.getString("userid"));
                    item.setName(feedObj.getString("name"));
                    String image = feedObj.isNull("image") ? null : "http://chichi.mobi/postsimages/" + feedObj.getString("image");
                    // Image might be null sometimes
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
                    pid = String.valueOf(feedObj.getInt("id"));
                    pidint = pidint + 1;
                    feedItems.add(item);
                }
                if (n.equals("5")) {

                    listView.onRefreshUpComplete();
                } else if (n.equals("2")) {
                    btnLoadMore.setVisibility(View.VISIBLE);
                    listView.onRefreshDownComplete(null);
                }
                listView.setVisibility(View.VISIBLE);
                prog.setVisibility(View.INVISIBLE);
                refresh.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                btnLoadMore.setText(R.string.loadmore);
                btnLoadMore.setBackgroundResource(R.drawable.button_white);
                // Adding Load More button to lisview at bottom
                listView.addFooterView(btnLoadMore);
                // notify data changes to list adapater
                listAdapter.notifyDataSetChanged();
                // Getting adapter
                listAdapter = new FeedListAdaptero(otherprofile.this, feedItems);
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
                        prog.setVisibility(View.VISIBLE);
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
                        prog.setVisibility(View.INVISIBLE);
                        refresh.setVisibility(View.VISIBLE);


                        listView.onRefreshUpComplete();

                        Toast.makeText(otherprofile.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private class Processgetdata extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            return userFunction.getdata(userid);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {

                        JSONObject json_user = json.getJSONObject("user");
                        String KEY_REALNAME = "realname";
                        username = json_user.getString(KEY_REALNAME);
                        String KEY_profileimage = "profileimages";
                        userprofilepic = json_user.getString(KEY_profileimage);

                        String KEY_BIO = "bio";
                        mybio.setText(json_user.getString(KEY_BIO));
                        myname.setText(json_user.getString(KEY_REALNAME));

                        profilePic.setImageUrl("http://chichi.mobi/profileimages/" + userprofilepic, imageLoader);
                        new Processfriendcheck().execute();

                    } else {

                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                        new Processfriendcheck().execute();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Processfriendcheck extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.ffcheck(idno, userid);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);
                    if (userid.equals(idno)) {

                        addordel.setVisibility(View.INVISIBLE);

                        new Processgetotherprofilelist().execute();
                    } else if (Integer.parseInt(res) == 51) {
                        prog.setVisibility(View.INVISIBLE);
                        refresh.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.errorfunblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 52) {
                        prog.setVisibility(View.INVISIBLE);
                        refresh.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.errorblock, Toast.LENGTH_SHORT).show();

                    } else {
                        if (Integer.parseInt(res) == 1) {
                            //دوست نیستید
                            isfriend = "1";
                            addordel.setVisibility(View.VISIBLE);
                            addordel.setBackgroundColor(0xFF00FF00);
                            addordel.setImageResource(R.drawable.ic_add_people_white_20);

                            new Processgetotherprofilelist().execute();
                        } else if (Integer.parseInt(res) == 2) {
                            isfriend = "2";
                            addordel.setVisibility(View.VISIBLE);
                            addordel.setImageResource(R.drawable.ic_dismiss_end_of_play_normal);

                            new Processgetotherprofilelist().execute();
                            //دوست هستید
                        } else {

                            new Processgetotherprofilelist().execute();
                            Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Processaaddordel extends AsyncTask<String, String, JSONObject> {

        String idno, myname, myprofilepic;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
            HashMap<String, String> user;
            user = db1.getUserDetails();
            idno = user.get("idno");
            myname = user.get("realname");
            myprofilepic = user.get("profileimage");
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            return userFunction.addordel(idno, userid, username, userprofilepic, myname, myprofilepic);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    addordel.setVisibility(View.VISIBLE);
                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        isfriend = "2";
                        Toast.makeText(getApplicationContext(), R.string.succes, Toast.LENGTH_SHORT).show();
                        addordel.setVisibility(View.VISIBLE);
                        addordel.setImageResource(R.drawable.ic_dismiss_end_of_play_normal);
                        addordel.setBackgroundResource(R.drawable.home1);
                        addordel.setActivated(true);

                    } else if (Integer.parseInt(res) == 51) {

                        Toast.makeText(getApplicationContext(), R.string.errorfunblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 52) {

                        Toast.makeText(getApplicationContext(), R.string.errorblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 2) {
                        isfriend = "1";
                        addordel.setActivated(true);
                        addordel.setVisibility(View.VISIBLE);
                        addordel.setBackgroundColor(0xFF00FF00);
                        addordel.setImageResource(R.drawable.ic_add_people_white_20);
                        Toast.makeText(getApplicationContext(), R.string.succes, Toast.LENGTH_SHORT).show();

                    } else {
                        addordel.setActivated(true);
                        addordel.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
                switch (n) {
                    case "1":
                        prog.setVisibility(View.VISIBLE);
                        refresh.setVisibility(View.INVISIBLE);
                        new Processgetdata().execute();
                        break;
                    case "2":
                        prog.setVisibility(View.VISIBLE);
                        refresh.setVisibility(View.INVISIBLE);
                        new Processgetotherprofilelistloadmore().execute();
                        break;
                    case "3":
                        prog.setVisibility(View.VISIBLE);
                        refresh.setVisibility(View.INVISIBLE);
                        new Processblock().execute();
                        break;
                    case "4":
                        new Processreport().execute();
                        break;
                    case "5":
                        new Processgetotherprofilelist().execute();
                        break;
                }

            } else {
                prog.setVisibility(View.INVISIBLE);
                refresh.setVisibility(View.VISIBLE);

                if (n.equals("5")) {

                    listView.onRefreshUpComplete();
                } else if (n.equals("2")) {
                    btnLoadMore.setVisibility(View.VISIBLE);
                    listView.onRefreshDownComplete(null);
                }
                Toast.makeText(otherprofile.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class NetCheck1 extends AsyncTask<String, String, Boolean> {

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

                new Processaaddordel().execute();
            } else {
                Toast.makeText(otherprofile.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                addordel.setVisibility(View.VISIBLE);
            }
        }
    }

    private class Processgetotherprofilelistloadmore extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.getprofilelistloadmore(idno, userid, pid);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                JSONArray feedArray = json.getJSONArray("profileposts");

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
                    pid = String.valueOf(feedObj.getInt("id"));
                    pidint = pidint + 1;
                    feedItems.add(item);
                }

                listView.onRefreshDownComplete(null);

                listView.setVisibility(View.VISIBLE);
                if (0 == feedItems.size()) {
                    refresh.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.INVISIBLE);

                } else {
                    refresh.setVisibility(View.GONE);
                    prog.setVisibility(View.GONE);
                }
                listView.onRefreshUpComplete();
                listView.onRefreshDownComplete(null);
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
                        Toast.makeText(otherprofile.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

    private class Processblock extends AsyncTask<String, String, JSONObject> {

        String idno;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseHandler db1 = new DatabaseHandler(otherprofile.this);

            HashMap<String, String> user;
            user = db1.getUserDetails();
            idno = user.get("idno");
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            return userFunction.blockun(idno, userid, username, userprofilepic);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        prog.setVisibility(View.INVISIBLE);
                        refresh.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                        feedItems.clear();
                        Toast.makeText(otherprofile.this, R.string.blocked, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 2) {
                        Toast.makeText(otherprofile.this, R.string.unblocked, Toast.LENGTH_SHORT).show();
                        new Processgetotherprofilelist().execute();
                    } else {
                        prog.setVisibility(View.INVISIBLE);
                        refresh.setVisibility(View.VISIBLE);
                        Toast.makeText(otherprofile.this, R.string.errorproblem, Toast.LENGTH_SHORT).show();

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Processreport extends AsyncTask<String, String, JSONObject> {

        String id0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseHandler db1 = new DatabaseHandler(otherprofile.this);
            HashMap<String, String> user;
            user = db1.getUserDetails();
            id0 = user.get("idno");

        }


        @Override
        protected JSONObject doInBackground(String... args) {
            UserFunctions userFunction = new UserFunctions();

            return userFunction.report(userid, id0, "user", reportcomment);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        Toast.makeText(otherprofile.this, R.string.report_inappropriate_saved, Toast.LENGTH_SHORT).show();
                        report_inappropriate_dialog.dismiss();
                        prog.setVisibility(View.INVISIBLE);
                        refresh.setVisibility(View.VISIBLE);

                    } else if (Integer.parseInt(res) == 2) {
                        Toast.makeText(otherprofile.this, R.string.report_inappropriate_errorsaved, Toast.LENGTH_SHORT).show();
                        report_inappropriate_dialog.dismiss();
                        prog.setVisibility(View.INVISIBLE);
                        refresh.setVisibility(View.VISIBLE);

                    } else {

                        Toast.makeText(otherprofile.this, R.string.errorproblem, Toast.LENGTH_SHORT).show();
                        prog.setVisibility(View.INVISIBLE);
                        refresh.setVisibility(View.VISIBLE);
                    }
                } else {
                    prog.setVisibility(View.INVISIBLE);
                    refresh.setVisibility(View.VISIBLE);
                    Toast.makeText(otherprofile.this, R.string.errorproblem, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
