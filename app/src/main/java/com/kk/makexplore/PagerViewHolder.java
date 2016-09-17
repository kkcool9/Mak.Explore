package com.kk.makexplore;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


public class PagerViewHolder extends RecyclerView.ViewHolder {

    public ImageView bgImg;
    public TextView expTitle;
    public TextView expDesc;
    public RatingBar expRating;

    public PagerViewHolder(View itemView) {
        super(itemView);

        bgImg = (ImageView)itemView.findViewById(R.id.mveImgIcon);
        expTitle = (TextView) itemView.findViewById(R.id.mvTitle);
        expDesc = (TextView)itemView.findViewById(R.id.mvDesc);
        expRating = (RatingBar)itemView.findViewById(R.id.ratingBar2);
    }
}
