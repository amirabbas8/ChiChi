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
import mobi.chichi.otherprofile;


public class FeedListAdapterc extends BaseAdapter {

    private static String KEY_SUCCESS = "success";
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ImageButton btnlike, btncomment;
    FeedItem item;
    String comid, postid;
    String posttext;
    int n;
    int pos;
    String tcomment;
    String commentname;
    String commentuserid;
    String commentprofilePic;
    ProgressBar oprog;
    ProgressBar reportprog;
    Dialog report_inappropriate_dialog;
    String reportcomment;
    EditText commenttext;
    ImageButton dialog_ok;
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;


    public FeedListAdapterc(Activity activity, List<FeedItem> feedItems) {
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
        String id = user.get("idno");

        if ("1".equals(item.getoprog())) {

            oprog.setVisibility(View.VISIBLE);
            option.setVisibility(View.INVISIBLE);

        } else if ("2".equals(item.getoprog())) {
            oprog.setVisibility(View.INVISIBLE);

            option.setVisibility(View.VISIBLE);

        }
        final String[] items = new String[]{activity.getResources().getString(R.string.delete), activity.getResources().getString(R.string.Share)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.managecomment);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {
                    int b0 = Integer.parseInt(comment.getNComment());
                    if (b0 > 1) {
                        if (position == 0) {
                            tcomment = feedItems.get(1).getStatus();
                            commentname = feedItems.get(1).getName();
                            commentuserid = feedItems.get(1).getUserId();
                            commentprofilePic = feedItems.get(1).getProfilePic();
                        } else {
                            tcomment = feedItems.get(0).getStatus();
                            commentname = feedItems.get(0).getName();
                            commentuserid = feedItems.get(0).getUserId();
                            commentprofilePic = feedItems.get(0).getProfilePic();
                        }

                    } else {
                        tcomment = "";
                        commentname = "";
                        commentuserid = "";
                        commentprofilePic = "";
                    }
                    feedItems.get(position).setoprog("1");
                    notifyDataSetChanged();
                    n = 1;
                    new NetCheck().execute();
                } else if (item == 1) {
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
                            n = 2;
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
                    comid = String.valueOf(feedItems.get(position).getId());
                    postid = String.valueOf(feedItems.get(position).getPostID());
                    posttext = String.valueOf(feedItems.get(position).getStatus());
                    dialog.show();
                }
            });

        } else {
            option.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    pos = position;
                    comid = String.valueOf(feedItems.get(position).getId());
                    postid = String.valueOf(feedItems.get(position).getPostID());
                    posttext = String.valueOf(feedItems.get(position).getStatus());
                    dialog1.show();
                }
            });
        }
        name.setText(item.getName());

        lastcommentname.setVisibility(View.GONE);
        lastcomment.setVisibility(View.GONE);
        lastcommentprofilePic.setVisibility(View.GONE);
        l1.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);

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
                if (n == 1) {

                    new Processdelete().execute();
                } else if (n == 2) {

                    new Processreport().execute();
                }

            } else {
                if (n == 2) {
                    reportprog.setVisibility(View.INVISIBLE);
                    dialog_ok.setVisibility(View.VISIBLE);
                }
                feedItems.get(pos).setoprog("2");
                notifyDataSetChanged();
                Toast.makeText(activity, R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class Processdelete extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected JSONObject doInBackground(String... args) {
            UserFunctions userFunction = new UserFunctions();

            return userFunction.deletecomment(comid, postid, tcomment, commentname, commentuserid, commentprofilePic);
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
                        int b = Integer.parseInt(comment.getNComment());
                        b = b - 1;
                        comment.setNComment(String.valueOf(b));
                        feedItems.remove(pos);
                        notifyDataSetChanged();
                    } else {
                        feedItems.get(pos).setoprog("2");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.errorproblem, Toast.LENGTH_SHORT).show();

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
            DatabaseHandler db1 = new DatabaseHandler(activity);
            HashMap<String, String> user;
            user = db1.getUserDetails();
            id0 = user.get("idno");

        }


        @Override
        protected JSONObject doInBackground(String... args) {
            UserFunctions userFunction = new UserFunctions();

            return userFunction.report(comid, id0, "comment", reportcomment);
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
