package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Message;

import java.util.List;

public class MessagesListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Message> mMessageList;
    private Long myId;

    public MessagesListAdapter(Context context, Long myId) {
        mContext = context;
        this.myId = myId;
    }

    public void setmMessageList(List<Message> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message m = mMessageList.get(position);

        if(m.userId.equals(myId))
            return VIEW_TYPE_MESSAGE_SENT;
        else return VIEW_TYPE_MESSAGE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == VIEW_TYPE_MESSAGE_SENT){
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_message, parent, false);
            return new SentMessageHolder(view);
        }
        else if(viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.others_message, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_gchat_user_other);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_other);
            nameText = (TextView) itemView.findViewById(R.id.text_gchat_message_other);
            profileImage = (ImageView) itemView.findViewById(R.id.image_gchat_profile_other);
        }

        void bind(Message message) {
            messageText.setText(message.participantName);

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.date.toString());
            nameText.setText(message.text);

            // Insert the profile image from the URL into the ImageView.
            if(message.participantImageUrl!=null) Picasso.get().load(message.participantImageUrl).into(profileImage);
            else Picasso.get().load("https://joyonlineschool.com/static/emptyuserphoto.png").into(profileImage);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_me);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_me);
        }

        void bind(Message message) {
            messageText.setText(message.text);

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.date.toString());
        }
    }
}
