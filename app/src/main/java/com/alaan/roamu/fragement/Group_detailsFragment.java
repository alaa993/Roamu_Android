package com.alaan.roamu.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.adapter.group_details_member_adapter;
import com.alaan.roamu.pojo.Group_List_membar;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Group_detailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Group_detailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    View rootView;
    RecyclerView recyclerView;

    public Group_detailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Group_detailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Group_detailsFragment newInstance(String param1, String param2) {
        Group_detailsFragment fragment = new Group_detailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_group_details, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.my_groups));
        recyclerView = (RecyclerView) rootView.findViewById(R.id.Group_details_list);
        //log.i("ibrahim", "inside view");
        getGroupList(Integer.parseInt(SessionManager.getUserId()));
        //log.i("ibrahim", "inside view1");
        return rootView;
    }

    private void getGroupList(int driver_id) {
        //log.i("ibrahim", "inside getGroupList");
        //log.i("ibrahim", String.valueOf(driver_id));

        final RequestParams params = new RequestParams();
        params.put("user_id", driver_id);
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.GET_MyGroupLIST, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                super.onSuccess(statusCode, headers, response);
                try {
                    Gson gson = new GsonBuilder().create();
                    List<Group_List_membar> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<Group_List_membar>>() {
                    }.getType());
//                    //log.i("ibrahim list reply", response.getJSONArray("data").toString());
//                    //log.i("ibrahim list reply", list.get(0).group_name);
//                    //log.i("ibrahim list reply", list.get(0).name);
//                    //log.i("ibrahim list reply", String.valueOf(list.get(0).group_id));

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    group_details_member_adapter group_details_member_adapter = new group_details_member_adapter(list);
                    recyclerView.setAdapter(group_details_member_adapter);
                    group_details_member_adapter.notifyDataSetChanged();

                } catch (NullPointerException e) {
                    System.err.println("Null pointer exception");
                }catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getContext(), "" + errorResponse, Toast.LENGTH_SHORT).show();
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