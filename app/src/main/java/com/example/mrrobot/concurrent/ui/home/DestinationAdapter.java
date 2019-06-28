package com.example.mrrobot.concurrent.ui.home;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mrrobot.concurrent.R;
import com.example.mrrobot.concurrent.databinding.DestinationBinding;
import com.example.mrrobot.concurrent.models.Destination;

import java.util.List;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.DestinationAdapterViewHolder> {


    private List<Destination> destinations;


    public DestinationAdapter() {

    }


    @NonNull
    @Override
    public DestinationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        DestinationBinding layoutDeviceBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.destination,
                        parent, false);

        return new DestinationAdapterViewHolder(layoutDeviceBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationAdapterViewHolder holder, int position) {

        holder.bindDevice(destinations.get(position));
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }

    /**
     * notifyNewChatInserted
     *
     */
    public void notifyNewDestinationInserted() {
        notifyItemInserted(this.destinations.size() - 1);
    }

    public interface ClickListener {
        /**
         * event on item's click
         *
         * @param position is list
         * @param destination     is a chat
         */
        void onItemClick(int position, Destination destination);
        //void onItemLongClick(int position, View v);
    }

    @Override
    public int getItemCount() {
        return this.destinations == null ? 0 : this.destinations.size();
    }


    public class DestinationAdapterViewHolder extends RecyclerView.ViewHolder  {
        private DestinationBinding destinationBinding;

        public DestinationAdapterViewHolder(DestinationBinding destinationBinding) {
            super(destinationBinding.getRoot());
            this.destinationBinding = destinationBinding;

        }

        public void bindDevice(Destination destination) {
            if (this.destinationBinding.getDestination() == null) {
                this.destinationBinding.setDestination(destination);
            }

        }


    }
}