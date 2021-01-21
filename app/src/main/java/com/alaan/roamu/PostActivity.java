package com.alaan.roamu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.AddPostActivity;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.acitivities.List_provider;
import com.alaan.roamu.acitivities.Requst_ride;
import com.alaan.roamu.acitivities.platform;
import com.alaan.roamu.adapter.search_d_adapter;
import com.alaan.roamu.fragement.HomeFragment;
import com.alaan.roamu.fragement.RequestFragment;
import com.alaan.roamu.pojo.Comment;
import com.alaan.roamu.pojo.CommentList;
import com.alaan.roamu.pojo.NearbyData;
import com.alaan.roamu.pojo.Pass;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class PostActivity extends Fragment {

    View view;
    ListView listViewPosts;
    EditText inputEditComment;
    TextView AddCommentBTN;
    TextView TripDetail;
    Pass pass;

    //a list to store all the artist from firebase database
    List<Comment> comments;
    DatabaseReference databasePost;
    DatabaseReference databaseComments;
    RelativeLayout PostRL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("ibrahim from post1", "-----------------");

        view = inflater.inflate(R.layout.activity_post, container, false);
        Log.i("ibrahim from post2", "-----------------");

        view.setBackgroundColor(Color.WHITE);
        PostRL = (RelativeLayout) view.findViewById(R.id.activity_post_post);

        Bundle bundle = getArguments();
        String Post_id_bundle = bundle.getString("Post_id");
        if (bundle.getString("request_type").contains("private")) {
            PostRL.setVisibility(View.GONE);
            databasePost = FirebaseDatabase.getInstance().getReference("private_posts").child(Post_id_bundle);
            databaseComments = FirebaseDatabase.getInstance().getReference("private_posts").child(Post_id_bundle).child("Comments");

        } else if (bundle.getString("request_type").contains("public")) {
            PostRL.setVisibility(View.VISIBLE);
            databasePost = FirebaseDatabase.getInstance().getReference("posts").child(Post_id_bundle);
            databaseComments = FirebaseDatabase.getInstance().getReference("posts").child(Post_id_bundle).child("Comments");

        }

        comments = new ArrayList<>();
        listViewPosts = (ListView) view.findViewById(R.id.Post_listViewComments);
        log.i("tag", "success by ibrahim");
        log.i("tag", Post_id_bundle);
        //log.i("tag",databaseComments.child("Comment1").child("username"));

        inputEditComment = view.findViewById(R.id.input_Comment);
        AddCommentBTN = view.findViewById(R.id.AddCommentBTN);
        TripDetail = view.findViewById(R.id.TripDetail);
        AddCommentBTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (inputEditComment.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), getString(R.string.Post_Empty), Toast.LENGTH_LONG).show();
                } else {
                    SavePost(bundle);
                }
            }
        });

        TripDetail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("ibrahim was here1","-----------------------------");
//                NeaBy("10");
            }
        });

        return view;
    }

    public void NeaBy(String travel_id) {
        Log.i("ibrahim was here2","-----------------------------");
        RequestParams params = new RequestParams();
        params.put("bag",travel_id);
        Server.setHeader(SessionManager.getKEY());

        Server.get("api/user/travels2/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("ibrahim was here3","-----------------------------");
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        Gson gson = new GsonBuilder().create();
                        log.i("tag","success by ibrahim");
                        Log.i("ibrahim was here4","-----------------------------");
                        Log.i("ibrahim was here5",response.getJSONArray("data").toString());
                        List<NearbyData> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<NearbyData>>() {

                        }.getType());
                        final NearbyData nearbyData = list.get(0);
                        pass = new Pass();
                        pass.f = Pass.fragment_type.ADD;
//                        pass.setFromPlace(nearbyData.getPickup_address());
//                        pass.setToPlace(nearbyData.getDrop_address());
                        pass.setFromAddress(nearbyData.getPickup_address());
                        pass.setToAddress(nearbyData.getDrop_address());
                        pass.setPickupPoint(nearbyData.getPickup_point());
                        pass.setDriverId(nearbyData.getUser_id());
                        //by ibrahim
                        pass.setTravelId(nearbyData.getTravel_id());
/////////
                        pass.setFare(nearbyData.getAmount());
                        pass.setDriverName(nearbyData.getName());
//                        pass.setSmoke(nearbyData.getsmoked());
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
                        //
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", pass);
                        Log.i("ibrahim was here","-----------------------------");
                        Log.i("ibrahim was here", String.valueOf(pass.getTravelId()));
                        Log.i("ibrahim was here", String.valueOf(nearbyData.empty_set));
                        Log.i("ibrahim was here", String.valueOf(pass.empty_set));
                        Intent myIntent = new Intent(getActivity(), Requst_ride.class);
                        myIntent.putExtras(bundle);

                        PostActivity.this.startActivity(myIntent);
                        //
                    }
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

    public void SavePost(Bundle bundle) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(uid.toString());

        databaseRefID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String UserName = dataSnapshot.child("username").getValue(String.class);
                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                log.i("tag", "success by ibrahim");
                log.i("tag", UserName);
                // Firebase code here
                if (bundle.getString("request_type").contains("private")) {
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("private_posts").child(bundle.getString("Post_id")).child("Comments").push();
                    Map<String, Object> author = new HashMap<>();
                    author.put("uid", user.getUid());
                    author.put("username", UserName);
                    author.put("photoURL", photoURL);

                    Map<String, Object> userObject = new HashMap<>();
                    userObject.put("author", author);
                    userObject.put("text", inputEditComment.getText().toString());
                    userObject.put("timestamp", ServerValue.TIMESTAMP);
                    //type = 0 => driver
                    //type = 1 => user
                    userObject.put("type", "1");
                    databaseRef.setValue(userObject);
                    inputEditComment.getText().clear();
                } else if (bundle.getString("request_type").contains("public")) {
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts").child(bundle.getString("Post_id")).child("Comments").push();
                    Map<String, Object> author = new HashMap<>();
                    author.put("uid", user.getUid());
                    author.put("username", UserName);
                    author.put("photoURL", photoURL);

                    Map<String, Object> userObject = new HashMap<>();
                    userObject.put("author", author);
                    userObject.put("text", inputEditComment.getText().toString());
                    userObject.put("timestamp", ServerValue.TIMESTAMP);
                    //type = 0 => driver
                    //type = 1 => user
                    userObject.put("type", "1");
                    databaseRef.setValue(userObject);
                    inputEditComment.getText().clear();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        attaching value event listener
        databasePost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                TextView textViewName = (TextView) view.findViewById(R.id.Post_textViewName);
                TextView textViewText = (TextView) view.findViewById(R.id.Post_textViewText);
                TextView textViewDate = (TextView) view.findViewById(R.id.Post_textViewDate);
                ImageView PostAvatar = (ImageView) view.findViewById(R.id.Post_image);
                textViewName.setText(post.author.username);
                textViewText.setText(post.text);

                if (post.timestamp != null) {
                    Date date = new Date(post.timestamp);
                    SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String stringDate = DateFor.format(date);
                    textViewDate.setText(stringDate.toString());
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                String uid = user.getUid();
                DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(post.author.uid);
                databaseRefID.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                        if (photoURL != null) {
//                    Glide.with(PostList.this.getContext()).load(post.author.photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
                            Glide.with(PostActivity.this).load(photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });

        databaseComments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                comments.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Comment Comment = postSnapshot.getValue(Comment.class);
                    //adding artist to the list
                    comments.add(Comment);
                }

                //creating adapter
                CommentList commentAdapter = new CommentList(getActivity(), comments);
                //attaching adapter to the listview
                listViewPosts.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}