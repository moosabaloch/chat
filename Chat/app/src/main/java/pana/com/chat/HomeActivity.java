package pana.com.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

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
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private DataModelMeSingleton ME;
    private NavigationView navigationView;
    private LinearLayout layout;
    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebase = FirebaseHandler.getInstance().getFirebaseBaseURL();
        if (firebase.getAuth() == null) {
            logout();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setCollapsible(true);
        toolbar.setElevation(0);

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
        layout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.mainHeaderBackground);
        setNavViewDetails();
        gcmImplementation();
        getSupportFragmentManager().beginTransaction().replace(R.id.homeActivityContent, new TabFragment()).commit();
    }

    private void setNavViewDetails() {
        AppController.getInstance().getImageLoader().get(ME.getImageUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Drawable drawable = new BitmapDrawable(getResources(), response.getBitmap());
                layout.setBackground(drawable);

            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Button button = (Button) navigationView.findViewById(R.id.logoutButtonNav);
        button.setOnClickListener(this);
//        navigationView.setNavigationItemSelectedListener();
        Menu menu = navigationView.getMenu();
        menu.getItem(0).setTitle(ME.getName());
        menu.getItem(1).setTitle(ME.getEmail());
        menu.getItem(2).setTitle(ME.getPhone());

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
    }

    @Override
    protected void onPause() {
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
        firebase.unauth();
        startActivity(new Intent(this, MainActivity.class));
        finish();

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
        }
    }
}
