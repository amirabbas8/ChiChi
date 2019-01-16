package mobi.chichi;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobi.chichi.listviewfeed.adapter.FeedListAdapterpbff;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.pulltoupdatelibrary.IonRefreshListener;
import mobi.chichi.pulltoupdatelibrary.PullToUpdateListView;

import static mobi.chichi.R.color;
import static mobi.chichi.R.drawable;
import static mobi.chichi.R.id;
import static mobi.chichi.R.string;


public class bestpeopleFragment extends Fragment {
	private PullToUpdateListView listView12;
	private FeedListAdapterpbff listAdapter12;
	private List<FeedItem> feedItems;
	private Button btnLoadMore;
	private String n = "1";

	private DatabaseHandler db1;
	private ProgressBar prog;

	private ImageButton refresh;

	public bestpeopleFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_bestpeople, container, false);
		db1 = new DatabaseHandler(getActivity());
		btnLoadMore = new Button(getActivity());
		listView12 = (PullToUpdateListView) rootView.findViewById(id.list);
		listView12.setVisibility(View.INVISIBLE);
		feedItems = new ArrayList<>();

		listAdapter12 = new FeedListAdapterpbff(getActivity(), feedItems);
		listView12.setAdapter(listAdapter12);
		n = "1";
		new newNetCheck().execute();
		listView12.setPullMode(PullToUpdateListView.MODE.UP_AND_DOWN);
		listView12.setAutoLoad(true, 8);
		listView12.setPullMessageColor(color.textColorPrimary);
		listView12.setLoadingMessage(getResources().getString(string.loading));
		listView12.setPullRotateImage(getResources().getDrawable(drawable.refresh_icon));
		prog = (ProgressBar) rootView.findViewById(id.progressBar1);

		refresh = (ImageButton) rootView.findViewById(id.refresh);

		refresh.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				refresh.setVisibility(View.INVISIBLE);
				prog.setVisibility(View.VISIBLE);
				n = "1";
				new newNetCheck().execute();
			}
		});
		listView12.setOnRefreshListener(new IonRefreshListener() {
			@Override
			public void onRefreshUp() {
				n = "1";
				new newNetCheck().execute();
			}

			@Override
			public void onRefeshDown() {
				if (9 < feedItems.size()) {

				}
			}

		});
		return rootView;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

		}
	}


	private class Processget_best_people_writings_list extends AsyncTask<String, String, JSONObject> {

		String idno;


		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			HashMap<String, String> user;
			user = db1.getUserDetails();
			idno = user.get("idno");

		}


		@Override
		protected JSONObject doInBackground(String... args) {

			UserFunctions userFunction = new UserFunctions();
			return userFunction.getbestpeoplelist(idno);
		}


		@Override
		protected void onPostExecute(JSONObject json) {

			try {

				JSONArray feedArray = json.getJSONArray("writingspeople");
				listView12.setVisibility(View.INVISIBLE);
				feedItems.clear();
				for (int i = 0; i < feedArray.length(); i++) {
					JSONObject feedObj = (JSONObject) feedArray.get(i);

					FeedItem item = new FeedItem();
					item.setId(feedObj.getInt("id"));
					item.setName(feedObj.getString("name"));
					item.setUserId(feedObj.getString("userid"));
					item.setProfilePic(feedObj.getString("profilePic"));
					item.setoprog("2");
					feedItems.add(item);

				}
				listView12.setVisibility(View.VISIBLE);
				// notify data changes to list adapater
				listAdapter12.notifyDataSetChanged();
				if (0 == feedItems.size()) {
					refresh.setVisibility(View.VISIBLE);
					prog.setVisibility(View.INVISIBLE);

				} else {
					refresh.setVisibility(View.GONE);
					prog.setVisibility(View.GONE);
				}
				listView12.onRefreshUpComplete();
				listView12.onRefreshDownComplete(null);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {

				e.printStackTrace();

				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						if (0 == feedItems.size()) {
							refresh.setVisibility(View.VISIBLE);
							prog.setVisibility(View.INVISIBLE);

						} else {
							refresh.setVisibility(View.GONE);
							prog.setVisibility(View.GONE);
						}

						listView12.onRefreshUpComplete();
						listView12.onRefreshDownComplete(null);
						Toast.makeText(getActivity(), string.errorconection, Toast.LENGTH_SHORT).show();
					}
				});

			}
		}
	}

	private class newNetCheck extends AsyncTask<String, String, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}


		@Override
		protected Boolean doInBackground(String... args) {

			/** * Gets current device state and checks for working internet connection by trying Google. **/

			ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
				if (n.equals("1")) {

					new Processget_best_people_writings_list().execute();
				}

			} else {
				if (0 == feedItems.size()) {
					refresh.setVisibility(View.VISIBLE);
					prog.setVisibility(View.INVISIBLE);

				} else {
					refresh.setVisibility(View.GONE);
					prog.setVisibility(View.GONE);
				}
				if (n.equals("1")) {

					listView12.onRefreshUpComplete();
				} else if (n.equals("2")) {
					btnLoadMore.setVisibility(View.VISIBLE);
					listView12.onRefreshDownComplete(null);
				}
				Toast.makeText(getActivity(), string.errorconection, Toast.LENGTH_SHORT).show();


			}
		}
	}
}

