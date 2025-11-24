package org.texttechnology.ppr.tutor.Kebede.rest;

import io.javalin.Javalin;
import org.texttechnology.ppr.tutor.Kebede.database.Neo4jConnection;
import org.texttechnology.ppr.tutor.Kebede.factory.StaffFactory;
import org.texttechnology.ppr.tutor.Kebede.implementation.StaffImpl;

import java.util.*;

public class TemplateHandler {
    private final StaffFactory factory;

    public TemplateHandler(Neo4jConnection connection) {
        this.factory = new StaffFactory(connection);
    }

    public void registerRoutes(Javalin app) {
        // Redirect "/" to staff page
        app.get("/", ctx -> ctx.redirect("/staff"));

        // Show all staff (template-rendered page)
        app.get("/staff", ctx -> {
            List<StaffImpl> staffList = factory.findAll();
            ctx.render("templates/staff.ftl", Map.of("staff", staffList));
        });

        // Search functionality for template (optional if you want server-side search)
        app.get("/staff/search", ctx -> {
            String id = ctx.queryParam("id");
            List<StaffImpl> staffList;

            if (id != null && !id.isBlank()) {
                StaffImpl staff = factory.findById(id);
                staffList = staff != null ? List.of(staff) : List.of();
            } else {
                staffList = factory.findAll();
            }

            ctx.render("templates/staff.ftl", Map.of("staff", staffList));
        });
    }
}