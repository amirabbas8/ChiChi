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
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import static android.view.View.VISIBLE;


public class send_to_neveshtan extends Activity {

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    EditText inputtext, inputlink;
    ProgressBar nprog;
    ImageButton btnsend, btnback, btnpicsetdel;
    Button fchoose;
    String imagename = "";
    int serverResponseCode = 0;
    ProgressDialog dialogProgress = null;
    /**
     * ********** Php script path ***************
     */
    String upLoadServerUri = "http://chichi.mobi/uploadimage.php";
    String fid = "";
    String fname = "";
    String fprofilePic;
    Bitmap croppedImage;
    CropImageView cropImageView;
    File f;
    private ImageView mImageView;
    private ImageView fImageView;
    private Uri mImageCaptureUri;
    private Dialog cropperdialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_to_neveshtan);

        btnsend = (ImageButton) findViewById(R.id.send);
        btnpicsetdel = (ImageButton) findViewById(R.id.btnpicsetdel);
        btnback = (ImageButton) findViewById(R.id.back);
        fchoose = (Button) findViewById(R.id.fchoose);
        nprog = (ProgressBar) findViewById(R.id.progressBar1);
        inputtext = (EditText) findViewById(R.id.text);
        inputlink = (EditText) findViewById(R.id.link);
        mImageView = (ImageView) findViewById(R.id.uploadimage);
        fImageView = (ImageView) findViewById(R.id.fprofile);
        btnsend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                btnsend.setVisibility(View.INVISIBLE);
                nprog.setVisibility(VISIBLE);
                if (inputtext.getText().toString().length() < 1) {
                    if (imagename.equals("")) {
                        Toast.makeText(getApplicationContext(), R.string.errorwrite, Toast.LENGTH_SHORT).show();
                        btnsend.setVisibility(VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
                    } else {
                        if (fid.equals("")) {
                            Toast.makeText(getApplicationContext(), R.string.Select_friend, Toast.LENGTH_SHORT).show();
                            btnsend.setVisibility(VISIBLE);
                            nprog.setVisibility(View.INVISIBLE);
                        } else {
                            new NetCheck().execute();
                        }

                    }
                } else {
                    if (fid.equals("")) {
                        Toast.makeText(getApplicationContext(), R.string.Select_friend, Toast.LENGTH_SHORT).show();
                        btnsend.setVisibility(VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
                    } else {
                        new NetCheck().execute();
                    }
                }
            }

        });

        btnback.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }

        });
        fchoose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent intent = new Intent(send_to_neveshtan.this, send_to_fchoose.class);

                startActivityForResult(intent, 14);
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
        btnpicsetdel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                inputtext.setHint(R.string.text);
                if (imagename.equals("")) {
                    dialog.show();
                } else {
                    imagename = "";
                    mImageView.setImageResource(R.drawable.camera_icon);

                    btnpicsetdel.setImageResource(R.drawable.add_icon);
                }

            }

        });
        cropperdialog = new Dialog(send_to_neveshtan.this);
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
                dialogProgress = ProgressDialog.show(send_to_neveshtan.this, "", getResources().getString(R.string.uploadingfile), true);
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
                cropImageView.setInitialAttributeValues(2, true, 10, 10);
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
                cropImageView.setInitialAttributeValues(2, true, 10, 10);
                cropImageView.setGuidelines(2);
                cropperdialog.show();

                break;

            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();

                if (extras != null) {
                    //000000000000000000

                    final File finalFile1 = new File(mImageCaptureUri.getPath());
                    Bitmap myBitmap = BitmapFactory.decodeFile(finalFile1.getAbsolutePath());

                    mImageView.setImageBitmap(myBitmap);
                    System.out.println(mImageCaptureUri);

                    dialogProgress = ProgressDialog.show(send_to_neveshtan.this, "", getResources().getString(R.string.uploadingfile), true);
                    new Thread(new Runnable() {

                        public void run() {
                            runOnUiThread(new Runnable() {

                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.uploadingstart, Toast.LENGTH_SHORT).show();
                                }
                            });

                            uploadFile(finalFile1);

                        }
                    }).start();
                }

                File f = new File(mImageCaptureUri.getPath());

                if (f.exists())
                    f.delete();

                break;
            case 14:
                fid = data.getStringExtra("fid");
                fname = data.getStringExtra("fname");
                fchoose.setText(fname);
                fprofilePic = data.getStringExtra("fprofilePic");
                // Loader image - will be shown before loading image
                int loader = R.drawable.ic_profile;
                String profile_url = "http://chichi.mobi/profileimages/" + fprofilePic;
                ImageLoader imgLoader = new ImageLoader(getApplicationContext());
                imgLoader.DisplayImage(profile_url, loader, fImageView);

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

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {

                        public void run() {

                            imagename = fileName;
                            dialogProgress.dismiss();
                            Toast.makeText(send_to_neveshtan.this, R.string.succes, Toast.LENGTH_SHORT).show();
                            inputtext.setHint(R.string.text);
                            btnpicsetdel.setImageResource(R.drawable.delete_icon);
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

                        Toast.makeText(send_to_neveshtan.this, "MalformedURLException Exception : check script url.", Toast.LENGTH_SHORT).show();
                    }
                });

                //    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialogProgress.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {

                    public void run() {
                        btnsend.setVisibility(VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
                        Toast.makeText(send_to_neveshtan.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
                    }
                });
                //    Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
            }
            dialogProgress.dismiss();
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

    /**
     * Async Task to get and send data to My Sql database through JSON respone.
     */
    private class Processwrite extends AsyncTask<String, String, JSONObject> {

        String id, text, link, profilepic, name;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            inputtext = (EditText) findViewById(R.id.text);
            text = inputtext.getText().toString();
            link = inputlink.getText().toString();
            DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());

            HashMap<String, String> user;
            user = db1.getUserDetails();
            id = user.get("idno");
            profilepic = user.get("profileimage");
            name = user.get("realname");
        }


        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.sendtowrite(id, text, link, profilepic, name, imagename, fid, fname, fprofilePic);

        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                String KEY_SUCCESS = "success";
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (Integer.parseInt(res) == 1) {
                        JSONObject json_user = json.getJSONObject("post");
                        Toast.makeText(getApplicationContext(), R.string.succes, Toast.LENGTH_SHORT).show();
                        String id0 = json_user.getString("id");
                        String image = json_user.getString("imagename");
                        String text = json_user.getString("text");
                        String timewrited = json_user.getString("timewrited");
                        String url = json_user.getString("url");
                        Intent intent = new Intent();
                        intent.putExtra("id", id0);
                        intent.putExtra("name", name);
                        intent.putExtra("userid", id);
                        intent.putExtra("image", image);
                        intent.putExtra("text", text);
                        intent.putExtra("profilepic", profilepic);
                        intent.putExtra("timewrited", timewrited);
                        intent.putExtra("setUrl", url);
                        intent.putExtra("fname", fname);
                        intent.putExtra("fprofilePic", fprofilePic);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        btnsend.setVisibility(VISIBLE);
                        nprog.setVisibility(View.INVISIBLE);
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

                new Processwrite().execute();
            } else {
                btnsend.setVisibility(VISIBLE);
                nprog.setVisibility(View.INVISIBLE);
                Toast.makeText(send_to_neveshtan.this, R.string.errorconection, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
