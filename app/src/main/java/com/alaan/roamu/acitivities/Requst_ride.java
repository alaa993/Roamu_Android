package com.alaan.roamu.acitivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamu.PostActivity;
import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.custom.CheckConnection;
import com.alaan.roamu.fragement.RequestFragment;
import com.alaan.roamu.pojo.Pass;
import com.alaan.roamu.pojo.PendingRequestPojo;
import com.alaan.roamu.session.MessageUtils;
import com.alaan.roamu.session.SessionManager;
import com.alaan.roamu.session.Utility;
import com.alaan.roamu.session.Validate;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;
import static gun0912.tedbottompicker.TedBottomPicker.TAG;

// not used by ibrahim probably
public class Requst_ride extends AppCompatActivity {

    AppCompatButton confirm, cancel;
    TextView pickup_location, drop_location;
    Double finalfare;

    private Double fare;
    AlertDialog alert;
    private String networkAvailable;
    private String tryAgain;
    private String directionRequest;
    TextView textView3, textView4, textView5, textView6, textView7, textView8, textView9, textView10, txt_fare_view, title, dateandtime, TimeVal, car_name;
    TextView txt_Driver_name, txt_Empty_Seats, txt_DriverRate, txt_TravelsCount, txt_PickupPoint, txt_fare, fianl_fare;
    private ImageView DriverAvatar;
    private ImageView DriverCar;
    EditText cobun_num;
    ElegantNumberButton num_set;
    String driver_id;
    String travel_id;
    private String user_id;
    private String pickup_address, drop_address, dateandtime_val, time_val;
    //    String distance;
    private String drivername = "";
    SwipeRefreshLayout swipeRefreshLayout;
    Pass pass;
    TableRow rating, car;//, fare_rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requst_ride);

        bindView(savedInstanceState);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckConnection.haveNetworkConnection(Requst_ride.this)) {
                    Toast.makeText(Requst_ride.this, networkAvailable, Toast.LENGTH_LONG).show();
                } else {
                    if (pickup_address == null) {
                        Toast.makeText(Requst_ride.this, getString(R.string.invalid_pickupaddress), Toast.LENGTH_SHORT).show();
                    } else if (drop_address == null) {
                        Toast.makeText(Requst_ride.this, getString(R.string.invalid_dropaddress), Toast.LENGTH_SHORT).show();
                    } else if (txt_fare == null) {
                        Toast.makeText(Requst_ride.this, getString(R.string.invalid_fare), Toast.LENGTH_SHORT).show();
                    } else {
                        AddRide(SessionManager.getKEY(), pickup_address, drop_address, "", "", String.valueOf(finalfare), "", dateandtime_val);
                        if (pass.getcheck() == "1")
                            SavePost(pickup_address, drop_address, dateandtime_val, time_val);

                        Log.d(TAG, "onClick: " + SessionManager.getKEY());
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Requst_ride.this, HomeActivity.class));
            }
        });

    }

    public void SavePost(String pickup_address, String Drop_address, String date_time_value, String time_value) {
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

    public void bindView(Bundle savedInstanceState) {

        confirm = (AppCompatButton) findViewById(R.id.btn_confirm1);//
        cancel = (AppCompatButton) findViewById(R.id.btn_cancel1);//
        pickup_location = (TextView) findViewById(R.id.txt_pickup1);//
        drop_location = (TextView) findViewById(R.id.txt_drop1);//
        rating = (TableRow) findViewById(R.id.car_rating1);//
        car = (TableRow) findViewById(R.id.car_image1);//
        textView3 = (TextView) findViewById(R.id.textView3_1);//
        car_name = (TextView) findViewById(R.id.car_name1);//
        textView4 = (TextView) findViewById(R.id.textView4_1);//

        num_set = (ElegantNumberButton) findViewById(R.id.num_set1);//
        txt_fare = (TextView) findViewById(R.id.txt_fare1);//
        fianl_fare = (TextView) findViewById(R.id.fianl_fare1);//
        dateandtime = (TextView) findViewById(R.id.dateTimeVal1);//
        TimeVal = (TextView) findViewById(R.id.TimeVal1);//
        textView8 = (TextView) findViewById(R.id.textView8_1);//
        textView5 = (TextView) findViewById(R.id.textView5_1);//
        textView6 = (TextView) findViewById(R.id.textView6_1);//
        textView7 = (TextView) findViewById(R.id.textView7_1);//
        textView9 = (TextView) findViewById(R.id.textView9_1);//
        textView10 = (TextView) findViewById(R.id.textView10_1);//
        txt_fare_view = (TextView) findViewById(R.id.txt_fare_view1);//
        txt_Driver_name = (TextView) findViewById(R.id.Driver_name1);//
        txt_Empty_Seats = (TextView) findViewById(R.id.txt_Empty_Seats1);//
        txt_DriverRate = (TextView) findViewById(R.id.txt_DriverRate1);//
        txt_TravelsCount = (TextView) findViewById(R.id.txt_TravelsCount1);//
        txt_PickupPoint = (TextView) findViewById(R.id.txt_PickupPoint1);//

        DriverAvatar = (ImageView) findViewById(R.id.DriverImage1);//
        DriverCar = (ImageView) findViewById(R.id.carImage1);//


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh1);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        setData();
        user_id = SessionManager.getUserId();

    }

    private void setData() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        pass = new Pass();
        if (bundle != null) {
            pass = (Pass) bundle.getSerializable("data");
            Log.i("ibrahim1", "-----------------------------");
            Log.i("ibrahim was here", String.valueOf(pass.getTravelId()));
            if (pass != null) {
                dateandtime_val = pass.getDate();
                time_val = pass.getTime();

                driver_id = pass.getDriverId();
                // by ibrahim
                travel_id = pass.getTravelId();
                log.i("tag", "success by ibrahim requestFragment");
                log.i("tag", pass.getVehicleName());

                car_name.setText(pass.getVehicleName());

                if (!String.valueOf(pass.vehicle_info).isEmpty()) {
                    Glide.with(Requst_ride.this).load(Server.BASE_URL + pass.vehicle_info).apply(new RequestOptions().error(R.drawable.images)).into(DriverCar);
                }
                if (!String.valueOf(pass.avatar).isEmpty()) {
                    Glide.with(Requst_ride.this).load(Server.BASE_URL + pass.avatar).apply(new RequestOptions().error(R.drawable.images)).into(DriverAvatar);
                }

                if (fare != null) {
                    fare = Double.valueOf(pass.getFare());
                }
                drivername = pass.getDriverName();
                pickup_address = pass.getFromAddress();
                drop_address = pass.getToAddress();

                txt_Driver_name.setText(pass.getDriverName());
                txt_Empty_Seats.setText(pass.empty_set);
                txt_DriverRate.setText(pass.DriverRate);
                txt_TravelsCount.setText(pass.Travels_Count);
                txt_PickupPoint.setText(pass.getPickupPoint());
                num_set.setNumber(String.valueOf(pass.NoPassengers));

                pickup_location.setText(pickup_address);
                drop_location.setText(drop_address);

                dateandtime.setText(pass.getDate());
                TimeVal.setText(pass.getTime());

                if (!String.valueOf(pass.TripPrice).isEmpty() && pass.NoPassengers != 0) {
                    int personal_fare = pass.TripPrice / pass.NoPassengers;
                    txt_fare.setText(String.valueOf(personal_fare));
                    fianl_fare.setText(String.valueOf(pass.TripPrice));
                    log.i("tag", "success by ibrahim");
                    log.i("tag", String.valueOf(pass.TripPrice));
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
//                num_set.setRange(0, Integer.parseInt(pass.empty_set));
                num_set.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                    @Override
                    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                        Log.i("ibrahim was here", pass.empty_set);
                        Log.i("ibrahim was here", "");
                    }
                });

                DriverAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!String.valueOf(pass.avatar).isEmpty()) {
                            Intent intent = new Intent(Requst_ride.this, image_view.class);
                            intent.putExtra("imageurl", String.valueOf(pass.avatar));
                            startActivity(intent);
                        }
                    }
                });

                DriverCar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!String.valueOf(pass.vehicle_info).isEmpty()) {
                            Intent intent = new Intent(Requst_ride.this, image_view.class);
                            intent.putExtra("imageurl", String.valueOf(pass.vehicle_info));
                            startActivity(intent);
                        }
                    }
                });

                textView10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!String.valueOf(pass.getTravelId()).isEmpty()) {
                            //getting the selected artist
                            //creating an intent
                            Intent intent = new Intent(Requst_ride.this, PostActivity.class);
                            Log.i("ibrahim was here", pass.getDriverId());
                            //putting artist name and id to intent
                            intent.putExtra("Post_id", pass.getDriverId());
                            intent.putExtra("request_type", "private");
//                            intent.putExtra(ARTIST_NAME, artist.getArtistName());

                            //starting the activity with intent
                            startActivity(intent);
                        }
                    }
                });

            }
        }
    }

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
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        Toast.makeText(Requst_ride.this, getString(R.string.ride_has_been_requested), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Requst_ride.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    } else {
                        Toast.makeText(Requst_ride.this, tryAgain, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(Requst_ride.this, tryAgain, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(Requst_ride.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (Requst_ride.this != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });
    }
}