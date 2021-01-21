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

import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.custom.CheckConnection;
import com.alaan.roamu.custom.GPSTracker;
import com.alaan.roamu.custom.SetCustomFont;
import com.alaan.roamu.pojo.PendingRequestPojo;
import com.alaan.roamu.pojo.Tracking;
import com.alaan.roamu.pojo.firebaseRide;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
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
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAcceptedDetailFragment extends FragmentManagePermission implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, BackFragment {
    private View view;
    AppCompatButton trackRide;
    private String mobile = "";
    AppCompatButton btn_cancel, btn_payment, btn_complete;
    TextView drivername, pickup_location, drop_location;
    private AlertDialog alert;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final String CONFIG_ENVIRONMENT = Server.ENVIRONMENT;
    private static PayPalConfiguration config;
    PayPalPayment thingToBuy;
    PendingRequestPojo rideJson;
    String permissions[] = {PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Dialog dialog;
    AppCompatButton cancel,mobilenumber;
    com.google.android.gms.maps.MapView mapView;
    TextView dateandtime, TimeVal;

    TextView txt_Driver_name, txt_PickupPoint;
    String distance;
    TextView calculateFare;
    Snackbar snackbar;

    DatabaseReference databaseRides;
    Bundle bundle;

    private String travel_status;
    private String ride_status;
    private String payment_status;
    private String payment_mode;

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

            Log.i("ibrahim",rideJson.getRide_id());
            databaseRides = FirebaseDatabase.getInstance().getReference("rides").child(rideJson.getRide_id());
        }
        view = inflater.inflate(R.layout.fragment_my_accepted_detail, container, false);
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
        //attaching value event listener
        databaseRides.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot rideSnapshot : dataSnapshot.getChildren()) {
                    firebaseRide fbRide = dataSnapshot.getValue(firebaseRide.class);
                    Log.i("ibrahim ride","----------");
                    travel_status = fbRide.travel_status;
                    ride_status = fbRide.ride_status;
                    payment_status = fbRide.payment_status;
                    payment_mode = fbRide.payment_mode;
                    setupData();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }

    public void BindView() {
        mapView = (com.google.android.gms.maps.MapView) view.findViewById(R.id.MyADF_mapview);
        cancel = (AppCompatButton) view.findViewById(R.id.MyADF_btn_cancel);
        mobilenumber= (AppCompatButton) view.findViewById(R.id.MyADF_mobilenumber);
        pickup_location = (TextView) view.findViewById(R.id.MyADF_txt_pickuplocation);
        drop_location = (TextView) view.findViewById(R.id.MyADF_txt_droplocation);
        dateandtime = (TextView) view.findViewById(R.id.MyADF_dateTimeVal);
        TimeVal = (TextView) view.findViewById(R.id.MyADF_TimeVal);
        txt_Driver_name = (TextView) view.findViewById(R.id.MyADF_Driver_name);
        txt_PickupPoint = (TextView) view.findViewById(R.id.MyADF_txt_PickupPoint);
        btn_complete = (AppCompatButton) view.findViewById(R.id.MyADF_btn_complete);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.MyADF_swipe_refresh);
        drivername = (TextView) view.findViewById(R.id.MyADF_Driver_name);
        trackRide = (AppCompatButton) view.findViewById(R.id.MyADF_btn_trackride);
        btn_payment = (AppCompatButton) view.findViewById(R.id.MyADF_btn_payment);
        btn_cancel = (AppCompatButton) view.findViewById(R.id.MyADF_btn_cancel);
        pickup_location.setSelected(true);
        drop_location.setSelected(true);
        if (bundle != null) {
            pickup_location.setText(rideJson.getpickup_address() + " ");
            drop_location.setText(rideJson.getDrop_address());
            txt_PickupPoint.setText(rideJson.getPickup_point());
            drivername.setText(rideJson.getDriver_name());
            mobilenumber.setText(rideJson.getDriver_mobile());
            mobile = rideJson.getDriver_mobile();
            TimeVal.setText(rideJson.getTime());
            dateandtime.setText(rideJson.getDate());
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
                btn_complete.setText(getString(R.string.reseve));
                btn_complete.setVisibility(View.VISIBLE);
                isStarted();
            }
            if (ride_status.equalsIgnoreCase("PENDING")) {
                btn_cancel.setVisibility(View.VISIBLE);
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

            }
            if (ride_status.equalsIgnoreCase("ACCEPTED")) {
                isStarted();
                if (payment_status.equals("") && payment_mode.equals("")) {
                    btn_cancel.setVisibility(View.VISIBLE);
                    trackRide.setVisibility(View.GONE);
                    btn_payment.setVisibility(View.VISIBLE);
                } else {
                    btn_complete.setText(getString(R.string.complete_ride));
                    btn_complete.setVisibility(View.VISIBLE);
                    trackRide.setVisibility(View.VISIBLE);
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
                                        updateRideFirebase(travel_status,ride_status,payment_status,"OFFLINE");
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
                }
                if (ride_status.equalsIgnoreCase("ACCEPTED")) {// edited by ibrahim it was completed date:21/1/2021
                    completeTask();
                }
            }
        });
    }

    public void setupData(){
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
                btn_complete.setText(getString(R.string.reseve));
                btn_complete.setVisibility(View.VISIBLE);
                isStarted();
            }
            if (ride_status.equalsIgnoreCase("PENDING")) {
                btn_cancel.setVisibility(View.VISIBLE);
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
            }
            if (ride_status.equalsIgnoreCase("ACCEPTED")) {
                isStarted();
                if (payment_status.equals("") && payment_mode.equals("")) {
                    btn_cancel.setVisibility(View.VISIBLE);
                    trackRide.setVisibility(View.GONE);
                    btn_payment.setVisibility(View.VISIBLE);
                }
                else if(payment_status.equals("") && payment_mode.equals("OFFLINE")) {
                    btn_cancel.setVisibility(View.GONE);
                    trackRide.setVisibility(View.VISIBLE);
                    btn_payment.setVisibility(View.GONE);
                }
                else {
                    btn_complete.setText(getString(R.string.complete_ride));
                    btn_complete.setVisibility(View.VISIBLE);
                    trackRide.setVisibility(View.VISIBLE);
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
            }
        });
    }

    public void completeTask() {
        //AlertDialogCreate(getString(R.string.ride_request_cancellation), getString(R.string.want_to_accept), "COMPLETED");
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ratingdialog);
        TextView titleTV = (TextView)dialog.findViewById(R.id.rateHeader);
        final TextView rateTV = (TextView)dialog.findViewById(R.id.rateTV);
        Button submitBtn = (Button)dialog.findViewById(R.id.submitRateBtn);
        Button cancelBtn = (Button)dialog.findViewById(R.id.cancelRateBtn);
        RatingBar ratingBar = (RatingBar)dialog.findViewById(R.id.ratingsBar);
        RatingBar fare_rating = (RatingBar)dialog.findViewById(R.id.fare_rating);
        dialog.show();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ibrahim", String.valueOf(ratingBar.getRating()));
                Log.i("ibrahim",String.valueOf(fare_rating.getRating()));
                Log.i("ibrahim getDriver_id",rideJson.getDriver_id());
                Log.i("ibrahim getUser_id",rideJson.getUser_id());
                Log.i("ibrahim getTravel_id",rideJson.getTravel_id());
                sendRating(ratingBar.getRating() * 2 , fare_rating.getRating() * 2);
                Log.i("ibrahim","complete");
                dialog.cancel();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ibrahim","cancel");
                dialog.cancel();
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
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("status", status);
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
                try {
                    AcceptedRequestFragment acceptedRequestFragment = new AcceptedRequestFragment();
                    Bundle bundle = null;
                    if (response.has("status") && response.getString("status").equals("success")) {
                        updateRideFirebase(travel_status,status,payment_status,payment_mode);
                    }
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
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    public void updateRideFirebase(String travel_status, String ride_status, String payment_status, String payment_mode) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("rides").child(rideJson.getRide_id());
        Map<String,Object> rideObject = new HashMap<>();

        rideObject.put("ride_status", ride_status);
        rideObject.put("travel_status", travel_status);
        rideObject.put("payment_status", payment_status);
        rideObject.put("payment_mode", payment_mode);
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
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
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
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
//                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                Log.i("ibrahim","Failure");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.i("ibrahim","onFinish");
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
}