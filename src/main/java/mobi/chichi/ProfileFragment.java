package mobi.chichi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobi.chichi.cropper.CropImageView;
import mobi.chichi.listviewfeed.adapter.FeedListAdapter;
import mobi.chichi.listviewfeed.app.AppController;
import mobi.chichi.listviewfeed.data.FeedItem;
import mobi.chichi.pulltoupdatelibrary.IonRefreshListener;
import mobi.chichi.pulltoupdatelibrary.PullToUpdateListView;

public class ProfileFragment extends Fragment {

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    ProgressDialog dialogProgress = null;
    String profileimage;
    Bitmap croppedImage;
    CropImageView cropImageView;
    File f;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
    private Uri mImageCaptureUri;
    private PullToUpdateListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private ProgressBar prog;
    private ImageButton refresh;
    private Button btnLoadMore;
    private String n = "1";
    private String pid = "0";
    private int pidint = 0;
    private NetworkImageView profilePic;
    private DatabaseHandler db1;
    private Dialog cropperdialog;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View rootView = inflater.inflate(R.layout.profile, container, false);
        btnLoadMore = new Button(getActivity());
        prog = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        //list
        listView = (PullToUpdateListView) rootView.findViewById(R.id.list);
        listView.setVisibility(View.INVISIBLE);
        feedItems = new ArrayList<>();

        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
        n = "1";
        new NetCheck().execute();
        listView = (PullToUpdateListView) rootView.findViewById(R.id.list);
        listView.setPullMode(PullToUpdateListView.MODE.UP_AND_DOWN);
        listView.setAutoLoad(true, 8);
        listView.setPullMessageColor(R.color.textColorPrimary);
        listView.setLoadingMessage(getResources().getString(R.string.loading));
        listView.setPullRotateImage(getResources().getDrawable(R.drawable.refresh_icon));

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

                }

            }

        });
        TextView myname = (TextView) rootView.findViewById(R.id.myname);
        TextView mybio = (TextView) rootView.findViewById(R.id.mybio);

        HashMap<String, String> user;
        db1 = new DatabaseHandler(getActivity());
        user = db1.getUserDetails();
        String realname = user.get("realname");
        String bio = user.get("bio");

        profileimage = user.get("profileimage");
        myname.setText(realname);
        mybio.setText(bio);

        String fontPath = "fonts/BYekan.ttf";
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), fontPath);
        myname.setTypeface(tf);
        mybio.setTypeface(tf);

        profilePic = (NetworkImageView) rootView.findViewById(R.id.profileimage1);
        profilePic.setImageUrl("http://chichi.mobi/profileimages/" + profileimage, imageLoader);

        //انتخاب عکس جدید برای پروفایل
        final String[] items = new String[]{getResources().getString(R.string.takecamera), getResources().getString(R.string.takegallery)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.selectpic);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) { //pick from camera
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                    try {
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else { //pick from file
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.completeaction)), PICK_FROM_FILE);
                }
            }
        });

        final AlertDialog dialog = builder.create();

        ImageView editprofileimage = (ImageView) rootView.findViewById(R.id.editprofileimage);
        editprofileimage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialog.show();
            }
        });
        refresh = (ImageButton) rootView.findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                refresh.setVisibility(View.INVISIBLE);
                prog.setVisibility(View.VISIBLE);
                n = "1";
                new NetCheck().execute();
            }
        });
        cropperdialog = new Dialog(getActivity());
        cropperdialog.setContentView(R.layout.cropper);
        // Initialize components of the app
        cropImageView = (CropImageView) cropperdialog.findViewById(R.id.CropImageView);
        //mImageCaptureUri
        // Sets initial aspect ratio to 10/10, for demonstration purposes
        cropImageView.setInitialAttributeValues(2, true, 10, 10);
        cropImageView.setGuidelines(2);
        ImageButton cropButton = (ImageButton) cropperdialog.findViewById(R.id.Button_crop);

        cropButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                croppedImage = cropImageView.getCroppedImage();
                profilePic.setImageBitmap(croppedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                croppedImage.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

//you can create a new file name "test.jpg" in sdcard folder.
                f = new File(Environment.getExternalStorageDirectory(), "chichi_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//write the bytes in file
                FileOutputStream fo = null;
                try {
                    fo = new FileOutputStream(f);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    assert fo != null;
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }

// remember close de FileOutput
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //   final File finalFile1 = new File(mImageCaptureUri.getPath());
                dialogProgress = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.uploadingfile), true);
                new Thread(new Runnable() {

                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {

                            public void run() {
                                Toast.makeText(getActivity(), getResources().getString(R.string.uploadingstart), Toast.LENGTH_SHORT).show();
                            }
                        });

                        uploadFile(f);

                    }
                }).start();

                cropperdialog.dismiss();
            }
        });
        ImageButton Buttoncancell = (ImageButton) cropperdialog.findViewById(R.id.Button_cancell);

        Buttoncancell.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mImageCaptureUri = null;
                cropperdialog.dismiss();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        getActivity();
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {

            case PICK_FROM_CAMERA:
                Bitmap PICK_FROM_CAMERA_bitmap = null;
                try {
                    PICK_FROM_CAMERA_bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageCaptureUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cropImageView.setImageBitmap(PICK_FROM_CAMERA_bitmap);
                cropImageView.setInitialAttributeValues(2, true, 10, 10);
                cropImageView.setGuidelines(2);
                cropperdialog.show();

                break;

            case PICK_FROM_FILE:
                mImageCaptureUri = data.getData();

                mImageCaptureUri = data.getData();
                Bitmap PICK_FROM_FILE_bitmap = null;
                try {
                    PICK_FROM_FILE_bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageCaptureUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cropImageView.setImageBitmap(PICK_FROM_FILE_bitmap);
                cropImageView.setInitialAttributeValues(2, true, 10, 10);
                cropImageView.setGuidelines(2);
                cropperdialog.show();

                break;

            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();

                if (extras != null) {
                    final File finalFile1 = new File(mImageCaptureUri.getPath());

                    System.out.println(mImageCaptureUri);

                    dialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.uploadingfile), true);
                    new Thread(new Runnable() {

                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {

                                public void run() {
                                    Toast.makeText(getActivity(), R.string.uploadingstart, Toast.LENGTH_SHORT).show();
                                }
                            });

                            uploadFile(finalFile1);

                        }
                    }).start();
                }

                File f = new File(mImageCaptureUri.getPath());

                if (f.exists()) {
                    f.delete();
                }

                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //uploadFile

    public int uploadFile(File finalFile) {

        final String fileName = finalFile.getName();
        HttpURLConnection conn;
        DataOutputStream dos;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        if (!finalFile.isFile()) {

            dialogProgress.dismiss();

            //   Log.e("uploadFile", "Source File not exist " );

            getActivity().runOnUiThread(new Runnable() {

                public void run() {

                    Toast.makeText(getActivity(), R.string.sourcefilenotexist, Toast.LENGTH_SHORT).show();

                    profilePic.setImageUrl("http://chichi.mobi/profileimages/" + profileimage, imageLoader);
                }
            });

            return 0;

        } else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(finalFile);
                /************ Php script path */
                String upLoadServerUri = "http://chichi.mobi/upload.php";
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                //  String serverResponseMessage = conn.getResponseMessage();

                //  Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    getActivity().runOnUiThread(new Runnable() {

                        public void run() {
                            dialogProgress.dismiss();
                            Toast.makeText(getActivity(), R.string.succes, Toast.LENGTH_SHORT).show();
                            HashMap<String, String> user;
                            user = db1.getUserDetails();
                            String idno = user.get("idno");
                            String realname = user.get("realname");
                            String email = user.get("email");
                            String firstpage = user.get("firstpage");
                            String bio = user.get("bio");
                            String phone = user.get("phone");
                            String npost = user.get("nposts");
                            String gender = user.get("gender");
                            String contrycode = user.get("contrycode");
                            String username = user.get("username");
                            String ndaric = user.get("ndaric");
                            profilePic.setImageUrl("http://chichi.mobi/profileimages/" + fileName, imageLoader);
                            db1.resetTables();
                            db1.addUser(idno, realname, email, firstpage, bio, phone, gender, contrycode, npost, fileName, username, ndaric);
                            File f23 = new File(mImageCaptureUri.getPath());
                            if (f23.exists()) {
                                f23.delete();
                            }
                            new Processeditprofileimage().execute();

                        }

                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialogProgress.dismiss();
                ex.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {

                    public void run() {
                        profilePic.setImageUrl("http://chichi.mobi/profileimages/" + profileimage, imageLoader);
                        Toast.makeText(getActivity(), "MalformedURLException Exception : check script url.", Toast.LENGTH_SHORT).show();
                    }
                });

                //     Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialogProgress.dismiss();
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
                        profilePic.setImageUrl("http://chichi.mobi/profileimages/" + profileimage, imageLoader);
                        Toast.makeText(getActivity(), R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });
                // Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block 
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    public void profilefragmentResult(String lastcomment, String pos1, String lastcommentname, String lastcommentuserid, String lastcommentprofilePic, String NComment, String c) {

        if ("".equals(lastcomment)) {

            int pos = Integer.parseInt(pos1);

            feedItems.get(pos).setNComment(NComment);
            listAdapter.notifyDataSetChanged();
        } else if ("1".equals(c)) {

        } else if ("2".equals(c)) {

            int pos = Integer.parseInt(pos1);

            feedItems.get(pos).setLastCommentName(lastcommentname);
            feedItems.get(pos).setLastCommentUserId(lastcommentuserid);
            feedItems.get(pos).setLastCommentProfilePic(lastcommentprofilePic);
            feedItems.get(pos).setLastComment(lastcomment);
            feedItems.get(pos).setNComment(NComment);
            listAdapter.notifyDataSetChanged();

        }
    }

    private class Processeditprofileimage extends AsyncTask<String, String, JSONObject> {

        String idno, profileimage;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            HashMap<String, String> user;
            user = db1.getUserDetails();
            idno = user.get("idno");
            profileimage = user.get("profileimage");

        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.editprofileimage(idno, profileimage);
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                String KEY_SUCCESS = "success";
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {

                        JSONObject json_user = json.getJSONObject("user");
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getActivity());
                        String KEY_username = "username";
                        String KEY_ID = "id";
                        String KEY_EMAIL = "email";
                        String KEY_BIO = "bio";
                        String KEY_GENDER = "gender";
                        String KEY_ndaric = "ndaric";
                        String KEY_nposts = "nposts";
                        String KEY_profileimage = "profileimages";
                        String KEY_REALNAME = "realname";
                        String KEY_PHONE = "phone";
                        String KEY_CONTRYCODE = "contrycode";

                        db1.addUser(json_user.getString(KEY_ID), json_user.getString(KEY_REALNAME), json_user.getString(KEY_EMAIL)
                                , "home", json_user.getString(KEY_BIO), json_user.getString(KEY_PHONE),
                                json_user.getString(KEY_GENDER), json_user.getString(KEY_CONTRYCODE),
                                json_user.getString(KEY_nposts), json_user.getString(KEY_profileimage),
                                json_user.getString(KEY_username), json_user.getString(KEY_ndaric));
                        MainActivity.reloadprofilepic();
                        Toast.makeText(getActivity(), R.string.succes, Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(getActivity(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {

                    public void run() {
                        prog.setVisibility(View.INVISIBLE);
                        refresh.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });
                //    Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);
            }
        }
    }

    private class Processgetprofilelist extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.getprofilelist(idno, idno);
        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.isNull("profileposts")) {
                    n = "1";


                    new NetCheck().execute();
                } else {
                    JSONArray feedArray = json.getJSONArray("profileposts");
                    listView.setVisibility(View.INVISIBLE);
                    feedItems.clear();
                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        listView.setVisibility(View.VISIBLE);
                        FeedItem item = new FeedItem();
                        item.setId(feedObj.getInt("id"));
                        item.setName(feedObj.getString("name"));
                        item.setUserId(feedObj.getString("userid"));
                        String image = feedObj.isNull("image") ? null : "http://chichi.mobi/postsimages/" + feedObj.getString("image");
                        // Image might be null sometimes
                        if (("http://chichi.mobi/postsimages/").equals(image)) {
                            image = null;

                        }
                        item.setImage(image);
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
                        item.setfragment("profile");
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
                    btnLoadMore.setText(R.string.loadmore);
                    btnLoadMore.setBackgroundResource(R.drawable.button_white);
                    // Adding Load More button to lisview at bottom
                    listView.addFooterView(btnLoadMore);
                    // notify data changes to list adapater
                    listAdapter.notifyDataSetChanged();
                    // Getting adapter
                    listAdapter = new FeedListAdapter(getActivity(), feedItems);
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

                            btnLoadMore.setVisibility(View.INVISIBLE);
                            n = "2";
                            new NetCheck().execute();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

                dialog.dismiss();
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
                        Toast.makeText(getActivity(), R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });
                //    Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);
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
                switch (n) {
                    case "1":

                        new Processgetprofilelist().execute();
                        break;
                    case "2":
                        new Processgetprofilelistloadmore().execute();
                        break;

                }

            } else {
                Toast.makeText(getActivity(), R.string.errorconection, Toast.LENGTH_SHORT).show();
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
            }
        }
    }

    private class Processgetprofilelistloadmore extends AsyncTask<String, String, JSONObject> {

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
            return userFunction.getprofilelistloadmore(idno, idno, pid);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.isNull("profileposts")) {

                    btnLoadMore.setVisibility(View.INVISIBLE);
                    n = "2";
                    new NetCheck().execute();
                } else {

                    JSONArray feedArray = json.getJSONArray("profileposts");

                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);

                        FeedItem item = new FeedItem();
                        item.setId(feedObj.getInt("id"));
                        item.setName(feedObj.getString("name"));
                        item.setUserId(feedObj.getString("userid"));
                        // Image might be null sometimes
                        String image = "http://chichi.mobi/postsimages/" + feedObj.getString("image");
                        if (("http://chichi.mobi/postsimages/").equals(image)) {
                            image = null;

                        }
                        item.setImage(image);
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
                        item.setfragment("profile");
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
                        Toast.makeText(getActivity(), R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }
    public void profileneveshtanedit(String id, String userid, String text, String url, String profilepic, String name, String imagename, String TimeStamp, String pos1) {
        int pos = Integer.parseInt(pos1);
        feedItems.get(pos).setId(Integer.parseInt(id));
        feedItems.get(pos).setName(name);
        feedItems.get(pos).setUserId(userid);
        // Image might be null sometimes
        String image = "http://chichi.mobi/postsimages/" + imagename;
        if (("http://chichi.mobi/postsimages/").equals(image)) {
            image = null;
        }
        feedItems.get(pos).setImage(image);
        feedItems.get(pos).setStatus(text);
        feedItems.get(pos).setProfilePic(profilepic);
        feedItems.get(pos).setTimeStamp(TimeStamp);
        feedItems.get(pos).setUrl(url);
        feedItems.get(pos).setoprog("2");
        feedItems.get(pos).setlprog("2");
        feedItems.get(pos).setfragment("profile");
        listAdapter.notifyDataSetChanged();
    }
}
