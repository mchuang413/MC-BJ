package com.example.bluejextension;

import bluej.extensions2.*;

public class MyBlueJExtension extends Extension {
    @Override
    public void startup(BlueJ bluej) {
        System.out.println("HELLO");
    }

    @Override
    public boolean isCompatible() {
        return true;
    }

    @Override
    public String getName() {
        return "My BlueJ Extension";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
}

