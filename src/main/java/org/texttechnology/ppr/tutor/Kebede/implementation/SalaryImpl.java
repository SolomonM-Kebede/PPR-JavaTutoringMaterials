package org.texttechnology.ppr.tutor.Kebede.implementation;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.texttechnology.ppr.tutor.Kebede.interfaces.Salary;

/**
 * Salary Object representation with all its attributes
 */
public class SalaryImpl implements Salary, Comparable<SalaryImpl> {
    private String salary;
    private String currency;

    /**
     * Constuctor
     * @param salary
     * @param currency
     */
    public SalaryImpl(String salary, String currency) {
        this.salary = salary;
        this.currency = currency;
    }

    @Override
    public String getSalary() {
        return salary;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public void setSalary(String salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "SalaryImpl [salary=" + salary + ", currency=" + currency + "]";
    }

    @Override
    public Node toNode(Transaction transaction) {
        Node salaryNode = transaction.createNode(Label.label("Salary"));
        if (salary != null) {
            salaryNode.setProperty("salary", salary);
        }
        if (currency != null) {
            salaryNode.setProperty("currency", currency);
        }
        return salaryNode;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject salaryJson = new JSONObject();
        salaryJson.put("salary", salary);
        salaryJson.put("currency", currency);
        return salaryJson;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((salary == null) ? 0 : salary.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        return result;
    }

    @Override
    public int compareTo(@NotNull SalaryImpl o) {
        if (salary == null) return o.getSalary() == null ? 0 : -1;
        if (o.getSalary() == null) return 1;
        return salary.compareTo(o.getSalary());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SalaryImpl other = (SalaryImpl) obj;

        if (salary == null) {
            if (other.salary != null) return false;
        } else if (!salary.equals(other.salary)) {
            return false;
        }

        if (currency == null) {
            return other.currency == null;
        } else {
            return currency.equals(other.currency);
        }
    }
}