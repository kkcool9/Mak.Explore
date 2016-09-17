package com.kk.makexplore;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.firebase.client.Firebase;
import com.firebase.client.core.persistence.LRUCachePolicy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;
import jp.wasabeef.recyclerview.animators.OvershootInRightAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


public class UpcomingExperiencesFragment extends Fragment {
    private RecyclerView mRecyclerView;
    public LruCache<String ,Bitmap> mImgMemoryCache;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String FIREBASEREF = "https://projectexplore.firebaseio.com/Experiences";
    private Firebase firebaseReftForAuth;
    UEFireBaseAdapter mFirebaseAdapter;
    final Firebase ref = new Firebase(FIREBASEREF);
    ExperienceCloudData experienceCloudData;
    StaggeredGridLayoutManager mStaggeredLManager;
    List <Map<String,?>> experienceList;
    private String userID;
    boolean menu_checked = false;



    // Fragment : New Instance
    public static UpcomingExperiencesFragment newInstance(int id,String UID,Firebase firebaseR){
        UpcomingExperiencesFragment fragment = new UpcomingExperiencesFragment();

        Bundle args = new Bundle();
        args.putInt("FRG_ID", id);
        args.putString("USERID",UID);
       // args.putSerializable("FIREBASE_REF", (Serializable) firebaseR);
        fragment.setArguments(args);
        return fragment;
    }

    // Default Constructor
    public UpcomingExperiencesFragment(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        experienceCloudData = new ExperienceCloudData();
        setRetainInstance(true);
        setHasOptionsMenu(true);

        if(mImgMemoryCache == null){
            final int MAX_MEMORY =(int)(Runtime.getRuntime().maxMemory()/1024);
            final int CACHE_SIZE = MAX_MEMORY/8;
            mImgMemoryCache = new LruCache<String, Bitmap>(CACHE_SIZE){
                protected int sizeOf(String key,Bitmap bitmap){
                    return bitmap.getByteCount()/1024;
                }
            };
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.upcomingevents_recyclerview_layout,container,false);

        if(getArguments() != null){
            userID = getArguments().getString("USERID");
        }

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.upcomingExperiencesList);
        if(experienceCloudData.getSize() == 0){
            experienceCloudData.setAdapter(mFirebaseAdapter);
            experienceCloudData.setContext(getActivity());
            experienceCloudData.initData();
        }



        mStaggeredLManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        SlideInLeftAnimator animator = new SlideInLeftAnimator();
        animator.setInterpolator(new OvershootInterpolator());

        //mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(mStaggeredLManager);
        mFirebaseAdapter = new UEFireBaseAdapter(ExperienceData.class,R.layout.upcoming_experiences_layout_cardview,UEFireBaseAdapter.ExperienceViewHolder.class,ref,getActivity(),experienceCloudData,mImgMemoryCache);
        mRecyclerView.setAdapter(mFirebaseAdapter);
        mFirebaseAdapter.setOnItemClickListner(new UEFireBaseAdapter.OnitemClickListner() {
            @Override
            public void onItemClick(View view, int position,ImageView imgView) {
                ExperienceDetailFragment experienceDetailFragmentFragment = ExperienceDetailFragment.newInstance((HashMap<String,String>)experienceCloudData.getItem(position),userID);
                experienceDetailFragmentFragment.setSharedElementEnterTransition(new detailsTransition());
                experienceDetailFragmentFragment.setEnterTransition(new Fade());
                experienceDetailFragmentFragment.setExitTransition(new Fade());
                experienceDetailFragmentFragment.setSharedElementReturnTransition(new detailsTransition() );
                getFragmentManager().beginTransaction().replace(R.id.mainactivity_fragmentcontainer,experienceDetailFragmentFragment)
                                    .addSharedElement(view,view.getTransitionName())
                                    .addToBackStack(null).commit();
            }
        });

        itemAnimation();
        adapteranimation();

        return  rootView;
    }

    public void itemAnimation() {
        FadeInDownAnimator animator = new FadeInDownAnimator();
        //OvershootInRightAnimator animator = new OvershootInRightAnimator();
        animator.setInterpolator((new OvershootInterpolator()));
        animator.setAddDuration(400);
        animator.setRemoveDuration(400);
        mRecyclerView.setItemAnimator(animator);
    }

    private void adapteranimation() {
        SlideInBottomAnimationAdapter animationAdapter1 = new SlideInBottomAnimationAdapter(mFirebaseAdapter);
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(animationAdapter1);
        scaleInAnimationAdapter.setDuration(400);

        mRecyclerView.setAdapter(scaleInAnimationAdapter);
    }




    public class detailsTransition extends TransitionSet{
        public detailsTransition(){
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds()).addTransition(new ChangeTransform()).addTransition(new ChangeImageTransform());
        }
    }

    public interface onListItemSelectedListner{
        void onListItemSelected(int position, HashMap<String,?> Experiences);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.upcomingeventstagger_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

      int id = item.getItemId();
        switch(id)
        {
            case R.id.view:
            {
                if(item.isChecked())
                {
                    item.setIcon(R.drawable.ic_view_module_white_18dp);
                    mStaggeredLManager.setSpanCount(1);
                    Log.i("MENU ITEM SELECTED", "onOptionsItemSelected: ");

                }

                else{
                    mStaggeredLManager.setSpanCount(2);
                    item.setIcon(R.drawable.ic_view_stream_white_18dp);
                    Log.i("MENU ITEM UNSELECTED", "onOptionsItemSelected: ");
                }
                item.setChecked(!item.isChecked());


                break;

            }
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onPrepareOptionsMenu(Menu menu) {


    }

}
