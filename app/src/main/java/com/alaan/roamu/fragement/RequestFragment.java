package com.alaan.roamu.fragement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.alaan.roamu.PostActivity;
import com.alaan.roamu.acitivities.List_provider;
import com.alaan.roamu.acitivities.image_view;
import com.alaan.roamu.pojo.PendingRequestPojo;
import com.alaan.roamu.pojo.Post;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.custom.CheckConnection;
import com.alaan.roamu.pojo.Pass;
import com.alaan.roamu.session.MessageUtils;
import com.alaan.roamu.session.SessionManager;
import com.alaan.roamu.session.Utility;
import com.alaan.roamu.session.Validate;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlusCode;
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
import com.paypal.android.sdk.payments.PayPalService;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;

import net.skoumal.fragmentback.BackFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

;import static com.loopj.android.http.AsyncHttpClient.log;
import static gun0912.tedbottompicker.TedBottomPicker.TAG;


public class RequestFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback, BackFragment {
    View view;
    AppCompatButton confirm, cancel;
    TextView pickup_location, drop_location;
    Double finalfare;
    Place pickup, drop, s_drop, s_pic;

    MapView mapView;
    private Double fare;
    GoogleMap myMap;
    AlertDialog alert;
    private LatLng origin;
    private LatLng destination;
    private String networkAvailable;
    private String tryAgain;
    private String directionRequest;
    TextView textViewCity, textView1, textView2, textView3, textView4, textView5, textView6, textView7, textView8, textView9, textView10, txt_fare_view, txt_name, txt_number, title, txt_vehiclename, dateandtime, TimeVal, txt_bag, txt_smoke, car_name;
    TextView txt_Driver_name, txt_city, txt_Empty_Seats, txt_DriverRate, txt_TravelsCount, txt_PickupPoint, txt_fare, fianl_fare;
    private ImageView DriverAvatar;
    private ImageView DriverCar;
    EditText cobun_num;
    ElegantNumberButton num_set;
    String driver_id;
    String travel_id;
    private String user_id;
    private String pickup_address, drop_address, dateandtime_val, time_val;
    String distance;
    private String drivername = "";
    SwipeRefreshLayout swipeRefreshLayout;
    Pass pass;
    PendingRequestPojo pass1;
    TextView calculateFare;
    Snackbar snackbar;
    Button btn_cobo;
    TableRow rating, car, fare_rating;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        networkAvailable = getResources().getString(R.string.network);
        tryAgain = getResources().getString(R.string.try_again);

        // directionRequest = getResources().getString(R.string.direction_request);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        container.removeAllViews(); // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.request_ride, container, false);
        if (!CheckConnection.haveNetworkConnection(getActivity())) {
            Toast.makeText(getActivity(), networkAvailable, Toast.LENGTH_LONG).show();
        }
        bindView(savedInstanceState);
        if (pass.getDriverId() == "-1") {
            txt_fare.setEnabled(true);
            //
            car.setVisibility(View.GONE);
            rating.setVisibility(View.GONE);
            fare_rating.setVisibility(View.GONE);
            fianl_fare.setVisibility(View.GONE);
            txt_fare_view.setVisibility(View.GONE);
            txt_city.setVisibility(View.GONE);
            textViewCity.setVisibility(View.GONE);
            //1234
        } else {
            txt_fare.setText(pass.getFare());
            car.setVisibility(View.VISIBLE);
            rating.setVisibility(View.VISIBLE);
            fare_rating.setVisibility(View.VISIBLE);
            txt_city.setVisibility(View.VISIBLE);
            textViewCity.setVisibility(View.VISIBLE);
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckConnection.haveNetworkConnection(getActivity())) {
                    Toast.makeText(getActivity(), networkAvailable, Toast.LENGTH_LONG).show();
                } else {
                    if (pickup_address == null) {
                        Toast.makeText(getActivity(), getString(R.string.invalid_pickupaddress), Toast.LENGTH_SHORT).show();
                    } else if (drop_address == null) {
                        Toast.makeText(getActivity(), getString(R.string.invalid_dropaddress), Toast.LENGTH_SHORT).show();
                    } else if (txt_fare == null) {
                        Toast.makeText(getActivity(), getString(R.string.invalid_fare), Toast.LENGTH_SHORT).show();
                    } else if (origin == null) {
                        Toast.makeText(getActivity(), getString(R.string.invalid_pickuplocation), Toast.LENGTH_SHORT).show();
                    } else if (destination == null) {
                        Toast.makeText(getActivity(), getString(R.string.invalid_droplocation), Toast.LENGTH_SHORT).show();
                    } else {
                        String o = origin.latitude + "," + origin.longitude;
                        String d = destination.latitude + "," + destination.longitude;
                        AddRide(SessionManager.getKEY(), pickup_address, drop_address, o, d, String.valueOf(finalfare), distance, dateandtime_val);
                        Log.d(TAG, "onClick: " + SessionManager.getKEY());
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomeActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void SavePost(String pickup_address, String Drop_address, String date_time_value, String time_value, int ride_id) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(uid.toString());
        databaseRefID.addValueEventListener(new ValueEventListener() {
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

    public void bindView(Bundle savedInstanceState) {
        mapView = (MapView) view.findViewById(R.id.mapview);
        calculateFare = (TextView) view.findViewById(R.id.txt_calfare);
        confirm = (AppCompatButton) view.findViewById(R.id.btn_confirm);
        cancel = (AppCompatButton) view.findViewById(R.id.btn_cancel);
        pickup_location = (TextView) view.findViewById(R.id.txt_pickup);
        drop_location = (TextView) view.findViewById(R.id.txt_drop);
        textView1 = (TextView) view.findViewById(R.id.textView1);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        rating = (TableRow) view.findViewById(R.id.car_rating);
        car = (TableRow) view.findViewById(R.id.car_image);
        fare_rating = (TableRow) view.findViewById(R.id.fare_rating);
        textView3 = (TextView) view.findViewById(R.id.textView3);
        car_name = (TextView) view.findViewById(R.id.car_name);
        textView4 = (TextView) view.findViewById(R.id.textView4);
        num_set = (ElegantNumberButton) view.findViewById(R.id.num_set);
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
        txt_fare_view = (TextView) view.findViewById(R.id.txt_fare_view);
        txt_name = (TextView) view.findViewById(R.id.txt_name);
        txt_Driver_name = (TextView) view.findViewById(R.id.Driver_name);
        txt_city = (TextView) view.findViewById(R.id.txt_city);
        textViewCity = (TextView) view.findViewById(R.id.textViewCity);
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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Typeface book = Typeface.createFromAsset(getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        title.setTypeface(book);
        cancel.setTypeface(book);
        confirm.setTypeface(book);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        setData();
        overrideFonts(getActivity(), view);
        user_id = SessionManager.getUserId();

        if (pass.f == Pass.fragment_type.ADD) {
            DriverAvatar.setVisibility(View.GONE);
            DriverCar.setVisibility(View.GONE);
            txt_Driver_name.setVisibility(View.GONE);
            txt_city.setVisibility(View.GONE);
            textViewCity.setVisibility(View.GONE);
            car_name.setVisibility(View.GONE);
            textView5.setVisibility(View.GONE);
            textView6.setVisibility(View.GONE);
            textView7.setVisibility(View.GONE);
            textView8.setVisibility(View.GONE);
            textView9.setVisibility(View.GONE);
            textView10.setVisibility(View.GONE);
            txt_fare.setVisibility(View.GONE);
            txt_Empty_Seats.setVisibility(View.GONE);
            txt_DriverRate.setVisibility(View.GONE);
            txt_TravelsCount.setVisibility(View.GONE);
            txt_PickupPoint.setVisibility(View.GONE);
            num_set.setEnabled(false);
        }
        if (pass.f == Pass.fragment_type.GET) {
            DriverAvatar.setVisibility(View.VISIBLE);
            DriverCar.setVisibility(View.VISIBLE);
            txt_Driver_name.setVisibility(View.VISIBLE);
            txt_city.setVisibility(View.VISIBLE);
            textViewCity.setVisibility(View.VISIBLE);
            car_name.setVisibility(View.VISIBLE);
            textView5.setVisibility(View.VISIBLE);
            textView6.setVisibility(View.VISIBLE);
            textView7.setVisibility(View.VISIBLE);
            textView8.setVisibility(View.VISIBLE);
            textView9.setVisibility(View.VISIBLE);
            txt_fare.setVisibility(View.VISIBLE);
            txt_Empty_Seats.setVisibility(View.VISIBLE);
            txt_DriverRate.setVisibility(View.VISIBLE);
            txt_TravelsCount.setVisibility(View.VISIBLE);
            txt_PickupPoint.setVisibility(View.VISIBLE);
            num_set.setEnabled(true);
        }
    }

    private void setData() {
        Bundle bundle = getArguments();
        pass = new Pass();
        if (bundle != null) {
            pass = (Pass) bundle.getSerializable("data");
            if (pass != null) {
                pickup = new Place() {
                    @Nullable
                    @Override
                    public String getAddress() {
                        return pass.getFromPlace();
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
                        String[] parts = pass.getFromAddress().split(",");
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
                        return pass.getToPlace();
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
                        String[] parts = pass.getToAddress().split(",");
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
                dateandtime_val = pass.getDate();
                time_val = pass.getTime();
                origin = pickup.getLatLng();
                destination = drop.getLatLng();
                driver_id = pass.getDriverId();
                // by ibrahim
                travel_id = pass.getTravelId();
                car_name.setText(pass.getVehicleName());
                if (!String.valueOf(pass.vehicle_info).isEmpty()) {
                    Glide.with(RequestFragment.this.getActivity()).load(Server.BASE_URL + pass.vehicle_info).apply(new RequestOptions().error(R.drawable.images)).into(DriverCar);
                }
                if (!String.valueOf(pass.avatar).isEmpty()) {
                    Glide.with(RequestFragment.this.getActivity()).load(Server.BASE_URL + pass.avatar).apply(new RequestOptions().error(R.drawable.images)).into(DriverAvatar);
                }
                if (fare != null) {
                    fare = Double.valueOf(pass.getFare());
                }
                drivername = pass.getDriverName();
                pickup_address = pickup.getAddress().toString();
                drop_address = drop.getAddress().toString();
                if (drivername != null) { txt_name.setText(drivername); }
                txt_Driver_name.setText(pass.getDriverName());
                txt_city.setText(pass.getDriverCity());
                txt_Empty_Seats.setText(pass.empty_set);
                txt_DriverRate.setText(pass.DriverRate);
                txt_TravelsCount.setText(pass.Travels_Count);
                txt_PickupPoint.setText(pass.getPickupPoint());
                num_set.setNumber(String.valueOf(pass.NoPassengers));
                pickup_location.setText(pickup_address);
                drop_location.setText(drop_address);
                txt_bag.setText(pass.getAvalibleset());
                txt_smoke.setText(pass.getSmoke());
                dateandtime.setText(pass.getDate());
                TimeVal.setText(pass.getTime());

                if (!String.valueOf(pass.TripPrice).isEmpty() && pass.NoPassengers != 0) {
                    int personal_fare = pass.TripPrice / pass.NoPassengers;
                    txt_fare.setText(String.valueOf(personal_fare));
                    fianl_fare.setText(String.valueOf(pass.TripPrice));
                }
                //
                txt_fare.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        pass.setFare(txt_fare.getText().toString());
                    }
                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
                if (pass.f == Pass.fragment_type.GET) {
                    num_set.setRange(0, Integer.parseInt(pass.empty_set));
                }
                num_set.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                    @Override
                    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                        if (Integer.parseInt(num_set.getNumber().toString()) >= 1 && Integer.parseInt(num_set.getNumber().toString()) <= Integer.parseInt(pass.empty_set)) {
                            if (txt_fare.getText() != null) {
                                calculateDistance(Double.valueOf(newValue));
                            } else {
                                Toast.makeText(getContext(), "Enter fare", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                        }
                    }
                });
                txt_vehiclename.setText(pass.getVehicleName() + "");
                btn_cobo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Validate.isNotNull(cobun_num.getText().toString().trim())) {
                            if (Utility.isOnline(getContext())) {
                                ApplyCode(SessionManager.getKEY(), cobun_num.getText().toString(), pass.getFare());
                                Utility.hideKeyboard(getActivity());
                            } else {
                                MessageUtils.showToast(getContext(), getString(R.string.msg_no_internet));
                            }
                        } else {
                            MessageUtils.showAlert(getContext(), "Please enter promo code first.");
                        }
                    }
                });

                DriverAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!String.valueOf(pass.avatar).isEmpty()) {
                            Intent intent = new Intent(getActivity(), image_view.class);
                            intent.putExtra("imageurl", String.valueOf(pass.avatar));
                            startActivity(intent);
                        }
                    }
                });

                DriverCar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!String.valueOf(pass.vehicle_info).isEmpty()) {
                            Intent intent = new Intent(getActivity(), image_view.class);
                            intent.putExtra("imageurl", String.valueOf(pass.vehicle_info));
                            startActivity(intent);
                        }
                    }
                });

                textView10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!String.valueOf(pass.getTravelId()).isEmpty()) {
                            Bundle bundle = new Bundle();
                            bundle.putString("Post_id", pass.getDriverId());
                            bundle.putString("request_type", "private");
                            PostFragment postfragment = new PostFragment();
                            postfragment.setArguments(bundle);
                            changeFragment(postfragment, "Requests");
                        }
                    }
                });
            }
        }
    }

    public void changeFragment(final Fragment fragment, final String fragmenttag) {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();//.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
                    fragmentTransaction.commit();
                    //fragmentTransaction.addToBackStack(null);
                }
            }, 50);
        } catch (Exception e) {

        }
    }

    private void ApplyCode(String key, String promo_code, String total_fee) {
        String code = cobun_num.getText().toString().trim();
        if (txt_fare.getText() != null) {
            final String total = txt_fare.getText().toString().trim();
        } else {
            Toast.makeText(getContext(), "Enter fare", Toast.LENGTH_SHORT).show();
        }
        boolean apply_code = false;
        final RequestParams params = new RequestParams();
        params.put("promo_code_apply", promo_code);
        params.put("total_cart_total", total_fee);
        Server.setHeader(key);
        Server.post("api/user/promoApplay/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    boolean apply_code = false;
                    float price = Float.parseFloat(pass.getFare()) * Float.parseFloat(num_set.getNumber());
                    float discount = Float.parseFloat(response.getJSONObject("promo_code_apply").getString("promo_percentage"));
                    float total = price - (price) * (discount) / (100);
                    fianl_fare.setText(String.valueOf(total));
                    apply_code = true;
                    if (apply_code == true) {
                        btn_cobo.setEnabled(true);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
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
                if (getActivity() != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (getActivity() != null) {
            if (direction.isOK()) {
                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                myMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
                myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title("Pickup Location").snippet(pickup_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                myMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title("Drop Location").snippet(drop_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));

            } else {
                distanceAlert(direction.getErrorMessage());
                dismiss();
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        distanceAlert(t.getMessage() + "\n" + t.getLocalizedMessage() + "\n");
        dismiss();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        setData();
        new Handler().postDelayed(this::requestDirection, 2000);


    }

    public void requestDirection() {
        Context context = getContext();
        if (context != null) {
            GoogleDirection.withServerKey(getString(R.string.google_android_map_api_key))
                    .from(origin)
                    .to(destination)
                    .transportMode(TransportMode.DRIVING)
                    .execute(this);
            // commeted by ibrahim
            //confirm.setEnabled(false);
            num_set.setEnabled(false);
        }
    }

    private void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/AvenirLTStd_Medium.otf"));
            }
        } catch (Exception e) {
        }
    }

    // pending request by ibrahim
    public void AddRide(String key, String pickup_address, String drop_address, String pickup_location, String drop_location, String amount, String distance, String time) {
        final RequestParams params = new RequestParams();
        params.put("driver_id", driver_id);
        //by ibrahim
        params.put("travel_id", travel_id);
        params.put("user_id", user_id);
        params.put("pickup_address", pickup_address);
        params.put("drop_address", drop_address);
        params.put("date", pass.getDate());
        params.put("time", pass.getTime());
        log.i("tag", "success by ibrahim");
        log.i("tag", pass.getDate());
        //commited by ibrahim
        //params.put("time",pass.getDate());
        params.put("pickup_location", pickup_location);
        params.put("drop_location", drop_location);
        params.put("Ride_smoked", pass.getSmoke());
        params.put("amount", fianl_fare.getText());
        params.put("distance", distance);
        params.put("status", pass.getStatus());
        params.put("booked_set", num_set.getNumber());
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
                            if (pass.getcheck() == "1") {
                                SavePost(pickup_address, drop_address, dateandtime_val, time_val, ride_id);
                            }
                            addRideFirebase(ride_id, pass.getTravel_status(),pass.getStatus(), pass.getPayment_status(), pass.getPayment_mode());
                            addNotificationFirebase(ride_id);
                        }
                        startActivity(new Intent(getContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    } else {
//                        Toast.makeText(getActivity(), tryAgain, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
//                    Toast.makeText(getActivity(), tryAgain, Toast.LENGTH_LONG).show();
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

    public void addRideFirebase(int ride_id_param, String travel_status, String ride_status, String payment_status, String payment_mode) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("rides").child(String.valueOf(ride_id_param));
        Map<String, Object> rideObject = new HashMap<>();

        rideObject.put("ride_status", "PENDING");
        rideObject.put("travel_status", travel_status);
        rideObject.put("payment_status", "");
        rideObject.put("payment_mode", "");
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        databaseRef.setValue(rideObject);
    }

    public void addNotificationFirebase(int ride_id_param) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Notifications").child(pass.getDriverId()).push();
        Map<String, Object> rideObject = new HashMap<>();
        rideObject.put("ride_id", String.valueOf(ride_id_param));
        rideObject.put("text", "Ride Updated");
        rideObject.put("readStatus", "0");
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        rideObject.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseRef.setValue(rideObject);
    }

    public void calculateDistance(Double aDouble) {
        if (aDouble == null) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        } else {
            double cost;
            if (txt_fare.getText().toString() != "" && pass.getFare() != null) {
                if (pass.getFare().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Enter Fare", Toast.LENGTH_SHORT).show();
                } else {
                    cost = Double.valueOf(pass.getFare()) * Double.valueOf(aDouble);
                    finalfare = Double.valueOf(cost);
                    fianl_fare.setText(finalfare + " ");
                    dismiss();
                    confirm.setEnabled(true);
                }
            } else {
                Toast.makeText(getContext(), "enter the price", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void distanceAlert(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getString(R.string.INVALID_DISTANCE));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(true);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        alertDialog.setIcon(drawable);
        alertDialog.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.cancel();
            }
        });
        alert = alertDialog.create();
        alert.show();
    }

    private void dismiss() {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    @Override
    public boolean onBackPressed() {
        listProviderFragment fragobj = new listProviderFragment();
        Toast.makeText(fragobj, "back pressed", Toast.LENGTH_SHORT).show();
        this.startActivity(new Intent(getContext(), listProviderFragment.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        return false;
    }

    @Override
    public int getBackPriority() {
        return NORMAL_BACK_PRIORITY;
    }
}