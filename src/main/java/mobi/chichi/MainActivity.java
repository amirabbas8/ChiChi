package mobi.chichi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gcm.GCMRegistrar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import mobi.chichi.listviewfeed.FeedImageView;
import mobi.chichi.listviewfeed.app.AppController;
import mobi.chichi.materialdesign.activity.FragmentDrawer;

import static mobi.chichi.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static mobi.chichi.CommonUtilities.EXTRA_MESSAGE;
import static mobi.chichi.CommonUtilities.SENDER_ID;

public class MainActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener {


    static ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    static String profileimage1;
    static NetworkImageView profileimage;
    private static AlertDialog dialog;
    private static long back_pressed;
    private static Activity activity1;
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */

            // Showing received message
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            WakeLocker.release();
        }
    };
    android.support.v4.app.Fragment fragment = null;
    android.support.v4.app.Fragment ProfileFragment = new ProfileFragment();
    android.support.v4.app.Fragment HomeFragment = new HomeFragment();
    android.support.v4.app.Fragment newFragment = new CFragment();
    android.support.v4.app.Fragment writingsFragment = new writingsFragment();
    android.support.v4.app.Fragment notificationFragment = new notificationFragment();
    android.support.v4.app.Fragment friendFragment = new friendFragment();
    android.support.v4.app.Fragment searchFragment = new searchFragment();
    android.support.v4.app.Fragment settingFragment = new settingFragment();
    String realname;
    Toolbar mToolbar;
    private FeedImageView advertise;
    private String n = "1";
    private ProgressDialog pDialog;

    public static void reloadprofilepic() {
        profileimage.setImageUrl("http://chichi.mobi/profileimages/" + profileimage1, imageLoader);
    }

    public static void refresh() {
        activity1.finish();
        Intent upanel4 = new Intent(activity1, main.class);
        activity1.startActivity(upanel4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity1 = this;
        FragmentDrawer drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        profileimage = (NetworkImageView) findViewById(R.id.profileimage);
        advertise = (FeedImageView) findViewById(R.id.ad);
        TextView name = (TextView) findViewById(R.id.name);
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

        registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));

        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(this);
        // Check if regid already presents
        if (regId.equals("")) {

            if (GCMRegistrar.isRegistered(MainActivity.this)) {
                GCMRegistrar.register(MainActivity.this, SENDER_ID);
                GCMRegistrar.setRegisteredOnServer(MainActivity.this, true);
            } else {
                GCMRegistrar.unregister(MainActivity.this);
                GCMRegistrar.setRegisteredOnServer(MainActivity.this, false);
            //    UserFunctions logout = new UserFunctions();
            //    logout.logoutUser(getApplicationContext());
            //    Intent upanel = new Intent(this, main.class);
           //     startActivity(upanel);
            //    finish();
            }
        }

        DatabaseHandler db = new DatabaseHandler(this);
        HashMap<String, String> user;
        user = db.getUserDetails();
        String username = user.get("username");
        if (username.equals("")) {
            Intent upanel = new Intent(MainActivity.this, usernamesetmainactivity.class);
            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(upanel);
        }
        realname = user.get("realname");
        profileimage1 = user.get("profileimage");
        profileimage.setImageUrl("http://chichi.mobi/profileimages/" + profileimage1, imageLoader);
        name.setText(realname);
        String fontPath = "fonts/BYekan.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        name.setTypeface(tf);
        name.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                displayView(28);
            }

        });
        profileimage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                displayView(28);
            }

        });
        // display the first navigation drawer view on app launch
        displayView(0);
        n = "2";
        new NetCheck().execute();
        final String[] items = new String[]{getResources().getString(R.string.username), getResources().getString(R.string.phonecontact)};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.makefriend);
        builder.setAdapter(adapter1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {

                    HashMap<String, String> user = new HashMap<>();
                    String username;

                    username = user.get("username");
                    if ("".equals(username)) {
                        Intent upanel = new Intent(MainActivity.this, usernameset.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(upanel);
                    } else {
                        Intent upanel = new Intent(MainActivity.this, usernamefriendfind.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(upanel);
                    }

                } else if (item == 1) {
                    Intent upanel = new Intent(MainActivity.this, phonebookfriendfind.class);
                    upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(upanel);
                }

            }
        });
        dialog = builder.create();
        n = "3";
        new NetCheck().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "action_settings action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {


        String title = getString(R.string.app_name);
        switch (position) {
            case 28:
                fragment = ProfileFragment;
                getSupportActionBar().setCustomView(R.layout.profiletoolbar);
                LayoutInflater profiletoolbarInflater = LayoutInflater.from(this);
                View profiletoolbarview = profiletoolbarInflater.inflate(R.layout.profiletoolbar, null);
                getSupportActionBar().setCustomView(profiletoolbarview);
                getSupportActionBar().show();
                getSupportActionBar().setDisplayShowCustomEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);

                //neveshtan
                ImageButton edit = (ImageButton) profiletoolbarview.findViewById(R.id.edit);
                pDialog = new ProgressDialog(this);
                edit.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        n = "1";
                        pDialog.setMessage(getResources().getString(R.string.loading));
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(true);
                        pDialog.show();
                        new NetCheck().execute();
                    }
                });

                Button name = (Button) profiletoolbarview.findViewById(R.id.button1);
                HashMap<String, String> user;
                DatabaseHandler db1;
                db1 = new DatabaseHandler(MainActivity.this);
                user = db1.getUserDetails();
                String realname = user.get("realname");
                name.setText(realname);
                title = realname;
                FragmentDrawer.closedrawer();
                break;
            case 0:


                fragment = HomeFragment;

                getSupportActionBar().setCustomView(R.layout.hometoolbar);
                LayoutInflater mInflater = LayoutInflater.from(this);
                View view = mInflater.inflate(R.layout.hometoolbar, null);
                getSupportActionBar().setCustomView(view);
                getSupportActionBar().show();
                getSupportActionBar().setDisplayShowCustomEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);

                //neveshtan
                ImageButton neveshtan = (ImageButton) view.findViewById(R.id.plus);

                neveshtan.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        Intent intent = new Intent(MainActivity.this, mobi.chichi.neveshtan.class);
                        intent.putExtra("pos", "");

                        startActivityForResult(intent, 16);
                    }
                });

                //send to
                ImageButton btnsendto = (ImageButton) view.findViewById(R.id.sendto);

                btnsendto.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, send_to.class));

                    }
                });
                title = "ChiChi";
                break;

            case 1:
                fragment = newFragment;
                title = getString(R.string.planet);
                getSupportActionBar().show();
                getSupportActionBar().setDisplayShowCustomEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                break;
            case 2:
                fragment = writingsFragment;
                title = getString(R.string.writings);
                getSupportActionBar().show();
                getSupportActionBar().setDisplayShowCustomEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(true);

                break;
            case 3:

                fragment = notificationFragment;
                title = getString(R.string.notification);
                getSupportActionBar().show();
                getSupportActionBar().setDisplayShowCustomEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                break;
            case 4:
                fragment = friendFragment;
                getSupportActionBar().setCustomView(R.layout.hometoolbar);
                LayoutInflater friendInflater = LayoutInflater.from(this);
                View friendview = friendInflater.inflate(R.layout.friendtoolbar, null);
                getSupportActionBar().setCustomView(friendview);
                getSupportActionBar().show();
                getSupportActionBar().setDisplayShowCustomEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);

                //neveshtan
                ImageButton plus = (ImageButton) friendview.findViewById(R.id.plus);

                plus.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        dialog.show();
                    }
                });


                title = getString(R.string.manage_friend);
                break;
            case 5:
                fragment = searchFragment;
                title = getString(R.string.search);
                getSupportActionBar().hide();
                break;
            case 6:
                dialog.show();
                break;
            case 7:
                fragment = settingFragment;
                getSupportActionBar().show();
                getSupportActionBar().setDisplayShowCustomEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                title = getString(R.string.setting);
                break;


            default:
                break;
        }

        if (fragment != null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.hide(HomeFragment);
            fragmentTransaction.hide(newFragment);
            fragmentTransaction.hide(notificationFragment);
            fragmentTransaction.hide(friendFragment);
            fragmentTransaction.hide(searchFragment);
            fragmentTransaction.hide(settingFragment);
            fragmentTransaction.hide(ProfileFragment);
            fragmentTransaction.hide(writingsFragment);
            if (fragment.isAdded()) { // if the fragment is already in container
                fragmentTransaction.show(fragment);
            } else { // fragment needs to be added to frame container
                fragmentTransaction.add(R.id.container_body, fragment);
                fragmentTransaction.show(fragment);
            }


            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {

            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), R.string.onback, Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            if (requestCode == 11) {
                //comment
                String Fragment = data.getStringExtra("Fragment");
                String lastcomment = data.getStringExtra("lastcomment");
                String pos = data.getStringExtra("pos");
                String lastcommentname = data.getStringExtra("lastcommentname");
                String lastcommentuserid = data.getStringExtra("lastcommentuserid");
                String lastcommentprofilePic = data.getStringExtra("lastcommentprofilePic");
                String NComment = data.getStringExtra("NComment");
                String c = data.getStringExtra("c");
                switch (Fragment) {
                    case "home":
                        ((mobi.chichi.HomeFragment) HomeFragment).homefragmentResult(lastcomment, pos, lastcommentname, lastcommentuserid, lastcommentprofilePic, NComment, c);
                        break;
                    case "profile":
                        ((mobi.chichi.ProfileFragment) ProfileFragment).profilefragmentResult(lastcomment, pos, lastcommentname, lastcommentuserid, lastcommentprofilePic, NComment, c);

                        break;
                    case "new":
                        ((CFragment) newFragment).newfragmentResult(lastcomment, pos, lastcommentname, lastcommentuserid, lastcommentprofilePic, NComment, c);

                        break;
                }
            } else if (requestCode == 16) {
                //neveshtan
                String userid = data.getStringExtra("userid");
                String text = data.getStringExtra("text");
                String profilepic = data.getStringExtra("profilepic");
                String name = data.getStringExtra("name");
                String imagename = data.getStringExtra("imagename");
                String TimeStamp = data.getStringExtra("TimeStamp");
                String setUrl = data.getStringExtra("setUrl");
                String id = data.getStringExtra("id");
                if (HomeFragment.isAdded()) {
                    ((mobi.chichi.HomeFragment) HomeFragment).homeneveshtan(id, userid, text, setUrl, profilepic, name, imagename, TimeStamp);
                }

                if (newFragment.isAdded()) {
                    ((CFragment) newFragment).newneveshtan(id, userid, text, setUrl, profilepic, name, imagename, TimeStamp);
                }
            } else if (requestCode == 17) {
                //editneveshtan
                String fragment = data.getStringExtra("fragment");
                String userid = data.getStringExtra("userid");
                String pos = data.getStringExtra("pos");
                String text = data.getStringExtra("text");
                String profilepic = data.getStringExtra("profilepic");
                String name = data.getStringExtra("name");
                String imagename = data.getStringExtra("imagename");
                String Url = data.getStringExtra("Url");
                String TimeStamp = data.getStringExtra("TimeStamp");
                String id = data.getStringExtra("id");
                switch (fragment) {
                    case "home":
                        ((mobi.chichi.HomeFragment) HomeFragment).homeneveshtanedit(id, userid, text, Url, profilepic, name, imagename, TimeStamp, pos);
                        break;
                    case "profile":
                        ((mobi.chichi.ProfileFragment) ProfileFragment).profileneveshtanedit(id, userid, text, Url, profilepic, name, imagename, TimeStamp, pos);

                        break;
                    case "new":
                        ((CFragment) newFragment).newneveshtanedit(id, userid, text, Url, profilepic, name, imagename, TimeStamp, pos);

                        break;
                }
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void newad1() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {
                n = "2";

                new NetCheck().execute();
            }
        }, 30000);

    }

    private class Processgetad extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.getad();

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                String KEY_SUCCESS = "success";
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        JSONObject json_post = json.getJSONObject("post");

                        String imagename = json_post.getString("imagename");
                        final String link = json_post.getString("link");
                        //Show your view
                        advertise.setVisibility(View.VISIBLE);
                        advertise.setImageUrl("http://chichi.mobi/advertisement/" + imagename, imageLoader);
                        advertise.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v) {
                                PackageManager packageManager = getPackageManager();
                                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                intent1.setData(Uri.parse(link));
                                List<ResolveInfo> list = packageManager.queryIntentActivities(intent1, PackageManager.MATCH_DEFAULT_ONLY);
                                int size = list.size();
                                if (size == 0) {
                                    //no app found
                                    Toast.makeText(MainActivity.this, R.string.no_app, Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                    intent.setData(Uri.parse(link));
                                    startActivity(intent);
                                }
                            }
                        });
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            public void run() {
                                advertise.setVisibility(View.INVISIBLE);
                                newad1();
                            }
                        }, 20000);
                    } else {

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Processgetdata extends AsyncTask<String, String, JSONObject> {

        String id;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            HashMap<String, String> user;
            DatabaseHandler db1 = new DatabaseHandler(MainActivity.this);
            user = db1.getUserDetails();
            id = user.get("idno");

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            return userFunction.getdata(id);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                String KEY_SUCCESS = "success";
                String KEY_ID = "id";
                String KEY_REALNAME = "realname";
                String KEY_EMAIL = "email";
                String KEY_BIO = "bio";
                String KEY_PHONE = "phone";
                String KEY_CONTRYCODE = "contrycode";
                String KEY_GENDER = "gender";
                String KEY_nposts = "nposts";
                String KEY_profileimage = "profileimages";
                String KEY_username = "username";
                String KEY_ndaric = "ndaric";

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        pDialog.dismiss();
                        JSONObject json_user = json.getJSONObject("user");
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(MainActivity.this);
                        DatabaseHandler db1 = new DatabaseHandler(MainActivity.this);
                        db1.addUser(json_user.getString(KEY_ID), json_user.getString(KEY_REALNAME), json_user.getString(KEY_EMAIL), "home", json_user.getString(KEY_BIO), json_user.getString(KEY_PHONE), json_user.getString(KEY_GENDER), json_user.getString(KEY_CONTRYCODE), json_user.getString(KEY_nposts), json_user.getString(KEY_profileimage), json_user.getString(KEY_username), json_user.getString(KEY_ndaric));

                        Intent upanel = new Intent(MainActivity.this, editprofile.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(upanel);

                    } else {
                        pDialog.dismiss();
                        Toast.makeText(MainActivity.this, R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Processgetdata2 extends AsyncTask<String, String, JSONObject> {

        String id;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            HashMap<String, String> user;
            DatabaseHandler db1 = new DatabaseHandler(MainActivity.this);
            user = db1.getUserDetails();
            id = user.get("idno");

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            return userFunction.getdata(id);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                String KEY_SUCCESS = "success";
                String KEY_ID = "id";
                String KEY_REALNAME = "realname";
                String KEY_EMAIL = "email";
                String KEY_BIO = "bio";
                String KEY_PHONE = "phone";
                String KEY_CONTRYCODE = "contrycode";
                String KEY_GENDER = "gender";
                String KEY_nposts = "nposts";
                String KEY_profileimage = "profileimages";
                String KEY_username = "username";
                String KEY_ndaric = "ndaric";

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        JSONObject json_user = json.getJSONObject("user");
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(MainActivity.this);
                        DatabaseHandler db1 = new DatabaseHandler(MainActivity.this);
                        db1.addUser(json_user.getString(KEY_ID), json_user.getString(KEY_REALNAME), json_user.getString(KEY_EMAIL), "home", json_user.getString(KEY_BIO), json_user.getString(KEY_PHONE), json_user.getString(KEY_GENDER), json_user.getString(KEY_CONTRYCODE), json_user.getString(KEY_nposts), json_user.getString(KEY_profileimage), json_user.getString(KEY_username), json_user.getString(KEY_ndaric));

                    } else {

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
                        new Processgetdata().execute();

                        break;
                    case "2":
                        new Processgetad().execute();
                        break;
                    case "3":
                        new Processgetdata2().execute();
                        break;
                }

            } else {
                if (n.equals("1")) {
                    pDialog.dismiss();

                } else if (n.equals("2")) {
                }

            }
        }
    }
}
