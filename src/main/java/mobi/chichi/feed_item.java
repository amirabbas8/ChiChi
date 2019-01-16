package mobi.chichi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import mobi.chichi.listviewfeed.FeedImageView;
import mobi.chichi.listviewfeed.app.AppController;


public class feed_item extends Activity {

    private static String KEY_SUCCESS = "success";
    String postid;
    ProgressBar prog;
    ProgressBar lprog;
    ImageButton refresh;
    ImageButton btnlike, btncomment;
    TextView name;
    TextView timestamp;
    TextView statusMsg;
    TextView url;
    TextView nlike;
    TextView ncomment;
    NetworkImageView profilePic;
    FeedImageView feedImageView;
    TextView lastcommentname;
    NetworkImageView lastcommentprofilePic;
    TextView lastcomment;
    LinearLayout l1;
    LinearLayout liner;
    RelativeLayout l2;
    ImageButton option;
    String id;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    String posttext;
    String n = "1";
    String name0;
    String profilePics;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_item222);
        Bundle bundle = getIntent().getExtras();
        postid = bundle.getString("postid");
        prog = (ProgressBar) findViewById(R.id.progressBar1);
        lprog = (ProgressBar) findViewById(R.id.progressBar2);
        Button button1 = (Button) findViewById(R.id.button1);
        name = (TextView) findViewById(R.id.name);
        timestamp = (TextView) findViewById(R.id.timestamp);
        statusMsg = (TextView) findViewById(R.id.txtStatusMsg);
        url = (TextView) findViewById(R.id.txtUrl);
        nlike = (TextView) findViewById(R.id.nlike);
        ncomment = (TextView) findViewById(R.id.ncomment);
        profilePic = (NetworkImageView) findViewById(R.id.profilePic);
        feedImageView = (FeedImageView) findViewById(R.id.feedImage1);
        lastcommentname = (TextView) findViewById(R.id.lastcommentname);
        lastcommentprofilePic = (NetworkImageView) findViewById(R.id.lastcommentprofilePic);
        lastcomment = (TextView) findViewById(R.id.lastcomment);
        l1 = (LinearLayout) findViewById(R.id.l1);
        liner = (LinearLayout) findViewById(R.id.liner);
        l2 = (RelativeLayout) findViewById(R.id.l2);
        btnlike = (ImageButton) findViewById(R.id.btnlike);
        btncomment = (ImageButton) findViewById(R.id.btncomment);
        option = (ImageButton) findViewById(R.id.option);
        refresh = (ImageButton) findViewById(R.id.refresh);
        button1.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.INVISIBLE);
        String fontPath = "fonts/BYekan.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        name.setTypeface(tf);
        timestamp.setTypeface(tf);
        statusMsg.setTypeface(tf);
        nlike.setTypeface(tf);
        ncomment.setTypeface(tf);
        lastcommentname.setTypeface(tf);
        lastcomment.setTypeface(tf);
        prog.setVisibility(View.VISIBLE);
        n = "1";
        new NetCheck().execute();
        //refresh

        refresh.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                prog.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.INVISIBLE);
                n = "1";
                new NetCheck().execute();
            }
        });
        //back
        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 11) {

            if (resultCode == RESULT_OK) {

                String lastcomment1 = data.getStringExtra("lastcomment");

                String lastcommentname1 = data.getStringExtra("lastcommentname");
                final String lastcommentuserid1 = data.getStringExtra("lastcommentuserid");
                String lastcommentprofilePic1 = data.getStringExtra("lastcommentprofilePic");
                String NComment1 = data.getStringExtra("NComment");

                if ("".equals(lastcomment)) {
                    ncomment.setText(NComment1);
                    lastcommentprofilePic.setOnClickListener(new OnClickListener() {

                        public void onClick(View v) {
                        }
                    });
                    lastcommentname.setText("");
                    lastcommentname.setText("");
                    lastcommentprofilePic.setImageUrl("http://chichi.mobi/profileimages/" + "", imageLoader);

                    lastcomment.setText("");

                } else {
                    if ("1".equals(data.getStringExtra("c"))) {

                    } else {
                        lastcommentprofilePic.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {

                                Intent intent = new Intent(getApplicationContext(), otherprofile.class);
                                intent.putExtra("userid", lastcommentuserid1);
                                feed_item.this.startActivity(intent);
                            }
                        });
                        lastcommentname.setText(lastcommentname1);
                        lastcommentname.setText(lastcommentname1);
                        lastcommentprofilePic.setImageUrl("http://chichi.mobi/profileimages/" + lastcommentprofilePic1, imageLoader);

                        ncomment.setText(NComment1);
                        lastcomment.setText(lastcomment1);

                    }
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
                        new Processgetpost().execute();
                        break;
                    case "2":
                        new Processlike().execute();
                        break;
                    case "3":
                        new Processdislike().execute();
                        break;
                }
            } else {
                prog.setVisibility(View.INVISIBLE);
                refresh.setVisibility(View.VISIBLE);
                lprog.setVisibility(View.INVISIBLE);
                btnlike.setVisibility(View.VISIBLE);
                Toast.makeText(feed_item.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Processgetpost extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            return userFunction.getpost(postid);
        }


        @Override
        protected void onPostExecute(final JSONObject json) {
            try {
                final JSONObject json_user = json.getJSONObject("user");
                if (json.getString("success") != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        imageLoader = AppController.getInstance().getImageLoader();
                        DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
                        HashMap<String, String> user;
                        user = db1.getUserDetails();
                        id = user.get("idno");
                        if (id.equals(json_user.getString("userid"))) {
                            option.setVisibility(View.VISIBLE);

                        } else {
                            option.setVisibility(View.GONE);
                        }
                        postid = json_user.getString("id");
                        posttext = json_user.getString("status");
                        final String[] items = new String[]{"delete", "Share the text"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.select_dialog_item, items);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

                        builder.setTitle("manage your post");
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int item) {

                                if (item == 0) {

                                    UserFunctions userFunction = new UserFunctions();
                                    JSONObject json = userFunction.deletepost(postid, id);

                                    try {
                                        if (json.getString(KEY_SUCCESS) != null) {

                                            String res = json_user.getString(KEY_SUCCESS);

                                            if (Integer.parseInt(res) == 1) {
                                                Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                                            } else if (Integer.parseInt(res) == 2) {
                                                finish();
                                            } else {

                                                Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else if (item == 1) {
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("text/plain");
                                    share.putExtra(Intent.EXTRA_TEXT, posttext);
                                    startActivity(Intent.createChooser(share, "Share Text"));
                                }

                            }
                        });

                        final AlertDialog dialog = builder.create();

                        option.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {

                                dialog.show();
                            }
                        });

                        name.setText(json_user.getString("name"));

                        lastcommentname.setVisibility(View.VISIBLE);
                        lastcomment.setVisibility(View.VISIBLE);
                        lastcommentprofilePic.setVisibility(View.VISIBLE);
                        l1.setVisibility(View.VISIBLE);
                        l2.setVisibility(View.VISIBLE);
                        lastcommentname.setText(json_user.getString("lastcommentname"));
                        lastcomment.setText(json_user.getString("lastcomment"));
                        lastcommentprofilePic.setImageUrl("http://chichi.mobi/profileimages/" + json_user.getString("lastcommentprofilePic"), imageLoader);

                        timestamp.setText(json_user.getString("timeStamp"));
                        nlike.setText(json_user.getString("nlike"));
                        ncomment.setText(json_user.getString("ncomment"));
                        // Chcek for empty status message
                        if (!TextUtils.isEmpty(json_user.getString("status"))) {
                            statusMsg.setText(json_user.getString("status"));
                            statusMsg.setVisibility(View.VISIBLE);
                        } else {
                            // status is empty, remove from view
                            statusMsg.setVisibility(View.GONE);
                        }

                        // Checking for null feed url
                        if (json_user.getString("url") != null) {
                            url.setText(Html.fromHtml("<a href=\"" + json_user.getString("url") + "\">"
                                    + json_user.getString("url") + "</a> "));

                            // Making url clickable
                            url.setMovementMethod(LinkMovementMethod.getInstance());
                            url.setVisibility(View.VISIBLE);
                        } else {
                            // url is null, remove from the view
                            url.setVisibility(View.GONE);
                        }

                        // user profile pic
                        profilePic.setImageUrl("http://chichi.mobi/profileimages/" + json_user.getString("profilePic"), imageLoader);

                        // Feed image
                        if (json_user.getString("image") != null) {
                            feedImageView.setImageUrl("http://chichi.mobi/postsimages/" + json_user.getString("image"), imageLoader);
                            feedImageView.setVisibility(View.VISIBLE);
                            feedImageView
                                    .setResponseObserver(new FeedImageView.ResponseObserver() {

                                        public void onError() {
                                        }


                                        public void onSuccess() {
                                        }
                                    });
                        } else {
                            feedImageView.setVisibility(View.GONE);
                        }
                        //btnlike

                        if ("1".equals(json_user.getString("mylike"))) {
                            btnlike.setImageResource(R.drawable.feed_button_like_active);

                        } else if ("2".equals(json_user.getString("mylike"))) {
                            btnlike.setImageResource(R.drawable.feed_button_like);
                        }
                        final String a = json_user.getString("mylike");
                        btnlike.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {

                                if ("1".equals(a)) {
                                    n = "3";
                                    lprog.setVisibility(View.VISIBLE);
                                    btnlike.setVisibility(View.INVISIBLE);
                                    new NetCheck().execute();
                                } else if ("2".equals(a)) {
                                    lprog.setVisibility(View.VISIBLE);
                                    btnlike.setVisibility(View.INVISIBLE);
                                    n = "2";
                                    new NetCheck().execute();
                                }
                            }
                        });
                        final String ncomment = json_user.getString("ncomment");
                        final String userid = json_user.getString("userid");
                        name0 = json_user.getString("name");
                        profilePics = json_user.getString("profilePic");
                        final String lastcommentuserid = json_user.getString("lastcommentuserid");
                        btncomment.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {

                                Intent intent = new Intent(getApplicationContext(), comment.class);
                                intent.putExtra("postid", postid);
                                intent.putExtra("pos", "");
                                intent.putExtra("NComment", ncomment);
                                intent.putExtra("userid", userid);
                                feed_item.this.startActivityForResult(intent, 11);
                            }

                        });
                        profilePic.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {

                                Intent intent = new Intent(getApplicationContext(), otherprofile.class);
                                intent.putExtra("userid", userid);
                                intent.putExtra("username", name0);
                                intent.putExtra("userprofilepic", profilePics);
                                feed_item.this.startActivity(intent);
                            }
                        });
                        lastcommentprofilePic.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {

                                Intent intent = new Intent(getApplicationContext(), otherprofile.class);
                                intent.putExtra("userid", lastcommentuserid);
                                feed_item.this.startActivity(intent);
                            }
                        });
                        prog.setVisibility(View.INVISIBLE);
                        refresh.setVisibility(View.VISIBLE);
                    } else {

                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Processlike extends AsyncTask<String, String, JSONObject> {

        String id0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

            HashMap<String, String> user;
            user = db1.getUserDetails();
            id0 = user.get("idno");

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            return userFunction.like(id0, postid, id, name0, profilePics);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 51) {

                        Toast.makeText(getApplicationContext(), R.string.errorfunblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 52) {

                        Toast.makeText(getApplicationContext(), R.string.errorblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 1) {
                        lprog.setVisibility(View.INVISIBLE);
                        btnlike.setVisibility(View.VISIBLE);
                        int b = Integer.parseInt(nlike.getText().toString());
                        nlike.setText(String.valueOf(b + 1));
                        btnlike.setImageResource(R.drawable.feed_button_like_active);
                        btnlike.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {
                                n = "3";
                                lprog.setVisibility(View.VISIBLE);
                                btnlike.setVisibility(View.INVISIBLE);
                                new NetCheck().execute();

                            }
                        });

                    } else {

                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Processdislike extends AsyncTask<String, String, JSONObject> {

        String id0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

            HashMap<String, String> user;
            user = db1.getUserDetails();
            id0 = user.get("idno");
        }


        @Override
        protected JSONObject doInBackground(String... args) {
            UserFunctions userFunction = new UserFunctions();

            return userFunction.dislike(id0, postid, id);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);
                    if (Integer.parseInt(res) == 51) {

                        Toast.makeText(getApplicationContext(), R.string.errorfunblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 52) {

                        Toast.makeText(getApplicationContext(), R.string.errorblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 1) {
                        lprog.setVisibility(View.INVISIBLE);
                        btnlike.setVisibility(View.VISIBLE);
                        int b = Integer.parseInt(nlike.getText().toString());
                        nlike.setText(String.valueOf(b - 1));
                        btnlike.setImageResource(R.drawable.feed_button_like);
                        btnlike.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {
                                n = "2";
                                lprog.setVisibility(View.VISIBLE);
                                btnlike.setVisibility(View.INVISIBLE);
                                new NetCheck().execute();

                            }
                        });

                    } else {

                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}