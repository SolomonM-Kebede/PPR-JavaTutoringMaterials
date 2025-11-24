package org.texttechnology.ppr.tutor.Kebede.neo4j;

import org.json.JSONObject;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.texttechnology.ppr.tutor.Kebede.implementation.SalaryImpl;
import org.texttechnology.ppr.tutor.Kebede.interfaces.Salary;

import java.util.HashMap;
import java.util.Map;

public class SalaryNeo4jImpl implements Salary {

    private final SalaryImpl salary;

    public SalaryNeo4jImpl(SalaryImpl salary) {
        this.salary = salary;
    }

    @Override
    public String getSalary() {
        return "";
    }

    @Override
    public String getCurrency() {
        return "";
    }

    @Override
    public void setCurrency(String currency) {

    }

    @Override
    public void setSalary(String salary) {

    }

    /** Java Embedded API version */

    public Node toNode(Transaction transaction){
        Node salaryNode = transaction.createNode(Label.label("Salary"));
        salaryNode.setProperty("salary", salary.getSalary()); // assume salary is never null

        if(getCurrency() != null && !getCurrency().isEmpty()) {
            salaryNode.setProperty("currency", getCurrency());
        }

        return salaryNode;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    /** Cypher version */
    public Map<String, Object> toCypherProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("salary", salary.getSalary());
        props.put("currency", salary.getCurrency());
        return props;
    }
}