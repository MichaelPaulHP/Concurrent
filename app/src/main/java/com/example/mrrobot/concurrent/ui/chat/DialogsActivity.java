package com.example.mrrobot.concurrent.ui.chat;

import android.arch.lifecycle.ViewModelProviders;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.mrrobot.concurrent.R;

import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;


public class DialogsActivity extends AppCompatActivity {

    // Attributes

    private ChatViewModel chatViewModel; // this is ViewModel
    //  views
    private RecyclerView recyclerViewListChats;// chats' list
    private MessagesList messagesList; // list of messages
    // End Attributes


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        setContentView(R.layout.activity_dialogs);

        initRecyclerViewOfChatsList();
        initListOfMessages();
        initMessageInput();

    }


    private void initRecyclerViewOfChatsList() {

        this.recyclerViewListChats = findViewById(R.id.recyclerViewListChats);
        //this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()), LinearLayoutManager.HORIZONTAL, false);
        this.recyclerViewListChats.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        this.recyclerViewListChats.setAdapter(this.chatViewModel.getChatsAdapter());

    }

    private void initListOfMessages() {
        this.messagesList = (MessagesList) findViewById(R.id.messagesList);
        this.messagesList.setAdapter(this.chatViewModel.getMessagesAdapter());
    }

    private void initMessageInput() {
        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this.chatViewModel);
        input.setTypingListener(this.chatViewModel);
        input.setAttachmentsListener(this.chatViewModel);
    }


}
