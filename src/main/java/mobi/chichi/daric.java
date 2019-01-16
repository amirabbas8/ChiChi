package mobi.chichi;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;


public class daric extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daric);
        TextView whatsdaric = (TextView) findViewById(R.id.whatsdaric);
        whatsdaric.setMovementMethod(new ScrollingMovementMethod());
        String fontPath = "fonts/BYekan.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        whatsdaric.setTypeface(tf);
        TextView ndaric = (TextView) findViewById(R.id.ndaric);
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        HashMap<String, String> user;
        user = db.getUserDetails();
        String ndaric0 = user.get("ndaric");
        ndaric.setText(ndaric0);

        //back
        ImageButton back = (ImageButton) findViewById(R.id.bazgasht);

        back.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                finish();
            }
        });
        //plus
        ImageButton plus = (ImageButton) findViewById(R.id.plus);

        plus.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(daric.this, buydaric.class);

                startActivityForResult(intent, 13);
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


    @Override
    public void onResume() {
        TextView ndaric = (TextView) findViewById(R.id.ndaric);
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        HashMap<String, String> user;
        user = db.getUserDetails();
        String ndaric0 = user.get("ndaric");
        ndaric.setText(ndaric0);
        super.onResume();
    }
}
