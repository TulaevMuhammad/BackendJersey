package com.jetbrains.hellowebapp;

import org.glassfish.jersey.server.ResourceConfig;

public class MyApplication extends ResourceConfig {

    public MyApplication() {
        packages("com.jetbrains.hellowebapp.resources");
        property("jersey.config.server.wadl.disableWadl", true);
    }
}
