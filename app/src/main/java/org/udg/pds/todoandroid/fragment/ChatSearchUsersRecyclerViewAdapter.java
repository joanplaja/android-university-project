package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Chat;
import org.udg.pds.todoandroid.entity.CreateChat;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatSearchUsersRecyclerViewAdapter extends RecyclerView.Adapter<ChatSearchUsersRecyclerViewAdapter.ChatSearchUsersViewHolder> {

    List<User> users = new ArrayList<>();
    private Context context;
    private TodoApi mTodoService;

    public ChatSearchUsersRecyclerViewAdapter(Context context, TodoApi mTodoService){
        this.context = context;
        this.mTodoService = mTodoService;
    }


    @NonNull
    @Override
    public ChatSearchUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.chatuserssearch_item,parent,false);
        return  new ChatSearchUsersRecyclerViewAdapter.ChatSearchUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatSearchUsersViewHolder holder, int position) {
        holder.mItem = users.get(position);
        holder.mName.setText(holder.mItem.username);
        if(holder.mItem.imageUrl!=null)Picasso.get().load(holder.mItem.imageUrl).into(holder.mUserImage);
        else Picasso.get().load("https://joyonlineschool.com/static/emptyuserphoto.png").into(holder.mUserImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateChat chat = new CreateChat();
                chat.userId = holder.mItem.id;
                Call<Long> call = mTodoService.createChat(chat);

                call.enqueue(new Callback<Long>() {
                    @Override
                    public void onResponse(Call<Long> call, Response<Long> response) {
                        if(response.isSuccessful()){
                            Long id = response.body();
                            NavDirections action = ChatListFragmentDirections.actionChatMessage(id);
                            Navigation.findNavController(v).navigate(action);
                        }
                        else{
                            Toast toast = Toast.makeText(context, "Error creating chat", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Long> call, Throwable t) {
                        Toast toast = Toast.makeText(context, "Error creating chat", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

            }
        });
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ChatSearchUsersViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        public final TextView mName;
        public final ImageView mUserImage;
        public User mItem;

        public ChatSearchUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mName = (TextView) itemView.findViewById(R.id.userAddFriendName);
            mUserImage = (ImageView) itemView.findViewById(R.id.userAddFriendImage);


        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
