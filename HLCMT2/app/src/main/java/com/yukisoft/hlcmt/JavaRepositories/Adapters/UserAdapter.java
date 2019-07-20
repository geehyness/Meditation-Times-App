package com.yukisoft.hlcmt.JavaRepositories.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yukisoft.hlcmt.JavaRepositories.Models.UserModel;
import com.yukisoft.hlcmt.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<UserModel> UserList;
    private OnItemClickListener userListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        userListener = listener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public ImageView userIconView;
        public TextView userNameView;
        public TextView userEmailView;

        public UserViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            userIconView = itemView.findViewById(R.id.userListPic);
            userNameView = itemView.findViewById(R.id.listUserName);
            userEmailView = itemView.findViewById(R.id.listUserEmail);

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

    public UserAdapter(ArrayList<UserModel> exampleList) {
        UserList = exampleList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item, viewGroup, false);
        UserViewHolder evh = new UserViewHolder(v, userListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        UserModel currentUser = UserList.get(i);

        userViewHolder.userIconView.setImageResource(R.drawable.ic_profile);
        userViewHolder.userNameView.setText(currentUser.getName());
        userViewHolder.userEmailView.setText(currentUser.getEmail());

        if(currentUser.isAdmin() && currentUser.isWriter()){
            userViewHolder.userNameView.append(" (Admin & Writer)");
        } else if(currentUser.isAdmin() && !currentUser.isWriter()){
            userViewHolder.userNameView.append(" (Admin)");
        } else if(!currentUser.isAdmin() && currentUser.isWriter()){
            userViewHolder.userNameView.append(" (Writer)");
        }
    }

    @Override
    public int getItemCount() {
        return UserList.size();
    }

}
