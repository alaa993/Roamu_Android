package com.alaan.roamu.acitivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.custom.CheckConnection;
import com.alaan.roamu.fragement.RequestFragment;
import com.alaan.roamu.session.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.thebrownarrow.permissionhelper.ActivityManagePermission;

import static gun0912.tedbottompicker.TedBottomPicker.TAG;

public class image_view extends ActivityManagePermission {

    View view;
    ImageView fullScreenImageView;
    Bundle intent;
    String newString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);
        fullScreenImageView = (ImageView) findViewById(R.id.fullScreenImageView);
        intent = getIntent().getExtras();
        newString = intent.getString("imageurl");

        Log.i("ibrahim" , Server.BASE_URL + newString);
        Glide.with(this).load(Server.BASE_URL + newString).apply(new RequestOptions().error(R.drawable.images)).into(fullScreenImageView);

    }

//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        container.removeAllViews(); // Inflate the layout for this fragment
//        view = inflater.inflate(R.layout.image_view, container, false);
//
//        fullScreenImageView = (ImageView) findViewById(R.id.fullScreenImageView);
//        intent = getIntent().getExtras();
//        newString = intent.getString("imageurl");
//
//        Log.i("ibrahim" , Server.BASE_URL + newString);
//        Glide.with(this).load(Server.BASE_URL + newString).apply(new RequestOptions().error(R.drawable.images)).into(fullScreenImageView);
//        return view;
//    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
