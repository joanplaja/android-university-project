package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactAddFriendsRecyclerViewAdapter extends RecyclerView.Adapter<UserAddFriendsRecyclerViewAdapter.UserAddFriendViewHolder> {

    List<User> contactList = new ArrayList<>();
    private Context context;
    private TodoApi mTodoService;

    public ContactAddFriendsRecyclerViewAdapter(Context context,TodoApi mTodoService){
        this.context = context;
        this.mTodoService = mTodoService;
    }

    @NonNull
    @Override
    public UserAddFriendsRecyclerViewAdapter.UserAddFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_useraddfriend,parent,false);
        return new UserAddFriendsRecyclerViewAdapter.UserAddFriendViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UserAddFriendsRecyclerViewAdapter.UserAddFriendViewHolder holder, int position) {
        holder.mItem = contactList.get(position);
        holder.mName.setText(holder.mItem.username);
        if(holder.mItem.imageUrl!=null) Picasso.get().load(holder.mItem.imageUrl).into(holder.mUserImage);
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
                            contactList.remove(position);
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

    public void setContactList(List<User> contactList) {
        this.contactList = contactList;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
