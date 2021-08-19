package com.alaan.roamu.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alaan.roamu.R;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.fragement.Gruop_managment;
import com.alaan.roamu.pojo.Group_membar;
import com.alaan.roamu.pojo.PassMembar;

import java.util.List;

import static com.loopj.android.http.AsyncHttpClient.log;

public class Group_membar_Adapter extends RecyclerView.Adapter<Group_membar_Adapter.Holder> {
    List<Group_membar> list;
    PassMembar pass;

    public Group_membar_Adapter(List<Group_membar> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_item, viewGroup, false));
    }

    public class Holder extends RecyclerView.ViewHolder {


        TextView PromoDec, PromoTitle, PromoPre;

        public Holder(View itemView) {
            super(itemView);


            PromoDec = (TextView) itemView.findViewById(R.id.driver_Phone);
            PromoTitle = (TextView) itemView.findViewById(R.id.driver_name);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int i) {
        final Group_membar pojo = list.get(i);

        holder.PromoTitle.setText(pojo.getDriver_name());
        holder.PromoDec.setText(pojo.getDriver_mobile());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", pojo);
                pass = new PassMembar();
                pass.setDriver_id(pojo.getDriver_id());
                pass.setDriver_name(pojo.getDriver_name());
                pass.setDriver_mobile(pojo.getDriver_mobile());
                pass.setDriver_email(pojo.getDriver_email());
                pass.setDriver_country(pojo.getDriver_country());
                pass.setGroup_name(pojo.getGroup_name());
                Log.i("ibrahim was here onlin", pojo.getDriver_is_online());
                pass.setDriver_is_online(pojo.getDriver_is_online());
                pass.setDriver_vehicle_no(pojo.getDriver_vehicle_no());
//                AcceptRideFragment fragobj = new AcceptRideFragment();
//                fragobj.setArguments(bundle);
//                AppCompatActivity activity = (AppCompatActivity) view.getContext();
//                bundle.putSerializable("data", pass);
//                Gruop_managment gruop_managment = new Gruop_managment();
//                gruop_managment.setArguments(bundle);
//                ((HomeActivity) holder.itemView.getContext()).changeFragment(gruop_managment, "Group Managment");

            }
        });
        log.e("ss", "" + list.size());
    }

    @Override
    public int getItemCount() {
        return list.size();

    }
}
