package com.alaan.roamu.fragement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.List_provider;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.PlusCode;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.alaan.roamu.R;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.custom.CheckConnection;
import com.alaan.roamu.custom.GPSTracker;
import com.alaan.roamu.pojo.NearbyData;
import com.alaan.roamu.pojo.Pass;
import com.alaan.roamu.session.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import net.skoumal.fragmentback.BackFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
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


/**
 * Created by android on 7/3/17.
 */

public class HomeFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback, Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, BackFragment,
        LocationListener {
    private static final String TAG = "HomeFragment";
    private String driver_id, passanger_value, bag_value, smoke_value, date_time_value;
    private String cost;
    private String unit;
    private int PLACE_PICKER_REQUEST = 7896;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1234;
    private int PLACE_search_AUTOCOMPLETE_REQUEST_CODE = 7777;
    private int PLACE_search_pic_AUTOCOMPLETE_REQUEST_CODE = 7778;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private View rootView;
    Calendar date;
    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;

    int mHour;
    int mMinute;

    Boolean flag = false;
    GoogleMap myMap;
    Button custom_request, search_box_custom;
    ImageView current_location, clear;
    // PlaceDetectionClient mPlaceDetectionClient;
    private RelativeLayout header, footer, search_box;
    Animation animFadeIn, animFadeOut;
    TextView pickup_location, drop_location, search_drop_location, search_pich_location;
    RelativeLayout relative_drop, search_drop;
    RelativeLayout linear_pickup;
    TextView txt_vehicleinfo, rate, txt_info, txt_cost, txt_color, txt_address, request_ride, txt_date, txt_smoke, txt_fee, passanger_search, smoke_search, date_time_search;
    LinearLayout linear_request;
    LinearLayout liner_close;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    private String drivername;
    MapView mMapView;
    Pass pass;
    Place pickup, drop, s_drop, s_pic;
    ProgressBar progressBar;
    private PlacesClient placesClient;
    RecyclerView recyclerView;

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //  MapsInitializer.initialize(this.getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            rootView = inflater.inflate(R.layout.home_fragment, container, false);
            ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.home));
            bindView(savedInstanceState);
            if (!CheckConnection.haveNetworkConnection(getActivity())) {
                Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            }
            search_box.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askCompactPermissions(permissionAsk, new PermissionResult() {
                    @Override
                    public void permissionGranted() {
                        if (!GPSEnable()) {
                            tunonGps();
                        } else {
                            getcurrentlocation();
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
                    getcurrentlocation();
                }

            }

            linear_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (CheckConnection.haveNetworkConnection(getActivity())) {
                        //      if (pickup_location.getText().toString().trim().equals("")) {
                        //        Toast.makeText(getActivity(), getString(R.string.select_pickup_location), Toast.LENGTH_LONG).show();
                        //  } else if (drop_location.getText().toString().trim().equals("")) {
                        //    Toast.makeText(getActivity(), getString(R.string.select_drop_location), Toast.LENGTH_LONG).show();
                        if (pickup_location == null || drop == null) {
                            Toast.makeText(getActivity(), getString(R.string.invalid_location), Toast.LENGTH_LONG).show();
                        } else if (driver_id == null || drivername == null) {
                            Toast.makeText(getActivity(), getString(R.string.select_driver), Toast.LENGTH_LONG).show();
                        } else if (cost == null || unit == null) {
                            Toast.makeText(getActivity(), getString(R.string.invalid_fare), Toast.LENGTH_SHORT).show();
                        } else {

                            Bundle bundle = new Bundle();
                            pass.setDriverId(driver_id);
                            pass.setDriverName(drivername);
                            bundle.putSerializable("data", pass);
                            RequestFragment fragobj = new RequestFragment();
                            fragobj.setArguments(bundle);
                            ((HomeActivity) getActivity()).changeFragment(fragobj, getString(R.string.request_ride));
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();

                    }
                }
            });
            custom_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (CheckConnection.haveNetworkConnection(getActivity())) {
                        if (s_drop == null || s_pic == null) {
                            Toast.makeText(getActivity(), getString(R.string.invalid_pickupaddress), Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), getString(R.string.invalid_droplocation), Toast.LENGTH_LONG).show();

                        } else {
                            //
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeFragment.this.getContext());
                            View mView = getLayoutInflater().inflate(R.layout.dialog_addtravel_layout, null);
                            final EditText mPassengers = (EditText) mView.findViewById(R.id.etPassengers);
                            final EditText mPrice = (EditText) mView.findViewById(R.id.etPrice);
                            Button mSubmit = (Button) mView.findViewById(R.id.btnSubmitDialog);
                            Button mCancel = (Button) mView.findViewById(R.id.btnCancelDialog);
                            CheckBox Checkbox = (CheckBox)mView.findViewById(R.id.checkBox);
                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();
                            mSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!mPassengers.getText().toString().isEmpty() ) {
//                                        Toast.makeText(HomeFragment.this.getContext(),
//                                                "Success",
//                                                Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        log.i("tag", "success by ibrahim");
                                        log.i("tag", mPassengers.getText().toString());
                                        log.i("tag", mPrice.getText().toString());
                                        Bundle bundle = new Bundle();
                                        String from_add = s_pic.getLatLng().latitude + "," + s_pic.getLatLng().longitude;
                                        String to_add = s_drop.getLatLng().latitude + "," + s_drop.getLatLng().longitude;
                                        pass.f = Pass.fragment_type.ADD;
                                        pass.setToPlace(s_drop.getAddress());
                                        pass.setToAddress(to_add);
                                        pass.setFromPlace(s_pic.getAddress());
                                        pass.setFromAddress(from_add);
                                        log.i("tag", "DateTime by ibrahim");
                                        log.i("tag", date_time_value);
                                        pass.setDate(date_time_value);
                                        pass.setDriverId("-1");
                                        pass.setFare(cost);
                                        pass.setDriverName(drivername);
                                        pass.setStatus("REQUESTED");
                                        pass.NoPassengers = Integer.parseInt(mPassengers.getText().toString());
 //                                       pass.TripPrice = Integer.parseInt(mPrice.getText().toString());

                                            if(Checkbox.isChecked())
                                                pass.setCheck("1");
                                            else pass.setCheck("0");

                                        bundle.putSerializable("data", pass);
                                        RequestFragment fragobj = new RequestFragment();
                                        fragobj.setArguments(bundle);
                                        ((HomeActivity) getActivity()).changeFragment(fragobj, getString(R.string.request_ride));
                                    } else {
                                        Toast.makeText(HomeFragment.this.getContext(),
                                                "Failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            mCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();

                    }
                }
            });
            liner_close.setOnClickListener(new View.OnClickListener() {
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


                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

// Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(getActivity());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                }
            });
            search_drop_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /* Intent intent =
                             new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                     .build(getActivity());
                     startActivityForResult(intent, PLACE_PICKER_REQUEST);*/


                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

// Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(getActivity());
                    startActivityForResult(intent, PLACE_search_AUTOCOMPLETE_REQUEST_CODE);

                }
            });
            search_pich_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /* Intent intent =
                             new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                     .build(getActivity());
                     startActivityForResult(intent, PLACE_PICKER_REQUEST);*/

                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

// Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(getActivity());
                    startActivityForResult(intent, PLACE_search_pic_AUTOCOMPLETE_REQUEST_CODE);

                }
            });

            search_box_custom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRefresh();

                    if (currentLatitude != null && !currentLatitude.equals(0.0) && currentLongitude != null && !currentLongitude.equals(0.0)) {
                        Intent intent = new Intent(getActivity(), List_provider.class);
                        intent.putExtra("cureentlatitude", String.valueOf(currentLatitude));
                        intent.putExtra("currentLongitude", String.valueOf(currentLongitude));
                        intent.putExtra("search_pich_location", String.valueOf(search_pich_location.getText().toString()));
                        intent.putExtra("search_drop_location", String.valueOf(search_drop_location.getText().toString()));
                        if (smoke_value != null) {
                            intent.putExtra("smoke_value", String.valueOf(smoke_value));
                        }
                        if (smoke_value != null) {
                            intent.putExtra("date_time_value", String.valueOf(date_time_value));
                        }
                        if (passanger_value != null) {
                            intent.putExtra("passanger_value", String.valueOf(passanger_value));
                        }
                        if (bag_value != null) {

                            intent.putExtra("bag_value", String.valueOf(bag_value));
                        }
                        startActivity(intent);


                        s_pic = null;
                        s_drop = null;

                    }

                }
            });
            drop_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

// Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                }
            });

        } catch (InflateException e) {

        }

        return rootView;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                getcurrentlocation();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                pickup = Autocomplete.getPlaceFromIntent(data);
                pickup_location.setText(pickup.getAddress());
                //  search_drop.setVisibility(View.VISIBLE);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e(TAG, status.toString());
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

        } else if (requestCode == PLACE_search_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                s_drop = Autocomplete.getPlaceFromIntent(data);
                search_drop_location.setText(s_drop.getAddress());

                Log.e(TAG, "search_drop: " + PLACE_search_AUTOCOMPLETE_REQUEST_CODE);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG).show();

            }
        } else if (requestCode == PLACE_search_pic_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                s_pic = Autocomplete.getPlaceFromIntent(data);
                search_pich_location.setText(s_pic.getAddress());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG).show();

            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }

    }

    public void onRefresh() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMapView != null) {

            mMapView.onStart();

        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {

            mMapView.onResume();

        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public void multipleMarker(List<NearbyData> list) {
        if (list != null) {
            for (NearbyData location : list) {
                Double latitude = null;
                Double longitude = null;
                try {
                    String[] parts = location.getPickup_location().split(",");

                    // latitude = Double.valueOf(location.getLatitude());
                    //longitude = Double.valueOf(location.getLongitude());
                    latitude = Double.valueOf(parts[0]);
                    longitude = Double.valueOf(parts[1]);

                    Marker marker = myMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(location.getName())
                            .snippet(location.getVehicle_info()));
                    marker.setTag(location);
                } catch (NumberFormatException e) {

                }

                CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 14);
                myMap.animateCamera(camera);
            }
        }

    }

    public void Search(List<NearbyData> list) {
        String[] da = new String[]{};
        if (list != null) {

            for (NearbyData search : list) {

                try {

                    da = new String[]{search.getPickup_address().toString()};

                } catch (NumberFormatException e) {

                }

                CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 14);
                myMap.animateCamera(camera);
            }
        }

    }


    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {


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
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        MapsInitializer.initialize(this.getActivity());
        current_location = (ImageView) rootView.findViewById(R.id.current_location);
        clear = (ImageView) rootView.findViewById(R.id.clear);
        // current_Search = (ImageView) rootView.findViewById(R.id.current_Search);
        txt_vehicleinfo = (TextView) rootView.findViewById(R.id.txt_vehicleinfo);
        rate = (TextView) rootView.findViewById(R.id.rate);
        //bag_search = (TextView) rootView.findViewById(R.id.bag_search);
        smoke_search = (TextView) rootView.findViewById(R.id.somoke_search);
        date_time_search = (TextView) rootView.findViewById(R.id.Time_date_search);
        passanger_search = (TextView) rootView.findViewById(R.id.passenger_search);

        txt_info = (TextView) rootView.findViewById(R.id.txt_info);
        txt_address = (TextView) rootView.findViewById(R.id.txt_addresss);
        request_ride = (TextView) rootView.findViewById(R.id.request_rides);
        txt_fee = (TextView) rootView.findViewById(R.id.TravelFee);
        txt_date = (TextView) rootView.findViewById(R.id.TravelDate);
        txt_smoke = (TextView) rootView.findViewById(R.id.Smoke);

        txt_color = (TextView) rootView.findViewById(R.id.txt_color);
        txt_cost = (TextView) rootView.findViewById(R.id.txt_cost);
        mMapView = (MapView) rootView.findViewById(R.id.mapview);
        linear_request = (LinearLayout) rootView.findViewById(R.id.linear_request);
        liner_close = (LinearLayout) rootView.findViewById(R.id.linear_clear);
        custom_request = (Button) rootView.findViewById(R.id.ride_add_btn);
        search_box_custom = (Button) rootView.findViewById(R.id.search_for_users_btn);

        header = (RelativeLayout) rootView.findViewById(R.id.header);
        search_box = (RelativeLayout) rootView.findViewById(R.id.search_box_rel);
        footer = (RelativeLayout) rootView.findViewById(R.id.footer);
        //  search_drop = (RelativeLayout) rootView.findViewById(R.id.search_drop);

        pickup_location = (TextView) rootView.findViewById(R.id.pickup_location);
        search_drop_location = (TextView) rootView.findViewById(R.id.search_drop_location);
        search_pich_location = (TextView) rootView.findViewById(R.id.pickup_search_location);

        drop_location = (TextView) rootView.findViewById(R.id.drop_location);
        linear_pickup = (RelativeLayout) rootView.findViewById(R.id.linear_pickup);
        relative_drop = (RelativeLayout) rootView.findViewById(R.id.relative_drop);
        /*mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);*/
        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
        Places.initialize(getApplicationContext(), getString(R.string.google_android_map_api_key));
        pass = new Pass();
        // load animations
        animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(getActivity(),
                R.anim.fade_out);
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
        applyfonts();
        placesClient = Places.createClient(getActivity());

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
        smoke_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Somke");
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.box_input, (ViewGroup) getView(), false);
                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                final TextInputLayout inputvalue = (TextInputLayout) viewInflated.findViewById(R.id.input_value);
                final RadioButton no = (RadioButton) viewInflated.findViewById(R.id.no);
                final RadioButton yes = (RadioButton) viewInflated.findViewById(R.id.yes);

                inputvalue.setVisibility(View.GONE);
                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (yes.isChecked()) {
                            smoke_value = "Yes";

                        } else {
                            smoke_value = "no";
                        }
                        //log.e("grouop_id", String.valueOf(gruop_id));
                        //gruop_id = gruop_id;
                        //phone = input.getText().toString();
                        //Add_user_Group(phone,Driver_groups_model.getGroup_id());
                        txt_smoke.setText(smoke_value);
                        //Toast.makeText(getContext(), "smoked" + smoke_value, Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


            }
        });
        passanger_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Passanger Number");
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.box_input, (ViewGroup) getView(), false);
                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                final TextInputLayout inputvalue = (TextInputLayout) viewInflated.findViewById(R.id.input_value);
                // final RadioButton no = (RadioButton) viewInflated.findViewById(R.id.no);
                //final RadioButton yes = (RadioButton) viewInflated.findViewById(R.id.yes);
                final LinearLayout smoke_lyner = (LinearLayout) viewInflated.findViewById(R.id.smoke_lyner);

                smoke_lyner.setVisibility(View.GONE);
                builder.setView(viewInflated);


                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                        if (!input.getText().toString().equals("")) {

                            passanger_value = String.valueOf(input.getText());
                            passanger_search.setText(String.valueOf(input.getText()));

                        }


                        //log.e("grouop_id", String.valueOf(gruop_id));
                        //gruop_id = gruop_id;
                        //phone = input.getText().toString();
                        //Add_user_Group(phone,Driver_groups_model.getGroup_id());
                        //  Toast.makeText(getContext(), "passs" + passanger_value, Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        date_time_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                datePicker();
                //  Toast.makeText(getContext(), "date" + date_time_value, Toast.LENGTH_SHORT).show();


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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        int result = ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                //If everything went fine lets get latitude and longitude
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                if (!currentLatitude.equals(0.0) && !currentLongitude.equals(0.0)) {
                    if (!flag) {
                        //     NeaBy(String.valueOf(currentLatitude), String.valueOf(currentLongitude),search_pich_location.getText().toString(),search_drop_location.getText().toString(),smoke_value,date_time_value,passanger_value,bag_value);
                    }
                } else {

                    Toast.makeText(getActivity(), getString(R.string.couldnt_get_location), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            askCompactPermissions(permissionAsk, new PermissionResult() {
                @Override
                public void permissionGranted() {

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

        }


    }

    private void datePicker() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

//                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        date_time = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        //*************Call Time Picker Here ********************
                       // date_time_search.setText(date_time);
                        tiemPicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void tiemPicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {


                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        date_time_value = date_time + " " + hourOfDay + ":" + minute + ":00";
                        date_time_search.setText(date_time_value);
                    }
                }, mHour, 0, true);
        timePickerDialog.show();
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
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

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
                                    Log.i(TAG, String.format("Place '%s' has likelihood: %f",
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
                                    pickup_location.setText(placeLikelihood.getPlace().getAddress());
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
    public void onLocationChanged(android.location.Location location) {
        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        }
    }


    public void applyfonts() {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
        Typeface font1 = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Book.otf");
        pickup_location.setTypeface(font);
        drop_location.setTypeface(font);
        txt_vehicleinfo.setTypeface(font1);
        rate.setTypeface(font1);

        txt_color.setTypeface(font);
        txt_address.setTypeface(font);
        request_ride.setTypeface(font1);


    }


    public void getcurrentlocation() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
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
                            getcurrentlocation();
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

    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            return true;

        } else {
            return false;
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {

                View v = getActivity().getLayoutInflater().inflate(R.layout.view_custom_marker, null);

                LatLng latLng = marker.getPosition();
                TextView title = (TextView) v.findViewById(R.id.t);
                TextView t1 = (TextView) v.findViewById(R.id.t1);
                TextView t2 = (TextView) v.findViewById(R.id.t2);
                ImageView imageView = (ImageView) v.findViewById(R.id.profile_image);
                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
                t1.setTypeface(font);
                t2.setTypeface(font);
                String name = marker.getTitle();
                title.setText(name);
                String info = marker.getSnippet();
                t1.setText(info);

                NearbyData nearbyData = (NearbyData) marker.getTag();


                if (nearbyData != null) {
                    log.i("tag", "success by ibrahim1234");
                    log.i("tag", "success by ibrahim1234");
                    log.i("tag", "success by ibrahim1234");
                    log.i("tag", "success by ibrahim1234");
                    log.i("tag", "success by ibrahim1234");
                    log.i("tag", nearbyData.getVehicle_info());
                    pass.setVehicleName(nearbyData.getVehicle_info());
                    txt_info.setText(nearbyData.getVehicle_info());
                    txt_address.setText("");
                    driver_id = nearbyData.getUser_id();
                    pickup = new Place() {
                        @Nullable
                        @Override
                        public String getAddress() {
                            return nearbyData.getPickup_address();
                        }

                        @Nullable
                        @Override
                        public List<String> getAttributions() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public String getId() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public LatLng getLatLng() {
                            String[] parts = nearbyData.getPickup_location().split(",");
                            LatLng location = new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));

                            return location;
                        }

                        @Nullable
                        @Override
                        public String getName() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public OpeningHours getOpeningHours() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public String getPhoneNumber() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public List<PhotoMetadata> getPhotoMetadatas() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public PlusCode getPlusCode() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Integer getPriceLevel() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Double getRating() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public List<Type> getTypes() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Integer getUserRatingsTotal() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public LatLngBounds getViewport() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Uri getWebsiteUri() {
                            return null;
                        }

                        @Override
                        public int describeContents() {
                            return 0;
                        }

                        @Override
                        public void writeToParcel(Parcel dest, int flags) {

                        }
                    };
                    drop = new Place() {
                        @Nullable
                        @Override
                        public String getAddress() {
                            return nearbyData.getDrop_address();
                        }

                        @Nullable
                        @Override
                        public List<String> getAttributions() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public String getId() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public LatLng getLatLng() {
                            String[] parts = nearbyData.getDrop_location().split(",");
                            LatLng location = new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));

                            return location;
                        }

                        @Nullable
                        @Override
                        public String getName() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public OpeningHours getOpeningHours() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public String getPhoneNumber() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public List<PhotoMetadata> getPhotoMetadatas() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public PlusCode getPlusCode() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Integer getPriceLevel() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Double getRating() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public List<Type> getTypes() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Integer getUserRatingsTotal() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public LatLngBounds getViewport() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Uri getWebsiteUri() {
                            return null;
                        }

                        @Override
                        public int describeContents() {
                            return 0;
                        }

                        @Override
                        public void writeToParcel(Parcel dest, int flags) {

                        }
                    };
                    pickup_location.setText(pickup.getAddress());
                    drop_location.setText(drop.getAddress());
                    drivername = marker.getTitle();
                    t2.setVisibility(View.VISIBLE);
                } else {
                    t2.setVisibility(View.GONE);
                }
                unit = nearbyData.getAmount();
                txt_cost.setText(unit);
                SessionManager.setUnit(unit);

                txt_address.setText(getAdd(Double.valueOf(nearbyData.getLatitude()), Double.valueOf(nearbyData.getLongitude())) + " " + "PickUp Location");
                txt_smoke.setText(nearbyData.getSomked());
                txt_date.setText("Date: " + nearbyData.getTravel_date() + " Time: " + nearbyData.getTravel_time());
                txt_fee.setText(nearbyData.getAmount());


                return v;

            }
        });

        myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                NearbyData nearbyData = (NearbyData) marker.getTag();
                if (nearbyData != null) {
                    driver_id = nearbyData.getUser_id();
                    drivername = marker.getTitle();
                }


                if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
                    header.startAnimation(animFadeOut);
                    footer.startAnimation(animFadeOut);
                    header.setVisibility(View.GONE);
                    footer.setVisibility(View.GONE);
                } else {

                    header.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.VISIBLE);
                    header.startAnimation(animFadeIn);
                    footer.startAnimation(animFadeIn);
                }

            }
        });

        if (myMap != null) {
            tunonGps();
        }

    }


    private String getAdd(double latitude, double longitude) {
        String finalAddress = null;
        try {

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            finalAddress = address + ", " + city + "," + state + "," + country;


        } catch (Exception e) {

        }
        return finalAddress;
    }

    public void changeFragment(final Fragment fragment, final String fragmenttag) {

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack(null);
                }
            }, 50);
        } catch (Exception e) {

        }

    }


    @Override
    public boolean onBackPressed() {
        Log.d("app fargment", "onBackPressed: homeprovider ");

        return false;
    }

    @Override
    public int getBackPriority() {
        return NORMAL_BACK_PRIORITY;
    }
}


