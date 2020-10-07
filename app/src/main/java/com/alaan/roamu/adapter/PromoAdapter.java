package com.alaan.roamu.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alaan.roamu.R;
import com.alaan.roamu.pojo.promopojo;

import java.util.List;

import static com.loopj.android.http.AsyncHttpClient.log;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.Holder> {
    List<promopojo> list;

    public PromoAdapter(List<promopojo> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.promo_list, viewGroup, false));
    }
    public class Holder extends RecyclerView.ViewHolder {


        TextView PromoDec, PromoTitle, PromoPre;

        public Holder(View itemView) {
            super(itemView);




            PromoDec = (TextView) itemView.findViewById(R.id.tvPromoDesc);
            PromoTitle = (TextView) itemView.findViewById(R.id.tvPromoTitle);
             PromoPre = (TextView) itemView.findViewById(R.id.tvPromoTerm);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final promopojo pojo = list.get(i);

        holder.PromoTitle.setText(pojo.getPromo_code());
        holder.PromoDec.setText(pojo.getPromo_desc());
        holder.PromoPre.setText(pojo.getPromo_code());
        log.e("ss",""+list.size());
    }



    @Override
    public int getItemCount() {
        return list.size();

    }
}
