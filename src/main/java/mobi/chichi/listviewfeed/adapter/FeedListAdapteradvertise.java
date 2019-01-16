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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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

import mobi.chichi.DatabaseHandler;
import mobi.chichi.R;
import mobi.chichi.UserFunctions;
import mobi.chichi.advertiseedit;
import mobi.chichi.listviewfeed.app.AppController;
import mobi.chichi.listviewfeed.data.FeedItemadvertise;


public class FeedListAdapteradvertise extends BaseAdapter {

    private static String KEY_SUCCESS = "success";
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    FeedItemadvertise item;
    String advertiseid;
    String id;
    int n = 0;
    int pos;
    ProgressBar oprog;
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItemadvertise> feedItems;
    private Dialog deletedialog;
    private Dialog paydialog;


    public FeedListAdapteradvertise(Activity activity, List<FeedItemadvertise> feedItems) {
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
            convertView = inflater.inflate(R.layout.feed_itemadvertise, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView start = (TextView) convertView.findViewById(R.id.start);
        TextView end = (TextView) convertView.findViewById(R.id.end);
        TextView status = (TextView) convertView.findViewById(R.id.status);
        NetworkImageView Pic = (NetworkImageView) convertView.findViewById(R.id.Pic);
        ImageButton option = (ImageButton) convertView.findViewById(R.id.option);
        oprog = (ProgressBar) convertView.findViewById(R.id.progressBar1);
        String fontPath = "fonts/BYekan.ttf";
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), fontPath);
        name.setTypeface(tf);
        status.setTypeface(tf);
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
        deletedialog = new Dialog(activity);
        deletedialog.setContentView(R.layout.dialog);
        deletedialog.setTitle(R.string.delete);
        TextView text = (TextView) deletedialog.findViewById(R.id.text);
        text.setText(R.string.delete_dialog);
        Button yes = (Button) deletedialog.findViewById(R.id.yes);
        Button no = (Button) deletedialog.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                feedItems.get(pos).setoprog("1");
                notifyDataSetChanged();
                n = 0;
                new NetCheck().execute();
                deletedialog.dismiss();

            }
        });
        no.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                deletedialog.dismiss();

            }
        });
        paydialog = new Dialog(activity);
        paydialog.setContentView(R.layout.dialog);
        paydialog.setTitle(R.string.pay);
        Button payyes = (Button) paydialog.findViewById(R.id.yes);
        Button payno = (Button) paydialog.findViewById(R.id.no);
        TextView textview = (TextView) paydialog.findViewById(R.id.text);
        textview.setText(R.string.pay_dialog);
        payyes.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                feedItems.get(pos).setoprog("1");
                notifyDataSetChanged();
                n = 3;
                new NetCheck().execute();
                paydialog.dismiss();

            }
        });
        payno.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                paydialog.dismiss();

            }
        });
        status.setVisibility(View.VISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.advertisemanage);
        if ("0".equals(item.getStatus())) {
            status.setText(R.string.saved);
            final String[] items = new String[]{activity.getResources().getString(R.string.check), activity.getResources().getString(R.string.edit), activity.getResources().getString(R.string.delete)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, items);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int item) {

                    if (item == 0) {
                        n = 1;
                        feedItems.get(position).setoprog("1");
                        notifyDataSetChanged();
                        new NetCheck().execute();

                    }
                    if (item == 1) {

                        Intent intent = new Intent(activity, advertiseedit.class);
                        String id = String.valueOf(feedItems.get(position).getId());
                        intent.putExtra("advertiseid", id);
                        String name = String.valueOf(feedItems.get(position).getName());
                        intent.putExtra("advertisename", name);
                        String image = String.valueOf(feedItems.get(position).getImage());
                        intent.putExtra("advertiseimage", image);
                        String link = String.valueOf(feedItems.get(position).getlink());
                        intent.putExtra("advertiselink", link);
                        String d = String.valueOf(position);
                        intent.putExtra("pos", d);

                        activity.startActivityForResult(intent, 11);

                    } else if (item == 2) {
                        deletedialog.show();
                    }

                }
            });
        } else if ("1".equals(item.getStatus())) {

            status.setText(activity.getResources().getString(R.string.checking));
            final String[] items = new String[]{activity.getResources().getString(R.string.edit), activity.getResources().getString(R.string.delete)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, items);

            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {

                        Intent intent = new Intent(activity, advertiseedit.class);
                        String id = String.valueOf(feedItems.get(position).getId());
                        intent.putExtra("advertiseid", id);
                        String name = String.valueOf(feedItems.get(position).getName());
                        intent.putExtra("advertisename", name);
                        String image = String.valueOf(feedItems.get(position).getImage());
                        intent.putExtra("advertiseimage", image);
                        String link = String.valueOf(feedItems.get(position).getlink());
                        intent.putExtra("advertiselink", link);
                        String d = String.valueOf(position);
                        intent.putExtra("pos", d);

                        activity.startActivityForResult(intent, 11);

                    }
                    if (item == 1) {
                        deletedialog.show();

                    }

                }
            });
        } else if ("2".equals(item.getStatus())) {

            status.setText(R.string.checked);
            final String[] items = new String[]{activity.getResources().getString(R.string.pay), activity.getResources().getString(R.string.edit), activity.getResources().getString(R.string.delete)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, items);

            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int item) {

                    if (item == 0) {
                        paydialog.show();

                    }
                    if (item == 1) {
                        Intent intent = new Intent(activity, advertiseedit.class);
                        String id = String.valueOf(feedItems.get(position).getId());
                        intent.putExtra("advertiseid", id);
                        String name = String.valueOf(feedItems.get(position).getName());
                        intent.putExtra("advertisename", name);
                        String image = String.valueOf(feedItems.get(position).getImage());
                        intent.putExtra("advertiseimage", image);
                        String link = String.valueOf(feedItems.get(position).getlink());
                        intent.putExtra("advertiselink", link);
                        String d = String.valueOf(position);
                        intent.putExtra("pos", d);

                        activity.startActivityForResult(intent, 11);

                    } else if (item == 2) {
                        deletedialog.show();
                    }

                }
            });
        } else if ("3".equals(item.getStatus())) {

            status.setText(R.string.payed);
            final String[] items = new String[]{activity.getResources().getString(R.string.active), activity.getResources().getString(R.string.edit), activity.getResources().getString(R.string.delete)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, items);

            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int item) {

                    if (item == 0) {

                        n = 4;
                        feedItems.get(position).setoprog("1");
                        notifyDataSetChanged();
                        new NetCheck().execute();
                    }
                    if (item == 1) {
                        Intent intent = new Intent(activity, advertiseedit.class);
                        String id = String.valueOf(feedItems.get(position).getId());
                        intent.putExtra("advertiseid", id);
                        String name = String.valueOf(feedItems.get(position).getName());
                        intent.putExtra("advertisename", name);
                        String image = String.valueOf(feedItems.get(position).getImage());
                        intent.putExtra("advertiseimage", image);
                        String link = String.valueOf(feedItems.get(position).getlink());
                        intent.putExtra("advertiselink", link);
                        String d = String.valueOf(position);
                        intent.putExtra("pos", d);

                        activity.startActivityForResult(intent, 11);

                    } else if (item == 2) {
                        deletedialog.show();
                    }

                }
            });
        } else if ("4".equals(item.getStatus())) {

            status.setText(R.string.activated);
            final String[] items = new String[]{activity.getResources().getString(R.string.disactive), activity.getResources().getString(R.string.edit), activity.getResources().getString(R.string.delete)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, items);

            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int item) {

                    if (item == 0) {
                        n = 5;
                        feedItems.get(position).setoprog("1");
                        notifyDataSetChanged();
                        new NetCheck().execute();

                    }
                    if (item == 1) {
                        Intent intent = new Intent(activity, advertiseedit.class);
                        String id = String.valueOf(feedItems.get(position).getId());
                        intent.putExtra("advertiseid", id);
                        String name = String.valueOf(feedItems.get(position).getName());
                        intent.putExtra("advertisename", name);
                        String image = String.valueOf(feedItems.get(position).getImage());
                        intent.putExtra("advertiseimage", image);
                        String link = String.valueOf(feedItems.get(position).getlink());
                        intent.putExtra("advertiselink", link);
                        String d = String.valueOf(position);
                        intent.putExtra("pos", d);

                        activity.startActivityForResult(intent, 11);

                    } else if (item == 2) {
                        deletedialog.show();
                    }

                }
            });
        } else if ("5".equals(item.getStatus())) {

            status.setText(R.string.deactivated);
            final String[] items = new String[]{activity.getResources().getString(R.string.active), activity.getResources().getString(R.string.edit), activity.getResources().getString(R.string.delete)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, items);

            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int item) {

                    if (item == 0) {
                        n = 4;
                        feedItems.get(position).setoprog("1");
                        notifyDataSetChanged();
                        new NetCheck().execute();

                    }
                    if (item == 1) {
                        Intent intent = new Intent(activity, advertiseedit.class);
                        String id = String.valueOf(feedItems.get(position).getId());
                        intent.putExtra("advertiseid", id);
                        String name = String.valueOf(feedItems.get(position).getName());
                        intent.putExtra("advertisename", name);
                        String image = String.valueOf(feedItems.get(position).getImage());
                        intent.putExtra("advertiseimage", image);
                        String link = String.valueOf(feedItems.get(position).getlink());
                        intent.putExtra("advertiselink", link);
                        String d = String.valueOf(position);
                        intent.putExtra("pos", d);

                        activity.startActivityForResult(intent, 11);

                    } else if (item == 2) {
                        deletedialog.show();
                    }

                }
            });
        } else if ("6".equals(item.getStatus())) {

            status.setText(R.string.rejected);
            final String[] items = new String[]{activity.getResources().getString(R.string.edit), activity.getResources().getString(R.string.delete)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, items);

            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int item) {

                    if (item == 0) {
                        Intent intent = new Intent(activity, advertiseedit.class);
                        String id = String.valueOf(feedItems.get(position).getId());
                        intent.putExtra("advertiseid", id);
                        String name = String.valueOf(feedItems.get(position).getName());
                        intent.putExtra("advertisename", name);
                        String image = String.valueOf(feedItems.get(position).getImage());
                        intent.putExtra("advertiseimage", image);
                        String link = String.valueOf(feedItems.get(position).getlink());
                        intent.putExtra("advertiselink", link);
                        String d = String.valueOf(position);
                        intent.putExtra("pos", d);

                        activity.startActivityForResult(intent, 11);

                    } else if (item == 1) {
                        deletedialog.show();
                    }

                }
            });
        }

        final AlertDialog dialog = builder.create();

        option.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                pos = position;
                advertiseid = String.valueOf(feedItems.get(position).getId());

                dialog.show();
            }
        });
        start.setText(item.getstartdate());
        end.setText(item.getenddate());
        name.setText(item.getName());

        // user profile pic
        Pic.setImageUrl("http://chichi.mobi/advertisement/" + item.getImage(), imageLoader);

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
                if (n == 0) {
                    new Processdelete().execute();
                } else if (n == 1) {
                    new Processcheck().execute();
                } else if (n == 3) {
                    new Processpay().execute();
                } else if (n == 4) {
                    new Processactive().execute();
                } else if (n == 5) {
                    new Processdisactive().execute();
                }

            } else {
                feedItems.get(pos).setoprog("2");
                notifyDataSetChanged();
                Toast.makeText(activity, R.string.errorconection, Toast.LENGTH_SHORT).show();
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

            return userFunction.deleteadvertise(advertiseid, id0);
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


    private class Processcheck extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.advertisecheck(advertiseid);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        feedItems.get(pos).setoprog("2");
                        feedItems.get(pos).setStatus("1");
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


    private class Processpay extends AsyncTask<String, String, JSONObject> {

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

            return userFunction.payadvertise(advertiseid, id0);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        //رداخت شد
                        feedItems.get(pos).setoprog("2");
                        feedItems.get(pos).setStatus("3");
                        notifyDataSetChanged();
                        Toast.makeText(activity, R.string.succes, Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(res) == 2) {
                        //دریک نداری
                        feedItems.get(pos).setoprog("2");
                        Toast.makeText(activity, "دریک ندارید", Toast.LENGTH_SHORT).show();
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


    private class Processactive extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.advertiseactive(advertiseid);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        feedItems.get(pos).setoprog("2");
                        feedItems.get(pos).setStatus("4");
                        JSONObject json_post = json.getJSONObject("post");
                        String startdate = json_post.getString("startdate");
                        String enddate = json_post.getString("enddate");
                        feedItems.get(pos).setstartdate(startdate);
                        feedItems.get(pos).setenddate(enddate);
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


    private class Processdisactive extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.advertisedisactive(advertiseid);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        feedItems.get(pos).setoprog("2");
                        feedItems.get(pos).setStatus("5");
                        JSONObject json_post = json.getJSONObject("post");
                        String startdate = json_post.getString("startdate");
                        String enddate = json_post.getString("enddate");
                        feedItems.get(pos).setstartdate(startdate);
                        feedItems.get(pos).setenddate(enddate);
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

}
