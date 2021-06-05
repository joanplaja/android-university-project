package org.udg.pds.todoandroid.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.LineRadarDataSet;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.activity.Register;
import org.udg.pds.todoandroid.entity.Message;
import org.udg.pds.todoandroid.entity.SendMessage;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    LinearLayout loadingLyt;
    ConstraintLayout chatLyt;
    RecyclerView recyclerView;
    Button send;
    EditText eText;

    Long chatId;

    private MessagesListAdapter messagesListAdapter;

    private List<Message> messages;
    Context context;

    TodoApi mTodoService;

    private BroadcastReceiver mMessageReceiver;

    public ChatFragment() {
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("oncreate chat fragment");
        super.onCreate(savedInstanceState);

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String title = intent.getExtras().getString("title");
                String body = intent.getExtras().getString("body");
                String type = intent.getExtras().getString("type");

                System.out.println("type fragment chat:"+type);

                if(type.equals("notification")){
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_personalizado,
                        (ViewGroup) getActivity().findViewById(R.id.custom_toast_container));

                    TextView messageToast = (TextView) layout.findViewById(R.id.text);

                    String text = title + body;

                    messageToast.setText(text);

                    Toast toast = new Toast(getActivity());

                    toast.setGravity(Gravity.TOP, 0, 0);

                    toast.setDuration(Toast.LENGTH_LONG);

                    toast.setView(layout);

                    toast.show();
                }
                else{

                    String userId = intent.getExtras().getString("userId");
                    String img = intent.getExtras().getString("img");
                    String messageId = intent.getExtras().getString("messageId");

                    Message m = new Message();
                    m.userId = Long.valueOf(userId);
                    m.date = new Date();
                    m.text = body;
                    m.participantName = title;
                    m.participantImageUrl = img;
                    m.messageId = Long.valueOf(messageId);

                    messages.add(m);
                    messagesListAdapter.setmMessageList(messages);
                    messagesListAdapter.notifyDataSetChanged();
                    if(messages.size()!=0)recyclerView.smoothScrollToPosition(messages.size()-1);
                }

            }
        };


        if (getArguments() != null) {
            chatId = getArguments().getLong("chat-id",-1);
        }
        System.out.println("chat id:"+chatId);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((mMessageReceiver),
            new IntentFilter("Notification Data")
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        context = rootView.getContext();

        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();

        messages = new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_chat);
        loadingLyt = rootView.findViewById(R.id.chatMessagesLoading);
        chatLyt = rootView.findViewById(R.id.chatMessagesLyt);
        send = rootView.findViewById(R.id.button_chat_send);
        eText = rootView.findViewById(R.id.edit_chat_message);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eText.getText().toString() == null){
                    Toast toast = Toast.makeText(context, "Can't send empty message", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    SendMessage m = new SendMessage();
                    m.message = eText.getText().toString();
                    Call<Message> call = mTodoService.sendMessage(chatId,m);

                    call.enqueue(new Callback<Message>() {
                        @Override
                        public void onResponse(Call<Message> call, Response<Message> response) {
                            if(response.isSuccessful()){
                                Message m = response.body();
                                messages.add(m);
                                messagesListAdapter.setmMessageList(messages);
                                messagesListAdapter.notifyDataSetChanged();
                                if(messages.size()!=0)recyclerView.smoothScrollToPosition(messages.size()-1);

                                eText.setText("");
                            }
                            else{
                                Toast toast = Toast.makeText(context, "Error sending message", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Message> call, Throwable t) {
                            Toast toast = Toast.makeText(context, "Error sending message", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            }
        });


        Call<List<Message>> call = mTodoService.getMessages(chatId);

        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {

                if(response.isSuccessful()){
                    messages = response.body();

                    LinearLayoutManager l = new LinearLayoutManager(context);
                    l.setReverseLayout(false);
                    recyclerView.setLayoutManager(l);

                    Call<User> callUser = mTodoService.getUserMe();
                    callUser.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if(response.isSuccessful()){
                                messagesListAdapter = new MessagesListAdapter(context,response.body().id);
                                messagesListAdapter.setmMessageList(messages);
                                recyclerView.setAdapter(messagesListAdapter);
                                messagesListAdapter.notifyDataSetChanged();
                                if(messages.size()!=0)recyclerView.smoothScrollToPosition(messages.size()-1);

                                loadingLyt.setVisibility(View.GONE);
                                chatLyt.setVisibility(View.VISIBLE);
                            }
                            else{
                                System.out.println("error getting user id");
                                Toast toast = Toast.makeText(context, "Error getting user information", Toast.LENGTH_SHORT);
                                toast.show();
                                loadingLyt.setVisibility(View.GONE);
                                chatLyt.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            System.out.println("error getting user id onfaliure");
                            Toast toast = Toast.makeText(context, "Error getting user information", Toast.LENGTH_SHORT);
                            toast.show();
                            loadingLyt.setVisibility(View.GONE);
                            chatLyt.setVisibility(View.VISIBLE);
                        }
                    });
                }
                else{
                    System.out.println("error getting messages");
                    Toast toast = Toast.makeText(context, "Error loading messages", Toast.LENGTH_SHORT);
                    toast.show();
                    loadingLyt.setVisibility(View.GONE);
                    chatLyt.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                System.out.println("error getting messages onfailure");
                Toast toast = Toast.makeText(context, "Error loading messages", Toast.LENGTH_SHORT);
                toast.show();
                loadingLyt.setVisibility(View.GONE);
                chatLyt.setVisibility(View.VISIBLE);
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });

        return  rootView;
    }
}
