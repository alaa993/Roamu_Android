package com.alaan.roamu.acitivities;

import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.adapter.search_d_adapter;
import com.alaan.roamu.fragement.RequestFragment;
import com.alaan.roamu.pojo.NearbyData;
import com.alaan.roamu.session.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class List_provider extends AppCompatActivity {
    RecyclerView recyclerView;
    private String cost;
    private String unit;
    Boolean flag = false;
    private int smoke_int = 0;

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_provider);
        Bundle bundle = getIntent().getExtras();
        recyclerView = (RecyclerView)findViewById(R.id.search_drivers);

        if (bundle != null) {
           // Toast.makeText(this, "" + bundle.getString("cureentlatitude"), Toast.LENGTH_SHORT).show();


            if (bundle.getString("cureentlatitude") != null && !bundle.getString("cureentlatitude").equals(0.0) && bundle.getString("currentLongitude") != null && !bundle.getString("currentLongitude").equals(0.0)) {

                String currentLatitude = bundle.getString("cureentlatitude");
                String currentLongitude = bundle.getString("currentLongitude");
                String search_pich_location = bundle.getString("search_pich_location");
                String search_drop_location = bundle.getString("search_drop_location");
                String smoke_value = bundle.getString("smoke_value");
                String date_time_value = bundle.getString("date_time_value");
                String passanger_value = bundle.getString("passanger_value");
                String bag_value = bundle.getString("bag_value");
                NeaBy(String.valueOf(currentLatitude), String.valueOf(currentLongitude), search_pich_location, search_drop_location, smoke_value, date_time_value, passanger_value, bag_value);


            }

            log.d("tag",bundle.getString("currentLongitude"));
        }
    }
    public void NeaBy(String latitude, String longitude , String pick,String drop, String Smoke , String date,String passn,String bag) {

        flag = true;
        RequestParams params = new RequestParams();
        params.put("pickup_address",pick);
        params.put("drop_address",drop);
        params.put("lat", latitude);
        params.put("long", longitude);
        params.put("travel_date",date);

        if (Smoke == "No") smoke_int = 0;
        else if (Smoke == "Yes") smoke_int = 1;
        params.put("smoked",smoke_int);

        params.put("bag",bag);
        params.put("booked_set",passn);
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
                        log.i("tag","success by ibrahim");
                        List<NearbyData> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<NearbyData>>() {

                        }.getType());
                        log.i("tag","inside by ibrahim");
                        log.i("tag", response.getJSONArray("data").toString());

                        recyclerView = (RecyclerView)findViewById(R.id.search_drivers);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(linearLayoutManager);

                        search_d_adapter search_d_adapter = new search_d_adapter(list);
                        recyclerView.setAdapter(search_d_adapter);
                        search_d_adapter.notifyDataSetChanged();
//                        cost = response.getJSONObject("fair").getString("cost");
//                        unit = response.getJSONObject("fair").getString("unit");

//                        SessionManager.setUnit(unit);
//                        SessionManager.setCost(cost);
                        // Inflate the layout for this fragment
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(getApplication(), getString(R.string.try_again), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getApplication() != null) {
                }
            }
        });
    }
    public void changeFragment(final Fragment fragment, final String fragmenttag) {

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                   // recyclerView.setVisibility(View.GONE);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();//.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame_list, fragment, fragmenttag);
                    fragmentTransaction.commit();
                  //  fragmentTransaction.addToBackStack(null);
                }
            }, 50);
        } catch (Exception e) {

        }

    }
    public void showframe(int stauts) {

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(stauts == 0) {
                      recyclerView.setVisibility(View.GONE);
                    }else if(stauts == 1){
                        recyclerView.setVisibility(View.VISIBLE);
}
                }
            }, 50);
        } catch (Exception e) {

        }

    }
    @Override
    public void onBackPressed() {
        recyclerView.setVisibility(View.VISIBLE);
            super.onBackPressed();

    }
    public void onResume() {
        super.onResume();

        // On resume, make everything in activity_main invisible except this fragment.
    }

}