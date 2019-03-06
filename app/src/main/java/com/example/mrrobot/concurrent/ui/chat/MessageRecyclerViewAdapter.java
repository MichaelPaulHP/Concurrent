package com.example.mrrobot.concurrent.ui.chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mrrobot.concurrent.models.Message;
import com.example.mrrobot.concurrent.R;

import java.util.ArrayList;
import java.util.List;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {

    private List<Message> mMessages=new ArrayList<>();

    public MessageRecyclerViewAdapter() {
        super();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        int layout = -1;
        switch (viewType) {
            case Message.LOG_MESSAGE:
                layout = R.layout.log_message;
                break;
            case Message.ME_MESSAGE:
                layout = R.layout.me_message;
                break;
            case Message.YOUR_MESSAGE:
                layout = R.layout.your_message;
                break;
        }
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(layout, viewGroup, false);
        return new ViewHolder(v);

    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = mMessages.get(position);
        viewHolder.setMessage(message);
        //viewHolder.setUsername(message.getUsername());
    }
    @Override
    public int getItemCount() {
        if(this.mMessages==null){
            return 0;
        }
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).type;
    }

    public void addMessage(Message message) {
        mMessages.add(message);
        notifyItemInserted(mMessages.size() - 1);
    }

    /**
     *  view holder
     */
    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView usernameTextView;
        TextView timeTextView;
        TextView textTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textTextView=itemView.findViewById(R.id.text_message);
            this.timeTextView=itemView.findViewById(R.id.time_message);
            this.usernameTextView=itemView.findViewById(R.id.user_name);
        }
        public void setMessage(Message message){
            this.textTextView.setText(message.text);
            this.timeTextView.setText(message.text);
            this.usernameTextView.setText(message.userName);
        }
    }
}
