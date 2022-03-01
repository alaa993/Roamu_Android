package com.alaan.roamu.fragement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.akexorcist.googledirection.DirectionCallback;
import com.alaan.roamu.R;
import com.alaan.roamu.Server.GoogMatrixRequest;
import com.alaan.roamu.adapter.search_d_adapter;
import com.alaan.roamu.pojo.NearbyData;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;

import net.skoumal.fragmentback.BackFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.custom.GPSTracker;
import com.alaan.roamu.custom.Utils;
import com.alaan.roamu.pojo.Pass;
import com.alaan.roamu.pojo.PendingRequestPojo;
import com.alaan.roamu.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;
import com.google.android.libraries.places.api.Places;

import net.skoumal.fragmentback.BackFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.loopj.android.http.AsyncHttpClient.log;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;


public class MapFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback,
        Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, BackFragment, LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public String NETWORK;
    public String ERROR = "error occured";
    public String TRYAGAIN;
    //    Boolean flag = false;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1234;
    private PlacesClient placesClient;
    Pass pass;
    MapView mMapView;
    GoogleMap myMap;
    private int PLACE_PICKER_REQUEST = 7896;

    ImageView current_location, clear;
    ProgressBar progressBar;

    int i = 0;
    String result = "";
    Animation animFadeIn, animFadeOut;
    TextView txt_distance, txt_timedistance;
    String TAG = "home";
    LinearLayout linear_request;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
            PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    //    CardView rides, earnings;
    private String driver_id = "";
    private String cost = "";
    private String unit = "";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private View rootView;
    private String check = "";
    private String drivername = "";
    private Marker my_marker;
    private boolean isShown = true;
    private RelativeLayout header, footer, footer2;
    TextView pickup_location, drop_location;
    RelativeLayout relative_drop;
    //    Button show_eringn;
    RelativeLayout linear_pickup;
    com.google.android.libraries.places.api.model.Place pickup;
    Place drop;

    TextView textView_today, textView_overall, textView_totalride;

    int ride_number;

    List<NearbyData> list;
    private List<Marker> markers;

    private LatLng origin;
    private LatLng destination;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NETWORK = getString(R.string.network_not_available);
        TRYAGAIN = getString(R.string.try_again);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_map, container, false);
        try {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
            ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.home));
            bindView(savedInstanceState);
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
        } catch (Exception e) {

            //log.e("tag", "Inflate exception   " + e.toString());
        }

        linear_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                // by ibrahim to check with driver
                pass.setFromPlace(String.valueOf(pickup));
                pass.setToPlace(String.valueOf(drop));
                pass.setDriverId(driver_id);
                pass.setFare(cost);
                pass.setDriverName(drivername);
                bundle.putSerializable("data", pass);
                RequestFragment fragobj = new RequestFragment();
                fragobj.setArguments(bundle);
                ((HomeActivity) getActivity()).changeFragment(fragobj, "Request Ride");
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
                    header.startAnimation(animFadeOut);
                    footer.startAnimation(animFadeOut);
                    header.setVisibility(View.GONE);
                    footer.setVisibility(View.GONE);
                }
            }
        });

        pickup_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    /* Intent intent =
                             new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                     .build(getActivity());
                     startActivityForResult(intent, PLACE_PICKER_REQUEST);*/


                List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getActivity());
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            }
        });
        drop_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    /*  Intent intent =
                              new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                      .build(getActivity());
                      startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                  */

                List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getActivity());
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });
        NearBy();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                getCurrentlOcation();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }

        }
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                getCurrentlOcation();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                pickup = Autocomplete.getPlaceFromIntent(data);

                pickup_location.setText(pickup.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                //log.e(TAG, status.toString());
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                drop = Autocomplete.getPlaceFromIntent(data);
                drop_location.setText(drop.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG).show();

            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (getActivity() != null && mMapView != null) {
                mMapView.onPause();
            }
            //by ibrahim
//            if (mGoogleApiClient != null) {
//                if (mGoogleApiClient.isConnected()) {
//                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//                    mGoogleApiClient.disconnect();
//                }
//            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mMapView != null) {
                mMapView.onDestroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
//        myMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        myMap.setMaxZoomPreference(80);
//        GetRides();
//        requestDirection();
        //by ibrahim
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

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (getActivity() != null) {
            if (direction.isOK()) {
                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                myMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
//                myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title("Pickup Location").snippet(pickup_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                myMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title("Drop Location").snippet(drop_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
//                calculateDistance(Double.valueOf(direction.getRouteList().get(0).getLegList().get(0).getDistance().getValue()) / 1000);
            } else {
//                distanceAlert(direction.getErrorMessage());
                //calculateFare.setVisibility(View.GONE);
//                dismiss();
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

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

    public void bindView(Bundle savedInstanceState) {

        MapsInitializer.initialize(this.getActivity());
        markers = new ArrayList<Marker>();
        mMapView = (MapView) rootView.findViewById(R.id.fm_mapview);
        header = (RelativeLayout) rootView.findViewById(R.id.header);
        footer = (RelativeLayout) rootView.findViewById(R.id.footer2);
        footer2 = (RelativeLayout) rootView.findViewById(R.id.footer);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        textView_today = (TextView) rootView.findViewById(R.id.txt_todayearning);
        textView_overall = (TextView) rootView.findViewById(R.id.txt_overallearning);
        textView_totalride = (TextView) rootView.findViewById(R.id.txt_total_ridecount);

        footer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //log.i("ibrahim", "insideFooter2");
                Bundle bundle = new Bundle();
                pass = new Pass();
                pass.f = Pass.fragment_type.GET;
                pass.setFromPlace(list.get(ride_number).getPickup_address());
                pass.setToPlace(list.get(ride_number).getDrop_address());
                pass.setFromAddress(list.get(ride_number).getPickup_location());
                pass.setToAddress(list.get(ride_number).getDrop_location());
                pass.setPickupPoint(list.get(ride_number).getPickup_point());
                pass.setDriverId(list.get(ride_number).getUser_id());
                //by ibrahim
                pass.setTravelId(list.get(ride_number).getTravel_id());

                pass.setFare(list.get(ride_number).getAmount());
                pass.setDriverName(list.get(ride_number).getName());
                pass.setDriverCity(list.get(ride_number).getDriverCity());
                pass.setSmoke(list.get(ride_number).getsmoked());
                pass.setDate(list.get(ride_number).getTravel_date());
                pass.setTime(list.get(ride_number).getTravel_time());
                pass.setAvalibleset(list.get(ride_number).getBooked_set());
                pass.setTravel_status(list.get(ride_number).getTravel_status());
                pass.setStatus("PENDING");
                pass.setPayment_mode(list.get(ride_number).getPayment_mode());
                pass.setPayment_status(list.get(ride_number).getPayment_status());
                pass.avatar = list.get(ride_number).avatar;
                pass.vehicle_info = list.get(ride_number).vehicle_info;
                // by ibrahim
                //log.i("ibrahim", "success by ibrahim search_d_adapter");
                //log.i("ibrahim", pass.getStatus());
                //log.i("ibrahim", list.get(ride_number).getVehicle_info());

                pass.setVehicleName(list.get(ride_number).getVehicle_info());
                pass.empty_set = list.get(ride_number).empty_set;
                pass.DriverRate = list.get(ride_number).DriverRate;
                pass.Travels_Count = list.get(ride_number).Travels_Count;
                pass.pickup_location = list.get(ride_number).getPickup_location();

                bundle.putSerializable("data", pass);
                RequestFragment fragobj = new RequestFragment();
                fragobj.setArguments(bundle);

                ((HomeActivity) getContext()).changeFragment(fragobj, "Passenger Information");
            }
        });

        pickup_location = (TextView) rootView.findViewById(R.id.pickup_location);
        drop_location = (TextView) rootView.findViewById(R.id.drop_location);
        current_location = (ImageView) rootView.findViewById(R.id.current_location);
        linear_pickup = (RelativeLayout) rootView.findViewById(R.id.linear_pickup);
        relative_drop = (RelativeLayout) rootView.findViewById(R.id.relative_drop);
        clear = (ImageView) rootView.findViewById(R.id.clear);
        Places.initialize(getApplicationContext(), getString(R.string.google_android_map_api_key));
        pass = new Pass();
//        show_eringn = (Button) rootView.findViewById(R.id.show_erning);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.dialogue_scale_anim_open);
        animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.dialogue_scale_anim_exit);
        txt_distance = (TextView) rootView.findViewById(R.id.txt_distance);
        txt_timedistance = (TextView) rootView.findViewById(R.id.txt_timedistance);
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
        linear_request = (LinearLayout) rootView.findViewById(R.id.linear_request);

        // by ibrahim to check with driver
//        rides = (CardView) rootView.findViewById(R.id.cardview_totalride);
//        earnings = (CardView) rootView.findViewById(R.id.earnings);

        placesClient = Places.createClient(getActivity());

        Utils.overrideFonts(getActivity(), rootView);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drop_location.setText("");
                if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
                    header.startAnimation(animFadeOut);
                    footer.startAnimation(animFadeOut);
                    header.setVisibility(View.GONE);
                    footer.setVisibility(View.GONE);
                }
            }
        });

        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askCompactPermissions(permissionAsk, new PermissionResult() {
                        @Override
                        public void permissionGranted() {
                            if (pickup_location.getText().toString().trim().equals("")) {
                                setCurrentLocation();
                            } else {
                                pickup_location.setText("");
                                current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
                            }
                        }

                        @Override
                        public void permissionDenied() {

                        }

                        @Override
                        public void permissionForeverDenied() {
                            Snackbar.make(rootView, getString(R.string.allow_permission), Snackbar.LENGTH_LONG).show();
                            openSettingsApp(getActivity());

                        }
                    });
                } else {
                    if (!GPSEnable()) {
                        tunonGps();
                    } else {
                        if (pickup_location.getText().toString().trim().equals("")) {
                            setCurrentLocation();
                        } else {
                            pickup_location.setText("");
                            current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
                        }
                    }
                }
            }
        });
    }

    private void setCurrentLocation() {
        if (!GPSEnable()) {
            tunonGps();
        } else {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                try {
                    /*@SuppressLint("MissingPermission") Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                    placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            try {
                                if (task.isSuccessful()) {
                                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                        pickup = placeLikelihood.getPlace().freeze();
                                        pickup_location.setText(placeLikelihood.getPlace().getAddress());
                                        current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));

                                    }
                                    likelyPlaces.release();
                                }
                            } catch (Exception e) {

                            }

                        }
                    });*/

                    // Use fields to define the data types to return.
                    List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Use the builder to create a FindCurrentPlaceRequest.
                    FindCurrentPlaceRequest request =
                            FindCurrentPlaceRequest.builder(placeFields).build();

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
                    if (ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
                        placeResponse.addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FindCurrentPlaceResponse response = task.getResult();
                                /*for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                    //log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                            placeLikelihood.getPlace().getName(),
                                            placeLikelihood.getLikelihood()));
                                    pickup = placeLikelihood.getPlace();
                                    pickup_location.setText(placeLikelihood.getPlace().getAddress());
                                    current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));

                                }
*/
                                if (response != null && response.getPlaceLikelihoods() != null) {
                                    PlaceLikelihood placeLikelihood = response.getPlaceLikelihoods().get(0);
                                    pickup = placeLikelihood.getPlace();
                                    //log.i("ibrahim", "gps");
                                    //log.i("ibrahim", placeLikelihood.getPlace().getLatLng().toString());

                                    pickup_location.setText(placeLikelihood.getPlace().getAddress());
                                    current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));

                                }
                            } else {
                                Exception exception = task.getException();
                                if (exception instanceof ApiException) {
                                    ApiException apiException = (ApiException) exception;
                                    //log.e(TAG, "Place not found: " + apiException.getStatusCode());
                                }
                            }
                        });
                    }
                } catch (Exception e) {

                }


            }
        }


    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                if (myMap != null) {
                    myMap.clear();
                    my_marker = myMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("You are here.").icon(BitmapDescriptorFactory.fromResource(R.drawable.user_default)));
                    my_marker.showInfoWindow();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 10);
                    myMap.animateCamera(cameraUpdate);
                    myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            if (footer2.getVisibility() == View.VISIBLE) {
                                footer2.setVisibility(View.GONE);
                            }
                        }
                    });

                    myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if (footer2.getVisibility() == View.VISIBLE) {
                                footer2.startAnimation(animFadeOut);
                                footer2.setVisibility(View.GONE);
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
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

                                                //log.i("ibrahim", "insideMarker");
                                                textView_totalride.setText("");
                                                textView_today.setText("");
                                                textView_overall.setText("");
                                                //log.i("ibrahim", String.valueOf(markers.size()));
                                                for (int i = 0; i < markers.size(); i++) {
//                                                    //log.i("ibrahim", "beforeloop");
//                                                    //log.i("ibrahim", markers.get(i).getTitle().toString());

                                                    if (marker.equals(markers.get(i))) {
//                                                        //log.i("ibrahim", "insideMarker");
//                                                        //log.i("ibrahim", "" + i);

                                                        textView_totalride.setText(list.get(i).getName());
                                                        textView_today.setText(list.get(i).getPickup_address());
                                                        textView_overall.setText(list.get(i).getDrop_address());
//                                                        footer2.setVisibility(View.VISIBLE);
                                                        ride_number = i;
                                                        break;
                                                    }
                                                }

                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            System.out.println(obj4.get("text"));
                                                            System.out.println(obj5.get("text"));
                                                            txt_distance.setText(obj4.get("text").toString());
                                                            txt_timedistance.setText(obj5.get("text").toString());
                                                            footer2.setVisibility(View.VISIBLE);
                                                            footer2.startAnimation(animFadeIn);
                                                            progressBar.setVisibility(View.GONE);
                                                        } catch (NullPointerException e) {
                    System.err.println("Null pointer exception");
                }catch (JSONException e) {
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
                }
                setCurrentLocation(currentLatitude, currentLongitude);
            }
        } catch (Exception e) {

        }

    }

    public void setCurrentLocation(final Double lat, final Double log) {
        try {
            my_marker.setPosition(new LatLng(lat, log));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 10);
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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (IntentSender.SendIntentException e) {

                e.printStackTrace();
            }
        }

    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            if (!currentLatitude.equals(0.0) && !currentLongitude.equals(0.0)) {
                setCurrentLocation(currentLatitude, currentLongitude);
            } else {
                Toast.makeText(getActivity(), getString(R.string.couldnt_get_location), Toast.LENGTH_LONG).show();
            }
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

    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public int getBackPriority() {
        return 0;
    }

    public void NearBy() {
        RequestParams params = new RequestParams();
//        params.put("travel_date",date);

        Server.setHeader(SessionManager.getKEY());
        Server.get("api/user/travels2/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        Gson gson = new GsonBuilder().create();
                        //log.i("tag", "success by ibrahim");
                        //log.e("success", response.toString());

                        list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<NearbyData>>() {
                        }.getType());
                        //log.e("success", String.valueOf(list.size()));

                        for (int i = 0; i < list.size(); i++) {
                            //log.i("success", String.valueOf(i));

                            String[] pickuplatlong = list.get(i).getPickup_location().split(",");
                            double pickuplatitude = Double.parseDouble(pickuplatlong[0]);
                            double pickuplongitude = Double.parseDouble(pickuplatlong[1]);
                            origin = new LatLng(pickuplatitude, pickuplongitude);
                            //log.i("origin", origin.toString());


                            String[] droplatlong = list.get(i).getDrop_location().split(",");
                            double droplatitude = Double.parseDouble(droplatlong[0]);
                            double droplongitude = Double.parseDouble(droplatlong[1]);
                            destination = new LatLng(droplatitude, droplongitude);
                            //log.i("destination", destination.toString());

                            Marker myMarker = myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title("Travel").snippet(list.get(i).getPickup_address()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            markers.add(myMarker);
                        }
                    }
                } catch (NullPointerException e) {
                    System.err.println("Null pointer exception");
                }catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        //log.i("ibrahim", "insideMarker");
//        textView_totalride.setText("");
//        textView_today.setText("");
//        textView_overall.setText("");
//        //log.i("ibrahim", String.valueOf(markers.size()));
//        for (int i = 0; i < markers.size(); i++) {
//            //log.i("ibrahim", "beforeloop");
//            //log.i("ibrahim", markers.get(i).getTitle().toString());
//
//            if (marker.equals(markers.get(i))) {
//                //log.i("ibrahim", "insideMarker");
//                //log.i("ibrahim", "" + i);
//
//                textView_totalride.setText(list.get(i).getName());
//                textView_today.setText(list.get(i).getPickup_address());
//                textView_overall.setText(list.get(i).getDrop_address());
//                footer2.setVisibility(View.VISIBLE);
//                ride_number = i;
//                break;
//            }
//        }
//
//        return false;
//    }
}