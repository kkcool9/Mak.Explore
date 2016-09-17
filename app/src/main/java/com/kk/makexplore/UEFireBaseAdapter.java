package com.kk.makexplore;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;


public class UEFireBaseAdapter extends FirebaseRecyclerAdapter<ExperienceData,UEFireBaseAdapter.ExperienceViewHolder>{
    static OnitemClickListner mItemClickListner;
    private List<Map<String,String>> mDataSet;
    private Context mContext;
    private LruCache<String,Bitmap> mImgMEMCAche;

    public UEFireBaseAdapter(Class<ExperienceData> modelClass, int modelLayout, Class<ExperienceViewHolder> viewHolderClass, Query ref, Context context, ExperienceCloudData expCloudData, LruCache<String,Bitmap> mImgCache) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mImgMEMCAche = mImgCache;
        this.mContext= context;
        mDataSet = expCloudData.getExperienceList();
    }


    @Override
    protected void populateViewHolder(ExperienceViewHolder experienceViewHolder, ExperienceData experienceData, int i) {
        Picasso.with(mContext).load(experienceData.getPhotoURL()).into(experienceViewHolder.expBgImg);
        experienceViewHolder.expDesc.setText(experienceData.getEXPDescription());
        experienceViewHolder.expTitle.setText(experienceData.getEXPTitle());
        experienceViewHolder.expRating.setRating(4.5f);

    }

    @Override
    public void onBindViewHolder(ExperienceViewHolder viewHolder, int position) {
        Map<String,String> experience = mDataSet.get(position);
        Log.i("Experience Data", "==========================================: " + experience);

        Uri url = (Uri.parse((String)experience.get("PhotoURL")));
        Picasso.with(mContext).load(url).into(viewHolder.expBgImg);
        viewHolder.bindExpData(experience);
    }

    public void setOnItemClickListner(final OnitemClickListner onItemClickListner){
        mItemClickListner = onItemClickListner;

    }

    public interface OnitemClickListner{
        void onItemClick(View view,int position,ImageView ImgView);

    }

    public static class ExperienceViewHolder extends RecyclerView.ViewHolder{

        public ImageView expBgImg;
        public TextView expTitle;
        public TextView expDesc;
        public RatingBar expRating;

        public ExperienceViewHolder(final View itemView) {
            super(itemView);
            expBgImg = (ImageView)itemView.findViewById(R.id.mveImgIcon);
            expTitle = (TextView)itemView.findViewById(R.id.mvTitle);
            expDesc = (TextView)itemView.findViewById(R.id.mvDesc);
            expRating = (RatingBar)itemView.findViewById(R.id.ratingBar2);
    //TODO : rating . . .
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListner != null) {
                        mItemClickListner.onItemClick(itemView, getAdapterPosition(),expBgImg);
                    }

                }
            });
        }

        public void bindExpData(Map<String,String> exp){
            expTitle.setText(exp.get("EXPTitle"));
            expDesc.setText(exp.get("EXPDescription"));
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                itemView.setTransitionName(exp.get("EXPID"));
            }



        }


    }

}
