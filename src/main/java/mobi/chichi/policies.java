package mobi.chichi;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;


public class policies extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.policies);
        TextView policies = (TextView) findViewById(R.id.policies);
        policies.setMovementMethod(new ScrollingMovementMethod());
        String fontPath = "fonts/BYekan.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        policies.setTypeface(tf);


    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }


}
