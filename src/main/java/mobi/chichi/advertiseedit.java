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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;

import mobi.chichi.cropper.CropImageView;
import mobi.chichi.imageloader.ImageLoader;


public class advertiseedit extends Activity {

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 3;
    private static String KEY_SUCCESS = "success";
    EditText inputtext, inputlink;
    ProgressBar nprog;
    ImageButton btnsend, btnback;
    Button btnselect;
    Button btncancel;
    String imagename = "";
    int serverResponseCode = 0;
    ProgressDialog dialogProgress = null;
    Dialog dialog1;
    /**
     * ********** Php script path ***************
     */
    String upLoadServerUri = "http://chichi.mobi/uploadimageadvertise.php";
    Button yes;
    Button no;
    String id;
    int n;
    String advertiseid, advertisename, advertiseimage, advertiselink, pos;
    Bitmap croppedImage;
    CropImageView cropImageView;
    File f;
    private ImageView mImageView;
    private Uri mImageCaptureUri;
    private Dialog cropperdialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertiseplus);

        btnsend = (ImageButton) findViewById(R.id.send);
        btnback = (ImageButton) findViewById(R.id.back);
        btnselect = (Button) findViewById(R.id.select);
        btncancel = (Button) findViewById(R.id.cancell);
        nprog = (ProgressBar) findViewById(R.id.progressBar1);
        inputtext = (EditText) findViewById(R.id.text);
        inputlink = (EditText) findViewById(R.id.link);
        mImageView = (ImageView) findViewById(R.id.uploadimage);
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Bundle bundle = getIntent().getExtras();
        advertiseid = bundle.getString("advertiseid");
        advertisename = bundle.getString("advertisename");
        advertiseimage = bundle.getString("advertiseimage");
        imagename = advertiseimage;
        advertiselink = bundle.getString("advertiselink");
        pos = bundle.getString("pos");
        inputtext.setText(advertisename);
        inputlink.setText(advertiselink);
        int loader = R.drawable.camera_icon;
        ImageLoader imgLoader = new ImageLoader(getApplicationContext());

        imgLoader.DisplayImage("http://chichi.mobi/advertisement/" + advertiseimage, loader, mImageView);
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else {
                // Handle other intents, such as being started from the home screen
            }
        }
        btnsend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                btnsend.setVisibility(View.INVISIBLE);
                nprog.setVisibility(View.VISIBLE);
                if (inputtext.getText().toString().length() < 1 || imagename.equals("") || inputlink.getText().toString().length() < 1) {

                    Toast.makeText(getApplicationContext(), R.string.erroradvertiseinput, Toast.LENGTH_SHORT).show();
                    btnsend.setVisibility(View.VISIBLE);
                    nprog.setVisibility(View.INVISIBLE);

                } else {
                    n = 1;
                    new NetCheck().execute();
                }
            }

        });
        dialog1 = new Dialog(advertiseedit.this);
        dialog1.setContentView(R.layout.dialog);
        dialog1.setTitle(R.string.check);
        TextView text = (TextView) dialog1.findViewById(R.id.text);
        text.setText(R.string.checkadvertise);
        btncancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                imagename = "";
                btncancel.setVisibility(View.INVISIBLE);
                mImageView.setImageResource(R.drawable.camera_icon);
                btnselect.setText(R.string.selectpic);

                btnselect.setClickable(true);

                inputtext.setHint(R.string.text);
            }

        });
        btnback.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                finish();

            }

        });
        //111111111

        final String[] items = new String[]{getResources().getString(R.string.takecamera), getResources().getString(R.string.takegallery)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
        btnselect.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                dialog.show();

            }
        });
        cropperdialog = new Dialog(advertiseedit.this);
        cropperdialog.setContentView(R.layout.cropper);
        // Initialize components of the app
        cropImageView = (CropImageView) cropperdialog.findViewById(R.id.CropImageView);
        //mImageCaptureUri
        // Sets initial aspect ratio to 10/10, for demonstration purposes
        cropImageView.setInitialAttributeValues(2, true, 19, 3);
        cropImageView.setGuidelines(2);
        ImageButton cropButton = (ImageButton) cropperdialog.findViewById(R.id.Button_crop);

        cropButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                croppedImage = cropImageView.getCroppedImage();
                mImageView.setImageBitmap(croppedImage);
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
                dialogProgress = ProgressDialog.show(advertiseedit.this, "", getResources().getString(R.string.uploadingfile), true);
                new Thread(new Runnable() {

                    public void run() {
                        runOnUiThread(new Runnable() {

                            public void run() {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.uploadingstart), Toast.LENGTH_SHORT).show();
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

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {

            case PICK_FROM_CAMERA:
                Bitmap PICK_FROM_CAMERA_bitmap = null;
                try {
                    PICK_FROM_CAMERA_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cropImageView.setImageBitmap(PICK_FROM_CAMERA_bitmap);
                cropImageView.setInitialAttributeValues(2, true, 19, 3);
                cropImageView.setGuidelines(2);
                cropperdialog.show();
                break;

            case PICK_FROM_FILE:
                mImageCaptureUri = data.getData();
                Bitmap PICK_FROM_FILE_bitmap = null;
                try {
                    PICK_FROM_FILE_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cropImageView.setImageBitmap(PICK_FROM_FILE_bitmap);
                cropImageView.setInitialAttributeValues(2, true, 19, 3);
                cropImageView.setGuidelines(2);
                cropperdialog.show();


                break;

        }

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

            // Log.e("uploadFile", "Source File not exist ");

            runOnUiThread(new Runnable() {

                public void run() {
                    mImageView.setImageResource(R.drawable.camera_icon);
                    Toast.makeText(getApplicationContext(), R.string.sourcefilenotexist, Toast.LENGTH_SHORT).show();
                }
            });

            return 0;

        } else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(finalFile);
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
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {

                        public void run() {
                            dialogProgress.dismiss();
                            Toast.makeText(advertiseedit.this, R.string.succes, Toast.LENGTH_SHORT).show();
                            imagename = fileName;
                            btncancel.setVisibility(View.VISIBLE);
                            btnselect.setText(R.string.selected);
                            btnselect.setClickable(false);
                            File f23 = new File(mImageCaptureUri.getPath());

                            if (f23.exists())
                                f23.delete();
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

                runOnUiThread(new Runnable() {

                    public void run() {
                        mImageView.setImageResource(R.drawable.camera_icon);
                        Toast.makeText(advertiseedit.this, "MalformedURLException Exception : check script url.", Toast.LENGTH_SHORT).show();
                    }
                });

                //    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialogProgress.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        mImageView.setImageResource(R.drawable.camera_icon);
                        btnsend.setVisibility(View.VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
                        Toast.makeText(advertiseedit.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });
                //    Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
            }

            dialogProgress.dismiss();
            return serverResponseCode;

        } // End else block 
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            inputtext.setText(sharedText);
            // Update UI to reflect text being shared
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

    /**
     * Async Task to get and send data to My Sql database through JSON respone.
     */
    private class Processadvertiseedit extends AsyncTask<String, String, JSONObject> {

        String userid, name, link, status, startdate, enddate;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            inputtext = (EditText) findViewById(R.id.text);
            name = inputtext.getText().toString();
            link = inputlink.getText().toString();
            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

            HashMap<String, String> user;
            user = db1.getUserDetails();
            userid = user.get("idno");
            yes = (Button) dialog1.findViewById(R.id.yes);
            no = (Button) dialog1.findViewById(R.id.no);
            yes.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    dialog1.dismiss();
                    nprog.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), R.string.wait, Toast.LENGTH_SHORT).show();
                    n = 2;
                    new NetCheck().execute();

                }
            });
            no.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("userid", userid);
                    intent.putExtra("name", name);
                    intent.putExtra("imagename", imagename);
                    intent.putExtra("pos", pos);
                    intent.putExtra("id", id);
                    intent.putExtra("status", status);
                    intent.putExtra("link", link);
                    intent.putExtra("startdate", startdate);
                    intent.putExtra("enddate", enddate);
                    setResult(RESULT_OK, intent);
                    dialog1.dismiss();
                    finish();

                }
            });
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.advertiseedit(userid, advertiseid, name, imagename, link);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        JSONObject json_post = json.getJSONObject("post");
                        nprog.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.succes, Toast.LENGTH_SHORT).show();
                        dialog1.show();
                        id = json_post.getString("id");
                        status = json_post.getString("status");
                        startdate = json_post.getString("startdate");
                        enddate = json_post.getString("enddate");

                    } else {
                        btnsend.setVisibility(View.VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Processadvertisecheck extends AsyncTask<String, String, JSONObject> {

        String userid, name, link, status, startdate, enddate;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inputtext = (EditText) findViewById(R.id.text);
            name = inputtext.getText().toString();
            link = inputlink.getText().toString();
            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

            HashMap<String, String> user;
            user = db1.getUserDetails();
            userid = user.get("idno");
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.advertisecheck(id);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        JSONObject json_post = json.getJSONObject("post");
                        nprog.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.succes, Toast.LENGTH_SHORT).show();

                        id = json_post.getString("id");
                        status = json_post.getString("status");
                        startdate = json_post.getString("startdate");
                        enddate = json_post.getString("enddate");
                        Intent intent = new Intent();
                        intent.putExtra("pos", pos);
                        intent.putExtra("userid", userid);
                        intent.putExtra("name", name);
                        intent.putExtra("imagename", imagename);
                        intent.putExtra("id", id);
                        intent.putExtra("status", status);
                        intent.putExtra("link", link);
                        intent.putExtra("startdate", startdate);
                        intent.putExtra("enddate", enddate);
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {
                        btnsend.setVisibility(View.VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.errorproblem, Toast.LENGTH_SHORT).show();
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
                if (n == 1) {
                    new Processadvertiseedit().execute();
                } else if (n == 2) {
                    new Processadvertisecheck().execute();
                }
            } else {
                btnsend.setVisibility(View.VISIBLE);
                nprog.setVisibility(View.INVISIBLE);
                Toast.makeText(advertiseedit.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
