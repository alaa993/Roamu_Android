package com.alaan.roamu.fragement;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.List_provider;
import com.fxn.stash.Stash;
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
import static com.alaan.roamu.fragement.lang.setLocale;
import static com.loopj.android.http.AsyncHttpClient.log;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class HomeFragment extends Fragment implements BackFragment, AdapterView.OnItemSelectedListener {
    private static final String TAG = "HomeFragment";
    private String driver_id, passanger_value, bag_value, smoke_value, date_time_value, time_value;
    private String cost;
    private String unit;
    private int PLACE_PICKER_REQUEST = 7896;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1234;
    private int PLACE_search_AUTOCOMPLETE_REQUEST_CODE = 7777;
    private int PLACE_search_pic_AUTOCOMPLETE_REQUEST_CODE = 7778;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    //    private Double currentLatitude;
//    private Double currentLongitude;
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
//    MapView mMapView;
    Pass pass;
    Place pickup, drop, s_drop, s_pic;
    ProgressBar progressBar;
    private PlacesClient placesClient;
    RecyclerView recyclerView;
    private CheckBox Checkbox;

    TextView NS_car_type;
    Spinner droplist;
    String[] status_arr;
    String[] status_Content_arr={"car","minibus","bus"};
    String carType = "car";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.home_fragment, container, false);
            ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.home));
            bindView(savedInstanceState);

//            search_box.setVisibility(View.VISIBLE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                askCompactPermissions(permissionAsk, new PermissionResult() {
//                    @Override
//                    public void permissionGranted() {
//                        if (!GPSEnable()) {
////                            tunonGps();
//                        } else {
////                            getcurrentlocation();
//                        }
//                    }
//
//                    @Override
//                    public void permissionDenied() {
//                    }
//
//                    @Override
//                    public void permissionForeverDenied() {
//                        openSettingsApp(getActivity());
//                    }
//                });
//            } else {
//                if (!GPSEnable()) {
//                    tunonGps();
//                } else {
//                    getcurrentlocation();
//                }
//            }

            linear_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("ibrahim","linear_request");
                    if (CheckConnection.haveNetworkConnection(getActivity())) {
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
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeFragment.this.getContext());
                            View mView = getLayoutInflater().inflate(R.layout.dialog_addtravel_layout, null);
                            final EditText mPassengers = (EditText) mView.findViewById(R.id.etPassengers);
                            final EditText mPrice = (EditText) mView.findViewById(R.id.etPrice);
                            Button mSubmit = (Button) mView.findViewById(R.id.btnSubmitDialog);
                            Button mCancel = (Button) mView.findViewById(R.id.btnCancelDialog);
                            Checkbox = (CheckBox) mView.findViewById(R.id.checkBox);
                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();
                            mSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!mPassengers.getText().toString().isEmpty()) {
                                        dialog.dismiss();
                                        String from_add = s_pic.getLatLng().latitude + "," + s_pic.getLatLng().longitude;
                                        String to_add = s_drop.getLatLng().latitude + "," + s_drop.getLatLng().longitude;
                                        AddRide(SessionManager.getKEY(), s_pic.getAddress(), s_drop.getAddress(), from_add, to_add, String.valueOf(mPrice.getText()), "0", String.valueOf(mPassengers.getText()));
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
//            clear.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
//                        header.startAnimation(animFadeOut);
//                        footer.startAnimation(animFadeOut);
//                        header.setVisibility(View.GONE);
//                        footer.setVisibility(View.GONE);
//                    }
//                }
//            });
//            pickup_location.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
//                    Intent intent = new Autocomplete.IntentBuilder(
//                            AutocompleteActivityMode.FULLSCREEN, fields)
//                            .build(getActivity());
//                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
//                }
//            });
            search_drop_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(getActivity());
                    startActivityForResult(intent, PLACE_search_AUTOCOMPLETE_REQUEST_CODE);
                }
            });
            search_pich_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(getActivity());
                    startActivityForResult(intent, PLACE_search_pic_AUTOCOMPLETE_REQUEST_CODE);
                }
            });

            search_box_custom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("ibrahim","search_box_custom");
//                    onRefresh();
//                    if (currentLatitude != null && !currentLatitude.equals(0.0) && currentLongitude != null && !currentLongitude.equals(0.0)) {
                    Intent intent = new Intent(getActivity(), List_provider.class);
//                        intent.putExtra("cureentlatitude", String.valueOf(currentLatitude));
//                        intent.putExtra("currentLongitude", String.valueOf(currentLongitude));
                    intent.putExtra("search_pich_location", String.valueOf(search_pich_location.getText().toString()));
                    intent.putExtra("search_drop_location", String.valueOf(search_drop_location.getText().toString()));
                    intent.putExtra("car_type", carType);

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
//                    }
                }
            });
//            drop_location.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
//                    Intent intent = new Autocomplete.IntentBuilder(
//                            AutocompleteActivityMode.FULLSCREEN, fields)
//                            .build(getActivity());
//                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//                }
//            });

        } catch (InflateException e) {
        }
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
//                getcurrentlocation();
            }
            if (resultCode == RESULT_CANCELED) {
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                pickup = Autocomplete.getPlaceFromIntent(data);
                pickup_location.setText(pickup.getAddress());
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

//    @Override
//    public void onPause() {
//        super.onPause();
//        if (mMapView != null) {
//            mMapView.onPause();
//        }
//        if (mGoogleApiClient != null) {
//            if (mGoogleApiClient.isConnected()) {
//                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//                mGoogleApiClient.disconnect();
//            }
//        }
//    }


//    public void multipleMarker(List<NearbyData> list) {
//        if (list != null) {
//            for (NearbyData location : list) {
//                Double latitude = null;
//                Double longitude = null;
//                try {
//                    String[] parts = location.getPickup_location().split(",");
//                    latitude = Double.valueOf(parts[0]);
//                    longitude = Double.valueOf(parts[1]);
//                    Marker marker = myMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(latitude, longitude))
//                            .title(location.getName())
//                            .snippet(location.getVehicle_info()));
//                    marker.setTag(location);
//                } catch (NumberFormatException e) {
//                }
//                CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 14);
//                myMap.animateCamera(camera);
//            }
//        }
//    }

//    public void Search(List<NearbyData> list) {
//        String[] da = new String[]{};
//        if (list != null) {
//            for (NearbyData search : list) {
//                try {
//                    da = new String[]{search.getPickup_address().toString()};
//                } catch (NumberFormatException e) {
//                }
//                CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 14);
//                myMap.animateCamera(camera);
//            }
//        }
//    }

//    @Override
//    public void onDirectionSuccess(Direction direction, String rawBody) {
//    }
//
//    @Override
//    public void onDirectionFailure(Throwable t) {
//    }
//
//    @Override
//    public void onAnimationStart(Animation animation) {
//    }
//
//    @Override
//    public void onAnimationEnd(Animation animation) {
//    }
//
//    @Override
//    public void onAnimationRepeat(Animation animation) {
//    }

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
        linear_request = (LinearLayout) rootView.findViewById(R.id.linear_request);
        liner_close = (LinearLayout) rootView.findViewById(R.id.linear_clear);
        custom_request = (Button) rootView.findViewById(R.id.ride_add_btn);
        search_box_custom = (Button) rootView.findViewById(R.id.search_for_users_btn);
        header = (RelativeLayout) rootView.findViewById(R.id.header);
        search_box = (RelativeLayout) rootView.findViewById(R.id.search_box_rel);
        footer = (RelativeLayout) rootView.findViewById(R.id.footer);
        pickup_location = (TextView) rootView.findViewById(R.id.pickup_location);
        search_drop_location = (TextView) rootView.findViewById(R.id.search_drop_location);
        search_pich_location = (TextView) rootView.findViewById(R.id.pickup_search_location);
        drop_location = (TextView) rootView.findViewById(R.id.drop_location);
        linear_pickup = (RelativeLayout) rootView.findViewById(R.id.linear_pickup);
        relative_drop = (RelativeLayout) rootView.findViewById(R.id.relative_drop);

        NS_car_type = (TextView) rootView.findViewById(R.id.NS_car_type);
        droplist = (Spinner) rootView.findViewById(R.id.carTypeSpinner);
        droplist.setOnItemSelectedListener(this);
        status_arr = new String[]{getString(R.string.car_type1), getString(R.string.car_type2), getString(R.string.car_type3)};
        ArrayAdapter data = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,status_arr);
        droplist.setAdapter(data);

        Places.initialize(getApplicationContext(), getString(R.string.google_android_map_api_key));
        pass = new Pass();
        animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
//        animFadeIn.setAnimationListener(this);
//        animFadeOut.setAnimationListener(this);
//        applyfonts();
        placesClient = Places.createClient(getActivity());
//        clear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                drop_location.setText("");
//                if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
//                    header.startAnimation(animFadeOut);
//                    footer.startAnimation(animFadeOut);
//                    header.setVisibility(View.GONE);
//                    footer.setVisibility(View.GONE);
//                }
//            }
//        });
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
                        txt_smoke.setText(smoke_value);
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

            }
        });
//        current_location.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    askCompactPermissions(permissionAsk, new PermissionResult() {
//                        @Override
//                        public void permissionGranted() {
//                            if (pickup_location.getText().toString().trim().equals("")) {
//                                setCurrentLocation();
//                            } else {
//                                pickup_location.setText("");
//                                current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
//                            }
//                        }
//
//                        @Override
//                        public void permissionDenied() {
//                        }
//
//                        @Override
//                        public void permissionForeverDenied() {
//                            Snackbar.make(rootView, getString(R.string.allow_permission), Snackbar.LENGTH_LONG).show();
//                            openSettingsApp(getActivity());
//                        }
//                    });
//                } else {
//                    if (!GPSEnable()) {
//                        tunonGps();
//                    } else {
//                        if (pickup_location.getText().toString().trim().equals("")) {
//                            setCurrentLocation();
//                        } else {
//                            pickup_location.setText("");
//                            current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
//                        }
//                    }
//                }
//            }
//        });
    }

//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        // error by ibrahim
//        int result = ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);
//        if (result == PackageManager.PERMISSION_GRANTED) {
//            android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            if (location == null) {
//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//            } else {
//                currentLatitude = location.getLatitude();
//                currentLongitude = location.getLongitude();
//                if (!currentLatitude.equals(0.0) && !currentLongitude.equals(0.0)) {
//                    if (!flag) {
//                    }
//                } else {
//                    Toast.makeText(getActivity(), getString(R.string.couldnt_get_location), Toast.LENGTH_LONG).show();
//                }
//            }
//        } else {
//            askCompactPermissions(permissionAsk, new PermissionResult() {
//                @Override
//                public void permissionGranted() {
//                }
//
//                @Override
//                public void permissionDenied() {
//                }
//
//                @Override
//                public void permissionForeverDenied() {
//                    Snackbar.make(rootView, getString(R.string.allow_permission), Snackbar.LENGTH_LONG).show();
//                    openSettingsApp(getActivity());
//                }
//            });
//        }
//    }

    private void datePicker() {
        setLocale("en", getActivity());
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    //                    @SuppressLint("US")
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (Locale.getDefault().getLanguage().equals("ar")) {
                            Log.i("lang_ibrahim", "arabic");
                            Log.i("lang_ibrahim", Locale.getDefault().getLanguage());
                            date_time_value = String.format("%02d-%02d-%04d", dayOfMonth, 1 + monthOfYear, year);
                        } else {
                            Log.i("lang_ibrahim", "english");
                            Log.i("lang_ibrahim", Locale.getDefault().getLanguage());
                            date_time_value = String.format("%04d-%02d-%02d", year, 1 + monthOfYear, dayOfMonth);
                        }

                        tiemPicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void tiemPicker() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    //                    @SuppressLint("US")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;

                        time_value = String.format("%02d:%02d", hourOfDay, minute);
                        date_time_search.setText(date_time_value + " " + time_value);
                        setLocale("ar", getActivity());
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

//    private void setCurrentLocation() {
//        if (!GPSEnable()) {
//            tunonGps();
//
//        } else {
//            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//                try {
//                    List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
//                    FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();
//                    if (ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
//                        placeResponse.addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                FindCurrentPlaceResponse response = task.getResult();
//                                if (response != null && response.getPlaceLikelihoods() != null) {
//                                    PlaceLikelihood placeLikelihood = response.getPlaceLikelihoods().get(0);
//                                    pickup = placeLikelihood.getPlace();
//                                    pickup_location.setText(placeLikelihood.getPlace().getAddress());
//                                    current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));
//                                }
//                            } else {
//                                Exception exception = task.getException();
//                                if (exception instanceof ApiException) {
//                                    ApiException apiException = (ApiException) exception;
//                                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
//                                }
//                            }
//                        });
//                    }
//                } catch (Exception e) {
//                }
//            }
//        }
//    }

//    @Override
//    public void onConnectionSuspended(int i) {
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        if (connectionResult.hasResolution()) {
//            try {
//                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
//            } catch (IntentSender.SendIntentException e) {
//
//                e.printStackTrace();
//            }
//        } else {
//        }
//    }

//    @Override
//    public void onLocationChanged(android.location.Location location) {
//        if (location != null) {
//            currentLatitude = location.getLatitude();
//            currentLongitude = location.getLongitude();
//        }
//    }

//    public void applyfonts() {
//        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
//        Typeface font1 = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Book.otf");
//        pickup_location.setTypeface(font);
//        drop_location.setTypeface(font);
//        txt_vehicleinfo.setTypeface(font1);
//        rate.setTypeface(font1);
//        txt_color.setTypeface(font);
//        txt_address.setTypeface(font);
//        request_ride.setTypeface(font1);
//    }

//    public void getcurrentlocation() {
//
//        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//
//        // Create the LocationRequest object
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(30 * 1000);
//        mLocationRequest.setFastestInterval(5 * 1000);
//    }

//    public void tunonGps() {
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                    .addApi(LocationServices.API).addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this).build();
//            mGoogleApiClient.connect();
//            mLocationRequest = LocationRequest.create();
//            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            mLocationRequest.setInterval(30 * 1000);
//            mLocationRequest.setFastestInterval(5 * 1000);
//            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                    .addLocationRequest(mLocationRequest);
//
//            // **************************
//            builder.setAlwaysShow(true); // this is the key ingredient
//            // **************************
//
//            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
//                    .checkLocationSettings(mGoogleApiClient, builder.build());
//            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//                @Override
//                public void onResult(LocationSettingsResult result) {
//                    final Status status = result.getStatus();
//                    final LocationSettingsStates state = result
//                            .getLocationSettingsStates();
//                    switch (status.getStatusCode()) {
//                        case LocationSettingsStatusCodes.SUCCESS:
//                            getcurrentlocation();
//                            break;
//                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                            try {
//                                // Show the dialog by calling
//                                // startResolutionForResult(),
//                                // and setting the result in onActivityResult().
//                                status.startResolutionForResult(getActivity(), 1000);
//                            } catch (IntentSender.SendIntentException e) {
//                                // Ignore the error.
//                            }
//                            break;
//                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                            // Location settings are not satisfied. However, we have
//                            // no way to fix the
//                            // settings so we won't show the dialog.
//                            break;
//                    }
//                }
//            });
//        }
//
//    }

//    public Boolean GPSEnable() {
//        GPSTracker gpsTracker = new GPSTracker(getActivity());
//        if (gpsTracker.canGetLocation()) {
//            return true;
//
//        } else {
//            return false;
//        }
//
//
//    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        myMap = googleMap;
//        myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(final Marker marker) {
//
//                View v = getActivity().getLayoutInflater().inflate(R.layout.view_custom_marker, null);
//
//                LatLng latLng = marker.getPosition();
//                TextView title = (TextView) v.findViewById(R.id.t);
//                TextView t1 = (TextView) v.findViewById(R.id.t1);
//                TextView t2 = (TextView) v.findViewById(R.id.t2);
//                ImageView imageView = (ImageView) v.findViewById(R.id.profile_image);
//                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
//                t1.setTypeface(font);
//                t2.setTypeface(font);
//                String name = marker.getTitle();
//                title.setText(name);
//                String info = marker.getSnippet();
//                t1.setText(info);
//
//                NearbyData nearbyData = (NearbyData) marker.getTag();
//
//
//                if (nearbyData != null) {
//                    pass.setVehicleName(nearbyData.getVehicle_info());
//                    txt_info.setText(nearbyData.getVehicle_info());
//                    txt_address.setText("");
//                    driver_id = nearbyData.getUser_id();
//                    pickup = new Place() {
//                        @Nullable
//                        @Override
//                        public String getAddress() {
//                            return nearbyData.getPickup_address();
//                        }
//
//                        @Nullable
//                        @Override
//                        public List<String> getAttributions() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public String getId() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public LatLng getLatLng() {
//                            String[] parts = nearbyData.getPickup_location().split(",");
//                            LatLng location = new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
//
//                            return location;
//                        }
//
//                        @Nullable
//                        @Override
//                        public String getName() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public OpeningHours getOpeningHours() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public String getPhoneNumber() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public List<PhotoMetadata> getPhotoMetadatas() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public PlusCode getPlusCode() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public Integer getPriceLevel() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public Double getRating() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public List<Type> getTypes() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public Integer getUserRatingsTotal() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public LatLngBounds getViewport() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public Uri getWebsiteUri() {
//                            return null;
//                        }
//
//                        @Override
//                        public int describeContents() {
//                            return 0;
//                        }
//
//                        @Override
//                        public void writeToParcel(Parcel dest, int flags) {
//
//                        }
//                    };
//                    drop = new Place() {
//                        @Nullable
//                        @Override
//                        public String getAddress() {
//                            return nearbyData.getDrop_address();
//                        }
//
//                        @Nullable
//                        @Override
//                        public List<String> getAttributions() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public String getId() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public LatLng getLatLng() {
//                            String[] parts = nearbyData.getDrop_location().split(",");
//                            LatLng location = new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
//
//                            return location;
//                        }
//
//                        @Nullable
//                        @Override
//                        public String getName() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public OpeningHours getOpeningHours() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public String getPhoneNumber() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public List<PhotoMetadata> getPhotoMetadatas() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public PlusCode getPlusCode() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public Integer getPriceLevel() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public Double getRating() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public List<Type> getTypes() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public Integer getUserRatingsTotal() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public LatLngBounds getViewport() {
//                            return null;
//                        }
//
//                        @Nullable
//                        @Override
//                        public Uri getWebsiteUri() {
//                            return null;
//                        }
//
//                        @Override
//                        public int describeContents() {
//                            return 0;
//                        }
//
//                        @Override
//                        public void writeToParcel(Parcel dest, int flags) {
//
//                        }
//                    };
//                    pickup_location.setText(pickup.getAddress());
//                    drop_location.setText(drop.getAddress());
//                    drivername = marker.getTitle();
//                    t2.setVisibility(View.VISIBLE);
//                } else {
//                    t2.setVisibility(View.GONE);
//                }
//                unit = nearbyData.getAmount();
//                txt_cost.setText(unit);
//                SessionManager.setUnit(unit);
//
//                txt_address.setText(getAdd(Double.valueOf(nearbyData.getLatitude()), Double.valueOf(nearbyData.getLongitude())) + " " + "PickUp Location");
//                txt_smoke.setText(nearbyData.getsmoked());
//                txt_date.setText("Date: " + nearbyData.getTravel_date() + " Time: " + nearbyData.getTravel_time());
//                txt_fee.setText(nearbyData.getAmount());
//
//
//                return v;
//
//            }
//        });
//
//        myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                NearbyData nearbyData = (NearbyData) marker.getTag();
//                if (nearbyData != null) {
//                    driver_id = nearbyData.getUser_id();
//                    drivername = marker.getTitle();
//                }
//
//
//                if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
//                    header.startAnimation(animFadeOut);
//                    footer.startAnimation(animFadeOut);
//                    header.setVisibility(View.GONE);
//                    footer.setVisibility(View.GONE);
//                } else {
//
//                    header.setVisibility(View.VISIBLE);
//                    footer.setVisibility(View.VISIBLE);
//                    header.startAnimation(animFadeIn);
//                    footer.startAnimation(animFadeIn);
//                }
//
//            }
//        });
//
//        if (myMap != null) {
//            tunonGps();
//        }
//
//    }

//    private String getAdd(double latitude, double longitude) {
//        String finalAddress = null;
//        try {
//
//            Geocoder geocoder;
//            List<Address> addresses;
//            geocoder = new Geocoder(getActivity(), Locale.getDefault());
//            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//
//            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            finalAddress = address + ", " + city + "," + state + "," + country;
//
//
//        } catch (Exception e) {
//
//        }
//        return finalAddress;
//    }

    public void changeFragment(final Fragment fragment, final String fragmenttag) {

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();//.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
                    fragmentTransaction.commit();
                    //fragmentTransaction.addToBackStack(null);
                }
            }, 50);
        } catch (Exception e) {

        }

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public int getBackPriority() {
        return NORMAL_BACK_PRIORITY;
    }

    // pending request by ibrahim
    public void AddRide(String key, String pickup_address, String drop_address, String pickup_location, String drop_location, String amount, String distance, String booked_set) {
        final RequestParams params = new RequestParams();
        params.put("driver_id", "-1");
        //by ibrahim
        params.put("travel_id", "-1");
        params.put("user_id", SessionManager.getUserId());
        params.put("pickup_address", pickup_address);
        params.put("drop_address", drop_address);
        params.put("date", date_time_value);
        params.put("time", time_value);
        params.put("pickup_location", pickup_location);
        params.put("drop_location", drop_location);
        params.put("Ride_smoked", "0");
        params.put("amount", amount);
        params.put("distance", distance);
        params.put("status", "REQUESTED");
        params.put("booked_set", booked_set);
        params.put("car_type", carType);
        Server.setHeader(key);
        Server.post("api/user/addRide2/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        Toast.makeText(getActivity(), getString(R.string.ride_has_been_requested), Toast.LENGTH_LONG).show();
                        if (response.has("data")) {
                            JSONObject data = response.getJSONObject("data");
                            int ride_id = Integer.parseInt(data.getString("ride_id"));
                            if (Checkbox.isChecked()) {
                                SavePost(pickup_address, drop_address, date_time_value, time_value, ride_id);
                            }
                            addRideFirebase(ride_id, "", "REQUESTED", "", "");
                            search_drop_location.setText("");
                            search_pich_location.setText("");
                            date_time_search.setText("");
                        }
//                        startActivity(new Intent(getContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.try_again), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.try_again), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void SavePost(String pickup_address, String Drop_address, String date_time_value, String time_value, int ride_id) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(uid.toString());
        databaseRefID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String UserName = dataSnapshot.child("username").getValue(String.class);
                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                String text = getString(R.string.Travel_is_going_from) + " " + System.getProperty("line.separator")
                        + getString(R.string.Travel_from) + " " + pickup_address + System.getProperty("line.separator")
                        + getString(R.string.Travel_to) + " " + Drop_address + System.getProperty("line.separator")
                        + getString(R.string.Travel_on) + " " + date_time_value + System.getProperty("line.separator")
                        + getString(R.string.the_clock) + " " + time_value;
                // Firebase code here
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts").push();
                Map<String, Object> author = new HashMap<>();
                author.put("uid", user.getUid());
                author.put("username", UserName);
                author.put("photoURL", photoURL);
                Map<String, Object> userObject = new HashMap<>();
                userObject.put("author", author);
                userObject.put("text", text);
                //type = 0 => driver
                //type = 1 => user
                userObject.put("type", "1");
                userObject.put("privacy", "1");
                userObject.put("travel_id", ride_id);
                userObject.put("timestamp", ServerValue.TIMESTAMP);
                databaseRef.setValue(userObject);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    public void addRideFirebase(int ride_id_param, String travel_status, String ride_status, String payment_status, String payment_mode) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("rides").child(String.valueOf(ride_id_param));
        Map<String, Object> rideObject = new HashMap<>();

        rideObject.put("ride_status", ride_status);
        rideObject.put("travel_status", travel_status);
        rideObject.put("payment_status", payment_status);
        rideObject.put("payment_mode", payment_mode);
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        databaseRef.setValue(rideObject);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i("ibrahim", status_arr[i]);
        carType = status_Content_arr[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}