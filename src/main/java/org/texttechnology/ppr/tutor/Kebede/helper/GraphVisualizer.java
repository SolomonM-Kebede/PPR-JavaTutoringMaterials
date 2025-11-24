package org.texttechnology.ppr.tutor.Kebede.helper;

import org.neo4j.graphdb.*;

public class GraphVisualizer {

    public static void printStaffSalaryGraph(GraphDatabaseService db) {

        System.out.println("\nNeo4j Staff → Salary Graph");

        try (Transaction tx = db.beginTx()) {

            ResourceIterator<Node> staffNodes =
                    tx.findNodes(Label.label("Staff"));

            boolean found = false;

            while (staffNodes.hasNext()) {
                found = true;
                Node staff = staffNodes.next();

                String id = (String) staff.getProperty("id", "N/A");
                String first = (String) staff.getProperty("firstName", "");
                String last = (String) staff.getProperty("lastName", "");

                System.out.println("Staff: " + id + " - " + first + " " + last);

                for (Relationship rel : staff.getRelationships(Direction.OUTGOING)) {
                    if (rel.getType().name().equals("HAS_SALARY")) {
                        Node sal = rel.getEndNode();
                        String salary = (String) sal.getProperty("salary", "?");
                        String currency = (String) sal.getProperty("currency", "?");

                        System.out.println("   → Salary: " + salary + " " + currency);
                    }
                }
            }

            if (!found) {
                System.out.println("No Staff nodes found.");
            }

            tx.commit();
        }
    }
}