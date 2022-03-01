package com.alaan.roamu.fragement;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.adapter.search_d_adapter;
import com.alaan.roamu.pojo.NearbyData;
import com.alaan.roamu.session.SessionManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;

import net.skoumal.fragmentback.BackFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class ListProviderFragment extends FragmentManagePermission implements BackFragment {
    RecyclerView recyclerView;
    TextView txt_error;
    Boolean flag = false;
    private int smoke_int = 0;

    private View rootView;

    public ListProviderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list_providers, container, false);
        Bundle bundle = getArguments();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.search_drivers);
        txt_error = (TextView) rootView.findViewById(R.id.MyAR_txt_error);

        if (bundle != null) {
            String currentLatitude = bundle.getString("cureentlatitude");
            String currentLongitude = bundle.getString("currentLongitude");
            String search_pich_location = bundle.getString("search_pich_location");
            String search_drop_location = bundle.getString("search_drop_location");
            String smoke_value = bundle.getString("smoke_value");
            String date_time_value = bundle.getString("date_time_value");
            String passanger_value = bundle.getString("passanger_value");
            String bag_value = bundle.getString("bag_value");
            String car_type = bundle.getString("car_type");
            NeaBy(String.valueOf(currentLatitude), String.valueOf(currentLongitude), search_pich_location, search_drop_location, smoke_value, date_time_value, passanger_value, bag_value, car_type);
        }
        return rootView;
    }

    public void NeaBy(String latitude, String longitude, String pick, String drop, String Smoke, String date, String passn, String bag, String car_type) {
        //log.i("ibrahim", "NeaBy");

        flag = true;
        RequestParams params = new RequestParams();
        params.put("pickup_address", pick);
        params.put("drop_address", drop);
        params.put("lat", latitude);
        params.put("long", longitude);
        params.put("travel_date", date);
        params.put("car_type", car_type);

        if (Smoke == "No") smoke_int = 0;
        else if (Smoke == "Yes") smoke_int = 1;
        params.put("smoked", smoke_int);

        params.put("bag", bag);
        params.put("booked_set", passn);
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
                        //log.i("ibrahim", "success by ibrahim");
                        //log.i("ibrahim", response.toString());

                        List<NearbyData> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<NearbyData>>() {

                        }.getType());

//                        if (response.has("data") && response.getJSONArray("data").length() == 0) {
//                            txt_error.setVisibility(View.VISIBLE);
//                            recyclerView = (RecyclerView) findViewById(R.id.search_drivers);
//                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false);
//                            recyclerView.setLayoutManager(linearLayoutManager);
//                            search_d_adapter search_d_adapter = new search_d_adapter(list);
//                            recyclerView.setAdapter(search_d_adapter);
//                            search_d_adapter.notifyDataSetChanged();
//                        } else {
//                            txt_error.setVisibility(View.GONE);
//                            recyclerView = (RecyclerView) findViewById(R.id.search_drivers);
//                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false);
//                            recyclerView.setLayoutManager(linearLayoutManager);
//                            search_d_adapter search_d_adapter = new search_d_adapter(list);
//                            recyclerView.setAdapter(search_d_adapter);
//                            search_d_adapter.notifyDataSetChanged();
//                        }


                        recyclerView = (RecyclerView) rootView.findViewById(R.id.search_drivers);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(linearLayoutManager);

                        search_d_adapter search_d_adapter = new search_d_adapter(list);
                        recyclerView.setAdapter(search_d_adapter);
                        search_d_adapter.notifyDataSetChanged();
                    }
                } catch (NullPointerException e) {
                    System.err.println("Null pointer exception");
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
                }
            }
        });
    }

    public void showframe(int stauts) {

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (stauts == 0) {
                        recyclerView.setVisibility(View.GONE);
                    } else if (stauts == 1) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }, 50);
        } catch (Exception e) {

        }

    }

    @Override
    public boolean onBackPressed() {
        recyclerView.setVisibility(View.GONE);
//        ListProviderFragment fragobj = new ListProviderFragment();
//        Toast.makeText(fragobj, "back pressed", Toast.LENGTH_SHORT).show();
//        this.startActivity(new Intent(getContext(), ListProviderFragment.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        return false;
    }

    @Override
    public int getBackPriority() {
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();

        // On resume, make everything in activity_main invisible except this fragment.
    }
}