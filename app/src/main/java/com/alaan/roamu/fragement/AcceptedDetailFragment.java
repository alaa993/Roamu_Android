package com.alaan.roamu.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamu.pojo.Post;
import com.alaan.roamu.pojo.firebaseRide;
import com.alaan.roamu.pojo.firebaseTravel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.custom.CheckConnection;
import com.alaan.roamu.custom.GPSTracker;
import com.alaan.roamu.custom.SetCustomFont;
import com.alaan.roamu.pojo.PendingRequestPojo;
import com.alaan.roamu.pojo.Tracking;
import com.alaan.roamu.session.SessionManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcceptedDetailFragment extends FragmentManagePermission implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private View view;
    AppCompatButton trackRide;
    private String mobile = "";
    AppCompatButton btn_cancel, btn_payment, btn_complete;
    LinearLayout linearChat;
    ImageView call_phone;
    TextView title, drivername, pickup_location, drop_location, fare;
    private AlertDialog alert;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final String CONFIG_ENVIRONMENT = Server.ENVIRONMENT;
    private static PayPalConfiguration config;
    PayPalPayment thingToBuy;
    TableRow tr_Driver1, tr_Driver2, phone_row, tr_pickup, tr_empty_set, tr_fare, tr_total_fare, tr_fare_rate, tr_driver_rate, tr_travel_count, tr_comment;
    PendingRequestPojo rideJson;
    String permissions[] = {PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Dialog dialog;
    AppCompatButton confirm, cancel, mobilenumber;
    Double finalfare;
    Place pickup, drop, s_drop, s_pic;
    com.google.android.gms.maps.MapView mapView;
    GoogleMap myMap;
    private LatLng origin;
    private LatLng destination;
    private String networkAvailable;
    private String tryAgain;
    private String directionRequest;
    TextView textView1, textView2, textView3, textView4, textView5,
            textView6, textView7, textView8, textView9, textView10, textView11,
            txt_fare_view, txt_name, txt_number, txt_vehiclename,
            dateandtime, TimeVal, txt_bag, txt_smoke, car_name, mobilenumbertext, carConditon, carConditonrating;

    TextView txt_Driver_name, txt_city, txt_Empty_Seats,
            txt_DriverRate, txt_TravelsCount, txt_PickupPoint, txt_fare, fianl_fare, num_set;

    private ImageView DriverAvatar;
    private ImageView DriverCar;
    EditText cobun_num;
    String driver_id;
    String travel_id;
    private String user_id;
    private String pickup_address, drop_address, dateandtime_val, time_val;
    String distance;
    TextView calculateFare;
    Snackbar snackbar;
    Button btn_cobo;
    TableRow rating, car, fare_rating;
    DatabaseReference databaseRides;
    Bundle bundle;
    private String travel_status;
    private String ride_status;
    private String payment_status = "";
    private String payment_mode = "";

    Button acc_d_f_home_button;

    ValueEventListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("ibrahim", "onCreateView");
        view = inflater.inflate(R.layout.accepted_detail_fragmnet, container, false);
        bundle = getArguments();
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.passenger_info));
        BindView();
        configPaypal();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        listener = databaseRides.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseRide fbRide = dataSnapshot.getValue(firebaseRide.class);
                Log.i("ibrahim ride", "----------");
                travel_status = fbRide.travel_status;
                ride_status = fbRide.ride_status;
                payment_status = fbRide.payment_status;
                payment_mode = fbRide.payment_mode;
                setupData();
                changeFragment();
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

    public void onRefresh() {
        Log.i("ibrahim", "onRefresh");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public void BindView() {
        acc_d_f_home_button = (Button) view.findViewById(R.id.acc_d_f_home_button);
        mapView = (com.google.android.gms.maps.MapView) view.findViewById(R.id.mapview);

        //ibrahim
//        if (Locale.getDefault().getLanguage().equals("ar")) {
//            LinearLayout linrtl=(LinearLayout)findViewById(R.id.linrtl);
//            linrtl.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//        } else {
//
//        }

        tr_Driver1 = (TableRow) view.findViewById(R.id.tr_Driver1);
        tr_Driver2 = (TableRow) view.findViewById(R.id.tr_Driver2);
        phone_row = (TableRow) view.findViewById(R.id.phone_row);
        tr_pickup = (TableRow) view.findViewById(R.id.tr_pickup);
        tr_empty_set = (TableRow) view.findViewById(R.id.tr_empty_set);
        tr_fare = (TableRow) view.findViewById(R.id.tr_fare);
        tr_total_fare = (TableRow) view.findViewById(R.id.tr_total_fare);
        tr_fare_rate = (TableRow) view.findViewById(R.id.tr_fare_rate);
        tr_driver_rate = (TableRow) view.findViewById(R.id.tr_driver_rate);
        tr_travel_count = (TableRow) view.findViewById(R.id.tr_travel_count);
        tr_comment = (TableRow) view.findViewById(R.id.tr_comment);

        calculateFare = (TextView) view.findViewById(R.id.txt_calfare);
        confirm = (AppCompatButton) view.findViewById(R.id.btn_confirm);
        cancel = (AppCompatButton) view.findViewById(R.id.btn_cancel);
        mobilenumber = (AppCompatButton) view.findViewById(R.id.mobilenumber);
        pickup_location = (TextView) view.findViewById(R.id.txt_pickup);
        drop_location = (TextView) view.findViewById(R.id.txt_drop);
        textView1 = (TextView) view.findViewById(R.id.textView1);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        rating = (TableRow) view.findViewById(R.id.car_rating);
        car = (TableRow) view.findViewById(R.id.car_image);
        fare_rating = (TableRow) view.findViewById(R.id.fare_rating);
        textView3 = (TextView) view.findViewById(R.id.textView3);
        car_name = (TextView) view.findViewById(R.id.car_name);
        mobilenumbertext = (TextView) view.findViewById(R.id.mobilenumbertext);
        textView4 = (TextView) view.findViewById(R.id.textView4);
        num_set = (TextView) view.findViewById(R.id.num_set);
        txt_fare = (TextView) view.findViewById(R.id.txt_fare);
        btn_cobo = (Button) view.findViewById(R.id.btn_cobo);
        cobun_num = (EditText) view.findViewById(R.id.cobun_num);
        fianl_fare = (TextView) view.findViewById(R.id.fianl_fare);
        dateandtime = (TextView) view.findViewById(R.id.dateTimeVal);
        TimeVal = (TextView) view.findViewById(R.id.TimeVal);
        txt_bag = (TextView) view.findViewById(R.id.bag_val);
        txt_smoke = (TextView) view.findViewById(R.id.smoke_val);
        textView5 = (TextView) view.findViewById(R.id.textView5);
        textView6 = (TextView) view.findViewById(R.id.textView6);
        textView7 = (TextView) view.findViewById(R.id.textView7);
        textView8 = (TextView) view.findViewById(R.id.textView8);
        textView9 = (TextView) view.findViewById(R.id.textView9);
        textView10 = (TextView) view.findViewById(R.id.textView10);
        textView11 = (TextView) view.findViewById(R.id.textView11);
        carConditon = (TextView) view.findViewById(R.id.carConditon);
        carConditonrating = (TextView) view.findViewById(R.id.carConditonrating);
        txt_fare_view = (TextView) view.findViewById(R.id.txt_fare_view);
        txt_name = (TextView) view.findViewById(R.id.txt_name);
        txt_Driver_name = (TextView) view.findViewById(R.id.Driver_name);
        txt_city = (TextView) view.findViewById(R.id.txt_city);
        txt_Empty_Seats = (TextView) view.findViewById(R.id.txt_Empty_Seats);
        txt_DriverRate = (TextView) view.findViewById(R.id.txt_DriverRate);
        txt_TravelsCount = (TextView) view.findViewById(R.id.txt_TravelsCount);
        txt_PickupPoint = (TextView) view.findViewById(R.id.txt_PickupPoint);
        txt_number = (TextView) view.findViewById(R.id.txt_number);
        txt_vehiclename = (TextView) view.findViewById(R.id.txt_vehiclename);
        title = (TextView) view.findViewById(R.id.title);
        DriverAvatar = (ImageView) view.findViewById(R.id.DriverImage);
        DriverCar = (ImageView) view.findViewById(R.id.carImage);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        btn_complete = (AppCompatButton) view.findViewById(R.id.btn_complete);
        drivername = (TextView) view.findViewById(R.id.Driver_name);
        trackRide = (AppCompatButton) view.findViewById(R.id.btn_trackride);
        btn_payment = (AppCompatButton) view.findViewById(R.id.btn_payment);
        btn_cancel = (AppCompatButton) view.findViewById(R.id.btn_cancel);
        pickup_location.setSelected(true);
        drop_location.setSelected(true);
        if (bundle != null) {
            rideJson = (PendingRequestPojo) bundle.getSerializable("data");

            if (rideJson.getStatus().contains("REQUESTED")) {
                Log.i("ibrahim", "REQUESTED");
                drivername.setVisibility(View.GONE);
                DriverAvatar.setVisibility(View.GONE);
                car_name.setVisibility(View.GONE);
                DriverCar.setVisibility(View.GONE);
                mobilenumber.setVisibility(View.GONE);
                mobilenumbertext.setVisibility(View.GONE);
                textView9.setVisibility(View.GONE);
                txt_PickupPoint.setVisibility(View.GONE);
                textView6.setVisibility(View.GONE);
                txt_Empty_Seats.setVisibility(View.GONE);
                txt_fare_view.setVisibility(View.GONE);
                fianl_fare.setVisibility(View.GONE);
                textView7.setVisibility(View.GONE);
                txt_DriverRate.setVisibility(View.GONE);
                textView8.setVisibility(View.GONE);
                textView5.setVisibility(View.GONE);
                carConditon.setVisibility(View.GONE);
                carConditonrating.setVisibility(View.GONE);
                txt_TravelsCount.setVisibility(View.GONE);


                tr_Driver1.setVisibility(View.GONE);
                tr_Driver2.setVisibility(View.GONE);
                phone_row.setVisibility(View.GONE);
                tr_pickup.setVisibility(View.GONE);
                tr_empty_set.setVisibility(View.GONE);
                tr_fare.setVisibility(View.GONE);
                tr_total_fare.setVisibility(View.GONE);
                tr_fare_rate.setVisibility(View.GONE);
                tr_driver_rate.setVisibility(View.GONE);
                tr_travel_count.setVisibility(View.GONE);
                tr_comment.setVisibility(View.GONE);
            } else {
                Log.i("ibrahim", "not REQUESTED");
                drivername.setVisibility(View.VISIBLE);
                DriverAvatar.setVisibility(View.VISIBLE);
                car_name.setVisibility(View.VISIBLE);
                DriverCar.setVisibility(View.VISIBLE);
                mobilenumber.setVisibility(View.VISIBLE);
                mobilenumbertext.setVisibility(View.VISIBLE);
                textView9.setVisibility(View.VISIBLE);
                txt_PickupPoint.setVisibility(View.VISIBLE);
                textView6.setVisibility(View.VISIBLE);
                txt_Empty_Seats.setVisibility(View.VISIBLE);
                txt_fare_view.setVisibility(View.VISIBLE);
                fianl_fare.setVisibility(View.VISIBLE);
                textView7.setVisibility(View.VISIBLE);
                txt_DriverRate.setVisibility(View.VISIBLE);
                textView8.setVisibility(View.VISIBLE);
                textView5.setVisibility(View.VISIBLE);
                carConditon.setVisibility(View.VISIBLE);
                carConditonrating.setVisibility(View.VISIBLE);
                txt_TravelsCount.setVisibility(View.VISIBLE);

                tr_Driver1.setVisibility(View.VISIBLE);
                tr_Driver2.setVisibility(View.VISIBLE);
                phone_row.setVisibility(View.VISIBLE);
                tr_pickup.setVisibility(View.VISIBLE);
                tr_empty_set.setVisibility(View.VISIBLE);
                tr_fare.setVisibility(View.VISIBLE);
                tr_total_fare.setVisibility(View.VISIBLE);
                tr_fare_rate.setVisibility(View.VISIBLE);
                tr_driver_rate.setVisibility(View.VISIBLE);
                tr_travel_count.setVisibility(View.VISIBLE);
                tr_comment.setVisibility(View.VISIBLE);
            }

            databaseRides = FirebaseDatabase.getInstance().getReference("rides").child(rideJson.getRide_id());
            travel_status = rideJson.getTravel_status();
            ride_status = rideJson.getStatus();
            payment_status = rideJson.getPayment_status();
            payment_mode = rideJson.getPayment_mode();

            pickup_location.setText(rideJson.getpickup_address() + " ");
            drop_location.setText(rideJson.getDrop_address());
            drivername.setText(rideJson.getDriver_name());
            mobilenumber.setText(rideJson.getDriver_mobile());
            mobile = rideJson.getDriver_mobile();
            TimeVal.setText(rideJson.getTime());
            dateandtime.setText(rideJson.getDate());
            txt_Empty_Seats.setText(rideJson.getempty_set());
//            txt_city.setText(rideJson.);
            car_name.setText(rideJson.model);
            txt_Driver_name.setText(rideJson.getDriver_name());
            num_set.setText(rideJson.getBooked_set());
            mobilenumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("ibrahim", "mobile call function");
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
                    btn_cancel.setVisibility(View.VISIBLE);
                    trackRide.setVisibility(View.GONE);
                    btn_payment.setVisibility(View.VISIBLE);
                    btn_complete.setVisibility(View.GONE);
                } else {
                    btn_complete.setText(getString(R.string.complete_ride));
                    btn_complete.setVisibility(View.VISIBLE);
                    trackRide.setVisibility(View.GONE);
//                    mobilenumber_row.setVisibility(View.VISIBLE);
                }
                if (!payment_status.equals("PAID") && payment_mode.equals("OFFLINE")) {
                    btn_complete.setVisibility(View.GONE);
                } else {
                }
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
                                        swipeRefreshLayout.setRefreshing(true);
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
                                            swipeRefreshLayout.setRefreshing(false);
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
        textView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                Log.i("ibrahim","textView10");
//                Log.i("ibrahim",rideJson.getDriver_id());
//                bundle.putSerializable("data", rideJson.getDriver_id());
//                UsersCommentsFragment detailFragment = new UsersCommentsFragment();
//                detailFragment.setArguments(bundle);
//
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), detailFragment);
//                fragmentTransaction.commit();

                Bundle bundle = new Bundle();
                bundle.putSerializable("data", rideJson.getDriver_id());
                UsersCommentsFragment detailFragment = new UsersCommentsFragment();
                detailFragment.setArguments(bundle);
                ((HomeActivity) getActivity()).changeFragment(detailFragment, "UsersCommentsFragment");
            }
        });

        textView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("travel_id", rideJson.getTravel_id());
                NoteFragment noteFragment = new NoteFragment();
                noteFragment.setArguments(bundle);
                ((HomeActivity) getActivity()).changeFragment(noteFragment, "fragment_note");
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
                    Log.i("ibrahim_waited", "--------------");
                    sendStatus(rideJson.getRide_id(), "ACCEPTED");
                    updateTravelFirebase();
                    deletePostFirebase();
                    //send notification
                    addNotificationFirebase(rideJson.getRide_id(), rideJson.getTravel_id());
                }
                if (ride_status.equalsIgnoreCase("ACCEPTED") || ride_status.equalsIgnoreCase("COMPLETED")) {// edited by ibrahim it was completed date:21/1/2021
                    Log.i("ibrahim_completed", "--------------");
                    completeTask();
                }
            }
        });
        acc_d_f_home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        //        {
//            databaseRides.addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    if (dataSnapshot.getKey().contains("travel_status"))
//                    {
//                        travel_status = dataSnapshot.getValue().toString();
//                    }
//                    else if (dataSnapshot.getKey().contains("ride_status"))
//                    {
//                        ride_status = dataSnapshot.getValue().toString();
//                    }
//                    else if (dataSnapshot.getKey().contains("payment_status"))
//                    {
//                        payment_status = dataSnapshot.getValue().toString();
//                    }
//                    else if (dataSnapshot.getKey().contains("payment_mode"))
//                    {
//                        payment_mode = dataSnapshot.getValue().toString();
//                    }
//                    //firebaseRide fbRide = dataSnapshot.getValue(firebaseRide.class);
//                    Log.i("ibrahim_ride", "----------");
//                    Log.i("ibrahim_ride", dataSnapshot.toString());
////                    onRefresh();
////                    changeFragment();
//                }
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                }
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
    }

    public void deletePostFirebase() {
        Log.i("ibrahim", "deletePostFirebase");
        FirebaseDatabase.getInstance().getReference("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("ibrahim", "dataSnapshot");
                Log.i("ibrahim", dataSnapshot.toString());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.i("ibrahim", "dataSnapshot");
                    Log.i("ibrahim", postSnapshot.toString());
                    Post post = postSnapshot.getValue(Post.class);
                    if (rideJson.getTravel_id().equalsIgnoreCase(String.valueOf(post.travel_id))) {
                        Log.i("ibrahim", "dataSnapshot");
                        Log.i("ibrahim", post.text);
                        postSnapshot.getRef().removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void updateTravelCounterFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseTravel fbTravel = dataSnapshot.getValue(firebaseTravel.class);
                if (fbTravel != null) {
                    Log.i("ibrahim", "fbTravel");
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Counters").child("OFFLINE");
                    databaseRef.setValue(fbTravel.Counters.OFFLINE + 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    public void setupData() {
        if (bundle != null) {
            pickup_location.setText(rideJson.getpickup_address() + " ");
            drop_location.setText(rideJson.getDrop_address());
            txt_PickupPoint.setText(rideJson.getPickup_point());
            drivername.setText(rideJson.getDriver_name());
            mobilenumber.setText(rideJson.getDriver_mobile());
            mobile = rideJson.getDriver_mobile();
            TimeVal.setText(rideJson.getTime());
            dateandtime.setText(rideJson.getDate());
            txt_Empty_Seats.setText(rideJson.getempty_set());
            num_set.setText(rideJson.getBooked_set());
            if (!String.valueOf(rideJson.vehicle_info).isEmpty()) {
                Glide.with(AcceptedDetailFragment.this.getActivity()).load(rideJson.vehicle_info).apply(new RequestOptions().error(R.drawable.images)).into(DriverCar);
            }
            if (!String.valueOf(rideJson.getDriver_avatar()).isEmpty()) {
                Glide.with(AcceptedDetailFragment.this.getActivity()).load(rideJson.getDriver_avatar()).apply(new RequestOptions().error(R.drawable.images)).into(DriverAvatar);
            }
            mobilenumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                    btn_cancel.setVisibility(View.VISIBLE);
                    trackRide.setVisibility(View.GONE);
                    btn_payment.setVisibility(View.VISIBLE);
                    btn_complete.setVisibility(View.GONE);
                } else if (payment_status.equals("") && payment_mode.equals("OFFLINE")) {
                    btn_cancel.setVisibility(View.GONE);
                    trackRide.setVisibility(View.GONE);
                    btn_payment.setVisibility(View.GONE);
                } else {
                    btn_complete.setText(getString(R.string.complete_ride));
                    btn_complete.setVisibility(View.VISIBLE);
                    trackRide.setVisibility(View.GONE);
//                    mobilenumber_row.setVisibility(View.VISIBLE);
                }
                if (!payment_status.equals("PAID") && payment_mode.equals("OFFLINE")) {
                    btn_complete.setVisibility(View.GONE);
                } else {
                }
            }
        }
        SetCustomFont setCustomFont = new SetCustomFont();
        setCustomFont.overrideFonts(getActivity(), view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Fragment currentFragment = getFragmentManager().findFragmentByTag("Passenger Information");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.detach(currentFragment);
                fragmentTransaction.attach(currentFragment);
                fragmentTransaction.commit();
            }
        });
    }

    public void changeFragment() {
        if (ride_status.equalsIgnoreCase("ACCEPTED") && travel_status.equalsIgnoreCase("STARTED")) {
            Bundle bundle = new Bundle();
            rideJson.setTravel_status(travel_status);
            rideJson.setStatus(ride_status);
            rideJson.setPayment_status(payment_status);
            rideJson.setPayment_mode(payment_mode);
            bundle.putSerializable("data", rideJson);
            MyAcceptedDetailFragment detailFragment = new MyAcceptedDetailFragment();
            detailFragment.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, detailFragment, "Passenger Information");
            fragmentTransaction.commit();
        }
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
                Log.i("ibrahim", String.valueOf(ratingBar.getRating()));
                Log.i("ibrahim", String.valueOf(fare_rating.getRating()));
                Log.i("ibrahim getDriver_id", rideJson.getDriver_id());
                Log.i("ibrahim getUser_id", rideJson.getUser_id());
                Log.i("ibrahim getTravel_id", rideJson.getTravel_id());
                sendRating(ratingBar.getRating() * 2, fare_rating.getRating() * 2);
                Log.i("ibrahim", "complete");
                if (etComments.getText().toString().length() > 0) {
                    AddComment(etComments.getText().toString());
                }
                dialog.cancel();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ibrahim", "cancel");
                dialog.cancel();
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
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getActivity(), getString(R.string.payment_update), Toast.LENGTH_LONG).show();
//                ((HomeActivity) getActivity()).onBackPressed();
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
                    swipeRefreshLayout.setRefreshing(false);

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
        Log.i("ibrahim", "sendStatus");
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("status", status);
        params.put("by", "user");
        Server.setHeader(SessionManager.getKEY());
        Server.setContetntType();
        Server.post("api/user/rides", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("ibrahim", "response.toString()");
                Log.i("ibrahim", response.toString());
                try {
                    Gson gson = new GsonBuilder().create();
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                        }.getType());
                        if (response.has("data") && response.getJSONArray("data").length() > 0) {
                            if (list.size() > 0) {
                                if (list.get(0).getStatus().contains("REQUESTED")) {
                                    updateRideFirebase(travel_status, list.get(0).getStatus(), payment_status, payment_mode);
                                    updateTravelCounterFirebase(status);
                                    Toast.makeText(getActivity(), getString(R.string.full_travel), Toast.LENGTH_LONG).show();
                                    //HIDDEN
                                    btn_complete.setVisibility(View.GONE);
                                    btn_cancel.setVisibility(View.GONE);
                                    btn_payment.setVisibility(View.GONE);
                                    trackRide.setVisibility(View.GONE);
                                } else {
                                    updateNotificationFirebase(list.get(0).getStatus());
                                    Log.i("ibrahim", "waited");
                                    updateRideFirebase(travel_status, list.get(0).getStatus(), payment_status, payment_mode);
                                }
                            }
                        }
                    } else {
//                        Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                        Log.i("ibrahim", "sendStatus");
                        Log.i("ibrahim", "success else");
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                    Log.i("ibrahim", "sendstatus_onSuccess_catch");
                    Log.i("ibrahim", e.getMessage());
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
                    swipeRefreshLayout.setRefreshing(false);
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
                        Log.i("ibrahim", "fbTravel");
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Counters").child("ACCEPTED");
                        databaseRef.setValue(fbTravel.Counters.ACCEPTED + 1);
                    } else if (status.contains("COMPLETED")) {
                        Log.i("ibrahim", "fbTravel");
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Counters").child("COMPLETED");
                        databaseRef.setValue(fbTravel.Counters.COMPLETED + 1);

                        DatabaseReference databaseRef1 = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Counters").child("ACCEPTED");
                        databaseRef1.setValue(fbTravel.Counters.ACCEPTED - 1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    public void addNotificationFirebase(String ride_id_param, String travel_id_param) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Notifications").child(rideJson.getDriver_id()).push();
        Map<String, Object> rideObject = new HashMap<>();
        rideObject.put("ride_id", ride_id_param);
        rideObject.put("travel_id", travel_id_param);
        Log.i("ibrahim", "addNotificationFirebase");
//        Log.i("ibrahim",rideJson.getTravel_id());
//        rideObject.put("travel_id", rideJson.getTravel_id());
        rideObject.put("text", "request_approve");
        rideObject.put("readStatus", "0");
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        rideObject.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
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

    public void CheckRating(String user_id, String travel_id, String driver_id) {
        Log.i("ibrahim", "CheckRating");

        final RequestParams params = new RequestParams();
        params.put("user_id", user_id);
        params.put("travel_id", travel_id);
        params.put("driver_id", driver_id);
        Server.setHeader(SessionManager.getKEY());
        Server.get("api/user/rating_android/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("ibrahim", "CheckRating_onSuccess");

                Log.i("ibrahim", "checkrating response");
                Log.i("ibrahim", response.toString());
                try {
                    Log.i("ibrahim", "CheckRating_onSuccess_try");
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        if (response.has("data") && response.getString("data").equalsIgnoreCase("true")) {
                            Log.i("ibrahim", "CheckRating_onSuccess_try1");

                            btn_payment.setVisibility(View.GONE);
                            Log.i("ibrahim", "GONE");

                        } else {
                            btn_complete.setVisibility(View.VISIBLE);
                            Log.i("ibrahim", "VISIBLE");

                        }
                    } else {
//                        Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                        Log.i("ibrahim", "checkRating");
                        Log.i("ibrahim", "success else");
                        Log.i("ibrahim", "CheckRating_onSuccess_else");

                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                    Log.i("ibrahim", "CheckRating_onSuccess_catch");
                    Log.i("ibrahim", e.getMessage());

                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
                    swipeRefreshLayout.setRefreshing(false);
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


    public void sendRating(float DriverRatingST, float FareRatingST) {
        Log.i("ibrahim", "sendRating");


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
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("ibrahim", "sendRating_onSuccess");

//                Log.i("ibrahim",response.toString());
//                try {
//                    if (response.has("status") && response.getString("status").equals("success")) {
////                        sendStatus(rideJson.getRide_id(), "COMPLETED");
//                        //Log.i("ibrahim","complete travel");
//                    } else {
//                        //String error = response.getString("data");
//                        //Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
//                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i("ibrahim", "sendRating_onFailure");

//                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                Log.i("ibrahim", "sendRating_onFailure1");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.i("ibrahim", "onFinish");
                sendStatus(rideJson.getRide_id(), "COMPLETED");
                if (getActivity() != null) {
                    swipeRefreshLayout.setRefreshing(false);
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
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject()
                                .toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.d("FuturePaymentExample", authorization_code);

                        /*sendAuthorizationToServer(auth);
                        Toast.makeText(getActivity(),
                                "Future Payment code received from PayPal",
                                Toast.LENGTH_LONG).show();*/
                        Log.e("paypal", "future Payment code received from PayPal  :" + authorization_code);

                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "failure Occurred", Toast.LENGTH_LONG).show();
                        Log.e("FuturePaymentExample",
                                "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), getString(R.string.payment_hbeen_cancelled), Toast.LENGTH_LONG).show();

                Log.d("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {

                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

                Log.d("FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }

    }

    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            return true;

        } else {
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

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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

    }

    void isStarted() {
        Log.i("ibrahim", "isStarted");
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
                                                    Log.i("ibrahim", "trackride");
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
//                                trackRide.setVisibility(View.INVISIBLE);

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
}