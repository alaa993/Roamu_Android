package com.alaan.roamu.fragement;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alaan.roamu.R;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.adapter.NotificationAdapter;
import com.alaan.roamu.custom.SetCustomFont;
import com.alaan.roamu.pojo.Notification;
import com.alaan.roamu.pojo.Post;
import com.alaan.roamu.pojo.PostList;
import com.alaan.roamu.session.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    View view;
    ListView listViewNotificatoins;
    private FirebaseUser fUser;


    //a list to store all the artist from firebase database
    List<Notification> notifications;
    DatabaseReference databasePosts;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
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
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        notifications = new ArrayList<>();
        Log.i("ibrahim_uid", String.valueOf(SessionManager.getUser()));
        try {
            databasePosts = FirebaseDatabase.getInstance().getReference("Notifications").child(SessionManager.getUser().getUser_id());
        } catch (Exception e) {
            Log.i("ibrahim_e", e.getMessage());
        }
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.notifications));
        view.setBackgroundColor(Color.WHITE);
        BindView();

        return view;
    }

    public void BindView() {
        listViewNotificatoins = (ListView) view.findViewById(R.id.listViewNotifications);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener
        databasePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                notifications.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Log.i("ibrahim_notificatoni", postSnapshot.toString());
                    Notification notification = postSnapshot.getValue(Notification.class);
                    notification.id = postSnapshot.getKey();
                    notifications.add(notification);
                }
                Collections.reverse(notifications);
                if (!notifications.isEmpty()) {
                    //creating adapter
                    try {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                NotificationAdapter notificationAdapter = new NotificationAdapter(getActivity(), notifications);
                                //attaching adapter to the listview
                                notificationAdapter.notifyDataSetChanged();
                                listViewNotificatoins.setAdapter(notificationAdapter);
                            }
                        }, 50);
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}