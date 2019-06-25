package com.example.mrrobot.concurrent.ui.chat;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.mrrobot.concurrent.Firebase.DB.ChatData;
import com.example.mrrobot.concurrent.models.Chat;
import com.example.mrrobot.concurrent.models.Message;
import com.example.mrrobot.concurrent.models.MessagePrototypeFactory;
import com.example.mrrobot.concurrent.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class ChatViewModel extends ViewModel implements
        MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener,
        ChatsAdapter.ClickListener,
        Chat.IChatListener,
        User.IUserListeners {

    // prueba
    User user ;// this USER
    User userTest;
    //Chat chatTest = new Chat("testChat","idChatTest1","idMessagesTest","idUser");


    ObservableField<Integer> count = new ObservableField<>(0);
    private MessagesListAdapter<Message> messagesAdapter;
    private ChatsAdapter chatsAdapter;
    private ImageLoader imageLoader;
    private int indexChat=0;
    public ChatViewModel() {

        this.user=User.getCurrentUser();
        // listener user
        user.userListeners=this;
        user.chatListener=this;
        initMessagesAdapter();
        initChatsAdapter();
        this.user.requestMyChats();
        // actualizar mis datos con los de DB
    }

    private void initChatsAdapter() {
        this.chatsAdapter = new ChatsAdapter(this.user.getMyChats());
        this.chatsAdapter.setOnItemClickListener(this);

    }

    private void initMessagesAdapter() {
        initImageLoader();
        this.messagesAdapter = new MessagesListAdapter<>("0", imageLoader);

//        messagesAdapter.enableSelectionMode(this);
//        messagesAdapter.setLoadMoreListener(this);
//        messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
//                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
//                    @Override
//                    public void onMessageViewClick(View view, Message message) {
//                        AppUtils.showToast(DefaultMessagesActivity.this,
//                                message.getUser().getName() + " avatar click",
//                                false);
//                    }
//                });
    }

    private void createChatAndJoin() {
        Random random = new Random();
        String name="MY_CHAT"+random.nextInt();
        final Chat chat = new Chat(name,this.user.getIdGoogle());
        chat.chatListener = this;
        this.user.saveChatAndJoint(chat);
    }

    /**
     * get the chat current
     *
     * @return chat selected
     */
    private Chat getCurrentChat() {
        return this.user.getMyChats().get(0);
    }


    private void showMessages(Chat chat) {
        this.messagesAdapter.addToEnd(chat.getMessages(), true);
    }


    private Message testCreateMessage() {
        Message message = MessagePrototypeFactory.getPrototype("meMessage");
        Calendar calendar = Calendar.getInstance();
        message.setText("textcreateMessage");
        return message;
    }


    public void testCreateChatAndJoin() {
        this.createChatAndJoin();
    }

    // call on click
    /*public void testSaveMessage() {
        // TEST show messages
        Message messageOfTestUser = testCreateMessage();
        Chat.saveMessage(getCurrentChat(), messageOfTestUser);
    }*/

    public void testCreateUser(){
        this.user.save();
        //Chat.saveUser(getCurrentChat().getIdParticipants(),userTest);
    }


    /////////////////////////////////////////////////////////////
    ////////// MESSAGE LISTENERS
    //////////////////////////////////////////////////////////

    /**
     * Fires when user presses 'add' button.
     */
    @Override
    public void onAddAttachments() {

    }

    /**
     * Fires when user presses 'send' button.
     *
     * @param input input entered by user
     * @return if input text is valid, you must return {@code true} and input will be cleared, otherwise return false.
     */
    @Override
    public boolean onSubmit(CharSequence input) {

        Chat chatCurrent=getCurrentChat();
        Message message= MessagePrototypeFactory.getPrototype("myMessage");
        message.setText(input.toString());
        message.setCreateAt(Chat.getNowDate());
        chatCurrent.saveMessage(message);

        return true;
    }

    /**
     * Fires when user presses start typing
     */
    @Override
    public void onStartTyping() {

    }

    /**
     * Fires when user presses stop typing
     */
    @Override
    public void onStopTyping() {

    }


    /////////////////////////////////////////////////////////////
    ////////// END MESSAGE LISTENERS
    ////////////////////////

    /////////////////////////////////////////////////////////////
    ////////// CHAT ADAPTER LISTENERS
    //////////////////////////////////////////////////////////


    /**
     * event on item's click
     *
     * @param position is list
     * @param v        is view
     */
    @Override
    public void onItemClick(int position, View v) {
        if(this.indexChat!=position){
            Chat chat = this.user.getMyChats().get(position);
            this.messagesAdapter.clear();
            this.messagesAdapter.addToEnd(chat.getMessages(),true);
        }

        // show messages if
        //showMessages(this.user.myChats.get(position));


        //messagesAdapter.addToStart(messageOfTestUser, true);
    }


    /////////////////////////
    ////////// END  CHAT ADAPTER LISTENERS
    ////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////
    //////////////////////// CHILD LISTENERS
    ///////////////////

    /////////////////////////////////////////////////////////////
    ////////// CHAT AND USER LISTENERS
    ///////////////////////////////////

    /**
     * new message x in chat chat
     *
     * @param chat chat
     * @param x    message
     */
    @Override
    public void onNewMessage(Chat chat, Message x) {
        // if message is for current chat : show
        // else notification of new message 1
        this.messagesAdapter.addToStart(x, true);
    }


    /**
     * when this user join to chat x
     *
     */
    @Override
    public void onJoinToChat() {
        this.chatsAdapter.notifyNewChatInserted();
    }


    /////////////////////////
    ////////// END  CHAT AND USER LISTENERS
    ////////////////////////////////////////////////////////////


    public MessagesListAdapter<Message> getMessagesAdapter() {
        return messagesAdapter;
    }

    public ChatsAdapter getChatsAdapter() {
        return chatsAdapter;
    }




    private void initImageLoader() {
        this.imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Picasso.get().load(url).into(imageView);
            }
        };
    }



}
