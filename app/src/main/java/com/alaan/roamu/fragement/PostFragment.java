package com.alaan.roamu.fragement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamu.PostActivity;
import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.List_provider;
import com.alaan.roamu.acitivities.Requst_ride;
import com.alaan.roamu.pojo.Comment;
import com.alaan.roamu.pojo.CommentList;
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


public class PostFragment extends Fragment {

    View view;
    ListView listViewPosts;
    EditText inputEditComment;
    TextView AddCommentBTN;


    //a list to store all the artist from firebase database
    List<Comment> comments;
    DatabaseReference databasePost;
    DatabaseReference databaseComments;
    RelativeLayout PostRL;
    ValueEventListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("ibrahim from post1", "-----------------");

        view = inflater.inflate(R.layout.fragment_post, container, false);
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
        AddCommentBTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (inputEditComment.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), getString(R.string.Post_Empty), Toast.LENGTH_LONG).show();
                } else {
                    SavePost(bundle);
                }
            }
        });

        return view;
    }

    public void SavePost(Bundle bundle) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(uid.toString());

        databaseRefID.addListenerForSingleValueEvent(new ValueEventListener() {
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
        databasePost.addListenerForSingleValueEvent(new ValueEventListener() {
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
                databaseRefID.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                        if (photoURL != null) {
//                    Glide.with(PostList.this.getContext()).load(post.author.photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
                            Glide.with(PostFragment.this).load(photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
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

        listener = databaseComments.addValueEventListener(new ValueEventListener() {
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

    @Override
    public void onPause() {
        super.onPause();
        databaseComments.removeEventListener(listener);
    }
}