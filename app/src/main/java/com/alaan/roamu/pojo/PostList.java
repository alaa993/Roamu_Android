package com.alaan.roamu.pojo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alaan.roamu.PostActivity;
import com.alaan.roamu.R;
import com.alaan.roamu.acitivities.platform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.sql.Timestamp;

public class PostList extends ArrayAdapter<Post> {
    private FragmentActivity context;
    List<Post> posts;

    public PostList(FragmentActivity context, List<Post> posts) {
        super(context, R.layout.layout_artist_list, posts);
        this.context = context;
        this.posts = posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
     //   LayoutInflater inflater = context.getLayoutInflater();
//        View view = super.getView(position,convertView,parent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View listViewItem = inflater.inflate(R.layout.layout_artist_list, null, true);
        //listViewItem.setBackgroundResource(R.drawable.listview_item_border);


        Log.i("ttttttttttt", "getView: ");
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewText = (TextView) listViewItem.findViewById(R.id.textViewText);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.textViewDate);
        ImageView PostAvatar  = (ImageView) listViewItem.findViewById(R.id.image);
        Post post = posts.get(position);
        String type = post.type;

         //   Log.i("mmmmmmmmtttttt", type);
        // Log.println(1,"tttttttt", type);

        if(type.equals("0")){
            listViewItem.setBackgroundColor(Color.parseColor("#f5fafe"));
        }else if(type.equals("1")){
            listViewItem.setBackgroundColor(Color.WHITE);
            //listViewItem.setBackgroundResource(R.drawable.listview_item_border);
        }
if(post.author.username!=null && post.text!=null) {
    Log.i("ttttttttttt", "getView111: ");
    textViewName.setText(post.author.username);
    textViewText.setText(post.text);
}
        if (post.timestamp != null)
        {
            Date date = new Date(post.timestamp);
            SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String stringDate= DateFor.format(date);
            textViewDate.setText(stringDate.toString());
        }
        Log.i("ttttttttttt", "getView222: ");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(post.author.uid);
        databaseRefID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                Log.i("ttttttttttt", "getView3333: ");
                if (photoURL != null) {
//                    Glide.with(PostList.this.getContext()).load(post.author.photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
                    Glide.with(PostList.this.getContext()).load(photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
        return listViewItem;
    }
}