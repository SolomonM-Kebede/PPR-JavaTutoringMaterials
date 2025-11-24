package org.texttechnology.ppr.tutor.Kebede.interfaces;

import org.json.JSONObject;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public interface Staff {
    String getId();
    String getName();
    String getFirstName();
    String getLastName();
    String getNickname();
    Salary getSalary();

    String getStaff();

    Node toNode(Transaction transaction);

    JSONObject toJSON();

    void setNickname(String nickname);
}
