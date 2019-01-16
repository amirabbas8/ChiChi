package mobi.chichi;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
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

import mobi.chichi.listviewfeed.adapter.FeedListAdapterc;
import mobi.chichi.listviewfeed.data.FeedItem;


public class comment extends Activity {

    private static String NComment = "";
    String postid, pos;
    ImageButton refresh;
    EditText inputtext;
    ProgressBar nprog;
    ProgressBar prog;
    Button btnsend;
    String userid;
    String Fragment;
    String n = "1";
    String pid = "0";
    int pidint = 0;
    Button btnLoadMore;
    int c = 1;
    private ListView listViewc;
    private FeedListAdapterc listAdapterc;
    private List<FeedItem> feedItemsc;

    public static String getNComment() {
        return NComment;
    }

    public static void setNComment(String nComment) {
        NComment = nComment;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        btnLoadMore = new Button(comment.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);
        Bundle bundle = getIntent().getExtras();
        postid = bundle.getString("postid");
        pos = bundle.getString("pos");
        setNComment(bundle.getString("NComment"));
        userid = bundle.getString("userid");
        Fragment = bundle.getString("Fragment");
        // creating connection detector class instance

        btnsend = (Button) findViewById(R.id.send);
        nprog = (ProgressBar) findViewById(R.id.progressBar1);
        inputtext = (EditText) findViewById(R.id.editText1);
        prog = (ProgressBar) findViewById(R.id.progressBar2);
        btnsend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (inputtext.getText().toString().length() > 1) {

                    btnsend.setVisibility(View.INVISIBLE);
                    nprog.setVisibility(View.VISIBLE);
                    new NetCheck1().execute();

                } else {
                    Toast.makeText(getApplicationContext(), R.string.errorcomment, Toast.LENGTH_SHORT).show();
                }
            }

        });
        //list
        listViewc = (ListView) findViewById(R.id.list);
        listViewc.setVisibility(View.INVISIBLE);
        feedItemsc = new ArrayList<>();

        listAdapterc = new FeedListAdapterc(this, feedItemsc);
        listViewc.setAdapter(listAdapterc);
        n = "1";
        c = 1;
        new NetCheck().execute();

        //refresh
        refresh = (ImageButton) findViewById(R.id.refresh);

        refresh.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                c = 1;
                n = "1";
                refresh.setVisibility(View.INVISIBLE);
                prog.setVisibility(View.VISIBLE);

                new NetCheck().execute();
            }
        });

        //back
        ImageButton back = (ImageButton) findViewById(R.id.back);

        back.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if ("0".equals(getNComment())) {

                    Intent intent = new Intent();
                    intent.putExtra("pos", pos);
                    intent.putExtra("lastcomment", "");
                    intent.putExtra("NComment", getNComment());
                    intent.putExtra("Fragment", Fragment);
                    setResult(RESULT_OK, intent);
                    finish();

                } else if (0 == feedItemsc.size()) {

                    Intent intent = new Intent();
                    intent.putExtra("pos", pos);
                    intent.putExtra("lastcomment", "");
                    intent.putExtra("NComment", "0");
                    intent.putExtra("Fragment", Fragment);
                    setResult(RESULT_OK, intent);
                    finish();

                } else {
                    if (c == 1) {

                        Intent intent = new Intent();
                        intent.putExtra("c", "1");
                        intent.putExtra("lastcomment", "c=1");
                        intent.putExtra("Fragment", Fragment);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (c == 2) {

                        String lastcomment = feedItemsc.get(0).getStatus();
                        String lastcommentname = feedItemsc.get(0).getName();
                        String lastcommentuserid = feedItemsc.get(0).getUserId();
                        String lastcommentprofilePic = feedItemsc.get(0).getProfilePic();

                        Intent intent = new Intent();
                        intent.putExtra("pos", pos);
                        intent.putExtra("c", "2");
                        intent.putExtra("lastcommentname", lastcommentname);
                        intent.putExtra("lastcommentuserid", lastcommentuserid);
                        intent.putExtra("lastcommentprofilePic", lastcommentprofilePic);
                        intent.putExtra("lastcomment", lastcomment);
                        intent.putExtra("NComment", getNComment());
                        intent.putExtra("Fragment", Fragment);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if ("0".equals(getNComment())) {

            Intent intent = new Intent();
            intent.putExtra("pos", pos);
            intent.putExtra("lastcomment", "");
            intent.putExtra("NComment", getNComment());
            intent.putExtra("Fragment", Fragment);
            setResult(RESULT_OK, intent);
            finish();

        } else {
            if (c == 1) {

                Intent intent = new Intent();
                intent.putExtra("c", "1");
                intent.putExtra("lastcomment", "c=1");
                intent.putExtra("Fragment", Fragment);
                setResult(RESULT_OK, intent);
                finish();
            } else if (c == 2) {

                String lastcomment = feedItemsc.get(0).getStatus();
                String lastcommentname = feedItemsc.get(0).getName();
                String lastcommentuserid = feedItemsc.get(0).getUserId();
                String lastcommentprofilePic = feedItemsc.get(0).getProfilePic();

                Intent intent = new Intent();
                intent.putExtra("pos", pos);
                intent.putExtra("c", "2");
                intent.putExtra("lastcommentname", lastcommentname);
                intent.putExtra("lastcommentuserid", lastcommentuserid);
                intent.putExtra("lastcommentprofilePic", lastcommentprofilePic);
                intent.putExtra("lastcomment", lastcomment);
                intent.putExtra("NComment", getNComment());
                intent.putExtra("Fragment", Fragment);
                setResult(RESULT_OK, intent);
                finish();
            }
        }

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

    private class Processgetcommentlist extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.getcommentlist(idno, postid);
        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                JSONArray feedArray = json.getJSONArray("comment");
                listViewc.setVisibility(View.INVISIBLE);
                feedItemsc.clear();
                for (int i = 0; i < feedArray.length(); i++) {

                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    listViewc.setVisibility(View.VISIBLE);
                    FeedItem item = new FeedItem();
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("name"));
                    item.setUserId(feedObj.getString("userid"));
                    item.setPostID(feedObj.getString("postid"));
                    // Image might be null sometimes
                    String image = feedObj.isNull("image") ? null : feedObj
                            .getString("image");
                    item.setImage(image);
                    item.setStatus(feedObj.getString("comment"));
                    item.setProfilePic(feedObj.getString("profilePic"));
                    item.setTimeStamp(feedObj.getString("timeStamp"));

                    // url might be null sometimes
                    String feedUrl = feedObj.isNull("url") ? null : feedObj
                            .getString("url");
                    item.setUrl(feedUrl);
                    item.setoprog("2");
                    feedItemsc.add(item);
                    pid = String.valueOf(feedObj.getInt("id"));
                    pidint = pidint + 1;

                }
                c = 2;
                prog.setVisibility(View.INVISIBLE);
                refresh.setVisibility(View.VISIBLE);
                listViewc.setVisibility(View.VISIBLE);
                btnLoadMore.setText(R.string.loadmore);
                btnLoadMore.setBackgroundResource(R.drawable.button_white);
                // Adding Load More button to lisview at bottom
                listViewc.addFooterView(btnLoadMore);
                // notify data changes to list adapater
                listAdapterc.notifyDataSetChanged();
                // Getting adapter
                listAdapterc = new FeedListAdapterc(comment.this, feedItemsc);
                listViewc.setAdapter(listAdapterc);

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
                        btnLoadMore.setVisibility(View.INVISIBLE);
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
                        Toast.makeText(comment.this, R.string.loadmore,
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

    private class Processcomwrite extends AsyncTask<String, String, JSONObject> {

        String id, text, pic, name;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            inputtext = (EditText) findViewById(R.id.editText1);
            text = inputtext.getText().toString();
            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

            HashMap<String, String> user;
            user = db1.getUserDetails();
            id = user.get("idno");
            pic = user.get("profileimage");
            name = user.get("realname");
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.comwrite(id, postid, text, pic, name, userid);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                String KEY_SUCCESS = "success";
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);
                    if (Integer.parseInt(res) == 51) {
                        btnsend.setVisibility(View.VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.errorfunblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 52) {
                        btnsend.setVisibility(View.VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.errorblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 1) {

                        JSONObject json_user = json.getJSONObject("post");
                        btnsend.setVisibility(View.VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),
                                R.string.succes, Toast.LENGTH_SHORT).show();
                        listViewc.setVisibility(View.VISIBLE);
                        FeedItem item = new FeedItem();
                        item.setId(json_user.getInt("id"));
                        item.setName(json_user.getString("name"));
                        item.setUserId(json_user.getString("userid"));
                        item.setPostID(json_user.getString("postid"));
                        // Image might be null sometimes
                        String image = json_user.isNull("image") ? null : json_user.getString("image");
                        item.setImage(image);
                        item.setStatus(json_user.getString("text"));
                        item.setProfilePic(json_user.getString("profilePic"));
                        item.setTimeStamp(json_user.getString("timeStamp"));

                        // url might be null sometimes
                        String feedUrl = json_user.isNull("url") ? null : json_user
                                .getString("url");
                        item.setUrl(feedUrl);
                        item.setoprog("2");
                        feedItemsc.add(0, item);
                        listAdapterc.notifyDataSetChanged();
                        int b = Integer.parseInt(getNComment());
                        b = b + 1;
                        setNComment(String.valueOf(b));
                        inputtext.setText("");
                    } else {
                        btnsend.setVisibility(View.VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
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
                if (n.equals("1")) {

                    new Processgetcommentlist().execute();
                } else if (n.equals("2")) {
                    new Processgetcommentlistloadmore().execute();
                }

            } else {

                Toast.makeText(comment.this, R.string.errorconection,
                        Toast.LENGTH_SHORT).show();
                refresh.setVisibility(View.VISIBLE);
                prog.setVisibility(View.INVISIBLE);
                btnLoadMore.setVisibility(View.VISIBLE);
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

                new Processcomwrite().execute();
            } else {
                btnsend.setVisibility(View.VISIBLE);
                nprog.setVisibility(View.INVISIBLE);
                Toast.makeText(comment.this, R.string.errorconection,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Processgetcommentlistloadmore extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.getcommentlistloadmore(idno, postid, pid);

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
                    feedItemsc.add(item);
                }
                listViewc.setVisibility(View.VISIBLE);
                prog.setVisibility(View.INVISIBLE);
                refresh.setVisibility(View.VISIBLE);
                // notify data changes to list adapater
                listAdapterc.notifyDataSetChanged();
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
                        refresh.setVisibility(View.VISIBLE);
                        prog.setVisibility(View.INVISIBLE);
                        Toast.makeText(comment.this, R.string.errorconection,
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }
}
