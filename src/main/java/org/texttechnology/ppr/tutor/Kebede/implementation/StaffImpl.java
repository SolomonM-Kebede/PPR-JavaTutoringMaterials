package org.texttechnology.ppr.tutor.Kebede.implementation;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.texttechnology.ppr.tutor.Kebede.interfaces.Salary;
import org.texttechnology.ppr.tutor.Kebede.interfaces.Staff;


/**
 * Staff Object representation
 */
public class StaffImpl implements Staff, Comparable<StaffImpl> {

    private String id;
    private String firstName;
    private String lastName;
    private Salary salary;
    private String staff;
    private String nickname;

    public StaffImpl(String id, String firstName, String lastName, String nickname, Salary salary) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.salary = salary;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return lastName + " " + firstName;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public Salary getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return "StaffImpl [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName +
                ", nickname=" + nickname + ", salary=" + salary + "]";
    }

    @Override
    public String getStaff() {
        return staff;
    }

    @Override
    public Node toNode(Transaction transaction) {
        Node staffNode = transaction.createNode(Label.label("Staff"));
        staffNode.setProperty("id", id);
        staffNode.setProperty("firstName", firstName);
        staffNode.setProperty("lastName", lastName);
        staffNode.setProperty("nickname", nickname);

        // Create salary relationship if salary exists
        if (salary != null) {
            Node salaryNode = salary.toNode(transaction);
            staffNode.createRelationshipTo(salaryNode, () -> "HAS_SALARY");
        }

        return staffNode;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("firstName", firstName);
        obj.put("lastName", lastName);
        obj.put("nickname", nickname);
        if (salary != null) {
            obj.put("salary", salary.toJSON());
        }
        return obj;
    }

    void setStaff(String staff) {
        this.staff = staff;
    }

    void setId(String id) {
        this.id = id;
    }

    void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    void setLastName(String lastName) {
        this.lastName = lastName;
    }

    void setSalary(Salary salary) {
        this.salary = salary;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public int compareTo(@NotNull StaffImpl o) {
        return this.getId().compareTo(o.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof StaffImpl && this.id.equals(((StaffImpl) o).id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}