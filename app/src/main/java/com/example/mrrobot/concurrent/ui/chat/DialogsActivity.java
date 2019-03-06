package com.example.mrrobot.concurrent.ui.chat;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.mrrobot.concurrent.Models.Dialog;
import com.example.mrrobot.concurrent.Models.Message;
import com.example.mrrobot.concurrent.Models.User;
import com.example.mrrobot.concurrent.R;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class DialogsActivity extends AppCompatActivity implements
        DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog> {


    protected ImageLoader imageLoader;
    protected DialogsListAdapter<Dialog> dialogsAdapter;
    private DialogsList dialogsList;
    private ChatViewModel chatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        setContentView(R.layout.activity_dialogs);
        dialogsList = (DialogsList) findViewById(R.id.dialogsList);
        initAdapter();

    }

    private void initAdapter() {
        dialogsAdapter = new DialogsListAdapter<>(imageLoader);
        // Dialogs
        dialogsAdapter.setItems(chatsTest());

        dialogsAdapter.setOnDialogClickListener(this);
        dialogsAdapter.setOnDialogLongClickListener(this);

        dialogsList.setAdapter(dialogsAdapter);
    }
    private  ArrayList<Dialog> chatsTest(){
        ArrayList<Dialog> chats = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        users.add(this.chatViewModel.user);
        users.add(this.chatViewModel.userTest);

        Message lastMessage = this.chatViewModel.message;

        Dialog dialog = new Dialog("rwer","wewrwr","perimer char",users,lastMessage,1);


        chats.add(dialog);

        return chats;
    }
    private void initImageLoader(){
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {

                Picasso.get().load(url).into(imageView);
            }
        };
    }


    @Override
    public void onDialogClick(Dialog dialog) {
        // init MessageActivity
        getApplicationContext()
                .startActivity(new Intent(getApplicationContext(), MessageActivity.class));
    }

    @Override
    public void onDialogLongClick(Dialog dialog) {

    }
}
