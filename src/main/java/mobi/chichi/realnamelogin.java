package mobi.chichi;

/**
 * Author :Raj Amal
 * Email :raj.amalw@learn2crack.com
 * Website:www.learn2crack.com
 **/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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


public class realnamelogin extends Activity {

    ImageButton btnLogin;

    EditText inputrealname;

    ProgressBar vprog;

    /**
     * Called when the activity is first created.
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.realnamelogin);
        // creating connection detector class instance

        inputrealname = (EditText) findViewById(R.id.inputrealname);

        btnLogin = (ImageButton) findViewById(R.id.btnlogin);
        vprog = (ProgressBar) findViewById(R.id.progressBar1);
        /**
         * Login button click event
         * A Toast is set to alert when the Email and Password field is empty
         **/
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (inputrealname.getText().toString().length() > 4 & inputrealname.getText().toString().length() < 21) {
                    btnLogin.setVisibility(View.INVISIBLE);
                    vprog.setVisibility(View.VISIBLE);
                    new NetCheck().execute();

                } else {
                    Toast.makeText(getApplicationContext(), R.string.errorrname, Toast.LENGTH_SHORT).show();
                    btnLogin.setVisibility(View.VISIBLE);
                    vprog.setVisibility(View.INVISIBLE);
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
    private class ProcessLogin extends AsyncTask<String, String, JSONObject> {

        String idno, realname;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            inputrealname = (EditText) findViewById(R.id.inputrealname);
            realname = inputrealname.getText().toString();
            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

            HashMap<String, String> user;
            user = db1.getUserDetails();
            idno = user.get("idno");
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.realnamelogin(idno, realname);
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

                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");
                        /**
                         * Clear all previous data in SQlite database.
                         **/
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());
                        db.addUser(json_user.getString(KEY_ID), json_user.getString(KEY_REALNAME), json_user.getString(KEY_EMAIL), "home", json_user.getString(KEY_BIO),
                                json_user.getString(KEY_PHONE), json_user.getString(KEY_GENDER), json_user.getString(KEY_CONTRYCODE),
                                json_user.getString(KEY_nposts), json_user.getString(KEY_profileimage), json_user.getString(KEY_username)
                                , json_user.getString(KEY_ndaric));

                        Intent upanel = new Intent(getApplicationContext(), main.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(upanel);
                        /**
                         * Close Login Screen
                         **/
                        finish();
                    } else {

                        btnLogin.setVisibility(View.VISIBLE);
                        vprog.setVisibility(View.INVISIBLE);
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

                new ProcessLogin().execute();
            } else {
                btnLogin.setVisibility(View.VISIBLE);
                vprog.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }
}