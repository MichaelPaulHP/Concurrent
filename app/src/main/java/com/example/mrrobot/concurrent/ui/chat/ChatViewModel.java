package com.example.mrrobot.concurrent.ui.chat;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.mrrobot.concurrent.models.Chat;
import com.example.mrrobot.concurrent.models.Dialog;
import com.example.mrrobot.concurrent.models.Message;
import com.example.mrrobot.concurrent.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatViewModel extends ViewModel implements
        MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener,
        ChatsAdapter.ClickListener
{

    List<Chat> chats = new ArrayList<>();
    User user = new User("CURRENT USER","0","http://i.imgur.com/pv1tBmT.png");
    User userTest = new User("test User","1","http://i.imgur.com/R3Jm1CL.png");
    Message message = new Message("id",userTest,"firstMEssage", Calendar.getInstance().getTime());
    Chat chatTest = new Chat("testChat","idChatTest1","idMessagesTest","idUser");
    MessageRecyclerViewAdapter messageAdapter;
    ChatRoomListener chatRoomListener;
    // prueba

    ObservableField<Integer> count= new ObservableField<>(0);

    private MessagesListAdapter<IMessage> messagesAdapter;
    private ChatsAdapter chatsAdapter;
    private ImageLoader imageLoader;

    public ChatViewModel() {

        initMessagesAdapter();
        initChatsAdapter();
    }


    public void createChat(){
        Chat chat = Chat.save("first chat",this.user);
        chat.addUserToList(this.user);

        this.chats.add(chat);
        // why am I had a list of messages
        // on create chat update UI
        this.chatRoomListener.onNewChat(chat);

        chat.addMessagesListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // { key = -LZbgTSxYE_Wc87bXZSp, value = {text=hola, time=1551149164415, type=1, userName=CURRENT USER} }
                Message message = dataSnapshot.getValue(Message.class);

                Log.i("CHAT","onChildAdded"+ message.toString());
                messageAdapter.addMessage(message);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("CHAT","onChildChanged"+ dataSnapshot.toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void initChatsAdapter(){
        this.chatsAdapter = new ChatsAdapter(this.chats);
        this.chatsAdapter.setOnItemClickListener(this);
    }

    private void initMessagesAdapter(){
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



    public void TEST(){
        Chat prueba= this.chats.get(0);
        prueba.saveMessage(new Message("hola",this.user.getUserName(),Message.ME_MESSAGE));
        // prueba.addMessageToList();
        prueba.saveMessage(new Message("Mundo HOLA",this.user.getUserName(),Message.ME_MESSAGE));
        User qwe = new User("MY NAME","MY ID","http://i.imgur.com/R3Jm1CL.png");
        prueba.saveUser(qwe);
        prueba.addUserToList(qwe);
        prueba.saveMessage(new Message("hola q hace",qwe.getUserName(),Message.YOUR_MESSAGE));
        prueba.saveMessage(new Message("dime",this.user.getUserName(),Message.ME_MESSAGE));
    }
    private  ArrayList<Dialog> chatsTest(){
        ArrayList<Dialog> chats = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        users.add(this.user);
        users.add(this.userTest);

        Message lastMessage = this.message;

        Dialog dialog = new Dialog("rwer","wewrwr","perimer char",users,lastMessage,1);


        chats.add(dialog);

        return chats;
    }
    public void testCreateChat(){

        this.chatsAdapter.addChat(chatTest);
    }

    public void setMessageAdapter(MessageRecyclerViewAdapter messageAdapter) {
        this.messageAdapter = messageAdapter;
    }




    public void setChatRoomListener(ChatRoomListener chatRoomListener) {
        this.chatRoomListener = chatRoomListener;
    }

    public Message createMessage(){
        Calendar  calendar= Calendar.getInstance();
        Message message = new Message("werweID",this.userTest,"message de userTest",calendar.getTime());
        return message;
    }

    public void increment(){
        Integer i=this.count.get();
        this.count.set(i+1);
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
        Calendar calendar = Calendar.getInstance();
        Message message = new Message("5566",user,input.toString(),calendar.getTime());
        messagesAdapter.addToStart(message, true);
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
        chatTest.persons.set("1/1");
        // show messages
        Message messageOfTestUser = createMessage();
        messagesAdapter.addToStart(messageOfTestUser, true);

    }
    /////////////////////////////////////////////////////////////
    ////////// END  CHAT ADAPTER LISTENERS
    ////////////////////////

    public MessagesListAdapter<IMessage> getMessagesAdapter() {
        return messagesAdapter;
    }

    public ChatsAdapter getChatsAdapter() {
        return chatsAdapter;
    }

    private void initImageLoader(){
        this.imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Picasso.get().load(url).into(imageView);
            }
        };
    }

    public interface ChatRoomListener{
        void onNewChat(Chat chat);
    }


}
