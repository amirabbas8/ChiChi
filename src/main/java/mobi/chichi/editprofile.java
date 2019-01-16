package mobi.chichi;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;


public class editprofile extends Activity {

    Button email;
    EditText inputbio;
    EditText inputrealname;

    ProgressBar eprog;
    ImageButton ok;
    int n;
    int randcod;
    String code;
    Dialog emailverify_dialog;
    ProgressBar reportprog;
    EditText codetext;
    ImageButton dialog_ok;
    Dialog email_dialog;
    ProgressBar reportprog2;
    EditText emailtext;
    ImageButton dialog_ok2;
    String inserttedemail;
    String inserttedemailp;
    private Spinner cgender;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        ImageButton bazgasht = (ImageButton) findViewById(R.id.back);
        email = (Button) findViewById(R.id.email);
        inputbio = (EditText) findViewById(R.id.bio);
        inputrealname = (EditText) findViewById(R.id.rname);
        eprog = (ProgressBar) findViewById(R.id.progressBar1);
        cgender = (Spinner) findViewById(R.id.gender);

        DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

        HashMap<String, String> user;
        user = db1.getUserDetails();

        String realname = user.get("realname");
        String bio = user.get("bio");
        String gender = user.get("gender");
        final String emailx = user.get("email");
        inputbio.setText(bio);
        inputrealname.setText(realname);

        if ("1".equals(gender)) {
            cgender.setSelection(1);
        } else if ("2".equals(gender)) {
            cgender.setSelection(2);
        }

        bazgasht.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                finish();
            }
        });
        email.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                email_dialog = new Dialog(editprofile.this);
                email_dialog.setContentView(R.layout.email_dialog);
                email_dialog.setTitle(R.string.email);

                emailtext = (EditText) email_dialog.findViewById(R.id.email);
                emailtext.setText(emailx);
                DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
                HashMap<String, String> user;
                user = db1.getUserDetails();
                final String emaily = user.get("email");
                emailtext.setText(emaily);
                reportprog2 = (ProgressBar) email_dialog.findViewById(R.id.progressBar1);
                dialog_ok2 = (ImageButton) email_dialog.findViewById(R.id.dialog_ok);
                Button cancell = (Button) email_dialog.findViewById(R.id.cancell);
                dialog_ok2.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        inserttedemailp = emailtext.getText().toString().trim();
                        inserttedemail = emailtext.getText().toString();
                        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                        if (inserttedemailp.matches(emailPattern)) {
                            Random r = new Random();
                            randcod = r.nextInt((9999 - 1000) + 1) + 1000;
                            code = String.valueOf(randcod);
                            reportprog2.setVisibility(View.VISIBLE);
                            dialog_ok2.setVisibility(View.INVISIBLE);
                            n = 2;
                            new NetCheck().execute();


                        } else {
                            Toast.makeText(getApplicationContext(), R.string.erroremail, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                cancell.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        email_dialog.dismiss();

                    }
                });
                email_dialog.show();
            }
        });
        ok = (ImageButton) findViewById(R.id.ok);

        ok.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                ok.setVisibility(View.INVISIBLE);
                eprog.setVisibility(View.VISIBLE);
                n = 1;
                new NetCheck().execute();


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

    private class Processeditprofile extends AsyncTask<String, String, JSONObject> {

        String idno, realname, bio, fgender;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inputbio = (EditText) findViewById(R.id.bio);
            inputrealname = (EditText) findViewById(R.id.rname);
            cgender = (Spinner) findViewById(R.id.gender);
            realname = inputrealname.getText().toString();
            bio = inputbio.getText().toString();

            if (1 == cgender.getSelectedItemPosition()) {
                fgender = "1";
            } else if (2 == cgender.getSelectedItemPosition()) {
                fgender = "2";
            } else {
                fgender = "";
            }
            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
            HashMap<String, String> user;
            user = db1.getUserDetails();
            idno = user.get("idno");

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.editprofile(idno, realname, bio, fgender);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                String KEY_SUCCESS = "success";
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
                        String KEY_ndaric = "ndaric";
                        String KEY_CONTRYCODE = "contrycode";
                        String KEY_ID = "id";
                        String KEY_REALNAME = "realname";
                        String KEY_EMAIL = "email";
                        String KEY_BIO = "bio";
                        String KEY_nposts = "nposts";
                        String KEY_username = "username";
                        String KEY_PHONE = "phone";
                        String KEY_GENDER = "gender";
                        String KEY_profileimage = "profileimages";
                        db.addUser(json_user.getString(KEY_ID), json_user.getString(KEY_REALNAME), json_user.getString(KEY_EMAIL), "home",
                                json_user.getString(KEY_BIO), json_user.getString(KEY_PHONE),
                                json_user.getString(KEY_GENDER), json_user.getString(KEY_CONTRYCODE),
                                json_user.getString(KEY_nposts), json_user.getString(KEY_profileimage),
                                json_user.getString(KEY_username), json_user.getString(KEY_ndaric));
                        MainActivity.refresh();
                        finish();
                    } else {

                        ok.setVisibility(View.VISIBLE);
                        eprog.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Processsendemailverify extends AsyncTask<String, String, JSONObject> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.sendemailverify(inserttedemail, code);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                String KEY_SUCCESS = "success";
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        emailverify_dialog = new Dialog(editprofile.this);
                        emailverify_dialog.setContentView(R.layout.emailverify_dialog);
                        emailverify_dialog.setTitle(R.string.email);

                        codetext = (EditText) emailverify_dialog.findViewById(R.id.code);
                        reportprog = (ProgressBar) emailverify_dialog.findViewById(R.id.progressBar1);
                        dialog_ok = (ImageButton) emailverify_dialog.findViewById(R.id.dialog_ok);
                        Button cancell = (Button) emailverify_dialog.findViewById(R.id.cancell);
                        dialog_ok.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v) {

                                String insertedcode = codetext.getText().toString();
                                if (insertedcode.equals(code)) {
                                    reportprog.setVisibility(View.VISIBLE);
                                    dialog_ok.setVisibility(View.INVISIBLE);
                                    n = 3;
                                    new NetCheck().execute();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.wrongcode, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        cancell.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v) {
                                emailverify_dialog.dismiss();

                            }
                        });
                        email_dialog.dismiss();
                        emailverify_dialog.show();
                    } else {

                        reportprog2.setVisibility(View.VISIBLE);
                        dialog_ok2.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Processeditemailprofile extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.editemailprofile(idno, inserttedemail);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                String KEY_SUCCESS = "success";
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
                        String KEY_ndaric = "ndaric";
                        String KEY_CONTRYCODE = "contrycode";
                        String KEY_ID = "id";
                        String KEY_REALNAME = "realname";
                        String KEY_EMAIL = "email";
                        String KEY_BIO = "bio";
                        String KEY_nposts = "nposts";
                        String KEY_username = "username";
                        String KEY_PHONE = "phone";
                        String KEY_GENDER = "gender";
                        String KEY_profileimage = "profileimages";
                        db.addUser(json_user.getString(KEY_ID), json_user.getString(KEY_REALNAME), json_user.getString(KEY_EMAIL), "home",
                                json_user.getString(KEY_BIO), json_user.getString(KEY_PHONE),
                                json_user.getString(KEY_GENDER), json_user.getString(KEY_CONTRYCODE),
                                json_user.getString(KEY_nposts), json_user.getString(KEY_profileimage), json_user.getString(KEY_username), json_user.getString(KEY_ndaric));
                        emailverify_dialog.dismiss();
                    } else {

                        ok.setVisibility(View.VISIBLE);
                        eprog.setVisibility(View.INVISIBLE);
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
                if (n == 1) {
                    new Processeditprofile().execute();
                } else if (n == 2) {
                    new Processsendemailverify().execute();
                } else if (n == 3) {
                    new Processeditemailprofile().execute();
                } else {
                    ok.setVisibility(View.VISIBLE);
                    eprog.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), R.string.errorconection, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
