package com.alaan.roamu.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.alaan.roamu.R;
import com.alaan.roamu.Server.GoogMatrixRequest;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.custom.CheckConnection;
import com.alaan.roamu.custom.GPSTracker;
import com.alaan.roamu.custom.SetCustomFont;
import com.alaan.roamu.custom.Utils;
import com.alaan.roamu.pojo.Fav_Places;
import com.alaan.roamu.pojo.NearbyData;
import com.alaan.roamu.pojo.Notification;
import com.alaan.roamu.pojo.Pass;
import com.alaan.roamu.pojo.PendingRequestPojo;
import com.alaan.roamu.pojo.Tracking;
import com.alaan.roamu.pojo.firebaseClients;
import com.alaan.roamu.pojo.firebaseRide;
import com.alaan.roamu.pojo.firebaseTravel;
import com.alaan.roamu.session.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import net.skoumal.fragmentback.BackFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.loopj.android.http.AsyncHttpClient.log;

public class MyAcceptedDetailFragment extends FragmentManagePermission
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, BackFragment, OnMapReadyCallback, DirectionCallback,
        Animation.AnimationListener {

    private View view;
    private RelativeLayout distancematrix_informations;
    Animation animFadeIn, animFadeOut;
    TextView txt_distance, txt_timedistance;

    AppCompatButton trackRide;
    private String mobile = "";
    AppCompatButton btn_cancel, btn_payment, btn_complete;
    TextView drivername, pickup_location, drop_location, mobilenumber;
    private AlertDialog alert;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final String CONFIG_ENVIRONMENT = Server.ENVIRONMENT;
    private static PayPalConfiguration config;
    PayPalPayment thingToBuy;
    PendingRequestPojo rideJson;
    String permissions[] = {PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    //    private SwipeRefreshLayout swipeRefreshLayout;
    private Dialog dialog;
    AppCompatButton cancel;
    com.google.android.gms.maps.MapView mapView;
    TextView dateandtime, TimeVal;

    TextView txt_Driver_name, txt_PickupPoint;
    TextView calculateFare;
    Snackbar snackbar;
    Switch switchCompat;
    List<NearbyData> list;

    DatabaseReference databaseRides;
    ValueEventListener listener;
    DatabaseReference databaseTravelRef;
    DatabaseReference databaseClientsLocation;
    DatabaseReference databaseDriverLocation;

    Bundle bundle;

    private String travel_status;
    private String ride_status;
    private String payment_status;
    private String payment_mode;

    Button my_acc_d_f_home_button;
    ValueEventListener listener_fav_places;
    DatabaseReference database_fav_places;
    com.google.android.gms.maps.MapView mMapView;
    GoogleMap myMap;

    GPSTracker gpsTracker;

    private LatLng origin;
    private LatLng destination;
    private String pickup_address;
    private String drop_address;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private Marker my_marker;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    String distance;
    private Double fare = 50.00;
    Double finalfare;
    firebaseTravel fbTravel;

    int[][] states = new int[][]{
            new int[]{-android.R.attr.state_checked},
            new int[]{android.R.attr.state_checked},
    };

    int[] thumbColors = new int[]{
            Color.RED,
            Color.GREEN,
    };

    public MyAcceptedDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bundle = getArguments();
        if (bundle != null) {
            rideJson = (PendingRequestPojo) bundle.getSerializable("data");
            travel_status = rideJson.getTravel_status();
            ride_status = rideJson.getStatus();
            payment_status = rideJson.getPayment_status();
            payment_mode = rideJson.getPayment_mode();
//            //log.i("ibrahim", "payment_status");
//            //log.i("ibrahim", "rideJson.getPayment_status()");
//            //log.i("ibrahim", payment_status);
//            //log.i("ibrahim", payment_mode);

//            //log.i("ibrahim", rideJson.getRide_id());
            databaseRides = FirebaseDatabase.getInstance().getReference("rides").child(rideJson.getRide_id());
            databaseTravelRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id());
        }
        view = inflater.inflate(R.layout.fragment_my_accepted_detail, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.passenger_info));
        BindView(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askCompactPermissions(permissionAsk, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    if (!GPSEnable()) {
                        tunonGps();
                    } else {
                        getCurrentlOcation();
                    }
                }

                @Override
                public void permissionDenied() {

                }

                @Override
                public void permissionForeverDenied() {
                    openSettingsApp(getActivity());
                }
            });

        } else {
            if (!GPSEnable()) {
                tunonGps();
            } else {
                getCurrentlOcation();
            }
        }

        configPaypal();
        NearBy();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener
        listener = databaseRides.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseRide fbRide = dataSnapshot.getValue(firebaseRide.class);
//                //log.i("ibrahim ride", "----------");
                travel_status = fbRide.travel_status;
                ride_status = fbRide.ride_status;
                payment_status = fbRide.payment_status;
                payment_mode = fbRide.payment_mode;
//                //log.i("ibrahim", "payment_status");
//                //log.i("ibrahim", "databaseRides");
//                //log.i("ibrahim", payment_status);
//                //log.i("ibrahim", payment_mode);
                setupData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseRides.removeEventListener(listener);
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }

    public void BindView(Bundle savedInstanceState) {
        gpsTracker = new GPSTracker(getActivity());

        mMapView = (com.google.android.gms.maps.MapView) view.findViewById(R.id.MyADF_mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.dialogue_scale_anim_open);
        animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.dialogue_scale_anim_exit);
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
        distancematrix_informations = (RelativeLayout) view.findViewById(R.id.distancematrix);
        txt_distance = (TextView) view.findViewById(R.id.txt_distance);
        txt_timedistance = (TextView) view.findViewById(R.id.txt_timedistance);

        distancematrix_informations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (distancematrix_informations.getVisibility() == View.VISIBLE) {
                    distancematrix_informations.startAnimation(animFadeOut);
                    distancematrix_informations.setVisibility(View.GONE);
                }
            }
        });

        my_acc_d_f_home_button = (Button) view.findViewById(R.id.my_acc_d_f_home_button);
        mapView = (com.google.android.gms.maps.MapView) view.findViewById(R.id.MyADF_mapview);
//        cancel = (AppCompatButton) view.findViewById(R.id.MyADF_btn_cancel);
        mobilenumber = (TextView) view.findViewById(R.id.MyADF_txt_mobilenumber);
        pickup_location = (TextView) view.findViewById(R.id.MyADF_txt_pickuplocation);
        drop_location = (TextView) view.findViewById(R.id.MyADF_txt_droplocation);
        dateandtime = (TextView) view.findViewById(R.id.MyADF_dateTimeVal);
        TimeVal = (TextView) view.findViewById(R.id.MyADF_TimeVal);
        txt_Driver_name = (TextView) view.findViewById(R.id.MyADF_Driver_name);
        txt_PickupPoint = (TextView) view.findViewById(R.id.MyADF_txt_PickupPoint);
        btn_complete = (AppCompatButton) view.findViewById(R.id.MyADF_btn_complete);
//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.MyADF_swipe_refresh);
        drivername = (TextView) view.findViewById(R.id.MyADF_Driver_name);
        trackRide = (AppCompatButton) view.findViewById(R.id.MyADF_btn_trackride);
        btn_payment = (AppCompatButton) view.findViewById(R.id.MyADF_btn_payment);
        btn_cancel = (AppCompatButton) view.findViewById(R.id.MyADF_btn_cancel);
        pickup_location.setSelected(true);
        drop_location.setSelected(true);
        switchCompat = (Switch) view.findViewById(R.id.travel_schedule);
        switchCompat.setChecked(false);
        DrawableCompat.setTintList(DrawableCompat.wrap(switchCompat.getThumbDrawable()), new ColorStateList(states, thumbColors));
        if (bundle != null) {
            pickup_location.setText(rideJson.getpickup_address() + " ");
            drop_location.setText(rideJson.getDrop_address());
            txt_PickupPoint.setText(rideJson.getPickup_point());
            drivername.setText(rideJson.getDriver_name());
            mobilenumber.setText(rideJson.getDriver_mobile());
            mobile = rideJson.getDriver_mobile();
            TimeVal.setText(rideJson.getTime());
            dateandtime.setText(rideJson.getDate());

            if (rideJson.getpickup_location() != null && rideJson.getdrop_location() != null) {
//                //log.i("ibrahim", "inside");
//                //log.i("ibrahim", rideJson.getpickup_location());
//                //log.i("ibrahim", rideJson.getdrop_location());

                String[] pickuplatlong = rideJson.getpickup_location().split(",");
                double pickuplatitude = Double.parseDouble(pickuplatlong[0]);
                double pickuplongitude = Double.parseDouble(pickuplatlong[1]);
                origin = new LatLng(pickuplatitude, pickuplongitude);

//                //log.i("ibrahim", origin.toString());

                String[] droplatlong = rideJson.getdrop_location().split(",");
                double droplatitude = Double.parseDouble(droplatlong[0]);
                double droplongitude = Double.parseDouble(droplatlong[1]);
                destination = new LatLng(droplatitude, droplongitude);
//                //log.i("ibrahim", destination.toString());
            }

            mobilenumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    //log.i("ibrahim", "mobile call function");
                    askCompactPermission(PermissionUtils.Manifest_CALL_PHONE, new PermissionResult() {
                        @Override
                        public void permissionGranted() {

                            if (mobile != null && !mobile.equals("")) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + mobile));
                                startActivity(callIntent);
                            }
                        }

                        @Override
                        public void permissionDenied() {

                        }

                        @Override
                        public void permissionForeverDenied() {

                        }
                    });
                }
            });

            if (ride_status.equalsIgnoreCase("WAITED")) {
                btn_cancel.setVisibility(View.VISIBLE);
                btn_complete.setText(getString(R.string.confirm));
                btn_complete.setVisibility(View.VISIBLE);
                isStarted();
            }
            if (ride_status.equalsIgnoreCase("PENDING")) {
                btn_cancel.setVisibility(View.VISIBLE);
                btn_complete.setVisibility(View.GONE);
                isStarted();
            }
            if (ride_status.equalsIgnoreCase("CANCELLED")) {
                btn_complete.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);
                btn_payment.setVisibility(View.GONE);
                trackRide.setVisibility(View.GONE);

            }
            if (ride_status.equalsIgnoreCase("COMPLETED")) {
                btn_payment.setVisibility(View.GONE);
                trackRide.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);
                btn_complete.setVisibility(View.GONE);
                CheckRating(rideJson.getUser_id(), rideJson.getTravel_id(), rideJson.getDriver_id());
            }
            if (ride_status.equalsIgnoreCase("ACCEPTED")) {
                isStarted();
                if (payment_status.equals("") && payment_mode.equals("")) {
                    btn_complete.setVisibility(View.GONE);
                    btn_cancel.setVisibility(View.VISIBLE);
                    trackRide.setVisibility(View.GONE);
                    btn_payment.setVisibility(View.VISIBLE);
                } else {
                    btn_complete.setText(getString(R.string.complete_ride));
                    btn_complete.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.GONE);
                    trackRide.setVisibility(View.GONE);
//                    mobilenumber_row.setVisibility(View.VISIBLE);
                }
                if (!payment_status.equals("PAID") && payment_mode.equals("OFFLINE")) {
                    btn_complete.setVisibility(View.GONE);
                    btn_cancel.setVisibility(View.GONE);
                } else {
                }
            }

            if (rideJson.ride_type.equalsIgnoreCase("1")) {
                switchCompat.setChecked(true);
                DrawableCompat.setTintList(DrawableCompat.wrap(switchCompat.getThumbDrawable()), new ColorStateList(states, thumbColors));
            }
        }
        setupData();
        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnection.haveNetworkConnection(getActivity())) {

                    new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.payment_method)).setItems(R.array.payment_mode, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                RequestParams params = new RequestParams();
                                params.put("ride_id", rideJson.getRide_id());
                                params.put("payment_mode", "OFFLINE");
                                Server.setContetntType();
                                Server.setHeader(SessionManager.getKEY());
                                Server.post("api/user/rides", params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onStart() {
//                                        swipeRefreshLayout.setRefreshing(true);
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        updateRideFirebase(travel_status, ride_status, payment_status, "OFFLINE");
                                        updateNotificationFirebase("offline_request");
                                        updateTravelCounterFirebase();
                                        rideJson.setPayment_mode("OFFLINE");
                                        btn_payment.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), getString(R.string.payment_update), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        super.onFailure(statusCode, headers, responseString, throwable);
                                    }

                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                        if (getActivity() != null) {
//                                            swipeRefreshLayout.setRefreshing(false);
                                        }
                                    }
                                });

                            } else if (which == 1) {
                                SendMoneyToWallet(SessionManager.getUserId(), rideJson.getDriver_id(), rideJson.getAmount());
                                RequestParams params = new RequestParams();
                                params.put("ride_id", rideJson.getRide_id());
                                params.put("payment_mode", "OFFLINE");
                                Server.setContetntType();
                                Server.setHeader(SessionManager.getKEY());
                                Server.post("api/user/rides", params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onStart() {
//                                        swipeRefreshLayout.setRefreshing(true);
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        updateRideFirebase(travel_status, ride_status, payment_status, "OFFLINE");
                                        updateNotificationFirebase("offline_request");
                                        updateTravelCounterFirebase();
                                        rideJson.setPayment_mode("OFFLINE");
                                        btn_payment.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), getString(R.string.payment_update), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        super.onFailure(statusCode, headers, responseString, throwable);
                                    }

                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                        if (getActivity() != null) {
//                                            swipeRefreshLayout.setRefreshing(false);
                                        }
                                    }
                                });
                            } else {
                                MakePayment();
                            }
                        }
                    }).create().show();

                    //MakePayment();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogCreate(getString(R.string.ride_request_cancellation), getString(R.string.want_to_cancel), "CANCELLED");
            }
        });
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ride_status.equalsIgnoreCase("WAITED")) {
                    sendStatus(rideJson.getRide_id(), "ACCEPTED");
                    updateTravelFirebase();
                    //
                }
                if (ride_status.equalsIgnoreCase("ACCEPTED") || ride_status.equalsIgnoreCase("COMPLETED")) {// edited by ibrahim it was completed date:21/1/2021
                    completeTask();
                }
            }
        });
        my_acc_d_f_home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Utils.haveNetworkConnection(getContext())) {
                    DrawableCompat.setTintList(DrawableCompat.wrap(switchCompat.getThumbDrawable()), new ColorStateList(states, thumbColors));

                    if (isChecked) {
                        //log.i("ibrhim", "switchCompat");
                        //log.i("ibrhim", "1");
                        travel_type_change(rideJson.getRide_id(), "1", false);
                    } else {
                        //log.i("ibrhim", "switchCompat");
                        //log.i("ibrhim", "0");
                        travel_type_change(rideJson.getRide_id(), "0", false);
                    }

                } else {
                    Toast.makeText(getContext(), getString(R.string.network_not_available), Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void SendMoneyToWallet(String wallet_id, String wallet_id2, String amount) {
        RequestParams params = new RequestParams();
        params.put("wallet_id", wallet_id);
        params.put("wallet_id2", wallet_id2);
        params.put("amount", amount);
        params.put("transaction_type_id", "2");
        params.put("transaction_type_id2", "1");
        params.put("status_type_id", "2");

        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();

        Server.get("api/user/addBalanceUserToWallets/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //log.i("response", response.toString());
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        String data = response.getString("message");
                        if (data.contains("transactionSuccess")) {
                            String resourceAppStatusString = "message_".concat(data);
                            //log.i("resourceAppStatusString", resourceAppStatusString);
                            int messageId = getResourceId(resourceAppStatusString, "string", "com.alaan.roamu");
                            String message = getString(messageId);

                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), data, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.sonething_went_wrong, Toast.LENGTH_LONG).show();
                    }
                } catch (NullPointerException e) {
                    System.err.println("Null pointer exception");
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private int getResourceId(String pVariableName, String pResourceName, String pPackageName) {
        try {
            return getResources().getIdentifier(pVariableName, pResourceName, pPackageName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void updateTravelCounterFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseTravel fbTravel = dataSnapshot.getValue(firebaseTravel.class);
                if (fbTravel != null) {
                    //log.i("ibrahim", "fbTravel");
                    try {
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Counters").child("OFFLINE");
                        databaseRef.setValue(fbTravel.Counters.OFFLINE + 1);
                    } catch (NullPointerException e) {
                        System.err.println("Null pointer exception");
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    public void updateTravelFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id());
        Map<String, Object> travelObject = new HashMap<>();
        travelObject.put("driver_id", rideJson.getDriver_id());

//        Map<String, String> Client = new HashMap<>();
//        Client.put(rideJson.getUser_id(),rideJson.getUser_id());

//        travelObject.put("Clients", Client);

        databaseRef.updateChildren(travelObject).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Clients").child(rideJson.getUser_id());
//                Map<String, String> Client = new HashMap<>();
//                Client.put(rideJson.getUser_id(),rideJson.getUser_id());Map<String, String> Client = new HashMap<>();
////                Client.put(rideJson.getUser_id(),rideJson.getUser_id());
                databaseRef.setValue(rideJson.getUser_id());
            }
        });

    }

    public void setupData() {
        if (bundle != null) {
            pickup_location.setText(rideJson.getpickup_address() + " ");
            drop_location.setText(rideJson.getDrop_address());
            drivername.setText(rideJson.getDriver_name());
            mobilenumber.setText(rideJson.getDriver_mobile());
            mobile = rideJson.getDriver_mobile();
            TimeVal.setText(rideJson.getTime());
            dateandtime.setText(rideJson.getDate());
            mobilenumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    //log.i("ibrahim", "mobile call function");
                    askCompactPermission(PermissionUtils.Manifest_CALL_PHONE, new PermissionResult() {
                        @Override
                        public void permissionGranted() {

                            if (mobile != null && !mobile.equals("")) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + mobile));
                                startActivity(callIntent);
                            }
                        }

                        @Override
                        public void permissionDenied() {

                        }

                        @Override
                        public void permissionForeverDenied() {

                        }
                    });
                }
            });

            if (ride_status.equalsIgnoreCase("WAITED")) {
                btn_cancel.setVisibility(View.VISIBLE);
                btn_complete.setText(getString(R.string.confirm));
                btn_complete.setVisibility(View.VISIBLE);
                isStarted();
            }
            if (ride_status.equalsIgnoreCase("PENDING")) {
                btn_cancel.setVisibility(View.VISIBLE);
                btn_complete.setVisibility(View.GONE);
                isStarted();
            }
            if (ride_status.equalsIgnoreCase("CANCELLED")) {
                btn_complete.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);
                btn_payment.setVisibility(View.GONE);
                trackRide.setVisibility(View.GONE);

            }
            if (ride_status.equalsIgnoreCase("COMPLETED")) {
                btn_payment.setVisibility(View.GONE);
                trackRide.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);
                btn_complete.setVisibility(View.GONE);
                CheckRating(rideJson.getUser_id(), rideJson.getTravel_id(), rideJson.getDriver_id());
            }
            if (ride_status.equalsIgnoreCase("ACCEPTED")) {
                isStarted();
                if (payment_status.equals("") && payment_mode.equals("")) {
                    btn_complete.setVisibility(View.GONE);
                    btn_cancel.setVisibility(View.VISIBLE);
                    trackRide.setVisibility(View.GONE);
                    btn_payment.setVisibility(View.VISIBLE);
                } else if (payment_status.equals("") && payment_mode.equals("OFFLINE")) {
                    btn_cancel.setVisibility(View.GONE);
                    trackRide.setVisibility(View.GONE);
                    btn_payment.setVisibility(View.GONE);
                } else {
                    btn_complete.setText(getString(R.string.complete_ride));
                    btn_complete.setVisibility(View.VISIBLE);
                    trackRide.setVisibility(View.GONE);
                }
                if (!payment_status.equals("PAID") && payment_mode.equals("OFFLINE")) {
                    btn_complete.setVisibility(View.GONE);
                } else {
                }
            }
        }
        SetCustomFont setCustomFont = new SetCustomFont();
        setCustomFont.overrideFonts(getActivity(), view);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
    }

    public void completeTask() {
        //AlertDialogCreate(getString(R.string.ride_request_cancellation), getString(R.string.want_to_accept), "COMPLETED");
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ratingdialog);
        TextView titleTV = (TextView) dialog.findViewById(R.id.rateHeader);
        final TextView rateTV = (TextView) dialog.findViewById(R.id.rateTV);
        Button submitBtn = (Button) dialog.findViewById(R.id.submitRateBtn);
        Button cancelBtn = (Button) dialog.findViewById(R.id.cancelRateBtn);
        RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.ratingsBar);
        RatingBar fare_rating = (RatingBar) dialog.findViewById(R.id.fare_rating);
        EditText etComments = (EditText) dialog.findViewById(R.id.etComments);
        dialog.show();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //log.i("ibrahim", String.valueOf(ratingBar.getRating()));
//                //log.i("ibrahim", String.valueOf(fare_rating.getRating()));
//                //log.i("ibrahim getDriver_id", rideJson.getDriver_id());
//                //log.i("ibrahim getUser_id", rideJson.getUser_id());
//                //log.i("ibrahim getTravel_id", rideJson.getTravel_id());
                sendRating(ratingBar.getRating() * 2, fare_rating.getRating() * 2);
//                //log.i("ibrahim", "complete");
                if (etComments.getText().toString().length() > 0) {
                    AddComment(etComments.getText().toString());
                }
                dialog.cancel();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //log.i("ibrahim", "cancel");
                dialog.cancel();
            }
        });
    }

    public void travel_type_change(String user_id, String status, Boolean what) {
        RequestParams params = new RequestParams();
        params.put("ride_id", user_id);
        params.put("ride_type", status);
        Server.setHeader(SessionManager.getKEY());
        Server.post(Server.ride_type_change, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        String status = response.getJSONObject("data").getString("is_online");

                        if (what) {
                        } else {
                            if (status.equals("1")) {
                                switchCompat.setChecked(true);
                            } else {
                                switchCompat.setChecked(false);
                            }
                        }

                    } else {
                        Toast.makeText(getContext(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                    }
                } catch (NullPointerException e) {
                    System.err.println("Null pointer exception");
                } catch (JSONException e) {
                    Toast.makeText(getContext(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                //log.e("FAIl", throwable.toString() + ".." + errorResponse);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //log.e("FAIl", throwable.toString() + ".." + errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //log.e("FAIl", throwable.toString() + ".." + responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();

            }
        });
    }

    public void AddComment(String text) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(uid.toString());
        databaseRefID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String UserName = dataSnapshot.child("username").getValue(String.class);
                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);

                // Firebase code here
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("private_posts").child(rideJson.getDriver_id()).child("Comments").push();

                Map<String, Object> author = new HashMap<>();
                author.put("uid", user.getUid());
                author.put("username", UserName);
                author.put("photoURL", photoURL);
                Map<String, Object> userObject = new HashMap<>();
                userObject.put("author", author);
                userObject.put("text", text);
                userObject.put("travel_status", travel_status);
                userObject.put("type", "1");
                userObject.put("travel_id", 0);
                userObject.put("timestamp", ServerValue.TIMESTAMP);
                databaseRef.setValue(userObject);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    public void Updatepayemt(String ride_id, String payment_status) {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("payment_status", payment_status);
        params.put("payment_mode", "PAYPAL");
        Server.setContetntType();
        Server.setHeader(SessionManager.getKEY());

        Server.post("api/user/rides", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getActivity(), getString(R.string.payment_update), Toast.LENGTH_LONG).show();
                ((HomeActivity) getActivity()).onBackPressed();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                Toast.makeText(getActivity(), getString(R.string.error_payment), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity(), getString(R.string.server_not_respond), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
//                    swipeRefreshLayout.setRefreshing(false);

                }
            }
        });

    }

    public void AlertDialogCreate(String title, String message, final String status) {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new AlertDialog.Builder(getActivity())
                .setIcon(drawable)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sendStatus(rideJson.getRide_id(), status);

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    public void cancelAlert(String title, String message, final String status) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(title);

        alertDialog.setMessage(message);
        alertDialog.setCancelable(true);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        alertDialog.setIcon(drawable);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendStatus(rideJson.getRide_id(), status);
            }
        });


        alertDialog.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.cancel();
            }
        });
        alert = alertDialog.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
    }

    public void sendStatus(String ride_id, final String status) {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("status", status);
        Server.setHeader(SessionManager.getKEY());
        Server.setContetntType();
        Server.post("api/user/rides", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    AcceptedRequestFragment acceptedRequestFragment = new AcceptedRequestFragment();
                    Bundle bundle = null;
                    if (response.has("status") && response.getString("status").equals("success")) {
                        updateRideFirebase(travel_status, status, payment_status, payment_mode);
                        updateNotificationFirebase(status);
                        updateTravelCounterFirebase(status);
                        if (status.contains("CANCELLED") || status.contains("COMPLETED")) {
                            if (response.has("ride_id")) {
                                String ride_id = response.getString("ride_id");
                                if (ride_id != null && !ride_id.contains("null")) {
                                    addRideFirebase(ride_id, "", "REQUESTED", "", "");
                                }
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    System.err.println("Null pointer exception");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
//                    swipeRefreshLayout.setRefreshing(false);
                }
                if (status.contains("COMPLETED")) {
                    btn_complete.setVisibility(View.GONE);
                }
            }
        });

    }

    public void updateTravelCounterFirebase(String status) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseTravel fbTravel = dataSnapshot.getValue(firebaseTravel.class);
                if (fbTravel != null) {
                    if (status.contains("ACCEPTED")) {
                        try {
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Counters").child("ACCEPTED");
                            databaseRef.setValue(fbTravel.Counters.ACCEPTED + 1);
                        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        }
                    } else if (status.contains("COMPLETED")) {
                        //log.i("ibrahim", "fbTravel");
                        try {
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Counters").child("COMPLETED");
                            databaseRef.setValue(fbTravel.Counters.COMPLETED + 1);

                            DatabaseReference databaseRef1 = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Counters").child("ACCEPTED");
                            databaseRef1.setValue(fbTravel.Counters.ACCEPTED - 1);
                        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    public void CheckRating(String user_id, String travel_id, String driver_id) {
        final RequestParams params = new RequestParams();
        params.put("user_id", user_id);
        params.put("travel_id", travel_id);
        params.put("driver_id", driver_id);
        Server.setHeader(SessionManager.getKEY());
        Server.get("api/user/rating/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //log.i("ibrahim", "reponse");
                //log.i("ibrahim", response.toString());
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        if (response.has("data") && response.getString("data").equalsIgnoreCase("true")) {
                            btn_payment.setVisibility(View.GONE);
                        } else {
                            btn_complete.setVisibility(View.VISIBLE);
                        }
                    } else {
//                        Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                        //log.i("ibrahim", "else not success");
                    }
                } catch (NullPointerException e) {
                    System.err.println("Null pointer exception");
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
//                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void updateRideFirebase(String travel_status, String ride_status, String payment_status, String payment_mode) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("rides").child(rideJson.getRide_id());
        Map<String, Object> rideObject = new HashMap<>();

        rideObject.put("ride_status", ride_status);
        rideObject.put("travel_status", travel_status);
        rideObject.put("payment_status", payment_status);
        rideObject.put("payment_mode", payment_mode);
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        databaseRef.setValue(rideObject);
    }

    public void addRideFirebase(String ride_id_param, String travel_status, String ride_status, String payment_status, String payment_mode) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("rides").child(String.valueOf(ride_id_param));
        Map<String, Object> rideObject = new HashMap<>();

        rideObject.put("ride_status", ride_status);
        rideObject.put("travel_status", travel_status);
        rideObject.put("payment_status", payment_status);
        rideObject.put("payment_mode", payment_mode);
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        databaseRef.setValue(rideObject);
    }

    public void updateNotificationFirebase(String notificationText) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Notifications").child(rideJson.getDriver_id()).push();
        Map<String, Object> rideObject = new HashMap<>();
        rideObject.put("ride_id", rideJson.getRide_id());
        rideObject.put("travel_id", rideJson.getTravel_id());
        rideObject.put("text", notificationText.toLowerCase());
        rideObject.put("readStatus", "0");
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        rideObject.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseRef.setValue(rideObject);
    }

    public void sendRating(float DriverRatingST, float FareRatingST) {

        RequestParams params = new RequestParams();
        params.put("driver_id", rideJson.getDriver_id());
        params.put("user_id", rideJson.getUser_id());
        params.put("travel_id", rideJson.getTravel_id());
        params.put("driver_rate", DriverRatingST);
        params.put("travel_rate", FareRatingST);


        Server.setHeader(SessionManager.getKEY());
        Server.setContetntType();
        Server.post("api/user/addRating", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
//                //log.i("ibrahim",response.toString());
//                try {
//                    if (response.has("status") && response.getString("status").equals("success")) {
////                        sendStatus(rideJson.getRide_id(), "COMPLETED");
//                        ////log.i("ibrahim","complete travel");
//                    } else {
//                        //String error = response.getString("data");
//                        //Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
//                    }
//                } catch (NullPointerException e) {
//                    System.err.println("Null pointer exception");
//                }catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
//                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
//                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
//                //log.i("ibrahim", "Failure");
            }

            @Override
            public void onFinish() {
                super.onFinish();
//                //log.i("ibrahim", "onFinish");
                sendStatus(rideJson.getRide_id(), "COMPLETED");
                if (getActivity() != null) {
//                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    public void configPaypal() {
        config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(Server.PAYPAL_KEY)
                .merchantName(getString(R.string.merchant_name))
                .merchantPrivacyPolicyUri(
                        Uri.parse(getString(R.string.merchant_privacy)))
                .merchantUserAgreementUri(
                        Uri.parse(getString(R.string.merchant_user_agreement)));
    }

    public void MakePayment() {

        if (rideJson.getAmount() != null && !rideJson.getAmount().equals("")) {
            Intent intent = new Intent(getActivity(), PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            getActivity().startService(intent);
            thingToBuy = new PayPalPayment(new java.math.BigDecimal(String.valueOf(rideJson.getAmount())), getString(R.string.paypal_payment_currency), "Ride Payment",
                    PayPalPayment.PAYMENT_INTENT_SALE);
            Intent payment = new Intent(getActivity(), PaymentActivity.class);
            payment.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
            payment.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            startActivityForResult(payment, REQUEST_CODE_PAYMENT);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                //  String.valueOf(finalfare)
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject()
                                .toString(4));
                        Updatepayemt(rideJson.getRide_id(), "PAID");

                    } catch (NullPointerException e) {
                        System.err.println("Null pointer exception");
                    } catch (JSONException e) {

                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), getString(R.string.payment_hbeen_cancelled), Toast.LENGTH_LONG).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                //  Log.d("payment", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
//                    try {
////                        //log.i("FuturePaymentExample", auth.toJSONObject().toString(4));
//
//                        String authorization_code = auth.getAuthorizationCode();
////                        Log.d("FuturePaymentExample", authorization_code);
//
//                        /*sendAuthorizationToServer(auth);
//                        Toast.makeText(getActivity(),
//                                "Future Payment code received from PayPal",
//                                Toast.LENGTH_LONG).show();*/
////                        //log.e("paypal", "future Payment code received from PayPal  :" + authorization_code);
//
//                    }
//                    catch (NullPointerException e) {
//                    System.err.println("Null pointer exception");
//                }catch (JSONException e) {
//                        Toast.makeText(getActivity(), "failure Occurred", Toast.LENGTH_LONG).show();
//                        //log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
//                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), getString(R.string.payment_hbeen_cancelled), Toast.LENGTH_LONG).show();

//                Log.d("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {

                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

//                Log.d("FuturePaymentExample","Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }

    }

    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            return true;

        } else {
            gpsTracker.showSettingsAlert();
            return false;
        }


    }

    public void turnonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.

                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and setting the result in onActivityResult().
                                status.startResolutionForResult(getActivity(), 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {

                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        databaseTravelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fbTravel = dataSnapshot.getValue(firebaseTravel.class);
//                //log.i("ibrahim", fbTravel.toString());
//                //log.i("ibrahim_travel", fbTravel.driver_id);
//                //log.i("ibrahim_travel", fbTravel.Clients.toString());
                drawMap(fbTravel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    void isStarted() {
//        //log.i("ibrahim", "isStarted");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tracking/" + rideJson.getRide_id());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    Tracking tracking = dataSnapshot.getValue(Tracking.class);

                    if (tracking.getStatus() != null && getActivity() != null) {
                        switch (tracking.getStatus()) {
                            case "accepted":
                                trackRide.setText(getString(R.string.Track_Driver));

                                trackRide.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        askCompactPermissions(permissions, new PermissionResult() {
                                            @Override
                                            public void permissionGranted() {
                                                if (GPSEnable()) {

                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable("data", rideJson);
                                                    MapView mapView = new MapView();
                                                    mapView.setArguments(bundle);
                                                    ((HomeActivity) getActivity()).changeFragment(mapView, "MapView");


                                                } else {
                                                    turnonGps();
                                                }
                                            }

                                            @Override
                                            public void permissionDenied() {

                                            }

                                            @Override
                                            public void permissionForeverDenied() {

                                            }
                                        });
                                    }
                                });
                                break;
                            case "started":
                                trackRide.setText(getString(R.string.Track_Ride));

                                trackRide.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        askCompactPermissions(permissions, new PermissionResult() {
                                            @Override
                                            public void permissionGranted() {
                                                if (GPSEnable()) {

                                                    try {
                                                        String[] latlong = rideJson.getpickup_location().split(",");
                                                        double latitude = Double.parseDouble(latlong[0]);
                                                        double longitude = Double.parseDouble(latlong[1]);
                                                        String[] latlong1 = rideJson.getdrop_location().split(",");
                                                        double latitude1 = Double.parseDouble(latlong1[0]);
                                                        double longitude1 = Double.parseDouble(latlong1[1]);

                                                        Point origin = Point.fromLngLat(longitude, latitude);
                                                        Point destination = Point.fromLngLat(longitude1, latitude1);

                                                        fetchRoute(origin, destination);


                                                    } catch (Exception e) {
                                                        Toast.makeText(getActivity(), e.toString() + " ", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    turnonGps();
                                                }
                                            }

                                            @Override
                                            public void permissionDenied() {

                                            }

                                            @Override
                                            public void permissionForeverDenied() {

                                            }
                                        });
                                    }
                                });
                                break;
                        }
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void launchNavigation() {


        if (GPSEnable()) {

            try {
                String[] latlong = rideJson.getpickup_location().split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                String[] latlong1 = rideJson.getdrop_location().split(",");
                double latitude1 = Double.parseDouble(latlong1[0]);
                double longitude1 = Double.parseDouble(latlong1[1]);

                Point origin = Point.fromLngLat(longitude, latitude);
                Point destination = Point.fromLngLat(longitude1, latitude1);

                fetchRoute(origin, destination);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.toString() + " ", Toast.LENGTH_SHORT).show();
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void fetchRoute(Point origin, Point destination) {
        NavigationRoute.builder(getActivity())
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        DirectionsRoute directionsRoute = response.body().routes().get(0);
                        startNavigation(directionsRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
    }

    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationLauncherOptions.Builder navigationLauncherOptions = NavigationLauncherOptions.builder();
        navigationLauncherOptions.shouldSimulateRoute(false);
        navigationLauncherOptions.enableOffRouteDetection(true);
        navigationLauncherOptions.snapToRoute(true);
        navigationLauncherOptions.directionsRoute(directionsRoute);
        NavigationLauncher.startNavigation(getActivity(), navigationLauncherOptions.build());
    }

    @Override
    public boolean onBackPressed() {
        this.startActivity(new Intent(getContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        return false;
    }

    @Override
    public int getBackPriority() {
        return 0;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            if (mMapView != null) {
                mMapView.onSaveInstanceState(outState);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try {
            if (mMapView != null) {
                mMapView.onLowMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (mMapView != null) {
                mMapView.onStop();
            }
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (mMapView != null) {
                mMapView.onResume();
            }
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;


        myMap.setMaxZoomPreference(80);
//        myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        requestDirection();

        myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                View v = null;
                if (getActivity() != null) {
                    v = getActivity().getLayoutInflater().inflate(R.layout.view_custom_marker, null);
                    TextView title = (TextView) v.findViewById(R.id.t);
                    TextView t1 = (TextView) v.findViewById(R.id.t1);
                    TextView t2 = (TextView) v.findViewById(R.id.t2);
                    Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
                    t1.setTypeface(font);
                    t2.setTypeface(font);
                    String name = marker.getTitle();
                    title.setText(name);
                    String info = marker.getSnippet();
                    t1.setText(info);
//                    driver_id = (String) marker.getTag();
//                    drivername = marker.getTitle();
                }
                return v;
            }
        });
        if (myMap != null) {
            tunonGps();
        }
    }

    public void requestDirection() {

        try {
            Snackbar.make(view, getString(R.string.direct_requesting), Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
        GoogleDirection.withServerKey(getString(R.string.google_android_map_api_key))
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (getActivity() != null) {
            if (direction.isOK()) {
                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                myMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
                myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title(getString(R.string.pick_up_location)).snippet(rideJson.getpickup_address()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                myMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title(getString(R.string.drop_up_location)).snippet(rideJson.getDrop_address()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
                calculateDistance(Double.valueOf(direction.getRouteList().get(0).getLegList().get(0).getDistance().getValue()) / 1000);
            } else {
//                distanceAlert(direction.getErrorMessage());
                //calculateFare.setVisibility(View.GONE);
//                dismiss();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void onConnected(@Nullable Bundle bundle) {
        try {
            @SuppressLint("MissingPermission") android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                if (myMap != null) {
                    myMap.clear();
                    my_marker = myMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("You are here.").icon(BitmapDescriptorFactory.fromResource(R.drawable.user_default)));
                    my_marker.showInfoWindow();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15);
                    myMap.animateCamera(cameraUpdate);

                    myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
//                            Toast.makeText(getActivity(), "Lat " + latLng.latitude + " " + "Long " + latLng.longitude, Toast.LENGTH_LONG).show();
                            if (distancematrix_informations.getVisibility() == View.VISIBLE) {
                                distancematrix_informations.startAnimation(animFadeOut);
                                distancematrix_informations.setVisibility(View.GONE);
                            } else {
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //test GoogMatrixRequest
                                        try {
//                                        String googleMatrixResponse = GoogMatrixRequest.getGoogMatrixRequest("37.7680296,-122.4375126", "37.7663444,-122.4412006");
                                            String googleMatrixResponse = GoogMatrixRequest.getGoogMatrixRequest(currentLatitude + "," + currentLongitude, latLng.latitude + "," + latLng.longitude);
                                            try {
                                                JSONObject jsonObject = new JSONObject(googleMatrixResponse);
                                                JSONArray dist = (JSONArray) jsonObject.get("rows");
                                                JSONObject obj2 = (JSONObject) dist.get(0);
                                                JSONArray disting = (JSONArray) obj2.get("elements");
                                                JSONObject obj3 = (JSONObject) disting.get(0);
                                                JSONObject obj4 = (JSONObject) obj3.get("distance");
                                                JSONObject obj5 = (JSONObject) obj3.get("duration");

                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            System.out.println(obj4.get("text"));
                                                            System.out.println(obj5.get("text"));
                                                            txt_distance.setText(obj4.get("text").toString());
                                                            txt_timedistance.setText(obj5.get("text").toString());
                                                            distancematrix_informations.setVisibility(View.VISIBLE);
                                                            distancematrix_informations.startAnimation(animFadeIn);
                                                        } catch (NullPointerException e) {
                                                            System.err.println("Null pointer exception");
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });

                                            } catch (JSONException err) {
                                                Log.d("Error", err.toString());
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        //
                                    }
                                });

                                thread.start();
                            }
                        }
                    });
                    myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if (distancematrix_informations.getVisibility() == View.VISIBLE) {
                                distancematrix_informations.startAnimation(animFadeOut);
                                distancematrix_informations.setVisibility(View.GONE);
                            } else {
                                LatLng position = marker.getPosition();
                                //                            Toast.makeText(getActivity(), "Lat " + position.latitude + " " + "Long " + position.longitude, Toast.LENGTH_LONG).show();
                                Thread thread = new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        //test GoogMatrixRequest
                                        try {
//                                        String googleMatrixResponse = GoogMatrixRequest.getGoogMatrixRequest("37.7680296,-122.4375126", "37.7663444,-122.4412006");
                                            String googleMatrixResponse = GoogMatrixRequest.getGoogMatrixRequest(currentLatitude + "," + currentLongitude, position.latitude + "," + position.longitude);
                                            try {
                                                JSONObject jsonObject = new JSONObject(googleMatrixResponse);
                                                JSONArray dist = (JSONArray) jsonObject.get("rows");
                                                JSONObject obj2 = (JSONObject) dist.get(0);
                                                JSONArray disting = (JSONArray) obj2.get("elements");
                                                JSONObject obj3 = (JSONObject) disting.get(0);
                                                JSONObject obj4 = (JSONObject) obj3.get("distance");
                                                JSONObject obj5 = (JSONObject) obj3.get("duration");

                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            System.out.println(obj4.get("text"));
                                                            System.out.println(obj5.get("text"));
                                                            txt_distance.setText(obj4.get("text").toString());
                                                            txt_timedistance.setText(obj5.get("text").toString());
                                                            distancematrix_informations.setVisibility(View.VISIBLE);
                                                            distancematrix_informations.startAnimation(animFadeIn);
                                                        } catch (NullPointerException e) {
                                                            System.err.println("Null pointer exception");
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });


                                            } catch (JSONException err) {
                                                Log.d("Error", err.toString());
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        //
                                    }
                                });

                                thread.start();
                            }
                            return true;
                        }
                    });

                    databaseTravelRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            fbTravel = dataSnapshot.getValue(firebaseTravel.class);

//                            //log.i("ibrahim", dataSnapshot.toString());
//                            //log.i("ibrahim", fbTravel.toString());
//                            //log.i("ibrahim_travel1", fbTravel.driver_id);
//                            //log.i("ibrahim_travel1", fbTravel.clients.toString());
                            drawMap(fbTravel);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                        }
                    });
                }
                setCurrentLocation(currentLatitude, currentLongitude);
            }
        } catch (Exception e) {

        }

    }

    @SuppressLint("MissingPermission")
    public void drawMap(firebaseTravel fbTravel) {
        try {
            @SuppressLint("MissingPermission") android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            //log.i("ibrahim", "drawRoute-1");
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
//                //log.i("ibrahim", "drawRoute0");
                if (myMap != null) {
                    myMap.clear();
                    my_marker = myMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("You are here.").icon(BitmapDescriptorFactory.fromResource(R.drawable.user_default)));
                    my_marker.showInfoWindow();
//                    //log.i("ibrahim", "drawRoute");
                    for (Map.Entry<String, String> entry : fbTravel.Clients.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        databaseClientsLocation = FirebaseDatabase.getInstance().getReference("Location").child(value);
                        databaseClientsLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null) {
                                    firebaseClients clients = dataSnapshot.getValue(firebaseClients.class);
//                                    firebaseUsers.put(dataSnapshot.getKey(),clients);
                                    if (clients != null) {
                                        my_marker = myMap.addMarker(new MarkerOptions().position(new LatLng(clients.latitude, clients.longitude)).title("User").icon(BitmapDescriptorFactory.fromResource(R.drawable.user_default)));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
//                        //log.i("ibrahim", value.toString());
                    }

                    databaseDriverLocation = FirebaseDatabase.getInstance().getReference("Location").child(fbTravel.driver_id);
                    databaseDriverLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            //log.i("ibrahim","inside_dataSnapshot");
                            if (dataSnapshot != null) {
//                                //log.i("ibrahim","dataSnapshot");
//                                //log.i("ibrahim",dataSnapshot.toString());
                                firebaseClients Driver = dataSnapshot.getValue(firebaseClients.class);
//                                //log.i("ibrahim","dataSnapshot");
//                                //log.i("ibrahim",Driver.latitude.toString() + Driver.longitude.toString());

                                my_marker = myMap.addMarker(new MarkerOptions().position(new LatLng(Driver.latitude, Driver.longitude)).title("Driver").icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi)));

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
                setCurrentLocation(currentLatitude, currentLongitude);
            }
        } catch (Exception e) {

        }
    }

    public void setCurrentLocation(final Double lat, final Double log) {
        try {
            my_marker.setPosition(new LatLng(lat, log));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15);
            myMap.animateCamera(cameraUpdate);
            RequestParams par = new RequestParams();
            Server.setHeader(SessionManager.getKEY());
            par.put("user_id", SessionManager.getUserId());
            par.add("latitude", String.valueOf(currentLatitude));
            par.add("longitude", String.valueOf(currentLongitude));
            Server.post(Server.UPDATE, par, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                }

            });
        } catch (Exception e) {

        }
    }

    public void tunonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            getCurrentlOcation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and checkky the result in onActivityResult().
                                status.startResolutionForResult(getActivity(), 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    public void getCurrentlOcation() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
    }

    public void NearBy() {

        database_fav_places = FirebaseDatabase.getInstance().getReference("Fav_Places").child(SessionManager.getUserId());
        listener_fav_places = database_fav_places.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Fav_Places fav_places = postSnapshot.getValue(Fav_Places.class);
                    fav_places.id = postSnapshot.getKey();
                    origin = new LatLng(fav_places.latitude, fav_places.longitude);
                    Marker myMarker = myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title(getString(R.string.place_name)).snippet(fav_places.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void calculateDistance(Double aDouble) {

        distance = String.valueOf(aDouble);
//        confirm.setEnabled(true);

        if (aDouble != null) {
            if (fare != null && fare != 0.0) {
                DecimalFormat dtime = new DecimalFormat("##.##");
                Double ff = aDouble * fare;

                try {

                    if (dtime.format(ff).contains(",")) {
                        String value = dtime.format(ff).replaceAll(",", ".");
                        finalfare = Double.valueOf(value);
                    } else {

                        finalfare = Double.valueOf(dtime.format(ff));
                    }
//                    dismiss();

                } catch (Exception e) {

                }

                // txt_fare.setText(finalfare + " " + SessionManager.getUnit());
                //  txt_fare.setText(finalfare + " $ ");

            } else {
                //txt_fare.setText(SessionManager.getUnit());
            }
        }
    }
}