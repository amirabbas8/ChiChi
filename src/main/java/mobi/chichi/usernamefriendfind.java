package mobi.chichi;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import mobi.chichi.imageloader.ImageLoader;


public class usernamefriendfind extends Activity {

    private static String KEY_SUCCESS = "success";
    private static String KEY_ID = "id";
    private static String KEY_REALNAME = "realname";
    private static String KEY_profileimage = "profileimages";
    EditText inputusername;
    ImageButton ok;
    ProgressBar uprog;
    Button add;
    ImageView fpic;
    TextView fname;
    String friend;
    String profile_url;
    String friendname;
    String n = "1";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usernamefriendfind);

        // creating connection detector class instance

        add = (Button) findViewById(R.id.add);
        fname = (TextView) findViewById(R.id.fname);
        fpic = (ImageView) findViewById(R.id.imageView2);
        inputusername = (EditText) findViewById(R.id.inputusername2);
        //back
        ImageButton back = (ImageButton) findViewById(R.id.back);
        uprog = (ProgressBar) findViewById(R.id.progressBar1);
        back.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                finish();
            }
        });

        //ok
        ok = (ImageButton) findViewById(R.id.ok);

        ok.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                ok.setVisibility(4);
                uprog.setVisibility(0);
                if (inputusername.getText().toString().length() > 4 & inputusername.getText().toString().length() < 21) {
                    DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
                    HashMap<String, String> user = new HashMap<String, String>();
                    user = db1.getUserDetails();
                    String id = user.get("username");
                    String username = inputusername.getText().toString();
                    if (id.equals(username)) {
                        ok.setVisibility(0);
                        uprog.setVisibility(4);
                        Toast.makeText(getApplicationContext(), R.string.lockingyourself, Toast.LENGTH_SHORT).show();

                    } else {
                        n = "1";

                        new NetCheck().execute();

                    }

                } else {
                    Toast.makeText(getApplicationContext(), R.string.errorusername, Toast.LENGTH_SHORT).show();
                    ok.setVisibility(0);
                    uprog.setVisibility(4);
                }

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    /**
     * Async Task to get and send data to My Sql database through JSON respone.
     */
    private class Processusernamefindf extends AsyncTask<String, String, JSONObject> {

        String username;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inputusername = (EditText) findViewById(R.id.inputusername2);
            username = inputusername.getText().toString();

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            JSONObject json = userFunction.getffusername(username);
            return json;
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {

                        JSONObject json_user = json.getJSONObject("user");
                        add.setVisibility(0);
                        fpic.setVisibility(0);
                        fname.setVisibility(0);
                        ok.setVisibility(0);
                        uprog.setVisibility(4);
                        fname.setText(json_user.getString(KEY_REALNAME));
                        friend = json_user.getString(KEY_ID);
                        friendname = json_user.getString(KEY_REALNAME);
                        // Loader image - will be shown before loading image
                        int loader = R.drawable.ic_profile;
                        profile_url = json_user.getString(KEY_profileimage);
                        ImageLoader imgLoader = new ImageLoader(getApplicationContext());
                        imgLoader.DisplayImage("http://chichi.mobi/profileimages/" + profile_url, loader, fpic);
                        //add
                        add.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {
                                ok.setVisibility(4);
                                uprog.setVisibility(0);
                                n = "2";
                                new NetCheck().execute();
                            }
                        });
                    } else if (Integer.parseInt(res) == 3) {

                        Toast.makeText(getApplicationContext(), R.string.errorfusername, Toast.LENGTH_SHORT).show();
                        ok.setVisibility(0);
                        uprog.setVisibility(4);
                    } else {

                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                        ok.setVisibility(0);
                        uprog.setVisibility(4);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Processusernameaddf extends AsyncTask<String, String, JSONObject> {

        String idno, myname, myprofilepic;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
            HashMap<String, String> user = new HashMap<String, String>();
            user = db1.getUserDetails();
            idno = user.get("idno");
            myname = user.get("realname");
            myprofilepic = user.get("profileimage");
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            JSONObject json = userFunction.addffusername(idno, friend, friendname, profile_url, myname, myprofilepic);
            return json;
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 51) {
                        ok.setVisibility(0);
                        uprog.setVisibility(4);
                        Toast.makeText(getApplicationContext(), R.string.errorfunblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 52) {
                        ok.setVisibility(0);
                        uprog.setVisibility(4);
                        Toast.makeText(getApplicationContext(), R.string.errorblock, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(res) == 1) {
                        Toast.makeText(getApplicationContext(), R.string.succes, Toast.LENGTH_SHORT).show();
                        ok.setVisibility(0);
                        uprog.setVisibility(4);
                    } else if (Integer.parseInt(res) == 2) {
                        Toast.makeText(getApplicationContext(), R.string.wasfriend, Toast.LENGTH_SHORT).show();
                        ok.setVisibility(0);
                        uprog.setVisibility(4);
                    } else {

                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                        ok.setVisibility(0);
                        uprog.setVisibility(4);
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
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;

        }


        @Override
        protected void onPostExecute(Boolean th) {

            if (th == true) {
                if (n.equals("1")) {
                    new Processusernamefindf().execute();
                } else if (n.equals("2")) {
                    new Processusernameaddf().execute();
                }

            } else {
                Toast.makeText(usernamefriendfind.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                ok.setVisibility(0);
                uprog.setVisibility(4);
            }
        }
    }
}