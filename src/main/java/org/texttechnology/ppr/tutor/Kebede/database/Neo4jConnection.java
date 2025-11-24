package org.texttechnology.ppr.tutor.Kebede.database;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.texttechnology.ppr.tutor.Kebede.implementation.SalaryImpl;
import org.texttechnology.ppr.tutor.Kebede.implementation.StaffImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Neo4jConnection {

    private static final Logger log = LoggerFactory.getLogger(Neo4jConnection.class);

    private final DatabaseManagementService managementService;
    private final GraphDatabaseService db;

    public Neo4jConnection(String path) {
        File databaseDirectory = new File(path);

        this.managementService = new DatabaseManagementServiceBuilder(databaseDirectory.toPath())
                .build();

        this.db = managementService.database("neo4j");

        registerShutdownHook();
        log.info("Neo4j database initialized at: {}", path);
    }


    public GraphDatabaseService getDatabase() {
        return db;
    }

    // CREATE operations

    public void createStaffNode(StaffImpl staff) {
        try (Transaction tx = db.beginTx()) {
            Node staffNode = staff.toNode(tx);
            tx.commit();
            log.info("Created staff node: {}", staff.getId());
        } catch (Exception e) {
            log.error("Failed to create staff node", e);
            throw new RuntimeException("Failed to create staff node", e);
        }
    }

    public Node createSalaryNode(Transaction tx, SalaryImpl salary) {
        return salary.toNode(tx);
    }

    // READ operations

    public StaffImpl findStaffById(String id) {
        try (Transaction tx = db.beginTx()) {
            Result result = tx.execute(
                    "MATCH (s:Staff {id: $id}) " +
                            "OPTIONAL MATCH (s)-[:HAS_SALARY]->(sal:Salary) " +
                            "RETURN s, sal",
                    Map.of("id", id)
            );

            if (result.hasNext()) {
                Map<String, Object> row = result.next();
                Node staffNode = (Node) row.get("s");
                Node salaryNode = (Node) row.get("sal");

                SalaryImpl salary = null;
                if (salaryNode != null) {
                    String salaryValue = (String) salaryNode.getProperty("salary", null);
                    String currency = (String) salaryNode.getProperty("currency", null);
                    salary = new SalaryImpl(salaryValue, currency);
                }

                StaffImpl staff = new StaffImpl(
                        (String) staffNode.getProperty("id"),
                        (String) staffNode.getProperty("firstName"),
                        (String) staffNode.getProperty("lastName"),
                        (String) staffNode.getProperty("nickname"),
                        salary
                );

                tx.commit();
                return staff;
            }

            tx.commit();
            return null;
        } catch (Exception e) {
            log.error("Failed to find staff by id: {}", id, e);
            throw new RuntimeException("Failed to find staff", e);
        }
    }

    public List<StaffImpl> findAllStaff() {
        List<StaffImpl> staffList = new ArrayList<>();

        try (Transaction tx = db.beginTx()) {
            Result result = tx.execute(
                    "MATCH (s:Staff) " +
                            "OPTIONAL MATCH (s)-[:HAS_SALARY]->(sal:Salary) " +
                            "RETURN s, sal"
            );

            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                Node staffNode = (Node) row.get("s");
                Node salaryNode = (Node) row.get("sal");

                SalaryImpl salary = null;
                if (salaryNode != null) {
                    String salaryValue = (String) salaryNode.getProperty("salary", null);
                    String currency = (String) salaryNode.getProperty("currency", null);
                    salary = new SalaryImpl(salaryValue, currency);
                }

                StaffImpl staff = new StaffImpl(
                        (String) staffNode.getProperty("id"),
                        (String) staffNode.getProperty("firstName"),
                        (String) staffNode.getProperty("lastName"),
                        (String) staffNode.getProperty("nickname"),
                        salary
                );

                staffList.add(staff);
            }

            tx.commit();
            log.info("Found {} staff members", staffList.size());
            return staffList;
        } catch (Exception e) {
            log.error("Failed to find all staff", e);
            throw new RuntimeException("Failed to find all staff", e);
        }
    }

    // UPDATE operations

    public void updateStaff(String id, String firstName, String lastName, String nickname) {
        try (Transaction tx = db.beginTx()) {
            tx.execute(
                    "MATCH (s:Staff {id: $id}) " +
                            "SET s.firstName = $firstName, s.lastName = $lastName, s.nickname = $nickname",
                    Map.of(
                            "id", id,
                            "firstName", firstName,
                            "lastName", lastName,
                            "nickname", nickname
                    )
            );
            tx.commit();
            log.info("Updated staff: {}", id);
        } catch (Exception e) {
            log.error("Failed to update staff: {}", id, e);
            throw new RuntimeException("Failed to update staff", e);
        }
    }

    public void updateStaffSalary(String staffId, String salaryValue, String currency) {
        try (Transaction tx = db.beginTx()) {
            tx.execute(
                    "MATCH (s:Staff {id: $id})-[r:HAS_SALARY]->(sal:Salary) " +
                            "SET sal.salary = $salary, sal.currency = $currency",
                    Map.of(
                            "id", staffId,
                            "salary", salaryValue,
                            "currency", currency
                    )
            );
            tx.commit();
            log.info("Updated salary for staff: {}", staffId);
        } catch (Exception e) {
            log.error("Failed to update salary for staff: {}", staffId, e);
            throw new RuntimeException("Failed to update salary", e);
        }
    }

    // DELETE operations

    public void deleteStaffById(String id) {
        try (Transaction tx = db.beginTx()) {
            tx.execute(
                    "MATCH (s:Staff {id: $id}) " +
                            "OPTIONAL MATCH (s)-[r:HAS_SALARY]->(sal:Salary) " +
                            "DELETE r, sal, s",
                    Map.of("id", id)
            );
            tx.commit();
            log.info("Deleted staff: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete staff: {}", id, e);
            throw new RuntimeException("Failed to delete staff", e);
        }
    }

    public void deleteAllStaff() {
        try (Transaction tx = db.beginTx()) {
            tx.execute("MATCH (s:Staff) DETACH DELETE s");
            tx.commit();
            log.info("Deleted all staff");
        } catch (Exception e) {
            log.error("Failed to delete all staff", e);
            throw new RuntimeException("Failed to delete all staff", e);
        }
    }

    // Utility methods

    public long countStaff() {
        try (Transaction tx = db.beginTx()) {
            Result result = tx.execute("MATCH (s:Staff) RETURN count(s) as count");
            long count = (long) result.next().get("count");
            tx.commit();
            return count;
        } catch (Exception e) {
            log.error("Failed to count staff", e);
            throw new RuntimeException("Failed to count staff", e);
        }
    }

    public void shutdown() {
        managementService.shutdown();
        log.info("Neo4j connection closed");
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            managementService.shutdown();
            System.out.println("Neo4j stopped.");
        }));
    }
}