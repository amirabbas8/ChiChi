package mobi.chichi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
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

import mobi.chichi.listviewfeed.adapter.FeedListAdapterpbff;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.pulltoupdatelibrary.IonRefreshListener;
import mobi.chichi.pulltoupdatelibrary.PullToUpdateListView;


public class phonebookfriendfind extends Activity {

    String pnumber;
    private PullToUpdateListView listViewm;
    private FeedListAdapterpbff listAdapterm;
    private List<FeedItem> feedItemsm;
    private ProgressBar prog;

    private ImageButton refresh;


    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonebookfriendfind);
        //list
        listViewm = (PullToUpdateListView) findViewById(R.id.list);
        listViewm.setVisibility(View.INVISIBLE);
        feedItemsm = new ArrayList<>();
        listAdapterm = new FeedListAdapterpbff(this, feedItemsm);
        listViewm.setAdapter(listAdapterm);
        listViewm.setPullMode(PullToUpdateListView.MODE.UP_ONLY);
        listViewm.setAutoLoad(true, 8);
        listViewm.setPullMessageColor(R.color.textColorPrimary);
        listViewm.setLoadingMessage(getResources().getString(R.string.loading));
        listViewm.setPullRotateImage(getResources().getDrawable(R.drawable.refresh_icon));
        prog = (ProgressBar) findViewById(R.id.progressBar1);

        refresh = (ImageButton) findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                refresh.setVisibility(View.INVISIBLE);
                prog.setVisibility(View.VISIBLE);

                new NetCheck().execute();
            }
        });
        listViewm.setOnRefreshListener(new IonRefreshListener() {
            @Override
            public void onRefreshUp() {

                new NetCheck().execute();

                listViewm.onRefreshUpComplete();
            }

            @Override
            public void onRefeshDown() {

            }

        });
        fetchContacts();

    }


    public void fetchContacts() {

        String phoneNumber;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;

        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        StringBuilder output = new StringBuilder();

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {

                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        output.append(",'").append(phoneNumber).append("',");

                    }

                    phoneCursor.close();

                }

            }
            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

            HashMap<String, String> user;
            user = db1.getUserDetails();
            String contrycode = user.get("contrycode");
            pnumber = output.toString();
            pnumber = " " + pnumber + " ";
            pnumber = pnumber.replaceAll(",'0", "'" + contrycode);
            pnumber = pnumber.replaceAll(",+", ",");
            pnumber = pnumber.replaceAll("\\+", "");
            pnumber = pnumber.replaceAll("\\-", "");
            pnumber = pnumber.replaceAll(", ", "");
            pnumber = pnumber.replaceAll(" ,", "");

            new NetCheck().execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 12) {

            if (resultCode == RESULT_OK) {

                String isfriend = data.getStringExtra("isfriend");
                String pos1 = data.getStringExtra("pos");
                int pos = Integer.parseInt(pos1);
                if ("2".equals(isfriend)) {

                    feedItemsm.get(pos).setoprog("2");

                    feedItemsm.get(pos).setMylike("1");
                    listAdapterm.notifyDataSetChanged();
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

    private class Processgetfriendlist extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.getphonefriendlist(pnumber, idno);
        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                JSONArray feedArray = json.getJSONArray("friend");
                listViewm.setVisibility(View.INVISIBLE);
                feedItemsm.clear();
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);

                    FeedItem item = new FeedItem();
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("name"));
                    item.setUserId(feedObj.getString("userid"));
                    item.setProfilePic(feedObj.getString("profilePic"));
                    item.setMylike(feedObj.getString("a"));
                    item.setoprog("2");
                    feedItemsm.add(item);
                }
                listViewm.setVisibility(View.VISIBLE);
                // notify data changes to list adapater
                listAdapterm.notifyDataSetChanged();
                if (0 == feedItemsm.size()) {
                    refresh.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.INVISIBLE);

                } else {
                    refresh.setVisibility(View.GONE);
                    prog.setVisibility(View.GONE);
                }

                listViewm.onRefreshUpComplete();
                listViewm.onRefreshDownComplete(null);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        if (0 == feedItemsm.size()) {
                            refresh.setVisibility(View.VISIBLE);
                            prog.setVisibility(View.INVISIBLE);

                        } else {
                            refresh.setVisibility(View.GONE);
                            prog.setVisibility(View.GONE);
                        }

                        listViewm.onRefreshUpComplete();
                        listViewm.onRefreshDownComplete(null);
                        Toast.makeText(phonebookfriendfind.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

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

                new Processgetfriendlist().execute();
            } else {
                Toast.makeText(phonebookfriendfind.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                if (0 == feedItemsm.size()) {
                    refresh.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.INVISIBLE);

                } else {
                    refresh.setVisibility(View.GONE);
                    prog.setVisibility(View.GONE);
                }

                listViewm.onRefreshUpComplete();
                listViewm.onRefreshDownComplete(null);
            }
        }
    }
}
