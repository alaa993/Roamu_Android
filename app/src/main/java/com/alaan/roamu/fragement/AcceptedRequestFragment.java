package com.alaan.roamu.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.adapter.AcceptedRequestAdapter;
import com.alaan.roamu.custom.CheckConnection;
import com.alaan.roamu.custom.SetCustomFont;
import com.alaan.roamu.pojo.PendingRequestPojo;
import com.alaan.roamu.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.skoumal.fragmentback.BackFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AcceptedRequestFragment extends Fragment implements BackFragment, AdapterView.OnItemSelectedListener {
    private View view;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    String userid = "";
    String key = "";
    String[] status_arr;

    String[] status_val_arr = {"All","PENDING", "ACCEPTED", "COMPLETED", "CANCELLED", "REQUESTED"};
    TextView txt_error;
    String status;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.accepted_request_fragment, container, false);
        bindView();
        return view;
    }

    public void bindView() {
        status_arr = new String[]{getString(R.string.All_travel),
                getString(R.string.pending_request), getString(R.string.accepted_request),
                getString(R.string.completed_request), getString(R.string.cancelled_request),
                getString(R.string.requested_request)};
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        txt_error = (TextView) view.findViewById(R.id.txt_error);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        Spinner droplist = (Spinner) view.findViewById(R.id.arf_simpleSpinner);
        droplist.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        ArrayAdapter data = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, status_arr);

        droplist.setAdapter(data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        userid = SessionManager.getUserId();
        key = SessionManager.getKEY();

        Bundle bundle = getArguments();
        if (bundle != null) {
            status = bundle.getString("status");
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (CheckConnection.haveNetworkConnection(getActivity())) {
            ((HomeActivity) getActivity()).fontToTitleBar(setTitle(status_val_arr[i]));
            getAcceptedRequest(userid, status_val_arr[i], key);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void getAcceptedRequest(String id, String status, String key) {
        final RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("status", status);
        Server.setHeader(key);
        Server.get("api/user/rides/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Gson gson = new GsonBuilder().create();
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                        }.getType());
                        if (response.has("data") && response.getJSONArray("data").length() == 0) {
                            txt_error.setVisibility(View.VISIBLE);
                            AcceptedRequestAdapter acceptedRequestAdapter = new AcceptedRequestAdapter(list);
                            recyclerView.setAdapter(acceptedRequestAdapter);
                            acceptedRequestAdapter.notifyDataSetChanged();

                        } else {
                            txt_error.setVisibility(View.GONE);
                            AcceptedRequestAdapter acceptedRequestAdapter = new AcceptedRequestAdapter(list);
                            recyclerView.setAdapter(acceptedRequestAdapter);
                            acceptedRequestAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
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

    private String setTitle(String s) {
        String title = null;
        switch (s) {
            case "ACCEPTED":
                title = getString(R.string.accepted_request);
                break;
            case "PENDING":
                title = getString(R.string.pending_request);
                break;
            case "CANCELLED":
                title = getString(R.string.cancelled_request);
                break;
            case "COMPLETED":
                title = getString(R.string.completed_request);
                break;
            case "REQUESTED":
                title = getString(R.string.requested_request);
                break;
            case "All":
                title = getString(R.string.All_travel);
                break;

        }
        return title;
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
