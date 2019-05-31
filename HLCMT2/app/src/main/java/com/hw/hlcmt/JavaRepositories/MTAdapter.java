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
    private ArrayList<MessageModel> MTList;
    private OnItemClickListener mtListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mtListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mtIconView;
        public TextView mtTitleView;
        public TextView mtAuthorView;
        public TextView mtWeekView;

        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mtIconView = itemView.findViewById(R.id.mtImage);
            mtTitleView = itemView.findViewById(R.id.mtTitle);
            mtAuthorView = itemView.findViewById(R.id.mtAuthor);
            mtWeekView = itemView.findViewById(R.id.mtDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public MTAdapter(ArrayList<MessageModel> exampleList) {
        MTList = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mt_item, viewGroup, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mtListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder exampleViewHolder, int i) {
        MessageModel currentItem = MTList.get(i);

        exampleViewHolder.mtIconView.setImageResource(currentItem.getImageResource());
        exampleViewHolder.mtTitleView.setText(currentItem.getTitle());
        exampleViewHolder.mtAuthorView.setText(currentItem.getAuthor()+" ("+currentItem.getDate()+")");
        exampleViewHolder.mtWeekView.setText("Week "+currentItem.getWeek() + " - " + currentItem.getYear());
    }

    @Override
    public int getItemCount() {
        return MTList.size();
    }

}
