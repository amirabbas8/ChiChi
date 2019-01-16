package mobi.chichi.listviewfeed.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import java.util.List;
import java.util.regex.Pattern;

import mobi.chichi.DatabaseHandler;
import mobi.chichi.R;
import mobi.chichi.UserFunctions;
import mobi.chichi.listviewfeed.app.AppController;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.otherprofile;


public class FeedListAdapter_writings extends BaseAdapter {

    private static String KEY_SUCCESS = "success";
    private static String KEY_ID = "id";
    private static String KEY_REALNAME = "realname";
    private static String KEY_EMAIL = "email";
    private static String KEY_BIO = "bio";
    private static String KEY_PHONE = "phone";
    private static String KEY_CONTRYCODE = "contrycode";
    private static String KEY_GENDER = "gender";
    private static String KEY_nposts = "nposts";
    private static String KEY_profileimage = "profileimages";
    private static String KEY_username = "username";
    private static String KEY_ndaric = "ndaric";
    private static
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ImageButton btnlike,btnshare;
    FeedItem item;
    String userid;
    String postid;
    String posttext;
    String id;
    int n = 0;
    int pos;
    ProgressBar lprog;
    ProgressBar oprog;
    ProgressBar reportprog;
    Dialog report_inappropriate_dialog;
    String reportcomment;
    EditText commenttext;
    ImageButton dialog_ok;
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;


    public FeedListAdapter_writings(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }


    public int getCount() {
        return feedItems.size();
    }


    public Object getItem(int location) {
        return feedItems.get(location);
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item_writings, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView.findViewById(R.id.txtStatusMsg);
        TextView nlike = (TextView) convertView.findViewById(R.id.nlike);
        NetworkImageView profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);
        btnlike = (ImageButton) convertView.findViewById(R.id.btnlike);
        ImageButton option = (ImageButton) convertView.findViewById(R.id.option);
         btnshare = (ImageButton) convertView.findViewById(R.id.share);
        lprog = (ProgressBar) convertView.findViewById(R.id.progressBar2);
        oprog = (ProgressBar) convertView.findViewById(R.id.progressBar1);
        String fontPath = "fonts/BYekan.ttf";
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), fontPath);
        name.setTypeface(tf);
        timestamp.setTypeface(tf);
        statusMsg.setTypeface(tf);
        nlike.setTypeface(tf);
        item = feedItems.get(position);
        DatabaseHandler db1 = new DatabaseHandler(activity);
        HashMap<String, String> user;
        user = db1.getUserDetails();
        id = user.get("idno");

        if ("1".equals(item.getoprog())) {

            oprog.setVisibility(View.VISIBLE);
            option.setVisibility(View.INVISIBLE);

        } else if ("2".equals(item.getoprog())) {

            oprog.setVisibility(View.INVISIBLE);
            option.setVisibility(View.VISIBLE);

        }
        final String[] items = new String[]{activity.getResources().getString(R.string.delete)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.managepost);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    n = 4;
                    feedItems.get(position).setoprog("1");
                    notifyDataSetChanged();
                    new NetCheck().execute();

                }

            }
        });

        final AlertDialog dialog = builder.create();

        final String[] items1 = new String[]{ activity.getResources().getString(R.string.report_inappropriate)};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, items1);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setTitle(R.string.managepost);
        builder1.setAdapter(adapter1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {
                    report_inappropriate_dialog = new Dialog(activity);
                    report_inappropriate_dialog.setContentView(R.layout.report_inappropriate_dialog);
                    report_inappropriate_dialog.setTitle(R.string.report_inappropriate);

                    commenttext = (EditText) report_inappropriate_dialog.findViewById(R.id.commenttext);
                    reportprog = (ProgressBar) report_inappropriate_dialog.findViewById(R.id.progressBar1);
                    dialog_ok = (ImageButton) report_inappropriate_dialog.findViewById(R.id.dialog_ok);
                    Button cancell = (Button) report_inappropriate_dialog.findViewById(R.id.cancell);
                    dialog_ok.setOnClickListener(new OnClickListener() {

                        public void onClick(View v) {

                            String text = commenttext.getText().toString();
                            reportprog.setVisibility(View.VISIBLE);
                            dialog_ok.setVisibility(View.INVISIBLE);
                            n = 5;
                            new NetCheck().execute();
                            reportcomment = text;

                        }
                    });
                    cancell.setOnClickListener(new OnClickListener() {

                        public void onClick(View v) {

                            report_inappropriate_dialog.dismiss();

                        }
                    });
                    report_inappropriate_dialog.show();
                }

            }
        });

        final AlertDialog dialog1 = builder1.create();
        if (id.equals(item.getUserId())) {
            option.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    pos = position;
                    postid = String.valueOf(feedItems.get(position).getId());
                    posttext = String.valueOf(feedItems.get(position).getStatus());
                    dialog.show();
                }
            });

        } else {
            option.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    pos = position;
                    postid = String.valueOf(feedItems.get(position).getId());

                    dialog1.show();
                }
            });
        }

        name.setText(item.getName());

        timestamp.setText(item.getTimeStamp());
        nlike.setText(item.getNLike());
        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }
        Pattern tagMatcher = Pattern.compile("[#]+\\w+\\b");
        String newActivityURL = "chichihash://chichi.mobi/";
        Linkify.addLinks(statusMsg, tagMatcher, newActivityURL);

         Pattern tagMatcher2 = Pattern.compile("[@]+[A-Za-z0-9-_]+\\b");
         String newActivityURL2 = "chichitag://chichi.mobi/";
          Linkify.addLinks(statusMsg, tagMatcher2, newActivityURL2);
       //  Checking for null feed url



        // user profile pic
        profilePic.setImageUrl("http://chichi.mobi/profileimages/" + item.getProfilePic(), imageLoader);

        // Feed image


        if ("1".equals(item.getMylike())) {
            btnlike.setImageResource(R.drawable.feed_button_like_active);

            btnlike.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {

                    pos = position;
                    postid = String.valueOf(feedItems.get(position).getId());
                    userid = String.valueOf(feedItems.get(position).getUserId());

                    feedItems.get(position).setlprog("1");
                    notifyDataSetChanged();
                    n = 3;
                    new NetCheck().execute();

                }
            });

        } else if ("2".equals(item.getMylike())) {

            btnlike.setImageResource(R.drawable.feed_button_like);

            btnlike.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {

                    pos = position;
                    postid = String.valueOf(feedItems.get(position).getId());
                    userid = String.valueOf(feedItems.get(position).getUserId());

                    feedItems.get(position).setlprog("1");
                    notifyDataSetChanged();
                    n = 2;
                    new NetCheck().execute();

                }
            });

        }
        btnshare.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                posttext = String.valueOf(feedItems.get(position).getStatus());
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, posttext + "              ChiChi writings ");
                activity.startActivity(Intent.createChooser(share, activity.getResources().getString(R.string.Share)));

            }
        });

        profilePic.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String userid = String.valueOf(feedItems.get(position).getUserId());
                String username = String.valueOf(feedItems.get(position).getName());
                String userprofilepic = String.valueOf(feedItems.get(position).getProfilePic());
                Intent intent = new Intent(activity, otherprofile.class);
                intent.putExtra("userid", userid);
                intent.putExtra("username", username);
                intent.putExtra("userprofilepic", userprofilepic);
                activity.startActivity(intent);
            }
        });
        name.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String userid = String.valueOf(feedItems.get(position).getUserId());
                String username = String.valueOf(feedItems.get(position).getName());
                String userprofilepic = String.valueOf(feedItems.get(position).getProfilePic());
                Intent intent = new Intent(activity, otherprofile.class);
                intent.putExtra("userid", userid);
                intent.putExtra("username", username);
                intent.putExtra("userprofilepic", userprofilepic);
                activity.startActivity(intent);
            }
        });
        return convertView;

    }


    private class NetCheck extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected Boolean doInBackground(String... args) {

            /** * Gets current device state and checks for working internet connection by trying Google. **/

            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                if (n == 2) {

                    new Processlike_writings().execute();
                } else if (n == 3) {

                    new Processdislike_writings().execute();
                } else if (n == 4) {
                    new Processdelete().execute();
                } else if (n == 5) {
                    new Processreport().execute();
                }
            } else {
                if (n == 5) {
                    reportprog.setVisibility(View.INVISIBLE);
                    dialog_ok.setVisibility(View.VISIBLE);
                }
                feedItems.get(pos).setoprog("2");
                feedItems.get(pos).setlprog("2");
                notifyDataSetChanged();
                Toast.makeText(activity, R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class Processlike_writings extends AsyncTask<String, String, JSONObject> {

        String id0, pic, name;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseHandler db1 = new DatabaseHandler(activity);

            HashMap<String, String> user;
            user = db1.getUserDetails();
            id0 = user.get("idno");
            pic = user.get("profileimage");
            name = user.get("realname");
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            return userFunction.like_writings(id0, postid, userid, name, pic);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 51) {
                        feedItems.get(pos).setlprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorfunblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 52) {
                        feedItems.get(pos).setlprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 1) {

                        DatabaseHandler db = new DatabaseHandler(activity);
                        JSONObject json_user = json.getJSONObject("user");
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(activity);
                        db.addUser(json_user.getString(KEY_ID), json_user.getString(KEY_REALNAME), json_user.getString(KEY_EMAIL), "home", json_user.getString(KEY_BIO), json_user.getString(KEY_PHONE), json_user.getString(KEY_GENDER), json_user.getString(KEY_CONTRYCODE), json_user.getString(KEY_nposts), json_user.getString(KEY_profileimage), json_user.getString(KEY_username), json_user.getString(KEY_ndaric));
                        //hh
                        feedItems.get(pos).setMylike("1");
                        int b = Integer.parseInt(feedItems.get(pos).getNLike());
                        int c = b + 1;
                        String d = String.valueOf(c);
                        feedItems.get(pos).setNLike(d);

                        btnlike.setImageResource(R.drawable.feed_button_like_active);
                        feedItems.get(pos).setlprog("2");
                        notifyDataSetChanged();

                    } else {
                        feedItems.get(pos).setlprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class Processdislike_writings extends AsyncTask<String, String, JSONObject> {

        String id0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseHandler db1 = new DatabaseHandler(activity);

            HashMap<String, String> user;
            user = db1.getUserDetails();
            id0 = user.get("idno");
        }


        @Override
        protected JSONObject doInBackground(String... args) {
            UserFunctions userFunction = new UserFunctions();

            return userFunction.dislike_writings(id0, postid, userid);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);
                    if (Integer.parseInt(res) == 51) {
                        feedItems.get(pos).setlprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorfunblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 52) {
                        feedItems.get(pos).setlprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 1) {

                        DatabaseHandler db = new DatabaseHandler(activity);
                        JSONObject json_user = json.getJSONObject("user");
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(activity);
                        db.addUser(json_user.getString(KEY_ID), json_user.getString(KEY_REALNAME), json_user.getString(KEY_EMAIL), "home", json_user.getString(KEY_BIO), json_user.getString(KEY_PHONE), json_user.getString(KEY_GENDER), json_user.getString(KEY_CONTRYCODE), json_user.getString(KEY_nposts), json_user.getString(KEY_profileimage), json_user.getString(KEY_username), json_user.getString(KEY_ndaric));

                        feedItems.get(pos).setMylike("2");
                        int b = Integer.parseInt(feedItems.get(pos).getNLike());
                        int c = b - 1;
                        String d = String.valueOf(c);
                        feedItems.get(pos).setNLike(d);

                        btnlike.setImageResource(R.drawable.feed_button_like);
                        feedItems.get(pos).setlprog("2");

                        notifyDataSetChanged();

                    } else {
                        feedItems.get(pos).setlprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class Processdelete extends AsyncTask<String, String, JSONObject> {

        String id0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseHandler db1 = new DatabaseHandler(activity);

            HashMap<String, String> user;
            user = db1.getUserDetails();
            id0 = user.get("idno");
        }


        @Override
        protected JSONObject doInBackground(String... args) {
            UserFunctions userFunction = new UserFunctions();

            return userFunction.deletewritings(postid, id0);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        feedItems.get(pos).setoprog("2");

                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(res) == 2) {
                        feedItems.get(pos).setoprog("2");

                        feedItems.remove(pos);
                        notifyDataSetChanged();
                    } else {
                        feedItems.get(pos).setoprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorproblem, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    feedItems.get(pos).setoprog("2");
                    notifyDataSetChanged();
                    Toast.makeText(activity, R.string.errorproblem, Toast.LENGTH_SHORT).show();
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
            DatabaseHandler db1 = new DatabaseHandler(activity);
            HashMap<String, String> user;
            user = db1.getUserDetails();
            id0 = user.get("idno");
        }


        @Override
        protected JSONObject doInBackground(String... args) {
            UserFunctions userFunction = new UserFunctions();

            return userFunction.report(postid, id0, "writings", reportcomment);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        reportprog.setVisibility(View.INVISIBLE);
                        dialog_ok.setVisibility(View.VISIBLE);
                        Toast.makeText(activity, R.string.report_inappropriate_saved, Toast.LENGTH_SHORT).show();
                        report_inappropriate_dialog.dismiss();
                        feedItems.get(pos).setoprog("2");
                        notifyDataSetChanged();

                    } else if (Integer.parseInt(res) == 2) {
                        reportprog.setVisibility(View.INVISIBLE);
                        dialog_ok.setVisibility(View.VISIBLE);
                        Toast.makeText(activity, R.string.report_inappropriate_errorsaved, Toast.LENGTH_SHORT).show();
                        report_inappropriate_dialog.dismiss();
                        feedItems.get(pos).setoprog("2");
                        notifyDataSetChanged();

                    } else {
                        reportprog.setVisibility(View.INVISIBLE);
                        dialog_ok.setVisibility(View.VISIBLE);
                        feedItems.get(pos).setoprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorproblem, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    reportprog.setVisibility(View.INVISIBLE);
                    dialog_ok.setVisibility(View.VISIBLE);
                    feedItems.get(pos).setoprog("2");
                    notifyDataSetChanged();
                    Toast.makeText(activity, R.string.errorproblem, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
