package com.example.mrrobot.concurrent.Utils;

import com.example.mrrobot.concurrent.models.Destination;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;

public class DestinationSymbol {

    private static final String ICON_PLACE = "ic-place";

    private SymbolPrinter symbolPrinter;
    private Destination destination;

    private Symbol originSymbol;
    private Symbol destinationSymbol;


    public  DestinationSymbol(SymbolPrinter symbolPrinter, Destination destination){
        this.symbolPrinter=symbolPrinter;
        this.destination=destination;

    }

    public void print(){
        if(destination.getOrigin()!=null)
            this.originSymbol= symbolPrinter.printSymbol(
                    originSymbol,
                    destination.getOriginLatLng(),
                    destination.getColor(),
                    ICON_PLACE);
        if(destination.getDestination()!=null){
            this.destinationSymbol= symbolPrinter.printSymbol(
                    destinationSymbol,
                    destination.getDestinationLatLng(),
                    destination.getColor(),
                    ICON_PLACE);
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

    public long getDestinationId(){
        return this.destinationSymbol.getId();
    }
    public long getOriginId(){
        return this.originSymbol.getId();
    }
}
