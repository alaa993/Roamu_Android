package com.alaan.roamu.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamu.R;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.pojo.Comment;
import com.alaan.roamu.adapter.CommentAdapter;
import com.alaan.roamu.session.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


import net.skoumal.fragmentback.BackFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.loopj.android.http.AsyncHttpClient.log;

public class UsersCommentsFragment extends Fragment implements BackFragment {

    View view;
    ListView listViewPosts;
    EditText inputEditComment;
    TextView AddCommentBTN;

    //a list to store all the artist from firebase database
    List<Comment> comments;
    DatabaseReference databasePost;
    DatabaseReference databaseComments;
    ValueEventListener listener;

    Bundle bundle;
    String Driver_id;

    public UsersCommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //log.i("ibrahim", SessionManager.getUserId());
        view = inflater.inflate(R.layout.fragment_users_comments, container, false);
        bundle = getArguments();
        if (bundle != null) {
            if(bundle.getSerializable("request_type").equals("private")){

            }
            else
                ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.userComments));
            Driver_id = (String) bundle.getSerializable("data");
            //log.i("ibrahim", "bundle");
            //log.i("ibrahim", Driver_id);

        }

        comments = new ArrayList<>();
        databasePost = FirebaseDatabase.getInstance().getReference("private_posts").child(Driver_id);
        databaseComments = FirebaseDatabase.getInstance().getReference("private_posts").child(Driver_id).child("Comments");
        listViewPosts = (ListView) view.findViewById(R.id.Post_listViewComments1);
        //log.i("tag", "success by ibrahim");


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        attaching value event listener
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
                CommentAdapter commentAdapter = new CommentAdapter(UsersCommentsFragment.this.getActivity(), comments);
                //attaching adapter to the listview
                listViewPosts.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void SavePost() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(uid.toString());

        databaseRefID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String UserName = dataSnapshot.child("username").getValue(String.class);
                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                //log.i("tag", "success by ibrahim");
                //log.i("tag", UserName);
                // Firebase code here
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("private_posts").child(Driver_id).child("Comments").push();
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
                userObject.put("type", "0");
                userObject.put("privacy", "1");
                userObject.put("travel_id", 0);
                databaseRef.setValue(userObject);
                inputEditComment.getText().clear();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseComments.removeEventListener(listener);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public int getBackPriority() {
        return 0;
    }
}