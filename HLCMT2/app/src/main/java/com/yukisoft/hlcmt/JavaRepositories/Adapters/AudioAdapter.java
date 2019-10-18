package com.yukisoft.hlcmt.JavaRepositories.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yukisoft.hlcmt.JavaRepositories.Models.AudioModel;
import com.yukisoft.hlcmt.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.CommentViewHolder> {
    private ArrayList<AudioModel> AudioList;
    private OnItemClickListener audioListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        audioListener = listener;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView audioIconView;
        public TextView audioTitleView;
        public TextView audioSpeakerView;
        public TextView audioDatePreached;

        public CommentViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            audioIconView = itemView.findViewById(R.id.audioIcon);
            audioTitleView = itemView.findViewById(R.id.audioTitle);
            audioSpeakerView = itemView.findViewById(R.id.audioSpeaker);
            audioDatePreached = itemView.findViewById(R.id.audioDate);

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

    public AudioAdapter(ArrayList<AudioModel> exampleList) {
        AudioList = exampleList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.audio_item, viewGroup, false);
        CommentViewHolder evh = new CommentViewHolder(v, audioListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        AudioModel currentItem = AudioList.get(i);
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(currentItem.getDatePreached());

        commentViewHolder.audioIconView.setImageResource(R.drawable.ic_audio);
        commentViewHolder.audioTitleView.setText(currentItem.getTitle());
        commentViewHolder.audioSpeakerView.setText("by " + currentItem.getSpeaker());
        commentViewHolder.audioDatePreached.setText(date);
    }

    @Override
    public int getItemCount() {
        return AudioList.size();
    }

}
