package com.solanki.sahil.letschat;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class
MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {

    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<String> mDataset;
    private static MyRecyclerViewAdapter.MyLongClickListener myLongClickListener;



    public MyRecyclerViewAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rc, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.label.setText(mDataset.get(position));

    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setOnItemLongClickListener(MyRecyclerViewAdapter.MyLongClickListener myClickListener) {
        this.myLongClickListener = myClickListener;
    }

    public interface MyLongClickListener {
         boolean onItemLongClick(int position, View v);
    }


    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener
    {
        TextView label;


        public DataObjectHolder(View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.textView);
            itemView.setOnLongClickListener(this);
            Log.i(LOG_TAG, "Adding Listener");

        }

        @Override
        public boolean onLongClick(View v) {
            myLongClickListener.onItemLongClick(getAdapterPosition(),v);
            return true;
        }


    }






}
