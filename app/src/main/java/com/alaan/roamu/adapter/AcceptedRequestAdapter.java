package com.alaan.roamu.adapter;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.alaan.roamu.R;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.fragement.AcceptedDetailFragment;
import com.alaan.roamu.pojo.PendingRequestPojo;
import com.alaan.roamu.pojo.firebaseRide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AcceptedRequestAdapter extends RecyclerView.Adapter<AcceptedRequestAdapter.Holder> {
    List<PendingRequestPojo> list;
    ValueEventListener listener;
    DatabaseReference databaseRides;

    public AcceptedRequestAdapter(List<PendingRequestPojo> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.acceptedrequest_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final PendingRequestPojo pojo = list.get(position);
        holder.from_add.setText(pojo.getpickup_address());
        holder.to_add.setText(pojo.getDrop_address());
        holder.drivername.setText(pojo.getDriver_name());
        holder.time.setText(pojo.getTime());
        holder.date.setText(pojo.getDate());
        holder.status.setText(pojo.getStatus());
        holder.txt_car_type.setText(pojo.getCarType());

        databaseRides = FirebaseDatabase.getInstance().getReference("rides").child(pojo.getRide_id());
        listener = databaseRides.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseRide fbRide = dataSnapshot.getValue(firebaseRide.class);
                Log.i("ibrahim ride", "----------");
                holder.status.setText(fbRide.ride_status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", pojo);

                AcceptedDetailFragment detailFragment = new AcceptedDetailFragment();
                detailFragment.setArguments(bundle);
                ((HomeActivity) holder.itemView.getContext()).changeFragment(detailFragment, "Passenger Information");
            }
        });
        BookFont(holder, holder.f);
        BookFont(holder, holder.t);
        BookFont(holder, holder.dn);
        BookFont(holder, holder.dt);
        MediumFont(holder, holder.from_add);
        MediumFont(holder, holder.to_add);
        MediumFont(holder, holder.date);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView drivername, from_add, to_add, date, time, status, txt_car_type;
        TextView f, t, dn, dt;

        public Holder(View itemView) {
            super(itemView);
            f = (TextView) itemView.findViewById(R.id.from);
            t = (TextView) itemView.findViewById(R.id.to);
            dn = (TextView) itemView.findViewById(R.id.drivername);
            dt = (TextView) itemView.findViewById(R.id.datee);
            drivername = (TextView) itemView.findViewById(R.id.txt_drivername);
            from_add = (TextView) itemView.findViewById(R.id.txt_from_add);
            to_add = (TextView) itemView.findViewById(R.id.txt_to_add);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            status = (TextView) itemView.findViewById(R.id.Statuss);
            txt_car_type = (TextView) itemView.findViewById(R.id.txt_car_type);
        }
    }

    public void BookFont(Holder holder, TextView view1) {
        Typeface font1 = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        view1.setTypeface(font1);
    }

    public void MediumFont(Holder holder, TextView view) {
        Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Medium.otf");
        view.setTypeface(font);
    }
}