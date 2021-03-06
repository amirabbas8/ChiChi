package mobi.chichi.listviewfeed.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
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
import java.util.List;
import java.util.regex.Pattern;

import mobi.chichi.DatabaseHandler;
import mobi.chichi.R;
import mobi.chichi.UserFunctions;
import mobi.chichi.comment;
import mobi.chichi.listviewfeed.FeedImageView;
import mobi.chichi.listviewfeed.app.AppController;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.neveshtanedit;
import mobi.chichi.otherprofile;


public class FeedListAdaptero extends BaseAdapter {

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
    ImageButton btnlike, btncomment;
    FeedItem item;
    String userid0;
    String postid;
    String posttext;
    String id;
    int n = 0;
    int pos;
    ProgressBar lprog;
    ProgressBar oprog;
    Dialog report_inappropriate_dialog;
    String reportcomment;
    EditText commenttext;
    ProgressBar reportprog;
    ImageButton dialog_ok;
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;


    public FeedListAdaptero(Activity activity, List<FeedItem> feedItems) {
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
            convertView = inflater.inflate(R.layout.feed_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView.findViewById(R.id.txtStatusMsg);
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        TextView nlike = (TextView) convertView.findViewById(R.id.nlike);
        TextView ncomment = (TextView) convertView.findViewById(R.id.ncomment);
        NetworkImageView profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) convertView.findViewById(R.id.feedImage1);
        TextView lastcommentname = (TextView) convertView.findViewById(R.id.lastcommentname);
        NetworkImageView lastcommentprofilePic = (NetworkImageView) convertView.findViewById(R.id.lastcommentprofilePic);
        TextView lastcomment = (TextView) convertView.findViewById(R.id.lastcomment);
        LinearLayout l1 = (LinearLayout) convertView.findViewById(R.id.l1);
        RelativeLayout l2 = (RelativeLayout) convertView.findViewById(R.id.l2);
        btnlike = (ImageButton) convertView.findViewById(R.id.btnlike);
        btncomment = (ImageButton) convertView.findViewById(R.id.btncomment);
        ImageButton option = (ImageButton) convertView.findViewById(R.id.option);
        lprog = (ProgressBar) convertView.findViewById(R.id.progressBar2);
        oprog = (ProgressBar) convertView.findViewById(R.id.progressBar1);
        String fontPath = "fonts/BYekan.ttf";
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), fontPath);
        name.setTypeface(tf);
        timestamp.setTypeface(tf);
        statusMsg.setTypeface(tf);
        nlike.setTypeface(tf);
        ncomment.setTypeface(tf);
        lastcommentname.setTypeface(tf);
        lastcomment.setTypeface(tf);
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
        final String[] items = new String[]{activity.getResources().getString(R.string.edit),activity.getResources().getString(R.string.delete), activity.getResources().getString(R.string.Share)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.managepost);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {

                    Intent intent = new Intent(activity, neveshtanedit.class);
                    String id = String.valueOf(feedItems.get(position).getId());
                    intent.putExtra("id", id);
                    String status = String.valueOf(feedItems.get(position).getStatus());
                    intent.putExtra("status", status);
                    String image = String.valueOf(feedItems.get(position).getImage());
                    intent.putExtra("image", image);
                    String url = String.valueOf(feedItems.get(position).getUrl());
                    intent.putExtra("url", url);
                    String fragment = String.valueOf(feedItems.get(position).getFragment());
                    intent.putExtra("fragment", fragment);
                    String d = String.valueOf(position);
                    intent.putExtra("pos", d);

                    activity.startActivityForResult(intent, 17);


                } else
                if (item == 1) {

                    n = 4;
                    feedItems.get(position).setoprog("1");
                    notifyDataSetChanged();
                    new NetCheck().execute();

                } else if (item == 2) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, posttext);
                    activity.startActivity(Intent.createChooser(share, activity.getResources().getString(R.string.Share)));
                }

            }
        });

        final AlertDialog dialog = builder.create();

        final String[] items1 = new String[]{activity.getResources().getString(R.string.Share), activity.getResources().getString(R.string.report_inappropriate)};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, items1);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setTitle(R.string.managepost);
        builder1.setAdapter(adapter1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, posttext);
                    activity.startActivity(Intent.createChooser(share, activity.getResources().getString(R.string.Share)));
                }
                if (item == 1) {
                    report_inappropriate_dialog = new Dialog(activity);
                    report_inappropriate_dialog.setContentView(R.layout.report_inappropriate_dialog);
                    report_inappropriate_dialog.setTitle(R.string.report_inappropriate);

                    commenttext = (EditText) report_inappropriate_dialog.findViewById(R.id.commenttext);
                    reportprog = (ProgressBar) report_inappropriate_dialog.findViewById(R.id.progressBar1);
                    dialog_ok = (ImageButton) report_inappropriate_dialog.findViewById(R.id.dialog_ok);
                    Button cancell = (Button) report_inappropriate_dialog.findViewById(R.id.cancell);
                    dialog_ok.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {

                            String text = commenttext.getText().toString();
                            reportprog.setVisibility(View.VISIBLE);
                            dialog_ok.setVisibility(View.INVISIBLE);
                            n = 5;
                            new NetCheck().execute();
                            reportcomment = text;

                        }
                    });
                    cancell.setOnClickListener(new View.OnClickListener() {

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
                    posttext = String.valueOf(feedItems.get(position).getStatus());
                    dialog1.show();
                }
            });
        }

        name.setText(item.getName());

        if ("0".equals(item.getNComment())) {
            lastcommentname.setVisibility(View.GONE);
            lastcomment.setVisibility(View.GONE);
            lastcommentprofilePic.setVisibility(View.GONE);
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.VISIBLE);
        } else {
            lastcommentname.setVisibility(View.VISIBLE);
            lastcomment.setVisibility(View.VISIBLE);
            lastcommentprofilePic.setVisibility(View.VISIBLE);
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.VISIBLE);
            lastcommentname.setText(item.getLastCommentName());
            lastcomment.setText(item.getLastComment());
            lastcommentprofilePic.setImageUrl("http://chichi.mobi/profileimages/" + item.getLastCommentProfilePic(), imageLoader);

        }
        timestamp.setText(item.getTimeStamp());
        nlike.setText(item.getNLike());
        ncomment.setText(item.getNComment());
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
        // Checking for null feed url
        if (item.getUrl() != null) {
            PackageManager packageManager = activity.getPackageManager();
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setData(Uri.parse(item.getUrl()));
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent1, PackageManager.MATCH_DEFAULT_ONLY);
            int size = list.size();
            if (size == 0) {
                url.setText(Html.fromHtml("<a href=\"" + "http://" + item.getUrl() + "\">"
                        + item.getUrl() + "</a> "));
                url.setMovementMethod(LinkMovementMethod.getInstance());
                url.setVisibility(View.VISIBLE);

            } else {
                url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                        + item.getUrl() + "</a> "));

                // Making url clickable
                url.setMovementMethod(LinkMovementMethod.getInstance());
                url.setVisibility(View.VISIBLE);
            }

        } else {
            // url is null, remove from the view
            url.setVisibility(View.GONE);
        }

        // user profile pic
        profilePic.setImageUrl("http://chichi.mobi/profileimages/" + item.getProfilePic(), imageLoader);

        // Feed image
        if (item.getImage() != null) {
            feedImageView.setImageUrl(item.getImage(), imageLoader);
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
        if ("1".equals(item.getlprog())) {

            lprog.setVisibility(View.VISIBLE);
            btnlike.setVisibility(View.INVISIBLE);

        } else if ("2".equals(item.getlprog())) {

            lprog.setVisibility(View.INVISIBLE);
            btnlike.setVisibility(View.VISIBLE);

        }

        if ("1".equals(item.getMylike())) {
            btnlike.setImageResource(R.drawable.feed_button_like_active);
            btnlike.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    pos = position;
                    postid = String.valueOf(feedItems.get(position).getId());
                    userid0 = String.valueOf(feedItems.get(position).getUserId());
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
                    userid0 = String.valueOf(feedItems.get(position).getUserId());
                    feedItems.get(position).setlprog("1");
                    notifyDataSetChanged();
                    n = 2;
                    new NetCheck().execute();

                }
            });

        }
        btncomment.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String id = String.valueOf(feedItems.get(position).getId());
                Intent intent = new Intent(activity, comment.class);
                intent.putExtra("postid", id);
                String d = String.valueOf(position);
                intent.putExtra("pos", d);

                String NComment = String.valueOf(feedItems.get(position).getNComment());
                intent.putExtra("NComment", NComment);
                userid0 = String.valueOf(feedItems.get(position).getUserId());
                intent.putExtra("userid", userid0);
                activity.startActivityForResult(intent, 11);
            }

        });
        lastcommentprofilePic.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String userid = String.valueOf(feedItems.get(position).getLastCommentUserId());
                Intent intent = new Intent(activity, otherprofile.class);
                intent.putExtra("userid", userid);
                activity.startActivity(intent);
            }
        });
        lastcommentname.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String userid = String.valueOf(feedItems.get(position).getLastCommentUserId());
                Intent intent = new Intent(activity, otherprofile.class);
                intent.putExtra("userid", userid);
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
                    new Processlike().execute();
                } else if (n == 3) {
                    new Processdislike().execute();
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


    private class Processlike extends AsyncTask<String, String, JSONObject> {

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

            return userFunction.like(id0, postid, userid0, name, pic);
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
                        btnlike.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {
                                n = 3;
                                new NetCheck().execute();

                            }
                        });

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


    private class Processdislike extends AsyncTask<String, String, JSONObject> {

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

            return userFunction.dislike(id0, postid, userid0);
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
                        btnlike.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {
                                n = 2;
                                new NetCheck().execute();

                            }
                        });

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

            return userFunction.deletepost(postid, id0);
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

            return userFunction.report(postid, id0, "post", reportcomment);
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
