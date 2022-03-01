package com.alaan.roamu.fragement;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.alaan.roamu.R;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.pojo.Post;

import com.alaan.roamu.adapter.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.skoumal.fragmentback.BackFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class platformFragment extends Fragment implements BackFragment {

    View view;
    ListView listViewPosts;
    private FirebaseUser fUser;
    List<Post> posts;
    DatabaseReference databasePosts;
    ValueEventListener listener;

    public platformFragment() {
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


        //list to store artists
        posts = new ArrayList<>();
        databasePosts = FirebaseDatabase.getInstance().getReference("posts");
        view = inflater.inflate(R.layout.fragment_platform, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.platform));
        view.setBackgroundColor(Color.WHITE);
        BindView();

        listViewPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected artist
                Post post = posts.get(i);

                Bundle bundle = new Bundle();
                bundle.putString("Post_id", post.id);
                bundle.putString("request_type", "public");
                //log.i("ibrahim from platform1", "-----------------");
                PostFragment postfragment = new PostFragment();
                //log.i("ibrahim from platform2", "-----------------");
                postfragment.setArguments(bundle);
                changeFragment(postfragment, "Requests");
            }
        });

        listViewPosts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                     @Override
                                                     public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                         Post post = posts.get(i);
                                                         //getting the selected artist
                                                         if (post.author.uid.endsWith(fUser.getUid())) {
                                                             android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(getActivity());
                                                             View mView = getLayoutInflater().inflate(R.layout.dialog_update_post_layout, null);
                                                             final EditText ET_DUOL = (EditText) mView.findViewById(R.id.ET_DUOL);
                                                             ET_DUOL.setText(post.text);
                                                             Button btnSubmit_DUOL = (Button) mView.findViewById(R.id.btnSubmit_DUOL);
                                                             Button btnCancel_DUOL = (Button) mView.findViewById(R.id.btnCancel_DUOL);
                                                             mBuilder.setView(mView);
                                                             final AlertDialog dialog = mBuilder.create();
                                                             dialog.show();
                                                             btnSubmit_DUOL.setOnClickListener(new View.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(View view) {
                                                                     //log.i("ibrahim", post.id);
                                                                     //log.i("ibrahim", ET_DUOL.getText().toString());
                                                                     SavePost(ET_DUOL.getText().toString(), post.id);
                                                                     dialog.dismiss();
                                                                 }
                                                             });
                                                             btnCancel_DUOL.setOnClickListener(new View.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(View view) {
                                                                     dialog.dismiss();
                                                                 }
                                                             });
                                                         }
                                                         return true;
                                                     }
                                                 });
        return view;
    }

    public void SavePost(String post_text, String Post_id) {

        //log.i("ibrahim", Post_id);
        //log.i("ibrahim", post_text);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts").child(Post_id).child("text");
        databaseRef.setValue(post_text);
    }

    public void changeFragment(final Fragment fragment, final String fragmenttag) {

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    drawer_close();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack(null);
                }
            }, 50);
        } catch (Exception e) {

        }
    }

    public void BindView() {
        listViewPosts = (ListView) view.findViewById(R.id.listViewPosts);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener
        listener = databasePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                posts.clear();
                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Post Post = postSnapshot.getValue(Post.class);
                    Post.id = postSnapshot.getKey();
                    //adding artist to the list
                    //log.i("ibrahim", Post.privacy);
                    if (Post.privacy.contains("1")) {
                        posts.add(Post);
                    }
                }
                Collections.reverse(posts);
                if (!posts.isEmpty()) {
                    //creating adapter
                    Fragment fragment = getFragmentManager().findFragmentByTag(getString(R.string.platform));
                    if (fragment != null && fragment.isVisible()) {
                        PostAdapter postAdapter = new PostAdapter(getActivity(), posts);
                        //attaching adapter to the listview
                        postAdapter.notifyDataSetChanged();
                        listViewPosts.setAdapter(postAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getSystemService(String layoutInflaterService) {
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public int getBackPriority() {
        return 0;
    }

    @Override
    public void onPause() {
        super.onPause();
        databasePosts.removeEventListener(listener);
    }
}