package mobi.chichi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import static mobi.chichi.CommonUtilities.SENDER_ID;

public class phonelogin extends Activity {


    EditText inputPhone;
    Spinner inputcontrycode;

    ProgressBar vprog;
    ImageButton btnlogin;
    String regId;
    int randcod,nlogin;
    String sms;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonelogin);
        // creating connection detector class instance
        Random r = new Random();
        randcod = r.nextInt((9999 - 1000) + 1) + 1000;
        sms = String.valueOf(randcod);
        btnlogin = (ImageButton) findViewById(R.id.btnlogin);
        vprog = (ProgressBar) findViewById(R.id.progressBar1);
        inputcontrycode = (Spinner) findViewById(R.id.contrycode);
        inputPhone = (EditText) findViewById(R.id.phone);
        TextView refresh = (TextView) findViewById(R.id.policiesaccept);

        refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent upanel5 = new Intent(phonelogin.this, policies.class);
                upanel5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upanel5);
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (inputPhone.getText().toString().length() > 9 & inputPhone.getText().toString().length() < 11) {

                    if (0 == inputcontrycode.getSelectedItemPosition()) {
                        Toast.makeText(getApplicationContext(), R.string.selectcountry, Toast.LENGTH_SHORT).show();
                    } else {

                        btnlogin.setVisibility(View.INVISIBLE);
                        vprog.setVisibility(View.VISIBLE);

                        String phoneNo = "30004746575666";

                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                           // Toast.makeText(getApplicationContext(), sms, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), R.string.onemin, Toast.LENGTH_SHORT).show();
                            // Make sure the device has the proper dependencies.
                            GCMRegistrar.checkDevice(phonelogin.this);
                            GCMRegistrar.checkManifest(phonelogin.this);
                            // registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
                            regId = GCMRegistrar.getRegistrationId(phonelogin.this);
                            if (regId.equals("")) {
                                // Registration is not present, register now with GCM
                                GCMRegistrar.register(phonelogin.this, SENDER_ID);
                            }

                            nlogin=1;
                            new NetCheck().execute();


                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), R.string.errorsendsms, Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            btnlogin.setVisibility(View.VISIBLE);
                            vprog.setVisibility(View.INVISIBLE);
                        }

                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.errorphone, Toast.LENGTH_SHORT).show();
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


    private class ProcessLogin extends AsyncTask<String, String, JSONObject> {

        String contrycode, phone;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            inputcontrycode = (Spinner) findViewById(R.id.contrycode);
            inputPhone = (EditText) findViewById(R.id.phone);
            contrycode = inputcontrycode.getSelectedItem().toString();
            if (1 == inputcontrycode.getSelectedItemPosition()) {
                contrycode = "iran";
            } else if (2 == inputcontrycode.getSelectedItemPosition()) {

            }

            phone = inputPhone.getText().toString();


            // Get GCM registration id
            regId = GCMRegistrar.getRegistrationId(phonelogin.this);
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.PhoneLogin2(contrycode, phone, sms, regId);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                String KEY_SUCCESS = "success";
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

                    String KEY_ID = "id";
                    if (Integer.parseInt(res) == 1) {
                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");
                        /**
                         * Clear all previous data in SQlite database.
                         **/
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());
                        if ("".equals(json_user.getString(KEY_REALNAME))) {
                            db.addUser(json_user.getString(KEY_ID), json_user.getString(KEY_REALNAME), json_user.getString(KEY_EMAIL), "realnamelogin", json_user.getString(KEY_BIO), json_user.getString(KEY_PHONE), json_user.getString(KEY_GENDER), json_user.getString(KEY_CONTRYCODE), json_user.getString(KEY_nposts), json_user.getString(KEY_profileimage), json_user.getString(KEY_username), json_user.getString(KEY_ndaric));
                        } else {
                            db.addUser(json_user.getString(KEY_ID), json_user.getString(KEY_REALNAME), json_user.getString(KEY_EMAIL), "home", json_user.getString(KEY_BIO), json_user.getString(KEY_PHONE), json_user.getString(KEY_GENDER), json_user.getString(KEY_CONTRYCODE), json_user.getString(KEY_nposts), json_user.getString(KEY_profileimage), json_user.getString(KEY_username), json_user.getString(KEY_ndaric));
                        }
                        GCMRegistrar.setRegisteredOnServer(phonelogin.this, true);
                        /**
                         * If JSON array details are stored in SQlite it launches the User Panel.
                         **/
                        Intent upanel = new Intent(getApplicationContext(), main.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //  pDialog.dismiss();

                        startActivity(upanel);
                        /**
                         * Close Login Screen
                         **/

                        finish();
                    } else if (Integer.parseInt(res) == 3) {
                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");
                        /**
                         * Clear all previous data in SQlite database.
                         **/
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());
                        db.addUser(json_user.getString(KEY_ID), json_user.getString(KEY_REALNAME), json_user.getString(KEY_EMAIL), "realnamelogin", json_user.getString(KEY_BIO), json_user.getString(KEY_PHONE), json_user.getString(KEY_GENDER), json_user.getString(KEY_CONTRYCODE), json_user.getString(KEY_nposts), json_user.getString(KEY_profileimage), json_user.getString(KEY_username), json_user.getString(KEY_ndaric));

                        /**
                         * If JSON array details are stored in SQlite it launches the User Panel.
                         **/
                        Intent upanel = new Intent(getApplicationContext(), main.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //  pDialog.dismiss();

                        startActivity(upanel);
                        /**
                         * Close Login Screen
                         **/

                        finish();
                    } else if (Integer.parseInt(res) == 2) {
                        if (nlogin==1)
                        {
                            nlogin=2;
                            new NetCheck().execute();
                        }else if(nlogin==2){
                            nlogin=3;
                            new NetCheck().execute();
                        }else if(nlogin==3){
                            btnlogin.setVisibility(View.VISIBLE);
                            vprog.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), R.string.errorresivesms, Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        btnlogin.setVisibility(View.VISIBLE);
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
                btnlogin.setVisibility(View.VISIBLE);
                vprog.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
