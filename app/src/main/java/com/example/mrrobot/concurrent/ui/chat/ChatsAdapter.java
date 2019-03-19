package com.example.mrrobot.concurrent.ui.chat;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mrrobot.concurrent.models.Chat;
import com.example.mrrobot.concurrent.R;
import com.example.mrrobot.concurrent.databinding.LayoutChatCircleBinding;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private List<Chat> chats;
    private ClickListener clickListener;

    public ChatsAdapter(List<Chat> chats) {
        super();
        this.chats = chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutChatCircleBinding layoutChatCircleBinding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(viewGroup.getContext()),
                        R.layout.layout_chat_circle, viewGroup, false);
        return new ViewHolder(layoutChatCircleBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setChat(this.chats.get(i));
    }

    /**
     * notifyNewChatInserted
     *
     */
    public void notifyNewChatInserted() {

        notifyItemInserted(this.chats.size() - 1);
    }

    @Override
    public int getItemCount() {
        if (this.chats == null) {
            return 0;
        }
        return this.chats.size();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        /**
         * event on item's click
         *
         * @param position is list
         * @param v        is view
         */
        void onItemClick(int position, View v);
        //void onItemLongClick(int position, View v);
    }

    /**
     * view holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LayoutChatCircleBinding layoutChatCircleBinding;

        public ViewHolder(@NonNull LayoutChatCircleBinding itemView) {
            super(itemView.getRoot());
            View view = itemView.getRoot();
            view.setOnClickListener(this);
            this.layoutChatCircleBinding = itemView;
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

        public void setChat(Chat chat) {
            if (this.layoutChatCircleBinding.getChat() == null) {
                this.layoutChatCircleBinding.setChat(chat);
            }
        }

    }
}
