package pana.com.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import pana.com.chat.DataModel.DataModelMeSingleton;
import pana.com.chat.Util.FirebaseHandler;
import pana.com.chat.Util.Utils;
import pana.com.chat.gcm.RegistrationIntentService;
import pana.com.chat.ui.Fragments.HomeFragment;
import pana.com.chat.ui.Fragments.TabFragment;

public class HomeActivity extends AppCompatActivity
        implements HomeFragment.HomeFragInter, TabFragment.OnFragmentInteractionListener, View.OnClickListener {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private static final int PICK_IMAGE = 1;
    private static final int RESULT_CROP = 2;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private DataModelMeSingleton ME;
    private NavigationView navigationView;
    private RelativeLayout layout;
    private Bitmap bitmap;
    private Cloudinary cloudinary;
    private Uri selectedImageUri = null;
    private String selectedImagePath;
    private ImageView imageViewProfileForDrawer;
    private Firebase firebase;
    private String frag;
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frag = getIntent().getStringExtra("frag");
        title = getIntent().getStringExtra("sender");
        setContentView(R.layout.activity_home);
        firebase = FirebaseHandler.getInstance().getFirebaseBaseURL();
        if (firebase.getAuth() == null) {
            logout();
        }
        cloudinary = Utils.cloudinary();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setCollapsible(true);

        setSupportActionBar(toolbar);
        // toolbar.setLogo(R.drawable.friend);
        ME = DataModelMeSingleton.getInstance();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.hide();
        fab.setVisibility(View.GONE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        layout = (RelativeLayout) navigationView.getHeaderView(0).findViewById(R.id.mainHeaderBackground);
        imageViewProfileForDrawer = (ImageView) layout.findViewById(R.id.mainHeaderBackgroundImageView);

        setNavViewDetails();
        gcmImplementation();
        TabFragment tabFragment = new TabFragment();
        if (frag != null) {
            Bundle bundle = new Bundle();
            bundle.putString("frag", frag);
            bundle.putString("sender", title);
            tabFragment.setArguments(bundle);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.homeActivityContent, tabFragment).commit();
    }

    private void setNavViewDetails() {
        try {
            AppController.getInstance().getImageLoader().get(ME.getImageUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    imageViewProfileForDrawer.setImageBitmap(response.getBitmap());

                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            Button buttonChangePic = (Button) layout.findViewById(R.id.changeProfilePicture);
            buttonChangePic.setOnClickListener(this);
            Button button = (Button) navigationView.findViewById(R.id.logoutButtonNav);
            button.setOnClickListener(this);
//        navigationView.setNavigationItemSelectedListener();
            Menu menu = navigationView.getMenu();
            menu.getItem(0).setTitle(ME.getName());
            menu.getItem(1).setTitle(ME.getEmail());
            menu.getItem(2).setTitle(ME.getPhone());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void gcmImplementation() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(Utils.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {

                    //    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    //  mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Utils.REGISTRATION_COMPLETE));
        SharedPreferences prefs = this.getSharedPreferences(HomeActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        prefs.edit().putBoolean(Utils.MY_APP_IS_RUNNING, true).apply();
    }

    @Override
    protected void onPause() {
        SharedPreferences prefs = this.getSharedPreferences(HomeActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        prefs.edit().putBoolean(Utils.MY_APP_IS_RUNNING, false).apply();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");

                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void registerGCMService() {
        Log.d("Interface HomeFrag", "registerGCMService Method Invoked");
        /**
         Functionality of Broadcast Receiver is When the Service get the token
         it updates the UI and save the data in SharedPreferences
         if Google service is installed then continue to start service code
         */
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /*

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.home, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    */
/*

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }*/
/* else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*//*


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

*/
    private void logout() {
        DataModelMeSingleton userData = DataModelMeSingleton.getInstance();
        Log.d("Logout()", "User Data:" + userData.toString());
        try {
            if (!(userData.getId().length() < 10)) {
                HashMap<String, String> userDetails = new HashMap<>();
                userDetails.put("email_id", userData.getEmail());
                userDetails.put("image_url", userData.getImageUrl());
                userDetails.put("name", userData.getName());
                userDetails.put("phone", userData.getPhone());
                userDetails.put("gcm", "0000");
                FirebaseHandler.getInstance().getFirebaseUsersURL().child(userData.getId()).setValue(userDetails, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        Log.d("User Token Removed", "User Logout");
                        firebase.unauth();
                        startActivity(new Intent(HomeActivity.this, MainActivity.class));
                        finish();

                    }
                });
            }
        } catch (Exception x) {
            Utils.ToastLong(this, "Sorry you have Slow internet connection");

        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.logoutButtonNav):
                logout();
                break;
            case (R.id.changeProfilePicture):
                Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show();
                performSelect();
                break;

        }
    }

    ////////////////PICTURE UPLOAD////////////////////
    private void performSelect() {
        Log.d("INVOKED", "Perform Select");

        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String filePath = null;
        switch (requestCode) {

            case PICK_IMAGE:
                Log.d("INVOKED", "onActivityResult case PICK IMAGE");

                if (resultCode == Activity.RESULT_OK) {
                    Log.d("INVOKED", "onActivityResult == OK");

                    selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        Log.d("Image Selected", " URI" + selectedImageUri.toString());

                        try {
                            //selectedImagePath = getPath(selectedImageUri);
                            performCrop();
                            //imageView.setImageURI(selectedImageUri);
                        } catch (Exception e) {
                            Toast.makeText(HomeActivity.this, "Internal error", Toast.LENGTH_LONG).show();
                            Log.e(e.getClass().getName(), e.getMessage(), e);
                        }
                    }
                }
                break;

            case RESULT_CROP:
                Log.d("INVOKED", "onActivityResult case CROP");

                if (resultCode == Activity.RESULT_OK) {
                    Log.d("CASE CROP", " Result == OK");

                    Bundle extras = data.getExtras();
                    bitmap = extras.getParcelable("data");
                    String path = saveImage(bitmap);
                    Log.d("PATH AFTER CROPPING", path);
                    if (path != null) {
                        selectedImagePath = path;
                        decodeFile(path);
                    }
                }
                break;
        }
    }

    private void performCrop() {
        Log.d("INVOKED", "Performed Crop");
        try {
            if (selectedImageUri != null) {
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(selectedImageUri, "image/*");
                cropIntent.putExtra("crop", "true");
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("outputX", 280);
                cropIntent.putExtra("outputY", 280);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, RESULT_CROP);
            }
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "your device doesn't support the crop action!";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void decodeFile(final String filePath) {
        Log.d("INVOKED", "Decode File");

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        final int REQUIRED_SIZE = 1024;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);
        new AlertDialog.Builder(this)
                .setTitle("Upload Picture")
                .setMessage("Are you sure you want to upload picture?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        imageViewProfileForDrawer.setImageBitmap(bitmap);
                        Log.d("File PATH IS ", selectedImagePath + "");
                        ////////////////////////////////UPLOADING CLOUDINARY////////////////////

                        AsyncTask<String, Void, HashMap<String, Object>> upload = new AsyncTask<String, Void, HashMap<String, Object>>() {
                            @Override
                            protected HashMap<String, Object> doInBackground(String... params) {
                                File file = new File(selectedImagePath);
                                HashMap<String, Object> responseFromServer = null;
                                try {
                                    responseFromServer = (HashMap<String, Object>) cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                                } catch (IOException e) {
                                    Toast.makeText(HomeActivity.this, "Cannot Upload Image Please Try Again", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }

                                return responseFromServer;
                            }

                            @Override
                            protected void onPostExecute(HashMap<String, Object> stringObjectHashMap) {
                                String url = (String) stringObjectHashMap.get("url");
                                firebase.child("users").child(ME.getId()).child("image_url").setValue(url, new Firebase.CompletionListener() {
                                    @Override
                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                        if (firebaseError != null) {
                                            Toast.makeText(HomeActivity.this, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(HomeActivity.this, "Upload Completed", Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });
                            }
                        };
                        upload.execute(selectedImagePath);
                        ////////////////////////////////UPLOADING COMPLETED////////////////////////////////////////
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                }).show();
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            return null;
        }
    }

    private String saveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Chat/ProfileImages");

        if (!myDir.exists())
            myDir.mkdirs();

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image_" + n + ".jpg";
        File file = new File(myDir, fname);

        if (file.exists())
            file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root + "/Chat/ProfileImages/" + fname;
    }


}
