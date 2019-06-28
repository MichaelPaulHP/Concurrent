package com.example.mrrobot.concurrent.models;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.widget.ImageView;

import com.example.mrrobot.concurrent.Utils.RandomColors;

public class Destination {

    private String id;
    private int numUsers;
    private Localization localization;
    private int color;
    private String name;
    private IListenerDestination listenerDestination;
    public Destination() {
        this.color= new RandomColors().getColor();
    }


    /////////GETTERS AND SETTER


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IListenerDestination getListenerDestination() {
        return listenerDestination;
    }

    public void setListenerDestination(IListenerDestination iListenerDestination) {
        this.listenerDestination = iListenerDestination;
    }

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public Localization getLocalization() {
        return localization;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public interface IListenerDestination{
        /**
         * on Click in Destination
         */
        void onClick(Destination destination);
    }

    @BindingAdapter("android:tint")
    public static void setColorFilter(ImageView imageView, int color){
        imageView.setColorFilter(color);
    }
}
