package org.texttechnology.ppr.tutor.Kebede.factory;

import org.neo4j.graphdb.*;
import org.texttechnology.ppr.tutor.Kebede.database.Neo4jConnection;
import org.texttechnology.ppr.tutor.Kebede.implementation.StaffImpl;
import org.texttechnology.ppr.tutor.Kebede.neo4j.StaffNeo4jImpl;

import java.util.List;

public class StaffFactory {

    private final Neo4jConnection connection;

    public StaffFactory(Neo4jConnection connection) {
        this.connection = connection;
    }

    // create

    public void create(StaffImpl staff) {
        try (Transaction tx = connection.getDatabase().beginTx()) {
            StaffNeo4jImpl neo = new StaffNeo4jImpl(staff);
            neo.toNode(tx);
            tx.commit();
        }
    }

    // read

    public StaffImpl findById(String id) {
        return connection.findStaffById(id);
    }

    public List<StaffImpl> findAll() {
        return connection.findAllStaff().stream().sorted().toList();
    }

    // update

    public void update(StaffImpl staff) {
        connection.updateStaff(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getNickname()
        );

        if (staff.getSalary() != null) {
            connection.updateStaffSalary(
                    staff.getId(),
                    staff.getSalary().getSalary(),
                    staff.getSalary().getCurrency()
            );
        }
    }

    // delete

    public void delete(String id) {
        connection.deleteStaffById(id);
    }

    public void deleteAll() {
        connection.deleteAllStaff();
    }

    // utility

    public long count() {
        return connection.countStaff();
    }
}