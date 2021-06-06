package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Chat;
import org.udg.pds.todoandroid.entity.Participant;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChatsRecyclerViewAdapter extends RecyclerView.Adapter<ChatsRecyclerViewAdapter.ChatListViewHolder> {

    List<Chat> chatList = new ArrayList<>();
    private Context context;

    public ChatsRecyclerViewAdapter(Context context){
        this.context = context;
    }


    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_chatlistitem,parent,false);
        return  new ChatsRecyclerViewAdapter.ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        holder.mItem = chatList.get(position);
        Participant p = null;
        Iterator<Participant> it = holder.mItem.participants.iterator();
        while (it.hasNext()) {
            p = it.next();
        }

        holder.mName.setText(p.user.username);
        //proviosnalment
        Picasso.get().load("https://joyonlineschool.com/static/emptyuserphoto.png").into(holder.mUserImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = ChatListFragmentDirections.actionChatMessage(holder.mItem.chatId);
                Navigation.findNavController(v).navigate(action);

            }
        });
    }

    public void setChatList(List<Chat> chatList) {
        this.chatList = chatList;
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        public final TextView mName;
        public final ImageView mUserImage;
        public Chat mItem;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mName = (TextView) itemView.findViewById(R.id.chatItemName);
            mUserImage = (ImageView) itemView.findViewById(R.id.chatItemImage);


        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
