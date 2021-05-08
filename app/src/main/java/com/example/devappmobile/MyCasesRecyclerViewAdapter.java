package com.example.devappmobile;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public class MyCasesRecyclerViewAdapter extends RecyclerView.Adapter<MyCasesRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<Cases> mValues;
    private List<Cases> listcopie ;

    private final CasesFragment.OnListFragmentInteractionListener mListener;

    public MyCasesRecyclerViewAdapter(List<Cases> mValues, CasesFragment.OnListFragmentInteractionListener listener) {
        this.mValues = mValues;
        this.mListener = listener;
        this.listcopie=new ArrayList<>(mValues);
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getPro_title().substring(0,mValues.get(position).getPro_title().indexOf("(") ));
        holder.mIdView.setText(mValues.get(position).getPro_title().
                substring(mValues.get(position).getPro_title().indexOf("(") +1 , mValues.get(position).getPro_title().indexOf(")") ));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;

        public Cases mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.description);
            mContentView = (TextView) view.findViewById(R.id.content);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

    }
    //search
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Cases> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredList.addAll(listcopie);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Cases item : listcopie) {
                    if (item.getPro_title().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mValues.clear();
            mValues.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
