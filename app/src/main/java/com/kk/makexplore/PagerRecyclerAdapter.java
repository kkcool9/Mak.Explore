package com.kk.makexplore;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;


public class PagerRecyclerAdapter extends RecyclerView.Adapter<PagerViewHolder> {

    Context mContext;
    List<Map<String,String>> experiences;

    public PagerRecyclerAdapter(List<Map<String, String>> experiences, Context mContext) {
        this.experiences = experiences;
        this.mContext = mContext;
    }

    @Override
    public PagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       Log.i("Pager Adapter", "onCreateViewHolder: "+experiences.get(1));
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_experiences_layout_cardview,null);
        PagerViewHolder holder = new PagerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PagerViewHolder holder, int position) {
        holder.expDesc.setText(experiences.get(position).get("EXPDescription"));
        holder.expTitle.setText(experiences.get(position).get("EXPTitle"));
        Uri url = (Uri.parse(experiences.get(position).get("PhotoURL")));
        Picasso.with(mContext).load(url).into(holder.bgImg);

       // holder.expDesc.setText(experiences.get(position).get("EXPDescription"));
        //TODO : PICASSO


    }

    @Override
    public int getItemCount() {
        if(experiences == null)
            return  0;
        return experiences.size();
    }
}
