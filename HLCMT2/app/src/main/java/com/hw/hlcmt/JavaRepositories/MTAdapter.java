package com.hw.hlcmt.JavaRepositories;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hw.hlcmt.R;

import java.util.ArrayList;

public class MTAdapter extends RecyclerView.Adapter<MTAdapter.ExampleViewHolder> {
    private ArrayList<MessageModel> mExampleList;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.mtImage);
            mTextView1 = itemView.findViewById(R.id.mtTitle);
            mTextView2 = itemView.findViewById(R.id.mtAuthor);
            mTextView3 = itemView.findViewById(R.id.mtDate);
        }
    }

    public MTAdapter(ArrayList<MessageModel> exampleList) {
        mExampleList = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mt_item, viewGroup, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder exampleViewHolder, int i) {
        MessageModel currentItem = mExampleList.get(i);

        exampleViewHolder.mImageView.setImageResource(currentItem.getImageResource());
        exampleViewHolder.mTextView1.setText(currentItem.getTitle());
        exampleViewHolder.mTextView2.setText(currentItem.getAuthor());
        exampleViewHolder.mTextView3.setText(currentItem.getDate());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

}
