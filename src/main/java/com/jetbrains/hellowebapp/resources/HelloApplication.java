package com.jetbrains.hellowebapp.resources;

import com.jetbrains.hellowebapp.config.CorsFilter;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class HelloApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(CorsFilter.class);
        classes.add(HelloResource.class);
        // Add your other resource classes here
        return classes;
    }
}