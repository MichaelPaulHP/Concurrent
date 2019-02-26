package com.example.mrrobot.concurrent.ui.chat;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import com.example.mrrobot.concurrent.Models.Chat;
import com.example.mrrobot.concurrent.R;

public class ChatFragment extends Fragment
        implements View.OnClickListener,
        ChatViewModel.ChatRoomListener {

    private ChatViewModel mViewModel;
    private RecyclerView recyclerView;
    private MessageRecyclerViewAdapter messageAdapter;
    private Button btnTest;
    private Button btnCreate;
    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        this.recyclerView = view.findViewById(R.id.recyclerViewChat);
        // BUTTON TEST
        this.btnTest = view.findViewById(R.id.test_button);
        this.btnCreate = view.findViewById(R.id.testCreate_button);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // VIEW MODEL
        mViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        mViewModel.setChatRoomListener(this);


        this.btnTest.setOnClickListener(this);
        this.btnCreate.setOnClickListener(this);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View view) {
        int id =view.getId();
        switch (id){
            case R.id.test_button:
                this.mViewModel.TEST();
                break;
            case R.id.testCreate_button:
                this.mViewModel.createChat();
                break;
        }

    }


    @Override
    public void onNewChat(Chat chat) {
        this.messageAdapter = new MessageRecyclerViewAdapter();
        //this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()), LinearLayoutManager.HORIZONTAL, false);
        this.recyclerView.setLayoutManager( new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        this.recyclerView.setAdapter(this.messageAdapter);
        // ser adapter
        mViewModel.setMessageAdapter(this.messageAdapter);
    }


}
