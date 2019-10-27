package com.yukisoft.hlcmt.JavaRepositories.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yukisoft.hlcmt.JavaRepositories.Models.AudioCollectionModel;
import com.yukisoft.hlcmt.R;

import java.io.IOException;
import java.util.ArrayList;

public class AudioCollectionListAdapter extends RecyclerView.Adapter<AudioCollectionListAdapter.CommentViewHolder> {
    private ArrayList<AudioCollectionModel> CatList;
    private AudioCollectionListAdapter.OnItemClickListener catListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
    }

    public void setOnItemClickListener(AudioCollectionListAdapter.OnItemClickListener listener) {
        catListener = listener;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView catIconView;
        public TextView catNameView;
        public TextView catDetailsView;

        public CommentViewHolder(View itemView, final AudioCollectionListAdapter.OnItemClickListener listener) {
            super(itemView);
            catIconView = itemView.findViewById(R.id.collectionIcon);
            catNameView = itemView.findViewById(R.id.collectionTitle);
            catDetailsView = itemView.findViewById(R.id.collectionDetails);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            try {
                                listener.onItemClick(position);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(v.getContext(), "Unable to play message!\nPlease report error in 'Help' menu.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    }

    public AudioCollectionListAdapter(ArrayList<AudioCollectionModel> exampleList) {
        CatList = exampleList;
    }

    @NonNull
    @Override
    public AudioCollectionListAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cat_list_item, viewGroup, false);
        AudioCollectionListAdapter.CommentViewHolder evh = new AudioCollectionListAdapter.CommentViewHolder(v, catListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull AudioCollectionListAdapter.CommentViewHolder commentViewHolder, int i) {
        AudioCollectionModel currentItem = CatList.get(i);

        commentViewHolder.catIconView.setImageResource(R.drawable.ic_audio);
        commentViewHolder.catNameView.setText(currentItem.getName());
        commentViewHolder.catDetailsView.setText(currentItem.getDetails());
    }

    @Override
    public int getItemCount() {
        return CatList.size();
    }

}
