package org.texttechnology.ppr.tutor.Kebede.interfaces;

import org.json.JSONObject;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public interface Salary {
    String getSalary();
    String getCurrency();
    void setCurrency(String currency);
    void setSalary(String salary);

    Node toNode(Transaction transaction);

    JSONObject toJSON();
}
