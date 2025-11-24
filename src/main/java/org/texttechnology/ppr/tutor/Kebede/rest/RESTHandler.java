package org.texttechnology.ppr.tutor.Kebede.rest;

import io.javalin.Javalin;
import org.texttechnology.ppr.tutor.Kebede.database.Neo4jConnection;
import org.texttechnology.ppr.tutor.Kebede.factory.StaffFactory;
import org.texttechnology.ppr.tutor.Kebede.implementation.StaffImpl;

import java.util.*;

public class RESTHandler {
    private final StaffFactory factory;

    public RESTHandler(Neo4jConnection connection) {
        this.factory = new StaffFactory(connection);
    }

    public void registerRoutes(Javalin app) {
        // Get all staff
        app.get("/api/staff", ctx -> {
            ctx.json(factory.findAll());
        });

        // Get staff by ID
        app.get("/api/staff/{id}", ctx -> {
            String id = ctx.pathParam("id");
            StaffImpl s = factory.findById(id);
            if (s == null) {
                ctx.status(404).json(Map.of("error", "Staff not found"));
            } else {
                ctx.json(s);
            }
        });
    }
}