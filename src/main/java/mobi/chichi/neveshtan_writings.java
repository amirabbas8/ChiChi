package mobi.chichi;

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
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class neveshtan_writings extends Activity {


    EditText inputtext;
    ProgressBar nprog;
    ImageButton btnsend, btnback;
    Spinner sptags;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addwriting);
        DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
        if (!(db1.getRowCount() > 0)) {

            Intent intent = new Intent(neveshtan_writings.this, main.class);
            startActivity(intent);
            finish();
        }
        btnsend = (ImageButton) findViewById(R.id.send);
        btnback = (ImageButton) findViewById(R.id.back);
        sptags = (Spinner) findViewById(R.id.tags);
        nprog = (ProgressBar) findViewById(R.id.progressBar1);
        inputtext = (EditText) findViewById(R.id.text);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else {
                // Handle other intents, such as being started from the home screen
            }
        }

        btnsend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                btnsend.setVisibility(View.INVISIBLE);
                nprog.setVisibility(View.VISIBLE);
                if (0 == sptags.getSelectedItemPosition()) {
                    Toast.makeText(getApplicationContext(),R.string.Select_tag, Toast.LENGTH_SHORT).show();
                    btnsend.setVisibility(View.VISIBLE);
                    nprog.setVisibility(View.INVISIBLE);
                }else {
                if (inputtext.getText().toString().length() < 1) {
                    Toast.makeText(getApplicationContext(), R.string.errorwrite_writings, Toast.LENGTH_SHORT).show();
                    btnsend.setVisibility(View.VISIBLE);
                    nprog.setVisibility(View.INVISIBLE);

                } else {
                    new NetCheck().execute();
                }
            }}

        });

        btnback.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                finish();

            }

        });
        //111111111


    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            inputtext.setText(sharedText);
            // Update UI to reflect text being shared
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


    private class Processwrite extends AsyncTask<String, String, JSONObject> {

        String userid, text, profilepic, name,tags;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            inputtext = (EditText) findViewById(R.id.text);
            text = inputtext.getText().toString();

            if (0 == sptags.getSelectedItemPosition()) {
                tags = "0";
            } else if (1 == sptags.getSelectedItemPosition()) {
                tags="1";
            }
            else if (2 == sptags.getSelectedItemPosition()) {
                tags="2";
            }
            else if (3 == sptags.getSelectedItemPosition()) {
                tags="3";
            }      else if (4 == sptags.getSelectedItemPosition()) {
                tags="4";
            }      else if (5 == sptags.getSelectedItemPosition()) {
                tags="5";
            }      else if (6 == sptags.getSelectedItemPosition()) {
                tags="6";
            }      else if (7 == sptags.getSelectedItemPosition()) {
                tags="7";
            }      else if (8 == sptags.getSelectedItemPosition()) {
                tags="8";
            }      else if (9 == sptags.getSelectedItemPosition()) {
                tags="9";
            }      else if (10 == sptags.getSelectedItemPosition()) {
                tags="10";
            }      else if (11== sptags.getSelectedItemPosition()) {
                tags="11";
            }      else if (12== sptags.getSelectedItemPosition()) {
                tags="12";
            }      else if (13== sptags.getSelectedItemPosition()) {
                tags="13";
            }      else if (14== sptags.getSelectedItemPosition()) {
                tags="14";
            }      else if (15== sptags.getSelectedItemPosition()) {
                tags="15";
            }      else if (16== sptags.getSelectedItemPosition()) {
                tags="16";
            }
            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

            HashMap<String, String> user;
            user = db1.getUserDetails();
            userid = user.get("idno");
            profilepic = user.get("profileimage");
            name = user.get("realname");


        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.addwriting(userid, text, profilepic, name, tags);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                String KEY_SUCCESS = "success";
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        Toast.makeText(getApplicationContext(),R.string.succes, Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        btnsend.setVisibility(View.VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
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

                new Processwrite().execute();
            } else {
                btnsend.setVisibility(View.VISIBLE);
                nprog.setVisibility(View.INVISIBLE);
                Toast.makeText(neveshtan_writings.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
