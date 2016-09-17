package com.kk.makexplore;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import java.util.ArrayList;
import java.util.Map;


public class PlacesNearByRecyclerAdapter extends RecyclerView.Adapter<PlacesNearByRecyclerAdapter.ViewHolder> {
   private Context mContext;
    ArrayList<Business> placesDataSet;
    Business placeData;
    OnItemClickListner mItemClickListner;

   public PlacesNearByRecyclerAdapter(Context context, ArrayList<Business> businesses){
       mContext = context;
       placesDataSet = businesses;
   }

    @Override
    public PlacesNearByRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_experiences_layout_cardview,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    public interface OnItemClickListner{
        public void onItemClick(View view,int position,ImageView imgView);

    }

    public void setOnItemClickListner(final OnItemClickListner mItemClickListner){
        this.mItemClickListner = mItemClickListner;
    }



    @Override
    public void onBindViewHolder(PlacesNearByRecyclerAdapter.ViewHolder holder, int position) {
        placeData = placesDataSet.get(position);
        holder.bindPlaceData(placeData);
        if (placeData.imageUrl() != null){
            Uri url = (Uri.parse(placeData.imageUrl()));
            Log.i("PLACE IMAGE URL :", "onBindViewHolder: "+placeData.imageUrl());
            Picasso.with(mContext).load(url).into(holder.imgView);

        }


    }

    @Override
    public int getItemCount() {
        return placesDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView placeTitle;
        public TextView placeDesc;
        public ImageView imgView;

        public ViewHolder(View itemView) {
            super(itemView);

            placeTitle = (TextView)itemView.findViewById(R.id.mvTitle);
            placeDesc = (TextView)itemView.findViewById(R.id.mvDesc);
            imgView = (ImageView)itemView.findViewById(R.id.mveImgIcon);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListner != null) {
                        mItemClickListner.onItemClick(v, getAdapterPosition(),imgView);
                    }
                }
            });

        }

        public void bindPlaceData(Business place){
            placeTitle.setText(place.name());
            placeDesc.setText(place.snippetText());
            imgView.setTransitionName(place.name());


        }


    }
}
