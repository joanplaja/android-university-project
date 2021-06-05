package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mypopsy.widget.FloatingSearchView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Chat;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment {

    LinearLayout loadingLyt, chatsLayout, noChatsLayout;
    RecyclerView recyclerView;
    FloatingSearchView floatingSearchView;

    private ChatsRecyclerViewAdapter adapter;
    private ChatSearchUsersRecyclerViewAdapter adapterSearch;

    List<User> users;

    private List<Chat> chats;
    Context context;

    TodoApi mTodoService;

    public ChatListFragment() {
    }

    public static ChatListFragment newInstance(String param1, String param2) {
        ChatListFragment fragment = new ChatListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_list, container, false);

        context = rootView.getContext();

        users = new ArrayList<>();

        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();

        chats = new ArrayList<>();

        loadingLyt = rootView.findViewById(R.id.chatListLoadingLyt);
        chatsLayout = rootView.findViewById(R.id.chatsLyt);
        noChatsLayout = rootView.findViewById(R.id.NoChatsLyt);
        recyclerView = rootView.findViewById(R.id.chatRecyclerView);
        floatingSearchView = rootView.findViewById(R.id.floatingSearchView);

        adapterSearch = new ChatSearchUsersRecyclerViewAdapter(context,mTodoService);
        adapterSearch.setUsers(users);
        floatingSearchView.setAdapter(adapterSearch);


//        mSuggestionsAdapter = new MySuggestionsAdapter(); // use a RecyclerView.Adapter
//        mSearchView.setAdapter(mSuggestionsAdapter);

        floatingSearchView.setOnIconClickListener(new FloatingSearchView.OnIconClickListener() {
            @Override
            public void onNavigationClick() {
                System.out.println("on icon click");
            }
        });
        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSearchAction(CharSequence charSequence) {
                System.out.println("on search action:"+charSequence);
            }
        });
        floatingSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println("before text changed:");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("afterTextChanged:"+s.toString());

                Call<List<User>> call = mTodoService.searchUsersWithoutChat(s.toString());
                call.enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        if(response.isSuccessful()){
                            users = response.body();
                            adapterSearch.setUsers(users);
                            adapterSearch.notifyDataSetChanged();
                        }
                        else{
                            Toast toast = Toast.makeText(context, "Error searching users", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        Toast toast = Toast.makeText(context, "Error searching users", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        });
        floatingSearchView.setOnSearchFocusChangedListener(new FloatingSearchView.OnSearchFocusChangedListener() {
            @Override
            public void onFocusChanged(boolean b) {
                System.out.println("on focus changed "+ b);
                if(!b) {
                    chatsLayout.setVisibility(View.VISIBLE);
                    noChatsLayout.setVisibility(View.VISIBLE);
                }
                else{
                    chatsLayout.setVisibility(View.GONE);
                    noChatsLayout.setVisibility(View.GONE);
                }
            }
        });

        Call<List<Chat>> call = mTodoService.getChats();
        call.enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {

                if(response.isSuccessful()){
                    chats = response.body();

                    if(chats.size() == 0){
                        loadingLyt.setVisibility(View.GONE);
                        noChatsLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        System.out.println("quants"+chats.size());

                        adapter = new ChatsRecyclerViewAdapter(context);
                        recyclerView.setAdapter(adapter);
                        adapter.setChatList(chats);

                        loadingLyt.setVisibility(View.GONE);
                        chatsLayout.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    Toast toast = Toast.makeText(context, "Error loading chats", Toast.LENGTH_SHORT);
                    toast.show();
                    loadingLyt.setVisibility(View.GONE);
                    noChatsLayout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) {
                System.out.println(t.getMessage());
                Toast toast = Toast.makeText(context, "Error loading chats", Toast.LENGTH_SHORT);
                toast.show();
                loadingLyt.setVisibility(View.GONE);
                noChatsLayout.setVisibility(View.VISIBLE);
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
