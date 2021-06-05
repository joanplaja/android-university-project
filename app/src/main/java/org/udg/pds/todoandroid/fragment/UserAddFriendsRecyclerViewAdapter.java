package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Chat;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAddFriendsRecyclerViewAdapter extends RecyclerView.Adapter<UserAddFriendsRecyclerViewAdapter.UserAddFriendViewHolder> {

    private List<User> firendsList = new ArrayList<>();
    private Context context;
    private TodoApi mTodoService;

    public UserAddFriendsRecyclerViewAdapter(Context context,TodoApi mTodoService){
        this.context = context;
        this.mTodoService = mTodoService;
    }


    @Override
    public UserAddFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_useraddfriend,parent,false);
        return new UserAddFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserAddFriendViewHolder holder, int position) {
        holder.mItem = firendsList.get(position);
        holder.mName.setText(holder.mItem.username);
        if(holder.mItem.imageUrl!=null)Picasso.get().load(holder.mItem.imageUrl).into(holder.mUserImage);
        else Picasso.get().load("https://joyonlineschool.com/static/emptyuserphoto.png").into(holder.mUserImage);
        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<String> call = mTodoService.followUser(holder.mItem.id);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            Toast toast = Toast.makeText(context, "Successfuly followed", Toast.LENGTH_SHORT);
                            View view = toast.getView();
                            view.setBackgroundColor(R.drawable.tab_color);
                            TextView text = (TextView) view.findViewById(android.R.id.message);
                            text.setTextColor(Color.parseColor("#FFFFFF"));
                            toast.show();
                            firendsList.remove(position);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(context, "Error on following", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return firendsList.size();
    }

    public void setFirendsList(List<User> firendsList) {
        this.firendsList = firendsList;
        notifyDataSetChanged();
    }

    public static class UserAddFriendViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mName;
        public final ImageView mUserImage;
        public final Button followButton;
        public User mItem;

        public UserAddFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            followButton = (Button) itemView.findViewById(R.id.userAddFriendFollow);
            mName = (TextView) itemView.findViewById(R.id.userAddFriendName);
            mUserImage = (ImageView) itemView.findViewById(R.id.userAddFriendImage);


        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
