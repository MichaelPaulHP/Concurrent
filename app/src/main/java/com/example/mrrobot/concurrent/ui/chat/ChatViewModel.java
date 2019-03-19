package com.example.mrrobot.concurrent.ui.chat;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.mrrobot.concurrent.models.Chat;
import com.example.mrrobot.concurrent.models.Message;
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

    ChatRoomListener chatRoomListener;


    ObservableField<Integer> count = new ObservableField<>(0);
    private MessagesListAdapter<Message> messagesAdapter;
    private ChatsAdapter chatsAdapter;
    private ImageLoader imageLoader;

    public ChatViewModel() {

        this.user=User.getCurrentUser();
        user.listeners = this; // listener
        User.findAndUpdateOrSave(this.user);

        initMessagesAdapter();
        initChatsAdapter();
    }


    private void createChat() {
        final Chat chat = new Chat();
        chat.chatListener = this;
        Chat.saveChatAndAddUser("first chat", this.user, chat);

//
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                addChat(chat);
//                chat.addMessagesListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        //{ key = -L_P69pGl7VJcXTzmbY8, value = {createdAtLong=1551995022664, text=created byCURRENT USER, userName=CURRENT USER, userIdGoogle=0} } s: null
//                        // key = -L_P69pGl7VJcXTzmbY8 is idMessage
//                        Log.i("CHAT","dataSnapshot:"+dataSnapshot.toString()+" s: "+s);
//                        Message message=dataSnapshot.getValue(Message.class);
//                        // set message ID
//                        // set user
//                        message.setCreateAtFromLong(message.createAtLong);
//                        User userEmit=chat.findUserById(message.userIdGoogle);
//                        message.setUser(userEmit);
//                        chat.addMessage(message);
//                        messagesAdapter.addToStart(message,true);
//
//                    }
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) { }
//                });
//                chat.addUsersListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        User user=dataSnapshot.getValue(User.class);
//                        // user with name and avatar
//                        user.setId(dataSnapshot.getKey()); // set firebase's key
//                        chat.addUser(user);
//                        // message for all
//                        Chat.saveMessage(chat.idMessages,new Message("hola me uni",user,Chat.getNowDate()));
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) { }
//                });
//                //ChatViewModel.this.chatRoomListener.onNewChat(chat);
//            }
//        });

    }

    private void initChatsAdapter() {
        this.chatsAdapter = new ChatsAdapter(this.user.myChats);
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

    /**
     * get the chat current
     *
     * @return chat selected
     */
    private Chat getCurrentChat() {
        return this.user.myChats.get(0);
    }


    private void showMessages(Chat chat) {
        this.messagesAdapter.addToEnd(chat.getMessages(), true);
    }


    public void setChatRoomListener(ChatRoomListener chatRoomListener) {
        this.chatRoomListener = chatRoomListener;
    }

    private Message testCreateMessage() {
        Calendar calendar = Calendar.getInstance();
        Message message = new Message("message de userTest", this.userTest);
        return message;
    }

    public void testCreateChat() {
        this.createChat();
    }

    // call on click
    public void testSaveMessage() {
        // TEST show messages
        Message messageOfTestUser = testCreateMessage();
        Chat.saveMessage(getCurrentChat(), messageOfTestUser);
    }

    /*public void testCreateUser(){
        Chat.saveUser(getCurrentChat().getIdParticipants(),userTest);
    }*/


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

        Calendar calendar = Calendar.getInstance();
        Message message = new Message(input.toString(), user);

        Chat chatCurrent = getCurrentChat();
        Chat.saveMessage(chatCurrent, message);
        //messagesAdapter.addToStart(message, true);
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
        // show messages if
        showMessages(this.user.myChats.get(position));


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
    //////////////////////////////////////////////////////////

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
     * @param x chat saved in DB
     */
    @Override
    public void onJoinToChat(Chat x) {
        if (x == null) {
            this.chatsAdapter.notifyDataSetChanged();
        } else {
            this.chatsAdapter.notifyNewChatInserted();
        }
    }


    /////////////////////////
    ////////// CHAT AND USER LISTENERS
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

    public interface ChatRoomListener {
        void onNewChat(Chat chat);
    }


}
