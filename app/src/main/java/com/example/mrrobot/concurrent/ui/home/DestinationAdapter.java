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
    private IEventListener iEventListener;

    public DestinationAdapter() {

    }


    @NonNull
    @Override
    public DestinationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        DestinationBinding layoutDeviceBinding =
                DataBindingUtil.inflate
                        (
                        LayoutInflater.from(parent.getContext()),
                        R.layout.destination,
                        parent,
                        false
                        );


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
        this.notifyDataSetChanged();
        /*if(this.destinations.size()==0)
            notifyItemInserted(1);
        else
            notifyItemInserted(this.destinations.size() - 1);*/
    }

    public void setEventListener(IEventListener iEventListener) {
        this.iEventListener = iEventListener;
    }

    public interface IEventListener {
        /**
         * Called when a Destination layout has been clicked
         *
         * @param position in list
         * @param view The view that was clicked.
         */
        void onDestinationClick(int position, View view);
        //void onItemLongClick(int position, View v);
    }

    @Override
    public int getItemCount() {
        return this.destinations == null ? 0 : this.destinations.size();
    }


    public class DestinationAdapterViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {
        private DestinationBinding destinationBinding;

        public DestinationAdapterViewHolder(DestinationBinding destinationBinding) {
            super(destinationBinding.getRoot());
            View root=destinationBinding.getRoot();
            root.setOnClickListener(this);
            this.destinationBinding = destinationBinding;

        }

        public void bindDevice(Destination destination) {
            if (this.destinationBinding.getDestination() == null) {
                this.destinationBinding.setDestination(destination);
            }

        }
        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            if(iEventListener!=null){
            iEventListener.onDestinationClick(getAdapterPosition(),view);
            }
        }
    }
}
