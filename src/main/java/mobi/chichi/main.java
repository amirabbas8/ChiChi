package mobi.chichi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;

public class main extends Activity {
int y,m,d;
    int year,month,day;
    Intent exdate,home,PhoneLogin,realnamelogin;
    String firstpage,realname;
    TextView hi;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
         y = 2016;
         m = 9;
         d = 15;

        Calendar c = Calendar.getInstance();
         year = c.get(Calendar.YEAR);
         month = c.get(Calendar.MONTH);
         day = c.get(Calendar.DAY_OF_MONTH);
         hi = (TextView) findViewById(R.id.hi);

         exdate = new Intent(getApplicationContext(), exdate.class);
        exdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

         home = new Intent(getApplicationContext(), MainActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

         PhoneLogin = new Intent(getApplicationContext(), phonelogin.class);
        PhoneLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

         realnamelogin = new Intent(getApplicationContext(), realnamelogin.class);
        realnamelogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
        HashMap<String, String> user;
        if (db1.getRowCount() > 0) {
            user = db1.getUserDetails();

            firstpage = user.get("firstpage");
            realname = user.get("realname");
            String hi_1= getString(R.string.hi);
            hi.setText(hi_1+realname );
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                public void run() {


                    if (year >= y) {
                        if (year > y) {

                            startActivity(exdate);

                            finish();
                        } else if (year == y) {
                            if (month > m) {
                                startActivity(exdate);

                                finish();
                            } else if (month == m) {

                                if (day >= d) {
                                    startActivity(exdate);

                                    finish();
                                } else {
                                    switch (firstpage) {
                                        case "home":

                                            startActivity(home);

                                            finish();
                                            break;
                                        case "realnamelogin":

                                            startActivity(realnamelogin);

                                            finish();
                                            break;
                                        default:

                                            startActivity(PhoneLogin);

                                            finish();
                                            break;
                                    }
                                }
                            } else {

                                switch (firstpage) {
                                    case "home":

                                        startActivity(home);

                                        finish();
                                        break;
                                    case "realnamelogin":

                                        startActivity(realnamelogin);

                                        finish();
                                        break;
                                    default:

                                        startActivity(PhoneLogin);

                                        finish();
                                        break;
                                }
                            }

                        }

                    } else if ("home".equals(firstpage)) {

                        startActivity(home);

                        finish();
                    } else if ("realnamelogin".equals(firstpage)) {

                        startActivity(realnamelogin);

                        finish();
                    } else {

                        startActivity(PhoneLogin);

                        finish();
                    }
                }
            }, 2000);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                public void run() {
                    startActivity(PhoneLogin);

                    finish();
                }
            }, 2000);

        }
    }
}
