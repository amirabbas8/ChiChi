package mobi.chichi;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

public class writings_best extends FragmentActivity
	 {

	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writings_best);

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		TabsPagerAdapter mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);


		Button bestpeople = (Button) findViewById(R.id.bestpeople);

		bestpeople.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				viewPager.setCurrentItem(1);
			}
		});
		Button bestwritings = (Button) findViewById(R.id.bestwritings);

		bestwritings.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				viewPager.setCurrentItem(0);
			}
		});
		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}



}
