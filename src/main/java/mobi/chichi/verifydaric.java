package mobi.chichi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class verifydaric extends Activity {

    ProgressBar prog;
    ImageButton btnback, dialog_ok;
    Button btnpay, btnverify;
    String result, ndaric, phone;
    TextView ussd;
    EditText edit;
    Dialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verifydaric);
        Bundle bundle = getIntent().getExtras();
        result = bundle.getString("result");
        ndaric = bundle.getString("ndaric");
        btnback = (ImageButton) findViewById(R.id.back);
        btnpay = (Button) findViewById(R.id.pay);
        btnverify = (Button) findViewById(R.id.verify);
        prog = (ProgressBar) findViewById(R.id.progressBar1);
        ussd = (TextView) findViewById(R.id.ussd);
        String ussdnum = "USSD=*724*888*" + result + "#";
        ussd.setText(ussdnum);

        TextView textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setMovementMethod(new ScrollingMovementMethod());
        String fontPath = "fonts/BYekan.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        textView1.setTypeface(tf);

        btnback.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                finish();

            }

        });
        btnpay.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                String encodedHash = Uri.encode("#");
                String ussd_string = "tel:*724*888*" + result + "#";
                ussd_string = ussd_string.replace("#", encodedHash);
                callIntent.setData(Uri.parse(ussd_string));

                startActivity(callIntent);

            }

        });
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.phonedaric_dialog);
        dialog.setTitle(R.string.phonenumber);
        DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

        HashMap<String, String> user;
        user = db1.getUserDetails();
        edit = (EditText) dialog.findViewById(R.id.phonetext);
        edit.setText("0" + user.get("phone"));

        dialog_ok = (ImageButton) dialog.findViewById(R.id.dialog_ok);
        Button cancell = (Button) dialog.findViewById(R.id.cancell);
        dialog_ok.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String text = edit.getText().toString();
                btnverify.setVisibility(View.INVISIBLE);
                prog.setVisibility(View.VISIBLE);
                new NetCheck().execute();
                phone = text;
                dialog.dismiss();

            }
        });
        cancell.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                dialog.dismiss();

            }
        });
        btnverify.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                dialog.show();

            }

        });
    }


    @Override
    public void onBackPressed() {

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
    private class Processverifydaric extends AsyncTask<String, String, JSONObject> {

        String id;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

            HashMap<String, String> user;
            user = db1.getUserDetails();
            id = user.get("idno");
            int a = Integer.parseInt(ndaric);
            a = a / 10000;
            ndaric = String.valueOf(a);
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.verifydaric(id, result, ndaric, phone);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                if (json.getString("success") != null) {

                    String res = json.getString("success");

                    if (Integer.parseInt(res) == 1) {

                        Toast.makeText(getApplicationContext(), R.string.succes, Toast.LENGTH_SHORT).show();
                        prog.setVisibility(View.INVISIBLE);
                        btnverify.setVisibility(View.VISIBLE);
                        finish();
                    } else if (Integer.parseInt(res) == -1) {
                        //"api ارسالي با نوع api تعريف شده در فلاي نت سازگار نيست."
                        Toast.makeText(getApplicationContext(), R.string.tellerror + "-1", Toast.LENGTH_SHORT).show();
                        prog.setVisibility(View.INVISIBLE);
                        btnverify.setVisibility(View.VISIBLE);
                    } else if (Integer.parseInt(res) == -2) {
                        //"trans_id ارسالي نامعتبر ميباشد"
                        Toast.makeText(getApplicationContext(), R.string.tellerror + "-2", Toast.LENGTH_SHORT).show();
                        prog.setVisibility(View.INVISIBLE);
                        btnverify.setVisibility(View.VISIBLE);
                    } else if (Integer.parseInt(res) == -3) {
                        //"شماره همراه ارسالي صحيح نميباشد"
                        Toast.makeText(getApplicationContext(), R.string.errorphonenumber, Toast.LENGTH_SHORT).show();
                        dialog.show();
                        prog.setVisibility(View.INVISIBLE);
                        btnverify.setVisibility(View.VISIBLE);
                    } else if (Integer.parseInt(res) == -4) {
                        //"چنين تراکنشي در سيستم موجود نيست و يا موفقيت آميز نبوده است"
                        Toast.makeText(getApplicationContext(), R.string.errorinpays, Toast.LENGTH_LONG).show();
                        prog.setVisibility(View.INVISIBLE);
                        btnverify.setVisibility(View.VISIBLE);
                    } else if (Integer.parseInt(res) == 0) {
                        //"خطا در ارتباط با فلاي نت"
                        Toast.makeText(getApplicationContext(), R.string.errorinconnect, Toast.LENGTH_SHORT).show();
                        prog.setVisibility(View.INVISIBLE);
                        btnverify.setVisibility(View.VISIBLE);
                    } else {

                        prog.setVisibility(View.INVISIBLE);
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

                new Processverifydaric().execute();
            } else {
                btnverify.setVisibility(View.VISIBLE);
                prog.setVisibility(View.INVISIBLE);
                Toast.makeText(verifydaric.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
