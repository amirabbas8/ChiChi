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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class usernamesetmainactivity extends Activity {

    private static String KEY_SUCCESS = "success";
    EditText inputusername;
    ImageButton ok;
    ProgressBar uprog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usernameset);

        // creating connection detector class instance

        inputusername = (EditText) findViewById(R.id.inputusername);
        uprog = (ProgressBar) findViewById(R.id.progressBar2);
        //back
        ImageButton back = (ImageButton) findViewById(R.id.back);

        back.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                finish();
            }
        });

        //ok
        ok = (ImageButton) findViewById(R.id.ok);

        ok.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (inputusername.getText().toString().length() > 4 & inputusername.getText().toString().length() < 21) {
                    ok.setVisibility(View.INVISIBLE);
                    uprog.setVisibility(View.VISIBLE);

                    String userPattern = "[a-zA-Z0-9._-]+";

                    if (inputusername.getText().toString().matches(userPattern)) {
                        new NetCheck().execute();

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.errorusernameset, Toast.LENGTH_SHORT).show();
                        ok.setVisibility(View.VISIBLE);
                        uprog.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.errorusername, Toast.LENGTH_SHORT).show();
                    ok.setVisibility(View.VISIBLE);
                    uprog.setVisibility(View.INVISIBLE);
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

    private class Processusernamecheck extends AsyncTask<String, String, JSONObject> {

        String id, username;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inputusername = (EditText) findViewById(R.id.inputusername);
            username = inputusername.getText().toString();
            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

            HashMap<String, String> user;
            user = db1.getUserDetails();
            id = user.get("idno");

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            return userFunction.usernamecheck(id, username);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        new Processusernameset().execute();
                        Toast.makeText(getApplicationContext(),
                                "1/2", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(res) == 2) {

                        Toast.makeText(getApplicationContext(), R.string.usernamealreadytaken, Toast.LENGTH_SHORT).show();
                        ok.setVisibility(View.VISIBLE);
                        uprog.setVisibility(View.INVISIBLE);
                    } else {

                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                        ok.setVisibility(View.VISIBLE);
                        uprog.setVisibility(View.INVISIBLE);
                    }
                } else {

                    Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    ok.setVisibility(View.VISIBLE);
                    uprog.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Processusernameset extends AsyncTask<String, String, JSONObject> {

        String id, username;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inputusername = (EditText) findViewById(R.id.inputusername);
            username = inputusername.getText().toString();
            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

            HashMap<String, String> user;
            user = db1.getUserDetails();
            id = user.get("idno");

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            return userFunction.usernameset(id, username);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
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

                        Toast.makeText(getApplicationContext(),
                                "2/2", Toast.LENGTH_SHORT).show();
                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");
                        /**
                         * Clear all previous data in SQlite database.
                         **/
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());
                        db.addUser(json_user.getString(KEY_ID), json_user.getString(KEY_REALNAME), json_user.getString(KEY_EMAIL), "home", json_user.getString(KEY_BIO), json_user.getString(KEY_PHONE), json_user.getString(KEY_GENDER), json_user.getString(KEY_CONTRYCODE), json_user.getString(KEY_nposts), json_user.getString(KEY_profileimage), json_user.getString(KEY_username), json_user.getString(KEY_ndaric));
                        finish();
                    } else if (Integer.parseInt(res) == 2) {

                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                        ok.setVisibility(View.VISIBLE);
                        uprog.setVisibility(View.INVISIBLE);
                    } else {

                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                        ok.setVisibility(View.VISIBLE);
                        uprog.setVisibility(View.INVISIBLE);
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

                new Processusernamecheck().execute();
            } else {
                ok.setVisibility(View.VISIBLE);
                uprog.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }
}