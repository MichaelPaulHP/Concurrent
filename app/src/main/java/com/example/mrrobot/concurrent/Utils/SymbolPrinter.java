package com.example.mrrobot.concurrent.Utils;


import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolDragListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.utils.ColorUtils;

public class SymbolPrinter {

    SymbolManager symbolManager;

    public SymbolPrinter( MapView mapView,MapboxMap mapboxMap){
        configSymbolManager(mapView,mapboxMap,mapboxMap.getStyle());

    }

    private void configSymbolManager(MapView mapView,MapboxMap mapboxMap,Style style) {
        this.symbolManager = new SymbolManager(mapView, mapboxMap, style);
        symbolManager.setIconAllowOverlap(true);
        symbolManager.setTextAllowOverlap(true);
        //symbolManager.addClickListener
        //symbolManager.addLongClickListener
        //symbolManager.addDragListener
        //symbolManager.addDragListener(this);
    }

    public Symbol printSymbol(Symbol symbol, LatLng latLng, int color,String ICON){

        if(symbol==null){
            symbol = createSymbol(latLng, color,ICON);
        }else{
            symbol.setLatLng(latLng);
            symbol.setIconColor(color);
            this.symbolManager.update(symbol);
        }
       return symbol;
    }

    public void deleleSymbol(Symbol symbol){
        this.symbolManager.delete(symbol);
    }

    public void addDragListener(OnSymbolDragListener listener){
        symbolManager.addDragListener(listener);
    }

    private Symbol createSymbol(LatLng latLng, int iconColor,String ICON) {
        Symbol symbol;
        symbol = symbolManager.create(new SymbolOptions()
                .withLatLng(latLng)
                .withIconColor(ColorUtils.colorToRgbaString(iconColor))
                .withIconImage(ICON)
                .withIconSize(2.0f)
                .withDraggable(true));
        return symbol;
    }
}
