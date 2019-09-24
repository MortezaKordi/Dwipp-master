package com.mystartup.bezanberimapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeRecyclerViewAdapter extends  RecyclerView.Adapter<HomeRecyclerViewAdapter.MyViewHolder> {


    private List<HomeKashi> mKashiList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;

    // data is passed into the constructor
    HomeRecyclerViewAdapter(Context context, List<HomeKashi> data) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mKashiList = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.home_kashi_item, parent, false);
        return new MyViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        String animal = mData.get(position);
//        holder.myTextView.setText(animal);

        HomeKashi kashi = mKashiList.get(position);
        holder.imgKashiIcon.setImageResource(kashi.getImage());
        holder.txtTitle.setText(kashi.getTitle());
        holder.txtSubtitle.setText(kashi.getSubTitle());

        int color = Color.parseColor(kashi.getBgColor());
        holder.homeKashi.setCardBackgroundColor(color);

        int margin = dpToPx(4);


        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.homeKashi.getLayoutParams();
        layoutParams.setMargins(margin,margin,margin,margin);
        holder.homeKashi.setLayoutParams(layoutParams);


    }

    private int dpToPx(int dp){
        float px = dp*mContext.getResources().getDisplayMetrics().density;
        return (int) px;

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mKashiList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgKashiIcon;
        TextView txtTitle;
        TextView txtSubtitle;
        CardView homeKashi;


        MyViewHolder(View itemView) {
            super(itemView);

            imgKashiIcon = itemView.findViewById(R.id.imgHomeKashi);
            txtTitle = itemView.findViewById(R.id.txtHomeKashiTitle);
            txtSubtitle = itemView.findViewById(R.id.txtHomeKashiSubtitle);

            homeKashi = itemView.findViewById(R.id.homeKashi);
            itemView.setOnClickListener(this);




        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    HomeKashi getItem(int id) {
        return mKashiList.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }






}
