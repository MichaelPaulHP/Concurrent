package com.example.mrrobot.concurrent.ui.chat;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mrrobot.concurrent.Models.Message;
import com.example.mrrobot.concurrent.R;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MessageActivity extends AppCompatActivity
        implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener,
         MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener
{
    private static final int TOTAL_MESSAGES_COUNT = 20;
    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;
    private int selectionCount;
    private MessagesList messagesList;
    private ChatViewModel  chatViewModel;
    private Button btnTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initImageLoader();
        this.messagesList = (MessagesList) findViewById(R.id.messagesList);
        this.btnTest=findViewById(R.id.btnMessageOfTest);
        initAdapter();
        initMessageInput();

        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);

        this.btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message messageOfTestUser = chatViewModel.createMessage();
                messagesAdapter.addToStart(messageOfTestUser, true);

            }
        });
    }




    @Override
    protected void onStart() {
        super.onStart();
        // no se wey
        Message message= this.chatViewModel.message ;

        messagesAdapter.addToStart(message, true);
    }

    private void initImageLoader(){
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Picasso.get().load(url).into(imageView);
            }
        };
    }
    private void initAdapter() {
        messagesAdapter = new MessagesListAdapter<>("0", imageLoader);
        messagesAdapter.enableSelectionMode(this);
        messagesAdapter.setLoadMoreListener(this);
//        messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
//                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
//                    @Override
//                    public void onMessageViewClick(View view, Message message) {
//                        AppUtils.showToast(DefaultMessagesActivity.this,
//                                message.getUser().getName() + " avatar click",
//                                false);
//                    }
//                });

        this.messagesList.setAdapter(messagesAdapter);
    }
    private void initMessageInput() {
        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setTypingListener(this);
        input.setAttachmentsListener(this);
    }


    @Override
    public void onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesAdapter.unselectAllItems();
        }
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        Log.i("CHAT", "onLoadMore: " + page + " " + totalItemsCount);
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages();
        }
    }
    protected void loadMessages() {
//        new Handler().postDelayed(new Runnable() { //imitation of internet connection
//            @Override
//            public void run() {
//                ArrayList<Message> messages = MessagesFixtures.getMessages(lastLoadedDate);
//                lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
//                messagesAdapter.addToEnd(messages, false);
//            }
//        }, 1000);

    }

    @Override
    public void onSelectionChanged(int count) {
            this.selectionCount = count;
//        menu.findItem(R.id.action_delete).setVisible(count > 0);
//        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }

    ////////////////////////////////
    // Message Input Listens

    /**
     * Fires when user presses 'add' button.
     */
    @Override
    public void onAddAttachments() {
       /* messagesAdapter.addToStart(
                MessagesFixtures.getImageMessage(), true);*/
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
        Message message = new Message("5566",chatViewModel.user,input.toString(),calendar.getTime());
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

    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
        return new MessagesListAdapter.Formatter<Message>() {
            @Override
            public String format(Message message) {
                String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                        .format(message.getCreatedAt());

                String text = message.getText();
                if (text == null) text = "[attachment]";

                return String.format(Locale.getDefault(), "%s: %s (%s)",
                        message.getUser().getName(), text, createdAt);
            }
        };
    }
}
