package com.example.mrrobot.concurrent.Utils;

import com.example.mrrobot.concurrent.models.Destination;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;

public class DestinationSymbol {

    private SymbolPrinter symbolPrinter;
    private Destination destination;

    private Symbol originSymbol;
    private Symbol destinationSymbol;
    private String icon;

    public  DestinationSymbol(SymbolPrinter symbolPrinter, Destination destination,String icon ){
        this.symbolPrinter=symbolPrinter;
        this.destination=destination;
        this.icon=icon;
    }

    public void print(){
        if(destination.getOrigin()!=null)
            symbolPrinter.printSymbol(
                    originSymbol,
                    destination.getOriginLatLng(),
                    destination.getColor(),
                    this.icon);
        if(destination.getDestination()!=null){
            symbolPrinter.printSymbol(
                    destinationSymbol,
                    destination.getDestinationLatLng(),
                    destination.getColor(),
                    this.icon);
        }
    }

    public void hide(){
        this.symbolPrinter.deleleSymbol(destinationSymbol);
        this.symbolPrinter.deleleSymbol(originSymbol);
        this.originSymbol=null;
        this.destinationSymbol=null;
    }
    public void setDestination(Destination destination){
        this.destination=destination;
    }

}
