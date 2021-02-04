package com.alaan.roamu.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.fragement.AcceptedDetailFragment;
import com.alaan.roamu.pojo.Notification;
import com.alaan.roamu.pojo.Pass;
import com.alaan.roamu.pojo.PendingRequestPojo;
import com.alaan.roamu.pojo.Post;
import com.alaan.roamu.pojo.PostList;
import com.alaan.roamu.session.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class NotificationAdapter extends ArrayAdapter<Notification> {
    private FragmentActivity context;
    List<Notification> notifications;
    Pass pass;
    DatabaseReference databaseComments;

    public NotificationAdapter(FragmentActivity context, List<Notification> notifications) {
        super(context, R.layout.notification_item, notifications);
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View listViewItem = inflater.inflate(R.layout.notification_item, null, true);
        //listViewItem.setBackgroundResource(R.drawable.listview_item_border);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.Notificatoin_textViewName);
        TextView textViewText = (TextView) listViewItem.findViewById(R.id.Notificatoin_textViewText);
        ImageView PostAvatar = (ImageView) listViewItem.findViewById(R.id.Notificatoin_image);
        Notification notification = notifications.get(position);

        Log.i("ibrahim_1",notification.toString());
        textViewText.setText(notification.text);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(notification.uid);
        databaseRefID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                String UserName = dataSnapshot.child("username").getValue(String.class);

                if (photoURL != null) {
                    Glide.with(NotificationAdapter.this.getContext()).load(photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
                }
                textViewName.setText(UserName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
        listViewItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GetRides(String.valueOf(notification.ride_id), notification.id);
            }
        });

        return listViewItem;
    }

    public void updateNotificationFirebase(String ride_id, String user_id, String notification_id) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Notifications").child(user_id).child(notification_id).child("readStatus");
        databaseRef.setValue("1");
    }

    private void GetRides(String ride_id, String notification_id) {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.GET_SPECIFIC_RIDE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("success", response.toString());
                try {
                    Gson gson = new GsonBuilder().create();
                    List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                    }.getType());
                    updateNotificationFirebase(ride_id, list.get(0).getUser_id(), notification_id); // my id is user id
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", list.get(0));
                    AcceptedDetailFragment detailFragment = new AcceptedDetailFragment();
                    detailFragment.setArguments(bundle);
                    ((HomeActivity) getContext()).changeFragment(detailFragment, "Passenger Information");
                } catch (JSONException e) {
                    Log.e("Get Data", e.getMessage());
                }
            }
        });
    }
}