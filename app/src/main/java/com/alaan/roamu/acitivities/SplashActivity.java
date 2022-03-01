package com.alaan.roamu.acitivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.alaan.roamu.Server.Server;
import com.alaan.roamu.custom.Utils;
import com.alaan.roamu.pojo.User;
import com.fxn.stash.Stash;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.alaan.roamu.R;
import com.alaan.roamu.session.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.alaan.roamu.fragement.lang.setLocale;


/**
 * Created by android on 7/3/17.
 */

public class SplashActivity extends ActivityManagePermission {
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    String token;
    public static final int OPEN_NEW_ACTIVITY = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stash.init(this);
        if (Stash.getString("TAG_LANG") != null) {
            setLocale(Stash.getString("TAG_LANG"), this);

        } else {
            setLocale("en", this);

        }

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.splash_activity);
        int SPLASH_TIME_OUT = 500;
        isGooglePlayServicesAvailable(SplashActivity.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                askCompactPermissions(permissionAsk, new PermissionResult() {
                    @Override
                    public void permissionGranted() {
                        redirect();
                    }

                    @Override
                    public void permissionDenied() {
                        redirect();
                    }

                    @Override
                    public void permissionForeverDenied() {
                        redirect();
                    }
                });

            }
        }, SPLASH_TIME_OUT);

    }

    public void changement() {
        if (SessionManager.isLoggedIn()) {
            startActivityForResult(new Intent(SplashActivity.this, HomeActivity.class), OPEN_NEW_ACTIVITY);
        } else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivityForResult(i, OPEN_NEW_ACTIVITY);
        }
        finish();
    }

    private void redirect() {
        if (SessionManager.isLoggedIn()) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()) {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            token = instanceIdResult.getToken();
                            SessionManager.setGcmToken(token);
                            login(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), "");
                        }
                    });
                } else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivityForResult(i, OPEN_NEW_ACTIVITY);
                }
            } else {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivityForResult(i, OPEN_NEW_ACTIVITY);
            }
        } else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivityForResult(i, OPEN_NEW_ACTIVITY);
        }
    }

    public void login(String email, String password) {

        RequestParams params = new RequestParams();
        params.put("mobile", email);
        params.put("utype", "0");
        params.put("gcm_token", token);
        Server.post("user/loginByMobile/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {

                        Utils utils = new Utils(SplashActivity.this);
                        utils.isAnonymouslyLoggedIn();
                        if (response.has("data")) {
                            startActivityForResult(new Intent(SplashActivity.this, HomeActivity.class), OPEN_NEW_ACTIVITY);
                        }
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        startActivityForResult(new Intent(SplashActivity.this, LoginActivity.class), OPEN_NEW_ACTIVITY);
                        finish();
//                        Toast.makeText(SplashActivity.this, response.getString("data"), Toast.LENGTH_LONG).show();
                    }
                } catch (NullPointerException e) {
                    System.err.println("Null pointer exception");
                }catch (JSONException e) {

                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        } else {

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_NEW_ACTIVITY) {
            finish();
        }
    }
}
