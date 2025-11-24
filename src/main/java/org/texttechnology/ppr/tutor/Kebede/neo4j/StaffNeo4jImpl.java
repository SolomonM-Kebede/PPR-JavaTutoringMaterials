package org.texttechnology.ppr.tutor.Kebede.neo4j;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.texttechnology.ppr.tutor.Kebede.implementation.SalaryImpl;
import org.texttechnology.ppr.tutor.Kebede.implementation.StaffImpl;

import java.util.HashMap;
import java.util.Map;

public class StaffNeo4jImpl {

    private final StaffImpl staff;

    public StaffNeo4jImpl(StaffImpl staff) {
        this.staff = staff;
    }

    /**
     * Java Embedded API
     */
    public void toNode(Transaction tx) {

        Node staffNode = tx.createNode(Label.label("Staff"));
        staffNode.setProperty("id", staff.getId());
        staffNode.setProperty("firstname", staff.getFirstName());
        staffNode.setProperty("lastname", staff.getLastName());
        staffNode.setProperty("nickname", staff.getNickname());

        // Create salary node if present
        if (staff.getSalary() != null) {
            SalaryNeo4jImpl sn = new SalaryNeo4jImpl((SalaryImpl) staff.getSalary());
            Node salaryNode = sn.toNode(tx);
            staffNode.createRelationshipTo(salaryNode,
                    () -> "HAS_SALARY");
        }

    }

    /** Cypher Properties */
    public Map<String, Object> toCypherProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("id", staff.getId());
        props.put("firstname", staff.getFirstName());
        props.put("lastname", staff.getLastName());
        props.put("nickname", staff.getNickname());
        return props;
    }
}