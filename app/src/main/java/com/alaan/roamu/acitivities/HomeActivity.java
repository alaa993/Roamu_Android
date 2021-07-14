package com.alaan.roamu.acitivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamu.BuildConfig;
import com.alaan.roamu.about_us;
import com.alaan.roamu.custom.GPSTracker;
import com.alaan.roamu.fragement.Contact_usFragment;
import com.alaan.roamu.fragement.MapFragment;
import com.alaan.roamu.fragement.MyAcceptedRequestFragment;
import com.alaan.roamu.fragement.NominateDriverFragment;
import com.alaan.roamu.fragement.NotificationsFragment;
import com.alaan.roamu.fragement.ProfitFragment;
import com.alaan.roamu.fragement.lang;
import com.alaan.roamu.fragement.platform;
import com.alaan.roamu.pojo.Notification;
import com.alaan.roamu.privcy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.stash.Stash;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.custom.CheckConnection;
import com.alaan.roamu.fragement.AcceptedRequestFragment;
import com.alaan.roamu.fragement.HomeFragment;
import com.alaan.roamu.fragement.ProfileFragment;
import com.alaan.roamu.fragement.promo;
import com.alaan.roamu.pojo.User;
import com.alaan.roamu.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends ActivityManagePermission implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.ProfileUpdateListener, ProfileFragment.UpdateListener, LocationEngineListener {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public Toolbar toolbar;
    public Button addPost;
    TextView is_online, username;
    SwitchCompat switchCompat;
    LinearLayout linearLayout;
    NavigationView navigationView;
    private ImageView avatar;
    boolean post_notification = true;
    DatabaseReference databasePosts;

    LocationEngine locationEngine;
    double latitude; // latitude
    double longitude; // longitude
    String[] permissions = {PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    ValueEventListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        lang.loadLocale(getApplicationContext());
        Stash.init(getApplicationContext());

        BindView();
        getPhotoUri();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("action")) {
            String action = intent.getStringExtra("action");
            AcceptedRequestFragment acceptedRequestFragment = new AcceptedRequestFragment();
            Bundle bundle = new Bundle();
            bundle.putString("status", action);
            acceptedRequestFragment.setArguments(bundle);
            changeFragment(acceptedRequestFragment, "Requests");
        }
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        callLocationEngine();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askCompactPermissions(permissions, new PermissionResult() {
                @Override
                public void permissionGranted() {
//                    getLocation();
                    setListener();
                }

                @Override
                public void permissionDenied() {
                }

                @Override
                public void permissionForeverDenied() {
                    openSettingsApp(getApplicationContext());
                }
            });
        } else {
            setListener();
            // getLocation();
        }
        //by ibrahim
        GPSTracker tracker = new GPSTracker(this);
        setLocaiton(tracker.getLocation());

    }

    private void setupDrawer() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        //  globatTitle = );
        getSupportActionBar().setTitle(getString(R.string.app_name));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }


        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        //drawer.shouldDelayChildPressedState();


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void drawer_close() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        AcceptedRequestFragment acceptedRequestFragment = new AcceptedRequestFragment();
        platform platform = new platform();
        MyAcceptedRequestFragment myAcceptedRequestFragment;
        promo promo = new promo();

        Bundle bundle;
//        getVisibleFragment();
        switch (item.getItemId()) {
            case R.id.home:
                post_notification = false;
                addPost.setBackgroundResource(R.drawable.ic_notification);
                addPost.setText("0");
                addPost.setVisibility(View.VISIBLE);
                getNotificationsCount();
                changeFragment(new HomeFragment(), getString(R.string.home));
                break;

            case R.id.home_map:
                addPost.setVisibility(View.GONE);
                changeFragment(new MapFragment(), getString(R.string.home));
                break;

            case R.id.about_us:
                addPost.setVisibility(View.GONE);
                changeFragment(new about_us(), getString(R.string.about_uss));
                break;

            case R.id.privacy_policy:
                addPost.setVisibility(View.GONE);
                changeFragment(new privcy(), getString(R.string.pricvys));
                break;

            case R.id.profit:
                addPost.setVisibility(View.GONE);
                changeFragment(new ProfitFragment(), getString(R.string.pricvys));
                break;

            case R.id.lang:
                addPost.setVisibility(View.GONE);
                changeFragment(new lang(), getString(R.string.lang));
                break;

            case R.id.notifications:
                addPost.setVisibility(View.GONE);
                changeFragment(new NotificationsFragment(), getString(R.string.notifications));
                break;

            case R.id.platform:
                post_notification = true;
                databasePosts.removeEventListener(listener);
                addPost.setBackgroundResource(R.drawable.ronded_button2);
                addPost.setText(R.string.post);
                addPost.setVisibility(View.VISIBLE);
                changeFragment(platform, getString(R.string.platform));
                break;

            case R.id.my_requests:
                addPost.setVisibility(View.GONE);
                changeFragment(acceptedRequestFragment, "Requests");
                break;

            case R.id.my_accepted_requests:
                addPost.setVisibility(View.GONE);
                myAcceptedRequestFragment = new MyAcceptedRequestFragment();
                bundle = new Bundle();
                bundle.putString("status", "ACCEPTED");
                myAcceptedRequestFragment.setArguments(bundle);
                changeFragment(myAcceptedRequestFragment, "Requests");
                break;

            case R.id.profile:
                addPost.setVisibility(View.GONE);
                changeFragment(new ProfileFragment(), getString(R.string.profile));
                break;

            case R.id.Nominate_Driver:
                addPost.setVisibility(View.GONE);
                changeFragment(new NominateDriverFragment(), getString(R.string.profile));
                break;

            case R.id.contact_us:
                addPost.setVisibility(View.GONE);
                changeFragment(new Contact_usFragment(), getString(R.string.profile));
                break;

            case R.id.shareApp:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage = "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }
                break;

            case R.id.logout:
                addPost.setVisibility(View.GONE);
                SessionManager.logoutUser(getApplicationContext());
                FirebaseAuth.getInstance().signOut();
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public void changeFragment(final Fragment fragment, final String fragmenttag) {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    getVisibleFragment();
                    drawer_close();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack(null);
                }
            }, 30);
        } catch (Exception e) {
        }
    }

    public void getPhotoUri() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(uid.toString());

            databaseRefID.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String UserName = dataSnapshot.child("username").getValue(String.class);
                    String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                    Glide.with(getApplicationContext()).load(photoURL).apply(new RequestOptions().error(R.drawable.user_default)).into(avatar);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public void update(String url) {
        if (!url.equals("")) {
            //Glide.with(getApplicationContext()).load(url).apply(new RequestOptions().error(R.drawable.images)).into(avatar);
//            Glide.with(HomeActivity.this).load(user.getAvatar()).apply(new RequestOptions().error(R.drawable.images)).into(avatar);
        }
    }

    @Override
    public void name(String name) {
        if (!name.equals("")) {
            username.setText(name);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            setLocaiton(location);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationEngine != null) {
            locationEngine.activate();
        }
    }

    private void callLocationEngine() {
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.setFastestInterval(5);
        locationEngine.setInterval(10);
        locationEngine.setSmallestDisplacement(5);
        locationEngine.activate();
    }

    public void setLocaiton(Location location) {
        if (location != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("Location").child(SessionManager.getUserId());
            Map<String, Object> rideObject = new HashMap<>();
            rideObject.put("latitude", location.getLatitude());
            rideObject.put("longitude", location.getLongitude());
            rideObject.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseRef.setValue(rideObject);
        }
    }

    private void setListener() {
        locationEngine.addLocationEngineListener(this);
    }

    @SuppressLint("ParcelCreator")
    public class CustomTypefaceSpan extends TypefaceSpan {

        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        private void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public void fontToTitleBar(String title) {
        try {
            Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
            title = "<font color='#000000'>" + title + "</font>";
            SpannableString s = new SpannableString(title);
            s.setSpan(font, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                toolbar.setTitle(Html.fromHtml(String.valueOf(s), Html.FROM_HTML_MODE_LEGACY));
            } else {
                toolbar.setTitle((Html.fromHtml(String.valueOf(s))));
            }
        } catch (Exception e) {
            Log.e("catch", e.toString());
        }
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = HomeActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    Log.i("ibrahim_visible", fragment.toString());
                return fragment;
            }
        }
        return null;
    }

    public void BindView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        addPost = (Button) findViewById(R.id.toolbarPostBtn);
        addPost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //creating an intent
                if (post_notification == true) {
                    Intent intent = new Intent(HomeActivity.this, AddPostActivity.class);
                    startActivity(intent);
                } else {
                    updateNotificationFirebase(SessionManager.getUserId()); // my id is user id
//                    addPost.setVisibility(View.GONE);
                    changeFragment(new NotificationsFragment(), getString(R.string.notifications));
                }
            }
        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        switchCompat = (SwitchCompat) navigationView.getHeaderView(0).findViewById(R.id.online);
        avatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile);
        //linearLayout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.linear);
        is_online = (TextView) navigationView.getHeaderView(0).findViewById(R.id.is_online);
        username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_name);
        TextView version = (TextView) navigationView.getHeaderView(0).findViewById(R.id.version);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String ver = pInfo.versionName;
            version.setText("V ".concat(ver));
        } catch (PackageManager.NameNotFoundException e) {

        }

        navigationView.setCheckedItem(R.id.home);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.home));
        setupDrawer();
        try {
            Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
            username.setTypeface(font);
        } catch (Exception e) {

        }
        toolbar.setTitle("");

        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            getUserInfo();

        } else {
            Toast.makeText(HomeActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
            String name = SessionManager.getName();
            String url = SessionManager.getAvatar();
            username.setText(name);
            Glide.with(getApplicationContext()).load(url).apply(new RequestOptions().error(R.mipmap.ic_account_circle_black_24dp)).into(avatar);


        }
    }

    public void updateNotificationFirebase(String user_id) {
        Log.i("ibrahim", "updateNotificationFirebase");
        Log.i("ibrahim", user_id);
//        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Notifications").child(user_id).child(notification_id).child("readStatus");
//        databaseRef.setValue("1");

        // update all notificaitons read_status to be 1
        FirebaseDatabase.getInstance().getReference("Notifications").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Notification notification = postSnapshot.getValue(Notification.class);
                    notification.id = postSnapshot.getKey();
                    Log.i("ibrahim", notification.id);
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Notifications").child(user_id).child(notification.id).child("readStatus");
                    databaseRef.setValue("1");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getUserInfo() {
        String uid = SessionManager.getUserId();
        RequestParams params = new RequestParams();
        params.put("user_id", uid);
        Server.setHeader(SessionManager.getKEY());
        Server.get("api/user/profile/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {

                        Gson gson = new Gson();

                        User user = gson.fromJson(response.getJSONObject("data").toString(), User.class);
                        user.setKey(SessionManager.getKEY());

                        SessionManager.setUser(gson.toJson(user));

                        //Glide.with(HomeActivity.this).load(user.getAvatar()).apply(new RequestOptions().error(R.drawable.images)).into(avatar);

                        username.setText(user.getName());


                    }
                } catch (Exception e) {

                }
            }

        });

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        post_notification = false;
        getNotificationsCount();
        addPost.setBackgroundResource(R.drawable.ic_notification);
        addPost.setText("0");
        addPost.setVisibility(View.VISIBLE);

//        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawer_close();
//        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
//            finish();
//        } else {
//            super.onBackPressed();
//            getSupportFragmentManager().popBackStack();
//        }

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawer_close();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {

            super.onBackPressed();
        }
    }

    private boolean isInHomeFragment() {
        for (Fragment item : getSupportFragmentManager().getFragments()) {
            if (item.isVisible() && "HomeFragment".equals(item.getClass().getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    private void getNotificationsCount() {
        try {
            databasePosts = FirebaseDatabase.getInstance().getReference("Notifications").child(SessionManager.getUser().getUser_id());
            listener = databasePosts.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int NotificationsCount = 0;
                    for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        Notification notification = notificationSnapshot.getValue(Notification.class);
                        notification.id = notificationSnapshot.getKey();

                        if (notification.readStatus.contains("0")) {
                            NotificationsCount++;
                        }
                    }
                    addPost.setText(String.valueOf(NotificationsCount));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            Log.i("ibrahim_e", e.getMessage());
        }
    }
}