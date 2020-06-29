package com.yukisoft.hlcmt.JavaRepositories.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yukisoft.hlcmt.JavaRepositories.Models.AudioModel;
import com.yukisoft.hlcmt.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.CommentViewHolder> {
    private ArrayList<AudioModel> AudioList;
    private OnItemClickListener audioListener;
    private OnItemLongClickListener audioMenuListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
        void onMoreClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        audioListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position) throws IOException;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        audioMenuListener = listener;
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView audioIconView;
        TextView audioTitleView;
        TextView audioSpeakerView;
        TextView audioDatePreached;
        ImageView btnMore;

        CommentViewHolder(View itemView, final OnItemClickListener listener, final OnItemLongClickListener menuListener) {
            super(itemView);
            audioIconView = itemView.findViewById(R.id.audioIcon);
            audioTitleView = itemView.findViewById(R.id.audioTitle);
            audioSpeakerView = itemView.findViewById(R.id.audioSpeaker);
            audioDatePreached = itemView.findViewById(R.id.audioDate);
            btnMore = itemView.findViewById(R.id.btnAudioMore);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        try {
                            listener.onItemClick(position);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(v.getContext(), "Unable to show menu!\nPlease report error in 'Help' menu.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            btnMore.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onMoreClick(position);
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (menuListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        try {
                            menuListener.onItemLongClick(position);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(v.getContext(), "Unable to play message!\nPlease report error in 'Help' menu.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                return false;
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
        CommentViewHolder evh = new CommentViewHolder(v, audioListener, audioMenuListener);
        return evh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        AudioModel currentItem = AudioList.get(i);
        String pattern = "dd-MM-yyyy";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
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
