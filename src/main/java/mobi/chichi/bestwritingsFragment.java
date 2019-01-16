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

import mobi.chichi.listviewfeed.adapter.FeedListAdapter_writings;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.pulltoupdatelibrary.IonRefreshListener;
import mobi.chichi.pulltoupdatelibrary.PullToUpdateListView;

import static mobi.chichi.R.color;
import static mobi.chichi.R.drawable;
import static mobi.chichi.R.id;
import static mobi.chichi.R.string;


public class bestwritingsFragment extends Fragment {
	private PullToUpdateListView listView;
	private FeedListAdapter_writings listAdapter;
	private List<FeedItem> feedItems;
	private Button btnLoadMore;
	private String n = "1";
	private String pid = "0";
	private int pidint = 0;

	private DatabaseHandler db1;
	private ProgressBar prog;

	private ImageButton refresh;

	public bestwritingsFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_bestpeople, container, false);
		db1 = new DatabaseHandler(getActivity());
		// Inflate the layout for this fragment

		btnLoadMore = new Button(getActivity());
		prog = (ProgressBar) rootView.findViewById(id.progressBar1);
		refresh = (ImageButton) rootView.findViewById(id.refresh);

		refresh.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				refresh.setVisibility(View.INVISIBLE);
				prog.setVisibility(View.VISIBLE);
				n = "1";
				new NetCheck().execute();
			}
		});


		listView = (PullToUpdateListView) rootView.findViewById(id.list);
		listView.setPullMode(PullToUpdateListView.MODE.UP_AND_DOWN);
		listView.setAutoLoad(true, 8);
		listView.setPullMessageColor(color.textColorPrimary);
		listView.setLoadingMessage(getResources().getString(string.loading));
		listView.setPullRotateImage(getResources().getDrawable(drawable.refresh_icon));

		listView.setOnRefreshListener(new IonRefreshListener() {
			@Override
			public void onRefreshUp() {
				n = "1";

				new NetCheck().execute();

			}

			@Override
			public void onRefeshDown() {
				if (9 < feedItems.size()) {
					listView.setVisibility(View.VISIBLE);
					btnLoadMore.setVisibility(View.INVISIBLE);
					n = "2";
					new NetCheck().execute();
					Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();

				}

			}

		});
		listView.setVisibility(View.INVISIBLE);
		feedItems = new ArrayList<>();
		listAdapter = new FeedListAdapter_writings(getActivity(), feedItems);
		listView.setAdapter(listAdapter);
		n = "1";
		new NetCheck().execute();


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




	private class Processgetbestwritingslist extends AsyncTask<String, String, JSONObject> {

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
			return userFunction.getbestwritingslist(idno);
		}


		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				if (json.isNull("writingsposts")) {
					n = "1";


					new NetCheck().execute();
				} else {
					JSONArray feedArray = json.getJSONArray("writingsposts");
					listView.setVisibility(View.INVISIBLE);
					listView.removeFooterView(btnLoadMore);
					feedItems.clear();
					for (int i = 0; i < feedArray.length(); i++) {
						JSONObject feedObj = (JSONObject) feedArray.get(i);

						FeedItem item = new FeedItem();
						item.setId(feedObj.getInt("id"));
						item.setName(feedObj.getString("name"));
						item.setUserId(feedObj.getString("userid"));
						item.setStatus(feedObj.getString("status"));
						item.setProfilePic(feedObj.getString("profilePic"));
						item.setNLike(feedObj.getString("nlike"));
						item.setMylike(feedObj.getString("mylike"));
						item.setoprog("2");
						item.setlprog("2");
						item.setfragment("bestwritings");
						pid = String.valueOf(feedObj.getInt("id"));
						pidint = pidint + 1;
						feedItems.add(item);
					}
					if (0 == feedItems.size()) {
						refresh.setVisibility(View.VISIBLE);
						prog.setVisibility(View.INVISIBLE);

					} else {
						refresh.setVisibility(View.GONE);
						prog.setVisibility(View.GONE);
					}
					if (n.equals("1")) {

						listView.onRefreshUpComplete();
					} else if (n.equals("2")) {
						btnLoadMore.setVisibility(View.VISIBLE);
						listView.onRefreshDownComplete(null);
					}

					listView.setVisibility(View.VISIBLE);
					btnLoadMore.setText("Load More");
					btnLoadMore.setBackgroundResource(drawable.button_white);
					// Adding Load More button to lisview at bottom
					listView.addFooterView(btnLoadMore);
					// notify data changes to list adapater
					listAdapter.notifyDataSetChanged();
					// Getting adapter
					listAdapter = new FeedListAdapter_writings(getActivity(), feedItems);
					listView.setAdapter(listAdapter);

					if (pidint >= 10) {
						btnLoadMore.setVisibility(View.VISIBLE);
						pidint = 0;
					} else {
						btnLoadMore.setVisibility(View.GONE);
						pidint = 0;
					}
					btnLoadMore.setOnClickListener(new View.OnClickListener() {

						public void onClick(View arg0) {
							listView.setVisibility(View.VISIBLE);
							btnLoadMore.setVisibility(View.INVISIBLE);
							n = "2";
							new NetCheck().execute();
						}
					});

				}
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
						if (n.equals("1")) {

							listView.onRefreshUpComplete();
						} else if (n.equals("2")) {
							btnLoadMore.setVisibility(View.VISIBLE);
							listView.onRefreshDownComplete(null);
						}
						Toast.makeText(getActivity(), string.errorconection, Toast.LENGTH_SHORT).show();
					}
				});

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

					new Processgetbestwritingslist().execute();
				} else if (n.equals("2")) {
					new Processgetbestwritingslistloadmore().execute();
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

					listView.onRefreshUpComplete();
				} else if (n.equals("2")) {
					btnLoadMore.setVisibility(View.VISIBLE);
					listView.onRefreshDownComplete(null);
				}
				Toast.makeText(getActivity(), string.errorconection, Toast.LENGTH_SHORT).show();


			}
		}
	}

	private class Processgetbestwritingslistloadmore extends AsyncTask<String, String, JSONObject> {

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
			return userFunction.getbestwritingslistloadmore(idno, pid);
		}


		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				if (json.isNull("writingsposts")) {
					listView.setVisibility(View.VISIBLE);
					btnLoadMore.setVisibility(View.INVISIBLE);
					n = "2";
					new NetCheck().execute();
				} else {
					JSONArray feedArray = json.getJSONArray("writingsposts");

					for (int i = 0; i < feedArray.length(); i++) {
						JSONObject feedObj = (JSONObject) feedArray.get(i);

						FeedItem item = new FeedItem();
						item.setId(feedObj.getInt("id"));
						item.setName(feedObj.getString("name"));
						item.setUserId(feedObj.getString("userid"));
						item.setStatus(feedObj.getString("status"));
						item.setProfilePic(feedObj.getString("profilePic"));
						item.setTimeStamp(feedObj.getString("timeStamp"));

						// url might be null sometimes
						String feedUrl = feedObj.isNull("url") ? null : feedObj
								.getString("url");
						item.setUrl(feedUrl);
						item.setLastCommentName(feedObj.getString("lastcommentname"));
						item.setLastCommentUserId(feedObj.getString("lastcommentuserid"));
						item.setLastCommentProfilePic(feedObj.getString("lastcommentprofilePic"));
						item.setLastComment(feedObj.getString("lastcomment"));
						item.setNLike(feedObj.getString("nlike"));
						item.setNComment(feedObj.getString("ncomment"));
						item.setMylike(feedObj.getString("mylike"));
						item.setoprog("2");
						item.setlprog("2");
						item.setfragment("writings");
						pid = String.valueOf(feedObj.getInt("id"));
						pidint = pidint + 1;
						feedItems.add(item);
					}
					listView.setVisibility(View.VISIBLE);
					if (0 == feedItems.size()) {
						refresh.setVisibility(View.VISIBLE);
						prog.setVisibility(View.INVISIBLE);

					} else {
						refresh.setVisibility(View.GONE);
						prog.setVisibility(View.GONE);
					}
					if (n.equals("1")) {

						listView.onRefreshUpComplete();
					} else if (n.equals("2")) {
						btnLoadMore.setVisibility(View.VISIBLE);
						listView.onRefreshDownComplete(null);
					}
					// notify data changes to list adapater
					listAdapter.notifyDataSetChanged();
					if (pidint >= 10) {
						btnLoadMore.setVisibility(View.VISIBLE);
						pidint = 0;
					} else {
						btnLoadMore.setVisibility(View.GONE);
						pidint = 0;
					}
				}
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
						if (n.equals("1")) {

							listView.onRefreshUpComplete();
						} else if (n.equals("2")) {
							btnLoadMore.setVisibility(View.VISIBLE);
							listView.onRefreshDownComplete(null);
						}
						Toast.makeText(getActivity(), string.errorconection, Toast.LENGTH_SHORT).show();
					}
				});

			}
		}
	}
}

