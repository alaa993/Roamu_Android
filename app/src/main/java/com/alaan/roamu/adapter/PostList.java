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
import com.alaan.roamu.fragement.RequestFragment;
import com.alaan.roamu.pojo.NearbyData;
import com.alaan.roamu.pojo.Pass;
import com.alaan.roamu.pojo.Post;
import com.alaan.roamu.session.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PostList extends ArrayAdapter<Post> {
    private FragmentActivity context;
    List<Post> posts;
    Pass pass;
    DatabaseReference databaseComments;


    public PostList(FragmentActivity context, List<Post> posts) {
        super(context, R.layout.layout_artist_list, posts);
        this.context = context;
        this.posts = posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View listViewItem = inflater.inflate(R.layout.layout_artist_list, null, true);
        //listViewItem.setBackgroundResource(R.drawable.listview_item_border);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewText = (TextView) listViewItem.findViewById(R.id.textViewText);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.textViewDate);
        TextView textViewCommentsNo = (TextView) listViewItem.findViewById(R.id.textViewCommentsNo);
        ImageView PostAvatar = (ImageView) listViewItem.findViewById(R.id.image);
        TextView TripDetail = (TextView) listViewItem.findViewById(R.id.TripDetail);
        Post post = posts.get(position);
        String type = post.type;
        if (type.equals("0")) {
            listViewItem.setBackgroundColor(Color.parseColor("#f5fafe"));
        } else if (type.equals("1")) {
            listViewItem.setBackgroundColor(Color.WHITE);
        }
        if (post.author.username != null && post.text != null) {
            textViewName.setText(post.author.username);
            textViewText.setText(post.text);
        }
        //type = 0 => driver
        //type = 1 => user
        if (post.type.contains("0") && post.travel_id > 0) {
            TripDetail.setVisibility(View.VISIBLE);
        } else {
            TripDetail.setVisibility(View.GONE);
        }
        databaseComments = FirebaseDatabase.getInstance().getReference("posts").child(post.id).child("Comments");
        databaseComments.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewCommentsNo.setText(context.getResources().getString(R.string.CommentsNoSt) + " (" + dataSnapshot.getChildrenCount() + ")");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (post.timestamp != null) {
            Date date = new Date(post.timestamp);
            SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String stringDate = DateFor.format(date);
            textViewDate.setText(stringDate.toString());
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(post.author.uid);
        databaseRefID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                if (photoURL != null) {
                    Glide.with(PostList.this.getContext()).load(photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });

        TripDetail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("success", String.valueOf(post.travel_id));
                NeaBy(String.valueOf(post.travel_id));
            }
        });

        return listViewItem;
    }

    public void NeaBy(String travel_id) {
        RequestParams params = new RequestParams();
        params.put("travel_id", travel_id);
        Log.i("success", String.valueOf(travel_id));
        Server.setHeader(SessionManager.getKEY());
        Server.get("api/user/travels3/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        Log.e("success", response.toString());
                        Gson gson = new GsonBuilder().create();
                        List<NearbyData> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<NearbyData>>() {
                        }.getType());
                        if (list.size() > 0) {
                            final NearbyData nearbyData = list.get(0);
                            pass = new Pass();
                            pass.f = Pass.fragment_type.GET;
                            pass.setFromPlace(nearbyData.getPickup_address());
                            pass.setToPlace(nearbyData.getDrop_address());
                            pass.setFromAddress(nearbyData.getPickup_location());
                            pass.setToAddress(nearbyData.getDrop_location());
                            pass.setPickupPoint(nearbyData.getPickup_point());
                            pass.setDriverId(nearbyData.getUser_id());
                            //by ibrahim
                            pass.setTravelId(nearbyData.getTravel_id());
                            pass.setFare(nearbyData.getAmount());
                            pass.setDriverName(nearbyData.getName());
                            pass.setSmoke(nearbyData.getsmoked());
                            pass.setDate(nearbyData.getTravel_date());
                            pass.setTime(nearbyData.getTravel_time());
                            pass.setAvalibleset(nearbyData.getBooked_set());
                            pass.avatar = nearbyData.avatar;
                            pass.vehicle_info = nearbyData.vehicle_info;
                            pass.setVehicleName(nearbyData.getVehicle_info());
                            pass.empty_set = nearbyData.empty_set;
                            pass.DriverRate = nearbyData.DriverRate;
                            pass.Travels_Count = nearbyData.Travels_Count;
                            pass.pickup_location = nearbyData.getPickup_location();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("data", pass);
                            RequestFragment fragobj = new RequestFragment();
                            fragobj.setArguments(bundle);
                            (context).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame, fragobj)
                                    .commit();
                        }
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
//                Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (this != null) {
                }
            }
        });
    }
}