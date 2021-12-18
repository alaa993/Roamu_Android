package com.alaan.roamu.fragement;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.alaan.roamu.pojo.Fav_Places;
import com.alaan.roamu.pojo.NearbyData;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import android.webkit.WebStorage;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.loopj.android.http.AsyncHttpClient.log;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;


public class MyPlacesFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback,
        Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, BackFragment, LocationListener, GoogleMap.OnMarkerClickListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public String NETWORK;
    public String TRYAGAIN;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1234;
    private PlacesClient placesClient;
    Pass pass;
    com.google.android.gms.maps.MapView mMapView;
    GoogleMap myMap;
    private int PLACE_PICKER_REQUEST = 7896;
    private LatLng origin;
    ValueEventListener listener_fav_places;
    DatabaseReference database_fav_places;
    ImageView current_location;
    int i = 0;
    String result = "";
    String TAG = "home";
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    //    CardView rides, earnings;
    private String driver_id = "";
    private String cost = "";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private View rootView;
    private String drivername = "";
    private Marker my_marker;
    List<Fav_Places> fav_places_list;
    private List<Marker> markers;
    RelativeLayout relative_drop;
    RelativeLayout linear_pickup;
    com.google.android.libraries.places.api.model.Place pickup;
    Place drop;
    TextView textView_today, textView_overall, textView_totalride;
    int ride_number;
    EditText etFavAddres;
    EditText etType;

    private RelativeLayout dialog_insert_fav_place;
    Animation animFadeIn, animFadeOut;

    public MyPlacesFragment() {
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
            rootView = inflater.inflate(R.layout.fragment_my_places, container, false);
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
            Log.e("tag", "Inflate exception   " + e.toString());
        }
        NearBy();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (getActivity() != null && mMapView != null) {
                mMapView.onPause();
            }
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
        myMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        myMap.setMaxZoomPreference(80);
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
            } else {
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

        markers = new ArrayList<Marker>();
        fav_places_list = new ArrayList<Fav_Places>();
        MapsInitializer.initialize(this.getActivity());
        mMapView = (MapView) rootView.findViewById(R.id.fm_mapview);
        etFavAddres = (EditText) rootView.findViewById(R.id.etFavAddres);
        etType = (EditText) rootView.findViewById(R.id.etType);
        textView_today = (TextView) rootView.findViewById(R.id.txt_todayearning);
        textView_overall = (TextView) rootView.findViewById(R.id.txt_overallearning);
        textView_totalride = (TextView) rootView.findViewById(R.id.txt_total_ridecount);
        animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.dialogue_scale_anim_open);
        animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.dialogue_scale_anim_exit);
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
        dialog_insert_fav_place = (RelativeLayout) rootView.findViewById(R.id.RL_add_fav_place);

        dialog_insert_fav_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog_insert_fav_place.getVisibility() == View.VISIBLE) {
                    dialog_insert_fav_place.startAnimation(animFadeOut);
                    dialog_insert_fav_place.setVisibility(View.GONE);
                }
            }
        });
        Places.initialize(getApplicationContext(), getString(R.string.google_android_map_api_key));
        pass = new Pass();
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        placesClient = Places.createClient(getActivity());
        Utils.overrideFonts(getActivity(), rootView);
    }

    private void setCurrentLocation() {
        if (!GPSEnable()) {
            tunonGps();
        } else {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                try {
                    List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);
                    FindCurrentPlaceRequest request =
                            FindCurrentPlaceRequest.builder(placeFields).build();
                    if (ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
                        placeResponse.addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FindCurrentPlaceResponse response = task.getResult();
                                if (response != null && response.getPlaceLikelihoods() != null) {
                                    PlaceLikelihood placeLikelihood = response.getPlaceLikelihoods().get(0);
                                    pickup = placeLikelihood.getPlace();
                                    current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));
                                }
                            } else {
                                Exception exception = task.getException();
                                if (exception instanceof ApiException) {
                                    ApiException apiException = (ApiException) exception;
                                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }
        }


    }

//    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
//        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_home_button);
//        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
//        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
//        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
//        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        background.draw(canvas);
//        vectorDrawable.draw(canvas);
//        return BitmapDescriptorFactory.fromBitmap(bitmap);
//    }

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
                            if (dialog_insert_fav_place.getVisibility() == View.VISIBLE) {
                                dialog_insert_fav_place.startAnimation(animFadeOut);
                                dialog_insert_fav_place.setVisibility(View.GONE);
                            }
                        }
                    });

                    myMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng latLng) {
                            if (dialog_insert_fav_place.getVisibility() == View.VISIBLE) {
                                dialog_insert_fav_place.startAnimation(animFadeOut);
                                dialog_insert_fav_place.setVisibility(View.GONE);
                            } else {
                                Button mSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
                                Button mCancel = (Button) rootView.findViewById(R.id.btnCancel);

                                mSubmit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!etFavAddres.getText().toString().isEmpty()) {
                                            if (latLng != null) {
                                                setLocaiton(latLng.latitude, latLng.longitude, etFavAddres.getText().toString());
                                                dialog_insert_fav_place.startAnimation(animFadeOut);
                                                dialog_insert_fav_place.setVisibility(View.GONE);
                                            }
                                        } else {
                                            Toast.makeText(getActivity(),
                                                    "Failed",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                mCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog_insert_fav_place.startAnimation(animFadeOut);
                                        dialog_insert_fav_place.setVisibility(View.GONE);
                                    }
                                });

                                dialog_insert_fav_place.setVisibility(View.VISIBLE);
                                dialog_insert_fav_place.startAnimation(animFadeIn);
                            }
                        }
                    });
                }
                setCurrentLocation(currentLatitude, currentLongitude);
            }
        } catch (Exception e) {

        }

    }

    public void setLocaiton(Double lat, Double lon, String name) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("Fav_Places").child(SessionManager.getUserId()).push();
            Map<String, Object> rideObject = new HashMap<>();
            rideObject.put("latitude", lat);
            rideObject.put("longitude", lon);
            rideObject.put("name", name);
            rideObject.put("type", "Home");
            rideObject.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseRef.setValue(rideObject).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(getActivity(), getString(R.string.place_added_successfully), Toast.LENGTH_LONG).show();
                    origin = new LatLng(lat, lon);
                    Marker myMarker = myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title(name).snippet(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    myMarker.showInfoWindow();
                    etFavAddres.setText("");
                }
            });
        }
    }

    public void NearBy() {
        myMap.clear();
        database_fav_places = FirebaseDatabase.getInstance().getReference("Fav_Places").child(SessionManager.getUserId());
        listener_fav_places = database_fav_places.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Fav_Places fav_places = postSnapshot.getValue(Fav_Places.class);
                    fav_places.id = postSnapshot.getKey();
                    fav_places_list.add(fav_places);
                }
                for (int i = 0; i < fav_places_list.size(); i++) {
                    origin = new LatLng(fav_places_list.get(i).latitude, fav_places_list.get(i).longitude);
                    Marker myMarker = myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title(getString(R.string.place_name)).snippet(fav_places_list.get(i).name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    myMarker.showInfoWindow();
                    markers.add(myMarker);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setCurrentLocation(final Double lat, final Double log) {
        try {
            my_marker.setPosition(new LatLng(lat, log));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 10);
            myMap.animateCamera(cameraUpdate);
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
                            getCurrentlOcation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(getActivity(), 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("ibrahim", "insideMarker");
        Log.i("ibrahim", String.valueOf(markers.size()));
        Log.i("ibrahim", String.valueOf(marker.getTitle()));

        if (dialog_insert_fav_place.getVisibility() == View.VISIBLE) {
            dialog_insert_fav_place.startAnimation(animFadeOut);
            dialog_insert_fav_place.setVisibility(View.GONE);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle(getContext().getString(R.string.do_you_want_to_delete));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    for (int i = 0; i < markers.size(); i++) {
                        Log.i("ibrahim_i", String.valueOf(i));
                        if (marker.equals(markers.get(i))) {
                            Log.i("ibrahim", "insideMarker");
                            Log.i("ibrahim", "" + i);
                            Log.i("ibrahim", fav_places_list.get(i).id);
                            FirebaseDatabase.getInstance().getReference("Fav_Places").child(SessionManager.getUserId()).child(fav_places_list.get(i).id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@afu.org.checkerframework.checker.nullness.qual.NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), getContext().getString(R.string.Place_deleted_successfully), Toast.LENGTH_SHORT).show();
                                        myMap.clear();
                                        dialog.dismiss();
                                        NearBy();
                                    }
                                }
                            });
                            break;
                        }
                    }
                }
            });
            alertDialog.show();
        }

        return false;
    }
}