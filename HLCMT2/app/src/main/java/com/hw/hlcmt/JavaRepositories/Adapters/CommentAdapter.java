package com.hw.hlcmt.JavaRepositories.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hw.hlcmt.JavaRepositories.Models.CommentModel;
import com.hw.hlcmt.R;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private ArrayList<CommentModel> CommentList;
    private MTAdapter.OnItemClickListener commentListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MTAdapter.OnItemClickListener listener) {
        commentListener = listener;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView commentName;
        public TextView commentMessage;

        public CommentViewHolder(View itemView, final MTAdapter.OnItemClickListener listener) {
            super(itemView);
            commentName = itemView.findViewById(R.id.commentName);
            commentMessage = itemView.findViewById(R.id.commentText);

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

    public CommentAdapter(ArrayList<CommentModel> exampleList) {
        CommentList = exampleList;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item, viewGroup, false);
        CommentAdapter.CommentViewHolder evh = new CommentAdapter.CommentViewHolder(v, commentListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder commentViewHolder, int i) {
        CommentModel currentItem = CommentList.get(i);

        commentViewHolder.commentName.setText(currentItem.getUserName());
        commentViewHolder.commentMessage.setText(currentItem.getComment());
    }

    @Override
    public int getItemCount() {
        return CommentList.size();
    }

}