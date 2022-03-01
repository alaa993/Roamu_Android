package com.alaan.roamu.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.adapter.Group_membar_Adapter;
import com.alaan.roamu.pojo.Group_List_membar;
import com.alaan.roamu.pojo.Group_membar;
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

public class Group_details_MembersFragment extends Fragment {

    View rootView;
    Group_List_membar pojo;
    RecyclerView recyclerView;
    TextView group_name_txt,admin_name_txt;

    public Group_details_MembersFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Group_details_MembersFragment newInstance(String param1, String param2) {
        Group_details_MembersFragment fragment = new Group_details_MembersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_group_details__members, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.my_groups));
        group_name_txt = (TextView) rootView.findViewById(R.id.group_name_txt);
        admin_name_txt = (TextView) rootView.findViewById(R.id.admin_name_txt);
        recyclerView = (RecyclerView)  rootView.findViewById(R.id.member_list);

        Bundle bundle = getArguments();

        if (bundle != null) {
            pojo = (Group_List_membar) bundle.getSerializable("data");
            if (pojo != null) {
                group_name_txt.setText(pojo.group_name);
                admin_name_txt.setText(pojo.admin_name);
                getMemberList(Integer.parseInt(SessionManager.getUserId()));

            }
        }
        return rootView;
    }

    private void getMemberList(int gruop_id) {
        final RequestParams params = new RequestParams();
        params.put("admin_id", gruop_id);
        //log.i("ibrahim group id", String.valueOf(gruop_id));
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.GET_MEBLIST, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Gson gson = new GsonBuilder().create();
                    List<Group_membar> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<Group_membar>>() {
                    }.getType());
                    //log.i("ibrahim list reply", response.getJSONArray("data").toString());
//                        RecyclerView recyclerView = (RecyclerView)  rootView.findViewById(R.id.member_list);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    Group_membar_Adapter group_membar_adapter = new Group_membar_Adapter(list);
                    recyclerView.setAdapter(group_membar_adapter);
                    group_membar_adapter.notifyDataSetChanged();
                } catch (NullPointerException e) {
                    System.err.println("Null pointer exception");
                }catch (JSONException e) {
                    //Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
//                Toast.makeText(getContext(), "" + errorResponse, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
                }
            }
        });


    }
}
