package com.alaan.roamu.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alaan.roamu.R;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.fragement.RequestFragment;
import com.alaan.roamu.pojo.NearbyData;
import com.alaan.roamu.pojo.Pass;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public class search_d_adapter extends RecyclerView.Adapter<search_d_adapter.Holder> {
    List<NearbyData> list;
    Pass pass;

    public search_d_adapter(List<NearbyData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.acceptedrequest_item, viewGroup, false));
    }

    public class Holder extends RecyclerView.ViewHolder {


        TextView from, to, drivername, time, date, tvPrice, tvTypeCoach, txt_car_type;

        ImageButton btn_req;

        public Holder(View itemView) {
            super(itemView);


            //  btn_req = (ImageButton) itemView.findViewById(R.id.img_arrow);
            /// date = (TextView) itemView.findViewById(R.id.tvDate);
            time = (TextView) itemView.findViewById(R.id.time);
            from = (TextView) itemView.findViewById(R.id.txt_from_add);
            to = (TextView) itemView.findViewById(R.id.txt_to_add);
            //tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            //tvTypeCoach = (TextView) itemView.findViewById(R.id.tvTypeCoach);

            drivername = (TextView) itemView.findViewById(R.id.txt_drivername);
            txt_car_type = (TextView) itemView.findViewById(R.id.txt_car_type);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final NearbyData nearbyData = list.get(i);
        Place pickup, drop, s_drop, s_pic;
        //   holder.tvPrice.setText(nearbyData.getAmount());
        //  holder.tvTypeCoach.setText(nearbyData.getVehicle_info());
        holder.drivername.setText(nearbyData.getName());
        holder.from.setText(nearbyData.getPickup_address());
        holder.to.setText(nearbyData.getDrop_address());
        holder.time.setText(nearbyData.getTravel_date());
        holder.txt_car_type.setText(nearbyData.getCarType());

        //  holder.date.setText(nearbyData.getTravel_date());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // travel search results by ibrahim
                Bundle bundle = new Bundle();
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
                pass.setDriverCity(nearbyData.getDriverCity());
                pass.setSmoke(nearbyData.getsmoked());
                pass.setDate(nearbyData.getTravel_date());
                pass.setTime(nearbyData.getTravel_time());
                pass.setAvalibleset(nearbyData.getBooked_set());
                pass.setTravel_status(nearbyData.getTravel_status());
                pass.setStatus("PENDING");
                pass.setPayment_mode(nearbyData.getPayment_mode());
                pass.setPayment_status(nearbyData.getPayment_status());
                pass.avatar = nearbyData.avatar;
                pass.vehicle_info = nearbyData.vehicle_info;
                // by ibrahim
                //log.i("ibrahim", "success by ibrahim search_d_adapter");
                //log.i("ibrahim", pass.getStatus());
                //log.i("ibrahim", nearbyData.getVehicle_info());

                pass.setVehicleName(nearbyData.getVehicle_info());
                pass.empty_set = nearbyData.empty_set;
                pass.DriverRate = nearbyData.DriverRate;
                pass.Travels_Count = nearbyData.Travels_Count;
                pass.pickup_location = nearbyData.getPickup_location();

                RequestFragment fragobj = new RequestFragment();
                fragobj.setArguments(bundle);
                bundle.putSerializable("data", pass);
                fragobj.setArguments(bundle);
                ((HomeActivity) view.getContext()).changeFragment(fragobj, "Request Ride");
//                ((HomeActivity) view.getContext()).showframe(0);

                // activity.getFragmentManager().beginTransaction().replace(R.id.container,new RequestManagerFragment()).addToBackStack(null).commit();
            }
        });
        //log.e("ss", "" + list.size());


    }

    @Override
    public int getItemCount() {
        return list.size();

    }
}
