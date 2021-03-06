package com.yukisoft.hlcmt.JavaRepositories.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yukisoft.hlcmt.JavaRepositories.Fixed.Language;
import com.yukisoft.hlcmt.JavaRepositories.Models.MessageModel;
import com.yukisoft.hlcmt.R;

import java.util.ArrayList;

public class MTAdapter extends RecyclerView.Adapter<MTAdapter.CommentViewHolder> {
    private ArrayList<MessageModel> MTList;
    private OnItemClickListener mtListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mtListener = listener;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView mtIconView;
        public TextView mtTitleView;
        public TextView mtAuthorView;
        public TextView mtWeekView;

        public CommentViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mtIconView = itemView.findViewById(R.id.mtIcon);
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
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mt_item, viewGroup, false);
        CommentViewHolder evh = new CommentViewHolder(v, mtListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        MessageModel currentItem = MTList.get(i);

        if (currentItem.getLanguage().equals(Language.English.toString())){
            commentViewHolder.mtIconView.setImageResource(R.drawable.lang_en);
        } else if (currentItem.getLanguage().equals(Language.Siswati.toString())) {
            commentViewHolder.mtIconView.setImageResource(R.drawable.lang_ss);
        } else {
            commentViewHolder.mtIconView.setImageResource(R.drawable.ic_error);
        }
        commentViewHolder.mtTitleView.setText(currentItem.getTitle());
        commentViewHolder.mtAuthorView.setText("by "+currentItem.getAuthor());
        commentViewHolder.mtWeekView.setText("Week "+currentItem.getWeek() + " - " + currentItem.getYear());
    }

    @Override
    public int getItemCount() {
        return MTList.size();
    }

}
