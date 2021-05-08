package com.example.devappmobile;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.devappmobile.DraftFragment.OnListFragmentInteractionListener;
import com.google.android.material.card.MaterialCardView;


import java.util.List;


public class MyDraftRecyclerViewAdapter extends RecyclerView.Adapter<MyDraftRecyclerViewAdapter.ViewHolder> {

    private final List<Draft> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyDraftRecyclerViewAdapter(List<Draft> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_draft, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);


        String a=mValues.get(position).getApp_pro_title()+" \n ";
        String b=mValues.get(position).getApp_number();

        holder.mContentView.setText(a);
        holder.tv.setText(b);
        holder.mView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListDraftFragmentInteraction(holder.mItem);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void removeItem(int adapterPosition) {
        mValues.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);

    }

    public void restoreItem(Draft deletedItem, int deletedIndex) {
        mValues.add(deletedIndex, deletedItem);
        notifyItemInserted(deletedIndex);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public Draft mItem;
        public RelativeLayout  viewForeground;
        public TextView tv;

        public ViewHolder(View view) {
            super(view);
            mView = view;
          //  mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);

            viewForeground = view.findViewById(R.id.view_foreground);
            tv=view.findViewById(R.id.game_score) ;



        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }




}
