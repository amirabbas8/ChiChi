package mobi.chichi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import mobi.chichi.listviewfeed.adapter.settingDrawerListAdapter;
import mobi.chichi.listviewfeed.data.settingDrawerItem;


public class settingFragment extends Fragment {
    private DatabaseHandler db1;


    private ProgressDialog pDialog;
    private Dialog dialog;


    public settingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings, container, false);
        db1 = new DatabaseHandler(getActivity());
        String[] settingMenuTitles = getResources().getStringArray(R.array.setting_drawer_items);

        // setting drawer icons from resources
        TypedArray settingMenuIcons = getResources()
                .obtainTypedArray(R.array.setting_drawer_icons);
        ListView settingList = (ListView) rootView.findViewById(R.id.list_setting);

        ArrayList<settingDrawerItem> settingDrawerItems = new ArrayList<>();

        // adding setting drawer items to array
        settingDrawerItems.add(new settingDrawerItem(settingMenuTitles[0]));
        settingDrawerItems.add(new settingDrawerItem(settingMenuTitles[1]));
        settingDrawerItems.add(new settingDrawerItem(settingMenuTitles[2]));
        settingDrawerItems.add(new settingDrawerItem(settingMenuTitles[3]));
        settingDrawerItems.add(new settingDrawerItem(settingMenuTitles[4]));
        settingDrawerItems.add(new settingDrawerItem(settingMenuTitles[5]));
        settingDrawerItems.add(new settingDrawerItem(settingMenuTitles[6]));
        // Recycle the typed array
        settingMenuIcons.recycle();

        // setting the setting drawer list adapter
        settingDrawerListAdapter adapter = new settingDrawerListAdapter(getActivity(), settingDrawerItems);

        settingList.setAdapter(adapter);
        //00000000000000000000000000000000000000000
        settingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                displayViewsettings(position);
            }
        });
        pDialog = new ProgressDialog(getActivity());
        return rootView;
    }

    private void displayViewsettings(int position) {

        switch (position) {

            case 0://manageblock
                Intent upanel = new Intent(getActivity(), manageblock.class);
                upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upanel);
                break;
            case 1://daric
                pDialog.setMessage(getResources().getString(R.string.loading));
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
                new NetCheck().execute();
                break;
            case 2://advertise
                Intent upanel2 = new Intent(getActivity(), advertise.class);
                upanel2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upanel2);
                break;
            case 3://about
                Intent upanel3 = new Intent(getActivity(), about.class);
                upanel3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upanel3);
                break;
            case 4://support
                Intent upanel4 = new Intent(getActivity(), support.class);
                upanel4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upanel4);
                break;
            case 5://policies
                Intent upanel5 = new Intent(getActivity(), policies.class);
                upanel5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upanel5);
                break;
            case 6://logout
                db1 = new DatabaseHandler(getActivity());
                dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog);
                TextView textview = (TextView) dialog.findViewById(R.id.text);
                textview.setText(R.string.logouterror);

                dialog.setTitle(R.string.logout);
                Button yes = (Button) dialog.findViewById(R.id.yes);
                Button no = (Button) dialog.findViewById(R.id.no);

                yes.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        //    MainActivity.firsty();
                        db1.resetTables();
                        GCMRegistrar.unregister(getActivity());
                        GCMRegistrar.setRegisteredOnServer(getActivity(),false);
                        Intent upanel = new Intent(getActivity(), main.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(upanel);
                        getActivity().finish();
                        dialog.dismiss();

                    }
                });
                no.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                dialog.show();
                break;

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

                new Processgetdata().execute();

            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity(), R.string.errorconection, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private class Processgetdata extends AsyncTask<String, String, JSONObject> {

        String id;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            HashMap<String, String> user;
            user = db1.getUserDetails();
            id = user.get("idno");

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            return userFunction.getdata(id);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                String KEY_SUCCESS = "success";
                String KEY_ID = "id";
                String KEY_REALNAME = "realname";
                String KEY_EMAIL = "email";
                String KEY_BIO = "bio";
                String KEY_PHONE = "phone";
                String KEY_CONTRYCODE = "contrycode";
                String KEY_GENDER = "gender";
                String KEY_nposts = "nposts";
                String KEY_profileimage = "profileimages";
                String KEY_username = "username";
                String KEY_ndaric = "ndaric";

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        pDialog.dismiss();

                        JSONObject json_user = json.getJSONObject("user");
                        /**
                         * Clear all previous data in SQlite database.
                         **/
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getActivity());
                        db1.addUser(json_user.getString(KEY_ID), json_user.getString(KEY_REALNAME), json_user.getString(KEY_EMAIL), "home", json_user.getString(KEY_BIO)
                                , json_user.getString(KEY_PHONE), json_user.getString(KEY_GENDER)
                                , json_user.getString(KEY_CONTRYCODE), json_user.getString(KEY_nposts)
                                , json_user.getString(KEY_profileimage), json_user.getString(KEY_username)
                                , json_user.getString(KEY_ndaric));

                        Intent upanel = new Intent(getActivity(), daric.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(upanel);

                    } else {
                        pDialog.dismiss();
                        Toast.makeText(getActivity(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
