package com.example.mrrobot.concurrent.ui.chat;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mrrobot.concurrent.models.Chat;
import com.example.mrrobot.concurrent.models.Message;
import com.example.mrrobot.concurrent.models.MessagePrototypeFactory;
import com.example.mrrobot.concurrent.models.User;
import com.example.mrrobot.concurrent.ui.home.DestinationAdapter;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

public class ChatViewModel extends AndroidViewModel implements
        MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener,
        ChatsAdapter.ClickListener,
        User.IUserListeners {

    // prueba
    User user ;// this USER
    User userTest;
    //Chat chatTest = new Chat("testChat","idChatTest1","idMessagesTest","idUser");


    private MessagesListAdapter<Message> messagesAdapter;
    private ChatsAdapter chatsAdapter;

    private DestinationAdapter destinationAdapter;
    private ImageLoader imageLoader;
    private int indexChat=0;

    public ChatViewModel(Application application) {
        super(application);
        this.user=User.getCurrentUser();
        // listener user
        user.userListeners=this;
        initMessagesAdapter();
        initChatsAdapter();
        this.user.requestMyChats();

        // actualizar mis datos con los de DB
    }

    private void initChatsAdapter() {
        this.chatsAdapter = new ChatsAdapter(this.user.getMyChats());
        this.chatsAdapter.setOnItemClickListener(this);
        /*this.destinationAdapter = new DestinationAdapter();
        this.destinationAdapter.setDestinations(this.user.getMyDestinations());*/
    }

    private void initMessagesAdapter() {
        initImageLoader();
        this.messagesAdapter = new MessagesListAdapter<>(this.user.getGoogleId(), imageLoader);

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
        return this.user.getMyChats().get(0);
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
        Toast.makeText(this.getApplication().getApplicationContext(),position+"",Toast.LENGTH_LONG).show();
        /*if(this.indexChat!=position){
            Chat chat = this.user.getMyChats().get(position);
            this.messagesAdapter.clear();
            this.messagesAdapter.addToEnd(chat.getMessages(),true);
        }
        */

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
     * called when a new user join to chat
     *
     * @param chat
     * @param x
     */
    @Override
    public void onNewParticipant(Chat chat, User x) {

    }

    /**
     * when this user join to chat x
     *
     */
    @Override
    public void onNewChat() {
        this.chatsAdapter.notifyNewChatInserted();
        //this.destinationAdapter.notifyNewDestinationInserted();
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
