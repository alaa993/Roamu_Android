package com.alaan.roamu.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamu.R;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.custom.CheckConnection;
import com.alaan.roamu.custom.SetCustomFont;
import com.alaan.roamu.session.SessionManager;

import net.skoumal.fragmentback.BackFragment;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Search_fargment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search_fargment extends Fragment implements BackFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View view;
    RecyclerView recyclerView;
    String key = "";

    public Search_fargment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Search_fargment.
     */
    // TODO: Rename and change types and number of parameters
    public static Search_fargment newInstance(String param1, String param2) {
        Search_fargment fragment = new Search_fargment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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

        view =  inflater.inflate(R.layout.fragment_search_fargment, container, false);
        bindView();

        return  view;

    }
    public void bindView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.search_drivers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        SetCustomFont setCustomFont = new SetCustomFont();
        setCustomFont.overrideFonts(getActivity(), view);
        key = SessionManager.getKEY();



    }

    @Override
    public boolean onBackPressed() {

        HomeFragment fragobj = new HomeFragment();
        ((HomeActivity) getActivity()).changeFragment(fragobj, getString(R.string.request_ride));
        Log.d("app fargment", "onBackPressed: searchprovider ");

        return false;
    }

    @Override
    public int getBackPriority() {
        return NORMAL_BACK_PRIORITY;
    }
}
