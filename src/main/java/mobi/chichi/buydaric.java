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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class buydaric extends Activity {

    EditText inputtext;
    ProgressBar nprog;
    ImageButton btnok, btnback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buydaric);

        btnok = (ImageButton) findViewById(R.id.ok);
        btnback = (ImageButton) findViewById(R.id.back);

        nprog = (ProgressBar) findViewById(R.id.progressBar1);
        inputtext = (EditText) findViewById(R.id.ndaric);

        btnok.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                btnok.setVisibility(View.INVISIBLE);
                nprog.setVisibility(View.VISIBLE);
                String ndaric = inputtext.getText().toString();
                ndaric = " " + ndaric;
                ndaric = ndaric.replaceAll(" 0", "");
                ndaric = ndaric.replaceAll(" ", "");
                ndaric = ndaric.replaceAll(",", "");
                ndaric = ndaric.replaceAll("\\(", "");
                ndaric = ndaric.replaceAll("\\)", "");
                ndaric = ndaric.replaceAll(";", "");
                ndaric = ndaric.replaceAll("/", "");
                ndaric = ndaric.replaceAll("N", "");
                ndaric = ndaric.replaceAll("\\*", "");
                ndaric = ndaric.replaceAll("\\+", "");
                ndaric = ndaric.replaceAll("\\-", "");
                ndaric = ndaric.replaceAll("\\.", "");
                ndaric = ndaric.replaceAll("\\^", "");
                ndaric = ndaric.replaceAll("#", "");
                inputtext.setText(ndaric);
                if (inputtext.getText().toString().length() < 1) {

                    Toast.makeText(getApplicationContext(), R.string.inputdaricamount, Toast.LENGTH_SHORT).show();
                    btnok.setVisibility(View.VISIBLE);
                    nprog.setVisibility(View.INVISIBLE);
                } else {
                    new NetCheck().execute();
                }

            }

        });

        btnback.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                finish();

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
    private class Processbuydaric extends AsyncTask<String, String, JSONObject> {

        String userid, ndaric;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            inputtext = (EditText) findViewById(R.id.ndaric);

            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

            HashMap<String, String> user;
            user = db1.getUserDetails();
            userid = user.get("idno");
            ndaric = inputtext.getText().toString();

            int a = Integer.parseInt(ndaric);
            a = a * 10000;
            ndaric = String.valueOf(a);
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.buydaric(userid, ndaric);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                if (json.getString("success") != null) {

                    String res = json.getString("success");

                    if (Integer.parseInt(res) != 1) {
                        if (Integer.parseInt(res) == -1) {
                            //"api ارسالي با نوع api تعريف شده در فلاي نت سازگار نيست."
                            Toast.makeText(getApplicationContext(), R.string.tellerror + "-1", Toast.LENGTH_SHORT).show();
                            btnok.setVisibility(View.VISIBLE);
                            nprog.setVisibility(View.INVISIBLE);
                        } else if (Integer.parseInt(res) == -2) {
                            //"مقدار amount داده عددي نميباشد و يا کمتر از 1000 ريال است."
                            Toast.makeText(getApplicationContext(), R.string.tellerror + "-2", Toast.LENGTH_SHORT).show();
                            btnok.setVisibility(View.VISIBLE);
                            nprog.setVisibility(View.INVISIBLE);
                        } else if (Integer.parseInt(res) == -3) {
                            //"مقداررشته product_name در محدوده تعيين شده قرار ندارد"
                            Toast.makeText(getApplicationContext(), R.string.tellerror + "-3", Toast.LENGTH_SHORT).show();
                            btnok.setVisibility(View.VISIBLE);
                            nprog.setVisibility(View.INVISIBLE);
                        } else if (Integer.parseInt(res) == -4) {
                            //"درگاهي با اطلاعات ارسالي شما يافت نشده و يا در حالت انتظار ميباشد."
                            Toast.makeText(getApplicationContext(), R.string.tellerror + "-4", Toast.LENGTH_SHORT).show();
                            btnok.setVisibility(View.VISIBLE);
                            nprog.setVisibility(View.INVISIBLE);
                        } else if (Integer.parseInt(res) == 0) {
                            //"خطا در ارتباط با فلاي نت"
                            Toast.makeText(getApplicationContext(), R.string.tellerror + "0", Toast.LENGTH_SHORT).show();
                            btnok.setVisibility(View.VISIBLE);
                            nprog.setVisibility(View.INVISIBLE);
                        } else {
                            btnok.setVisibility(View.VISIBLE);
                            nprog.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        btnok.setVisibility(View.VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
                        String result = json.getString("result");
                        Intent intent = new Intent(getApplicationContext(), verifydaric.class);
                        intent.putExtra("result", result);
                        intent.putExtra("ndaric", ndaric);
                        startActivity(intent);
                        finish();
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

                new Processbuydaric().execute();
            } else {
                btnok.setVisibility(View.VISIBLE);
                nprog.setVisibility(View.INVISIBLE);
                Toast.makeText(buydaric.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
